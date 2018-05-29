package com.cn.flypay.pageModel.payment.pingan;

public class FshowsLiquidationSubmerchantCreateResponse extends CommonPinganResponse {
	private String sub_merchant_id;
	private String extranal_id;
	private String name;
	private String alias_name;
	private String service_phone;
	private String category_id;
	private String liquidator_name;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias_name() {
		return alias_name;
	}

	public void setAlias_name(String alias_name) {
		this.alias_name = alias_name;
	}

	public String getService_phone() {
		return service_phone;
	}

	public void setService_phone(String service_phone) {
		this.service_phone = service_phone;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getLiquidator_name() {
		return liquidator_name;
	}

	public void setLiquidator_name(String liquidator_name) {
		this.liquidator_name = liquidator_name;
	}

}