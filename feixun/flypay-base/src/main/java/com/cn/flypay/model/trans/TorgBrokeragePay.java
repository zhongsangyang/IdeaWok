package com.cn.flypay.model.trans;

// default package

import java.math.BigDecimal;
import java.util.Date;

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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cn.flypay.model.sys.Tuser;

/**
 * OrgBrokeragePay entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "TRANS_ORG_BROKERAGE_PAY")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TorgBrokeragePay implements java.io.Serializable {

	private static final long serialVersionUID = 7420213561398932319L;
	private Long id;
	private Long version;
	private TorgBrokerage orgBrokerage;
	private BigDecimal amt;
	private BigDecimal oldAmt;
	private Integer status;
	private Date payDatetime;
	private Tuser user;// 操作者

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

	@Column(name = "AMT", nullable = false, precision = 15, scale = 2)
	public BigDecimal getAmt() {
		return this.amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	@Column(name = "OLD_AMT", nullable = false, precision = 15, scale = 2)
	public BigDecimal getOldAmt() {
		return oldAmt;
	}

	public void setOldAmt(BigDecimal oldAmt) {
		this.oldAmt = oldAmt;
	}

	@Column(name = "STATUS", nullable = false, precision = 1, scale = 0)
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "PAY_DATETIME", nullable = false)
	public Date getPayDatetime() {
		return this.payDatetime;
	}

	public void setPayDatetime(Date payDatetime) {
		this.payDatetime = payDatetime;
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

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "org_brokerage_id")
	public TorgBrokerage getOrgBrokerage() {
		return orgBrokerage;
	}

	public void setOrgBrokerage(TorgBrokerage orgBrokerage) {
		this.orgBrokerage = orgBrokerage;
	}
}