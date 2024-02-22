package com.bankmas.report.webapi.service.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bankmas.report.webapi.config.StorageProperties;
import com.bankmas.report.webapi.dto.DataResponse;
import com.bankmas.report.webapi.dto.IdOnlyResponse;
import com.bankmas.report.webapi.dto.PaginationResponse;
import com.bankmas.report.webapi.dto.file.DetailReportTypeResponse;
import com.bankmas.report.webapi.dto.file.DetailUploadFileResponse;
import com.bankmas.report.webapi.dto.file.ListFileResponse;
import com.bankmas.report.webapi.dto.file.SaveFileRequest;
import com.bankmas.report.webapi.dto.file.SaveFileResponse;
import com.bankmas.report.webapi.dto.kafka.MessageKafkaUpdateStatusFile;
import com.bankmas.report.webapi.exception.ValidationException;
import com.bankmas.report.webapi.model.EnumDocumentFileType;
import com.bankmas.report.webapi.model.EnumUploadFileStatus;
import com.bankmas.report.webapi.model.ReportType;
import com.bankmas.report.webapi.model.UploadFile;
import com.bankmas.report.webapi.repository.ReportTypeRepository;
import com.bankmas.report.webapi.repository.UploadFileRepository;
import com.bankmas.report.webapi.service.kafka.KafkaProducer;
import com.bankmas.report.webapi.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    UploadFileRepository fileRepository;

    @Autowired 
    ReportTypeRepository reportTypeRepository;

    @Autowired
    KafkaProducer kafkaProducer;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(transactionManager = "transactionManager")
    public DataResponse<List<SaveFileResponse>> saveFile(SaveFileRequest request) throws IOException, NoSuchAlgorithmException,ValidationException {
        List<SaveFileResponse> result = new ArrayList<>();
        for(MultipartFile multipart : request.getFile()){
            try{
                StringBuilder stringBuilder = getLineFromMultipart(multipart);

                //validate file format
                List<Map<String, Object>> listMap = new ArrayList<>();
                try{
                    listMap = objectMapper.readValue(stringBuilder.toString(), List.class);
                } catch(IOException e){
                    throw new ValidationException("INVALID_FILE_FORMAT");
                }

                if(listMap.size() == 0)
                    throw new ValidationException("FILE_EMPTY");

                String checksum = StringUtil.generateChecksum(stringBuilder.toString());

                Optional<UploadFile> fileOnProcess = fileRepository.findFirstByDocumentFileTypeAndChecksumOrderByProcessDatetimeDesc(request.getDocumentFileType(), checksum);
                if(fileOnProcess.isPresent() && !fileOnProcess.get().getStatus().equals(EnumUploadFileStatus.ERROR))
                    throw new ValidationException("FILE_ON_PROCESS");

                String fileName = StringUtil.generateFilename();

                storeFileToStorage(stringBuilder.toString(), fileName);

                UploadFile uploadFile = storeFileToDatabase(request.getDocumentFileType(), request.getReportType(), checksum, fileName, multipart.getOriginalFilename());

                kafkaProducer.sendUploadFile(request.getDocumentFileType().name(), uploadFile.getId(), fileName, uploadFile.getReportType().getId());

                result.add(SaveFileResponse.builder().fileName(uploadFile.getOriginalFileName()).status("SUCCESS").reason(null).build());
            } catch(Exception e){
                e.printStackTrace();
                result.add(SaveFileResponse.builder().fileName(multipart.getOriginalFilename()).status("FAILED").reason(e.getMessage()).build());
            }
            
        }

        return new DataResponse<List<SaveFileResponse>>("SUCCESS", result);
    }

    private StringBuilder getLineFromMultipart(MultipartFile multipartFile) throws IOException {
        InputStream inputStream = multipartFile.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while((line=reader.readLine()) != null)
            stringBuilder.append(line);

        inputStream.close();
        reader.close();
        return stringBuilder;
    }

    @Transactional(transactionManager = "transactionManager")
    private UploadFile storeFileToDatabase(
            EnumDocumentFileType enumDocumentFileType, String reportTypeName, 
            String checksum, String fileName, String originalFilename) {
        
        ReportType reportType = reportTypeRepository.findFirstByName(reportTypeName.toUpperCase(Locale.ROOT))
            .orElseThrow(() -> new ValidationException("INVALID_REPORT_TYPE"));

        UploadFile uploadFile = UploadFile
            .builder()
            .name(fileName)
            .documentFileType(enumDocumentFileType)
            .reportType(reportType)
            .checksum(checksum)
            .originalFileName(originalFilename)
            .status(EnumUploadFileStatus.UNPROCESSED)
            .build();

        fileRepository.save(uploadFile);
        return uploadFile;
    }

    private void storeFileToStorage(String lines, String fileName) throws IOException {
        String path = StorageProperties.getUploadLocation() + fileName + ".json";
        File file = new File(path);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(lines);
        fileWriter.close();
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public void updateStatusFile(MessageKafkaUpdateStatusFile message) {
        Optional<UploadFile> optional = fileRepository.findById(message.getId());
        if(optional.isPresent()) {
            UploadFile file = optional.get();
            if(file.getStatus().equals(EnumUploadFileStatus.SUCCESS)|| file.getStatus().equals(EnumUploadFileStatus.ERROR))
                return;
            file.setStatus(message.getStatus());
            if(file.getStatus().equals(EnumUploadFileStatus.SUCCESS)) {
                file.setFinishDatetime(
                    LocalDateTime.parse(
                        message.getFinishDatetime(), 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    )
                );
                file.setFormatedFileName(message.getFormatedFileName());
            }
            fileRepository.save(file);
        }
    }

    @Override
    public PaginationResponse<List<ListFileResponse>> listFile(Integer page, Integer size, EnumUploadFileStatus status) {
        Page<UploadFile> files = Page.empty();
        if(status != null){
            try{
                files = fileRepository.findAllByStatus(status, PageRequest.of(page, size).withSort(Direction.DESC, "processDatetime"));
            } catch (IllegalArgumentException e) {
                //do nothing
            }
        }
        else{
            files = fileRepository.findAll(PageRequest.of(page, size).withSort(Direction.DESC, "processDatetime"));
        }
        return new PaginationResponse<List<ListFileResponse>>(
            "SUCCESS",
            files.getContent().stream().map(s->new ListFileResponse(s)).collect(Collectors.toList()),
            page,
            files.getTotalPages(),
            files.getTotalElements()
        );
    }

    @Override
    public DataResponse<DetailUploadFileResponse> getFile(String id){
        if(StringUtil.isNull(id))
            throw new ValidationException("BAD_REQUEST");
        

        Optional<UploadFile> optional = fileRepository.findById(id);
        if(optional.isPresent()) {
            UploadFile file = optional.get();
            
            return new DataResponse<DetailUploadFileResponse>("SUCCESS",new DetailUploadFileResponse(file));
            
        }
        throw new ValidationException("FILE_NOT_FOUND");
    }

    @Transactional(transactionManager = "transactionManager")
    @Override
    public DataResponse<IdOnlyResponse> deleteFile(String id) throws IOException {
        if(StringUtil.isNull(id))
            throw new ValidationException("BAD_REQUEST");

        Optional<UploadFile> optional = fileRepository.findById(id);
        if(optional.isPresent()) {
            UploadFile file = optional.get();
            if(!file.getStatus().equals(EnumUploadFileStatus.UNPROCESSED) && !file.getStatus().equals(EnumUploadFileStatus.IN_PROGRESS)) {
                try{
                    Files.delete(Paths.get(StorageProperties.getUploadLocation() + file.getName() + ".json"));
                } catch (IOException e) {}
                try{
                    Files.delete(Paths.get(getFilePath(file)));
                } catch (IOException e) {}
                

                fileRepository.deleteById(id);
                return new DataResponse<IdOnlyResponse>("SUCCESS", new IdOnlyResponse(id));
            }
            else{
                throw new ValidationException("FILE_STILL_PROCESSING");
            }
        }
        throw new ValidationException("FILE_NOT_FOUND");
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public ResponseEntity<byte[]> downloadFile(String id) {
        if(StringUtil.isNull(id))
            throw new ValidationException("BAD_REQUEST");
            
        Optional<UploadFile> optional = fileRepository.findById(id);
        if(optional.isPresent()) {
            UploadFile file = optional.get();
            if(file.getStatus().equals(EnumUploadFileStatus.SUCCESS)) {

                byte[] fileBytes = null;
                
                try {
                    Path path = Paths.get(getFilePath(file));
                    fileBytes = Files.readAllBytes(path);
                    return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=" + file.getFormatedFileName())
                        .header("Content-Type", getContentType(file))
                        .body(fileBytes);
                } catch (IOException e) {
                    if(e instanceof NoSuchFileException) {
                        throw new ValidationException("NOT_FOUND");
                    } else {
                        throw new ValidationException("INTERNAL_SERVER_ERROR");
                    }
                }
                
            } else {
                throw new ValidationException("NOT_FOUND");
            }
        } else {
            throw new ValidationException("NOT_FOUND");
        }
    }

    private String getFilePath(UploadFile file) {
        String filePath = null;
        switch (file.getDocumentFileType()) {
            case CSV:
                filePath=StorageProperties.getCsvLocation()+file.getFormatedFileName();
                break;
            case EXCEL:
                filePath=StorageProperties.getExcelLocation()+file.getFormatedFileName();
                break;
            case PDF:
                filePath=StorageProperties.getPdfLocation()+file.getFormatedFileName();
                break;
        }
        return filePath;
    }

    private String getContentType(UploadFile file) {
        String contentType = null;
        switch (file.getDocumentFileType()) {
            case CSV:
                contentType="text/csv";
                break;
            case EXCEL:
                contentType="application/vnd.ms-excel";
                break;
            case PDF:
                contentType="application/pdf";
                break;
        }
        return contentType;
    }
}
