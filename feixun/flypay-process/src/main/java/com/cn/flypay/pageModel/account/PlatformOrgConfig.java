package com.cn.flypay.pageModel.account;

import java.math.BigDecimal;

public class PlatformOrgConfig implements java.io.Serializable {

	private static final long serialVersionUID = 16565541L;
	private Long id;
	private Long version;
	private String orgName;
	/**
	 * 平台交易金额手续费率
	 */
	private BigDecimal platformInputRate;
	/**
	 * 平台实名认证费
	 */
	private BigDecimal platformAuthenticationFee;
	/**
	 * 平台短信费
	 */
	private BigDecimal platformMessageFee;
	/**
	 * 平台单笔提现手续费
	 */
	private BigDecimal platformTixianFee;
	/**
	 * 平台T0代付手续费率
	 */
	private BigDecimal platformT0TixianRate;
	private Integer status;

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

	public BigDecimal getPlatformInputRate() {
		return platformInputRate;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public void setPlatformInputRate(BigDecimal platformInputRate) {
		this.platformInputRate = platformInputRate;
	}

	public BigDecimal getPlatformAuthenticationFee() {
		return platformAuthenticationFee;
	}

	public void setPlatformAuthenticationFee(BigDecimal platformAuthenticationFee) {
		this.platformAuthenticationFee = platformAuthenticationFee;
	}

	public BigDecimal getPlatformMessageFee() {
		return platformMessageFee;
	}

	public void setPlatformMessageFee(BigDecimal platformMessageFee) {
		this.platformMessageFee = platformMessageFee;
	}

	public BigDecimal getPlatformTixianFee() {
		return platformTixianFee;
	}

	public void setPlatformTixianFee(BigDecimal platformTixianFee) {
		this.platformTixianFee = platformTixianFee;
	}

	public BigDecimal getPlatformT0TixianRate() {
		return platformT0TixianRate;
	}

	public void setPlatformT0TixianRate(BigDecimal platformT0TixianRate) {
		this.platformT0TixianRate = platformT0TixianRate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}