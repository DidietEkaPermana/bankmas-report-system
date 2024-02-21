package com.bankmas.report.servicepdf.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bankmas.report.servicepdf.dto.kafka.MessageKafkaUpdateStatusFile;
import com.bankmas.report.servicepdf.model.EnumUploadFileStatus;

@Service
public class KafkaProducer {
    private static final String UPDATE_STATUS_TOPIC = "bankmas-report-update-status";

    @Autowired
    private KafkaTemplate<String, MessageKafkaUpdateStatusFile> kafkaTemplate;
    public void updateStatusFile(String id, EnumUploadFileStatus status, String formatedFileName, String finishDatetime) {
        updateStatusFile(id, status, formatedFileName, finishDatetime, null);
    }

    public void updateStatusFile(String id, EnumUploadFileStatus status, String formatedFileName, String finishDatetime, String reason) {
        MessageKafkaUpdateStatusFile message = MessageKafkaUpdateStatusFile.builder()
               .id(id)
               .status(status)
               .formatedFileName(formatedFileName)
               .finishDatetime(finishDatetime)
               .reason(reason)
               .build();
        kafkaTemplate.send(UPDATE_STATUS_TOPIC, message);
    }
}
