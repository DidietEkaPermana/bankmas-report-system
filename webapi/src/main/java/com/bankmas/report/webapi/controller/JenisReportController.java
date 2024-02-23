package com.bankmas.report.webapi.controller;

import lombok.AllArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bankmas.report.webapi.dto.FileJenisReportCreateUpdateResponse;
import com.bankmas.report.webapi.dto.FileJenisReportRequest;
import com.bankmas.report.dto.FileJenisReportResponse;
import com.bankmas.report.webapi.services.FileJenisReportServiceImpl;
import com.bankmas.report.webapi.storage.StorageService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/jenis-report")
public class JenisReportController {

	private final StorageService storageService;
    private FileJenisReportServiceImpl serviceImpl;

    @GetMapping("/showAll")
    public List<FileJenisReportResponse> listData(){
        return serviceImpl.showAll();
    }

    @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileJenisReportCreateUpdateResponse add(
        @RequestParam(required = true) String namaReport,
        @RequestParam(required = true) String jsonDataField,
        @RequestParam("fileTemplate") MultipartFile fileTemplate
    ){
        FileJenisReportRequest request = FileJenisReportRequest.builder()
            .namaReport(namaReport)
            .jsonDataField(jsonDataField)
            .build();

        FileJenisReportCreateUpdateResponse response = serviceImpl.create(request, fileTemplate);
        
        // refresh redis data
        serviceImpl.refreshRedis();

        return response;
    }

    @PutMapping(value = "/{id}", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileJenisReportResponse update(
        @PathVariable("id") String id, 
        @RequestParam String namaReport,
        @RequestParam String jsonDataField,
        @RequestParam("file") MultipartFile file   
    ){
        FileJenisReportRequest request = FileJenisReportRequest.builder()
            .namaReport(namaReport)
            .jsonDataField(jsonDataField)
            .build();

        FileJenisReportResponse response = serviceImpl.update(id, request, file);
        
        // refresh redis data
        serviceImpl.refreshRedis();

        return response;
    }

    @GetMapping("/detail/{id}")
    public FileJenisReportResponse getById(@PathVariable("id") String id) {
        return serviceImpl.findById(id);
    }

    @GetMapping(path="/download-template/{id}", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> download(@PathVariable("id") String id) {
        String filename = serviceImpl.downloadFile(id);
        
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping(value = "/{id}")
    public String delete(@PathVariable("id") String id){
        serviceImpl.delete(id);
        return "success deleted";
    }


    @GetMapping("/listCache")
    public List<FileJenisReportResponse> listCache(){
        return serviceImpl.listCache();
    }

}
