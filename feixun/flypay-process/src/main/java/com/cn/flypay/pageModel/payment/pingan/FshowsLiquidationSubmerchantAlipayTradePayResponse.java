package com.cn.flypay.pageModel.payment.pingan;

import com.alibaba.fastjson.JSONObject;

public class FshowsLiquidationSubmerchantAlipayTradePayResponse extends CommonPinganResponse {
	private String trade_no;
	private String out_trade_no;
	private String buyer_logon_id;
	private String total_amount;
	private String receipt_amount;
	private String buyer_pay_amount;
	private String point_amount;
	private String invoice_amount;
	private String gmt_payment;
	private JSONObject fund_bill_list;
	private String card_balance;
	private String store_name;
	private String buyer_user_id;
	private String discount_goods_detail;

	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getBuyer_logon_id() {
		return buyer_logon_id;
	}

	public void setBuyer_logon_id(String buyer_logon_id) {
		this.buyer_logon_id = buyer_logon_id;
	}

	public String getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}

	public String getReceipt_amount() {
		return receipt_amount;
	}

	public void setReceipt_amount(String receipt_amount) {
		this.receipt_amount = receipt_amount;
	}

	public String getBuyer_pay_amount() {
		return buyer_pay_amount;
	}

	public void setBuyer_pay_amount(String buyer_pay_amount) {
		this.buyer_pay_amount = buyer_pay_amount;
	}

	public String getPoint_amount() {
		return point_amount;
	}

	public void setPoint_amount(String point_amount) {
		this.point_amount = point_amount;
	}

	public String getInvoice_amount() {
		return invoice_amount;
	}

	public void setInvoice_amount(String invoice_amount) {
		this.invoice_amount = invoice_amount;
	}

	public String getGmt_payment() {
		return gmt_payment;
	}

	public void setGmt_payment(String gmt_payment) {
		this.gmt_payment = gmt_payment;
	}

	public JSONObject getFund_bill_list() {
		return fund_bill_list;
	}

	public void setFund_bill_list(JSONObject fund_bill_list) {
		this.fund_bill_list = fund_bill_list;
	}

	public String getCard_balance() {
		return card_balance;
	}

	public void setCard_balance(String card_balance) {
		this.card_balance = card_balance;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public String getBuyer_user_id() {
		return buyer_user_id;
	}

	public void setBuyer_user_id(String buyer_user_id) {
		this.buyer_user_id = buyer_user_id;
	}

	public String getDiscount_goods_detail() {
		return discount_goods_detail;
	}

	public void setDiscount_goods_detail(String discount_goods_detail) {
		this.discount_goods_detail = discount_goods_detail;
	}

}