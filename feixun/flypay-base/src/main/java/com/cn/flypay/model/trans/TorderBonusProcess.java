package com.cn.flypay.model.trans;

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

@Entity
@Table(name = "trans_order_bonus_process")
public class TorderBonusProcess implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 4626895411261414956L;
	private Long id;
	private Integer version;
	private TuserOrder order;
	private String orderNum;
	private BigDecimal totalAmt;
	/**
	 * 0初始
	 * 100 success
	 * 200 failure
	 */
	private Integer status;

	public TorderBonusProcess() {

	}

	public TorderBonusProcess(TuserOrder order, BigDecimal totalAmt) {
		this.order = order;
		this.orderNum = order.getOrderNum();
		this.totalAmt = totalAmt;
		this.status = 0;
	}

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
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	public TuserOrder getOrder() {
		return order;
	}

	public void setOrder(TuserOrder order) {
		this.order = order;
	}

	@Column(name = "order_num", length = 64)
	public String getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	@Column(name = "total_amt", precision = 15, scale = 2)
	public BigDecimal getTotalAmt() {
		return this.totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}