package com.bankmas.report.webapi.services;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bankmas.report.webapi.dto.FileJenisReportCreateUpdateResponse;
import com.bankmas.report.webapi.dto.FileJenisReportRequest;
import com.bankmas.report.webapi.dto.FileJenisReportResponse;
import com.bankmas.report.webapi.exception.StorageException;
import com.bankmas.report.webapi.model.MFileJenisReport;
import com.bankmas.report.webapi.repository.FileJenisReportRepository;

import javax.sql.DataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "jenisReport")
public class FileJenisReportServiceImpl {

    @Autowired
    DataSource dataSource;

    private FileJenisReportRepository fileJenisReportRepository;

    @Transactional(transactionManager = "transactionManager")
    @CacheEvict(cacheNames = "companies", allEntries = true)
    public FileJenisReportCreateUpdateResponse create(FileJenisReportRequest request) {
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

        MFileJenisReport mReport = MFileJenisReport.builder()
                .namaReport(request.getNamaReport())
                .templateFile(request.getTemplateFile())
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
    @CacheEvict(cacheNames = "companies", allEntries = true)
    public FileJenisReportCreateUpdateResponse update(String id, FileJenisReportRequest request) {
        Optional<MFileJenisReport> findIdExisting = fileJenisReportRepository.findById(id);
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

        MFileJenisReport report = findIdExisting.get();

        report.setNamaReport(request.getNamaReport());
        report.setTemplateFile(request.getTemplateFile());
        report.setJsonDataField(request.getJsonDataField());

        MFileJenisReport saveExisting = fileJenisReportRepository.save(report);

        return FileJenisReportCreateUpdateResponse.builder()
                .message("success updated")
                .data(saveExisting)
                .status(true)
                .build();
    };

    @Transactional(transactionManager = "transactionManager")
    @SneakyThrows
    @Cacheable(cacheNames = "jenis-report", key = "#id", unless = "#result == null")
    public FileJenisReportCreateUpdateResponse findById(String id) {
        Optional<MFileJenisReport> findIdExisting = fileJenisReportRepository.findById(id);

        if (findIdExisting.isEmpty()) {
            log.warn("ID Jenis Report not founds " + id);
            return FileJenisReportCreateUpdateResponse.builder()
                    .message("ID Jenis Report not founds " + id)
                    .data(null)
                    .status(false)
                    .build();
        }

        MFileJenisReport saveExistingCompanyId = fileJenisReportRepository.save(findIdExisting.get());
        return FileJenisReportCreateUpdateResponse.builder()
                .message("get detail")
                .data(saveExistingCompanyId)
                .status(true)
                .build();
    };

    @SneakyThrows
    //@Cacheable(cacheNames = "jenis-report")
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
        boolean exists = fileJenisReportRepository.existsById(id);
        if (!exists) {
            log.warn("company with id" + id + " not exist");
            throw new IllegalStateException(
                    "company with id" + id + " not exist");
        }
        fileJenisReportRepository.deleteById(id);
    }
}
