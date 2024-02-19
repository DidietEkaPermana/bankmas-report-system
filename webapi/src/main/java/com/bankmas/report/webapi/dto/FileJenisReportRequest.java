package com.bankmas.report.webapi.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class FileJenisReportRequest {

    @NotNull(message = "The namaReport is required.")
    @NotBlank(message = "The namaReport cannot blank.")
    @Size(min = 2, message = "minimal namaReport 2 karakter")
    @Size(max = 64, message = "maksimal namaReport 100 karakter")
    private String namaReport;

    @NotNull(message = "The templateFile is required.")
    @NotBlank(message = "The templateFile cannot blank.")
    @Size(min = 2, message = "minimal templateFile 2 karakter")
    @Size(max = 200, message = "maksimal templateFile 100 karakter")
    private String templateFile;

    @NotNull(message = "The jsonDataField is required.")
    @NotBlank(message = "The jsonDataField cannot blank.")
    @Size(min = 2, message = "minimal jsonDataField 2 karakter")
    private String jsonDataField;

}
