package com.cn.flypay.model.sys;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * SysUserChannel entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_user_channel")
public class TuserChannel implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -1749298100521634510L;
	private TuserChannelId id;
	private Integer numPerDay;

	// Constructors

	/** default constructor */
	public TuserChannel() {
		numPerDay = 0;
	}

	/** minimal constructor */
	public TuserChannel(TuserChannelId id) {
		this.id = id;
		numPerDay = 0;
	}

	/** full constructor */
	public TuserChannel(TuserChannelId id, Integer numPerDay) {
		this.id = id;
		this.numPerDay = numPerDay;
		numPerDay = 0;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "userId", column = @Column(name = "USER_ID", nullable = false)),
			@AttributeOverride(name = "channelId", column = @Column(name = "channel_ID", nullable = false)) })
	public TuserChannelId getId() {
		return this.id;
	}

	public void setId(TuserChannelId id) {
		this.id = id;
	}

	@Column(name = "numPerDay")
	public Integer getNumPerDay() {
		return this.numPerDay;
	}

	public void setNumPerDay(Integer numPerDay) {
		this.numPerDay = numPerDay;
	}

}