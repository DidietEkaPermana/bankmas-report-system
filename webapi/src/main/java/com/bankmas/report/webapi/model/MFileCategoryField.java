package com.bankmas.report.webapi.model;

import java.io.Serializable;

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

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "m_file_category_field")
@Data
@SuperBuilder
@ToString
@NamedQuery(name="MFileCategoryField.findAll", query="SELECT a FROM MFileCategoryField a")
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MFileCategoryField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4865341808039154722L;

	@Id
    @GeneratedValue (generator = "file-system")
    @GenericGenerator(name = "file-system", strategy = "uuid2")
    @Column (name = "id", nullable = false, length = 36)
    private String id;
    
    @Column(name = "key", nullable = true, length = 100)
    private String key;
    
    @Column(name = "value", nullable = true, length = 100)
    private String value;
    
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name="file_category_id", referencedColumnName = "id")
	private MFileCategory mFileCategory;
    
    public MFileCategoryField() {
    }
    
}
