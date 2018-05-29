package com.cn.flypay.model.sys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * SysAgentUpgradeFeeCfgId entity. @author MyEclipse Persistence Tools
 */
@Embeddable
public class TagentUpgradeFeeCfgId implements java.io.Serializable {

	/**
	 * 
	 */
	private Torganization organization;
	private Integer agentType;

	// Constructors

	/** default constructor */
	public TagentUpgradeFeeCfgId() {
	}

	/** full constructor */
	public TagentUpgradeFeeCfgId(Torganization organization, Integer agentType) {
		this.organization = organization;
		this.agentType = agentType;
	}

	// Property accessors
	@ManyToOne
	@JoinColumn(name = "organization_id")
	public Torganization getOrganization() {
		return organization;
	}

	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}

	@Column(name = "agent_type", nullable = false)
	public Integer getAgentType() {
		return this.agentType;
	}

	public void setAgentType(Integer agentType) {
		this.agentType = agentType;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TagentUpgradeFeeCfgId))
			return false;
		TagentUpgradeFeeCfgId castOther = (TagentUpgradeFeeCfgId) other;

		return ((this.organization.getId() == castOther.organization.getId()) || (this.organization.getId() != null && castOther.organization.getId() != null && this.organization.getId().equals(
				castOther.organization.getId())))
				&& ((this.getAgentType() == castOther.getAgentType()) || (this.getAgentType() != null && castOther.getAgentType() != null && this.getAgentType().equals(castOther.getAgentType())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (organization.getId() == null ? 0 : this.organization.getId().hashCode());
		result = 37 * result + (getAgentType() == null ? 0 : this.getAgentType().hashCode());
		return result;
	}

}