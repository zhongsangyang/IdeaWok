package com.cn.flypay.pageModel.statement;

public class WeixinStatementDetail implements java.io.Serializable {
	private static final long serialVersionUID = -5498926943911004L;
	// 交易时间
	private String tranDateStr;
	// 微信订单号
	private String weixinNum;
	// 商户订单号
	private String orderNum;
	// 用户标识
	private String userInfo;
	// 交易类型
	private String tranType;
	// 交易状态
	private String tranStatus;
	// 付款银行
	private String cft;
	// 货币种类
	private String ccy;
	// 总金额
	private Double totalAmt;
	// 手续费
	private Double fee;
	// 费率
	private String feeRate;


	public String getTranDateStr() {
		return tranDateStr;
	}

	public void setTranDateStr(String tranDateStr) {
		this.tranDateStr = tranDateStr;
	}

	public String getWeixinNum() {
		return weixinNum;
	}

	public void setWeixinNum(String weixinNum) {
		this.weixinNum = weixinNum;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public String getTranStatus() {
		return tranStatus;
	}

	public void setTranStatus(String tranStatus) {
		this.tranStatus = tranStatus;
	}

	public String getCft() {
		return cft;
	}

	public void setCft(String cft) {
		this.cft = cft;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public Double getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(Double totalAmt) {
		this.totalAmt = totalAmt;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public String getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(String feeRate) {
		this.feeRate = feeRate;
	}

}