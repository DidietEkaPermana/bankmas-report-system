package com.bankmas.report.serviceexcel;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import com.bankmas.report.serviceexcel.dto.UploadRequest;

public interface FileExcelService {

	public void updateFile(String id, String status);
	public void updateFileAfterExport(String id, File file) throws IOException, NoSuchAlgorithmException;
	public List<UploadRequest> readJsonFile(String id) throws IOException;
	public File doExportXlsx(String jasperName, List listData, Map<String, Object> parameters)
			throws Exception;
}
