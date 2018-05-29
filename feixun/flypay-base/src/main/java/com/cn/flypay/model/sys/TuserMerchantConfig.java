package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

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
 * SysUserMerchantConfig entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_user_merchant_config")
public class TuserMerchantConfig implements java.io.Serializable {

	private static final long serialVersionUID = 3895540363770L;

	private Long id;
	private Long version;
	private Tuser user;
	private String subMerchantId;
	/**
	 * 商户所在通道方名称 SHENFU:申孚
	 */
	private String merchantChannelName;
	private String config;

	/**
	 * 1正常 0冻结
	 */
	private Integer status;
	/**
	 * 200 支付宝 300 微信， 999 综合
	 */
	private Integer type;
	private TserviceMerchant serviceMerchant;

	public TuserMerchantConfig() {
		this.status = 0;
	}

	public TuserMerchantConfig(Tuser user, TserviceMerchant serviceMerchant, Integer type, String subMerchantId,
			String config) {
		this.user = user;
		this.subMerchantId = subMerchantId;
		this.type = type;
		this.config = config;
		this.serviceMerchant = serviceMerchant;
		this.status = 0;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	public Tuser getUser() {
		return this.user;
	}

	public void setUser(Tuser user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_merchant_id")
	public TserviceMerchant getServiceMerchant() {
		return serviceMerchant;
	}

	public void setServiceMerchant(TserviceMerchant serviceMerchant) {
		this.serviceMerchant = serviceMerchant;
	}

	@Column(name = "sub_merchant_id", length = 64)
	public String getSubMerchantId() {
		return subMerchantId;
	}

	public void setSubMerchantId(String subMerchantId) {
		this.subMerchantId = subMerchantId;
	}

	@Column(name = "merchantChannelName", length = 32)
	public String getMerchantChannelName() {
		return merchantChannelName;
	}

	public void setMerchantChannelName(String merchantChannelName) {
		this.merchantChannelName = merchantChannelName;
	}

	@Column(name = "config", length = 2047)
	public String getConfig() {
		return this.config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}