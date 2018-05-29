package com.cn.flypay.pageModel.sys;

import java.math.BigDecimal;

/**
 * 
 * @author Administrator
 */
public class OrgChannelUserRateConfig implements java.io.Serializable {

	private static final long serialVersionUID = -54617609455L;

	private Integer channelType;
	private Long organizationId;
	private Integer agentType;
	private String orgName;
	private BigDecimal t1Rate;
	private BigDecimal d0Rate;
	private BigDecimal t1BigRate;
	private BigDecimal d0BigRate;

	public Integer getChannelType() {
		return channelType;
	}

	public void setChannelType(Integer channelType) {
		this.channelType = channelType;
	}

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

	public BigDecimal getT1Rate() {
		return t1Rate;
	}

	public void setT1Rate(BigDecimal t1Rate) {
		this.t1Rate = t1Rate;
	}

	public Integer getAgentType() {
		return agentType;
	}

	public void setAgentType(Integer agentType) {
		this.agentType = agentType;
	}

	public BigDecimal getD0Rate() {
		return d0Rate;
	}

	public void setD0Rate(BigDecimal d0Rate) {
		this.d0Rate = d0Rate;
	}

	public BigDecimal getT1BigRate() {
		return t1BigRate;
	}

	public void setT1BigRate(BigDecimal t1BigRate) {
		this.t1BigRate = t1BigRate;
	}

	public BigDecimal getD0BigRate() {
		return d0BigRate;
	}

	public void setD0BigRate(BigDecimal d0BigRate) {
		this.d0BigRate = d0BigRate;
	}

}