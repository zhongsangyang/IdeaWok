package com.cn.flypay.utils.gazhiyinlian.entities;
/**
 * 消费支付
 * 嘎吱（银联）基础类
 * @author liangchao
 */
public class OrderPayForGaZhiYinLian {
	/**
	 * 交易码
	 * CONPAY
	 */
	private String tranType;
	/**
	 * 合作商户编号
	 */
	private String merNo;
	/**
	 * 商户流水
	 * 商户流水号，商户须保证流水唯一
	 */
	private String merTrace;
	/**
	 * 支付订单号
	 */
	private String orderId;
	/**
	 * 支付流水号
	 */
	private String payNo;
	/**
	 * 支付金额
	 */
	private String payAmount;
	/**
	 * 作商户费率编号
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
	 * 银行卡类型
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
	 * 01、身份证
	 */
	private String certType;
	/**
	 * 银行预留证件号
	 */
	private String certNo;
	/**
	 * 短信验证码
	 * 6位短信验证码
	 */
	private String smsCode;
	/**
	 * 商品名称
	 */
	private String productName;
	/**
	 * 商品描述
	 */
	private String productDesc;
	/**
	 * 后台通知url
	 */
	private String notifyUrl;
	
	
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
	public String getPayNo() {
		return payNo;
	}
	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}
	public String getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
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
	public String getSmsCode() {
		return smsCode;
	}
	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	
}
