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

import com.bankmas.report.serviceexcel.dto.FileData;
import com.bankmas.report.serviceexcel.dto.MessageKafka;
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
			List<FileData> list = readJsonFile(path);
			String newFilename = path.toAbsolutePath().toString().replace(".json", ".xlsx");
			generateExcelFile(list, newFilename);
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
	public List<FileData> readJsonFile(Path jsonFilePath) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonFilePath.toFile(), new TypeReference<List<FileData>>() {});
	}

	@Async("taskExecutor")
    public void generateExcelFile(List<FileData> dataList, String excelFilePath) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Data");
        sheet.setColumnWidth(2, 35*256);

        int rowNum = 0;
        int size = dataList.size();
        for (FileData data : dataList) {
			
			logger.info("PROCESS : " + rowNum + " of " + size);
            
            sheet.setColumnWidth(2, 50*256);

            InputStream inputStream = new URL(data.getGambar()).openStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            int height = bufferedImage.getHeight();
            
            byteArrayInputStream.close();
            
            XSSFRow row = sheet.createRow(rowNum++);
            row.setHeightInPoints(height);

            int colNum = 0;
            row.createCell(colNum++).setCellValue(data.getWilayah());
            row.createCell(colNum++).setCellValue(data.getTanggal());
            
            XSSFCell cell = row.createCell(2);
                
            int pictureIndex = sheet.getWorkbook().addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PNG);
            // Create anchor that positions the image
            XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, cell.getColumnIndex(), cell.getRowIndex(), cell.getColumnIndex() + 1, cell.getRowIndex() + 1);

            // Create drawing patriarch
            XSSFDrawing drawing = sheet.createDrawingPatriarch();

            // Add picture to workbook and anchor it at specified cell
            drawing.createPicture(anchor, pictureIndex);
        }

        FileOutputStream outputStream = new FileOutputStream(excelFilePath);
        workbook.write(outputStream);
		workbook.close();
    }

    
}
