package com.bankmas.report.webapi.service;

import java.util.List;
import java.util.Map;

import com.bankmas.report.webapi.dto.UploadRequest;

public interface ReportBaseService {
	
	public byte[] doExportXlsx(String jasperName, List listData, Map<String, Object> parameters)
			throws Exception;
	public byte[] doExportPdf(String jasperName, List listData, Map<String, Object> parameters)
			throws Exception;
	public byte[] doExportCsv(List<UploadRequest> listData) throws Exception;
}
