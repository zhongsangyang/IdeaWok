package com.cn.flypay.model.sys;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "sys_agent_settlement_rate_cfg")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TagentSettlementRateCfg implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 3935258486909270307L;
	private TagentSettlementRateCfgId id;
	private Double settlementRate;

	// Constructors

	/** default constructor */
	public TagentSettlementRateCfg() {
	}

	/** full constructor */
	public TagentSettlementRateCfg(TagentSettlementRateCfgId id, Double settlementRate) {
		this.id = id;
		this.settlementRate = settlementRate;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "organizationId", column = @Column(name = "organization_id", nullable = false)),
			@AttributeOverride(name = "agentType", column = @Column(name = "agent_type", nullable = false)),
			@AttributeOverride(name = "payType", column = @Column(name = "pay_type", nullable = false)) })
	public TagentSettlementRateCfgId getId() {
		return this.id;
	}

	public void setId(TagentSettlementRateCfgId id) {
		this.id = id;
	}

	@Column(name = "settlement_rate", nullable = false, precision = 6, scale = 4)
	public Double getSettlementRate() {
		return this.settlementRate;
	}

	public void setSettlementRate(Double settlementRate) {
		this.settlementRate = settlementRate;
	}

}