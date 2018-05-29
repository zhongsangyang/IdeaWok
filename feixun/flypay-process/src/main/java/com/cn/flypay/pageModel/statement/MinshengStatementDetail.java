package com.cn.flypay.pageModel.statement;

public class MinshengStatementDetail implements java.io.Serializable {
	private static final long serialVersionUID = -5498929661004L;
	/** 合作方标识 */
	private String cooperator;
	/** 商户入驻返回的商户编号 */
	private String merchantCode;
	/** 平台流水号 */
	private String smzfMsgId;
	/** 合作方请求交易流水号 */
	private String reqMsgId;
	/** 交易金额（保留两位） */
	private Double amount;
	/** 对账日期 */
	private String settleDate;
	/** S成功; */
	private String respType;
	/** 响应码 */
	private String respCode;
	/** 响应描述 */
	private String respMsg;
	/** 1001 支付/1002 退款/1003 撤销 */
	private String transactionType;
	/** 原合作方支付流水号 */
	private String oriReqMsgId;
	/** 手续费（保留两位） */
	private Double fee;

	public String getCooperator() {
		return cooperator;
	}

	public void setCooperator(String cooperator) {
		this.cooperator = cooperator;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getSmzfMsgId() {
		return smzfMsgId;
	}

	public void setSmzfMsgId(String smzfMsgId) {
		this.smzfMsgId = smzfMsgId;
	}

	public String getReqMsgId() {
		return reqMsgId;
	}

	public void setReqMsgId(String reqMsgId) {
		this.reqMsgId = reqMsgId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public String getRespType() {
		return respType;
	}

	public void setRespType(String respType) {
		this.respType = respType;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getRespMsg() {
		return respMsg;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getOriReqMsgId() {
		return oriReqMsgId;
	}

	public void setOriReqMsgId(String oriReqMsgId) {
		this.oriReqMsgId = oriReqMsgId;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

}