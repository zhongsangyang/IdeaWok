package com.cn.flypay.model.sys;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 通道机构用户角色 三者确定的费率配置
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "org_channel_user_rate_config")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TorgChannelUserRateConfig implements java.io.Serializable {
	private static final long serialVersionUID = -546176083535839455L;

	private TorgChannelUserRateConfigId id;
	private BigDecimal t1Rate;
	private BigDecimal d0Rate;
	private BigDecimal t1BigRate;
	private BigDecimal d0BigRate;

	public TorgChannelUserRateConfig() {
	}

	public TorgChannelUserRateConfig(TorgChannelUserRateConfigId id) {
		this.id = id;
	}

	public TorgChannelUserRateConfig(TorgChannelUserRateConfigId id, BigDecimal t1Rate, BigDecimal d0Rate, BigDecimal t1BigRate, BigDecimal d0BigRate) {
		this.id = id;
		this.t1Rate = t1Rate;
		this.d0Rate = d0Rate;
		this.t1BigRate = t1BigRate;
		this.d0BigRate = d0BigRate;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "channelType", column = @Column(name = "channel_type", nullable = false)),
			@AttributeOverride(name = "organizationId", column = @Column(name = "organization_id", nullable = false)),
			@AttributeOverride(name = "agentType", column = @Column(name = "agent_Type", nullable = false)) })
	public TorgChannelUserRateConfigId getId() {
		return this.id;
	}

	public void setId(TorgChannelUserRateConfigId id) {
		this.id = id;
	}

	@Column(name = "t1_rate", precision = 8, scale = 6)
	public BigDecimal getT1Rate() {
		return this.t1Rate;
	}

	public void setT1Rate(BigDecimal t1Rate) {
		this.t1Rate = t1Rate;
	}

	@Column(name = "d0_rate", precision = 8, scale = 6)
	public BigDecimal getD0Rate() {
		return this.d0Rate;
	}

	public void setD0Rate(BigDecimal d0Rate) {
		this.d0Rate = d0Rate;
	}

	@Column(name = "t1_big_rate", precision = 8, scale = 6)
	public BigDecimal getT1BigRate() {
		return t1BigRate;
	}

	public void setT1BigRate(BigDecimal t1BigRate) {
		this.t1BigRate = t1BigRate;
	}
	@Column(name = "d0_big_rate", precision = 8, scale = 6)
	public BigDecimal getD0BigRate() {
		return d0BigRate;
	}

	public void setD0BigRate(BigDecimal d0BigRate) {
		this.d0BigRate = d0BigRate;
	}

}