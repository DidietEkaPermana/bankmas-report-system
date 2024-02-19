package com.bankmas.report.webapi.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class FileUploadRequest {

    @NotNull(message = "The fileName is required.")
    @NotBlank(message = "The fileName cannot blank.")
    @Size(min = 2, message = "minimal fileName 2 karakter")
    @Size(max = 64, message = "maksimal fileName 225 karakter")
    private String fileName;

    @NotNull(message = "The jenisReport is required.")
    @NotBlank(message = "The jenisReport cannot blank.")
    @Size(min = 2, message = "minimal jenisReport 2 karakter")
    @Size(max = 200, message = "maksimal jenisReport 20 karakter")
    private String jenisReport;

    @NotNull(message = "The tipeReport is required.")
    @NotBlank(message = "The tipeReport cannot blank.")
    @Size(min = 2, message = "minimal tipeReportl 2 karakter")
    @Size(max = 64, message = "maksimal tipeReport 20 karakter")
    private String tipeReport;

}
