package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


@Entity
@Table(name = "sys_org_rate")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TsysOrgRate implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5766670473128180851L;
	
	
	
	private Long id;
	private Torganization organization;
	private String orgType;
	private BigDecimal orgRate;
	
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organizationId")
	public Torganization getOrganization() {
		return organization;
	}
	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}
	
	
	@Column(name = "orgType")
	public String getOrgType() {
		return orgType;
	}
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
	
	
	@Column(name = "orgRate")
	public BigDecimal getOrgRate() {
		return orgRate;
	}
	public void setOrgRate(BigDecimal orgRate) {
		this.orgRate = orgRate;
	}
	
	
	
	
	
	
	
}
