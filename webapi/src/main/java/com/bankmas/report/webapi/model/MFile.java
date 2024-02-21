package com.bankmas.report.webapi.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "file")
@Data
@SuperBuilder
@ToString
@NamedQuery(name="MFile.findAll", query="SELECT a FROM MFile a")
public class MFile {

	@Id
    @GeneratedValue (generator = "file-system")
    @GenericGenerator(name = "file-system", strategy = "uuid2")
    @Column (name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "file_name", nullable = true, length = 200)
    private String fileName;
    
    @Column(name = "type", nullable = true, length = 10)
    private String type;
    
    @Column(name = "category", nullable = true, length = 20)
    private String category;
    
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "data", nullable = true)
    @Basic(fetch = FetchType.LAZY)
    private byte[] data;
    
    //store json file
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "data_2", nullable = true)
    @Basic(fetch = FetchType.LAZY)
    private byte[] data2;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    
    @Column(name = "checksum_file", nullable = true, length = 200)
    private String checksumFile;

    @Column(name = "created_at", nullable = true)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true)
    private Timestamp updatedAt;
    
    @Column(name = "path", nullable = true, length = 200)
    private String path;
    
    //checksum file json
    @Column(name = "checksum_file_2", nullable = true, length = 200)
    private String checksumFile2;
    
    @ManyToOne
	@JoinColumn(name="file_category_id", referencedColumnName = "id")
	private MFileCategory mFileCategory;

    public MFile() {
    }
}
