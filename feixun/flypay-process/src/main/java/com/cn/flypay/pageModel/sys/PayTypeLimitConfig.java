package com.cn.flypay.pageModel.sys;

import java.math.BigDecimal;

public class PayTypeLimitConfig implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 47985083987L;
	private Long id;
	private Long version;
	private Integer payType;
	private Integer amtType;
	private BigDecimal maxAmt;
	private BigDecimal minAmt;
	private BigDecimal srvFee;
	private Integer status; 
	private String hotType;
	private String sltType;
	private Long organizationId;

	private String organizationName;
	private String name;
	private String code;
	private String payTypeName;
	
	private String startTime;
	private String endTime;
	private String unSupportCardName;

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

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getAmtType() {
		return amtType;
	}

	public void setAmtType(Integer amtType) {
		this.amtType = amtType;
	}

	public BigDecimal getMaxAmt() {
		return maxAmt;
	}

	public void setMaxAmt(BigDecimal maxAmt) {
		this.maxAmt = maxAmt;
	}

	public BigDecimal getMinAmt() {
		return minAmt;
	}

	public void setMinAmt(BigDecimal minAmt) {
		this.minAmt = minAmt;
	}
	
	public BigDecimal getSrvFee() {
		return srvFee;
	}

	public void setSrvFee(BigDecimal srvFee) {
		this.srvFee = srvFee;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}


	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getHotType() {
		return hotType;
	}

	public void setHotType(String hotType) {
		this.hotType = hotType;
	}

	public String getSltType() {
		return sltType;
	}

	public void setSltType(String sltType) {
		this.sltType = sltType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getPayTypeName() {
		return payTypeName;
	}

	public void setPayTypeName(String payTypeName) {
		this.payTypeName = payTypeName;
	}

	public String getUnSupportCardName() {
		return unSupportCardName;
	}

	public void setUnSupportCardName(String unSupportCardName) {
		this.unSupportCardName = unSupportCardName;
	}

}