package com.cn.flypay.model.sys;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Embeddable
public class TbrokerageConfigId implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7208403268786865768L;
	private String agentType;
	private Torganization organization;
	private String cfgType;

	public TbrokerageConfigId() {
	}
	public TbrokerageConfigId(String agentType,Torganization organization,String cfgType) {
		this.agentType = agentType;
		this.organization = organization;
		this.cfgType =cfgType;
	}

	@Column(name = "AGENT_TYPE", nullable = false, precision = 4, scale = 0)
	public String getAgentType() {
		return this.agentType;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}
	@Column(name = "cfg_type", nullable = false, precision = 4, scale = 0)
	public String getCfgType() {
		return cfgType;
	}

	public void setCfgType(String cfgType) {
		this.cfgType = cfgType;
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
		if (!(other instanceof TbrokerageConfigId))
			return false;
		TbrokerageConfigId castOther = (TbrokerageConfigId) other;

		return (this.getAgentType() == castOther.getAgentType())
				&& (this.getCfgType() == castOther.getCfgType())
				&& (this.getOrganization().getId() == castOther.getOrganization().getId());
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + Integer.parseInt(this.getAgentType());
		result = 37 * result + Integer.parseInt(this.getCfgType());
		result = 37 * result + this.getOrganization().getId().intValue();
		return result;
	}

}