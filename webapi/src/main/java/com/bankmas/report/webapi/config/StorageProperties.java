package com.bankmas.report.webapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

public class StorageProperties {
    /**
	 * Folder location for storing files
	 */

	private static String rootLocation = "C:\\Users\\Ananda Dana Pratama\\Developer\\Kerja\\BANKMAS\\bankmas-report-system\\document-dir\\";
	private static String uploadLocation = "C:\\Users\\Ananda Dana Pratama\\Developer\\Kerja\\BANKMAS\\bankmas-report-system\\document-dir\\upload\\";
	private static String csvLocation = "C:\\Users\\Ananda Dana Pratama\\Developer\\Kerja\\BANKMAS\\bankmas-report-system\\document-dir\\csv\\";
	private static String pdfLocation = "C:\\Users\\Ananda Dana Pratama\\Developer\\Kerja\\BANKMAS\\bankmas-report-system\\document-dir\\pdf\\";
	private static String excelLocation = "C:\\Users\\Ananda Dana Pratama\\Developer\\Kerja\\BANKMAS\\bankmas-report-system\\document-dir\\excel\\";
	
	public static String rootLocation() {
		return rootLocation;
	}
	public static String getUploadLocation() {
		return uploadLocation;
	}
	public static String getCsvLocation() {
		return csvLocation;
	}
	public static String getPdfLocation() {
		return pdfLocation;
	}
	public static String getExcelLocation() {
		return excelLocation;
	}
}
