package com.cn.flypay.model.sys;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * SysUserChannelId entity. @author MyEclipse Persistence Tools
 */
@Embeddable
public class TuserChannelId implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 2394528187494027850L;
	private Long userId;
	private Long channelId;

	// Constructors

	/** default constructor */
	public TuserChannelId() {
	}

	/** full constructor */
	public TuserChannelId(Long userId, Long channelId) {
		this.userId = userId;
		this.channelId = channelId;
	}

	// Property accessors

	@Column(name = "USER_ID", nullable = false)
	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "channel_ID", nullable = false)
	public Long getChannelId() {
		return this.channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TuserChannelId))
			return false;
		TuserChannelId castOther = (TuserChannelId) other;

		return ((this.getUserId() == castOther.getUserId()) || (this.getUserId() != null && castOther.getUserId() != null && this.getUserId().equals(castOther.getUserId())))
				&& ((this.getChannelId() == castOther.getChannelId()) || (this.getChannelId() != null && castOther.getChannelId() != null && this.getChannelId().equals(castOther.getChannelId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getUserId() == null ? 0 : this.getUserId().hashCode());
		result = 37 * result + (getChannelId() == null ? 0 : this.getChannelId().hashCode());
		return result;
	}

}