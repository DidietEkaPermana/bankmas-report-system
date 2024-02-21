package com.bankmas.report.webapi.service.file;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.bankmas.report.webapi.dto.DataResponse;
import com.bankmas.report.webapi.dto.IdOnlyResponse;
import com.bankmas.report.webapi.dto.file.UpsertReportTypeRequest;
import com.bankmas.report.webapi.dto.file.DetailReportTypeResponse;

public interface ReportTypeService {

    public List<DetailReportTypeResponse.JsonField> getReportTypeFieldJsons(String id);
    public DataResponse<List<DetailReportTypeResponse>> listReportType();
    public DataResponse<DetailReportTypeResponse> getReportType(String id);

    public DataResponse<IdOnlyResponse> createReportType(UpsertReportTypeRequest request);
    public DataResponse<IdOnlyResponse> updateReportType(String id, UpsertReportTypeRequest request);
    public ResponseEntity<List<Map<String,String>>> downloadReportTypeTemplate(String id);
}
