package com.bankmas.report.servicecsv;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bankmas.report.dto.FileJenisReportResponse;
import com.bankmas.report.servicecsv.services.FileJenisReportCacheService;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/services-csv")
public class MainController {
    
    private FileJenisReportCacheService fileJenisReportCacheService;

    @GetMapping("/listCache")
    public List<FileJenisReportResponse> listCache(){
        return fileJenisReportCacheService.listFileJenisReport();
    }

}
