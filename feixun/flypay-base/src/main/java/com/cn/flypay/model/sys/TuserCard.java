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
import javax.validation.constraints.NotNull;

/**
 * SysUserCard entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_user_card")
public class TuserCard implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3052214665112587651L;
	// Fields

	/**
	 * 
	 */
	private Long id;
	private Long version;
	private Tuser user;
	private Integer status;
	private String cardNo;
	private Tbank bank;
	private String province;
	private String city;
	private String branchId;
	private String branchName;
	private String cardType;
	/**
	 * ' 0 否 1 是'
	 */
	private Integer isSettlmentCard;
	private String phone;
	private String cvv;
	private String validityDate;
	private Integer cardzhitong;
	/**
	 * 是否开通易联快捷支付  0 否  1是
	 */
	private String isOpenYilianQuickPay;
	
	/**
	 * 是否开通嘎吱银联快捷支付  0/null 否  1是
	 */
	private String isOpenGaZhiYinLianQuickPay;
	

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
	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "card_no", length = 32)
	public String getCardNo() {
		return this.cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_id")
	public Tbank getBank() {
		return bank;
	}

	public void setBank(Tbank bank) {
		this.bank = bank;
	}

	@Column(name = "province", length = 32)
	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	@Column(name = "city", length = 32)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "Branch_Id", length = 32)
	public String getBranchId() {
		return this.branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	@Column(name = "Branch_Name", length = 200)
	public String getBranchName() {
		return this.branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	@Column(name = "card_type", length = 4)
	public String getCardType() {
		return this.cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	@Column(name = "is_settlment_card")
	public Integer getIsSettlmentCard() {
		return this.isSettlmentCard;
	}

	public void setIsSettlmentCard(Integer isSettlmentCard) {
		this.isSettlmentCard = isSettlmentCard;
	}

	@Column(name = "phone", length = 32)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "cvv", length = 32)
	public String getCvv() {
		return this.cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	@Column(name = "validityDate", length = 32)
	public String getValidityDate() {
		return this.validityDate;
	}

	public void setValidityDate(String validityDate) {
		this.validityDate = validityDate;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	public Tuser getUser() {
		return this.user;
	}

	public void setUser(Tuser user) {
		this.user = user;
	}
	
	@Column(name = "card_zhitong")
	public Integer getCardzhitong() {
		return cardzhitong;
	}

	public void setCardzhitong(Integer cardzhitong) {
		this.cardzhitong = cardzhitong;
	}

	@Column(name = "is_open_yilian_quick_pay")
	public String getIsOpenYilianQuickPay() {
		return isOpenYilianQuickPay;
	}

	public void setIsOpenYilianQuickPay(String isOpenYilianQuickPay) {
		this.isOpenYilianQuickPay = isOpenYilianQuickPay;
	}
	
	
	@Column(name = "is_open_gazhiyinlian_quick_pay")
	public String getIsOpenGaZhiYinLianQuickPay() {
		return isOpenGaZhiYinLianQuickPay;
	}

	public void setIsOpenGaZhiYinLianQuickPay(String isOpenGaZhiYinLianQuickPay) {
		this.isOpenGaZhiYinLianQuickPay = isOpenGaZhiYinLianQuickPay;
	}
	
	
	
	
	
	
}