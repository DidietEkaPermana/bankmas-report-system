package com.bankmas.report.webapi.services;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bankmas.report.webapi.dto.FileJenisReportCreateUpdateResponse;
import com.bankmas.report.webapi.dto.FileJenisReportRequest;
import com.bankmas.report.dto.FileJenisReportResponse;
import com.bankmas.report.webapi.model.MFileJenisReport;
import com.bankmas.report.webapi.repository.FileJenisReportRepository;
import com.bankmas.report.webapi.storage.StorageService;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "jenis-report")
public class FileJenisReportServiceImpl {

    @Autowired
    DataSource dataSource;

	private final StorageService storageService;

    private FileJenisReportRepository fileJenisReportRepository;

    @Transactional(transactionManager = "transactionManager")
    //@CacheEvict(cacheNames = "jenis-report", allEntries = true)
    public FileJenisReportCreateUpdateResponse create(FileJenisReportRequest request, MultipartFile file) {
        List<MFileJenisReport> nameExist = fileJenisReportRepository.findByNamaReport(request.getNamaReport());

        if (!nameExist.isEmpty()) {
            log.warn("Jenis Report name duplicate");
            // throw new IllegalStateException("company name duplicate");
            return FileJenisReportCreateUpdateResponse.builder()
                    .message("Jenis Report name duplicate")
                    .data(null)
                    .status(false)
                    .build();
        }

        
        String fileName = request.getNamaReport() + "_" + System.currentTimeMillis() + "_template.json";
        storageService.store(fileName, file);

        MFileJenisReport mReport = MFileJenisReport.builder()
                .namaReport(request.getNamaReport())
                .templateFile(fileName)
                .jsonDataField(request.getJsonDataField())
                .build();

        MFileJenisReport savedToDatabase = fileJenisReportRepository.save(mReport);
    

        return FileJenisReportCreateUpdateResponse.builder()
                .message("success insert")
                .data(savedToDatabase)
                .status(true)
                .build();
    };

    @Transactional(transactionManager = "transactionManager")
    @SneakyThrows
    //@CacheEvict(cacheNames = "jenis-report", allEntries = true)
    public FileJenisReportResponse update(String id, FileJenisReportRequest request, MultipartFile file) {
        Optional<MFileJenisReport> findIdExisting = fileJenisReportRepository.findById(id);
        List<MFileJenisReport> nameExist = fileJenisReportRepository.findByNamaReport(request.getNamaReport());

        if (!nameExist.isEmpty()) {
            log.warn("Jenis Report name duplicate");
            throw new IllegalStateException("company name duplicate");
        }

        MFileJenisReport report = findIdExisting.get();

        String oldFilename = report.getTemplateFile();

        if(!StringUtils.isBlank(request.getNamaReport())){
            report.setNamaReport(request.getNamaReport());
        }

        if(file != null) {
            try {
                storageService.delete(oldFilename);
            } catch (Exception e) {
            }

            String fileName = request.getNamaReport() + "_" + System.currentTimeMillis() + "_template.json";
            storageService.store(fileName, file);
            report.setTemplateFile(fileName);
        }
        
        if(!StringUtils.isBlank(request.getJsonDataField())){
            report.setJsonDataField(request.getJsonDataField());
        }

        MFileJenisReport saveExisting = fileJenisReportRepository.save(report);

        return FileJenisReportResponse.builder()
            .id(saveExisting.getId())
            .namaReport(saveExisting.getNamaReport())
            .templateFile(saveExisting.getTemplateFile())
            .jsonDataField(saveExisting.getJsonDataField())
            .build();
            
    };

    @Transactional(transactionManager = "transactionManager")
    @SneakyThrows
    //@Cacheable(cacheNames = "jenis-report", key = "#id", unless = "#result == null")
    public FileJenisReportResponse findById(String id) {
        Optional<MFileJenisReport> findIdExisting = fileJenisReportRepository.findById(id);

        if (findIdExisting.isEmpty()) {
            log.warn("ID Jenis Report not founds " + id);
            throw new IllegalStateException("ID Jenis Report not founds");
        }

        MFileJenisReport saveExisting = fileJenisReportRepository.save(findIdExisting.get());
 
        return FileJenisReportResponse.builder()
            .id(saveExisting.getId())
            .namaReport(saveExisting.getNamaReport())
            .templateFile(saveExisting.getTemplateFile())
            .jsonDataField(saveExisting.getJsonDataField())
            .build();
    };

    @SneakyThrows
    @Cacheable(cacheNames = "jenis-report")
    public List<FileJenisReportResponse> showAll() {
        List<MFileJenisReport> findAllExistingCompany = fileJenisReportRepository.findAll();

        List<FileJenisReportResponse> response = new ArrayList<>();

        findAllExistingCompany.forEach(search -> {
            FileJenisReportResponse searchResponse = FileJenisReportResponse.builder()
                    .id(search.getId())
                    .namaReport(search.getNamaReport())
                    .templateFile(search.getTemplateFile())
                    .jsonDataField(search.getJsonDataField())
                    .build();

            response.add(searchResponse);
        });

        return response;
    };

    @SneakyThrows
    @Transactional(transactionManager = "transactionManager")
    @Caching( evict = { 
       @CacheEvict(cacheNames = "jenis-report", key = "#id"),
       @CacheEvict(cacheNames = "jenis-report", allEntries = true) 
    })
    public void delete(String id) {
        Optional<MFileJenisReport> object = fileJenisReportRepository.findById(id);
        if (!object.isPresent()) {
            log.warn("Jenis Report with id" + id + " not exist");
            throw new IllegalStateException("Jenis Report with id" + id + " not exist");
        }
        storageService.delete(object.get().getTemplateFile());
        fileJenisReportRepository.deleteById(id);
    }

    @Transactional(transactionManager = "transactionManager")
    @SneakyThrows
    public String downloadFile(String id) {
        Optional<MFileJenisReport> findExisting = fileJenisReportRepository.findById(id);

        if (findExisting.isEmpty()) {
            return null;
        }
        String newFilename = findExisting.get().getTemplateFile();
        return newFilename;
    };

    @SneakyThrows
    @CachePut(value="jenis-report")
    public List<FileJenisReportResponse> refreshRedis() {
        List<MFileJenisReport> findAllExistingCompany = fileJenisReportRepository.findAll();

        List<FileJenisReportResponse> response = new ArrayList<>();

        findAllExistingCompany.forEach(search -> {
            FileJenisReportResponse searchResponse = FileJenisReportResponse.builder()
                    .id(search.getId())
                    .namaReport(search.getNamaReport())
                    .templateFile(search.getTemplateFile())
                    .jsonDataField(search.getJsonDataField())
                    .build();

            response.add(searchResponse);
        });

        return response;
    };

    @CacheEvict(cacheNames = "jenis-report", allEntries = true)
    public String clearRedis() {
        return "OK";
    };


    @SneakyThrows
    @Cacheable(cacheNames = "jenis-report")
    public List<FileJenisReportResponse> listCache() {
        // Retrieve data from the cache
        List<FileJenisReportResponse> cachedData = new ArrayList<>(); // Modify this line to retrieve data from cache

        // If cache is empty, return an empty list
        if (cachedData.isEmpty()) {
            throw new Exception("Cache is empty, please initialize cache");
        }

        return cachedData;
    };

}
