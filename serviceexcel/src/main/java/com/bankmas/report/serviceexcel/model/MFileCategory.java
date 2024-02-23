package com.bankmas.report.serviceexcel.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "m_file_category")
@Data
@SuperBuilder
@ToString
@NamedQuery(name="MFileCategory.findAll", query="SELECT a FROM MFileCategory a")
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MFileCategory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4968386400712754455L;

	@Id
    @GeneratedValue (generator = "file-system")
    @GenericGenerator(name = "file-system", strategy = "uuid2")
    @Column (name = "id", nullable = false, length = 36)
    private String id;
    
    @Column(name = "category", nullable = true, length = 50)
    private String category;
    
    @JsonIgnore
    @OneToMany(mappedBy="mFileCategory", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MFileCategoryField> mFileCategoryFields;
    
    public MFileCategory() {
    }
}
