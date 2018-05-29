package com.cn.flypay.pageModel.statement;

import java.util.Date;

public class AlipayStatementDetail implements java.io.Serializable {
	private static final long serialVersionUID = -5498926943911004L;

	// 支付宝订单号
	private String alipayNum;
	// 商户订单号
	private String orderNum;
	// 交易类型
	private String tranType;

	// 交易时间
	private Date tranDate;
	// 交易账户
	private String fromUserName;
	// 交易金额
	private Double orderAmt;
	// 实收金额
	private Double realAmt;
	// 服务费（元）
	private Double fee;

	public String getAlipayNum() {
		return alipayNum;
	}

	public void setAlipayNum(String alipayNum) {
		this.alipayNum = alipayNum;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public Date getTranDate() {
		return tranDate;
	}

	public void setTranDate(Date tranDate) {
		this.tranDate = tranDate;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}


	public Double getOrderAmt() {
		return orderAmt;
	}

	public void setOrderAmt(Double orderAmt) {
		this.orderAmt = orderAmt;
	}

	public Double getRealAmt() {
		return realAmt;
	}

	public void setRealAmt(Double realAmt) {
		this.realAmt = realAmt;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

}