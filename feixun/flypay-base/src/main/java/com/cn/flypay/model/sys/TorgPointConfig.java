package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 * 通道 机构 通过降费率决定的配置
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "org_point_config")
public class TorgPointConfig implements java.io.Serializable {

	private static final long serialVersionUID = -4548659476180500235L;
	private Long id;
	private Long version;
	private Torganization organization;
	private Integer payType;
	private BigDecimal topRate;
	private BigDecimal midRate;
	private BigDecimal lowRate;
	private Integer toMidNum;
	private Integer toLowNum;
	private Integer status;
	private Integer type;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
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

	@Column(name = "pay_type")
	public Integer getPayType() {
		return this.payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "top_rate", precision = 8, scale = 6)
	public BigDecimal getTopRate() {
		return this.topRate;
	}

	public void setTopRate(BigDecimal topRate) {
		this.topRate = topRate;
	}

	@Column(name = "mid_rate", precision = 8, scale = 6)
	public BigDecimal getMidRate() {
		return this.midRate;
	}

	public void setMidRate(BigDecimal midRate) {
		this.midRate = midRate;
	}

	@Column(name = "low_rate", precision = 8, scale = 6)
	public BigDecimal getLowRate() {
		return this.lowRate;
	}

	public void setLowRate(BigDecimal lowRate) {
		this.lowRate = lowRate;
	}

	@Column(name = "to_mid_num")
	public Integer getToMidNum() {
		return this.toMidNum;
	}

	public void setToMidNum(Integer toMidNum) {
		this.toMidNum = toMidNum;
	}

	@Column(name = "to_low_num")
	public Integer getToLowNum() {
		return this.toLowNum;
	}

	public void setToLowNum(Integer toLowNum) {
		this.toLowNum = toLowNum;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	public Torganization getOrganization() {
		return organization;
	}

	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}

}