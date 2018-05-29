package com.cn.flypay.pageModel.payment.pingan;

/**
 * 微信刷卡支付
 * 
 * @author sunyue
 * 
 */
public class FshowsLiquidationWxTradePayResponse extends CommonPinganResponse {
	private String openid;
	private String is_subscribe;
	private String trade_type;
	private String bank_type;
	private String fee_type;
	private String total_fee;
	private String cash_fee;
	private String transaction_id;
	private String out_trade_no;
	private String time_end;
	private String liquidator_commission_fee;
	private String bank_commission_fee;
	private String pay_platform_fee;
	private String net_money;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getIs_subscribe() {
		return is_subscribe;
	}

	public void setIs_subscribe(String is_subscribe) {
		this.is_subscribe = is_subscribe;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public String getBank_type() {
		return bank_type;
	}

	public void setBank_type(String bank_type) {
		this.bank_type = bank_type;
	}

	public String getFee_type() {
		return fee_type;
	}

	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getCash_fee() {
		return cash_fee;
	}

	public void setCash_fee(String cash_fee) {
		this.cash_fee = cash_fee;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getTime_end() {
		return time_end;
	}

	public void setTime_end(String time_end) {
		this.time_end = time_end;
	}

	public String getLiquidator_commission_fee() {
		return liquidator_commission_fee;
	}

	public void setLiquidator_commission_fee(String liquidator_commission_fee) {
		this.liquidator_commission_fee = liquidator_commission_fee;
	}

	public String getBank_commission_fee() {
		return bank_commission_fee;
	}

	public void setBank_commission_fee(String bank_commission_fee) {
		this.bank_commission_fee = bank_commission_fee;
	}

	public String getPay_platform_fee() {
		return pay_platform_fee;
	}

	public void setPay_platform_fee(String pay_platform_fee) {
		this.pay_platform_fee = pay_platform_fee;
	}

	public String getNet_money() {
		return net_money;
	}

	public void setNet_money(String net_money) {
		this.net_money = net_money;
	}

}