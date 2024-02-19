package com.bankmas.report.webapi.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bankmas.report.webapi.dto.UploadRequest;
import com.opencsv.CSVWriter;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Service
public class ReportBaseServiceImpl implements ReportBaseService {

	private static final String REPORT_DIRECTORY = "report.directory";
	private static final String TEMP_DIRECTORY = "export.directory";
	private static final String TMP_REPORT_FILE = "tmpReportFile";
	private static final String NO_DATA_TO_EXPORT_ERROR = "No Data to Export";
	private static final String SHEET_1 = "Sheet1";
	
	@Resource
	private Environment env;
	
	@Override
	public byte[] doExportXlsx(String jasperName, List listData, Map<String, Object> parameters)
			throws Exception {
		String rootFileUrl = env.getRequiredProperty(REPORT_DIRECTORY);
		String tempPath = env.getRequiredProperty(TEMP_DIRECTORY);
		File file = File.createTempFile(TMP_REPORT_FILE, ".xlsx", new File(tempPath));
		byte[] byteReport = null;
		try {
			// cek list size
			if (listData == null || listData.isEmpty()) {
				throw new Exception(NO_DATA_TO_EXPORT_ERROR);
			}

			// path jasper report
			String reportJasper = rootFileUrl + jasperName;

			JasperPrint jasperPrint = JasperFillManager.fillReport(reportJasper, parameters,
					new JRBeanCollectionDataSource(listData));

			JRXlsxExporter xlsxExporter = new JRXlsxExporter();

			xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));

			SimpleXlsxReportConfiguration xlsxReportConfiguration = new SimpleXlsxReportConfiguration();
			xlsxReportConfiguration.setOnePagePerSheet(false);
			xlsxReportConfiguration.setDetectCellType(true);
			xlsxReportConfiguration.setWhitePageBackground(false);

			String[] sheetNames = new String[] { SHEET_1 };
			xlsxReportConfiguration.setSheetNames(sheetNames);

			xlsxExporter.setConfiguration(xlsxReportConfiguration);
			xlsxExporter.exportReport();

			// convert file temp to byte
			byteReport = Files.readAllBytes(file.toPath());

		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}

		return byteReport;
	}

	@Override
	public byte[] doExportPdf(String jasperName, List listData, Map<String, Object> parameters)
			throws Exception {
		String rootFileUrl = env.getRequiredProperty(REPORT_DIRECTORY);
		String tempPath = env.getRequiredProperty(TEMP_DIRECTORY);
		File file = File.createTempFile(TMP_REPORT_FILE, ".pdf", new File(tempPath));
		byte[] byteReport = null;
        try {
            // cek list size
            if (listData == null || listData.isEmpty()) {
                throw new Exception(NO_DATA_TO_EXPORT_ERROR);
            }

            // path jasper report
            String reportJasper = rootFileUrl + jasperName;
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportJasper, parameters,
                    new JRBeanCollectionDataSource(listData));

            JRPdfExporter pdfExporter = new JRPdfExporter();

            pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));

            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            configuration.setCreatingBatchModeBookmarks(true);
            
            pdfExporter.setConfiguration(configuration);
            
            pdfExporter.exportReport();

            // convert file temp to byte
            byteReport = Files.readAllBytes(file.toPath());

        } catch (Exception e) {            
            throw new IOException(e.getMessage());
		}

        return byteReport;
	}

	@Override
	public byte[] doExportCsv(List<UploadRequest> listData) throws Exception {
		String rootFileUrl = env.getRequiredProperty(REPORT_DIRECTORY);
		String tempPath = env.getRequiredProperty(TEMP_DIRECTORY);
		File file = File.createTempFile(TMP_REPORT_FILE, ".csv", new File(tempPath));
		byte[] byteReport = null;
		
		try {
            // cek list size
            if (listData == null || listData.isEmpty()) {
                throw new Exception(NO_DATA_TO_EXPORT_ERROR);
            }
            
            FileWriter outputfile = new FileWriter(file); 
      
            // create CSVWriter with ',' as separator 
            CSVWriter writer = new CSVWriter(outputfile, ',', 
                                             CSVWriter.NO_QUOTE_CHARACTER, 
                                             CSVWriter.DEFAULT_ESCAPE_CHARACTER, 
                                             CSVWriter.DEFAULT_LINE_END); 
      
            // create a List which contains String array 
            List<String[]> data = new ArrayList<String[]>(); 
            data.add(new String[] { "Wilayah", "Tanggal", "Gambar" });
            
            for (UploadRequest obj : listData) {
            	data.add(new String[] { obj.getWilayah(), obj.getTanggal(), obj.getGambar() }); 
            } 
            writer.writeAll(data); 
      
            // closing writer connection 
            writer.close();

            // convert file temp to byte
            byteReport = Files.readAllBytes(file.toPath());

        } catch (Exception e) {            
            throw new IOException(e.getMessage());
		}
		
		return byteReport;
	}

}
