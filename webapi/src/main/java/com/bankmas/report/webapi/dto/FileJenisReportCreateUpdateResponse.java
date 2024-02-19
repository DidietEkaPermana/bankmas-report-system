package com.bankmas.report.webapi.dto;

import com.bankmas.report.webapi.model.MFileJenisReport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class FileJenisReportCreateUpdateResponse {
    private String message;
    private MFileJenisReport data;
    private Boolean status;
}
