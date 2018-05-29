package com.cn.flypay.pageModel.sys;

import java.math.BigDecimal;

public class OrgPointConfig implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -454865180500235L;
	private Long id;
	private Long version;
	private Long organizationId;
	/**
	 * 200支付宝 300微信500银联在线
	 * */
	private Integer payType;
	private BigDecimal topRate;
	private BigDecimal midRate;
	private BigDecimal lowRate;
	private Integer toMidNum;
	private Integer toLowNum;
	private Integer status;
	private String orgName;
	/**
	 * 入账类型  D0  T1
	 */
	private Integer type;
	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public BigDecimal getTopRate() {
		return topRate;
	}

	public void setTopRate(BigDecimal topRate) {
		this.topRate = topRate;
	}

	public BigDecimal getMidRate() {
		return midRate;
	}

	public void setMidRate(BigDecimal midRate) {
		this.midRate = midRate;
	}

	public BigDecimal getLowRate() {
		return lowRate;
	}

	public void setLowRate(BigDecimal lowRate) {
		this.lowRate = lowRate;
	}

	public Integer getToMidNum() {
		return toMidNum;
	}

	public void setToMidNum(Integer toMidNum) {
		this.toMidNum = toMidNum;
	}

	public Integer getToLowNum() {
		return toLowNum;
	}

	public void setToLowNum(Integer toLowNum) {
		this.toLowNum = toLowNum;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}