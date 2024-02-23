package com.bankmas.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageStatusKafka {
    private Long messageId;
    private Long generateDate;
    private String dataId;
    private String statusProses;
    private Long tanggalProses;
    private Long tanggalSelesaiProses;
}