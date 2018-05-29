package com.cn.flypay.pageModel.sys;

/**
 * 所有的通道服务商 平安 民生
 * 
 * @author Administrator
 * 
 */
public class ServiceMerchant implements java.io.Serializable {

	private static final long serialVersionUID = -3848766797277820L;
	private Long id;
	private Long version;
	private String name;
	private String appId;
	private String detailName;
	private String config;
	private Integer status;
	/**
	 * 200 支付宝 300 微信， 999 综合
	 */
	private Integer type;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAppId() {
		return appId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getDetailName() {
		return detailName;
	}

	public void setDetailName(String detailName) {
		this.detailName = detailName;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}