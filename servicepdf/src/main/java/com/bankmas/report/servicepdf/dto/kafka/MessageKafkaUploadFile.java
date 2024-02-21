package com.bankmas.report.servicepdf.dto.kafka;

import java.util.HashMap;
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