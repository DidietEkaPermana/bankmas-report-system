package com.bankmas.report.webapi.dto.kafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageKafkaUploadFile {
    public String id;
    public String fileName;
}