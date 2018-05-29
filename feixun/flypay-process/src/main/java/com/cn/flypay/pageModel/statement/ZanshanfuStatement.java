package com.cn.flypay.pageModel.statement;

import java.util.ArrayList;
import java.util.List;

public class ZanshanfuStatement implements java.io.Serializable {

	private static final long serialVersionUID = -5498943911004L;
	private Integer totalSuccessNum;
	private Double totalSuccessAmt;
	private Double feeRate;
	private Double fee;
	private Double totalBalance;
	private String groupName;
	private List<ZanshanfuStatementDetail> details = new ArrayList<ZanshanfuStatementDetail>();

	public Integer getTotalSuccessNum() {
		return totalSuccessNum;
	}

	public void setTotalSuccessNum(Integer totalSuccessNum) {
		this.totalSuccessNum = totalSuccessNum;
	}


	public Double getTotalSuccessAmt() {
		return totalSuccessAmt;
	}

	public void setTotalSuccessAmt(Double totalSuccessAmt) {
		this.totalSuccessAmt = totalSuccessAmt;
	}

	public Double getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(Double feeRate) {
		this.feeRate = feeRate;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public Double getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(Double totalBalance) {
		this.totalBalance = totalBalance;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<ZanshanfuStatementDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ZanshanfuStatementDetail> details) {
		this.details = details;
	}

}