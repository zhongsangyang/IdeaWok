package com.cn.flypay.pageModel.trans;


// default package

public class OrgBrokerage implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -337495390907124054L;
	private Long id;
	private Long version;
	private Double brokerage;
	private Integer status;
	private String organizationName;
	private Double totalBrokerage;
	private Double totalTransBrokerage;
	private Double totalAgentBrokerage;

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

	public Double getBrokerage() {
		return brokerage;
	}

	public void setBrokerage(Double brokerage) {
		this.brokerage = brokerage;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public Double getTotalBrokerage() {
		return totalBrokerage;
	}

	public void setTotalBrokerage(Double totalBrokerage) {
		this.totalBrokerage = totalBrokerage;
	}

	public Double getTotalTransBrokerage() {
		return totalTransBrokerage;
	}

	public void setTotalTransBrokerage(Double totalTransBrokerage) {
		this.totalTransBrokerage = totalTransBrokerage;
	}

	public Double getTotalAgentBrokerage() {
		return totalAgentBrokerage;
	}

	public void setTotalAgentBrokerage(Double totalAgentBrokerage) {
		this.totalAgentBrokerage = totalAgentBrokerage;
	}

}