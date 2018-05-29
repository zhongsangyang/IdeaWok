package com.cn.flypay.model.sys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * SysAgentSettlementRateCfgId entity. @author MyEclipse Persistence Tools
 */
@Embeddable
public class TagentSettlementRateCfgId implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 3058399915157796427L;
	/**
	 * 
	 */
	private Torganization organization;
	private Integer agentType;
	private Integer payType;

	// Constructors

	/** default constructor */
	public TagentSettlementRateCfgId() {
	}

	/** full constructor */
	public TagentSettlementRateCfgId(Torganization organization, Integer agentType, Integer payType) {
		this.organization = organization;
		this.agentType = agentType;
		this.payType = payType;
	}

	// Property accessors
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
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

	@Column(name = "pay_type", nullable = false)
	public Integer getPayType() {
		return this.payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TagentSettlementRateCfgId))
			return false;
		TagentSettlementRateCfgId castOther = (TagentSettlementRateCfgId) other;

		return ((this.getOrganization() == castOther.getOrganization()) || (this.getOrganization() != null && castOther.getOrganization() != null && this.getOrganization().equals(
				castOther.getOrganization())))
				&& ((this.getAgentType() == castOther.getAgentType()) || (this.getAgentType() != null && castOther.getAgentType() != null && this.getAgentType().equals(castOther.getAgentType())))
				&& ((this.getPayType() == castOther.getPayType()) || (this.getPayType() != null && castOther.getPayType() != null && this.getPayType().equals(castOther.getPayType())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getOrganization() == null ? 0 : this.getOrganization().hashCode());
		result = 37 * result + (getAgentType() == null ? 0 : this.getAgentType().hashCode());
		result = 37 * result + (getPayType() == null ? 0 : this.getPayType().hashCode());
		return result;
	}

}