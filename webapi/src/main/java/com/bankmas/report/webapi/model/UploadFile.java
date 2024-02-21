package com.bankmas.report.webapi.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "upload_file")
@Data
@SuperBuilder
@ToString
@NoArgsConstructor
public class UploadFile {
    @Id
    @GeneratedValue (generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id",  length = 36, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_type_id")
    private ReportType reportType;

    @Column(name = "document_file_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private EnumDocumentFileType documentFileType;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private EnumUploadFileStatus status;

    @Column(name = "process_datetime", nullable = false)
    @CreationTimestamp
    private LocalDateTime processDatetime;

    @Column(name = "finish_datetime")
    private LocalDateTime finishDatetime;

    @Column(name = "checksum", columnDefinition = "TEXT")
    private String checksum;

    @Column(name = "formated_file_name", columnDefinition = "TEXT")
    private String formatedFileName;

    @Column(name = "original_file_name", columnDefinition = "TEXT")
    private String originalFileName;
}
