package com.cn.flypay.pageModel.payment.pingan;
/**
 * 京东入驻接口
 * @author liangchao
 *
 */
public class FshowsLiquidationJdMerchantCreate {
	/**
	 * 清算平台唯一商户号
	 */
	private String store_id;
	/**
	 * 京东商户行业类目码，
	 */
	private String business;
	/**
	 * 商户名称
	 */
	private String merchant_name;
	/**
	 * 商户简称
	 */
	private String merchant_shortname;
	/**
	 * 客服电话
	 */
	private String service_phone;
	public String getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}
	public String getBusiness() {
		return business;
	}
	public void setBusiness(String business) {
		this.business = business;
	}
	public String getMerchant_name() {
		return merchant_name;
	}
	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
	}
	public String getMerchant_shortname() {
		return merchant_shortname;
	}
	public void setMerchant_shortname(String merchant_shortname) {
		this.merchant_shortname = merchant_shortname;
	}
	public String getService_phone() {
		return service_phone;
	}
	public void setService_phone(String service_phone) {
		this.service_phone = service_phone;
	}
	
	
	
	
	
}
