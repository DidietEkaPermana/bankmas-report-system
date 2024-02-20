package com.bankmas.report.webapi.controller.file;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankmas.report.webapi.dto.DataResponse;
import com.bankmas.report.webapi.dto.IdOnlyResponse;
import com.bankmas.report.webapi.dto.file.DetailReportTypeResponse;
import com.bankmas.report.webapi.dto.file.ListReportType;
import com.bankmas.report.webapi.dto.file.UpsertReportTypeRequest;
import com.bankmas.report.webapi.service.file.ReportTypeService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/report-type")
public class ReportTypeController {

    @Autowired
    ReportTypeService reportTypeService;

    @GetMapping(produces = "application/json")
    @Operation(summary = "List Report Type", description = "Get List Report Type")
    public ResponseEntity<DataResponse<List<DetailReportTypeResponse>>> listReportType(){
        return ResponseEntity.ok(reportTypeService.listReportType());
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Detail Report Type", description = "Get Detail Report Type by Id")
    public ResponseEntity<DataResponse<DetailReportTypeResponse>> getReportType(@PathVariable("id") String id){
        return ResponseEntity.ok(reportTypeService.getReportType(id));
    }

    @PostMapping(produces = "application/json")
    @Operation(summary = "Create Report Type", description = "Create Report Type")
    public ResponseEntity<DataResponse<IdOnlyResponse>> createReportType(@Valid @RequestBody UpsertReportTypeRequest request){
        return ResponseEntity.ok(reportTypeService.createReportType(request));
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Update Report Type", description = "Update Report Type by Id")
    public ResponseEntity<DataResponse<IdOnlyResponse>> updateReportType(@PathVariable("id") String id, @Valid @RequestBody UpsertReportTypeRequest request){
        return ResponseEntity.ok(reportTypeService.updateReportType(id, request));
    }

    @GetMapping(value = "/download/{id}", produces = "application/json")
    @Operation(summary = "Download Report Type Template", description = "Download Report Type Template")
    public ResponseEntity<List<Map<String,String>>> downloadReportTypeTemplate(@PathVariable("id") String id){
        return reportTypeService.downloadReportTypeTemplate(id);
    }
}
