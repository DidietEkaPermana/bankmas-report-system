package com.bankmas.report.servicepdf.dto.kafka;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageKafkaUploadFile {
    public String id;
    public String fileName;
    public String reportTypeId;
}