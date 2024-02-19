package com.bankmas.report.serviceexcel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bankmas.report.serviceexcel.dto.MessageAfterUpdateKafka;

@Service
public class KafkaProducerImpl implements KafkaProducer {

	Logger logger = LoggerFactory.getLogger(KafkaProducerImpl.class);

    private static final String TOPIC = "file-topic-finish";
    
    @Autowired
	@Lazy
    private KafkaTemplate<Object,MessageAfterUpdateKafka> kafkaTemplate;

    @Async("taskExecutor")
	@Override
	public void generateMessage(String id, String status) {
    	try {
    		MessageAfterUpdateKafka message = new MessageAfterUpdateKafka(id, status);
    		this.kafkaTemplate.send(TOPIC, message);
    	} catch (Exception e) {
    		logger.error(id + "error: " + e.getMessage());
    	}
	}



//    @Async("taskExecutor")
//    public void generateMessage(String id, String status){
//        try {
//            MessageAfterUpdateKafka message = new MessageAfterUpdateKafka(id, status);
//            this.kafkaTemplate.send(TOPIC, message);
//        } catch (Exception e){
//            logger.error(id + "error: " + e.getMessage());
//        }
//    }
}
