package com.bankmas.report.servicecsv.dto.kafka;

import com.bankmas.report.servicecsv.model.EnumUploadFileStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageKafkaUpdateStatusFile {
    private String id;
    private EnumUploadFileStatus status;
    private String formatedFileName;
    private String finishDatetime;
}