package com.cn.flypay.pageModel.payment.minsheng;

/**
 * 交易查询
 * 
 * @author sunyue
 * 
 */
public class SMZF006 extends CommonSMZF {
	public static String operate_name = "SMZF006";
	/** 原交易流水号 必填项 */
	private String oriReqMsgId;
	/**
	 * 原交易应答类型 S：成功 E：失败 R：不确定（处理中）
	 */
	private String oriRespType;
	/**
	 * 原交易应答码 成功：000000 失败：返回具体的响应码。详情参看“附录-扫码支付平台应答码表”章节
	 */
	private String oriRespCode;
	/** 原交易应答描述 */
	private String oriRespMsg;
	/** 订单金额 */
	private String totalAmount;
	/** 买家实付金额 */
	private String buyerPayAmount;
	/** 积分支付金额 */
	private String pointAmount;
	/** 商户操作员编号 */
	private String operatorId;
	/** 商户门店编号 */
	private String storeId;
	/** 商户机具终端编号 */
	private String terminalId;
	/** 买家编号 */
	private String buyerId;
	/** 买家帐号 */
	private String buyerAccount;
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

	public String getOriReqMsgId() {
		return oriReqMsgId;
	}

	public void setOriReqMsgId(String oriReqMsgId) {
		this.oriReqMsgId = oriReqMsgId;
	}

	public String getOriRespType() {
		return oriRespType;
	}

	public void setOriRespType(String oriRespType) {
		this.oriRespType = oriRespType;
	}

	public String getOriRespCode() {
		return oriRespCode;
	}

	public void setOriRespCode(String oriRespCode) {
		this.oriRespCode = oriRespCode;
	}

	public String getOriRespMsg() {
		return oriRespMsg;
	}

	public void setOriRespMsg(String oriRespMsg) {
		this.oriRespMsg = oriRespMsg;
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
