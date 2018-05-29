package com.cn.flypay.model.trans;

import static javax.persistence.GenerationType.IDENTITY;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * 线下取现订单
 */
@Entity
@Table(name = "trans_offline_draw_order")
public class TOffLineDrawOrder implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9196627837678122857L;

	public static String STATUS_INIT = "0";// 初始
	public static String STATUS_SUCCESS = "1"; // 确定完成
	public static String STATUS_BUNCH = "2"; // 打批
	public static String STATUS_DOWNLOAD = "3"; // 已被下载
	public static String STATUS_FAIL = "8"; // 出款失败
	public static String STATUS_FREEZE = "9"; // 已被冻结

	private Long id;
	private Long version;
	private Long userId;

	// 提取源 account | brokerage
	private String drawSrc;

	private BigDecimal beforeAvlAmt;

	private BigDecimal afterAvlAmt;

	private BigDecimal payAmt;

	private String orderNo;
	/**
	 * 收款方手机号
	 */
	private String loginName;

	/**
	 * 商户流水号
	 */
	private String merFlowNo;
	/**
	 * 收款方姓名
	 */
	private String receiverName;

	/**
	 * 开户银行名称
	 */
	private String openBankName;

	/**
	 * 银行账户号
	 */
	private String accountBankNo;

	/**
	 * 订单状态
	 */
	private String status;

	private Date createTime;
	private Date bunchTime;
	private Date downloadTime;
	private Date finihsTime;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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

	@Column(name = "userId")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "drawSrc")
	public String getDrawSrc() {
		return drawSrc;
	}

	public void setDrawSrc(String drawSrc) {
		this.drawSrc = drawSrc;
	}

	@Column(name = "beforeAvlAmt")
	public BigDecimal getBeforeAvlAmt() {
		return beforeAvlAmt;
	}

	public void setBeforeAvlAmt(BigDecimal beforeAvlAmt) {
		this.beforeAvlAmt = beforeAvlAmt;
	}

	@Column(name = "afterAvlAmt")

	public BigDecimal getAfterAvlAmt() {
		return afterAvlAmt;
	}

	public void setAfterAvlAmt(BigDecimal afterAvlAmt) {
		this.afterAvlAmt = afterAvlAmt;
	}

	@Column(name = "payAmt")
	public BigDecimal getPayAmt() {
		return payAmt;
	}

	public void setPayAmt(BigDecimal payAmt) {
		this.payAmt = payAmt;
	}

	@Column(name = "orderNo")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Column(name = "loginName")
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Column(name = "merFlowNo")
	public String getMerFlowNo() {
		return merFlowNo;
	}

	public void setMerFlowNo(String merFlowNo) {
		this.merFlowNo = merFlowNo;
	}

	@Column(name = "receiverName")
	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	@Column(name = "openBankName")
	public String getOpenBankName() {
		return openBankName;
	}

	public void setOpenBankName(String openBankName) {
		this.openBankName = openBankName;
	}

	@Column(name = "accountBankNo")
	public String getAccountBankNo() {
		return accountBankNo;
	}

	public void setAccountBankNo(String accountBankNo) {
		this.accountBankNo = accountBankNo;
	}

	@Column(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "createTime")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "bunchTime")
	public Date getBunchTime() {
		return bunchTime;
	}

	public void setBunchTime(Date bunchTime) {
		this.bunchTime = bunchTime;
	}

	@Column(name = "downloadTime")
	public Date getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(Date downloadTime) {
		this.downloadTime = downloadTime;
	}

	@Column(name = "finihsTime")
	public Date getFinihsTime() {
		return finihsTime;
	}

	public void setFinihsTime(Date finihsTime) {
		this.finihsTime = finihsTime;
	}

}
