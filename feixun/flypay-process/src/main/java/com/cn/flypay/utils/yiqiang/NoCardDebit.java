package com.cn.flypay.utils.yiqiang;

/**
 * 消费
 * 
 * @author yq@2L01
 *
 */
public class NoCardDebit {

	private String tranType;
	/**
	 * 交易日期
	 */
	private String tranDate;

	/**
	 * 交易时间
	 */
	private String tranTime;

	private String merTrace;

	private String merNo;

	private String orderNo;

	private String currencyCode;

	private String orderAmount;

	private String name;

	private String idNumber;

	private String accNo;

	private String telNo;

	private String productType;

	private String paymentType;

	private String merGroup;

	private String frontUrl;

	private String backUrl;

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public String getTranDate() {
		return tranDate;
	}

	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
	}

	public String getTranTime() {
		return tranTime;
	}

	public void setTranTime(String tranTime) {
		this.tranTime = tranTime;
	}

	public String getMerTrace() {
		return merTrace;
	}

	public void setMerTrace(String merTrace) {
		this.merTrace = merTrace;
	}

	public String getMerNo() {
		return merNo;
	}

	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getTelNo() {
		return telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getMerGroup() {
		return merGroup;
	}

	public void setMerGroup(String merGroup) {
		this.merGroup = merGroup;
	}

	public String getFrontUrl() {
		return frontUrl;
	}

	public void setFrontUrl(String frontUrl) {
		this.frontUrl = frontUrl;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	@Override
	public String toString() {
		return "NoCardDebit [tranType=" + tranType + ", tranDate=" + tranDate + ", tranTime=" + tranTime + ", merTrace=" + merTrace + ", merNo=" + merNo + ", orderNo=" + orderNo + ", currencyCode=" + currencyCode + ", orderAmount="
				+ orderAmount + ", name=" + name + ", idNumber=" + idNumber + ", accNo=" + accNo + ", telNo=" + telNo + ", productType=" + productType + ", paymentType=" + paymentType + ", merGroup=" + merGroup + ", frontUrl=" + frontUrl
				+ ", backUrl=" + backUrl + "]";
	}

}
