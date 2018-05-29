package com.cn.flypay.pageModel.payment.pingan;

/**
 * 支付宝、微信－订单查询接口
 * 
 * @author sunyue
 * 
 */
public class FshowsLiquidationAlipayTradeQueryResponse extends CommonPinganResponse {
	private String trade_no;
	private String out_trade_no;
	private String net_receipt_amount;
	private String bank_commission_rate;
	private String bank_commission_fee;
	private String pay_platform_rate;
	private String pay_platform_fee;
	private String liquidator_commission_rate;
	private String liquidator_commission_fee;
	/* 支付宝 */
	private String buyer_logon_id;
	private String trade_status;
	private String total_amount;
	private String receipt_amount;
	private String buyer_pay_amount;
	private String point_amount;
	private String invoice_amount;
	private String send_pay_date;
	private String alipay_store_id;
	private String store_id;
	private String terminal_id;
	private String fund_bill_list;
	private String store_name;
	private String buyer_user_id;
	private String discount_goods_detail;
	private String industry_sepc_detail;
	/* 微信 */
	private String openid;
	private String is_subscribe;
	private String trade_type;
	private String trade_state;
	private String bank_type;
	private String total_fee;
	private String cash_fee;
	private String time_end;
	private String trade_state_desc;

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

	public String getBuyer_logon_id() {
		return buyer_logon_id;
	}

	public void setBuyer_logon_id(String buyer_logon_id) {
		this.buyer_logon_id = buyer_logon_id;
	}

	public String getTrade_status() {
		return trade_status;
	}

	public void setTrade_status(String trade_status) {
		this.trade_status = trade_status;
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

	public String getSend_pay_date() {
		return send_pay_date;
	}

	public void setSend_pay_date(String send_pay_date) {
		this.send_pay_date = send_pay_date;
	}

	public String getAlipay_store_id() {
		return alipay_store_id;
	}

	public void setAlipay_store_id(String alipay_store_id) {
		this.alipay_store_id = alipay_store_id;
	}

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public String getTerminal_id() {
		return terminal_id;
	}

	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}

	public String getFund_bill_list() {
		return fund_bill_list;
	}

	public void setFund_bill_list(String fund_bill_list) {
		this.fund_bill_list = fund_bill_list;
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

	public String getIndustry_sepc_detail() {
		return industry_sepc_detail;
	}

	public void setIndustry_sepc_detail(String industry_sepc_detail) {
		this.industry_sepc_detail = industry_sepc_detail;
	}

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

	public String getTrade_state() {
		return trade_state;
	}

	public void setTrade_state(String trade_state) {
		this.trade_state = trade_state;
	}

	public String getBank_type() {
		return bank_type;
	}

	public void setBank_type(String bank_type) {
		this.bank_type = bank_type;
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

	public String getTime_end() {
		return time_end;
	}

	public void setTime_end(String time_end) {
		this.time_end = time_end;
	}

	public String getTrade_state_desc() {
		return trade_state_desc;
	}

	public void setTrade_state_desc(String trade_state_desc) {
		this.trade_state_desc = trade_state_desc;
	}

}