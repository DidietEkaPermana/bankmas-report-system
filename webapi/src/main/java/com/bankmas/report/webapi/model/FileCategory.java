package com.bankmas.report.webapi.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "file_category")
@Data
@SuperBuilder
@ToString
@NamedQuery(name="FileCategory.findAll", query="SELECT a FROM FileCategory a")
public class FileCategory {

	@Id
    @GeneratedValue (generator = "file-system")
    @GenericGenerator(name = "file-system", strategy = "uuid2")
    @Column (name = "id", nullable = false, length = 36)
    private String id;
	
	@Column(name = "name", nullable = true, length = 200)
    private String name;
    
    @Column(name = "category", nullable = true, length = 20)
    private String category;
    
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "data", nullable = true)
    @Basic(fetch = FetchType.LAZY)
    private byte[] data;

    public FileCategory() {
    }
}
