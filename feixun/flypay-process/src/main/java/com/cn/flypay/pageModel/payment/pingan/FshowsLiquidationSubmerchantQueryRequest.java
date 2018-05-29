package com.cn.flypay.pageModel.payment.pingan;

/**
 * 商户查询接口
 * 
 * @author sunyue
 * 
 */
public class FshowsLiquidationSubmerchantQueryRequest{

	private String sub_merchant_id;
	private String external_id;

	public String getSub_merchant_id() {
		return sub_merchant_id;
	}

	public void setSub_merchant_id(String sub_merchant_id) {
		this.sub_merchant_id = sub_merchant_id;
	}

	public String getExternal_id() {
		return external_id;
	}

	public void setExternal_id(String external_id) {
		this.external_id = external_id;
	}

}