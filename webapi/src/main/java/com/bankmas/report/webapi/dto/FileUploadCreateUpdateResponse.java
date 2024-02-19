package com.bankmas.report.webapi.dto;

import com.bankmas.report.webapi.model.MFileUpload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class FileUploadCreateUpdateResponse {
    private String message;
    private MFileUpload data;
    private Boolean status;
}
