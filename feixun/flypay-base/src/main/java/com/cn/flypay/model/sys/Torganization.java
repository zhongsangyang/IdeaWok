package com.cn.flypay.model.sys;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "sys_organization")
@DynamicInsert(true)
@DynamicUpdate(true)
public class Torganization implements java.io.Serializable {

	private static final long serialVersionUID = 1006L;
	private Date createDatetime;
	private String name;
	private String appName;
	private String address;
	private String code;
	private String icon;
	private Integer seq;
	private Torganization organization;

	private BigDecimal principalRate;// 本机构保本费率
	private BigDecimal principalAgentRate;// 本机构保本费率
	private BigDecimal rate;// 上级机构分发的费率
	private Long diamondAgent;
	private Long goldAgent;
	private Long silverAgent;
	private Integer status;
	private Long id;
	private Long version;
	private String country;
	private String province;
	private String city;

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

	private Integer agentType;
	private Integer agentLevel;
	/* 购买金牌，给运营商的手续费，若为-1 表示全部给运营商 */
	private Integer goldFee;
	/* 购买钻石，给运营商的手续费，若为-1 表示全部给运营商 */
	private Integer diamondFee;

	private BigDecimal fee;

	/**
	 * 默认刷卡的手续费率
	 */
	private BigDecimal defaultInputFee;
	/**
	 * 默认钻石刷卡的手续费率
	 */
	private BigDecimal defaultInputDiamondRate;
	/**
	 * 默认金牌刷卡的手续费率
	 */
	private BigDecimal defaultInputGoldRate;
	/**
	 * 默认刷卡的分润费率
	 */
	private BigDecimal defaultShareFee;

	/**
	 * 是否使用积分制， 若0 不适用 ，1 使用
	 * 
	 * @see 若用户使用积分制，可以通过积分降低各种费率
	 * @see 若不适用积分制，用户输入的费率为运营商默认费率
	 */
	private Integer pointType;

	/**
	 * 是否使用1级用户人数将费率， 若0 不降低费率 ；1积分降费率； 2 用户升级降费率
	 */
	private Integer reductionUserRateType;
	/**
	 * 0不分润；1 固定比例分润；2 代理固定比例，流量交易差
	 */
	private Integer shareBonusType;

	private Integer shareBonusLevelType;

	private Integer diamondNum;
	private Integer goldNum;
	/**
	 * ' 1 购买代理升级，2 推广用户升级，100 先导者优先'
	 */
	private Integer userUpgradeType;
	/**
	 * 分佣账户手机号码
	 */
	private String userPhone;
	
	/**
	 * 代理商等级 (默认为0，  1，运营商   2，OEM 3，一级 4，二级  5，三级)
	 */
	private String orgType;

