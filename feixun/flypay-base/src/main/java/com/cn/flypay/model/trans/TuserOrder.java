package com.cn.flypay.model.trans;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserCard;

/**
 * TransOrder entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "trans_order")
public class TuserOrder implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 9016419875268155065L;
	private Long id;
	private Long version;
	private Tuser user;
	/* 10 普通支付订单 20 代理费用支付订单 */
	private Integer transPayType;
	/* 交易类型 */
	private Integer type;
	/* 用户输入的金额 */
	private BigDecimal orgAmt;
	/* 该笔订单的手续费 */
	private BigDecimal fee;
	
	private BigDecimal srvFee;
	/* 手续费中包含的提现手续费费率 */
	private BigDecimal extractFee;
	/* 实际支付金额 */
	private BigDecimal amt;
	/* 该笔订单结束后，账户中的可用金额 */
	private BigDecimal avlAmt;
	private Integer status;
	private Integer timeOut;
	private String orderNum;
	private String description;
	private TuserCard card;
	/**
	 * 0立即下单，1 隔日下单
	 */
	private Integer payType;
	private TranPayOrder tranPayOrder;

	private Date createTime;

	private String cdType;

	private Integer scanNum;

	/**
	 * 入账类型 T5 T1 D0
	 */
	private Integer inputAccType;
	/* 该笔订单结束后，账户中的可用金额 */
	private BigDecimal personRate;
	private BigDecimal shareRate;

	private Integer agentType;

	// Constructors

	/** default constructor */
	public TuserOrder() {
		this.scanNum = 0;
		this.createTime = new Date();
		this.status = 300;
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
	@Column(name = "VERSION", nullable = false)
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

	@Column(name = "TYPE", nullable = false)
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "SCAN_NUM")
	public Integer getScanNum() {
		return scanNum;
	}

	public void setScanNum(Integer scanNum) {
		this.scanNum = scanNum;
	}

	@Column(name = "ORG_AMT", precision = 12, scale = 2)
	public BigDecimal getOrgAmt() {
		return this.orgAmt;
	}

	public void setOrgAmt(BigDecimal orgAmt) {
		this.orgAmt = orgAmt;
	}

	@Column(name = "fee", precision = 12, scale = 2)
	public BigDecimal getFee() {
		return this.fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	
	
	@Column(name = "srv_fee", precision = 12, scale = 2)
	public BigDecimal getSrvFee() {
		return srvFee;
	}

	public void setSrvFee(BigDecimal srvFee) {
		this.srvFee = srvFee;
	}

	@Column(name = "extract_fee", precision = 8, scale = 6)
	public BigDecimal getExtractFee() {
		return extractFee;
	}

	public void setExtractFee(BigDecimal extractFee) {
		this.extractFee = extractFee;
	}

	/** 实际支付金额 */
	@Column(name = "amt", precision = 12, scale = 2)
	public BigDecimal getAmt() {
		return this.amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	/** 该笔订单结束后，账户中的可用金额 */
	@Column(name = "avl_amt", precision = 12, scale = 2)
	public BigDecimal getAvlAmt() {
		return this.avlAmt;
	}

	public void setAvlAmt(BigDecimal avlAmt) {
		this.avlAmt = avlAmt;
	}

	@Column(name = "STATUS", nullable = false)
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * 0立即下单，1 隔日下单
	 */
	@Column(name = "pay_type", nullable = false)
	public Integer getPayType() {
		return payType;
	}

	/**
	 * 0立即下单，1 隔日下单
	 * 
	 * @param payType
	 */
	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	@Column(name = "trans_pay_type", nullable = false)
	public Integer getTransPayType() {
		return transPayType;
	}

	public void setTransPayType(Integer transPayType) {
		this.transPayType = transPayType;
	}

	@Column(name = "TIME_OUT")
	public Integer getTimeOut() {
		return this.timeOut;
	}

	public void setTimeOut(Integer timeOut) {
		this.timeOut = timeOut;
	}

	@Column(name = "ORDER_NUM", nullable = false, length = 64)
	public String getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	@Column(name = "DESCRIPTION", length = 512)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "card_id")
	public TuserCard getCard() {
		return card;
	}

	public void setCard(TuserCard card) {
		this.card = card;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	public TranPayOrder getTranPayOrder() {
		return tranPayOrder;
	}

	@Column(name = "cd_type", nullable = false)
	public String getCdType() {
		return cdType;
	}

	public void setCdType(String cdType) {
		this.cdType = cdType;
	}

	public void setTranPayOrder(TranPayOrder tranPayOrder) {
		this.tranPayOrder = tranPayOrder;
	}

	@Column(name = "create_time", nullable = false, length = 19)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "input_Acc_Type")
	public Integer getInputAccType() {
		return inputAccType;
	}

	public void setInputAccType(Integer inputAccType) {
		this.inputAccType = inputAccType;
	}

	@Column(name = "person_rate", length = 8)
	public BigDecimal getPersonRate() {
		return personRate;
	}

	public void setPersonRate(BigDecimal personRate) {
		this.personRate = personRate;
	}

	@Column(name = "share_rate", length = 8)
	public BigDecimal getShareRate() {
		return shareRate;
	}

	public void setShareRate(BigDecimal shareRate) {
		this.shareRate = shareRate;
	}

	@Column(name = "agent_Type")
	public Integer getAgentType() {
		return agentType;
	}

	public void setAgentType(Integer agentType) {
		this.agentType = agentType;
	}

}