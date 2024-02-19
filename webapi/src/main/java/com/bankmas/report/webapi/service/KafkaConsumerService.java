package com.bankmas.report.webapi.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bankmas.report.webapi.dto.MessageAfterUpdateKafka;

@Service
public class KafkaConsumerService {

	Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

	@Autowired
	private FileService fileService;
	
    @Bean
	public RecordMessageConverter converter() {
		return new JsonMessageConverter();
	}

    // for batch 
    @Bean
    public BatchMessagingMessageConverter batchConverter() {
        return new BatchMessagingMessageConverter(converter());
    }

    // batch
    // update status : completed or error
    @Async("taskExecutor")
    @KafkaListener(id = "file-topic-finish-consumer", topics = "file-topic-finish")
    public void consumeMessageKafka(@Payload List<MessageAfterUpdateKafka> message,
                                    @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                                    @Header(KafkaHeaders.OFFSET) List<Long> offsets)
    {
        logger.info("=============================================");
        logger.info("Start batch consume");

        for(int i = 0; i < message.size(); i++){
            logger.info("received => {} with partition-offset ==> {}-{}", message.get(i), partitions.get(i), offsets.get(i));
            
            String id = message.get(i).getId();
            String status = message.get(i).getStatus();
            
            fileService.updateFile(id, status);
        }
        logger.info("Finish batch consume");
    }
}
