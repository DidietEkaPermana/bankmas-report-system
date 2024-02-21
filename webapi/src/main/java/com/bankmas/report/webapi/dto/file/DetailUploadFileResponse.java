package com.bankmas.report.webapi.dto.file;

import java.time.LocalDateTime;


import com.bankmas.report.webapi.model.EnumDocumentFileType;
import com.bankmas.report.webapi.model.EnumUploadFileStatus;
import com.bankmas.report.webapi.model.UploadFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DetailUploadFileResponse {
    private String id;
    private String name;
    private EnumDocumentFileType documentFileType;
    private EnumUploadFileStatus status;
    private LocalDateTime processDatetime;
    private LocalDateTime finishDatetime;
    private String checksum;
    private String formatedFileName;
    private String originalFileName;
    public DetailUploadFileResponse(UploadFile uploadFile) {
        this.id = uploadFile.getId();
        this.name = uploadFile.getName();
        this.documentFileType = uploadFile.getDocumentFileType();
        this.status = uploadFile.getStatus();
        this.processDatetime = uploadFile.getProcessDatetime();
        this.finishDatetime = uploadFile.getFinishDatetime();
        this.checksum = uploadFile.getChecksum();
        this.formatedFileName = uploadFile.getFormatedFileName();
        this.originalFileName = uploadFile.getOriginalFileName();
    }
}
