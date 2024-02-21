package com.bankmas.report.servicecsv.service.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.bankmas.report.servicecsv.config.StorageProperties;
import com.bankmas.report.servicecsv.dto.kafka.MessageKafkaUploadFile;
import com.bankmas.report.servicecsv.exception.ValidationException;
import com.bankmas.report.servicecsv.model.EnumUploadFileStatus;
import com.bankmas.report.servicecsv.service.kafka.KafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;

@Service
public class FileServiceImpl implements FileService{

    @Autowired
    KafkaProducer kafkaProducer;

    ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void processCsvFile(MessageKafkaUploadFile message) {

        //update status to in progress
        kafkaProducer.updateStatusFile(message.id, EnumUploadFileStatus.IN_PROGRESS, null, null);
        try{

            //load file
            String jsonPath = StorageProperties.getUploadLocation() + message.getFileName() + ".json";
            BufferedReader reader = new BufferedReader(new FileReader(jsonPath));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line=reader.readLine()) != null)
                stringBuilder.append(line);
            reader.close();
            
            List<Map<String, Object>> listMap = objectMapper.readValue(stringBuilder.toString(), List.class);
            if(listMap.size() == 0)
                return;

            Set<String> headers = message.fieldJsons.keySet();

            String fileName = message.fileName + ".csv";

            FileWriter fileWriter = new FileWriter(StorageProperties.getCsvLocation() + fileName);

            CSVWriter writer = new CSVWriter(fileWriter);

            //header
            writer.writeNext(headers.toArray(new String[headers.size()]));

            for(Map<String, Object> map : listMap) {
                Set<String> keySet = map.keySet();
                if(headers.stream().noneMatch(key ->keySet.contains(key))){
                    throw new ValidationException("INVALID_HEADER");
                }
                String[] values = new String[headers.size()];
                int i = 0;
                for(String header : headers) {
                    values[i] = map.get(header).toString();
                    i++;
                }
                writer.writeNext(values);
            }

            writer.close();

            //update status to success
            kafkaProducer.updateStatusFile(message.id, EnumUploadFileStatus.SUCCESS, fileName, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } catch(Exception e) {
            e.printStackTrace();
            //update status to error
            kafkaProducer.updateStatusFile(message.id, EnumUploadFileStatus.ERROR, null, null);
        }
            
		
	}

    @Override
    public byte[] downloadFile(String fileName) throws IOException {
        if(StringUtils.isBlank(fileName)){
            throw new ValidationException("BAD_REQUEST");
        }

        String filePath = new ClassPathResource("/src/main/resources/document/").getPath() + fileName;
        byte[] data = Files.readAllBytes(Path.of(filePath));
        return data;
    }
    
}
