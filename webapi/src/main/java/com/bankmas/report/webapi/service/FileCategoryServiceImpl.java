package com.bankmas.report.webapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankmas.report.webapi.dto.FileCategoryDTO;
import com.bankmas.report.webapi.dto.MFileCategoryDTO;
import com.bankmas.report.webapi.exception.DataNotFoundException;
import com.bankmas.report.webapi.model.MFileCategory;
import com.bankmas.report.webapi.model.MFileCategoryField;
import com.bankmas.report.webapi.repository.FileCategoryFieldRepository;
import com.bankmas.report.webapi.repository.FileCategoryRepository;

import lombok.SneakyThrows;

@Service
@CacheConfig(cacheNames = "fileCategoryCache")
public class FileCategoryServiceImpl implements FileCategoryService {

	@Autowired
	private FileCategoryRepository fileCategoryRepository;
	
	@Autowired
	private FileCategoryFieldRepository fileCategoryFieldRepository;
	
	@Autowired
	private MFileCategoryDTOMapper mapper;
	
	@Transactional(transactionManager = "transactionManager")
	@Override
	@CacheEvict(cacheNames = "fileCategories", allEntries = true)
	public MFileCategoryDTO add(FileCategoryDTO fileCategory) throws Exception, DataNotFoundException{
		String category = fileCategory.getCategory();
		
		//format: key1:value1;key2:value2
		String format = fileCategory.getTemplate();
		
		List<MFileCategory> mfc = fileCategoryRepository.findByCategory(category.toUpperCase());
		
		if (!mfc.isEmpty()) {
			throw new Exception("Category" + category + " already exist");
		}
		
		MFileCategory fc = MFileCategory.builder()
				.category(category)
				.build();
		
		MFileCategory fcSave = fileCategoryRepository.save(fc);
		
		String[] keyValue = format.split(";");
		
		if (keyValue.length < 1) {
			throw new DataNotFoundException("Empty template");
		}
		List<MFileCategoryField> fcfList = new ArrayList<>();
		
		for (String s : keyValue) {
			String[] keyValue2 = s.split(":");
			
			if (keyValue2.length != 2) {
				throw new Exception("Key value not valid");
			}
			
			MFileCategoryField fcf = MFileCategoryField.builder()
					.key(keyValue2[0])
					.value(keyValue2[1])
					.mFileCategory(fcSave)
					.build();
			
			MFileCategoryField fcfSave = fileCategoryFieldRepository.save(fcf);
			
			fcfList.add(fcfSave);
		}
		fcSave.setMFileCategoryFields(fcfList);
		
		return mapper.apply(fcSave);
	}
	
	//TODO
	@Override
	@SneakyThrows
	@Cacheable(cacheNames = "fileCategories", key = "#id")
	public MFileCategory getById(String id) {
		return fileCategoryRepository.findById(id).get();
	}
	
	//TODO
	@Override
	@Cacheable(cacheNames = "fileCategories")
	public List<MFileCategory> getAll() {
		return fileCategoryRepository.findAll();
	}

	@Transactional(transactionManager = "transactionManager")
    @SneakyThrows
    @Override
    @CacheEvict(cacheNames = "fileCategories", allEntries = true)
	public MFileCategoryDTO update(FileCategoryDTO fileCategory) throws Exception, DataNotFoundException {
		Optional<MFileCategory> fc = fileCategoryRepository.findById(fileCategory.getId());
		
		if (fc.isEmpty()) {
			throw new DataNotFoundException("Id " + fileCategory.getId() + " not found");
		}
		
		List<MFileCategory> mfc = fileCategoryRepository.findByCategory(fileCategory.getCategory().toUpperCase());
		
		if (!mfc.isEmpty()) {
			throw new Exception("Category" + fileCategory.getCategory() + " already exist");
		}
		
		List<MFileCategoryField> tmpFcf = fc.get().getMFileCategoryFields();
		
		fc.get().setCategory(fileCategory.getCategory());
		MFileCategory fcSave = fileCategoryRepository.save(fc.get());
		
		String[] keyValue = fileCategory.getTemplate().split(";");
		
		if (keyValue.length < 1) {
			throw new DataNotFoundException("Empty template");
		}
		List<MFileCategoryField> fcfList = new ArrayList<>();
		
		for (String s : keyValue) {
			String[] keyValue2 = s.split(":");
			
			if (keyValue2.length != 2) {
				throw new Exception("Key value not valid");
			}
			
			MFileCategoryField fcf = MFileCategoryField.builder()
					.key(keyValue2[0])
					.value(keyValue2[1])
					.mFileCategory(fcSave)
					.build();
			
			MFileCategoryField fcfSave = fileCategoryFieldRepository.save(fcf);
			
			fcfList.add(fcfSave);
		}
		fcSave.setMFileCategoryFields(fcfList);
		
		fileCategoryFieldRepository.deleteAllInBatch(tmpFcf);
		
		return mapper.apply(fcSave);
	}

	@SneakyThrows
    @Transactional(transactionManager = "transactionManager")
    @Override
    @Caching(evict = { @CacheEvict(cacheNames = "fileCategories", key = "#id"),
			@CacheEvict(cacheNames = "fileCategories", allEntries = true) })
	public void delete(String id) {
		MFileCategory fc = fileCategoryRepository.findById(id).get();
        if (fc.getMFileCategoryFields().isEmpty()) {
            throw new IllegalStateException(
                    "File category with id" + id + " not exist");
        }
        else {
        	fileCategoryRepository.deleteById(id);
        }
	}

	@Override
	public List<MFileCategory> getByCategory(String category) {
		return fileCategoryRepository.findByCategory(category.toUpperCase());
	}
}