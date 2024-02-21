package com.bankmas.report.webapi.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.bankmas.report.webapi.dto.FileCreateUpdateResponse;
import com.bankmas.report.webapi.dto.FileDescription;
import com.bankmas.report.webapi.dto.FileListResponse;
import com.bankmas.report.webapi.dto.FileResponse;
import com.bankmas.report.webapi.dto.PagingRequest;
import com.bankmas.report.webapi.model.MFile;
import com.bankmas.report.webapi.service.FileService;

@RestController
@RequestMapping("/file")
public class FileController {

	@Autowired
	private FileService fileService;
	
	@GetMapping
    public String test() {
        return "Hello World";
    }
	
	@PostMapping(value = "/findbycriteria", produces = "application/json")
	public ResponseEntity<FileListResponse> findByCriteria(@RequestBody(required = true) PagingRequest pagingRequest) {
		FileListResponse response = new FileListResponse();
		
		try {
			response = fileService.findByCriteria(pagingRequest);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/detail/{id}")
    public FileResponse getFileById(@PathVariable("id") String id) {
        return fileService.findById(id);
    }
	
	@DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable("id") String id){
        fileService.deleteFile(id);
        
        return new ResponseEntity<>("success deleted", HttpStatus.OK);
    }
	
	@GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable("id") String id) {
        MFile fileDetail = fileService.findFileById(id);
        File file = new File(fileDetail.getPath());
        
        // Check if the file exists
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        
        // Prepare resource for the file
        Resource resource = new FileSystemResource(file);
        
        // Prepare headers for the response
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
	
	@PostMapping(path="/upload", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileCreateUpdateResponse> uploadFiles(@RequestParam("files") MultipartFile[] files) {
		FileCreateUpdateResponse response = new FileCreateUpdateResponse();
		
		try {
			response = fileService.uploadAndSend(files);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@PostMapping(path="/upload-by-type", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileCreateUpdateResponse> uploadFileByType(@RequestParam("files") MultipartFile[] files, @RequestParam("fileDescription") String fileDescription) {
		FileCreateUpdateResponse response = new FileCreateUpdateResponse();
		
		try {
			response = fileService.uploadByType(files, fileDescription);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
