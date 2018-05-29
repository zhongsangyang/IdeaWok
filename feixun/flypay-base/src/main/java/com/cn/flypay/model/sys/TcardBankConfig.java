package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * SysCardBankConfig entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_card_bank_config")
public class TcardBankConfig implements java.io.Serializable {

	private static final long serialVersionUID = 107382253375924017L;
	private Long id;
	private Integer version;
	private String cardBankName;
	private String bankCode;
	private String accountNo;
	private Integer cardLength;
	private Long cardBin;
	private Integer cardBinLength;
	private String cardType;
	private Date createTime;

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
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "card_bank_name", length = 128)
	public String getCardBankName() {
		return this.cardBankName;
	}

	public void setCardBankName(String cardBankName) {
		this.cardBankName = cardBankName;
	}

	@Column(name = "bank_code", length = 64)
	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	@Column(name = "account_no", length = 64)
	public String getAccountNo() {
		return this.accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	@Column(name = "card_length")
	public Integer getCardLength() {
		return this.cardLength;
	}

	public void setCardLength(Integer cardLength) {
		this.cardLength = cardLength;
	}

	@Column(name = "card_bin")
	public Long getCardBin() {
		return this.cardBin;
	}

	public void setCardBin(Long cardBin) {
		this.cardBin = cardBin;
	}

	@Column(name = "card_bin_length")
	public Integer getCardBinLength() {
		return this.cardBinLength;
	}

	public void setCardBinLength(Integer cardBinLength) {
		this.cardBinLength = cardBinLength;
	}

	@Column(name = "card_type", length = 64)
	public String getCardType() {
		return this.cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}