package com.bankmas.report.webapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bankmas.report.webapi.dto.MessageKafka;

@Service
public class KafkaProducerService {

	Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private static final String TOPIC = "file-topic-csv";

    @Autowired
    @Lazy
    private KafkaTemplate<Object,MessageKafka> kafkaTemplate;
    
    @Autowired
    @Lazy
    private KafkaTemplate<Object,String> kafkaTemplate2;

    // @Autowired
    // private KafkaTemplate<Object, List<MessageKafka>> kafkaTemplateBatch;

    public long produceMessage(int n){
        long  start = System.currentTimeMillis();
        logger.info("start message => " +  System.currentTimeMillis());

        for(int i=1; i<=n; i++){
            generateMessage(i);
        }
        long finish = System.currentTimeMillis()-start;

        logger.info("end message => " +  System.currentTimeMillis());
        return finish;
    }

    @Async("taskExecutor")
    public void generateMessage(int i){
        try {
            // JSONObject message = new JSONObject();
            // long millis = System.currentTimeMillis();
            // message.put("sequenceNumber", i);
            // message.put("generatedTimestamp", millis);
            // this.kafkaTemplate.send(TOPIC, message.toString());
            MessageKafka message = new MessageKafka(Long.valueOf(i), System.currentTimeMillis());
            this.kafkaTemplate.send(TOPIC, message);
        } catch (Exception e){
            logger.error(i + "error: " + e.getMessage());
        }
    }
    
    //send to service csv, pdf, xlsx
    @Async("taskExecutor")
    public void sendMessage(String topic, String id){
        try {
            this.kafkaTemplate2.send(topic, id);
        } catch (Exception e){
            logger.error(id + "error: " + e.getMessage());
        }
    }
}
