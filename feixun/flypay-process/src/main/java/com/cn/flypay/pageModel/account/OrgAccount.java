package com.cn.flypay.pageModel.account;

import java.math.BigDecimal;

import com.cn.flypay.pageModel.sys.User;

public class OrgAccount implements java.io.Serializable {

	private static final long serialVersionUID = -69436210371989836L;
	private Long id;
	private Long version;
	private String orgName;
	private Long organizationId;
	private Integer type;
	private BigDecimal amt;
	private BigDecimal lockAmt;
	private BigDecimal limitAmt;
	private Integer status;
	private User operateUser;

	// Fields
	public enum account_type {
		/**
		 * 代理流量费
		 */
		trade(100),
		/**
		 * T0代付金额
		 */
		t0Amt(200),
		/**
		 * 代付手续费
		 */
		tixian(300),
		/**
		 * 实名认证费
		 */
		authentication(400),
		/**
		 * 短信费
		 */
		message(500);
		private account_type(Integer code) {
			this.code = code;
		}

		private Integer code;

		public Integer getCode() {
			return this.code;
		}
	};

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

	public Integer getType() {
		return type;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public BigDecimal getAmt() {
		return amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	public BigDecimal getLockAmt() {
		return lockAmt;
	}

	public void setLockAmt(BigDecimal lockAmt) {
		this.lockAmt = lockAmt;
	}

	public BigDecimal getLimitAmt() {
		return limitAmt;
	}

	public void setLimitAmt(BigDecimal limitAmt) {
		this.limitAmt = limitAmt;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}