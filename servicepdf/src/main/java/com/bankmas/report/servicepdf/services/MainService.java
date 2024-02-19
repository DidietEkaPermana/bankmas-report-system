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

import com.bankmas.report.servicepdf.dto.FileData;
import com.bankmas.report.servicepdf.dto.MessageKafka;
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
			String newFilename = path.toAbsolutePath().toString().replace(".json", ".pdf");
			generatePdfFile(list, newFilename);
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
    public void generatePdfFile(List<FileData> dataList, String pdfFilePath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA, 12);

            float startY = page.getMediaBox().getHeight() - 50; // Start Y position
            float lineSpacing = 20; // Line spacing

            float max_image_size = 50;

            int rowNum = 0;
            int size = dataList.size();

            for (FileData data : dataList) {
                
                logger.info("PROCESS : " + rowNum + " of " + size);
                float currentY = startY;

                // Draw Wilayah
                drawText(contentStream,   "Wilayah : " + data.getWilayah(), 50, currentY);
                currentY -= lineSpacing;

                // Draw Tanggal
                drawText(contentStream, "Tanggal : " + data.getTanggal(), 50, currentY);
                currentY -= lineSpacing;

                drawText(contentStream, "Gambar : ", 50, currentY);
                currentY -= lineSpacing / 2;

                InputStream inputStream = new URL(data.getGambar()).openStream();

                PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, inputStreamToByteArray(inputStream), "image");
                float imageWidth = pdImage.getWidth();
                float imageHeight = pdImage.getHeight();

                if(imageWidth > max_image_size) imageWidth = max_image_size;
                if(imageHeight > max_image_size) imageHeight = max_image_size;

                contentStream.drawImage(pdImage, 50, currentY - imageHeight, imageWidth, imageHeight);
                currentY -= imageHeight + 10; // Adjust currentY

                // Adjust startY for next entry
                startY = currentY - 10;

                // Check if new page is needed
                if (startY <= (50 + imageHeight )) {
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
