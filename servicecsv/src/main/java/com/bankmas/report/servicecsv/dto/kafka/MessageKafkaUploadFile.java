package com.bankmas.report.servicecsv.dto.kafka;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageKafkaUploadFile {
    public String id;
    public String fileName;
    public Map<String, String> fieldJsons;
}