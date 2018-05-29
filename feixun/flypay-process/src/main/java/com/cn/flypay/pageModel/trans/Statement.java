package com.cn.flypay.pageModel.trans;

import java.math.BigDecimal;
import java.util.Date;

public class Statement implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 3522263444457884L;
	private Long id;
	private Integer version;
	private String statemtentDate;
	private Long tradeNum;
	private Integer stsType;
	private Long successNum;
	private BigDecimal tradeAmt;
	private BigDecimal successAmt;
	private BigDecimal feeAmt;
	private BigDecimal feeRate;
	private Integer status;
	private Date createDate;

	private String createDateStart;
	private String createDateEnd;

	
	private Integer stsSuccussNum;
	private Integer stsFailNum;
	private Integer stsAutoDealNum;
	private Integer stsFreezeAccountNum;
	private Integer stsFreezeBrokerageNum;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getStatemtentDate() {
		return statemtentDate;
	}

	public Integer getStsType() {
		return stsType;
	}

	public void setStsType(Integer stsType) {
		this.stsType = stsType;
	}

	public Integer getStsAutoDealNum() {
		return stsAutoDealNum;
	}

	public void setStsAutoDealNum(Integer stsAutoDealNum) {
		this.stsAutoDealNum = stsAutoDealNum;
	}

	public Integer getStsFreezeAccountNum() {
		return stsFreezeAccountNum;
	}

	public void setStsFreezeAccountNum(Integer stsFreezeAccountNum) {
		this.stsFreezeAccountNum = stsFreezeAccountNum;
	}

	public Integer getStsFreezeBrokerageNum() {
		return stsFreezeBrokerageNum;
	}

	public void setStsFreezeBrokerageNum(Integer stsFreezeBrokerageNum) {
		this.stsFreezeBrokerageNum = stsFreezeBrokerageNum;
	}

	public void setStatemtentDate(String statemtentDate) {
		this.statemtentDate = statemtentDate;
	}

	public Long getTradeNum() {
		return tradeNum;
	}

	public String getCreateDateStart() {
		return createDateStart;
	}

	public void setCreateDateStart(String createDateStart) {
		this.createDateStart = createDateStart;
	}

	public String getCreateDateEnd() {
		return createDateEnd;
	}

	public void setCreateDateEnd(String createDateEnd) {
		this.createDateEnd = createDateEnd;
	}

	public void setTradeNum(Long tradeNum) {
		this.tradeNum = tradeNum;
	}

	public Long getSuccessNum() {
		return successNum;
	}

	public void setSuccessNum(Long successNum) {
		this.successNum = successNum;
	}

	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	public BigDecimal getSuccessAmt() {
		return successAmt;
	}

	public void setSuccessAmt(BigDecimal successAmt) {
		this.successAmt = successAmt;
	}

	public BigDecimal getFeeAmt() {
		return feeAmt;
	}

	public void setFeeAmt(BigDecimal feeAmt) {
		this.feeAmt = feeAmt;
	}

	public BigDecimal getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getStsSuccussNum() {
		return stsSuccussNum;
	}

	public void setStsSuccussNum(Integer stsSuccussNum) {
		this.stsSuccussNum = stsSuccussNum;
	}

	public Integer getStsFailNum() {
		return stsFailNum;
	}

	public void setStsFailNum(Integer stsFailNum) {
		this.stsFailNum = stsFailNum;
	}


}