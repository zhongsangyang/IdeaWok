package com.cn.flypay.model.account;

import static javax.persistence.GenerationType.IDENTITY;

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

@Entity
@Table(name = "account_log")
public class TaccountLog implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = -5315952425307581103L;
	private Long id;
	private Long version;
	private Taccount account;
	private String type;
	private String ordernum;
	private BigDecimal amt;
	private BigDecimal avlAmt;
	private BigDecimal lockOutAmt;
	private Date createTime;
	private String description;

	public TaccountLog() {

	}

	public TaccountLog(Taccount account, String type, BigDecimal amt, String description) {
		this.account = account;
		this.type = type;
		this.amt = amt;
		this.createTime = new Date();
		this.description = description;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
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
	@JoinColumn(name = "account_id")
	public Taccount getAccount() {
		return account;
	}

	public void setAccount(Taccount account) {
		this.account = account;
	}

	@Column(name = "type")
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "order_num")
	public String getOrdernum() {
		return ordernum;
	}

	public void setOrdernum(String ordernum) {
		this.ordernum = ordernum;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "amt", precision = 12, scale = 2)
	public BigDecimal getAmt() {
		return this.amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	@Column(name = "avlAmt", precision = 12, scale = 2)
	public BigDecimal getAvlAmt() {
		return avlAmt;
	}

	public void setAvlAmt(BigDecimal avlAmt) {
		this.avlAmt = avlAmt;
	}

	@Column(name = "lockOutAmt", precision = 12, scale = 2)
	public BigDecimal getLockOutAmt() {
		return lockOutAmt;
	}

	public void setLockOutAmt(BigDecimal lockOutAmt) {
		this.lockOutAmt = lockOutAmt;
	}

	@Column(name = "create_time", length = 10)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}