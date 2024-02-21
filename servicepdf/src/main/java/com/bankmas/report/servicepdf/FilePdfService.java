package com.bankmas.report.servicepdf;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import com.bankmas.report.servicepdf.dto.UploadRequest;

public interface FilePdfService {

	public void updateFile(String id, String status) throws Exception;
	public void updateFileAfterExport(String id, File file) throws IOException, NoSuchAlgorithmException;
	public List<UploadRequest> readJsonFile(String id) throws IOException;
	public File doExportPdf(String jasperName, List listData, Map<String, Object> parameters)
			throws Exception;
}
