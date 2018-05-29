package com.cn.flypay.pageModel.payment.minsheng;

/**
 * 交易异步通知(公共)
 * 
 * @author sunyue
 * 
 */
public class SMZF008 extends CommonSMZF {
	public static String operate_name = "SMZF008";
	/** 买家编号 */
	private String buyerId;
	/** 买家帐号 */
	private String buyerAccount;
	/** 订单金额 */
	private String totalAmount;
	/** 买家付款金额 */
	private String buyerPayAmount;
	/** 积分付款金额 */
	private String pointAmount;
	/** 交易支付时间 */
	private String payTime;
	/** 对账日期 */
	private String settleDate;
	/** 清算撤销标识 */
	private String isClearOrCancel;
	/** 支付渠道流水 */
	private String channelNo;
	/** 备用域1 */
	private String extend1;
	/** 备用域2 */
	private String extend2;
	/** 备用域3 */
	private String extend3;

	public String getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	public String getBuyerAccount() {
		return buyerAccount;
	}

	public void setBuyerAccount(String buyerAccount) {
		this.buyerAccount = buyerAccount;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getBuyerPayAmount() {
		return buyerPayAmount;
	}

	public void setBuyerPayAmount(String buyerPayAmount) {
		this.buyerPayAmount = buyerPayAmount;
	}

	public String getPointAmount() {
		return pointAmount;
	}

	public void setPointAmount(String pointAmount) {
		this.pointAmount = pointAmount;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public String getIsClearOrCancel() {
		return isClearOrCancel;
	}

	public void setIsClearOrCancel(String isClearOrCancel) {
		this.isClearOrCancel = isClearOrCancel;
	}

	public String getChannelNo() {
		return channelNo;
	}

	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
	}

	public String getExtend1() {
		return extend1;
	}

	public void setExtend1(String extend1) {
		this.extend1 = extend1;
	}

	public String getExtend2() {
		return extend2;
	}

	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}

	public String getExtend3() {
		return extend3;
	}

	public void setExtend3(String extend3) {
		this.extend3 = extend3;
	}

	@Override
	public String getOperateName() {
		return operate_name;
	}
}
