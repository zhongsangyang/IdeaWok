package com.cn.flypay.pageModel.payment.pingan;

/**
 * 支付宝、微信－订单查询接口
 * 
 * @author sunyue
 * 
 */
public class FshowsLiquidationAlipayTradeQueryRequest{
	private String out_trade_no;
	private String trade_no;

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

}