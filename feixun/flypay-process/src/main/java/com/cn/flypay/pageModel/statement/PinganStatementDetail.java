package com.cn.flypay.pageModel.statement;

public class PinganStatementDetail {

	// 20160324|::|102519|::|20160324|::|ZXLKF0320160324TV001|::||::|6216262000015318974|::|1.00|::||::|20160324|::|0126319|::|0000|::|银行主机单笔代收成功！|::|备注

	// 交易日期 20160324
	private String transDate;
	// 交易时间102519
	private String transTime;
	// 请算日期20160324
	private String stmDate;
	// 订单号ZXLKF0320160324TV001
	private String orderNum;
	// 批次号 6216262000015318974
	private String batchNum;
	// 收款借记卡/账号
	private String cardNo;
	// 金额
	private Double amt;
	// 实收手续费
	private Double fee;
	// 记账日期
	private String accDate;
	// 记账流水号
	private String accNum;
	// 错误码
	private String errorCode;
	// 错误消息
	private String errorInfo;
	// 备注
	private String remark;

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public String getStmDate() {
		return stmDate;
	}

	public void setStmDate(String stmDate) {
		this.stmDate = stmDate;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getBatchNum() {
		return batchNum;
	}

	public void setBatchNum(String batchNum) {
		this.batchNum = batchNum;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Double getAmt() {
		return amt;
	}

	public void setAmt(Double amt) {
		this.amt = amt;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public String getAccDate() {
		return accDate;
	}

	public void setAccDate(String accDate) {
		this.accDate = accDate;
	}

	public String getAccNum() {
		return accNum;
	}

	public void setAccNum(String accNum) {
		this.accNum = accNum;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
