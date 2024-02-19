package com.bankmas.report.webapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.bankmas.report.webapi.service.FileCategoryService;

@RestController
@RequestMapping("/file-category")
public class FileCategoryController {

	@Autowired
	private FileCategoryService fileCategoryService;
	
	
	@PostMapping(path="/upload", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("category") String category) {
		String response = "";
		
		try {
			response = fileCategoryService.upload(files, category);
//			response = fileService.uploadAndSend(files);
//			for (MultipartFile file : files) {
//				List<UploadRequest> objects = objectMapper.readValue(file.getInputStream(), new TypeReference<List<UploadRequest>>() {});
//				
//				for (UploadRequest obj : objects) {
//					System.out.println(obj.getWilayah());
//					System.out.println(obj.getTanggal());
//	                System.out.println(obj.getGambar());
//	            }
//				
//				String jasperName = "/rptFile.jasper";
//				Map<String, Object> parameters = new HashMap<>();
//				
//				byte[] test = reportBaseService.doExportXlsx(jasperName, objects, parameters);
//				byte[] test2 = reportBaseService.doExportCsv(objects);
//			}
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
