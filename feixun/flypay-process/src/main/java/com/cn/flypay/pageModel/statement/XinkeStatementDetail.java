package com.cn.flypay.pageModel.statement;

public class XinkeStatementDetail implements java.io.Serializable {
	private static final long serialVersionUID = -734227056568420816L;
	/** 订单号 */
	private String orderNum;
	/** 交易类型 */
	private String tradeType;
	/** 交易平台 */
	private String tradePlate;
	/** 交易时间 */
	private String tradeDate;
	/** 金额 */
	private String amt;
	/** 机构号 */
	private String orgNum;
	/** 商户号 */
	private String merchantId;
	/** 交易状态 */
	private String status;
	/** 手机号 */
	private String phone;
	/** 清算日期 */
	private String settlementDate;

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getTradePlate() {
		return tradePlate;
	}

	public void setTradePlate(String tradePlate) {
		this.tradePlate = tradePlate;
	}

	public String getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public String getOrgNum() {
		return orgNum;
	}

	public void setOrgNum(String orgNum) {
		this.orgNum = orgNum;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}

}