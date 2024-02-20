package com.bankmas.report.serviceexcel.service.file;

import java.io.IOException;

import com.bankmas.report.serviceexcel.dto.kafka.MessageKafkaUploadFile;

public interface FileService {
    public void processExcelFile(MessageKafkaUploadFile messageKafkaUploadFile);
    public byte[] downloadFile(String fileName) throws IOException;
}
