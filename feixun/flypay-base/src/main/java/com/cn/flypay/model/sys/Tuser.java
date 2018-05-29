package com.cn.flypay.model.sys;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "sys_user")
@DynamicInsert(true)
@DynamicUpdate(true)
public class Tuser implements java.io.Serializable {

	private static final long serialVersionUID = 1867623281523381449L;

	private Long id;
	private Long version;

	private String loginName; // 登录名
	private String password; // 密码
	private String name; // 姓名
	private Integer isAdmin; // 年龄
	private Date createDatetime; // 创建时间
	private Integer userType; // 用户类型
	private Integer isDefault; // 是否默认
	private Integer state; // 状态
	private String phone;
	private Torganization organization;// 所在机构
	private Set<Trole> roles = new HashSet<Trole>(0);

	private Tuser parentUser;// 推荐人
	private Date lastDateTime;// 最近登录日期

	private String code;
	private String stmPsw;
	private String iconPath;
	private String country;
	private String province;

	private String realName;
	private String idNo;
	private String city;
	private Integer authenticationStatus;

	private String speechType;
	private Integer settlementStatus;
	private Integer authErrorNum;
	private Integer authCardErrorNum;
	private Integer loginErrorNum;
	private Integer isChnl;

	private TuserSettlementConfig settlementConfig;

	private String merchantName;
	private String merchantShortName;
	private String merchantAuthMsg;
	private String address;
	// private String isShenfuD0Open;
	// private String shenfuD0Msg;
	private String merchantCity;

	private String agentId;
	private Long pid;

	/**
	 * 商户类型 0 非商户 1 普通商户
	 * 
	 * 90待认证 91认证失败
	 * 
	 */
	private Integer merchantType;
	/**
	 * 0 无声音，1铃声，2语音，3智能语音
	 */
	private Integer voiceType;

	private Date diamondDateTime;// 升级钻石日期
	private Date goldDateTime;// 升级金牌日期
	private Date authDateTime;
	private Integer blackStatus;// 黑名单状态

	private String openId;
	private String nickname;
	private String unionid;

	private Integer refreSum1;
	private Integer refreSum2;
	private Integer refreSum3;
	private Integer refreType;
	private Integer privacyType;// 隐私开关
	private Integer loanType;// 一键代还

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
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

	@Column(name = "LOGIN_NAME", length = 512)
	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Column(name = "merchant_type")
	public Integer getMerchantType() {
		return merchantType;
	}

	public void setMerchantType(Integer merchantType) {
		this.merchantType = merchantType;
	}

	@Column(name = "LOGIN_ERROR_NUM")
	public Integer getLoginErrorNum() {
		return loginErrorNum;
	}

	public void setLoginErrorNum(Integer loginErrorNum) {
		this.loginErrorNum = loginErrorNum;
	}

	@Column(name = "NAME", length = 512)
	public String getName() {
		return this.name;
	}

