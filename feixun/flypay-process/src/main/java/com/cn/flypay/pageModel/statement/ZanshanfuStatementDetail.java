package com.cn.flypay.pageModel.statement;

public class ZanshanfuStatementDetail implements java.io.Serializable {
	private static final long serialVersionUID = -5498926943911004L;
	private String payNo;
	private String tranDateStr;
	private String tranAmt;
	private String realAmt;
	private String feeRate;
	private String fee;
	private String balance;
	private String status;
	private String transChannel;
	private String extraInfo;

	public String getPayNo() {
		return payNo;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	public String getTranDateStr() {
		return tranDateStr;
	}

	public void setTranDateStr(String tranDateStr) {
		this.tranDateStr = tranDateStr;
	}

	public String getTranAmt() {
		return tranAmt;
	}

	public void setTranAmt(String tranAmt) {
		this.tranAmt = tranAmt;
	}

	public String getRealAmt() {
		return realAmt;
	}

	public void setRealAmt(String realAmt) {
		this.realAmt = realAmt;
	}

	public String getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(String feeRate) {
		this.feeRate = feeRate;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTransChannel() {
		return transChannel;
	}

	public void setTransChannel(String transChannel) {
		this.transChannel = transChannel;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

}