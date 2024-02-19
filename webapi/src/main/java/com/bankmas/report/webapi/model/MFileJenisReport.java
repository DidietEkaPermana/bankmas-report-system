package com.bankmas.report.webapi.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;

@Entity
@Table(name = "file_jenis_report")
@Data
@SuperBuilder
@ToString
public class MFileJenisReport {

    @Id
    @GeneratedValue (generator = "file-jenis-report-system")
    @GenericGenerator(name = "file-jenis-report-system", strategy = "uuid2")
    @Column (name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "nama_report", nullable = false, length = 100)
    private String namaReport;

    @Column(name = "template_file", nullable = false, length = 100)
    private String templateFile;

    @Column(name = "json_data_field", nullable = false)
    private String jsonDataField;

    public MFileJenisReport() {
    }

    public MFileJenisReport(String id, String namaReport, String templateFile, String jsonDataField) {
        this.id = id;
        this.namaReport = namaReport;
        this.templateFile = templateFile;
        this.jsonDataField = jsonDataField;
    }

    

}
