package com.cn.flypay.pageModel.sys;

import java.util.Date;

public class CardBankConfig implements java.io.Serializable {

	private static final long serialVersionUID = 107383375924017L;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getCardBankName() {
		return cardBankName;
	}

	public void setCardBankName(String cardBankName) {
		this.cardBankName = cardBankName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public Integer getCardLength() {
		return cardLength;
	}

	public void setCardLength(Integer cardLength) {
		this.cardLength = cardLength;
	}

	public Long getCardBin() {
		return cardBin;
	}

	public void setCardBin(Long cardBin) {
		this.cardBin = cardBin;
	}

	public Integer getCardBinLength() {
		return cardBinLength;
	}

	public void setCardBinLength(Integer cardBinLength) {
		this.cardBinLength = cardBinLength;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}