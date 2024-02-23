package com.bankmas.report.webapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.bankmas.report.webapi.dto.FileCategoryDTO;
import com.bankmas.report.webapi.dto.MFileCategoryDTO;
import com.bankmas.report.webapi.model.MFileCategory;
import com.bankmas.report.webapi.service.FileCategoryService;

@RestController
@RequestMapping("/file-category")
public class FileCategoryController {

	@Autowired
	private FileCategoryService fileCategoryService;
	
	@PostMapping(path="/add")
    public ResponseEntity<MFileCategoryDTO> add(@RequestBody FileCategoryDTO fileCategory) {
		MFileCategoryDTO response = new MFileCategoryDTO();
		
		try {
			response = fileCategoryService.add(fileCategory);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	//TODO when show id, category, and file category field list
	//DONE when show id and category
	@GetMapping("/findall")
	public List<MFileCategory> findAll() {
		return fileCategoryService.getAll();
	}
	
	@GetMapping("/find")
	public List<MFileCategory> findAll(@RequestParam String category) {
		return fileCategoryService.getByCategory(category.toUpperCase());
	}
	
	//TODO when show id, category, and file category field list
	//DONE when show id and category
	@GetMapping("/detail/{id}")
	public MFileCategory getById(@PathVariable("id") String id) {
		return fileCategoryService.getById(id);
	}
	
	@GetMapping(path="/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") String id) {
		byte[] fileContent = null;
		String fileName = "output_" + System.currentTimeMillis() + ".json";
		
		try {
			fileContent = fileCategoryService.download(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
		
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDispositionFormData(fileName, fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileContent.length)
                .contentType(MediaType.APPLICATION_JSON)
                .body(fileContent);
    }
	
	@PutMapping(path="/update")
    public ResponseEntity<MFileCategoryDTO> update(@RequestBody FileCategoryDTO fileCategory) {
		MFileCategoryDTO response = new MFileCategoryDTO();
		
		try {
			response = fileCategoryService.update(fileCategory);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@DeleteMapping(value = "/remove/{id}")
    public ResponseEntity<String> removeFileCategory(@PathVariable("id") String id){
        fileCategoryService.delete(id);
        
        return new ResponseEntity<>("success removed", HttpStatus.OK);
    }
}
