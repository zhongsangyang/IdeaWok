package com.cn.flypay.pageModel.sys;

import java.util.Date;

public class Business implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -88606453521032L;
	// Fields

	private Long id;
	private Long version;
	private String contactor;
	private String contactPhone;
	private Integer busType;
	private String companyNet;
	private String busDesc;
	private Integer status;
	private Date createTime;

	private Long organizationId;

	private String organizationAppName;
	private String organizationName;

	private String agentId;
	
	private User operateUser;

	// Constructors

	/** default constructor */
	public Business() {
	}

	/** full constructor */
	public Business(String contactor, String contactPhone, Integer busType, String companyNet, String busDesc,
			String agentId) {
		this.contactor = contactor;
		this.contactPhone = contactPhone;
		this.busType = busType;
		this.companyNet = companyNet;
		this.busDesc = busDesc;
		this.status = 0;
		this.createTime = new Date();
		this.agentId = agentId;
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

	public String getOrganizationAppName() {
		return organizationAppName;
	}

	public void setOrganizationAppName(String organizationAppName) {
		this.organizationAppName = organizationAppName;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getContactor() {
		return contactor;
	}

	public void setContactor(String contactor) {
		this.contactor = contactor;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public Integer getBusType() {
		return busType;
	}

	public void setBusType(Integer busType) {
		this.busType = busType;
	}

	public String getCompanyNet() {
		return companyNet;
	}

	public void setCompanyNet(String companyNet) {
		this.companyNet = companyNet;
	}

	public String getBusDesc() {
		return busDesc;
	}

	public void setBusDesc(String busDesc) {
		this.busDesc = busDesc;
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

}