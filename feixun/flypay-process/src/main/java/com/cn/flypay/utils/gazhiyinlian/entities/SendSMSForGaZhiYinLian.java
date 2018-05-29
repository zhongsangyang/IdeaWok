package com.cn.flypay.utils.gazhiyinlian.entities;
/**
 * 支付短信
 * 嘎吱（银联）基础类
 * @author liangchao
 */
public class SendSMSForGaZhiYinLian {
	/**
	 * 交易码
	 * PAYMSG
	 */
	private String tranType;
	/**
	 * 合作商户编号
	 * 合作商户的唯一标识
	 */
	private String merNo;
	/**
	 * 商户流水
	 * 商户流水号，商户须保证流水唯一
	 */
	private String merTrace;
	/**
	 * 支付订单号
	 * 商户系统保证唯一
	 */
	private String orderId;
	/**
	 * 订单金额
	 * 订单金额以分为单位
	 */
	private String orderAmount;
	/**
	 * 费率编号
	 */
	private String rateCode;
	/**
	 * 银行卡卡号
	 */
	private String cardNo;
	/**
	 * 银行卡姓名
	 */
	private String accountName;
	/**
	 * 1-借记卡2-信用卡
	 */
	private String cardType;
	/**
	 * 银行代码
	 */
	private String bankCode;
	/**
	 * 银行代号
	 */
	private String bankAbbr;
	/**
	 * 银行预留手机号
	 */
	private String phoneno;
	/**
	 * 证件类型
	 */
	private String certType;
	/**
	 * 银行预留证件号
	 */
	private String certNo;
	public String getTranType() {
		return tranType;
	}
	public void setTranType(String tranType) {
		this.tranType = tranType;
	}
	public String getMerNo() {
		return merNo;
	}
	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}
	public String getMerTrace() {
		return merTrace;
	}
	public void setMerTrace(String merTrace) {
		this.merTrace = merTrace;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}
	public String getRateCode() {
		return rateCode;
	}
	public void setRateCode(String rateCode) {
		this.rateCode = rateCode;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getBankAbbr() {
		return bankAbbr;
	}
	public void setBankAbbr(String bankAbbr) {
		this.bankAbbr = bankAbbr;
	}
	public String getPhoneno() {
		return phoneno;
	}
	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}
	public String getCertType() {
		return certType;
	}
	public void setCertType(String certType) {
		this.certType = certType;
	}
	public String getCertNo() {
		return certNo;
	}
	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}
	
}
