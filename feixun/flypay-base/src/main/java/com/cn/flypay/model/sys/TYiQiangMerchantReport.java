package com.cn.flypay.model.sys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "sys_yiqiang_merchant_report")
public class TYiQiangMerchantReport {

	private Long id;
	private Long version;
	private String frontMid; // 同 userId
	private String userType; // 报备等级
	private String merTrace;
	private String merName;
	private String realName;
	private String merState;
	private String merCity;
	private String merAddress;
	private String certType;// 01：身份证；02：军官证；03：护照；04：户口簿；05：回乡证；06：其他
	private String certId;
	private String mobile;
	private String accountId;
	private String accountName;
	private String bankName;
	private String bankCode;
	private String operFlag;// A：新增；M：修改全部；M01:修改商户基本信息；M02:修改结算卡信息；M03:修改T0费率；M04:修改T1费率；M05:修改图片信息（暂时不支持）
	private String t1consFee;
	private String t1consRate;

	private String respCode;
	private String respMsg;
	private String merNo;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "version", nullable = false, precision = 10, scale = 0)
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Column(name = "frontMid")
	public String getFrontMid() {
		return frontMid;
	}

	public void setFrontMid(String frontMid) {
		this.frontMid = frontMid;
	}

	@Column(name = "userType")
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	@Column(name = "merTrace")
	public String getMerTrace() {
		return merTrace;
	}

	public void setMerTrace(String merTrace) {
		this.merTrace = merTrace;
	}

	@Column(name = "merName")
	public String getMerName() {
		return merName;
	}

	public void setMerName(String merName) {
		this.merName = merName;
	}

	@Column(name = "realName")
	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	@Column(name = "merState")
	public String getMerState() {
		return merState;
	}

	public void setMerState(String merState) {
		this.merState = merState;
	}

	@Column(name = "merCity")
	public String getMerCity() {
		return merCity;
	}

	public void setMerCity(String merCity) {
		this.merCity = merCity;
	}

	@Column(name = "merAddress")
	public String getMerAddress() {
		return merAddress;
	}

	public void setMerAddress(String merAddress) {
		this.merAddress = merAddress;
	}

	@Column(name = "certType")
	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	@Column(name = "certId")
	public String getCertId() {
		return certId;
	}

	public void setCertId(String certId) {
		this.certId = certId;
	}

	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "accountId")
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	@Column(name = "accountName")
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Column(name = "bankName")
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Column(name = "bankCode")
	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	@Column(name = "operFlag")
	public String getOperFlag() {
		return operFlag;
	}

	public void setOperFlag(String operFlag) {
		this.operFlag = operFlag;
	}

	@Column(name = "t1consFee")
	public String getT1consFee() {
		return t1consFee;
	}

	public void setT1consFee(String t1consFee) {
		this.t1consFee = t1consFee;
	}

	@Column(name = "t1consRate")
	public String getT1consRate() {
		return t1consRate;
	}

	public void setT1consRate(String t1consRate) {
		this.t1consRate = t1consRate;
	}

	@Column(name = "respCode")
	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	@Column(name = "respMsg")
	public String getRespMsg() {
		return respMsg;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

	@Column(name = "merNo")
	public String getMerNo() {
		return merNo;
	}

	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}

}
