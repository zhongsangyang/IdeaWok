package com.cn.flypay.model.sys;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * 用户的申孚通道方报备信息
 * 
 * @author Administrator
 *
 */
@Entity
@Table(name = "sys_user_merchant_report")
public class TuserMerchantReport {

	private Long id;
	private Long version;
	private Long userId;
	private String loginName; // 登录名
	private String merchantName;
	private String settleCardNo; // 报备卡好
	private String channelName;
	private String mercId;
	private String retCode;
	private String retMsg;
	private String publicKey;
	private String privateKey;
	private String publicKey2;
	private String privateKey2;
	private Date createTime; // 创建时间

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

	@Column(name = "userId")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "loginName")
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Column(name = "merchantName")
	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	@Column(name = "mercId")
	public String getMercId() {
		return mercId;
	}

	public void setMercId(String mercId) {
		this.mercId = mercId;
	}

	@Column(name = "settleCardNo")
	public String getSettleCardNo() {
		return settleCardNo;
	}

	public void setSettleCardNo(String settleCardNo) {
		this.settleCardNo = settleCardNo;
	}

	@Column(name = "channelName")
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	@Column(name = "retCode")
	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	@Column(name = "retMsg")
	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

	@Column(name = "publicKey")
	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@Column(name = "privateKey")
	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	@Column(name = "publicKey2")
	public String getPublicKey2() {
		return publicKey2;
	}

	public void setPublicKey2(String publicKey2) {
		this.publicKey2 = publicKey2;
	}

	@Column(name = "privateKey2")
	public String getPrivateKey2() {
		return privateKey2;
	}

	public void setPrivateKey2(String privateKey2) {
		this.privateKey2 = privateKey2;
	}

	@Column(name = "createTime")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
