package com.bankmas.report.webapi.service.file;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.springframework.http.ResponseEntity;

import com.bankmas.report.webapi.dto.DataResponse;
import com.bankmas.report.webapi.dto.PaginationResponse;
import com.bankmas.report.webapi.dto.file.SaveFileRequest;
import com.bankmas.report.webapi.dto.kafka.MessageKafkaUpdateStatusFile;
import com.bankmas.report.webapi.exception.ValidationException;

public interface FileService {
    DataResponse saveFile(SaveFileRequest request) throws IOException, NoSuchAlgorithmException,ValidationException;
    void updateStatusFile(MessageKafkaUpdateStatusFile messageKafkaUpdateStatusFile);
    PaginationResponse listFile(Integer page, Integer size, String status);
    DataResponse getFile(String id);
    ResponseEntity<byte[]> downloadFile(String id);

    DataResponse deleteFile(String id) throws IOException;
}
