package com.bankmas.report.serviceexcel.dto.kafka;

import com.bankmas.report.serviceexcel.model.EnumUploadFileStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageKafkaUpdateStatusFile {
    private String id;
    private EnumUploadFileStatus status;
    private String formatedFileName;
    private String finishDatetime;
    private String reason;
}