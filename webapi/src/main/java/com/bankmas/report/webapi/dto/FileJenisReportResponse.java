package com.bankmas.report.webapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileJenisReportResponse {

    private String id;
    private String namaReport;
    private String templateFile;
    private String jsonDataField;
    
}
