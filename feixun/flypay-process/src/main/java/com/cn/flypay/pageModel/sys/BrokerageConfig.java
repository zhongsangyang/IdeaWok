package com.cn.flypay.pageModel.sys;

// default package

public class BrokerageConfig implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String agentType;
	private String cfgType;
	private long organizationId;
	private String orgName;
	private Integer firstRate;
	private Integer secRate;
	private Integer thirdRate;
	public String getAgentType() {
		return agentType;
	}
	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}
	public String getCfgType() {
		return cfgType;
	}
	public void setCfgType(String cfgType) {
		this.cfgType = cfgType;
	}
	public long getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(long organizationId) {
		this.organizationId = organizationId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public Integer getFirstRate() {
		return firstRate;
	}
	public void setFirstRate(Integer firstRate) {
		this.firstRate = firstRate;
	}
	public Integer getSecRate() {
		return secRate;
	}
	public void setSecRate(Integer secRate) {
		this.secRate = secRate;
	}
	public Integer getThirdRate() {
		return thirdRate;
	}
	public void setThirdRate(Integer thirdRate) {
		this.thirdRate = thirdRate;
	}

}