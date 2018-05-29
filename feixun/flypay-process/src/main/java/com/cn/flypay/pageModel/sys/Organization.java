package com.cn.flypay.pageModel.sys;

import java.math.BigDecimal;
import java.util.Date;

public class Organization implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1003L;
	private Long id;
	private Date createDatetime;
	private String name;
	private String appName;
	private String address;
	private String code;
	private String iconCls;
	private String icon;
	private Integer seq;
	private Long pid;
	private String pname;

	private BigDecimal principalRate;// 本机构保本费率
	private BigDecimal rate;// 上级机构分发的费率
	private BigDecimal principalAgentRate;
	private Long diamondAgent;
	private Long goldAgent;
	private Long silverAgent;
	private Long version;

	private Integer status;
	private String country;
	private String province;

	private BigDecimal t0Fee;
	private BigDecimal t1Fee;
	private BigDecimal minT0Amt;
	private BigDecimal maxT0Amt;
	private BigDecimal minT1Amt;
	private BigDecimal maxT1Amt;
	private BigDecimal rabaleFee;
	private BigDecimal maxRabaleAmt;
	private BigDecimal minRabaleAmt;
	private BigDecimal maxTodayOutAmt;

	/**
	 * 运营商类型 0 OEM 1 运营商 2代理商 100平台
	 */
	private Integer agentType;
	private Integer agentLevel;
	/* 购买金牌，给运营商的手续费，若为-1 表示全部给运营商 */
	private Integer goldFee;
	/* 购买钻石，给运营商的手续费，若为-1 表示全部给运营商 */
	private Integer diamondFee;

	/**
	 * 提现预留手续费
	 */
	private BigDecimal fee;
	private BigDecimal defaultInputFee;
	private BigDecimal defaultInputDiamondRate;
	private BigDecimal defaultInputGoldRate;
	private BigDecimal defaultShareFee;
	/**
	 * 积分降费率 0 不使用 1使用
	 */
	private Integer pointType;
	/**
	 * 是否使用1级用户人数将费率， 若0 不降低费率 ； 1 用户升级降费率 ；2 积分降费率
	 */
	private Integer reductionUserRateType;
	/**
	 * 0不分润；1 固定比例分润；2 代理固定比例，流量交易差
	 */
	private Integer shareBonusType;
	/**
	 * 0表示流量代理都为3级，1表示代理三级流量两级，2表示流量2级代理两级
	 */
	private Integer shareBonusLevelType;
	/**
	 * ' 1 购买代理升级，2 推广用户升级，100 先到者优先'
	 */
	private Integer userUpgradeType;
	private Integer diamondNum;
	private Integer goldNum;

	private User operateUser;
	/**
	 * 分佣账户手机号码
	 */
	private String userPhone;
	
	/**
	 * 代理商等级 (默认为0，  1，运营商   2，OEM 3，一级 4，二级  5，三级)
	 */
	private String orgType;	

	// Fields
	public enum reduction_user_rate_type {

		/**
		 * 费率不变
		 */
		NONE(0),
		/**
		 * 积分降费率
		 */
		POINT_SHOPPING(1),
		/**
		 * 用户升级降费率
		 */
		USER_UPGRADE(2);

		private reduction_user_rate_type(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	// Fields
	public enum user_upgrade_type {
		/**
		 * 购买代理升级
		 */
		SHOPPING(1),
		/**
		 * 推广用户升级
		 */
		POPULARIZE(2),
		/**
		 * 先到者优先
		 */
		FIRST_IN_FIRST_OUT(100);

		private user_upgrade_type(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	// Fields
	public enum agent_type {
		/**
		 * OEM
		 */
		OEM(0),
		/**
		 * 运营商
		 */
		SERVICE_PROVIDER(1),
		/**
		 * 代理商
		 */
		AGENT(2),
		/**
		 * 平台
		 */
		PLATFORM(100);

		private agent_type(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Integer getReductionUserRateType() {
		return reductionUserRateType;
	}

	public void setReductionUserRateType(Integer reductionUserRateType) {
		this.reductionUserRateType = reductionUserRateType;
	}

	public Long getId() {
		return id;
	}

	public Integer getPointType() {
		return pointType;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public Integer getShareBonusLevelType() {
		return shareBonusLevelType;
	}

	public void setShareBonusLevelType(Integer shareBonusLevelType) {
		this.shareBonusLevelType = shareBonusLevelType;
	}

	public Integer getShareBonusType() {
		return shareBonusType;
	}

	public void setShareBonusType(Integer shareBonusType) {
		this.shareBonusType = shareBonusType;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public void setPointType(Integer pointType) {
		this.pointType = pointType;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getMaxTodayOutAmt() {
		return maxTodayOutAmt;
	}

	public void setMaxTodayOutAmt(BigDecimal maxTodayOutAmt) {
		this.maxTodayOutAmt = maxTodayOutAmt;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public BigDecimal getDefaultShareFee() {
		return defaultShareFee;
	}

	public void setDefaultShareFee(BigDecimal defaultShareFee) {
		this.defaultShareFee = defaultShareFee;
	}

	public String getCode() {
		return this.code;
	}

	public BigDecimal getPrincipalAgentRate() {
		return principalAgentRate;
	}

	public void setPrincipalAgentRate(BigDecimal principalAgentRate) {
		this.principalAgentRate = principalAgentRate;
	}

	public String getCountry() {
		return country;
	}

	public Integer getGoldFee() {
		return goldFee;
	}

	public void setGoldFee(Integer goldFee) {
		this.goldFee = goldFee;
	}

	public Integer getDiamondNum() {
		return diamondNum;
	}

	public void setDiamondNum(Integer diamondNum) {
		this.diamondNum = diamondNum;
	}

	public Integer getGoldNum() {
		return goldNum;
	}

	public void setGoldNum(Integer goldNum) {
		this.goldNum = goldNum;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public Integer getDiamondFee() {
		return diamondFee;
	}

	public void setDiamondFee(Integer diamondFee) {
		this.diamondFee = diamondFee;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIconCls() {
		return iconCls;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getAgentType() {
		return agentType;
	}

	public void setAgentType(Integer agentType) {
		this.agentType = agentType;
	}

	public Integer getAgentLevel() {
		return agentLevel;
	}

	public void setAgentLevel(Integer agentLevel) {
		this.agentLevel = agentLevel;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	public BigDecimal getPrincipalRate() {
		return principalRate;
	}

	public void setPrincipalRate(BigDecimal principalRate) {
		this.principalRate = principalRate;
	}
	
	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getDiamondAgent() {
		return diamondAgent;
	}

	public void setDiamondAgent(Long diamondAgent) {
		this.diamondAgent = diamondAgent;
	}

	public Long getGoldAgent() {
		return goldAgent;
	}

	public void setGoldAgent(Long goldAgent) {
		this.goldAgent = goldAgent;
	}

	public Long getSilverAgent() {
		return silverAgent;
	}

	public void setSilverAgent(Long silverAgent) {
		this.silverAgent = silverAgent;
	}

	public BigDecimal getT0Fee() {
		return t0Fee;
	}

	public void setT0Fee(BigDecimal t0Fee) {
		this.t0Fee = t0Fee;
	}

	public BigDecimal getT1Fee() {
		return t1Fee;
	}

	public void setT1Fee(BigDecimal t1Fee) {
		this.t1Fee = t1Fee;
	}

	public BigDecimal getMinT0Amt() {
		return minT0Amt;
	}

	public void setMinT0Amt(BigDecimal minT0Amt) {
		this.minT0Amt = minT0Amt;
	}

	public BigDecimal getMaxT0Amt() {
		return maxT0Amt;
	}

	public void setMaxT0Amt(BigDecimal maxT0Amt) {
		this.maxT0Amt = maxT0Amt;
	}

	public BigDecimal getMinT1Amt() {
		return minT1Amt;
	}

	public void setMinT1Amt(BigDecimal minT1Amt) {
		this.minT1Amt = minT1Amt;
	}

	public BigDecimal getMaxT1Amt() {
		return maxT1Amt;
	}

	public void setMaxT1Amt(BigDecimal maxT1Amt) {
		this.maxT1Amt = maxT1Amt;
	}

	public BigDecimal getRabaleFee() {
		return rabaleFee;
	}

	public void setRabaleFee(BigDecimal rabaleFee) {
		this.rabaleFee = rabaleFee;
	}

	public BigDecimal getMaxRabaleAmt() {
		return maxRabaleAmt;
	}

	public void setMaxRabaleAmt(BigDecimal maxRabaleAmt) {
		this.maxRabaleAmt = maxRabaleAmt;
	}

	public BigDecimal getMinRabaleAmt() {
		return minRabaleAmt;
	}

	public void setMinRabaleAmt(BigDecimal minRabaleAmt) {
		this.minRabaleAmt = minRabaleAmt;
	}

	public BigDecimal getDefaultInputFee() {
		return defaultInputFee;
	}

	public void setDefaultInputFee(BigDecimal defaultInputFee) {
		this.defaultInputFee = defaultInputFee;
	}

	public BigDecimal getDefaultInputDiamondRate() {
		return defaultInputDiamondRate;
	}

	public void setDefaultInputDiamondRate(BigDecimal defaultInputDiamondRate) {
		this.defaultInputDiamondRate = defaultInputDiamondRate;
	}

	public BigDecimal getDefaultInputGoldRate() {
		return defaultInputGoldRate;
	}

	public void setDefaultInputGoldRate(BigDecimal defaultInputGoldRate) {
		this.defaultInputGoldRate = defaultInputGoldRate;
	}

	public Integer getUserUpgradeType() {
		return userUpgradeType;
	}

	public void setUserUpgradeType(Integer userUpgradeType) {
		this.userUpgradeType = userUpgradeType;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
	
}
