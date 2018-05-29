package com.cn.flypay.pageModel.payment.pingan;

public class FshowsLiquidationSubmerchantAlipayTradePrecreateResponse extends CommonPinganResponse {
	private String outTradeNo;
	private String qrCode;

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

}