package com.cn.flypay.pageModel.sys;

import java.util.Date;

public class User implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1000001L;

	private String loginName; // 登录名
	private Date createDatetime; // 创建时间
	private Integer userType; // 用户类型
	private Integer isDefault; // 是否默认

	private Long version;

	@Override
	public String toString() {
		return "User [loginName=" + loginName + ", createDatetime=" + createDatetime + ", userType=" + userType
				+ ", id=" + id + ", password=" + password + ", name=" + name + ", roleIds=" + roleIds + ", roleNames="
				+ roleNames + "]";
	}

	private Long id;
	private String password; // 密码
	private String name; // 姓名
	private Integer isAdmin; // 年龄
	/** 0 用户正常，1 用户停用 */
	private Integer state; // 状态
	private String phone;

	private Long organizationId;
	private String organizationName;
	private String organizationAppName;
	/* 运营商编号 */
	private String agentId;

	private String roleIds;
	private String roleNames;

	private String createdatetimeStart;
	private String createdatetimeEnd;

	private Long pid;// 推荐人
	private String pcode;// 父推荐人code

	private String parentName;
	private String path;// 推荐路径
	private Date lastDateTime;// 最近登录日期

	private String code;
	private String stmPsw;
	private String iconPath;
	private String country;
	private String province;

	private String realName;
	private String idNo;
	private String city;

	private String speechType;

	/**
	 * -1未认证，0认证失败，1已认证，2认证中
	 */
	private Integer authenticationStatus;

	private String authErrorInfo;
	private Integer settlementStatus;
	private Integer loginErrorNum;
	private Integer authErrorNum;
	private Integer authCardErrorNum;
	private Integer isChnl;

	private String merchantName;
	private String merchantCity;
	private User operateUser;
	/**
	 * 商户类型 0 非商户 1 普通商户
	 * 
	 * 90待认证 91认证失败
	 * 
	 */
	private Integer merchantType;
	private String merchantShortName;
	private String merchantAuthMsg;
	private String address;
	private String isShenfuD0Open;// 0未开通 1开通成功 9开通失败 开通申孚D0
	private String shenfuD0Msg;// 开通申孚D0反馈
	/**
	 * 0 无声音 1 “叮咚”的声音 2 “收款成功”的语音 3 智能播报（暂时不启用）
	 */
	private Integer voiceType;

	private Date diamondDateTime;// 升级钻石日期
	private Date goldDateTime;// 升级金牌日期
	private Date authDateTime;
	private Integer blackStatus;// 黑名单状态
	/**
	 * 下线用户层级
	 */
	private Integer subLevel;
	private Integer userUpgradeType;//若是代理:升级代理的方式 1购买 3手动
	private String openId;
	private String nickname;
	private String unionid;

	private Integer privacyType;// 隐私开关
	private Integer loanType;// 一键代还

	// Fields
	public enum merchant_type {
		/**
		 * 普通用户
		 */
		PERSON(0),
		/**
		 * 有营业执照商户
		 */
		REAL_MERCHANT(1),
		/**
		 * 无营业执照 商户
		 */
		NONE_MERCHANT(10),
		/**
		 * 有营业执照 待认证
		 */
		WAITING_REAL(90),
		/**
		 * 无营业执照 待认证
		 */
		WAITING_NONE(900),
		/**
		 * 认证失败
		 */
		FAILURE(91);
		private merchant_type(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	// Fields
	public enum authentication_status {
		/**
		 * 未认证
		 */
		INIT(-1),
		/**
		 * 已认证
		 */
		SUCCESS(1),
		/**
		 * 认证失败
		 */
		FAILURE(0),
		/**
		 * 认证中
		 */
		PROCESSING(2);
		private authentication_status(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	public String getLoginName() {
		return loginName;
	}

	public String getPcode() {
		return pcode;
	}

	public Integer getLoginErrorNum() {
		return loginErrorNum;
	}

	public void setLoginErrorNum(Integer loginErrorNum) {
		this.loginErrorNum = loginErrorNum;
	}

	public Integer getSubLevel() {
		return subLevel;
	}

	public void setSubLevel(Integer subLevel) {
		this.subLevel = subLevel;
	}

	public Integer getUserUpgradeType() {
		return userUpgradeType;
	}

	public void setUserUpgradeType(Integer userUpgradeType) {
		this.userUpgradeType = userUpgradeType;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Integer getAuthErrorNum() {
		return authErrorNum;
	}

	public Integer getMerchantType() {
		return merchantType;
	}

	public String getMerchantShortName() {
		return merchantShortName;
	}

	public void setMerchantShortName(String merchantShortName) {
		this.merchantShortName = merchantShortName;
	}

	public String getMerchantAuthMsg() {
		return merchantAuthMsg;
	}

	public void setMerchantAuthMsg(String merchantAuthMsg) {
		this.merchantAuthMsg = merchantAuthMsg;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIsShenfuD0Open() {
		return isShenfuD0Open;
	}

	public void setIsShenfuD0Open(String isShenfuD0Open) {
		this.isShenfuD0Open = isShenfuD0Open;
	}

	public String getShenfuD0Msg() {
		return shenfuD0Msg;
	}

	public void setShenfuD0Msg(String shenfuD0Msg) {
		this.shenfuD0Msg = shenfuD0Msg;
	}

	public void setMerchantType(Integer merchantType) {
		this.merchantType = merchantType;
	}

	public void setAuthErrorNum(Integer authErrorNum) {
		this.authErrorNum = authErrorNum;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public String getParentName() {
		return parentName;
	}

	public String getAuthErrorInfo() {
		return authErrorInfo;
	}

	public void setAuthErrorInfo(String authErrorInfo) {
		this.authErrorInfo = authErrorInfo;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantCity() {
		return merchantCity;
	}

	public void setMerchantCity(String merchantCity) {
		this.merchantCity = merchantCity;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Integer getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Integer isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

	public String getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}

	public String getCreatedatetimeStart() {
		return createdatetimeStart;
	}

	public void setCreatedatetimeStart(String createdatetimeStart) {
		this.createdatetimeStart = createdatetimeStart;
	}

	public String getCreatedatetimeEnd() {
		return createdatetimeEnd;
	}

	public void setCreatedatetimeEnd(String createdatetimeEnd) {
		this.createdatetimeEnd = createdatetimeEnd;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getLastDateTime() {
		return lastDateTime;
	}

	public void setLastDateTime(Date lastDateTime) {
		this.lastDateTime = lastDateTime;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStmPsw() {
		return stmPsw;
	}

	public void setStmPsw(String stmPsw) {
		this.stmPsw = stmPsw;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
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

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSpeechType() {
		return speechType;
	}

	public void setSpeechType(String speechType) {
		this.speechType = speechType;
	}

	public Integer getAuthenticationStatus() {
		return authenticationStatus;
	}

	public void setAuthenticationStatus(Integer authenticationStatus) {
		this.authenticationStatus = authenticationStatus;
	}

	public Integer getSettlementStatus() {
		return settlementStatus;
	}

	public String getOrganizationAppName() {
		return organizationAppName;
	}

	public void setOrganizationAppName(String organizationAppName) {
		this.organizationAppName = organizationAppName;
	}

	public void setSettlementStatus(Integer settlementStatus) {
		this.settlementStatus = settlementStatus;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public Integer getIsChnl() {
		return isChnl;
	}

	public Integer getAuthCardErrorNum() {
		return authCardErrorNum;
	}

	public void setAuthCardErrorNum(Integer authCardErrorNum) {
		this.authCardErrorNum = authCardErrorNum;
	}

	public void setIsChnl(Integer isChnl) {
		this.isChnl = isChnl;
	}

	public Date getDiamondDateTime() {
		return diamondDateTime;
	}

	public void setDiamondDateTime(Date diamondDateTime) {
		this.diamondDateTime = diamondDateTime;
	}

	public Date getGoldDateTime() {
		return goldDateTime;
	}

	public void setGoldDateTime(Date goldDateTime) {
		this.goldDateTime = goldDateTime;
	}

	public Integer getVoiceType() {
		return voiceType;
	}

	public void setVoiceType(Integer voiceType) {
		this.voiceType = voiceType;
	}

	public Date getAuthDateTime() {
		return authDateTime;
	}

	public void setAuthDateTime(Date authDateTime) {
		this.authDateTime = authDateTime;
	}

	public Integer getBlackStatus() {
		return blackStatus;
	}

	public void setBlackStatus(Integer blackStatus) {
		this.blackStatus = blackStatus;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public Integer getPrivacyType() {
		return privacyType;
	}

	public void setPrivacyType(Integer privacyType) {
		this.privacyType = privacyType;
	}

	public Integer getLoanType() {
		return loanType;
	}

	public void setLoanType(Integer loanType) {
		this.loanType = loanType;
	}

}