	@Column(name = "IP", length = 128)
	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "PASSWORD", length = 512)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "IS_ADMIN", nullable = false, precision = 4, scale = 0)
	public Integer getIsAdmin() {
		return this.isAdmin;
	}

	public void setIsAdmin(Integer isAdmin) {
		this.isAdmin = isAdmin;
	}

	@Column(name = "USER_TYPE", nullable = false)
	public Integer getUserType() {
		return this.userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	@Column(name = "IS_DEFAULT", nullable = false, precision = 4, scale = 0)
	public Integer getIsDefault() {
		return this.isDefault;
	}

	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
	}

	@Column(name = "STATE", nullable = false, precision = 4, scale = 0)
	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Column(name = "CREATE_DATETIME")
	public Date getCreateDatetime() {
		return this.createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	@Column(name = "diamond_Date_Time")
	public Date getDiamondDateTime() {
		return diamondDateTime;
	}

	public void setDiamondDateTime(Date diamondDateTime) {
		this.diamondDateTime = diamondDateTime;
	}

	@Column(name = "gold_Date_Time")
	public Date getGoldDateTime() {
		return goldDateTime;
	}

	public void setGoldDateTime(Date goldDateTime) {
		this.goldDateTime = goldDateTime;
	}

	@Column(name = "PHONE", length = 80)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "LAST_DATETIME")
	public Date getLastDateTime() {
		return this.lastDateTime;
	}

	public void setLastDateTime(Date lastDateTime) {
		this.lastDateTime = lastDateTime;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "sys_user_role", joinColumns = { @JoinColumn(name = "user_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "role_id", nullable = false, updatable = false) })
	public Set<Trole> getRoles() {
		return roles;
	}

	public void setRoles(Set<Trole> roles) {
		this.roles = roles;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	public Torganization getOrganization() {
		return organization;
	}

	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pid")
	public Tuser getParentUser() {
		return this.parentUser;
	}

	public void setParentUser(Tuser parentUser) {
		this.parentUser = parentUser;
	}

	@Column(name = "CODE", nullable = false)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "STM_PSW")
	public String getStmPsw() {
		return stmPsw;
	}

	public void setStmPsw(String stmPsw) {
		this.stmPsw = stmPsw;
	}

	@Column(name = "ICON_PATH")
	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	@Column(name = "country")
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "province")
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	@Column(name = "real_name", length = 256)
	public String getRealName() {
		return this.realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	@Column(name = "id_no")
	public String getIdNo() {
		return this.idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	@Column(name = "authentication_status")
	public Integer getAuthenticationStatus() {
		return this.authenticationStatus;
	}

	public void setAuthenticationStatus(Integer authenticationStatus) {
		this.authenticationStatus = authenticationStatus;
	}

	@Column(name = "speechType")
	public String getSpeechType() {
		return speechType;
	}

	public void setSpeechType(String speechType) {
		this.speechType = speechType;
	}

	@Column(name = "settlement_status")
	public Integer getSettlementStatus() {
		return this.settlementStatus;
	}

	public void setSettlementStatus(Integer settlementStatus) {
		this.settlementStatus = settlementStatus;
	}

	@Column(name = "auth_error_num")
	public Integer getAuthErrorNum() {
		return authErrorNum;
	}

	public void setAuthErrorNum(Integer authErrorNum) {
		this.authErrorNum = authErrorNum;
	}

	@Column(name = "auth_card_error_num")
	public Integer getAuthCardErrorNum() {
		return authCardErrorNum;
	}

	public void setAuthCardErrorNum(Integer authCardErrorNum) {
		this.authCardErrorNum = authCardErrorNum;
	}

	@Column(name = "isChnl")
	public Integer getIsChnl() {
		return this.isChnl;
	}

	public void setIsChnl(Integer isChnl) {
		this.isChnl = isChnl;
	}

	@Column(name = "CITY", length = 30)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "merchant_Name", length = 256)
	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	@Column(name = "merchant_City", length = 256)
	public String getMerchantCity() {
		return merchantCity;
	}

	public void setMerchantCity(String merchantCity) {
		this.merchantCity = merchantCity;
	}

	@Column(name = "agent_id", length = 256)
	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	@Column(name = "merchant_short_name", length = 128)
	public String getMerchantShortName() {
		return merchantShortName;
	}

	public void setMerchantShortName(String merchantShortName) {
		this.merchantShortName = merchantShortName;
	}

	@Column(name = "merchant_auth_msg", length = 128)
	public String getMerchantAuthMsg() {
		return merchantAuthMsg;
	}

	public void setMerchantAuthMsg(String merchantAuthMsg) {
		this.merchantAuthMsg = merchantAuthMsg;
	}

	@Column(name = "address", length = 1024)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	//
	// @Column(name = "is_shenfu_d0_open", length = 8)
	// public String getIsShenfuD0Open() {
	// return isShenfuD0Open;
	// }
	//
	// public void setIsShenfuD0Open(String isShenfuD0Open) {
	// this.isShenfuD0Open = isShenfuD0Open;
	// }
	//
	// @Column(name = "shenfu_d0_msg", length = 2047)
	// public String getShenfuD0Msg() {
	// return shenfuD0Msg;
	// }
	//
	// public void setShenfuD0Msg(String shenfuD0Msg) {
	// this.shenfuD0Msg = shenfuD0Msg;
	// }

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "slt_config_id")
	public TuserSettlementConfig getSettlementConfig() {
		return settlementConfig;
	}

	public void setSettlementConfig(TuserSettlementConfig settlementConfig) {
		this.settlementConfig = settlementConfig;
	}

	@Column(name = "voice_type")
	public Integer getVoiceType() {
		return voiceType;
	}

	public void setVoiceType(Integer voiceType) {
		this.voiceType = voiceType;
	}

	@Column(name = "auth_Date_Time")
	public Date getAuthDateTime() {
		return authDateTime;
	}

	public void setAuthDateTime(Date authDateTime) {
		this.authDateTime = authDateTime;
	}

	@Column(name = "PID")
	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	@Column(name = "black_status")
	public Integer getBlackStatus() {
		return blackStatus;
	}

	public void setBlackStatus(Integer blackStatus) {
		this.blackStatus = blackStatus;
	}

	@Column(name = "openId")
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	@Column(name = "nickname")
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Column(name = "unionid")
	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	@Column(name = "refreSum1", nullable = false, precision = 4, scale = 0)
	public Integer getRefreSum1() {
		return refreSum1;
	}

	public void setRefreSum1(Integer refreSum1) {
		this.refreSum1 = refreSum1;
	}

	@Column(name = "refreSum2", nullable = false, precision = 4, scale = 0)
	public Integer getRefreSum2() {
		return refreSum2;
	}

	public void setRefreSum2(Integer refreSum2) {
		this.refreSum2 = refreSum2;
	}

	@Column(name = "refreSum3", nullable = false, precision = 4, scale = 0)
	public Integer getRefreSum3() {
		return refreSum3;
	}

	public void setRefreSum3(Integer refreSum3) {
		this.refreSum3 = refreSum3;
	}

	@Column(name = "refreType", nullable = false, precision = 4, scale = 0)
	public Integer getRefreType() {
		return refreType;
	}

	public void setRefreType(Integer refreType) {
		this.refreType = refreType;
	}

	@Column(name = "privacyType", columnDefinition = "INT default 1")
	public Integer getPrivacyType() {
		return privacyType;
	}

	public void setPrivacyType(Integer privacyType) {
		this.privacyType = privacyType;
	}

	@Column(name = "LoanType", columnDefinition = "INT default 1")
	public Integer getLoanType() {
		return loanType;
	}

	public void setLoanType(Integer loanType) {
		this.loanType = loanType;
	}

}