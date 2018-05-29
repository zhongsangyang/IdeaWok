package com.cn.flypay.model.trans;

// default package

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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cn.flypay.model.sys.Tuser;

/**
 * Brokerage entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "TRANS_BROKERAGE")
@DynamicInsert(true)
@DynamicUpdate(true)
public class Tbrokerage implements java.io.Serializable {

	private static final long serialVersionUID = -3962349440140706800L;
	private Long id;
	private Long version;
	private Tuser user;
	private BigDecimal brokerage;// 佣金
	private BigDecimal totalBrokerage;
	private BigDecimal totalTransBrokerage;
	private BigDecimal totalAgentBrokerage;
	private BigDecimal totalLeadBrokerage;
	private BigDecimal lockBrokerage;
	/**
	 * 历史佣金
	 */
	private BigDecimal historyBrokerage;
	/**
	 * 昨日佣金
	 */
	private BigDecimal yesterdayBrokerage;
	private Integer status;// 状态

	// Property accessors
	@Id
	@GenericGenerator(name = "generator", strategy = "increment")
	@GeneratedValue(strategy = GenerationType.AUTO)
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

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	public Tuser getUser() {
		return this.user;
	}

	public void setUser(Tuser user) {
		this.user = user;
	}

	@Column(name = "BROKERAGE", nullable = false, precision = 9, scale = 2)
	public BigDecimal getBrokerage() {
		return this.brokerage;
	}

	public void setBrokerage(BigDecimal brokerage) {
		this.brokerage = brokerage;
	}

	@Column(name = "LOCK_BROKERAGE", nullable = false, precision = 9, scale = 2)
	public BigDecimal getLockBrokerage() {
		return lockBrokerage;
	}
	@Column(name = "history_Brokerage", nullable = false, precision = 9, scale = 2)
	public BigDecimal getHistoryBrokerage() {
		return historyBrokerage;
	}
	public void setHistoryBrokerage(BigDecimal historyBrokerage) {
		this.historyBrokerage = historyBrokerage;
	}

	@Column(name = "yesterday_Brokerage", nullable = false, precision = 9, scale = 2)
	public BigDecimal getYesterdayBrokerage() {
		return yesterdayBrokerage;
	}

	public void setYesterdayBrokerage(BigDecimal yesterdayBrokerage) {
		this.yesterdayBrokerage = yesterdayBrokerage;
	}

	public void setLockBrokerage(BigDecimal lockBrokerage) {
		this.lockBrokerage = lockBrokerage;
	}

	@Column(name = "STATUS", nullable = false, precision = 1, scale = 0)
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "total_brokerage", nullable = false, precision = 9, scale = 2)
	public BigDecimal getTotalBrokerage() {
		return totalBrokerage;
	}

	public void setTotalBrokerage(BigDecimal totalBrokerage) {
		this.totalBrokerage = totalBrokerage;
	}

	@Column(name = "TOTAL_TRANS_BROKERAGE", nullable = false, precision = 15, scale = 2)
	public BigDecimal getTotalTransBrokerage() {
		return totalTransBrokerage;
	}

	public void setTotalTransBrokerage(BigDecimal totalTransBrokerage) {
		this.totalTransBrokerage = totalTransBrokerage;
	}

	@Column(name = "TOTAL_AGENT_BROKERAGE", nullable = false, precision = 15, scale = 2)
	public BigDecimal getTotalAgentBrokerage() {
		return totalAgentBrokerage;
	}

	public void setTotalAgentBrokerage(BigDecimal totalAgentBrokerage) {
		this.totalAgentBrokerage = totalAgentBrokerage;
	}
    
	@Column(name = "TOTAL_LEAD_BROKERAGE", nullable = false, precision = 15, scale = 2)
	public BigDecimal getTotalLeadBrokerage() {
		return totalLeadBrokerage;
	}

	public void setTotalLeadBrokerage(BigDecimal totalLeadBrokerage) {
		this.totalLeadBrokerage = totalLeadBrokerage;
	}
	
	
	

}