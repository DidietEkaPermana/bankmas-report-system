package com.bankmas.report.webapi.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bankmas.report.webapi.common.DateUtil;
import com.bankmas.report.webapi.common.FileUtil;
import com.bankmas.report.webapi.common.MapUtil;
import com.bankmas.report.webapi.common.StringUtil;
import com.bankmas.report.webapi.dto.FileCreateUpdateResponse;
import com.bankmas.report.webapi.dto.FileDescription;
import com.bankmas.report.webapi.dto.FileListResponse;
import com.bankmas.report.webapi.dto.FileResponse;
import com.bankmas.report.webapi.dto.PagingRequest;
import com.bankmas.report.webapi.dto.UploadRequest;
import com.bankmas.report.webapi.model.MFile;
import com.bankmas.report.webapi.model.MFileCategory;
import com.bankmas.report.webapi.repository.FileRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
@AllArgsConstructor
public class FileServiceImpl implements FileService {
	
	private static final String TEMP_DIRECTORY = "export.directory";
	private static final String TMP_REPORT_FILE = "tmpReportFile";
	
	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	private FileCategoryService fileCategoryService;
	
	@Autowired
	private KafkaProducerService kafkaProducerService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Resource
	private Environment env;
	
	@Transactional(transactionManager = "transactionManager")
	@Override
	public FileCreateUpdateResponse upload(MultipartFile[] files) throws IOException, NoSuchAlgorithmException {
		List<FileResponse> fileList = new ArrayList<>();
		
		for (MultipartFile file : files) {
			String name = file.getOriginalFilename();
			String type = name.substring(name.lastIndexOf(".") + 1);
			byte[] data = file.getBytes();
			
			String checksum = FileUtil.calculateChecksum(data);
			String status = "Completed";
			Timestamp now = DateUtil.getTodayDate();
			
			MFile fileDto = MFile.builder()
					.fileName(name)
					.type(type)
					.category(null)
					.data(data)
					.status(status)
					.checksumFile(checksum)
					.createdAt(now)
					.updatedAt(null)
					.build();
			
			MFile saveFile = fileRepository.save(fileDto);
			
			FileResponse dto = FileResponse.builder()
					.fileName(saveFile.getFileName())
					.status(saveFile.getStatus())
					.build();

			fileList.add(dto);
        }
		
		return FileCreateUpdateResponse.builder()
                .message("success insert")
                .files(fileList)
                .build();
	}
	
	@Transactional(transactionManager = "transactionManager")
    @SneakyThrows
    @Override
    public FileResponse findById(String id) {
        Optional<MFile> detail = fileRepository.findById(id);

        return FileResponse.builder()
				.fileName(detail.get().getFileName())
				.status(detail.get().getStatus())
				.build();
    }

	@Transactional(transactionManager = "transactionManager")
    @SneakyThrows
	@Override
	public MFile findFileById(String fileId) {
		List<MFile> detail = fileRepository.findByIdAndStatus("Completed", fileId);
		
		if (detail.isEmpty()) {
			throw new IllegalStateException(
		              "file with id" + fileId + " not exist");
		}
		
		return detail.get(0);
	}

	@Transactional(transactionManager = "transactionManager")
	@SneakyThrows
	@Override
	public void deleteFile(String id) {
		if (id == null) {
			throw new IllegalStateException(id + " is null");
		}
		else {
			Optional<MFile> obj = fileRepository.findById(id);	
			
			if (obj.isPresent()) {
				fileRepository.deleteById(id);
				
				if (obj.get().getPath() != null) {
					Files.deleteIfExists(Paths.get(obj.get().getPath()));
				}
			}
			else {
				throw new IllegalStateException(
			              "file with id" + id + " not exist");
			}
		}
	}

	@Override
	public FileListResponse findByCriteria(PagingRequest pagingRequest) throws Exception {
		List<FileResponse> files = new ArrayList<>();
		
		Direction direction = pagingRequest.isDirection() ? Direction.ASC : Direction.DESC;
		String property = StringUtil.isNullOrEmpty(pagingRequest.getOrderBy()) ? "createdAt" : pagingRequest.getOrderBy();
		
		String status = MapUtil.getStringObject(pagingRequest.getFilters(), "status", true);
		Sort sort = Sort.by(direction, property);
		
		Pageable pageRequest = PageRequest.of(pagingRequest.getCurrentPage(), pagingRequest.getPageItems(), sort);
		
		files = fileRepository.findByCriteria2(status, pageRequest);
		
		return FileListResponse.builder()
				.message("success")
				.data(files)
				.build();
	}
	
	@Transactional(transactionManager = "transactionManager")
    @Override
    public void updateFile(String id, String status) {
        Optional<MFile> findIdExistingFile = fileRepository.findById(id);
        Timestamp now = DateUtil.getTodayDate();
        
        findIdExistingFile.get().setStatus(status);
    	findIdExistingFile.get().setChecksumFile2(null);
    	findIdExistingFile.get().setUpdatedAt(now);

    	fileRepository.save(findIdExistingFile.get());
    }

