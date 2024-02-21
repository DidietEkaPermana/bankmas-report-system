package com.bankmas.report.webapi.service.file;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.bankmas.report.webapi.dto.DataResponse;
import com.bankmas.report.webapi.dto.IdOnlyResponse;
import com.bankmas.report.webapi.dto.PaginationResponse;
import com.bankmas.report.webapi.dto.file.DetailUploadFileResponse;
import com.bankmas.report.webapi.dto.file.ListFileResponse;
import com.bankmas.report.webapi.dto.file.SaveFileRequest;
import com.bankmas.report.webapi.dto.file.SaveFileResponse;
import com.bankmas.report.webapi.dto.kafka.MessageKafkaUpdateStatusFile;
import com.bankmas.report.webapi.exception.ValidationException;
import com.bankmas.report.webapi.model.EnumUploadFileStatus;

public interface FileService {
    DataResponse<List<SaveFileResponse>> saveFile(SaveFileRequest request) throws IOException, NoSuchAlgorithmException,ValidationException;
    void updateStatusFile(MessageKafkaUpdateStatusFile messageKafkaUpdateStatusFile);
    PaginationResponse<List<ListFileResponse>> listFile(Integer page, Integer size, EnumUploadFileStatus status);
    DataResponse<DetailUploadFileResponse> getFile(String id);
    ResponseEntity<byte[]> downloadFile(String id);

    DataResponse<IdOnlyResponse> deleteFile(String id) throws IOException;
}
