package com.cn.flypay.pageModel.sys;

import java.util.Date;

/**
 * SysVersionId entity. @author MyEclipse Persistence Tools
 */
public class AppVersion implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 48443188113796L;
	// Fields

	private Long id;
	private Long version;
	private String appType;
	private String versionName;
	private Integer isForce;
	private String updateUrl;
	private String downloadNet;

	private String content;
	private Integer status;
	private Date createTime;
	private String creator;
	private Long organizationId;

	private String organizationName;
	private String appName;
	private String agentId;
	private Boolean bool;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getDownloadNet() {
		return downloadNet;
	}

	public void setDownloadNet(String downloadNet) {
		this.downloadNet = downloadNet;
	}

	public String getVersionName() {
		return versionName;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public Integer getIsForce() {
		return isForce;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public void setIsForce(Integer isForce) {
		this.isForce = isForce;
	}

	public String getUpdateUrl() {
		return updateUrl;
	}

	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Boolean getBool() {
		return bool;
	}

	public void setBool(Boolean bool) {
		this.bool = bool;
	}
	
	

}