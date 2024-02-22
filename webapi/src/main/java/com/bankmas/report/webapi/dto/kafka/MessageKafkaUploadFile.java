package com.bankmas.report.webapi.dto.kafka;

import lombok.Data;

@Data
public class MessageKafkaUploadFile {
    public String id;
    public String fileName;
    public String reportTypeId;

    public MessageKafkaUploadFile(){}

    public MessageKafkaUploadFile(String id, String fileName, String reportTypeId){
        this.id = id;
        this.fileName = fileName;
        this.reportTypeId = reportTypeId;
    }
}