package com.bankmas.report.servicecsv;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.bankmas.report.servicecsv.dto.UploadRequest;

public interface FileCsvService {

	public void updateFile(String id, String status);
	public void updateFileAfterExport(String id, File file) throws IOException, NoSuchAlgorithmException;
	public List<UploadRequest> readJsonFile(String id) throws IOException;
	public File doExportCsv(List<UploadRequest> listData) throws Exception;
}
