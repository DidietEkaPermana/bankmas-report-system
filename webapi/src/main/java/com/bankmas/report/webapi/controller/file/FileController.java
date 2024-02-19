package com.bankmas.report.webapi.controller.file;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankmas.report.webapi.dto.DataResponse;
import com.bankmas.report.webapi.dto.PaginationResponse;
import com.bankmas.report.webapi.dto.file.SaveFileRequest;
import com.bankmas.report.webapi.exception.ValidationException;
import com.bankmas.report.webapi.service.file.FileService;

@RestController
@RequestMapping("/api/v1/file")
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<DataResponse> saveFile(@Valid @ModelAttribute SaveFileRequest request) throws IOException, NoSuchAlgorithmException,ValidationException{
        return ResponseEntity.ok(fileService.saveFile(request));
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<PaginationResponse> listFile(
                @RequestParam(required = false, value = "page", defaultValue = "0") Integer page, 
                @RequestParam(required = false, value = "size", defaultValue = "10") Integer size, 
                @RequestParam(required = false, value = "status") String status)
        {
        return ResponseEntity.ok(fileService.listFile(page, size, status));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DataResponse> getFile(@PathVariable("id") String id){
        return ResponseEntity.ok(fileService.getFile(id));
    }

    @GetMapping(value = "/download/{id}",produces = "application/octet-stream")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("id") String id){
        return fileService.downloadFile(id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<DataResponse> deleteFile(@PathVariable("id") String id) throws IOException{
        return ResponseEntity.ok(fileService.deleteFile(id));
    }
}
