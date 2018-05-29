package com.cn.flypay.model.trans;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
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
@Table(name = "trans_brokerage_log")
public class TbrokerageLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2515290198610485435L;

	private Long id;
	private Long version;
	private Tbrokerage brokerage;//佣金账户
	private String type;//佣金变动类型 D 获取分拥 R 待提 T 提现完成 F 提现失败
	private BigDecimal amt;//变动金额
	private Date createTime;
	private String description;
	private BigDecimal avlAmt;//佣金余额
	private String ordernum;//相关订单号
	private BigDecimal lockOutAmt;//锁定佣金
	
	public TbrokerageLog() {
		
	}
	
	public TbrokerageLog(Tbrokerage brokerage, String type, BigDecimal amt, String description) {
		this.brokerage = brokerage;
		this.type = type;
		this.amt = amt;
		this.createTime = new Date();
		this.description = description;
	}


	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Version
	@Column(name = "version")
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "brokerage_id")
	public Tbrokerage getBrokerage() {
		return brokerage;
	}
	public void setBrokerage(Tbrokerage brokerage) {
		this.brokerage = brokerage;
	}
	
	@Column(name = "type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "amt",precision = 12,scale = 2)
	public BigDecimal getAmt() {
		return amt;
	}
	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}
	
	@Column(name = "create_time",length = 10)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "avlAmt",precision = 12,scale = 2)
	public BigDecimal getAvlAmt() {
		return avlAmt;
	}
	public void setAvlAmt(BigDecimal avlAmt) {
		this.avlAmt = avlAmt;
	}

	@Column(name = "lockOutAmt",precision = 12,scale = 2)
	public BigDecimal getLockOutAmt() {
		return lockOutAmt;
	}
	public void setLockOutAmt(BigDecimal lockOutAmt) {
		this.lockOutAmt = lockOutAmt;
	}

	@Column(name = "order_num")
	public String getOrdernum() {
		return ordernum;
	}
	public void setOrdernum(String ordernum) {
		this.ordernum = ordernum;
	}
	
	
}
