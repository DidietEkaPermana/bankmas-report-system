package com.bankmas.report.servicecsv.service.kafka;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bankmas.report.servicecsv.dto.kafka.MessageKafkaUploadFile;
import com.bankmas.report.servicecsv.service.file.FileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class KafkaConsumer{

    @Autowired
    FileService fileService;
    
    @Async("taskExecutor")
    @KafkaListener(topics = "bankmas-report-csv")
    public void receiveUploadFile(@Payload MessageKafkaUploadFile message) throws JsonMappingException, JsonProcessingException {
        CompletableFuture.runAsync(() -> {
            // Long running process...  
            fileService.processCsvFile(message);
        });
    }
}
