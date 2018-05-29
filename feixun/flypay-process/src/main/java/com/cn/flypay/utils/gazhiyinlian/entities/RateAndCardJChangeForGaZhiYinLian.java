package com.cn.flypay.utils.gazhiyinlian.entities;
/**
 * 商户费率、结算银行卡变更
 * 嘎吱（银联）基础类
 * @author liangchao
 */
public class RateAndCardJChangeForGaZhiYinLian {
	
	/**
	 * 交易码
	 */
	private String tranType;
	/**
	 * 合作商户编号
	 */
	private String merNo;
	/**
	 * 商户流水
	 */
	private String merTrace;
	/**
	 * 变更类型
	 * 1 交易费率变更 2 银行卡信息变更 3 交易费率新增 4 提现费率变更
	 */
	private String changeType;
	/**
	 * 结算卡
	 * 银行卡卡号
	 */
	private String bankAccNo;
	/**
	 * 银行卡预留手机
	 * 支付用户在系统中的卡号ID
	 */
	private String phoneno;
	/**
	 * 银行名称
	 */
	private String bankName;
	/**
	 * 银行支行名称
	 */
	private String bankSubName;
	/**
	 * 银行代码
	 */
	private String bankCode;
	/**
	 * 银行代号
	 */
	private String bankAbbr;
	/**
	 * 银行联行号
	 */
	private String bankChannelNo;
	/**
	 * 银行所属省
	 */
	private String bankProvince;
	/**
	 * 01、身份证
	 */
	private String bankCity;
	/**
	 * 合作商户费率编号
	 * (1、3必填)
	 */
	private String rateCode;
	/**
	 * 借记卡费率 
	 * (1、3必填)
	 */
	private String debitRate;
	/**
	 * 借记卡封顶
	 * (1、3必填)
	 */
	private String debitCapAmount;
	/**
	 * 信用卡费率
	 * (1、3必填)
	 */
	private String creditRate;
	/**
	 * 信用卡封顶
	 * (1、3必填)
	 */
	private String creditCapAmount;
	/**
	 * 提现费率
	 * (4必填)
	 */
	private String withdRate;
	/**
	 * 单笔提现手续费
	 * (4必填)
	 */
	private String withdSgFee;
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
	public String getChangeType() {
		return changeType;
	}
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
	public String getBankAccNo() {
		return bankAccNo;
	}
	public void setBankAccNo(String bankAccNo) {
		this.bankAccNo = bankAccNo;
	}
	public String getPhoneno() {
		return phoneno;
	}
	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankSubName() {
		return bankSubName;
	}
	public void setBankSubName(String bankSubName) {
		this.bankSubName = bankSubName;
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
	public String getBankChannelNo() {
		return bankChannelNo;
	}
	public void setBankChannelNo(String bankChannelNo) {
		this.bankChannelNo = bankChannelNo;
	}
	public String getBankProvince() {
		return bankProvince;
	}
	public void setBankProvince(String bankProvince) {
		this.bankProvince = bankProvince;
	}
	public String getBankCity() {
		return bankCity;
	}
	public void setBankCity(String bankCity) {
		this.bankCity = bankCity;
	}
	public String getRateCode() {
		return rateCode;
	}
	public void setRateCode(String rateCode) {
		this.rateCode = rateCode;
	}
	public String getDebitRate() {
		return debitRate;
	}
	public void setDebitRate(String debitRate) {
		this.debitRate = debitRate;
	}
	public String getDebitCapAmount() {
		return debitCapAmount;
	}
	public void setDebitCapAmount(String debitCapAmount) {
		this.debitCapAmount = debitCapAmount;
	}
	public String getCreditRate() {
		return creditRate;
	}
	public void setCreditRate(String creditRate) {
		this.creditRate = creditRate;
	}
	public String getCreditCapAmount() {
		return creditCapAmount;
	}
	public void setCreditCapAmount(String creditCapAmount) {
		this.creditCapAmount = creditCapAmount;
	}
	public String getWithdRate() {
		return withdRate;
	}
	public void setWithdRate(String withdRate) {
		this.withdRate = withdRate;
	}
	public String getWithdSgFee() {
		return withdSgFee;
	}
	public void setWithdSgFee(String withdSgFee) {
		this.withdSgFee = withdSgFee;
	}
	
	
}
