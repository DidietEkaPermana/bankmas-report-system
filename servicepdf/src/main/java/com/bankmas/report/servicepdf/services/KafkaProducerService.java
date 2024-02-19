package com.bankmas.report.servicepdf.services;

import com.bankmas.report.servicepdf.dto.MessageStatusKafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    
    @Autowired
    private KafkaTemplate<Object, MessageStatusKafka> kafkaTemplate;

	@Async("taskExecutor")
    public MessageStatusKafka produceMessage(String id, String status, Long proses, Long selesai) {
        long start = System.currentTimeMillis();
        MessageStatusKafka message = new MessageStatusKafka(start, start, id, status, proses, selesai);
        this.kafkaTemplate.send("STATUS_FILE", message);
        return message;
    }
}