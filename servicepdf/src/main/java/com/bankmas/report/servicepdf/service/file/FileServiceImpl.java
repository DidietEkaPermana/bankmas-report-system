package com.bankmas.report.servicepdf.service.file;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bankmas.report.servicepdf.config.StorageProperties;
import com.bankmas.report.servicepdf.dto.kafka.MessageKafkaUploadFile;
import com.bankmas.report.servicepdf.exception.ValidationException;
import com.bankmas.report.servicepdf.model.EnumUploadFileStatus;
import com.bankmas.report.servicepdf.service.kafka.KafkaProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileServiceImpl implements FileService{

    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    RedisTemplate<String, Serializable> redisTemplate;;

    ObjectMapper objectMapper = new ObjectMapper();

    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    // @Override
    // public void processCsvFile(MessageKafkaUploadFile message) {
    //     kafkaProducer.updateStatusFile(message.id, EnumUploadFileStatus.IN_PROGRESS, null, null);
    //     try{
    //         TypeReference<List<Map<String, String>>> typeReference = new TypeReference<List<Map<String, String>>>() {};
    //         List<Map<String, String>> data = objectMapper.readValue(message.getValue(), typeReference);

    //         // Generate a unique file name for the PDF document.
    //         String fileName = "PDF-" + Instant.now().toEpochMilli() + ".pdf";

    //         // Create a file path using the file name and the resource path.
    //         Path filePath = Path.of(new ClassPathResource("/src/main/resources/document/").getPath() + fileName);

    //         Document document = new Document();
    //         try (OutputStream outputStream = new FileOutputStream(filePath.toFile())) {
    //             PdfWriter.getInstance(document, outputStream);
    //             document.open();
                
    //             PdfPTable pdfPTable = new PdfPTable(3);
    //             pdfPTable.addCell("wilayah");
    //             pdfPTable.addCell("tanggal");
    //             pdfPTable.addCell("gambar");

    //             document.add(pdfPTable);

    //             ExecutorService executor = Executors.newFixedThreadPool(5);

    //             // Split data list and submit tasks
    //             List<List<Map<String, String>>> partitions = Lists.partition(data, 10);
    //             for(List<Map<String, String>> partition : partitions) {
    //                 executor.submit(new BuildTableTask(partition, fileName, document));
    //             }
    //             executor.shutdown();
    //             executor.awaitTermination(120, TimeUnit.SECONDS);

    //             document.close();
    //         }
            
            

    //     } catch (Exception e) {
    //         e.printStackTrace();

    //         // Update the file status to "ERROR" using the KafkaProducer.
    //         kafkaProducer.updateStatusFile(message.id, EnumUploadFileStatus.ERROR, null, null);
    //     }
        
    // }

    // class BuildTableTask implements Runnable {

    //     private List<Map<String,String>> rows;
    //     private String fileName;
    //     private Document document;

    //     public BuildTableTask(List<Map<String,String>> rows, String fileName, Document document) {
    //         this.rows = rows;
    //         this.fileName = fileName;
    //         this.document = document;
    //     }

    //     @Override 
    //     public void run() {
    //         // Create table 
    //         PdfPTable pTable = new PdfPTable(3);
            
    //         // Batch add rows
    //         for(Map<String, String> row : rows){
    //             pTable.addCell(row.get("wilayah"));;
    //             pTable.addCell(row.get("tanggal"));

    //             byte[] downloadImage;
    //             try {
    //                 downloadImage = downloadImage(row.get("gambar"));
    //                 Image image =  Image.getInstance(downloadImage);
    //                 PdfPCell pdfPCell = new PdfPCell(image);
    //                 pTable.addCell(pdfPCell);
    //             } catch (IOException|BadElementException e) {
    //                 // TODO Auto-generated catch block
    //                 e.printStackTrace();
    //             }
                
    //         }

    //         try {
    //             document.add(pTable);
    //         } catch (DocumentException e) {
    //             // TODO Auto-generated catch block
    //             e.printStackTrace();
    //         }
    //     }

    // }
    

    @Override
    public void processPdfFile(MessageKafkaUploadFile message) {
        // Update the file status to "IN_PROGRESS" using the KafkaProducer.
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
            
            // Generate a unique file name for the PDF document.
            String fileName = message.fileName + ".pdf";

            // Create a file path using the file name and the resource path.
            Path filePath = Path.of(StorageProperties.getPdfLocation() + fileName);

            try (OutputStream outputStream = Files.newOutputStream(filePath)) {

                PdfPTable pTable = new PdfPTable(headers.size());

                // Add the headers to the table.
                for (String header : headers) 
                    pTable.addCell(header);
                

                // Iterate over the list of maps starting from the second map.
                for (int i = 0; i < listMap.size(); i++) {
                    Map<String, Object> map = listMap.get(i);
                    Set<String> keySet = map.keySet();
                    if(headers.stream().noneMatch(key ->keySet.contains(key))){
                        throw new ValidationException("INVALID_HEADER");
                    }
                    
                    for(Entry<String, Object> field : fieldJsons.entrySet()){
                        if(field.getValue().equals("TEXT")){
                            pTable.addCell(map.get(field.getKey()).toString());
                        } else{
                            // If the header is "gambar", download the image using the downloadImage method and add it to the table as an image cell.
                            byte[] downloadImage = downloadImage(map.get(field.getKey()).toString());
                            Image image = Image.getInstance(downloadImage);
                            PdfPCell imageCell = new PdfPCell(image, true);
                            pTable.addCell(imageCell);
                        }
                    }

                    
                }
                // Create a new iText Document and associate it with the output stream.
                Document document = new Document();
                PdfWriter.getInstance(document, outputStream);

                document.open();
                document.add(pTable);
                document.close();
            }

            // Update the file status to "SUCCESS" using the KafkaProducer, along with the file name and the current date and time.
            kafkaProducer.updateStatusFile(message.id, EnumUploadFileStatus.SUCCESS, fileName, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } catch (Exception e) {
            e.printStackTrace();

            // Update the file status to "ERROR" using the KafkaProducer.
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
        if(fileName == null || fileName.isEmpty()) {
            throw new ValidationException("BAD_REQUEST");
        }

        String filePath = new ClassPathResource("/src/main/resources/document/").getPath() + fileName;
        return Files.readAllBytes(Path.of(filePath));
    }

    public byte[] downloadImage(String imageUrl) throws IOException {
        log.info("download image: " + imageUrl);
        HttpGet getRequest = new HttpGet(imageUrl);
        
        try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
            if(response.getStatusLine().getStatusCode() != 200){
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
        }
        
        return null;
    }
}