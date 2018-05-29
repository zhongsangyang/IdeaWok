package com.cn.flypay.utils.yiqiang;

/**
 * 商户进件
 * 
 * @author yq@2L01
 *
 */
public class ApplyMerInfo {

	/**
	 * 交易码
	 */
	private String tranType;

	/**
	 * 交易日期
	 */
	private String tranDate;

	/**
	 * 交易时间
	 */
	private String tranTime;

	/**
	 * 商户流水
	 */
	private String merTrace;

	/**
	 * 前端商户号
	 */
	private String frontMid;

	/**
	 * 商户名称
	 */
	private String merName;

	/**
	 * 姓名
	 */
	private String realName;

	/**
	 * 商户所在省份
	 */
	private String merState;
	/**
	 * 商户所在城市
	 */
	private String merCity;

	/**
	 * 商户所在详细地址(除省份和城市之外的地址内容)
	 */
	private String merAddress;

	/**
	 * 证件类型
	 */
	private String certType;
	/**
	 * 证件号
	 */
	private String certId;

	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 结算账号
	 */
	private String accountId;
	/**
	 * 结算户名
	 */
	private String accountName;
	/**
	 * 总行名称
	 */
	private String bankName;
	/**
	 * 总行联行号
	 */
	private String bankCode;

	/**
	 * 开户行全称
	 */
	private String openBName;

	/**
	 * 开户行联行号
	 */
	private String openBCode;
	/**
	 * 开户行省份
	 */
	private String openBState;
	/**
	 * 开户行城市
	 */
	private String openBCity;
	/**
	 * 身份证正面照片
	 */
	private String posCemage;
	/**
	 * 身份证反面照片
	 */
	private String backCemage;
	/**
	 * 手持身份证照片
	 */
	private String handCemage;
	/**
	 * 营业场所照片一
	 */
	private String firBuzmage;
	/**
	 * 营业场所照片二
	 */
	private String secBuzmage;
	/**
	 * 营业执照照片
	 */
	private String buzLicmage;
	/**
	 * 开户许可证照片
	 */
	private String openPemage;
	/**
	 * 操作标识
	 */
	private String operFlag;
	/**
	 * 单笔D0提现交易手续费
	 */
	private String t0drawFee;
	/**
	 * D0提现交易手续费扣率
	 */
	private String t0drawRate;
	/**
	 * 单笔消费交易手续费
	 */
	private String t1consFee;
	/**
	 * 消费交易手续费扣率
	 */
	private String t1consRate;

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

	public String getFrontMid() {
		return frontMid;
	}

	public void setFrontMid(String frontMid) {
		this.frontMid = frontMid;
	}

	public String getMerName() {
		return merName;
	}

	public void setMerName(String merName) {
		this.merName = merName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getMerState() {
		return merState;
	}

	public void setMerState(String merState) {
		this.merState = merState;
	}

	public String getMerCity() {
		return merCity;
	}

	public void setMerCity(String merCity) {
		this.merCity = merCity;
	}

	public String getMerAddress() {
		return merAddress;
	}

	public void setMerAddress(String merAddress) {
		this.merAddress = merAddress;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertId() {
		return certId;
	}

	public void setCertId(String certId) {
		this.certId = certId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getOpenBName() {
		return openBName;
	}

	public void setOpenBName(String openBName) {
		this.openBName = openBName;
	}

	public String getOpenBCode() {
		return openBCode;
	}

	public void setOpenBCode(String openBCode) {
		this.openBCode = openBCode;
	}

	public String getOpenBState() {
		return openBState;
	}

	public void setOpenBState(String openBState) {
		this.openBState = openBState;
	}

	public String getOpenBCity() {
		return openBCity;
	}

	public void setOpenBCity(String openBCity) {
		this.openBCity = openBCity;
	}

	public String getPosCemage() {
		return posCemage;
	}

	public void setPosCemage(String posCemage) {
		this.posCemage = posCemage;
	}

	public String getBackCemage() {
		return backCemage;
	}

	public void setBackCemage(String backCemage) {
		this.backCemage = backCemage;
	}

	public String getHandCemage() {
		return handCemage;
	}

	public void setHandCemage(String handCemage) {
		this.handCemage = handCemage;
	}

	public String getFirBuzmage() {
		return firBuzmage;
	}

	public void setFirBuzmage(String firBuzmage) {
		this.firBuzmage = firBuzmage;
	}

	public String getSecBuzmage() {
		return secBuzmage;
	}

	public void setSecBuzmage(String secBuzmage) {
		this.secBuzmage = secBuzmage;
	}

	public String getBuzLicmage() {
		return buzLicmage;
	}

	public void setBuzLicmage(String buzLicmage) {
		this.buzLicmage = buzLicmage;
	}

	public String getOpenPemage() {
		return openPemage;
	}

	public void setOpenPemage(String openPemage) {
		this.openPemage = openPemage;
	}

	public String getOperFlag() {
		return operFlag;
	}

	public void setOperFlag(String operFlag) {
		this.operFlag = operFlag;
	}

	public String getT0drawFee() {
		return t0drawFee;
	}

	public void setT0drawFee(String t0drawFee) {
		this.t0drawFee = t0drawFee;
	}

	public String getT0drawRate() {
		return t0drawRate;
	}

	public void setT0drawRate(String t0drawRate) {
		this.t0drawRate = t0drawRate;
	}

	public String getT1consFee() {
		return t1consFee;
	}

	public void setT1consFee(String t1consFee) {
		this.t1consFee = t1consFee;
	}

	public String getT1consRate() {
		return t1consRate;
	}

	public void setT1consRate(String t1consRate) {
		this.t1consRate = t1consRate;
	}

	@Override
	public String toString() {
		return "ApplyMerInfo [tranType=" + tranType + ", tranDate=" + tranDate + ", tranTime=" + tranTime + ", merTrace=" + merTrace + ", frontMid=" + frontMid + ", merName=" + merName + ", realName=" + realName + ", merState=" + merState
				+ ", merCity=" + merCity + ", merAddress=" + merAddress + ", certType=" + certType + ", certId=" + certId + ", mobile=" + mobile + ", accountId=" + accountId + ", accountName=" + accountName + ", bankName=" + bankName
				+ ", bankCode=" + bankCode + ", openBName=" + openBName + ", openBCode=" + openBCode + ", openBState=" + openBState + ", openBCity=" + openBCity + ", posCemage=" + posCemage + ", backCemage=" + backCemage + ", handCemage="
				+ handCemage + ", firBuzmage=" + firBuzmage + ", secBuzmage=" + secBuzmage + ", buzLicmage=" + buzLicmage + ", openPemage=" + openPemage + ", operFlag=" + operFlag + ", t0drawFee=" + t0drawFee + ", t0drawRate=" + t0drawRate
				+ ", t1consFee=" + t1consFee + ", t1consRate=" + t1consRate + "]";
	}

}
