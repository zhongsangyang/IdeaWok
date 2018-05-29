package com.cn.flypay.pageModel.trans;

// default package

import java.util.Date;

public class AppTransInfo implements java.io.Serializable {
	private static final long serialVersionUID = 439793244244820857L;
	private Long id;
	private Long version;
	private String appId;
	private String phone;
	private Integer transType;
	private Double amt;
	private Date transDatetime;
	private Date transDatetimeStart;
	private Date transDatetimeEnd;
	private Date statementDatetime;
	private Integer statementStuts;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getTransType() {
		return transType;
	}
	public void setTransType(Integer transType) {
		this.transType = transType;
	}
	public Double getAmt() {
		return amt;
	}
	public void setAmt(Double amt) {
		this.amt = amt;
	}
	public Date getTransDatetime() {
		return transDatetime;
	}
	public void setTransDatetime(Date transDatetime) {
		this.transDatetime = transDatetime;
	}
	public Date getStatementDatetime() {
		return statementDatetime;
	}
	public void setStatementDatetime(Date statementDatetime) {
		this.statementDatetime = statementDatetime;
	}
	public Integer getStatementStuts() {
		return statementStuts;
	}
	public void setStatementStuts(Integer statementStuts) {
		this.statementStuts = statementStuts;
	}
	public Date getTransDatetimeStart() {
		return transDatetimeStart;
	}
	public void setTransDatetimeStart(Date transDatetimeStart) {
		this.transDatetimeStart = transDatetimeStart;
	}
	public Date getTransDatetimeEnd() {
		return transDatetimeEnd;
	}
	public void setTransDatetimeEnd(Date transDatetimeEnd) {
		this.transDatetimeEnd = transDatetimeEnd;
	}


}