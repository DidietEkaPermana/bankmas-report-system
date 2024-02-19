package com.bankmas.report.servicepdf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankmas.report.servicepdf.common.DateUtil;
import com.bankmas.report.servicepdf.common.FileUtil;
import com.bankmas.report.servicepdf.dto.UploadRequest;
import com.bankmas.report.servicepdf.model.MFile;
import com.bankmas.report.servicepdf.repository.FileRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

@Service
public class FilePdfServiceImpl implements FilePdfService {

	private static final String REPORT_DIRECTORY = "report.directory";
	private static final String TEMP_DIRECTORY = "export.directory";
	private static final String TMP_REPORT_FILE = "tmpReportFile";
	private static final String NO_DATA_TO_EXPORT_ERROR = "No Data to Export";
	private static final String SHEET_1 = "Sheet1";
	
	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Resource
	private Environment env;
	
	@Transactional(transactionManager = "transactionManager")
    @SneakyThrows
    @Override
	public void updateFile(String id, String status) {
		Optional<MFile> findIdExistingFile = fileRepository.findById(id);
		Timestamp now = DateUtil.getTodayDate();
		
        if (!findIdExistingFile.isEmpty()) {
        	findIdExistingFile.get().setStatus(status);
        	findIdExistingFile.get().setUpdatedAt(now);;

        	fileRepository.save(findIdExistingFile.get());
        }
	}

	@Transactional(transactionManager = "transactionManager")
    @Override
	public void updateFileAfterExport(String id, File file) throws IOException, NoSuchAlgorithmException {
		Optional<MFile> findIdExistingFile = fileRepository.findById(id);
		Timestamp now = DateUtil.getTodayDate();
		
		try {
			if (!findIdExistingFile.isEmpty()) {
	        	String fileName = file.getName();
	        	byte[] byteReport = Files.readAllBytes(file.toPath());
	        	String checksum = FileUtil.calculateChecksum(byteReport);
	        	
	        	findIdExistingFile.get().setFileName(fileName);
	        	//findIdExistingFile.get().setData(byteReport);
	        	findIdExistingFile.get().setPath(file.getAbsolutePath());
	        	findIdExistingFile.get().setChecksumFile(checksum);
	        	findIdExistingFile.get().setUpdatedAt(now);;

	        	fileRepository.save(findIdExistingFile.get());
	        }
		}
		catch (IOException|NoSuchAlgorithmException e) {
			//logger.error("Can not update file csv");
			e.printStackTrace();
		}
		
        
	}

	@Override
	@SneakyThrows
	public List<UploadRequest> readJsonFile(String id) throws IOException {
		List<UploadRequest> objects = new ArrayList<>();
		
		try {
			Optional<MFile> findIdExistingFile = fileRepository.findById(id);
			
			if (!findIdExistingFile.isEmpty()) {
				String tempPath = env.getRequiredProperty(TEMP_DIRECTORY);
				File file = File.createTempFile(TMP_REPORT_FILE, ".json", new File(tempPath));
				
				String filePath = file.getAbsolutePath();
				Path path = Paths.get(filePath);
		        Files.write(path, findIdExistingFile.get().getData2());
		        
		        objects = objectMapper.readValue(file, new TypeReference<List<UploadRequest>>() {});
			}
		}
		catch (IOException e) {
			//logger.error("Can not read json file");
			e.printStackTrace();
		}
		
		return objects;
	}

	@Override
	public File doExportPdf(String jasperName, List listData, Map<String, Object> parameters) throws Exception {
		String rootFileUrl = env.getRequiredProperty(REPORT_DIRECTORY);
		String tempPath = env.getRequiredProperty(TEMP_DIRECTORY);
		File file = File.createTempFile(TMP_REPORT_FILE, ".pdf", new File(tempPath));
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

        } catch (Exception e) {            
            throw new IOException(e.getMessage());
		}

        return file;
	}

}
