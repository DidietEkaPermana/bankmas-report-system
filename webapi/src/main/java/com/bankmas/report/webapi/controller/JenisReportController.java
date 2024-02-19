package com.bankmas.report.webapi.controller;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;
import com.bankmas.report.webapi.dto.FileJenisReportCreateUpdateResponse;
import com.bankmas.report.webapi.dto.FileJenisReportRequest;
import com.bankmas.report.webapi.dto.FileJenisReportResponse;
import com.bankmas.report.webapi.services.FileJenisReportServiceImpl;
import java.util.List;
import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/jenis-report")
public class JenisReportController {

    private FileJenisReportServiceImpl serviceImpl;

    @GetMapping()
    public List<FileJenisReportResponse> listData(){
        return serviceImpl.showAll();
    }

    @PostMapping()
    public FileJenisReportCreateUpdateResponse add(@Valid @RequestBody FileJenisReportRequest request){
        return serviceImpl.create(request);
    }

    @PutMapping(value = "/{id}")
    public FileJenisReportCreateUpdateResponse updateExisting(@PathVariable("id") String id, @RequestBody FileJenisReportRequest request){
        return serviceImpl.update(id, request);
    }

    @GetMapping("/detail/{id}")
    public FileJenisReportCreateUpdateResponse getById(@PathVariable("id") String id) {
        return serviceImpl.findById(id);
    }

}
