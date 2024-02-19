package com.bankmas.report.webapi.dto.kafka;

import com.bankmas.report.webapi.model.EnumUploadFileStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageKafkaUpdateStatusFile {
    private String id;
    private EnumUploadFileStatus status;
    private String formatedFileName;
    private String finishDatetime;
}