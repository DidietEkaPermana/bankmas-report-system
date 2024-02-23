package com.bankmas.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FileJenisReportResponse {

    private String id;
    private String namaReport;
    private String templateFile;
    private String jsonDataField;

    
    public FileJenisReportResponse() {
        // Default constructor
    }
    
}
