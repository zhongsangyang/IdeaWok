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
 * SysBusiness entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_business")
public class Tbusiness implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8860645352105102332L;
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

	private Torganization organization;

	// Constructors

	/** default constructor */
	public Tbusiness() {
	}

	/** full constructor */
	public Tbusiness(String contactor, String contactPhone, Integer busType, String companyNet, String busDesc) {
		this.contactor = contactor;
		this.contactPhone = contactPhone;
		this.busType = busType;
		this.companyNet = companyNet;
		this.busDesc = busDesc;
		this.status = 0;
		this.createTime = new Date();
	}

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	public Torganization getOrganization() {
		return organization;
	}

	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}

	@Version
	@Column(name = "version")
	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Column(name = "contactor", length = 64)
	public String getContactor() {
		return this.contactor;
	}

	public void setContactor(String contactor) {
		this.contactor = contactor;
	}

	@Column(name = "contact_phone", length = 32)
	public String getContactPhone() {
		return this.contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	@Column(name = "bus_type")
	public Integer getBusType() {
		return this.busType;
	}

	public void setBusType(Integer busType) {
		this.busType = busType;
	}

	@Column(name = "company_net", length = 128)
	public String getCompanyNet() {
		return this.companyNet;
	}

	public void setCompanyNet(String companyNet) {
		this.companyNet = companyNet;
	}

	@Column(name = "bus_desc", length = 65535)
	public String getBusDesc() {
		return this.busDesc;
	}

	public void setBusDesc(String busDesc) {
		this.busDesc = busDesc;
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

}