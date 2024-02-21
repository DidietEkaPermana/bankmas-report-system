package com.bankmas.report.webapi.service;

import java.util.List;

import com.bankmas.report.webapi.dto.FileCategoryDTO;
import com.bankmas.report.webapi.dto.MFileCategoryDTO;
import com.bankmas.report.webapi.exception.DataNotFoundException;
import com.bankmas.report.webapi.model.MFileCategory;

public interface FileCategoryService {
	public MFileCategoryDTO add(FileCategoryDTO fileCategory) throws Exception, DataNotFoundException;
	public MFileCategory getById(String id);
	public List<MFileCategory> getAll();
	public MFileCategoryDTO update(FileCategoryDTO fileCategory) throws Exception, DataNotFoundException;
	public void delete(String id);
	public List<MFileCategory> getByCategory(String category);
}
