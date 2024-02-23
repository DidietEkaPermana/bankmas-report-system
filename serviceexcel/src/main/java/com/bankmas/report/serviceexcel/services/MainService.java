package com.bankmas.report.serviceexcel.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bankmas.report.dto.FileJenisReportResponse;
import com.bankmas.report.dto.MessageKafka;
import com.bankmas.report.serviceexcel.storage.StorageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		String status = "STATUS : OK";
		logger.info("***** : onprocess");
		try {
			String filename = message.getFileName();
			logger.info("***** : filename : " + filename);
			Path path = storageService.load(filename);
			String newFilename = path.toAbsolutePath().toString().replace(".json", ".xlsx");
			
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
			generateExcelFile(list, dataField, newFilename);

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

	@Async("taskExecutor")
	public List<Map<String, String>> readJsonFile(Path jsonFilePath) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonFilePath.toFile(), new TypeReference<List<Map<String, String>>>() {});
	}

	@Async("taskExecutor")
	public Map<String, String> readJsonDataField(String jsonField) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
       	return objectMapper.readValue(jsonField, new TypeReference<Map<String, String>>() {});
	}

	@Async("taskExecutor")
    public void generateExcelFile(List<Map<String, String>> dataList, Map<String, String> jsonField, String excelFilePath) throws IOException {
        
		// Get keys only as String[]
		String[] header = jsonField.keySet().toArray(new String[0]);

		XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Data");
        sheet.setColumnWidth(2, 35*256);

        int rowNum = 0;
        int size = dataList.size();
		for (Map<String, String> data : dataList) {
			
			logger.info("PROCESS : " + rowNum + " of " + size);
            
            sheet.setColumnWidth(2, 50*256);
            XSSFRow row = sheet.createRow(rowNum++);

			for (int i = 0; i < header.length; i++) {
				if(jsonField.get(header[i]).equals("image")){
					// download image here
					InputStream inputStream = new URL(data.get(header[i])).openStream();
					byte[] bytes = IOUtils.toByteArray(inputStream);
		
					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
					BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
					int height = bufferedImage.getHeight();
					
					byteArrayInputStream.close();
					row.setHeightInPoints(height);
				
					XSSFCell cell = row.createCell(i);
                
					int pictureIndex = sheet.getWorkbook().addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PNG);
					// Create anchor that positions the image
					XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, cell.getColumnIndex(), cell.getRowIndex(), cell.getColumnIndex() + 1, cell.getRowIndex() + 1);
		
					// Create drawing patriarch
					XSSFDrawing drawing = sheet.createDrawingPatriarch();
		
					// Add picture to workbook and anchor it at specified cell
					drawing.createPicture(anchor, pictureIndex);

				} else {
					// print as text
					row.createCell(i).setCellValue(data.get(header[i]));
				}
			}
        }

        FileOutputStream outputStream = new FileOutputStream(excelFilePath);
        workbook.write(outputStream);
		workbook.close();
    }

    
}
