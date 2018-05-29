package com.cn.flypay.pageModel.payment;

public class OrderQueryRequest {
	private String appid;
	private String mch_id;
	private String sub_mch_id;
	private String transaction_id;
	private String out_trade_no;
	private String nonce_str;
	private String sign;

	public OrderQueryRequest(String appid, String mch_id, String sub_mch_id) {
		this.appid = appid;
		this.mch_id = mch_id;
		this.sub_mch_id = sub_mch_id;
	}

	public String getAppid() {
		return this.appid;
	}

	public String getSub_mch_id() {
		return sub_mch_id;
	}

	public void setSub_mch_id(String sub_mch_id) {
		this.sub_mch_id = sub_mch_id;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMch_id() {
		return this.mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getTransaction_id() {
		return this.transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getOut_trade_no() {
		return this.out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getNonce_str() {
		return this.nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getSign() {
		return this.sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
}