package com.cn.flypay.model.sys;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 通道限额配置
 * 
 * @author liangchao
 *
 */
@Entity
@Table(name = "sys_pay_type_limit_config")
public class TpayTypeLimitConfig implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 4757084442985083987L;
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
	private String name;
	private String code;
	private String payTypeName;
	private String startTime;
	private String endTime;
	private String unSupportCardName;
	private Torganization organization;

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "version")
	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Column(name = "pay_type")
	public Integer getPayType() {
		return this.payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	@Column(name = "amt_type")
	public Integer getAmtType() {
		return this.amtType;
	}

	public void setAmtType(Integer amtType) {
		this.amtType = amtType;
	}

	@Column(name = "max_amt", precision = 15)
	public BigDecimal getMaxAmt() {
		return this.maxAmt;
	}

	public void setMaxAmt(BigDecimal maxAmt) {
		this.maxAmt = maxAmt;
	}

	@Column(name = "min_amt", precision = 15)
	public BigDecimal getMinAmt() {
		return this.minAmt;
	}

	public void setMinAmt(BigDecimal minAmt) {
		this.minAmt = minAmt;
	}

	@Column(name = "srvFee", precision = 15)
	public BigDecimal getSrvFee() {
		return srvFee;
	}

	public void setSrvFee(BigDecimal srvFee) {
		this.srvFee = srvFee;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "hot_type")
	public String getHotType() {
		return hotType;
	}

	public void setHotType(String hotType) {
		this.hotType = hotType;
	}

	@Column(name = "slt_type")
	public String getSltType() {
		return sltType;
	}

	public void setSltType(String sltType) {
		this.sltType = sltType;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	public Torganization getOrganization() {
		return organization;
	}

	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "payTypeName")
	public String getPayTypeName() {
		return payTypeName;
	}

	public void setPayTypeName(String payTypeName) {
		this.payTypeName = payTypeName;
	}

	@Column(name = "startTime")
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Column(name = "endTime")
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Column(name = "unSupportCardName")
	public String getUnSupportCardName() {
		return unSupportCardName;
	}

	public void setUnSupportCardName(String unSupportCardName) {
		this.unSupportCardName = unSupportCardName;
	}

}