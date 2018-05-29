package com.cn.flypay.pageModel.payment.pingan;

/**
 * 微信公众号支付
 * 
 * @author sunyue
 * 
 */
public class FshowsLiquidationWxpayMppayRequest {
	private String sub_merchant_id;
	private String body;
	private String out_trade_no;
	private String total_fee;
	private String sub_openid;
	private String total_amount;
	private String spbill_create_ip;
	private String notify_url;
	private String sub_appid;

	public String getSub_merchant_id() {
		return sub_merchant_id;
	}

	public void setSub_merchant_id(String sub_merchant_id) {
		this.sub_merchant_id = sub_merchant_id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getSub_openid() {
		return sub_openid;
	}

	public void setSub_openid(String sub_openid) {
		this.sub_openid = sub_openid;
	}

	public String getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}

	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}

	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getSub_appid() {
		return sub_appid;
	}

	public void setSub_appid(String sub_appid) {
		this.sub_appid = sub_appid;
	}

}