package com.cn.flypay.pageModel.sys;

import com.cn.flypay.model.sys.TuserMerchantReport;
import com.cn.flypay.model.sys.TweiLianBaoMerchantReport;

public class UserMerchantConfig implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 383770L;

	private Long id;
	private Long version;
	private String loginName;
	private String realName;
	private String subMerchantId;
	private String merchantChannelName;
	private String config;
	private Integer status;

	private Long organizationId;
	private String organizationName;

	private User operateUser;
	private Long serviceMerchantId;
	private String serviceMerchantName;
	private String serviceMerchantDetailName;
	private String merchantName;
	private String merchantShortName;
	private String address;

	/**
	 * 200 支付宝, 300 微信, 999 综合
	 */
	private Integer type;

	private TuserMerchantReport report;

	private TweiLianBaoMerchantReport weiLianBaoMerchantReport;

	// Fields
	public enum merchant_config_type {
		/**
		 * 支付宝
		 */
		ALIPAY(200),
		/**
		 * 微信
		 */
		WEIXIN(300),
		/**
		 * 综合
		 */
		COMPOSITE(999);
		private merchant_config_type(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	public UserMerchantConfig() {
		this.status = 0;
	}

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

	public String getServiceMerchantName() {
		return serviceMerchantName;
	}

	public void setServiceMerchantName(String serviceMerchantName) {
		this.serviceMerchantName = serviceMerchantName;
	}

	public String getServiceMerchantDetailName() {
		return serviceMerchantDetailName;
	}

	public void setServiceMerchantDetailName(String serviceMerchantDetailName) {
		this.serviceMerchantDetailName = serviceMerchantDetailName;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantShortName() {
		return merchantShortName;
	}

	public void setMerchantShortName(String merchantShortName) {
		this.merchantShortName = merchantShortName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getRealName() {
		return realName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Long getServiceMerchantId() {
		return serviceMerchantId;
	}

	public void setServiceMerchantId(Long serviceMerchantId) {
		this.serviceMerchantId = serviceMerchantId;
	}

	public String getSubMerchantId() {
		return subMerchantId;
	}

	public void setSubMerchantId(String subMerchantId) {
		this.subMerchantId = subMerchantId;
	}

	public String getMerchantChannelName() {
		return merchantChannelName;
	}

	public void setMerchantChannelName(String merchantChannelName) {
		this.merchantChannelName = merchantChannelName;
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

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public TuserMerchantReport getReport() {
		return report;
	}

	public void setReport(TuserMerchantReport report) {
		this.report = report;
	}

	public TweiLianBaoMerchantReport getWeiLianBaoMerchantReport() {
		return weiLianBaoMerchantReport;
	}

	public void setWeiLianBaoMerchantReport(TweiLianBaoMerchantReport weiLianBaoMerchantReport) {
		this.weiLianBaoMerchantReport = weiLianBaoMerchantReport;
	}

}