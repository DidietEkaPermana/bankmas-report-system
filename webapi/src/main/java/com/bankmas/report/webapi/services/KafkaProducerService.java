package com.bankmas.report.webapi.services;

import com.bankmas.report.webapi.dto.FileUploadResponse;

// import java.util.ArrayList;
// import java.util.List;

import com.bankmas.report.webapi.dto.MessageKafka;
import com.bankmas.report.webapi.model.MFileUpload;
import com.bankmas.report.webapi.model.TopicEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    private KafkaTemplate<Object, MessageKafka> kafkaTemplate;

    // @Autowired
    // private KafkaTemplate<Object, List<MessageKafka>> kafkaTemplateBatch;

    public MessageKafka produceMessage(TopicEnum topic, FileUploadResponse n){

        long start = System.currentTimeMillis();
        MessageKafka kafka = new MessageKafka(start, start, n.getId(), n.getChecksumFile(), n.getFileName());
        this.kafkaTemplate.send(topic.name(), kafka);
        return kafka;
    }

    public String produceMessage2(String topicStr, String id){
        TopicEnum topic = null;
        try {
            topic = TopicEnum.valueOf(topicStr);
        } catch (Exception e) {
            return topicStr + " not found";
        }
        long start = System.currentTimeMillis();
        MessageKafka kafka = new MessageKafka(start, start, id, null);
        this.kafkaTemplate.send(topic.name(), kafka);
        return id;
    }

}