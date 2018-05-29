package com.cn.flypay.pageModel.payment.pingan;
/**
 * 平安--微信—子商户配置接口
 * @author liangchao
 *
 */
public class FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest {
	private String store_id;
	private String jsapi_path;
	private String sub_appid;
	private String subscribe_appid;
	private String pay_type;
	public String getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}
	public String getJsapi_path() {
		return jsapi_path;
	}
	public void setJsapi_path(String jsapi_path) {
		this.jsapi_path = jsapi_path;
	}
	public String getSub_appid() {
		return sub_appid;
	}
	public void setSub_appid(String sub_appid) {
		this.sub_appid = sub_appid;
	}
	public String getSubscribe_appid() {
		return subscribe_appid;
	}
	public void setSubscribe_appid(String subscribe_appid) {
		this.subscribe_appid = subscribe_appid;
	}
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	
	
	
	
	
	
	
}
