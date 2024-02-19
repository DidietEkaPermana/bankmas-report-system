package com.bankmas.report.webapi.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.transaction.annotation.Transactional;

import com.bankmas.report.webapi.dto.MessageStatusKafka;
import com.bankmas.report.webapi.model.MFileUpload;
import com.bankmas.report.webapi.repository.FileUploadRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KafkaConsumerService {

    Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    
    private FileUploadRepository fileUploadRepository;

    //to convert object from kafka
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
    @Async("taskExecutor")
    @KafkaListener(id = "STATUS_CONSUMER", topics = "STATUS_FILE")
    public void consumeMessageKafka(@Payload List<MessageStatusKafka> message,
                                    @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                                    @Header(KafkaHeaders.OFFSET) List<Long> offsets)
    {
        logger.info("=============================================");
        logger.info("Start batch consume");

        for(int i = 0; i < message.size(); i++){
            //logger.info("received => {} with partition-offset ==> {}-{}", message.get(i), partitions.get(i), offsets.get(i));
            //logger.info("CONSUME "+i+" KAFKA JSON : " + message.get(i).getDataId());
            String resp = process(message.get(i));
            ///logger.info("CONSUME "+i+" KAFKA RESP : " + resp);
            
        }
        logger.info("=============================================");
        logger.info("Finish batch consume");
    }

    @Transactional(transactionManager = "transactionManager")
    @Async("taskExecutor")
    public String process(MessageStatusKafka message) {
        Optional<MFileUpload> optional = fileUploadRepository.findById(message.getDataId());
		if(!optional.isPresent()){
			return "not found";
		}

        logger.info("*** CONSUME "+message.getDataId()+" ****");
        logger.info("### " +message.getStatusProses()+" | "+ message.getTanggalProses() + " | " + message.getTanggalSelesaiProses());
        MFileUpload mFileUpload = optional.get();
		if(message.getTanggalProses() != null) {
            mFileUpload.setTanggalProses(new Timestamp(message.getTanggalProses()));
        }
		if(message.getTanggalSelesaiProses() != null) {
		    mFileUpload.setTanggalSelesaiProses(new Timestamp(message.getTanggalSelesaiProses()));
        }
		mFileUpload.setStatusProses(message.getStatusProses());
        //if(!message.getStatusProses().equals("finish")){
            fileUploadRepository.save(mFileUpload);
        //}

        return "updated : " + message.getDataId() + " | " + message.getStatusProses();
    }

}