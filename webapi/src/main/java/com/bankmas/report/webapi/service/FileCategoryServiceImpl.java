package com.bankmas.report.webapi.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bankmas.report.webapi.model.FileCategory;
import com.bankmas.report.webapi.repository.FileCategoryRepository;

@Service
//@CacheConfig(cacheNames = "fileCategoryCache")
public class FileCategoryServiceImpl implements FileCategoryService {

	@Autowired
	private FileCategoryRepository fileCategoryRepository;

	@Transactional(transactionManager = "transactionManager")
	@Override
	//@CacheEvict(cacheNames = "fileCategories", allEntries = true)
	public String upload(MultipartFile[] files, String category) throws IOException {
		for (MultipartFile file : files) {
			String name = file.getOriginalFilename();
			byte[] data = file.getBytes();
			
			FileCategory fileCategoryDto = FileCategory.builder()
					.name(name)
					.data(data)
					.category(category)
					.build();
			
			FileCategory saveFile = fileCategoryRepository.save(fileCategoryDto);
        }
		
		return "success insert";
	}
}
