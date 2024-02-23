package com.bankmas.report.webapi.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "file_upload")
@Data
@SuperBuilder
@ToString
public class MFileUpload {

    @Id
    @GeneratedValue (generator = "file-upload-system")
    @GenericGenerator(name = "file-upload-system", strategy = "uuid2")
    @Column (name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "file_name", nullable = false, length = 225)
    private String fileName;

    @Column(name = "jenis_report", nullable = false, length = 225)
    private String jenisReport;

    @Column(name = "status_proses", nullable = false, length = 20)
    private String statusProses;

    @Column(name = "tipe_report", nullable = false, length = 20)
    private String tipeReport;

    @Column(name = "checksum_file", nullable = false, length = 225)
    private String checksumFile;

    @Column(name = "tanggal_proses", nullable = true)
    private Timestamp tanggalProses;

    @Column(name = "tanggal_selesai_proses", nullable = true)
    private Timestamp tanggalSelesaiProses;

    public MFileUpload() {
    }

    public MFileUpload(String id, String fileName, String jenisReport, String statusProses, String tipeReport,
            String checksumFile, Timestamp tanggalProses, Timestamp tanggalSelesaiProses) {
        this.id = id;
        this.fileName = fileName;
        this.jenisReport = jenisReport;
        this.statusProses = statusProses;
        this.tipeReport = tipeReport;
        this.checksumFile = checksumFile;
        this.tanggalProses = tanggalProses;
        this.tanggalSelesaiProses = tanggalSelesaiProses;
    }

    
    

}
