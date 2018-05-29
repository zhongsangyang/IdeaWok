package com.cn.flypay.model.sys;

// default package

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "SYS_BROKERAGE_CONFIG")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TbrokerageConfig implements java.io.Serializable {

	private static final long serialVersionUID = 6101559767759408063L;
	private TbrokerageConfigId id;
	private Integer firstRate;
	private Integer secRate;
	private Integer thirdRate;

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "agentType", column = @Column(name = "AGENT_TYPE", nullable = false, precision = 2, scale = 0)),
			@AttributeOverride(name = "cfgType", column = @Column(name = "cfg_type", nullable = false, precision = 2, scale = 0)),
			@AttributeOverride(name = "organizationId", column = @Column(name = "ORGANIZATION_ID", nullable = false, precision = 10, scale = 0)) })
	public TbrokerageConfigId getId() {
		return this.id;
	}

	public void setId(TbrokerageConfigId id) {
		this.id = id;
	}

	@Column(name = "first_Rate", nullable = false, precision = 4, scale = 0)
	public Integer getFirstRate() {
		return firstRate;
	}

	public void setFirstRate(Integer firstRate) {
		this.firstRate = firstRate;
	}

	@Column(name = "sec_Rate", nullable = false, precision = 4, scale = 0)
	public Integer getSecRate() {
		return secRate;
	}

	public void setSecRate(Integer secRate) {
		this.secRate = secRate;
	}

	@Column(name = "third_Rate", nullable = false, precision = 4, scale = 0)
	public Integer getThirdRate() {
		return thirdRate;
	}

	public void setThirdRate(Integer thirdRate) {
		this.thirdRate = thirdRate;
	}

}