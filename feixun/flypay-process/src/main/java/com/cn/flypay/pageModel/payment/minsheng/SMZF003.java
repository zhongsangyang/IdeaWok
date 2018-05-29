package com.cn.flypay.pageModel.payment.minsheng;

/**
 * 条码支付
 * 
 * @author sunyue
 * 
 */
public class SMZF003 extends CommonSMZF {
	public static String operate_name = "SMZF003";
	/** 银行商户编码 必填 */
	private String merchantCode;
	/**
	 * 支付场景 必填 支付场景,条码支付-1 声波支付-2
	 */
	private String scene;
	/** 支付授权码 必填 */
	private String authCode;
	/** 订单金额 */
	private String totalAmount;
	/** 订单标题 */
	private String subject;
	/** 订单描述 */
	private String desc;
	/** 商户操作员编号 */
	private String operatorId;
	/** 商户门店编号 */
	private String storeId;
	/** 商户机具终端编号 */
	private String terminalId;
	/** 指定支付方式 */
	private String limitPay;
	/** 买家付款金额 */
	private String buyerPayAmount;
	/** 积分支付金额 */
	private String pointAmount;
	/** 交易支付时间 */
	private String payTime;
	/** 对账日期 */
	private String settleDate;
	/** 买家编号 */
	private String buyerId;
	/** 买家帐号 */
	private String buyerAccount;
	/** 清算撤销标识 0正常清算-1发生撤销，渠道不产生退款 */
	private String isClearOrCancel;
	/** 支付渠道流水 */
	private String channelNo;
	/** 备用域1 */
	private String extend1;
	/** 备用域2 */
	private String extend2;
	/** 备用域3 */
	private String extend3;

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getScene() {
		return scene;
	}

	public void setScene(String scene) {
		this.scene = scene;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getLimitPay() {
		return limitPay;
	}

	public void setLimitPay(String limitPay) {
		this.limitPay = limitPay;
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
