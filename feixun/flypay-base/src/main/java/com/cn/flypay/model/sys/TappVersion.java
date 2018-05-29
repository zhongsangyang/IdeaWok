package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * SysVersionId entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_version")
public class TappVersion implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4844318811261833796L;
	// Fields

	private Long id;
	private Long version;
	private String appType;
	private String versionName;
	private Integer isForce;
	private String updateUrl;
	private String content;
	private Integer status;
	private Date createTime;
	private String creator;
	private String downloadNet;

	private Torganization organization;

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "version")
	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Column(name = "version_name", length = 128)
	public String getVersionName() {
		return this.versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	public Torganization getOrganization() {
		return organization;
	}

	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}

	@Column(name = "app_type", length = 8)
	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	@Column(name = "is_force")
	public Integer getIsForce() {
		return this.isForce;
	}

	public void setIsForce(Integer isForce) {
		this.isForce = isForce;
	}

	@Column(name = "update_url", length = 256)
	public String getUpdateUrl() {
		return this.updateUrl;
	}

	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}

	@Column(name = "download_net", length = 256)
	public String getDownloadNet() {
		return downloadNet;
	}

	public void setDownloadNet(String downloadNet) {
		this.downloadNet = downloadNet;
	}

	@Column(name = "content", length = 65535)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "creator", length = 64)
	public String getCreator() {
		return this.creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

}