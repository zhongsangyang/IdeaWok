package com.cn.flypay.utils.gazhiyinlian.entities;
/**
 * 银联侧绑卡开通
 * 嘎吱（银联）基础类
 * @author liangchao
 */
public class BindCardForGaZhiYinLian {

	/**
	 * 交易码
	 * POPNCD
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
	 * 商户订单号
	 * 商户系统保证唯一
	 */
	private String orderId;
	/**
	 * 合作商户费率编号
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
	 * 2-对私
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
	 * 卡背面Cvn2数字
	 * 信用卡上送
	 */
	private String cvn2;
	/**
	 * 卡有效期
	 * 年在前月在后，信用卡上送
	 */
	private String expired;
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
	 * 页面返回url
	 * 开通结果通过页面跳转通知这个url，一般用于展示
	 */
	private String pageReturnUrl;
	/**
	 * 页面返回url
	 * 开通结果通过页面跳转通知这个url，一般用于展示
	 */
	private String offlineNotifyUrl;
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
	public String getCvn2() {
		return cvn2;
	}
	public void setCvn2(String cvn2) {
		this.cvn2 = cvn2;
	}
	public String getExpired() {
		return expired;
	}
	public void setExpired(String expired) {
		this.expired = expired;
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
	public String getPageReturnUrl() {
		return pageReturnUrl;
	}
	public void setPageReturnUrl(String pageReturnUrl) {
		this.pageReturnUrl = pageReturnUrl;
	}
	public String getOfflineNotifyUrl() {
		return offlineNotifyUrl;
	}
	public void setOfflineNotifyUrl(String offlineNotifyUrl) {
		this.offlineNotifyUrl = offlineNotifyUrl;
	}
	
}
