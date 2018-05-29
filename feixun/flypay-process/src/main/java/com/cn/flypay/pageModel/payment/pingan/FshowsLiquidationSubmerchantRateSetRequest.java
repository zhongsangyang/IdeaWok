package com.cn.flypay.pageModel.payment.pingan;
/**
 * 设置商户终端费率
 *
 */
public class FshowsLiquidationSubmerchantRateSetRequest {
	private String external_id;
	private String sub_merchant_id;
	private String merchant_rate;
	private String type;

	public String getExternal_id() {
		return external_id;
	}

	public void setExternal_id(String external_id) {
		this.external_id = external_id;
	}

	public String getSub_merchant_id() {
		return sub_merchant_id;
	}

	public void setSub_merchant_id(String sub_merchant_id) {
		this.sub_merchant_id = sub_merchant_id;
	}

	public String getMerchant_rate() {
		return merchant_rate;
	}

	public void setMerchant_rate(String merchant_rate) {
		this.merchant_rate = merchant_rate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	

}