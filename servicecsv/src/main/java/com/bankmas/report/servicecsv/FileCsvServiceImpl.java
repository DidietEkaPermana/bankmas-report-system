package com.bankmas.report.servicecsv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankmas.report.servicecsv.common.DateUtil;
import com.bankmas.report.servicecsv.common.FileUtil;
import com.bankmas.report.servicecsv.dto.UploadRequest;
import com.bankmas.report.servicecsv.model.MFile;
import com.bankmas.report.servicecsv.repository.FileRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@Service
public class FileCsvServiceImpl implements FileCsvService {
	
	private static final String TEMP_DIRECTORY = "export.directory";
	private static final String TMP_REPORT_FILE = "tmpReportFile";
	private static final String NO_DATA_TO_EXPORT_ERROR = "No Data to Export";
	
	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Resource
	private Environment env;
	
	
	@Transactional(transactionManager = "transactionManager")
    @Override
	public void updateFile(String id, String status) throws Exception {
		Optional<MFile> findIdExistingFile = fileRepository.findById(id);
		Timestamp now = DateUtil.getTodayDate();
		
        if (!findIdExistingFile.isEmpty()) {
        	findIdExistingFile.get().setStatus(status);
        	findIdExistingFile.get().setUpdatedAt(now);;

        	fileRepository.save(findIdExistingFile.get());
        }
        else {
        	throw new Exception(
		              "file with id" + id + " not exist");
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
			e.printStackTrace();
			throw new IOException("Can not read json file");
		}
		
		return objects;
	}

	@Override
	public File doExportCsv(List<UploadRequest> listData) throws Exception {
		String tempPath = env.getRequiredProperty(TEMP_DIRECTORY);
		File file = File.createTempFile(TMP_REPORT_FILE, ".csv", new File(tempPath));
		
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
            //byteReport = Files.readAllBytes(file.toPath());

        } catch (Exception e) {            
        	//logger.error("Can not export file csv");
        	e.printStackTrace();
        	throw new Exception(NO_DATA_TO_EXPORT_ERROR);
		}
		
		return file;
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
			throw new IOException("Can not update file csv");
		}
		
        
	}

}
