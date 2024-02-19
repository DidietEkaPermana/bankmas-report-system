package com.bankmas.report.servicecsv.services;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bankmas.report.servicecsv.dto.FileData;
import com.bankmas.report.servicecsv.dto.MessageKafka;
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

	@Async("taskExecutor")
    public String process(MessageKafka message) {
		Long now = System.currentTimeMillis();
		kafkaProducerService.produceMessage(message.getDataId(), "onprocess", now, null);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		String status = "STATUS : OK";
		logger.info("***** : onprocess");
		try {
			String filename = message.getFileName();
			logger.info("***** : filename : " + filename);
			Path path = storageService.load(filename);
			List<FileData> list = readJsonFile(path);
			String newFilename = path.toAbsolutePath().toString().replace(".json", ".csv");
			generateCsvFile(list, newFilename);
			now = System.currentTimeMillis();
			kafkaProducerService.produceMessage(message.getDataId(), "finish", null, now);
			status = "STATUS : finish";
			logger.info("***** : finish");
		} catch (Exception e) {
			kafkaProducerService.produceMessage(message.getDataId(), "error", null, now);
			status = "STATUS : error";
			logger.info("***** : error : " + e.getMessage());
			e.printStackTrace();
			return e.getMessage();
		}
		return status;
	}

	public List<FileData> readJsonFile(Path jsonFilePath) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonFilePath.toFile(), new TypeReference<List<FileData>>() {});
	}

	public void generateCsvFile(List<FileData> dataList, String csvFilePath) throws IOException {
		try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
			String[] header = {"WILAYAH", "TANGGAL", "GAMBAR"};
			writer.writeNext(header);
			
			int rowNum = 0;
			int size = dataList.size();

			for (FileData data : dataList) {
				logger.info("PROCESS : " + rowNum + " of " + size);
				String[] line = {data.getWilayah(), data.getTanggal(), data.getGambar()};
				writer.writeNext(line);
				rowNum++;
			}
		}
	}
    
}
