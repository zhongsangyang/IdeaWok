package com.cn.flypay.pageModel.payment.pingan;

public class FshowsLiquidationSubmerchantRateSetResponse extends CommonPinganResponse {
	private String sub_merchant_id;
	private String extranal_id;
	private String merchant_rate;
  

	public String getSub_merchant_id() {
		return sub_merchant_id;
	}

	public void setSub_merchant_id(String sub_merchant_id) {
		this.sub_merchant_id = sub_merchant_id;
	}

	public String getExtranal_id() {
		return extranal_id;
	}

	public void setExtranal_id(String extranal_id) {
		this.extranal_id = extranal_id;
	}

	public String getMerchant_rate() {
		return merchant_rate;
	}

	public void setMerchant_rate(String merchant_rate) {
		this.merchant_rate = merchant_rate;
	}


}