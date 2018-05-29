package com.cn.flypay.model.sys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * OrgChannelConfigId entity. @author MyEclipse Persistence Tools
 */
@Embeddable
public class TorgChannelUserRateConfigId implements java.io.Serializable {

	private static final long serialVersionUID = -8102499609358785548L;
	private Integer channelType;
	private Integer agentType;
	private Torganization organization;

	// Constructors

	public TorgChannelUserRateConfigId() {
	}

	public TorgChannelUserRateConfigId(Integer channelType, Integer agentType, Torganization organization) {
		this.channelType = channelType;
		this.organization = organization;
		this.agentType = agentType;
	}

	// Property accessors

	@Column(name = "channel_type", nullable = false)
	public Integer getChannelType() {
		return this.channelType;
	}

	public void setChannelType(Integer channelType) {
		this.channelType = channelType;
	}

	@Column(name = "agent_type", nullable = false)
	public Integer getAgentType() {
		return agentType;
	}

	public void setAgentType(Integer agentType) {
		this.agentType = agentType;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	public Torganization getOrganization() {
		return organization;
	}

	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TorgChannelUserRateConfigId))
			return false;
		TorgChannelUserRateConfigId castOther = (TorgChannelUserRateConfigId) other;

		return ((this.getChannelType() == castOther.getChannelType()) || (this.getChannelType() != null && castOther.getChannelType() != null && this.getChannelType().equals(
				castOther.getChannelType())))
				&& ((this.getOrganization().getId() == castOther.getOrganization().getId()) || (this.getOrganization().getId() != null && castOther.getOrganization().getId() != null && this
						.getOrganization().getId().equals(castOther.getOrganization().getId())))
				&& ((this.getAgentType() == castOther.getAgentType()) || (this.getAgentType() != null && castOther.getAgentType() != null && this.getAgentType().equals(castOther.getAgentType())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getChannelType() == null ? 0 : this.getChannelType().hashCode());
		result = 37 * result + (getOrganization().getId() == null ? 0 : this.getOrganization().getId().hashCode());
		result = 37 * result + (getAgentType() == null ? 0 : this.getAgentType().hashCode());
		return result;
	}
}