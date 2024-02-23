package com.bankmas.report.serviceexcel.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bankmas.report.dto.MessageKafka;

@Service
public class KafkaConsumerService {

    Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
	private MainService mainService;

    // batch
    @Async("taskExecutor")
    @KafkaListener(id = "EXCEL_FILE_CONSUMER", topics = "EXCEL_FILE")
    public void consumeMessageKafka(@Payload List<MessageKafka> message) {
        logger.info("=============================================");
        logger.info("Start batch consume");

        for(int i = 0; i < message.size(); i++){
            logger.info("CONSUME "+i+" KAFKA JSON : " + message.get(i).getDataId());
            String resp = mainService.process(message.get(i));
            logger.info("CONSUME "+i+" KAFKA RESP : " + resp);
            
        }
        logger.info("Finish batch consume");
    }
}