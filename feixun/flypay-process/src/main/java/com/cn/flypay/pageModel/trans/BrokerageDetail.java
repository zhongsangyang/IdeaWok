package com.cn.flypay.pageModel.trans;

// default package

import java.math.BigDecimal;
import java.util.Date;

import com.cn.flypay.pageModel.sys.User;

public class BrokerageDetail implements java.io.Serializable {

	private static final long serialVersionUID = 912643408736782263L;
	private Long id;
	private Long version;
	private String phone;
	private String userCode;
	private String userName;
	private Integer userType;
	private String organizationName;
	private Long appTransinfoId;
	private Integer transType;
	private BigDecimal amt;
	private Date transDatetime;
	private BigDecimal brokerage;
	private String description;
	private Long brokerageId;
	private String brokerageUserName;
	private String brokerageUserCode;
	/**
	 * 用户本次分拥所在的层级
	 */
	private Integer brokerageUserType;
	private Long brokerageUserId;

	private Double brokerageUserRate;
	private User operateUser;
	/**
	 * 方便前台查询今日或历史 类型
	 */
	private String searchType;
	
	private Integer brokerageType;
	
	private Date createDatetimeStart;
	private Date createDatetimeEnd;

	public enum trans_type {
		/**
		 * 佣金_代理
		 */
		brokerage_agent(100),
		/**
		 * 支付宝
		 */
		alipay(200),
		/**
		 * 微信
		 */
		wx(300),
		/**
		 * NFC
		 */
		nfc(400),
		/**
		 * 银联在线
		 */
		online_bank(500),
		/**
		 * card
		 */
		card(600);
		private trans_type(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public Long getAppTransinfoId() {
		return appTransinfoId;
	}

	public void setAppTransinfoId(Long appTransinfoId) {
		this.appTransinfoId = appTransinfoId;
	}

	public Integer getTransType() {
		return transType;
	}

	public void setTransType(Integer transType) {
		this.transType = transType;
	}

	public BigDecimal getAmt() {
		return amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	public Date getTransDatetime() {
		return transDatetime;
	}

	public void setTransDatetime(Date transDatetime) {
		this.transDatetime = transDatetime;
	}

	public BigDecimal getBrokerage() {
		return brokerage;
	}

	public void setBrokerage(BigDecimal brokerage) {
		this.brokerage = brokerage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getBrokerageId() {
		return brokerageId;
	}

	public void setBrokerageId(Long brokerageId) {
		this.brokerageId = brokerageId;
	}

	public String getBrokerageUserName() {
		return brokerageUserName;
	}

	public void setBrokerageUserName(String brokerageUserName) {
		this.brokerageUserName = brokerageUserName;
	}

	public String getBrokerageUserCode() {
		return brokerageUserCode;
	}

	public void setBrokerageUserCode(String brokerageUserCode) {
		this.brokerageUserCode = brokerageUserCode;
	}

	public Integer getBrokerageUserType() {
		return brokerageUserType;
	}

	public void setBrokerageUserType(Integer brokerageUserType) {
		this.brokerageUserType = brokerageUserType;
	}

	public Long getBrokerageUserId() {
		return brokerageUserId;
	}

	public void setBrokerageUserId(Long brokerageUserId) {
		this.brokerageUserId = brokerageUserId;
	}

	public Double getBrokerageUserRate() {
		return brokerageUserRate;
	}

	public void setBrokerageUserRate(Double brokerageUserRate) {
		this.brokerageUserRate = brokerageUserRate;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public Integer getBrokerageType() {
		return brokerageType;
	}

	public void setBrokerageType(Integer brokerageType) {
		this.brokerageType = brokerageType;
	}

	public Date getCreateDatetimeStart() {
		return createDatetimeStart;
	}

	public void setCreateDatetimeStart(Date createDatetimeStart) {
		this.createDatetimeStart = createDatetimeStart;
	}

	public Date getCreateDatetimeEnd() {
		return createDatetimeEnd;
	}

	public void setCreateDatetimeEnd(Date createDatetimeEnd) {
		this.createDatetimeEnd = createDatetimeEnd;
	};


}