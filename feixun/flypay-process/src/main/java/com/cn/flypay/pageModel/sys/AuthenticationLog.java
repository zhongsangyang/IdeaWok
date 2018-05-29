package com.cn.flypay.pageModel.sys;

import java.util.Date;

import com.cn.flypay.model.sys.Tuser;

public class AuthenticationLog implements java.io.Serializable {
	private static final long serialVersionUID = -549891926943911004L;
	private Long id;
	private Long version;
	private Long userId;
	private String idNo;
	private String realName;
	private String phone;
	private String cardNo;
	/**
	 * "0：pc 1：安卓 2：ios"
	 */
	private Integer phoneType;
	/**
	 * '0 未通过，1通过',
	 */
	private Integer status;
	private String errorCode;
	private String sysSeq;
	private String errorInfo;
	private Date createTime;
	private String agentCode;
	/**
	 * '0 自动认证，1人工审核认证，2人工商家认证',
	 */
	private Integer authType;

	private String loginName;
	private User operateUser;
	private Long organizationId;
	private String organizationName;

	// Fields
	public enum auth_type {
		/**
		 * 0 自动认证
		 */
		auto(0),
		/**
		 * 1人工审核认证，
		 */
		manual(1),
		/**
		 * 2人工商户审核认证，
		 */
		manual_merchant(2);
		private auth_type(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	public AuthenticationLog() {
	}

	public AuthenticationLog(Long userId, Integer authType, Integer phoneType, String idNo, String realName, String phone, String cardNo, String errorInfo, String agentCode) {
		this.userId = userId;
		this.idNo = idNo;
		this.realName = realName;
		this.authType = authType;
		this.phone = phone;
		this.cardNo = cardNo;
		this.phoneType = phoneType == null ? 0 : phoneType;
		this.createTime = new Date();
		this.errorInfo = errorInfo;
		this.agentCode = agentCode;
	}

	public String getSysSeq() {
		return sysSeq;
	}

	public void setSysSeq(String sysSeq) {
		this.sysSeq = sysSeq;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getAuthType() {
		return authType;
	}

	public void setAuthType(Integer authType) {
		this.authType = authType;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Integer getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(Integer phoneType) {
		this.phoneType = phoneType;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}