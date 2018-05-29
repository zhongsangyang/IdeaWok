package com.cn.flypay.pageModel.payment.pingan;

public class FshowsLiquidationWxTradePrecreateResponse extends CommonPinganResponse {
	private String out_trade_no;
	private String code_url;

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getCode_url() {
		return code_url;
	}

	public void setCode_url(String code_url) {
		this.code_url = code_url;
	}

}