package com.bankmas.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageKafka {
    private Long messageId;
    private Long generateDate;
    private String dataId;
    private String checksum;
    private String fileName;
    private String jenis;
}