package com.cn.flypay.utils.gazhiyinlian.entities;
/**
 * 支付状态查询
 * 嘎吱（银联）基础类
 * @author liangchao
 */
public class QueryOrderInfoForGaZhiYinLian {
	/**
	 * 交易码
	 */
	private String tranType;
	/**
	 * 合作商户的唯一标识
	 */
	private String merNo;
	/**
	 * 商户流水号，商户须保证流水唯一
	 */
	private String merTrace;
	/**
	 * 支付订单号
	 */
	private String orderId;
	public String getTranType() {
		return tranType;
	}
	public void setTranType(String tranType) {
		this.tranType = tranType;
	}
	public String getMerNo() {
		return merNo;
	}
	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}
	public String getMerTrace() {
		return merTrace;
	}
	public void setMerTrace(String merTrace) {
		this.merTrace = merTrace;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	
	
}