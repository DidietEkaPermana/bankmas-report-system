package com.bankmas.report.webapi.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.springframework.web.multipart.MultipartFile;

import com.bankmas.report.webapi.dto.FileCreateUpdateResponse;
import com.bankmas.report.webapi.dto.FileDescription;
import com.bankmas.report.webapi.dto.FileListResponse;
import com.bankmas.report.webapi.dto.FileResponse;
import com.bankmas.report.webapi.dto.PagingRequest;
import com.bankmas.report.webapi.model.MFile;

public interface FileService {

	public FileCreateUpdateResponse upload(MultipartFile[] files) throws IOException, NoSuchAlgorithmException;
	public FileResponse findById(String id);
	public MFile findFileById(String fileId);
	public void deleteFile(String id);
	public FileListResponse findByCriteria(PagingRequest pagingRequest) throws Exception;
	public void updateFile(String id, String status);
	public FileCreateUpdateResponse uploadAndSend(MultipartFile[] files) throws IOException;
	public FileCreateUpdateResponse uploadByType(MultipartFile[] files, String fileDescription) throws IOException, NoSuchAlgorithmException, Exception;
	public void readJsonFile(String id) throws IOException;
}
