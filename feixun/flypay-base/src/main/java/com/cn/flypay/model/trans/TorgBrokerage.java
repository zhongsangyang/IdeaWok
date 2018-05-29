package com.cn.flypay.model.trans;

// default package

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

import org.hibernate.annotations.GenericGenerator;

import com.cn.flypay.model.sys.Torganization;

/**
 * OrgBrokerage entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "TRANS_ORG_BROKERAGE")
public class TorgBrokerage implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -337495390907124054L;
	private Long id;
	private Long version;
	private BigDecimal brokerage;
	private Integer status;

	private Torganization organization;
	private BigDecimal totalBrokerage;
	private BigDecimal totalTransBrokerage;
	private BigDecimal totalAgentBrokerage;

	@Id
	@GenericGenerator(name = "generator", strategy = "increment")
	@GeneratedValue(generator = "generator")
	@Column(name = "ID", unique = true, nullable = false, precision = 15, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false, precision = 10, scale = 0)
	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Column(name = "BROKERAGE", nullable = false, precision = 9, scale = 3)
	public BigDecimal getBrokerage() {
		return this.brokerage;
	}

	public void setBrokerage(BigDecimal brokerage) {
		this.brokerage = brokerage;
	}

	@Column(name = "STATUS", nullable = false, precision = 1, scale = 0)
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "total_brokerage", nullable = false, precision = 9, scale = 3)
	public BigDecimal getTotalBrokerage() {
		return totalBrokerage;
	}

	public void setTotalBrokerage(BigDecimal totalBrokerage) {
		this.totalBrokerage = totalBrokerage;
	}

	@Column(name = "TOTAL_TRANS_BROKERAGE", nullable = false, precision = 15, scale = 3)
	public BigDecimal getTotalTransBrokerage() {
		return totalTransBrokerage;
	}

	public void setTotalTransBrokerage(BigDecimal totalTransBrokerage) {
		this.totalTransBrokerage = totalTransBrokerage;
	}

	@Column(name = "TOTAL_AGENT_BROKERAGE", nullable = false, precision = 15, scale = 3)
	public BigDecimal getTotalAgentBrokerage() {
		return totalAgentBrokerage;
	}

	public void setTotalAgentBrokerage(BigDecimal totalAgentBrokerage) {
		this.totalAgentBrokerage = totalAgentBrokerage;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORG_ID")
	public Torganization getOrganization() {
		return organization;
	}

	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}

}