	@Override
	public FileCreateUpdateResponse uploadAndSend(MultipartFile[] files) throws IOException {
		List<FileResponse> fileList = new ArrayList<>();
		
		for (MultipartFile file : files) {
			byte[] data = file.getBytes();
			String status = "Not Yet Processed";
			Timestamp now = DateUtil.getTodayDate();
			
			//csv file
			MFile fileDtoCsv = MFile.builder()
					.fileName(null)
					.type("csv")
					.category(null)
					.data(null)
					.data2(data)
					.status(status)
					.checksumFile(null)
					.createdAt(now)
					.updatedAt(null)
					.build();
			
			MFile saveFileCsv = fileRepository.save(fileDtoCsv);
			
			FileResponse dtoCsv = FileResponse.builder()
					.id(saveFileCsv.getId())
					.fileName(saveFileCsv.getFileName())
					.status(saveFileCsv.getStatus())
					.createdAt(saveFileCsv.getCreatedAt())
					.build();

			fileList.add(dtoCsv);
			
			kafkaProducerService.sendMessage("file-topic-csv", saveFileCsv.getId());
			
			//pdf file
			Timestamp now2 = DateUtil.getTodayDate();
			
			MFile fileDtoPdf = MFile.builder()
					.fileName(null)
					.type("pdf")
					.category(null)
					.data(null)
					.data2(data)
					.status(status)
					.checksumFile(null)
					.createdAt(now2)
					.updatedAt(null)
					.build();
			
			MFile saveFilePdf = fileRepository.save(fileDtoPdf);
			
			FileResponse dtoPdf = FileResponse.builder()
					.id(saveFilePdf.getId())
					.fileName(saveFilePdf.getFileName())
					.status(saveFilePdf.getStatus())
					.createdAt(saveFilePdf.getCreatedAt())
					.build();

			fileList.add(dtoPdf);
			
			kafkaProducerService.sendMessage("file-topic-pdf", saveFilePdf.getId());
			
			//xlsx file
			Timestamp now3 = DateUtil.getTodayDate();
			
			MFile fileDtoXlsx = MFile.builder()
					.fileName(null)
					.type("xlsx")
					.category(null)
					.data(null)
					.data2(data)
					.status(status)
					.checksumFile(null)
					.createdAt(now3)
					.updatedAt(null)
					.build();
			
			MFile saveFileXlsx = fileRepository.save(fileDtoXlsx);
			
			FileResponse dtoXlsx = FileResponse.builder()
					.id(saveFileXlsx.getId())
					.fileName(saveFileXlsx.getFileName())
					.status(saveFileXlsx.getStatus())
					.createdAt(saveFileXlsx.getCreatedAt())
					.build();

			fileList.add(dtoXlsx);
			
			kafkaProducerService.sendMessage("file-topic-excel", saveFileXlsx.getId());
        }
		
		return FileCreateUpdateResponse.builder()
                .message("success insert")
                .files(fileList)
                .build();
	}

	@Override
	@SneakyThrows
	public void readJsonFile(String id) throws IOException {
		try {			
			MFile detail = fileRepository.findById(id).get();
			
			if (detail.getData2() == null) {
				throw new IllegalStateException("data in json is null");
			}
			
			String tempPath = env.getRequiredProperty(TEMP_DIRECTORY);
			File file = File.createTempFile(TMP_REPORT_FILE, ".json", new File(tempPath));
			
			String filePath = file.getAbsolutePath();
			Path path = Paths.get(filePath);
            Files.write(path, detail.getData2());
            
			List<UploadRequest> objects = objectMapper.readValue(file, new TypeReference<List<UploadRequest>>() {});
			
			for (UploadRequest obj : objects) {
				System.out.println(obj.getWilayah());
				System.out.println(obj.getTanggal());
                System.out.println(obj.getGambar());
            }
		}
		catch (IOException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public FileCreateUpdateResponse uploadByType(MultipartFile[] files, String fileDescription)
			throws Exception {
		List<FileResponse> fileList = new ArrayList<>();
		
		for (MultipartFile file : files) {
			byte[] data = file.getBytes();
			String status = "Not Yet Processed";
			Timestamp now = DateUtil.getTodayDate();
			
			FileDescription fd = objectMapper.readValue(fileDescription, FileDescription.class);
			
			//pdf, csv, xlsx
			String format = fd.getFormat();
			//monthly, weekly, biweekly
			String type = fd.getType();
			
			List<MFileCategory> mfc = fileCategoryService.getByCategory(type.toUpperCase());
			
			if (mfc.isEmpty()) {
				throw new Exception("Category : " + type + " not found");
			}
			
			String checksumFile2 = FileUtil.calculateChecksum(data);
			
			List<MFile> tmp = fileRepository.findByChecksumFile2(checksumFile2);
			
			if (!tmp.isEmpty()) {
				throw new Exception("There in progress file");
			}
			
			String topic = "";
			switch (format) {
			case "pdf" :
				topic = "file-topic-pdf";
				break;
			case "xlsx" : 
				topic = "file-topic-excel";
				break;
			case "csv" :
				topic = "file-topic-csv";
				break;
			}

			MFile fileDto = MFile.builder()
					.fileName(null)
					.type(format)
					.category(null)
					.data(null)
					.data2(data)
					.status(status)
					.checksumFile(null)
					.checksumFile2(checksumFile2)
					.createdAt(now)
					.updatedAt(null)
					.mFileCategory(mfc.get(0))
					.build();
			
			MFile saveFile = fileRepository.save(fileDto);
			
			FileResponse dtoCsv = FileResponse.builder()
					.id(saveFile.getId())
					.fileName(saveFile.getFileName())
					.status(saveFile.getStatus())
					.createdAt(saveFile.getCreatedAt())
					.build();

			fileList.add(dtoCsv);
			
			kafkaProducerService.sendMessage(topic, saveFile.getId());
			//kafkaProducerService.sendMessage(topic, "not-found");
        }
		
		return FileCreateUpdateResponse.builder()
                .message("success insert")
                .files(fileList)
                .build();
	}
}
