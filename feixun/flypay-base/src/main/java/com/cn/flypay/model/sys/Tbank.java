package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SysBank entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_bank")
public class Tbank implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -8772780797005609059L;
	private Long id;
	private String bankName;
	private String code;
	private Integer status;
	private String bankIco;

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "bank_name", length = 128)
	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	@Column(name = "bank_ico", length = 128)
	public String getBankIco() {
		return bankIco;
	}

	public void setBankIco(String bankIco) {
		this.bankIco = bankIco;
	}

	@Column(name = "code", length = 128)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}