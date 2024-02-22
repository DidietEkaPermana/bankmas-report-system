package com.bankmas.report.webapi.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.redis.core.RedisHash;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "report_type")
@Data
@SuperBuilder
@NoArgsConstructor
@ToString
public class ReportType {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "template_file", length = 255)
    private String templateFile;

    @CreationTimestamp
    @Column(name = "created_datetime", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'")
    private LocalDateTime createdDatetime;

    @UpdateTimestamp
    @Column(name = "updated_datetime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'")
    private LocalDateTime updatedDatetime;

    @OneToMany(mappedBy="reportType")
    private List<ReportTypeFieldJson> fieldJsons;
    
}
