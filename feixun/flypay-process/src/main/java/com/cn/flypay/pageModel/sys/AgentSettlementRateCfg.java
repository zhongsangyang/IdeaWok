package com.cn.flypay.pageModel.sys;

public class AgentSettlementRateCfg implements java.io.Serializable {

	private static final long serialVersionUID = 39486909270307L;
	private Long organizationId;
	private String orgName;
	private Integer agentType;
	private Integer payType;
	private Double settlementRate;

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

	public Integer getAgentType() {
		return agentType;
	}

	public void setAgentType(Integer agentType) {
		this.agentType = agentType;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Double getSettlementRate() {
		return settlementRate;
	}

	public void setSettlementRate(Double settlementRate) {
		this.settlementRate = settlementRate;
	}

}