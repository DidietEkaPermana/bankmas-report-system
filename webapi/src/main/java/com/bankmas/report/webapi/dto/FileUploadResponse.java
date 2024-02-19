package com.bankmas.report.webapi.dto;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponse {

    private String id;
    private String fileName;
    private String jenisReport;
    private String statusProses;
    private String tipeReport;
    private String checksumFile;
    private Timestamp tanggalProses;
    private Timestamp tanggalSelesaiProses;
}
