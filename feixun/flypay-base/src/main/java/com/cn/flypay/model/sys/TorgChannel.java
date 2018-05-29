package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
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
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "sys_org_channel")
public class TorgChannel implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -5307493323317113602L;
	private Long id;
	private Integer version;
	private Torganization organization;
	private Tchannel channel;
	private BigDecimal realRate;
	private Integer status;
	private Date startDate;
	private Date endDate;
	private Date createDate;
	private String creator;
	private Date updateDate;
	private String updator;

	// Constructors

	/** default constructor */
	public TorgChannel() {
	}

	/** full constructor */
	public TorgChannel(BigDecimal realRate, Integer status, Date startDate, Date endDate, String creator) {
		this.realRate = realRate;
		this.status = status;
		this.startDate = startDate;
		this.endDate = endDate;
		this.createDate = new Date();
		this.creator = creator;
		this.updateDate = new Date();
		this.updator = creator;
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

	@Version
	@Column(name = "version")
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "org_id")
	public Torganization getOrganization() {
		return organization;
	}

	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "channel_id")
	public Tchannel getChannel() {
		return channel;
	}

	public void setChannel(Tchannel channel) {
		this.channel = channel;
	}

	@Column(name = "real_rate", precision = 6, scale = 4)
	public BigDecimal getRealRate() {
		return this.realRate;
	}

	public void setRealRate(BigDecimal realRate) {
		this.realRate = realRate;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "start_date", length = 19)
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "end_date", length = 19)
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "create_date", length = 19)
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Column(name = "creator", length = 32)
	public String getCreator() {
		return this.creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Column(name = "update_date", length = 19)
	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@Column(name = "updator", length = 32)
	public String getUpdator() {
		return this.updator;
	}

	public void setUpdator(String updator) {
		this.updator = updator;
	}

}