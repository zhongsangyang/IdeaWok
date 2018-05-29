package com.cn.flypay.model.sys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "sys_channel_distinct_merid")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TsysChannelDistinctMerid implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1580629725623815003L;

	private Long id;
	
	private String subMerchantId;
	private String channelId;
	private String channelName;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "sub_merchant_id")
	public String getSubMerchantId() {
		return subMerchantId;
	}
	public void setSubMerchantId(String subMerchantId) {
		this.subMerchantId = subMerchantId;
	}
	
	@Column(name = "channel_id")
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	
	@Column(name = "channel_name")
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	
	
	
	
	
	
}
