package com.bankmas.report.webapi.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "message_after_update")
public class MessageAfterUpdateKafka {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "status")
    private String status;
}