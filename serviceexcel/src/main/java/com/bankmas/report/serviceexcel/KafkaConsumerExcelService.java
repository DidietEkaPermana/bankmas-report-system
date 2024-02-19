package com.bankmas.report.serviceexcel;

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

import com.bankmas.report.serviceexcel.dto.UploadRequest;

@Service
public class KafkaConsumerExcelService {

	Logger logger = LoggerFactory.getLogger(KafkaConsumerExcelService.class);
	
	@Autowired
	private FileExcelService fileExcelService;
	
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
    @KafkaListener(id = "file-topic-excel-consumer", topics = "file-topic-excel")
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
                fileExcelService.updateFile(message.get(i), "In Progress");
                List<UploadRequest> content = fileExcelService.readJsonFile(id);
                
                String jasperName = "/rptFile.jasper";
                Map<String, Object> parameters = new HashMap<>();
                
                File file = fileExcelService.doExportXlsx(jasperName, content, parameters);
                
                //TODO UPDATE MFILE
                fileExcelService.updateFileAfterExport(id, file);
                
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
