package com.bankmas.report.servicecsv.services;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bankmas.report.dto.FileJenisReportResponse;
import com.bankmas.report.dto.MessageKafka;
import com.bankmas.report.servicecsv.storage.StorageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import liquibase.repackaged.com.opencsv.CSVWriter;

@Service
public class MainService {

    Logger logger = LoggerFactory.getLogger(MainService.class);
    
	@Autowired
    private StorageService storageService;

    @Autowired
	private KafkaProducerService kafkaProducerService;

    @Autowired
	private FileJenisReportCacheService fileJenisReportCacheService;

	@Async("taskExecutor")
    public String process(MessageKafka message) {
		Long now = System.currentTimeMillis();
		kafkaProducerService.produceMessage(message.getDataId(), "onprocess", now, null);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		String status = "STATUS : OK : " + message.getJenis();
		logger.info("***** : onprocess");
		try {
			String filename = message.getFileName();
			logger.info("***** : filename : " + filename);

			Path path = storageService.load(filename);
			String newFilename = path.toAbsolutePath().toString().replace(".json", ".csv");
			
			List<Map<String, String>> list = readJsonFile(path);
			List<FileJenisReportResponse> listCache = fileJenisReportCacheService.listFileJenisReport();
			FileJenisReportResponse fileJenisReportResponse = fileJenisReportCacheService.findById(message.getJenis(), listCache);

			if(fileJenisReportResponse == null){
				now = System.currentTimeMillis();
				logger.error("File jenis report not found from cache");
				kafkaProducerService.produceMessage(message.getDataId(), "error", null, now);
				return "File jenis report not found from cache";
			}

			Map<String, String> dataField = readJsonDataField(fileJenisReportResponse.getJsonDataField());
			generateCsvFile(list, dataField, newFilename);
			now = System.currentTimeMillis();
			kafkaProducerService.produceMessage(message.getDataId(), "finish", null, now);
			status = "STATUS : finish";
			logger.info("***** : finish");
		} catch (Exception e) {
			kafkaProducerService.produceMessage(message.getDataId(), "error", null, now);
			status = "STATUS : error";
			logger.error("***** : error : " + e.getMessage());
			e.printStackTrace();
			return e.getMessage();
		}
		return status;
	}

	public List<Map<String, String>> readJsonFile(Path jsonFilePath) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonFilePath.toFile(), new TypeReference<List<Map<String, String>>>() {});
	}

	public Map<String, String> readJsonDataField(String jsonField) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
       	return objectMapper.readValue(jsonField, new TypeReference<Map<String, String>>() {});
	}

	public void generateCsvFile(List<Map<String, String>> dataList, Map<String, String> jsonField, String csvFilePath) throws IOException {
		try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
			// Get keys only as String[]
			String[] header = jsonField.keySet().toArray(new String[0]);
			writer.writeNext(header);
			
			int rowNum = 0;
			int size = dataList.size();

			for (Map<String, String> data : dataList) {
				logger.info("PROCESS : " + rowNum + " of " + size);
				String[] line = new String[header.length];
				for (int i = 0; i < header.length; i++) {
					line[i] = data.get(header[i]);
				}
				writer.writeNext(line);
				rowNum++;
			}
		}
	}
    
}
