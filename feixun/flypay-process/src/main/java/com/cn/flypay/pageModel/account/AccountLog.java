package com.cn.flypay.pageModel.account;

import java.math.BigDecimal;
import java.util.Date;

import com.cn.flypay.pageModel.sys.User;

public class AccountLog implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5213662281512986001L;
	// Fields

	private Long id;
	private Long version;
	private String realName;
	private String type;
	private BigDecimal amt;
	private BigDecimal avlAmt;
	private BigDecimal lockOutAmt;
	private Date createTime;
	private String description;
	private String loginName;
	private String organizationName;
	private User operateUser;

	public AccountLog() {

	}

	public AccountLog(String loginName, String realName, String type, BigDecimal amt, Date createTime, String description) {
		this.loginName = loginName;
		this.realName = realName;
		this.type = type;
		this.amt = amt;
		this.createTime = createTime;
		this.description = description;
	}

	public AccountLog(String loginName, String realName, String type, BigDecimal amt, BigDecimal avlAmt, BigDecimal lockOutAmt, Date createTime, String description) {
		this.loginName = loginName;
		this.realName = realName;
		this.type = type;
		this.amt = amt;
		this.avlAmt = avlAmt;
		this.lockOutAmt = lockOutAmt;
		this.createTime = createTime;
		this.description = description;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getAmt() {
		return amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	public BigDecimal getAvlAmt() {
		return avlAmt;
	}

	public void setAvlAmt(BigDecimal avlAmt) {
		this.avlAmt = avlAmt;
	}

	public BigDecimal getLockOutAmt() {
		return lockOutAmt;
	}

	public void setLockOutAmt(BigDecimal lockOutAmt) {
		this.lockOutAmt = lockOutAmt;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

}