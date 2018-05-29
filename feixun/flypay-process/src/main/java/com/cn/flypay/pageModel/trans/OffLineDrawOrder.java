package com.cn.flypay.pageModel.trans;

import java.math.BigDecimal;
import java.util.Date;

public class OffLineDrawOrder {

	private Long id;

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

	private Date finishTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getDrawSrc() {
		return drawSrc;
	}

	public void setDrawSrc(String drawSrc) {
		this.drawSrc = drawSrc;
	}

	public BigDecimal getBeforeAvlAmt() {
		return beforeAvlAmt;
	}

	public void setBeforeAvlAmt(BigDecimal beforeAvlAmt) {
		this.beforeAvlAmt = beforeAvlAmt;
	}

	public BigDecimal getAfterAvlAmt() {
		return afterAvlAmt;
	}

	public void setAfterAvlAmt(BigDecimal afterAvlAmt) {
		this.afterAvlAmt = afterAvlAmt;
	}

	public BigDecimal getPayAmt() {
		return payAmt;
	}

	public void setPayAmt(BigDecimal payAmt) {
		this.payAmt = payAmt;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getMerFlowNo() {
		return merFlowNo;
	}

	public void setMerFlowNo(String merFlowNo) {
		this.merFlowNo = merFlowNo;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getOpenBankName() {
		return openBankName;
	}

	public void setOpenBankName(String openBankName) {
		this.openBankName = openBankName;
	}

	public String getAccountBankNo() {
		return accountBankNo;
	}

	public void setAccountBankNo(String accountBankNo) {
		this.accountBankNo = accountBankNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getBunchTime() {
		return bunchTime;
	}

	public void setBunchTime(Date bunchTime) {
		this.bunchTime = bunchTime;
	}

	public Date getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(Date downloadTime) {
		this.downloadTime = downloadTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

}
