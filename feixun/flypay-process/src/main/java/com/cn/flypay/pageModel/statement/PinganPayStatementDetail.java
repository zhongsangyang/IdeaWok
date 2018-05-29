package com.cn.flypay.pageModel.statement;

public class PinganPayStatementDetail implements java.io.Serializable {
	private static final long serialVersionUID = -54989296610104L;
	/** 清算平台订单ID */
	private String platformNum;
	/** 支付平台（微信、支付宝）订单号 */
	private String wxAlipayNum;
	/** 清算方订单号 */
	private String orderNum;
	/** 商户全称 */
	private String merchName;
	/** 创建时间 2016-10-28 16:47:20 */
	private String createDate;
	/** 支付时间 */
	private String paymentDate;
	/** 实收金额 */
	private Double oriAmt;
	/** 平台（微信、支付宝）手续费 */
	private Double platformFee;
	/** 银行手续费 */
	private Double bankFee;
	/** 清算方手续费 */
	private Double settleFee;
	/** 净收金额 */
	private Double realAmt;

	public String getPlatformNum() {
		return platformNum;
	}

	public void setPlatformNum(String platformNum) {
		this.platformNum = platformNum;
	}

	public String getWxAlipayNum() {
		return wxAlipayNum;
	}

	public void setWxAlipayNum(String wxAlipayNum) {
		this.wxAlipayNum = wxAlipayNum;
	}


	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getMerchName() {
		return merchName;
	}

	public void setMerchName(String merchName) {
		this.merchName = merchName;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Double getOriAmt() {
		return oriAmt;
	}

	public void setOriAmt(Double oriAmt) {
		this.oriAmt = oriAmt;
	}

	public Double getPlatformFee() {
		return platformFee;
	}

	public void setPlatformFee(Double platformFee) {
		this.platformFee = platformFee;
	}

	public Double getBankFee() {
		return bankFee;
	}

	public void setBankFee(Double bankFee) {
		this.bankFee = bankFee;
	}

	public Double getSettleFee() {
		return settleFee;
	}

	public void setSettleFee(Double settleFee) {
		this.settleFee = settleFee;
	}

	public Double getRealAmt() {
		return realAmt;
	}

	public void setRealAmt(Double realAmt) {
		this.realAmt = realAmt;
	}

}