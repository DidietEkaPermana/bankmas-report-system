package com.bankmas.report.servicepdf.service.file;

import java.io.IOException;

import com.bankmas.report.servicepdf.dto.kafka.MessageKafkaUploadFile;

public interface FileService {
    public void processPdfFile(MessageKafkaUploadFile messageKafkaUploadFile);
    public byte[] downloadFile(String fileName) throws IOException;
}
