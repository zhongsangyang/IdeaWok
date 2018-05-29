package com.cn.flypay.pageModel.sys;

public class OrgSysConfig implements java.io.Serializable {
	private static final long serialVersionUID = -48520009439L;
	// Fields

	private Long id;
	private Integer version;
	private Long orgId;
	private String orgName;
	private String agentId;
	private String msgCfg;
	private String jiguangCfg;
	private String agentSettlementCfg;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getMsgCfg() {
		return msgCfg;
	}

	public void setMsgCfg(String msgCfg) {
		this.msgCfg = msgCfg;
	}

	public String getJiguangCfg() {
		return jiguangCfg;
	}

	public void setJiguangCfg(String jiguangCfg) {
		this.jiguangCfg = jiguangCfg;
	}

	public String getAgentSettlementCfg() {
		return agentSettlementCfg;
	}

	public void setAgentSettlementCfg(String agentSettlementCfg) {
		this.agentSettlementCfg = agentSettlementCfg;
	}

}