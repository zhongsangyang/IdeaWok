package com.cn.flypay.model.sys;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * SysAgentUpgradeFeeCfg entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_agent_upgrade_fee_cfg")
public class TagentUpgradeFeeCfg implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -5694341686429102452L;
	private TagentUpgradeFeeCfgId id;
	private BigDecimal glodFee;
	private BigDecimal diamondFee;
	private BigDecimal authFee;
	private BigDecimal tixianT0Fee;
	private BigDecimal tixianT1Fee;

	// Constructors

	/** default constructor */
	public TagentUpgradeFeeCfg() {
	}

	/** minimal constructor */
	public TagentUpgradeFeeCfg(TagentUpgradeFeeCfgId id) {
		this.id = id;
	}

	/** full constructor */
	public TagentUpgradeFeeCfg(TagentUpgradeFeeCfgId id, BigDecimal glodFee, BigDecimal diamondFee, BigDecimal authFee, BigDecimal tixianT0Fee, BigDecimal tixianT1Fee) {
		this.id = id;
		this.glodFee = glodFee;
		this.diamondFee = diamondFee;
		this.authFee = authFee;
		this.tixianT0Fee = tixianT0Fee;
		this.tixianT1Fee = tixianT1Fee;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "organizationId", column = @Column(name = "organization_id", nullable = false)),
			@AttributeOverride(name = "agentType", column = @Column(name = "agent_type", nullable = false)) })
	public TagentUpgradeFeeCfgId getId() {
		return this.id;
	}

	public void setId(TagentUpgradeFeeCfgId id) {
		this.id = id;
	}

	@Column(name = "gold_fee", precision = 8)
	public BigDecimal getGlodFee() {
		return this.glodFee;
	}

	public void setGlodFee(BigDecimal glodFee) {
		this.glodFee = glodFee;
	}

	@Column(name = "diamond_fee", precision = 8)
	public BigDecimal getDiamondFee() {
		return this.diamondFee;
	}

	public void setDiamondFee(BigDecimal diamondFee) {
		this.diamondFee = diamondFee;
	}

	@Column(name = "auth_fee", precision = 8)
	public BigDecimal getAuthFee() {
		return this.authFee;
	}

	public void setAuthFee(BigDecimal authFee) {
		this.authFee = authFee;
	}

	@Column(name = "tixian_t0_fee", precision = 8)
	public BigDecimal getTixianT0Fee() {
		return this.tixianT0Fee;
	}

	public void setTixianT0Fee(BigDecimal tixianT0Fee) {
		this.tixianT0Fee = tixianT0Fee;
	}

	@Column(name = "tixian_t1_fee", precision = 8)
	public BigDecimal getTixianT1Fee() {
		return this.tixianT1Fee;
	}

	public void setTixianT1Fee(BigDecimal tixianT1Fee) {
		this.tixianT1Fee = tixianT1Fee;
	}

}