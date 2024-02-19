package com.bankmas.report.webapi.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.bankmas.report.webapi.dto.kafka.MessageKafkaUpdateStatusFile;
import com.bankmas.report.webapi.service.file.FileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class KafkaConsumer{

    @Autowired
    FileService fileService;
    
    @KafkaListener(topics = "bankmas-report-update-status")
    public void updateStatusFile(@Payload MessageKafkaUpdateStatusFile message) throws JsonMappingException, JsonProcessingException {
        fileService.updateStatusFile(message);
    }
}
