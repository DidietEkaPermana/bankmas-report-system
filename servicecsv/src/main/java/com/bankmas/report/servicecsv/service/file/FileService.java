package com.bankmas.report.servicecsv.service.file;

import java.io.IOException;

import com.bankmas.report.servicecsv.dto.kafka.MessageKafkaUploadFile;

public interface FileService {
    public void processCsvFile(MessageKafkaUploadFile messageKafkaUploadFile);
    public byte[] downloadFile(String fileName) throws IOException;
}
