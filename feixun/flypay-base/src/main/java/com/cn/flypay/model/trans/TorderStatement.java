package com.cn.flypay.model.trans;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.cn.flypay.model.sys.Tuser;

@Entity
@Table(name = "trans_order_statement")
public class TorderStatement implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 7725217360885028341L;
	private Long id;
	private Long orderId;
	private String orderNo;
	private Integer status;
	private String errorInfo;
	private String dealStatus;
	private String dealDesc;
	private String statementNo;
	private Date createTime;
	private Tuser user;
	private Integer statementType;
	private String statementChannel;
	private String statementDate;

	public TorderStatement() {
	}

	public TorderStatement(Tuser user, Integer statementType, String statementDate, Long orderId, String orderNo,
			String statementNo, Integer status, String errorInfo) {
		this.statementType = statementType;
		this.statementDate = statementDate;
		this.orderNo = orderNo;
		this.orderId = orderId;
		this.status = status;
		this.errorInfo = errorInfo;
		this.statementNo = statementNo;
		this.createTime = new Date();
		this.user = user;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "order_id")
	public Long getOrderId() {
		return this.orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	@Column(name = "statement_type")
	public Integer getStatementType() {
		return statementType;
	}

	public void setStatementType(Integer statementType) {
		this.statementType = statementType;
	}

	@Column(name = "statement_channel")
	public String getStatementChannel() {
		return statementChannel;
	}

	public void setStatementChannel(String statementChannel) {
		this.statementChannel = statementChannel;
	}

	@Column(name = "statement_date")
	public String getStatementDate() {
		return statementDate;
	}

	public void setStatementDate(String statementDate) {
		this.statementDate = statementDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	public Tuser getUser() {
		return this.user;
	}

	public void setUser(Tuser user) {
		this.user = user;
	}

	@Column(name = "order_no")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "error_info", length = 512)
	public String getErrorInfo() {
		return this.errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	@Column(name = "statement_no", length = 512)
	public String getStatementNo() {
		return statementNo;
	}

	public void setStatementNo(String statementNo) {
		this.statementNo = statementNo;
	}

	@Column(name = "deal_status", length = 32)
	public String getDealStatus() {
		return this.dealStatus;
	}

	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}

	@Column(name = "deal_desc", length = 512)
	public String getDealDesc() {
		return this.dealDesc;
	}

	public void setDealDesc(String dealDesc) {
		this.dealDesc = dealDesc;
	}

	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}