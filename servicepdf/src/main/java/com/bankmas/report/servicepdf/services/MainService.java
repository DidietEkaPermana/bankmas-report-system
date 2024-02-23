package com.bankmas.report.servicepdf.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bankmas.report.dto.FileJenisReportResponse;
import com.bankmas.report.dto.MessageKafka;
import com.bankmas.report.servicepdf.storage.StorageService;
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
			String newFilename = path.toAbsolutePath().toString().replace(".json", ".pdf");
            
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
			generatePdfFile(list, dataField, newFilename);
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
    public void generatePdfFile(List<Map<String, String>> dataList, Map<String, String> jsonField, String pdfFilePath) throws IOException {
        try (PDDocument document = new PDDocument()) {

            // Get keys only as String[]
		    String[] header = jsonField.keySet().toArray(new String[0]);

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA, 12);

            float startY = page.getMediaBox().getHeight() - 50; // Start Y position
            
            float lineSpacing = 20; // Line spacing

            float max_image_size = 50;

            int rowNum = 0;
            int size = dataList.size();

            float imageHeight_ = 0;
            for (Map<String, String> data : dataList) {
                
                logger.info("PROCESS : " + rowNum + " of " + size);
                float currentY = startY;
                for (int i = 0; i < header.length; i++) {
                    if(jsonField.get(header[i]).equals("image")){
					    // download image here
                        drawText(contentStream, header[i] + " : ", 50, currentY);
                        currentY -= lineSpacing / 2;
                        InputStream inputStream = new URL(data.get(header[i])).openStream();

                        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, inputStreamToByteArray(inputStream), "image");
                        float imageWidth = pdImage.getWidth();
                        float imageHeight = pdImage.getHeight();
                        imageHeight_ = imageHeight_ + imageHeight;
        
                        if(imageWidth > max_image_size) imageWidth = max_image_size;
                        if(imageHeight > max_image_size) imageHeight = max_image_size;
        
                        contentStream.drawImage(pdImage, 50, currentY - imageHeight, imageWidth, imageHeight);
                        currentY -= imageHeight + 10; // Adjust currentY
                    } else {
                        // print as text
                        drawText(contentStream,   header[i] + " : " + data.get(header[i]) , 50, currentY);
                        currentY -= lineSpacing;
                    }
                }


                // Adjust startY for next entry
                startY = currentY - 10;

                // Check if new page is needed
                
                if (startY <= (50 + max_image_size)) {
                    imageHeight_ = 0;
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    startY = page.getMediaBox().getHeight() - 50;
                }

                rowNum++;
            }

            contentStream.close();
            document.save(pdfFilePath);
        }
    }

	@Async("taskExecutor")
    private void drawText(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

	@Async("taskExecutor")
    private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
}
