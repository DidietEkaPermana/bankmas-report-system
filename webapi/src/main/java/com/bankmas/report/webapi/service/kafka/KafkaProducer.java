package com.bankmas.report.webapi.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bankmas.report.webapi.dto.kafka.MessageKafkaUploadFile;

@Service
public class KafkaProducer {

    private static final String EXCEL_TOPIC = "bankmas-report-excel";
    private static final String CSV_TOPIC = "bankmas-report-csv";
    private static final String PDF_TOPIC = "bankmas-report-pdf";

    @Autowired
    private KafkaTemplate<String, MessageKafkaUploadFile> kafkaTemplate;

    public void sendUploadFile(String documentFileType, String id, String fileName, String reportTypeId) {
        MessageKafkaUploadFile message = new MessageKafkaUploadFile(id, fileName, reportTypeId);
        switch (documentFileType) {
            case "EXCEL":
                kafkaTemplate.send(EXCEL_TOPIC, message);
                break;
            case "CSV":
                kafkaTemplate.send(CSV_TOPIC, message);
                break;
            case "PDF":
                kafkaTemplate.send(PDF_TOPIC, message);
                break;
            default:
                break;
        }
    }
    
}
