package com.cn.flypay.pageModel.payment;


public class WxDownloadBillRequest {
	private String appid;
	private String mch_id;
	private String sub_mch_id;
	private String nonce_str;
	private String sign;

	// private String device_info;
	private String bill_date;
	private String bill_type;

	public WxDownloadBillRequest(String appid, String mch_id, String sub_mch_id) {
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

	public String getBill_date() {
		return bill_date;
	}

	public void setBill_date(String bill_date) {
		this.bill_date = bill_date;
	}

	public String getBill_type() {
		return bill_type;
	}

	public void setBill_type(String bill_type) {
		this.bill_type = bill_type;
	}

}