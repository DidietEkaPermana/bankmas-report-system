package com.bankmas.report.webapi.services;

import com.bankmas.report.webapi.dto.FileUploadCreateUpdateResponse;
import com.bankmas.report.webapi.dto.FileUploadRequest;
import com.bankmas.report.webapi.dto.FileUploadResponse;
import com.bankmas.report.webapi.model.MFileUpload;
import com.bankmas.report.webapi.model.TopicEnum;
import com.bankmas.report.webapi.repository.FileUploadRepository;
import com.bankmas.report.webapi.storage.StorageService;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

@Service
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "fileUploadCache")
public class FileUploadServiceImpl {

	private final StorageService storageService;
    private final boolean filterChecksum = true; 
    
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    DataSource dataSource;

    private FileUploadRepository fileUploadRepository;

    public FileUploadCreateUpdateResponse createFileUpload(FileUploadRequest request) {

            MFileUpload requestDataFromTableToRequest = MFileUpload.builder()
                .fileName(request.getFileName())
                .jenisReport(request.getJenisReport())
                .statusProses("waiting")
                .tipeReport(request.getTipeReport())
                .checksumFile("checksum")
                .build();

			MFileUpload savedToDatabase = fileUploadRepository.save(requestDataFromTableToRequest);

			return FileUploadCreateUpdateResponse.builder()
					.message("success insert")
					.data(savedToDatabase)
					.status(true)
					.build();
    }

    public String uploadFile(TopicEnum topicEnum, MultipartFile file) {
        try {
            String extension = ".csv";
            if(topicEnum.equals(TopicEnum.EXCEL_FILE)){
                extension = ".xlsx";
            } else if(topicEnum.equals(TopicEnum.PDF_FILE)){
                extension = ".pdf";
            }
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + ".json";
            storageService.store(fileName, file);
            Path pathFile = storageService.load(fileName);

            byte[] data = Files.readAllBytes(pathFile);
            byte[] hash = MessageDigest.getInstance("MD5").digest(data);
            String checksum = new BigInteger(1, hash).toString(16);

            if(fileUploadRepository.existsByChecksumAndStatus(checksum) && filterChecksum){
                storageService.delete(fileName);
                return "File ("+checksum+") already processed";
            }

            MFileUpload requestDataFromTableToRequest = MFileUpload.builder()
                .id(uuid)
                .fileName(fileName)
                .jenisReport(extension)
                .statusProses("waiting")
                .tipeReport(extension)
                .checksumFile(checksum)
                .build();

            MFileUpload savedToDatabase = fileUploadRepository.save(requestDataFromTableToRequest);

            FileUploadResponse fileUploadResponse = FileUploadResponse.builder()
                .id(savedToDatabase.getId())
                .fileName(savedToDatabase.getFileName())
                .jenisReport(savedToDatabase.getJenisReport())
                .statusProses(savedToDatabase.getStatusProses())
                .tipeReport(savedToDatabase.getTipeReport())
                .checksumFile(savedToDatabase.getChecksumFile())
                .tanggalProses(savedToDatabase.getTanggalProses())
                .tanggalSelesaiProses(savedToDatabase.getTanggalSelesaiProses())
                .build();

            kafkaProducerService.produceMessage(topicEnum, fileUploadResponse);

            return "success insert : " + savedToDatabase.getId();
        } catch (Exception e) {
			e.printStackTrace();
            return "failed insert";
		}
    }

    public String kafka2(String tipe, String id) {
        kafkaProducerService.produceMessage2(tipe, id);
        return id;
    }

    @SneakyThrows
    public List<FileUploadResponse> showAllFileUpload() {
        List<MFileUpload> findAllExistingCompany = fileUploadRepository.findAll();

        List<FileUploadResponse> response = new ArrayList<>();

        findAllExistingCompany.forEach(search -> {
            FileUploadResponse searchResponse = FileUploadResponse.builder()
                    // .message("get data ")
                    // .data(search)
                    // .status(true)
                    .id(search.getId())
                    .fileName(search.getFileName())
                    .jenisReport(search.getJenisReport())
                    .statusProses(search.getStatusProses())
                    .tipeReport(search.getTipeReport())
                    .checksumFile(search.getChecksumFile())
                    .tanggalProses(search.getTanggalProses())
                    .tanggalSelesaiProses(search.getTanggalSelesaiProses())
                    .build();

            response.add(searchResponse);
        });

        return response;
    }

    public ResponseEntity<Map<String, Object>> findAllPage(String statusProses, Pageable paging) {
        Page<MFileUpload> page;
        
        if(!statusProses.isEmpty()){
            page = fileUploadRepository.findBystatusProses(statusProses, paging);
        } else {
            page = fileUploadRepository.findAll(paging);
        }
                
        Map<String, Object> response = new HashMap<>();
        response.put("items", page.getContent());
        response.put("currentPage", page.getNumber());
        response.put("totalItems", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional(transactionManager = "transactionManager")
    @SneakyThrows
    public List<MFileUpload> onProcessList() {
        List<MFileUpload> items = fileUploadRepository.findBystatusProses("onprocess");
        
        return items;
    };

    @Transactional(transactionManager = "transactionManager")
    @SneakyThrows
    public FileUploadCreateUpdateResponse onProcessById(String id) {
        Optional<MFileUpload> findExisting = fileUploadRepository.findById(id);

        if (findExisting.isEmpty() || !findExisting.get().getStatusProses().equals("onprocess")) {
            log.warn("ID Company not founds " + id);
            return FileUploadCreateUpdateResponse.builder()
                    .message("ID File Upload onprocess status not found " + id)
                    .data(null)
                    .status(false)
                    .build();
        }

        MFileUpload result = findExisting.get();
        return FileUploadCreateUpdateResponse.builder()
                .message("get onprocess status")
                .data(result)
                .status(true)
                .build();
    };

    @Transactional(transactionManager = "transactionManager")
    @SneakyThrows
    public String downloadFile(String id) {
        Optional<MFileUpload> findExisting = fileUploadRepository.findById(id);

        if (findExisting.isEmpty()) {
            return null;
        }
        MFileUpload fileUpload = findExisting.get();
        String newFilename = findExisting.get().getFileName().replace(".json", fileUpload.getTipeReport());
        return newFilename;
    };

    @SneakyThrows
    @Transactional(transactionManager = "transactionManager")
    public void delete(String id) {
        Optional<MFileUpload> object = fileUploadRepository.findById(id);
        if (!object.isPresent()) {
            log.warn("file upload with id" + id + " not exist");
            throw new IllegalStateException("file upload with id" + id + " not exist");
        }
        MFileUpload mFileUpload = object.get();
        log.warn("FILENAMEEEEE :" + mFileUpload.getFileName());
        String filename = mFileUpload.getFileName();
        String reportFilename = filename.replace(".json", mFileUpload.getTipeReport());
        storageService.delete(filename);
        storageService.delete(reportFilename);
        fileUploadRepository.deleteById(id);
    };

}
