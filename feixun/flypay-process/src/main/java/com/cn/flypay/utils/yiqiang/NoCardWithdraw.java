package com.cn.flypay.utils.yiqiang;
/**
 * 5.3	提现申请
 * @author yq@2L01
 *
 */
public class NoCardWithdraw {

	private String tranType;
	
	private String merTrace;
	
	private String merNo;
	
	private String orderNo;
	
	private String transAmount;
	
	private String productType;
	
	private String paymentType;

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public String getMerTrace() {
		return merTrace;
	}

	public void setMerTrace(String merTrace) {
		this.merTrace = merTrace;
	}

	public String getMerNo() {
		return merNo;
	}

	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTransAmount() {
		return transAmount;
	}

	public void setTransAmount(String transAmount) {
		this.transAmount = transAmount;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	@Override
	public String toString() {
		return "NoCardWithdraw [tranType=" + tranType + ", merTrace=" + merTrace + ", merNo=" + merNo + ", orderNo="
				+ orderNo + ", transAmount=" + transAmount + ", productType=" + productType + ", paymentType="
				+ paymentType + "]";
	}
	
	
	
}
