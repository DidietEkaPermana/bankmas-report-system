package com.bankmas.report.webapi.service.file;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.bankmas.report.webapi.dto.DataResponse;
import com.bankmas.report.webapi.dto.IdOnlyResponse;
import com.bankmas.report.webapi.dto.file.UpsertReportTypeRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bankmas.report.webapi.dto.file.DetailReportTypeResponse;

public interface ReportTypeService {
    public DataResponse<List<DetailReportTypeResponse>> listReportType();
    public DataResponse<DetailReportTypeResponse> getReportType(String id);

    public DataResponse<IdOnlyResponse> createReportType(UpsertReportTypeRequest request) throws JsonProcessingException;
    public DataResponse<IdOnlyResponse> updateReportType(String id, UpsertReportTypeRequest request) throws JsonProcessingException;
    public ResponseEntity<?> downloadReportTypeTemplate(String id);
    public ResponseEntity<List<Map<String,String>>> generateReportTypeTemplate(String id);
    public DataResponse<IdOnlyResponse> uploadReportTypeTemplate(String id, MultipartFile multipartFile) throws IOException;
}
