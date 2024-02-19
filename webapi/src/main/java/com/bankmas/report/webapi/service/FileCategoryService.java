package com.bankmas.report.webapi.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileCategoryService {

	public String upload(MultipartFile[] files, String category) throws IOException;
}
