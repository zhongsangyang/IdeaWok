package com.cn.flypay.pageModel.trans;

import java.math.BigDecimal;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.pageModel.sys.User;

// default package

public class Brokerage implements java.io.Serializable {

	private static final long serialVersionUID = -3962346440140706800L;
	private Long id;
	private Long version;
	private String userCode;
	private String userName;
	private String loginName;
	private String organizationName;
	private Long userId;
	private String openid;
	private BigDecimal brokerage;// 佣金
	private BigDecimal totalBrokerage;
	private BigDecimal totalTransBrokerage;
	private BigDecimal totalAgentBrokerage;
	private BigDecimal totalLeadBrokerage;

	private BigDecimal lockBrokerage;
	private BigDecimal historyBrokerage;
	private BigDecimal yesterdayBrokerage;

	private Integer status;// 状态

	private User operateUser;

	public Brokerage() {
	}

	public Brokerage(Tbrokerage t) {
		Tuser user = t.getUser();
		if (user != null) {
			this.userId = t.getUser().getId();
			this.userName = t.getUser().getRealName();
			this.loginName = t.getUser().getLoginName();
			this.userCode = t.getUser().getCode();
		}
		this.status = t.getStatus();
		this.brokerage = t.getBrokerage();
		this.totalBrokerage = t.getTotalBrokerage();
		this.totalTransBrokerage = t.getTotalTransBrokerage();
		this.totalAgentBrokerage = t.getTotalAgentBrokerage();
		this.totalLeadBrokerage = t.getTotalLeadBrokerage();
		this.lockBrokerage = t.getLockBrokerage();
		this.historyBrokerage = t.getHistoryBrokerage();
		this.yesterdayBrokerage = t.getYesterdayBrokerage();
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public BigDecimal getBrokerage() {
		return brokerage;
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

	public void setBrokerage(BigDecimal brokerage) {
		this.brokerage = brokerage;
	}

	public BigDecimal getTotalBrokerage() {
		return totalBrokerage;
	}

	public void setTotalBrokerage(BigDecimal totalBrokerage) {
		this.totalBrokerage = totalBrokerage;
	}

	public BigDecimal getTotalTransBrokerage() {
		return totalTransBrokerage;
	}

	public void setTotalTransBrokerage(BigDecimal totalTransBrokerage) {
		this.totalTransBrokerage = totalTransBrokerage;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public BigDecimal getTotalAgentBrokerage() {
		return totalAgentBrokerage;
	}

	public void setTotalAgentBrokerage(BigDecimal totalAgentBrokerage) {
		this.totalAgentBrokerage = totalAgentBrokerage;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public BigDecimal getTotalLeadBrokerage() {
		return totalLeadBrokerage;
	}

	public void setTotalLeadBrokerage(BigDecimal totalLeadBrokerage) {
		this.totalLeadBrokerage = totalLeadBrokerage;
	}

	public BigDecimal getLockBrokerage() {
		return lockBrokerage;
	}

	public void setLockBrokerage(BigDecimal lockBrokerage) {
		this.lockBrokerage = lockBrokerage;
	}

	public BigDecimal getHistoryBrokerage() {
		return historyBrokerage;
	}

	public void setHistoryBrokerage(BigDecimal historyBrokerage) {
		this.historyBrokerage = historyBrokerage;
	}

	public BigDecimal getYesterdayBrokerage() {
		return yesterdayBrokerage;
	}

	public void setYesterdayBrokerage(BigDecimal yesterdayBrokerage) {
		this.yesterdayBrokerage = yesterdayBrokerage;
	}

}