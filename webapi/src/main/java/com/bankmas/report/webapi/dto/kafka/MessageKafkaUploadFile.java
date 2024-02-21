package com.bankmas.report.webapi.dto.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bankmas.report.webapi.dto.file.DetailReportTypeResponse;

import lombok.Data;

@Data
public class MessageKafkaUploadFile {
    public String id;
    public String fileName;
    public Map<String, String> fieldJsons =  new HashMap<>();

    public MessageKafkaUploadFile(){}

    public MessageKafkaUploadFile(String id, String fileName, List<DetailReportTypeResponse.JsonField> fieldJsons){
        this.id = id;
        this.fileName = fileName;
        for (DetailReportTypeResponse.JsonField fieldJson : fieldJsons) {
            this.fieldJsons.put(fieldJson.getName(), fieldJson.getType().name());
        }
    }
}