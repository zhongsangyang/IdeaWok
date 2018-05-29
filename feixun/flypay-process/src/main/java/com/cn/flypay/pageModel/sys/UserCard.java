package com.cn.flypay.pageModel.sys;

import com.cn.flypay.model.sys.TuserCard;

public class UserCard implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -2598722514943777262L;
	/**
	 * 
	 */
	private Long id;
	private Long version;
	private Long userId;
	private Integer status;
	private String cardNo;
	private String bankCode;
	private Long bankId;
	private String province;
	private String city;
	private String branchId;
	private String branchName;
	private String cardType;
	private String bankName;
	private String bankIco;

	private String loginName;
	private User operateUser;
	private Long organizationId;

	// Fields
	public enum card_type {
		/**
		 * 借记卡
		 */
		J,
		/**
		 * 信用卡
		 */
		X
	};

	/**
	 * ' 0 否 1 是'
	 */
	private Integer isSettlmentCard;
	private String phone;
	private String cvv;
	private String validityDate;

	private String realName;
	private String idNo;
	private String organizationName;
	
	/**
	 * 是否开通易联快捷支付  0 否  1是
	 */
	private String isOpenYilianQuickPay;
	/**
	 * 是否开通嘎吱银联快捷支付  0/null 否  1是
	 */
	private String isOpenGaZhiYinLianQuickPay;
	

	public UserCard() {
		super();
	}

	public UserCard(TuserCard t) {
		this.id = t.getId();
		this.userId = t.getUser().getId();
		this.status = t.getStatus();
		this.cardNo = t.getCardNo();
		if (t.getBank() != null) {
			this.bankCode = t.getBank().getCode();
			this.bankId = t.getBank().getId();
			this.bankName = t.getBank().getBankName();
			this.bankIco = t.getBank().getBankIco();
		}
		this.province = t.getProvince();
		this.city = t.getCity();
		this.branchId = t.getBranchId();
		this.branchName = t.getBranchName();
		this.cardType = t.getCardType();
		this.isSettlmentCard = t.getIsSettlmentCard();
		this.phone = t.getPhone();
		this.cvv = t.getCvv();
		this.validityDate = t.getValidityDate();
		this.isOpenYilianQuickPay = t.getIsOpenYilianQuickPay();
		if (t.getUser() != null) {
			this.idNo = t.getUser().getIdNo();
			this.realName = t.getUser().getRealName();
			this.loginName = t.getUser().getLoginName();
			if (t.getUser().getOrganization() != null) {
				this.organizationName = t.getUser().getOrganization().getName();
			}
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public Long getBankId() {
		return bankId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}

	public String getBankIco() {
		return bankIco;
	}

	public void setBankIco(String bankIco) {
		this.bankIco = bankIco;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
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

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public Integer getIsSettlmentCard() {
		return isSettlmentCard;
	}

	public void setIsSettlmentCard(Integer isSettlmentCard) {
		this.isSettlmentCard = isSettlmentCard;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getValidityDate() {
		return validityDate;
	}

	public void setValidityDate(String validityDate) {
		this.validityDate = validityDate;
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

	public String getIsOpenYilianQuickPay() {
		return isOpenYilianQuickPay;
	}

	public void setIsOpenYilianQuickPay(String isOpenYilianQuickPay) {
		this.isOpenYilianQuickPay = isOpenYilianQuickPay;
	}

	public String getIsOpenGaZhiYinLianQuickPay() {
		return isOpenGaZhiYinLianQuickPay;
	}

	public void setIsOpenGaZhiYinLianQuickPay(String isOpenGaZhiYinLianQuickPay) {
		this.isOpenGaZhiYinLianQuickPay = isOpenGaZhiYinLianQuickPay;
	}
	
	

}