package com.cn.flypay.model.account;

import java.math.BigDecimal;
import java.util.Date;

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

@Entity
@Table(name = "org_account_log")
public class TorgAccountLog implements java.io.Serializable {

	private static final long serialVersionUID = 8331956701844197273L;
	private Long id;
	private Long version;
	private TorgAccount orgAccount;
	private Long orderId;
	private Integer type;
	private BigDecimal fee;
	private BigDecimal amt;
	private String content;
	private Date createDate;

	public TorgAccountLog() {
		super();
	}

	public TorgAccountLog(TorgAccount orgAccount, Long objectId, Integer type, BigDecimal fee, BigDecimal amt,
			String content) {
		this.orgAccount = orgAccount;
		this.orderId = objectId;
		this.type = type;
		this.fee = fee;
		this.amt = amt;
		this.content = content;
		this.createDate = new Date();
	}

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
	@JoinColumn(name = "org_acc_id")
	public TorgAccount getOrgAccount() {
		return orgAccount;
	}

	public void setOrgAccount(TorgAccount orgAccount) {
		this.orgAccount = orgAccount;
	}

	@Column(name = "order_id")
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "fee", precision = 8)
	public BigDecimal getFee() {
		return this.fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	@Column(name = "amt", precision = 8)
	public BigDecimal getAmt() {
		return this.amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	@Column(name = "createdate", length = 19)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Column(name = "content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}