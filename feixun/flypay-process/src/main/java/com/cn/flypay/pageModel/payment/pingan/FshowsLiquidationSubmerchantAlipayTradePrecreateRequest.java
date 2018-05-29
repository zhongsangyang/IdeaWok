package com.cn.flypay.pageModel.payment.pingan;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONType;

@JSONType(orders = { "out_trade_no", "sub_merchant", "subject", "total_amount", "notify_url", "discountable_amount",
		"undiscountable_amount", "body" })
public class FshowsLiquidationSubmerchantAlipayTradePrecreateRequest {

	private String out_trade_no;
	private String notify_url;
	private String total_amount;
	private String discountable_amount;
	private String undiscountable_amount;
	private String subject;
	private String body;
	private JSONObject sub_merchant;

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}

	public JSONObject getSub_merchant() {
		return sub_merchant;
	}

	public void setSub_merchant(JSONObject sub_merchant) {
		this.sub_merchant = sub_merchant;
	}

	public String getDiscountable_amount() {
		return discountable_amount;
	}

	public void setDiscountable_amount(String discountable_amount) {
		this.discountable_amount = discountable_amount;
	}

	public String getUndiscountable_amount() {
		return undiscountable_amount;
	}

	public void setUndiscountable_amount(String undiscountable_amount) {
		this.undiscountable_amount = undiscountable_amount;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}