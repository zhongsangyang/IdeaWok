package com.cn.flypay.utils.gazhiyinlian.entities;
/**
 * 商户注册
 * 嘎吱（银联）基础类
 * @author liangchao
 */
public class RegisterForGaZhiYinLian {

	/**
	 * 交易码
	 */
	private String tranType;
	
	/**
	 *商户流水  
	 *商户流水号，商户须保证流水唯一
	 */
	private String merTrace;
	
	/**
	 * 商户名称
	 */
	private String merName;
	
	/**
	 * 商户简称
	 */
	private String merAbbr;
	/**
	 * 合作商户费率编号
	 */
	private String rateCode;
	/**
	 * 身份证号
	 */
	private String idCardNo;
	/**
	 * 银行卡卡号
	 * 结算卡
	 */
	private String bankAccNo;
	/**
	 * 银行卡预留手机
	 */
	private String phoneno;
	/**
	 * 银行卡户名
	 */
	private String bankAccName;
	/**
	 * 银行卡账户类型
	 * 2 对私
	 */
	private String bankAccType;
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
	 * 银行所属市
	 */
	private String bankCity;
	/**
	 * 借记卡费率
	 */
	private String debitRate;
	/**
	 * 借记卡封顶
	 */
	private String debitCapAmount;
	/**
	 * 信用卡费率
	 */
	private String creditRate;
	/**
	 * 信用卡封顶
	 */
	private String creditCapAmount;
	/**
	 * 提现费率
	 */
	private String withdRate;
	/**
	 * 单笔提现手续费
	 */
	private String withdSgFee;
	public String getTranType() {
		return tranType;
	}
	public void setTranType(String tranType) {
		this.tranType = tranType;
	}
	public String getMerTrace() {
		return merTrace;
	}
	public void setMerTrace(String merTrace) {
		this.merTrace = merTrace;
	}
	public String getMerName() {
		return merName;
	}
	public void setMerName(String merName) {
		this.merName = merName;
	}
	public String getMerAbbr() {
		return merAbbr;
	}
	public void setMerAbbr(String merAbbr) {
		this.merAbbr = merAbbr;
	}
	public String getRateCode() {
		return rateCode;
	}
	public void setRateCode(String rateCode) {
		this.rateCode = rateCode;
	}
	public String getIdCardNo() {
		return idCardNo;
	}
	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
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
	public String getBankAccName() {
		return bankAccName;
	}
	public void setBankAccName(String bankAccName) {
		this.bankAccName = bankAccName;
	}
	public String getBankAccType() {
		return bankAccType;
	}
	public void setBankAccType(String bankAccType) {
		this.bankAccType = bankAccType;
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
