package com.bankmas.report.webapi.dto.file;

import java.time.LocalDateTime;

import com.bankmas.report.webapi.model.UploadFile;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ListFileResponse {
    private String id;
    private String fileName;
    private String reportType;
    private String status;
    private String documentType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processDatetime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishDatetime;

    public ListFileResponse(){

    }

    public ListFileResponse(UploadFile uploadFile){
        this.id = uploadFile.getId();
        this.fileName = uploadFile.getName();
        this.reportType = uploadFile.getReportType();
        this.status = uploadFile.getStatus().toString();
        this.documentType = uploadFile.getDocumentFileType().toString();
        this.processDatetime = uploadFile.getProcessDatetime();
        this.finishDatetime = uploadFile.getFinishDatetime();
    }
}