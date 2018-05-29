package com.cn.flypay.pageModel.trans;

import java.util.Date;

// default package

public class OrgBrokeragePay implements java.io.Serializable {

	private static final long serialVersionUID = 742021356139893239L;
	private Long id;
	private Long version;
	private String organizationName;
	private Long orgId;
	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	private Double amt;
	private Double oldAmt;
	private Integer status;
	private Date payDatetime;

	private String userName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}


	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public Double getAmt() {
		return amt;
	}

	public void setAmt(Double amt) {
		this.amt = amt;
	}

	public Double getOldAmt() {
		return oldAmt;
	}

	public void setOldAmt(Double oldAmt) {
		this.oldAmt = oldAmt;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getPayDatetime() {
		return payDatetime;
	}

	public void setPayDatetime(Date payDatetime) {
		this.payDatetime = payDatetime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}