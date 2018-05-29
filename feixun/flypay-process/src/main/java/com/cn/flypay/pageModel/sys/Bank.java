package com.cn.flypay.pageModel.sys;

public class Bank implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -8780797005609059L;
	private Long id;
	private String bankName;
	private String code;
	private Integer status;
	private String bankIco;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getBankIco() {
		return bankIco;
	}

	public void setBankIco(String bankIco) {
		this.bankIco = bankIco;
	}

}