package com.cn.flypay.utils.yiqiang;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.client.utils.URLEncodedUtils;

/**
 * 5.4 交易查询
 * 
 * @author yq@2L01
 *
 */
public class TransQuery {

	private String tranType;

	private String merTrace;

	private String merNo;

	private String orderNo;

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
		return "TransQuery [tranType=" + tranType + ", merTrace=" + merTrace + ", merNo=" + merNo + ", orderNo=" + orderNo + ", productType=" + productType + ", paymentType=" + paymentType + "]";
	}

}
