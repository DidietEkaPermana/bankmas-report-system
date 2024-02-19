package com.bankmas.report.servicepdf;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.bankmas.report.servicepdf.dto.UploadRequest;

@Service
public class KafkaConsumerPdfService {

	Logger logger = LoggerFactory.getLogger(KafkaConsumerPdfService.class);
	
	@Autowired
	private FilePdfService filePdfService;
	
	@Autowired
	private KafkaProducer KafkaProducer;

    @Bean
	public RecordMessageConverter converter() {
		return new JsonMessageConverter();
	}

    // for batch 
    @Bean
    public BatchMessagingMessageConverter batchConverter() {
        return new BatchMessagingMessageConverter(converter());
    }
    
    //@Autowired
    //private KafkaTemplate<Object,MessageAfterUpdateKafka> kafkaTemplate;

    // @Autowired
    // private MessageDao messageDao;

    // non batch
    // @Async("taskExecutor")
    // @KafkaListener(topics = "poc", groupId = "poc_consumer")
    // public void consumeMessageKafka(@Payload MessageKafka message,
    //                                 @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partitions,
    //                                 @Header(KafkaHeaders.OFFSET) Long offsets)
    // {
    //     // saveMessage(message);
    //     logger.info("received => {} with partition-offset ==> {}-{}", message, partitions, offsets);
    // }

    // batch
    @Async("taskExecutor")
    @KafkaListener(id = "file-topic-pdf-consumer", topics = "file-topic-pdf")
    public void consumeMessageKafka(@Payload List<String> message,
                                    @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                                    @Header(KafkaHeaders.OFFSET) List<Long> offsets)
    {
        String id = "";
        
    	try {
    		logger.info("=============================================");
            logger.info("Start batch consume");

            for(int i = 0; i < message.size(); i++){
                logger.info("received => {} with partition-offset ==> {}-{}", message.get(i), partitions.get(i), offsets.get(i));
                
                id = message.get(i);
                filePdfService.updateFile(message.get(i), "In Progress");
                List<UploadRequest> content = filePdfService.readJsonFile(id);
                
                String jasperName = "/rptFile.jasper";
                Map<String, Object> parameters = new HashMap<>();
                
                File file = filePdfService.doExportPdf(jasperName, content, parameters);
                
                //TODO UPDATE MFILE
                filePdfService.updateFileAfterExport(id, file);
                
                logger.info("Finish update file");
                
                KafkaProducer.generateMessage(id, "Completed");
            }
            logger.info("Finish batch consume");
    	}
    	catch (Exception e) {
    		KafkaProducer.generateMessage(id, "Error");
            
    		logger.error("File can not created");
    	}
        
    }
}