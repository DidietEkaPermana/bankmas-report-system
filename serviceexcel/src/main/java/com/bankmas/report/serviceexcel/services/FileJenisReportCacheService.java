package com.bankmas.report.serviceexcel.services;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.bankmas.report.dto.FileJenisReportResponse;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@CacheConfig(cacheNames = "jenis-report")
public class FileJenisReportCacheService {

    Logger logger = LoggerFactory.getLogger(FileJenisReportCacheService.class);

    public FileJenisReportResponse findById(String id, List<FileJenisReportResponse> cachedData){
        logger.info("REDIS SIZE : " + cachedData.size()); 
        FileJenisReportResponse fJenisReportResponse = null;
        for (FileJenisReportResponse fileJenisReportResponse : cachedData) {
            
			logger.info("REDIS : " + fileJenisReportResponse.getId() + " == " + id); 
            if(fileJenisReportResponse.getId().equals(id)) {
                fJenisReportResponse = fileJenisReportResponse;
                break;
            }
        }
        return fJenisReportResponse;

    }

    @Cacheable(cacheNames = "jenis-report")
    public List<FileJenisReportResponse> listFileJenisReport() {
        // Retrieve data from the cache
        List<FileJenisReportResponse> cachedData = new ArrayList<>(); // Modify this line to retrieve data from cache

        return cachedData;
    };
}
