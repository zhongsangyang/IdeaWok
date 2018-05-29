package com.cn.flypay.pageModel.payment.pingan;

/**
 * 微信刷卡支付
 * 
 * @author sunyue
 * 
 */
public class FshowsLiquidationJdpayH5payResponse extends CommonPinganResponse {
	private String prepay_id;
	private String trade_no;
	private String total_fee;
	private String net_receipt_amount;
	private String bank_commission_rate;
	private String bank_commission_fee;
	private String pay_platform_rate;
	private String pay_platform_fee;
	private String liquidator_commission_rate;
	private String liquidator_commission_fee;
	private String notify_url;

	public String getPrepay_id() {
		return prepay_id;
	}

	public void setPrepay_id(String prepay_id) {
		this.prepay_id = prepay_id;
	}

	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getNet_receipt_amount() {
		return net_receipt_amount;
	}

	public void setNet_receipt_amount(String net_receipt_amount) {
		this.net_receipt_amount = net_receipt_amount;
	}

	public String getBank_commission_rate() {
		return bank_commission_rate;
	}

	public void setBank_commission_rate(String bank_commission_rate) {
		this.bank_commission_rate = bank_commission_rate;
	}

	public String getBank_commission_fee() {
		return bank_commission_fee;
	}

	public void setBank_commission_fee(String bank_commission_fee) {
		this.bank_commission_fee = bank_commission_fee;
	}

	public String getPay_platform_rate() {
		return pay_platform_rate;
	}

	public void setPay_platform_rate(String pay_platform_rate) {
		this.pay_platform_rate = pay_platform_rate;
	}

	public String getPay_platform_fee() {
		return pay_platform_fee;
	}

	public void setPay_platform_fee(String pay_platform_fee) {
		this.pay_platform_fee = pay_platform_fee;
	}

	public String getLiquidator_commission_rate() {
		return liquidator_commission_rate;
	}

	public void setLiquidator_commission_rate(String liquidator_commission_rate) {
		this.liquidator_commission_rate = liquidator_commission_rate;
	}

	public String getLiquidator_commission_fee() {
		return liquidator_commission_fee;
	}

	public void setLiquidator_commission_fee(String liquidator_commission_fee) {
		this.liquidator_commission_fee = liquidator_commission_fee;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

}