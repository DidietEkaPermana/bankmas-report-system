package com.bankmas.report.webapi.controller;

import lombok.AllArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.bankmas.report.webapi.model.MFileUpload;
import com.bankmas.report.webapi.model.TopicEnum;
import com.bankmas.report.webapi.services.FileUploadServiceImpl;
import com.bankmas.report.webapi.storage.StorageService;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/web-api")
public class WebApiController {

	private final StorageService storageService;
    private FileUploadServiceImpl fileUploadService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> fileUploadListData( 
        @RequestParam(defaultValue = "") String statusProses,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
    ){
        Pageable paging = PageRequest.of(page, size, Sort.by("tanggalProses").descending());
        return fileUploadService.findAllPage(statusProses, paging);
    }

    @GetMapping("/onProcessList")
    public List<MFileUpload> onProcess() {
        return fileUploadService.onProcessList();
    }

    @DeleteMapping(value = "/{id}")
    public String delete(@PathVariable("id") String id){
        fileUploadService.delete(id);
        return "success deleted";
    }

    @PostMapping(path="/uploadAll", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleFileUploadProcess(
        @RequestParam String tipeReport,
        @RequestParam String jenisReportId,
        @RequestParam("file") MultipartFile file
    ) {
        TopicEnum topicEnum = null;
        if(tipeReport.equals("csv")) {
            topicEnum = TopicEnum.CSV_FILE;
        } else if(tipeReport.equals("excel")) {
            topicEnum = TopicEnum.EXCEL_FILE;
        } else if(tipeReport.equals("pdf")) {
            topicEnum = TopicEnum.PDF_FILE;
        }
        if(topicEnum == null){
            return "Invalid tipe (csv, excel, pdf)";
        }
        try {
            return fileUploadService.uploadFile(topicEnum, jenisReportId, file);   
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // @PostMapping(path="/uploadCSV", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    // public String handleFileUpload(@RequestParam("file") MultipartFile file) {

    //     try {
    //         return fileUploadService.uploadFile(TopicEnum.CSV_FILE, file);   
    //     } catch (Exception e) {
    //         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    //     }
    // }

    // @PostMapping(path="/uploadExcel", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    // public String handleFileUpload2(@RequestParam("file") MultipartFile file) {

    //     try {
    //         return fileUploadService.uploadFile(TopicEnum.EXCEL_FILE, file);   
    //     } catch (Exception e) {
    //         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    //     }
    // }

    // @PostMapping(path="/uploadPdf", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    // public String handleFileUpload3(@RequestParam("file") MultipartFile file) {

    //     try {
    //         return fileUploadService.uploadFile(TopicEnum.PDF_FILE, file);   
    //     } catch (Exception e) {
    //         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    //     }
    // }

    @GetMapping(path="/download-file/{filename:.+}", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
    

    @GetMapping(path="/download/{id}", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> download(@PathVariable("id") String id) {
        String filename = fileUploadService.downloadFile(id);
        
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/kafka")
    public String kafka(
        @RequestParam("tipe") String tipe,
        @RequestParam("id") String id
    ) {
        return fileUploadService.kafka2(tipe, id);
    }

}
