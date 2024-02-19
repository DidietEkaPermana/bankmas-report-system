package com.bankmas.report.servicecsv.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;

@Entity
@Table(name = "file_data")
@Data
@SuperBuilder
@ToString
public class MFileData {

    @Id
    @GeneratedValue (generator = "file-data-system")
    @GenericGenerator(name = "file-data-system", strategy = "uuid2")
    @Column (name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "wilayah", nullable = false, length = 100)
    private String wilayah;

    @Column(name = "tanggal", nullable = false, length = 20)
    private String tanggal;

    @Column(name = "gambar", nullable = false, length = 225)
    private String gambar;

    public MFileData() {
    }

    public MFileData(String id, String wilayah, String tanggal, String gambar) {
        this.id = id;
        this.wilayah = wilayah;
        this.tanggal = tanggal;
        this.gambar = gambar;
    }

}
