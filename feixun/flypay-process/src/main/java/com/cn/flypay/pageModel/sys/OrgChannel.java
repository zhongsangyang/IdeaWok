package com.cn.flypay.pageModel.sys;

import java.math.BigDecimal;
import java.util.Date;

public class OrgChannel implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -53023317113602L;
	private Long id;
	private Integer version;
	/**
	 * 上级机构给代理的费率
	 */
	private BigDecimal realRate;
	/***
	 * 0 正常 1 废弃
	 */
	private Integer status;
	private Date startDate;
	private Date endDate;
	private Date createDate;
	private String creator;
	private Date updateDate;
	private String updator;
	/**
	 * 机构名称
	 */
	private Long orgId;
	private String orgName;
	private Long channelId;
	private String channelName;
	private String detailName;

	public OrgChannel() {

	}

	public OrgChannel(Long id, Integer version, BigDecimal realRate, 
			String updator) {
		this.id = id;
		this.version = version;
		this.realRate = realRate;
		this.updateDate = new Date();
		this.updator = updator;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDetailName() {
		return detailName;
	}

	public void setDetailName(String detailName) {
		this.detailName = detailName;
	}

	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}

	public BigDecimal getRealRate() {
		return realRate;
	}

	public void setRealRate(BigDecimal realRate) {
		this.realRate = realRate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getUpdator() {
		return updator;
	}

	public void setUpdator(String updator) {
		this.updator = updator;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

}