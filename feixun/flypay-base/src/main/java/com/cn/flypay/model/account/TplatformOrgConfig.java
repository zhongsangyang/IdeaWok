package com.cn.flypay.model.account;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.cn.flypay.model.sys.Torganization;

/**
 * 平台_运营商_配置
 * 
 * @author sunyue
 * 
 */
@Entity
@Table(name = "platform_org_config")
public class TplatformOrgConfig implements java.io.Serializable {

	private static final long serialVersionUID = 165541L;
	private Long id;
	private Long version;
	private Torganization organization;// 所在机构
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

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "version")
	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "org_id")
	public Torganization getOrganization() {
		return organization;
	}

	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}

	@Column(name = "platform_Input_Rate", precision = 8, scale = 6)
	public BigDecimal getPlatformInputRate() {
		return this.platformInputRate;
	}

	public void setPlatformInputRate(BigDecimal platformInputRate) {
		this.platformInputRate = platformInputRate;
	}

	@Column(name = "platform_Authentication_Fee", precision = 8)
	public BigDecimal getPlatformAuthenticationFee() {
		return this.platformAuthenticationFee;
	}

	public void setPlatformAuthenticationFee(BigDecimal platformAuthenticationFee) {
		this.platformAuthenticationFee = platformAuthenticationFee;
	}

	@Column(name = "platform_Message_Fee", precision = 8)
	public BigDecimal getPlatformMessageFee() {
		return this.platformMessageFee;
	}

	public void setPlatformMessageFee(BigDecimal platformMessageFee) {
		this.platformMessageFee = platformMessageFee;
	}

	@Column(name = "platform_Tixian_Fee", precision = 8)
	public BigDecimal getPlatformTixianFee() {
		return this.platformTixianFee;
	}

	public void setPlatformTixianFee(BigDecimal platformTixianFee) {
		this.platformTixianFee = platformTixianFee;
	}

	@Column(name = "platform_T0_Tixian_rate", precision = 8, scale = 6)
	public BigDecimal getPlatformT0TixianRate() {
		return this.platformT0TixianRate;
	}

	public void setPlatformT0TixianRate(BigDecimal platformT0TixianRate) {
		this.platformT0TixianRate = platformT0TixianRate;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}