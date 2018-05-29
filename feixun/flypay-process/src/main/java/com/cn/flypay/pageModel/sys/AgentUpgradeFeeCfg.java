package com.cn.flypay.pageModel.sys;

import java.math.BigDecimal;

public class AgentUpgradeFeeCfg implements java.io.Serializable {

	private static final long serialVersionUID = 39270307L;
	private Long organizationId;
	private String orgName;
	private Integer agentType;
	private BigDecimal glodFee;
	private BigDecimal diamondFee;
	private BigDecimal authFee;
	private BigDecimal tixianT0Fee;
	private BigDecimal tixianT1Fee;
	public Long getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public BigDecimal getGlodFee() {
		return glodFee;
	}
	public void setGlodFee(BigDecimal glodFee) {
		this.glodFee = glodFee;
	}
	public BigDecimal getDiamondFee() {
		return diamondFee;
	}
	public Integer getAgentType() {
		return agentType;
	}
	public void setAgentType(Integer agentType) {
		this.agentType = agentType;
	}
	public void setDiamondFee(BigDecimal diamondFee) {
		this.diamondFee = diamondFee;
	}
	public BigDecimal getAuthFee() {
		return authFee;
	}
	public void setAuthFee(BigDecimal authFee) {
		this.authFee = authFee;
	}
	public BigDecimal getTixianT0Fee() {
		return tixianT0Fee;
	}
	public void setTixianT0Fee(BigDecimal tixianT0Fee) {
		this.tixianT0Fee = tixianT0Fee;
	}
	public BigDecimal getTixianT1Fee() {
		return tixianT1Fee;
	}
	public void setTixianT1Fee(BigDecimal tixianT1Fee) {
		this.tixianT1Fee = tixianT1Fee;
	}

}