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
 * 运营商平台账户
 * @author sunyue
 *
 */
@Entity
@Table(name = "org_account")
public class TorgAccount implements java.io.Serializable {

	private static final long serialVersionUID = -6943694210371989836L;
	private Long id;
	private Long version;
	private Torganization organization;// 所在机构
	private Integer type;
	private BigDecimal amt;
	private BigDecimal lockAmt;
	private BigDecimal limitAmt;
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

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "amt", precision = 15)
	public BigDecimal getAmt() {
		return this.amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	@Column(name = "lock_amt", precision = 15)
	public BigDecimal getLockAmt() {
		return this.lockAmt;
	}

	public void setLockAmt(BigDecimal lockAmt) {
		this.lockAmt = lockAmt;
	}

	@Column(name = "limit_amt", precision = 8)
	public BigDecimal getLimitAmt() {
		return this.limitAmt;
	}

	public void setLimitAmt(BigDecimal limitAmt) {
		this.limitAmt = limitAmt;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}