	@Id
	@GenericGenerator(name = "generator", strategy = "increment")
	@GeneratedValue(generator = "generator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false, precision = 10, scale = 0)
	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Version
	@Column(name = "app_Name")
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pid")
	/*@NotFound(action=NotFoundAction.IGNORE)*/
	public Torganization getOrganization() {
		return organization;
	}

	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_datetime", length = 19)
	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	@NotBlank
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

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getCountry() {
		return country;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "principal_Rate", length = 19)
	public BigDecimal getPrincipalRate() {
		return principalRate;
	}

	public void setPrincipalRate(BigDecimal principalRate) {
		this.principalRate = principalRate;
	}

	@Column(name = "principal_Agent_Rate", length = 19)
	public BigDecimal getPrincipalAgentRate() {
		return principalAgentRate;
	}

	public void setPrincipalAgentRate(BigDecimal principalAgentRate) {
		this.principalAgentRate = principalAgentRate;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	@Column(name = "A_Agent")
	public Long getDiamondAgent() {
		return diamondAgent;
	}

	public void setDiamondAgent(Long diamondAgent) {
		this.diamondAgent = diamondAgent;
	}

	@Column(name = "B_Agent")
	public Long getGoldAgent() {
		return goldAgent;
	}

	public void setGoldAgent(Long goldAgent) {
		this.goldAgent = goldAgent;
	}

	@Column(name = "C_Agent")
	public Long getSilverAgent() {
		return silverAgent;
	}

	public void setSilverAgent(Long silverAgent) {
		this.silverAgent = silverAgent;
	}

	@Column(name = "t0_fee", precision = 12)
	public BigDecimal getT0Fee() {
		return this.t0Fee;
	}

	public void setT0Fee(BigDecimal t0Fee) {
		this.t0Fee = t0Fee;
	}

	@Column(name = "t1_fee", precision = 12)
	public BigDecimal getT1Fee() {
		return this.t1Fee;
	}

	public void setT1Fee(BigDecimal t1Fee) {
		this.t1Fee = t1Fee;
	}

	@Column(name = "min_t0_amt", precision = 12)
	public BigDecimal getMinT0Amt() {
		return this.minT0Amt;
	}

	public void setMinT0Amt(BigDecimal minT0Amt) {
		this.minT0Amt = minT0Amt;
	}

	@Column(name = "max_t0_amt", precision = 12)
	public BigDecimal getMaxT0Amt() {
		return this.maxT0Amt;
	}

	public void setMaxT0Amt(BigDecimal maxT0Amt) {
		this.maxT0Amt = maxT0Amt;
	}

	@Column(name = "min_t1_amt", precision = 12)
	public BigDecimal getMinT1Amt() {
		return this.minT1Amt;
	}

	public void setMinT1Amt(BigDecimal minT1Amt) {
		this.minT1Amt = minT1Amt;
	}

	@Column(name = "max_t1_amt", precision = 12)
	public BigDecimal getMaxT1Amt() {
		return this.maxT1Amt;
	}

	public void setMaxT1Amt(BigDecimal maxT1Amt) {
		this.maxT1Amt = maxT1Amt;
	}

	@Column(name = "rabale_fee", precision = 12)
	public BigDecimal getRabaleFee() {
		return this.rabaleFee;
	}

	public void setRabaleFee(BigDecimal rabaleFee) {
		this.rabaleFee = rabaleFee;
	}

	@Column(name = "max_rabale_amt", precision = 12)
	public BigDecimal getMaxRabaleAmt() {
		return this.maxRabaleAmt;
	}

	public void setMaxRabaleAmt(BigDecimal maxRabaleAmt) {
		this.maxRabaleAmt = maxRabaleAmt;
	}

	@Column(name = "min_rabale_amt", precision = 12)
	public BigDecimal getMinRabaleAmt() {
		return this.minRabaleAmt;
	}

	public void setMinRabaleAmt(BigDecimal minRabaleAmt) {
		this.minRabaleAmt = minRabaleAmt;
	}

	@Column(name = "max_Today_Out_Amt", precision = 12)
	public BigDecimal getMaxTodayOutAmt() {
		return maxTodayOutAmt;
	}

	public void setMaxTodayOutAmt(BigDecimal maxTodayOutAmt) {
		this.maxTodayOutAmt = maxTodayOutAmt;
	}

	@Column(name = "fee", precision = 12)
	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	@Column(name = "agent_Type")
	public Integer getAgentType() {
		return agentType;
	}

	public void setAgentType(Integer agentType) {
		this.agentType = agentType;
	}

	@Column(name = "agent_Level")
	public Integer getAgentLevel() {
		return agentLevel;
	}

	public void setAgentLevel(Integer agentLevel) {
		this.agentLevel = agentLevel;
	}

	@Column(name = "gold_fee")
	public Integer getGoldFee() {
		return goldFee;
	}

	public void setGoldFee(Integer goldFee) {
		this.goldFee = goldFee;
	}

	@Column(name = "diamond_fee")
	public Integer getDiamondFee() {
		return diamondFee;
	}

	public void setDiamondFee(Integer diamondFee) {
		this.diamondFee = diamondFee;
	}

	@Column(name = "default_input_fee")
	public BigDecimal getDefaultInputFee() {
		return defaultInputFee;
	}

	public void setDefaultInputFee(BigDecimal defaultInputFee) {
		this.defaultInputFee = defaultInputFee;
	}

	@Column(name = "default_input_diamond_rate")
	public BigDecimal getDefaultInputDiamondRate() {
		return defaultInputDiamondRate;
	}

	public void setDefaultInputDiamondRate(BigDecimal defaultInputDiamondRate) {
		this.defaultInputDiamondRate = defaultInputDiamondRate;
	}

	@Column(name = "default_input_gold_rate")
	public BigDecimal getDefaultInputGoldRate() {
		return defaultInputGoldRate;
	}

	public void setDefaultInputGoldRate(BigDecimal defaultInputGoldRate) {
		this.defaultInputGoldRate = defaultInputGoldRate;
	}

	@Column(name = "default_share_rate")
	public BigDecimal getDefaultShareFee() {
		return defaultShareFee;
	}

	public void setDefaultShareFee(BigDecimal defaultShareFee) {
		this.defaultShareFee = defaultShareFee;
	}

	@Column(name = "point_Type")
	public Integer getPointType() {
		return pointType;
	}

	public void setPointType(Integer pointType) {
		this.pointType = pointType;
	}

	@Column(name = "reduction_user_rate_type")
	public Integer getReductionUserRateType() {
		return reductionUserRateType;
	}

	public void setReductionUserRateType(Integer reductionUserRateType) {
		this.reductionUserRateType = reductionUserRateType;
	}

	@Column(name = "share_bonus_type")
	public Integer getShareBonusType() {
		return shareBonusType;
	}

	public void setShareBonusType(Integer shareBonusType) {
		this.shareBonusType = shareBonusType;
	}

	@Column(name = "share_bonus_level_type")
	public Integer getShareBonusLevelType() {
		return shareBonusLevelType;
	}

	public void setShareBonusLevelType(Integer shareBonusLevelType) {
		this.shareBonusLevelType = shareBonusLevelType;
	}

	@Column(name = "diamond_num")
	public Integer getDiamondNum() {
		return diamondNum;
	}

	public void setDiamondNum(Integer diamondNum) {
		this.diamondNum = diamondNum;
	}

	@Column(name = "gold_num")
	public Integer getGoldNum() {
		return goldNum;
	}

	public void setGoldNum(Integer goldNum) {
		this.goldNum = goldNum;
	}

	@Column(name = "user_upgrade_type")
	public Integer getUserUpgradeType() {
		return userUpgradeType;
	}

	public void setUserUpgradeType(Integer userUpgradeType) {
		this.userUpgradeType = userUpgradeType;
	}
    
	@Column(name = "user_phone")
	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	@Column(name = "org_type")
	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
	
	

}
