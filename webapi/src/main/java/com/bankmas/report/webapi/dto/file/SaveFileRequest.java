package com.bankmas.report.webapi.dto.file;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.bankmas.report.webapi.model.EnumDocumentFileType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveFileRequest {    
    @NotBlank
    private String reportType;
    
    private EnumDocumentFileType documentFileType;
    
    @NotNull
    private MultipartFile[] file;
}
