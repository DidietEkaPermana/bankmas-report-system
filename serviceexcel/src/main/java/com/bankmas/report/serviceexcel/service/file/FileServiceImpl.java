
package com.bankmas.report.serviceexcel.service.file;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bankmas.report.serviceexcel.config.StorageProperties;
import com.bankmas.report.serviceexcel.dto.kafka.MessageKafkaUploadFile;
import com.bankmas.report.serviceexcel.exception.ValidationException;
import com.bankmas.report.serviceexcel.model.EnumUploadFileStatus;
import com.bankmas.report.serviceexcel.service.kafka.KafkaProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    RedisTemplate<String, Serializable> redisTemplate;;

    ObjectMapper objectMapper = new ObjectMapper();
;
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Override
    public void processExcelFile(MessageKafkaUploadFile message) {
        //update status to in progress
        kafkaProducer.updateStatusFile(message.id, EnumUploadFileStatus.IN_PROGRESS, null, null);
        try {

            //load file
            String jsonPath = StorageProperties.getUploadLocation() + message.getFileName() + ".json";
            BufferedReader reader = new BufferedReader(new FileReader(jsonPath));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);
            reader.close();

            List<Map<String, Object>> listMap = objectMapper.readValue(stringBuilder.toString(), List.class);
            if (listMap.size() == 0)
                return;

            //get from field jsons
            Map<String, Object> fieldJsons = getFieldJsons(message);

            Set<String> headers = fieldJsons.keySet();

            String fileName = message.fileName + ".xlsx";


            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("data");

            // add headers
            XSSFRow headerRow = sheet.createRow(0);
            int i = 0;
            for (String header : headers) {
                headerRow.createCell(i++).setCellValue(header);
            }
            int totalRow = listMap.size();
            for(i = 0; i < totalRow; i++) {
                Map<String, Object> map = listMap.get(i);
                Set<String> keySet = map.keySet();
                if(headers.stream().noneMatch(key ->keySet.contains(key))){
                    throw new ValidationException("INVALID_HEADER");
                }

                XSSFRow row = sheet.createRow(i);
                int j = 0;
                for(Entry<String, Object> field : fieldJsons.entrySet()) {
                    if(field.getValue().equals("TEXT")){
                        row.createCell(j++).setCellValue(map.get(field.getKey()).toString());
                    } else{
                        sheet.setColumnWidth(i, 40*256);
                        byte[] data = downloadImage(map.get(field.getKey()).toString());
                        
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);

                        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);

                        int height = bufferedImage.getHeight();
                        
                        byteArrayInputStream.close();
                        row.setHeightInPoints(height);

                        XSSFCell cell = row.createCell(j++);
                        
                        int pictureIndex = sheet.getWorkbook().addPicture(data, XSSFWorkbook.PICTURE_TYPE_PNG);
                        // Create anchor that positions the image
                        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, cell.getColumnIndex(), cell.getRowIndex(), cell.getColumnIndex() + 1, cell.getRowIndex() + 1);

                        // Create drawing patriarch
                        XSSFDrawing drawing = sheet.createDrawingPatriarch();

                        // Add picture to workbook and anchor it at specified cell
                        drawing.createPicture(anchor, pictureIndex);
                    }
                }
                
            }

            FileOutputStream outputStream = new FileOutputStream(StorageProperties.getExcelLocation() + fileName);

            workbook.write(outputStream);
            workbook.close();

            //update status to success
            kafkaProducer.updateStatusFile(message.id, EnumUploadFileStatus.SUCCESS, fileName, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } catch (Exception e) {
            e.printStackTrace();
            //update status to error
            kafkaProducer.updateStatusFile(message.id, EnumUploadFileStatus.ERROR, null, null, e.getMessage());
        }


    }

    private Map<String, Object> getFieldJsons(MessageKafkaUploadFile message)
            throws JsonProcessingException, JsonMappingException {
        Serializable serializable = redisTemplate.opsForValue().get("reportTypeFieldJsons::" + message.getReportTypeId());
        List<Map<String,Object>> listObjects = objectMapper.readValue(serializable.toString(), List.class);
        LinkedHashMap<String, Object> fieldJsons = new LinkedHashMap<>();
        listObjects.stream().forEach(
            e -> fieldJsons.put(e.get("name").toString(), e.get("type"))
        );
        return fieldJsons;
    }

    @Override
    public byte[] downloadFile(String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            throw new ValidationException("BAD_REQUEST");
        }

        String filePath = new ClassPathResource("/src/main/resources/document/").getPath() + fileName;
        byte[] data = Files.readAllBytes(Path.of(filePath));
        return data;
    }

    public byte[] downloadImage(String imageUrl) {
        log.info("download image: " + imageUrl);
        HttpGet getRequest = new HttpGet(imageUrl);

        try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // read response entity stream directly into a byte array
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    entity.writeTo(outputStream);
                    return outputStream.toByteArray();
                }
            }
        } catch (IOException e) {
            return null;
        } finally {
            getRequest.releaseConnection();
        }

        return null;
    }

}
