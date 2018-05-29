package com.cn.flypay.model.sys;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;


@Entity
@Table(name = "sys_dictionarytype")
@DynamicInsert(true)
@DynamicUpdate(true)
public class Tdictionarytype implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4995712506960257595L;
	private String code;
	private String name;
	private Integer seq;
	private String description;
	private Tdictionarytype dictionarytype;
	
	public Tdictionarytype(){
		
	}
	
	public Tdictionarytype(String code, String name, Integer seq,
			String description) {
		super();
		this.code = code;
		this.name = name;
		this.seq = seq;
		this.description = description;
	}
	private long id;
	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NotBlank
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@NotBlank
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pid")
	public Tdictionarytype getDictionarytype() {
		return dictionarytype;
	}

	public void setDictionarytype(Tdictionarytype dictionarytype) {
		this.dictionarytype = dictionarytype;
	}


}
