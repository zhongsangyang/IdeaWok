package com.cn.flypay.model.sys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "org_sys_config")
public class TorgSysConfig implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4852000948946813139L;
	// Fields

	private Long id;
	private Integer version;
	private Torganization organization;
	private String agentId;
	private String msgCfg;
	private String jiguangCfg;
	private String agentSettlementCfg;

	// Constructors

	/** default constructor */
	public TorgSysConfig() {
	}

	/** full constructor */
	public TorgSysConfig(String agentId, String msgCfg, String jiguangCfg, String agentSettlementCfg) {
		this.agentId = agentId;
		this.msgCfg = msgCfg;
		this.jiguangCfg = jiguangCfg;
		this.agentSettlementCfg = agentSettlementCfg;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "org_id")
	public Torganization getOrganization() {
		return organization;
	}

	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}

	@Column(name = "agentId", length = 16)
	public String getAgentId() {
		return this.agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	@Column(name = "msg_cfg", length = 65535)
	public String getMsgCfg() {
		return this.msgCfg;
	}

	public void setMsgCfg(String msgCfg) {
		this.msgCfg = msgCfg;
	}

	@Column(name = "jiguang_cfg", length = 65535)
	public String getJiguangCfg() {
		return this.jiguangCfg;
	}

	public void setJiguangCfg(String jiguangCfg) {
		this.jiguangCfg = jiguangCfg;
	}

	@Column(name = "agent_settlement_cfg", length = 65535)
	public String getAgentSettlementCfg() {
		return agentSettlementCfg;
	}

	public void setAgentSettlementCfg(String agentSettlementCfg) {
		this.agentSettlementCfg = agentSettlementCfg;
	}

}