package com.cn.flypay.service.payment.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationBestpayMerchantAccountRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantQueryRequest;
import com.cn.flypay.service.payment.PingAnService;
import com.cn.flypay.utils.pingan.PinganPaymentUtil;

@Service
public class PingAnServiceImpl implements PingAnService{
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	@Override
	public JSONObject createYZFMer(String sub_merchant_id, String merchant_store_name,String appId) {
		JSONObject returnJson = new JSONObject();
		FshowsLiquidationBestpayMerchantAccountRequest account = new FshowsLiquidationBestpayMerchantAccountRequest();
	    account.setStore_id(sub_merchant_id);
	    account.setMerchant_store_name(merchant_store_name);
	    account.setStore_area_id("33");	//商户门店所在省
	    account.setStore_city_id("01");	//商户门店所在市
	    account.setMcc_code("5812");	//行业mac编码
	    log.info("------平安子商户Id为"+sub_merchant_id + "的商户，调用报备翼支付商户入驻接口开始，传递的商户名称为:"+merchant_store_name);
		JSONObject accountResult = PinganPaymentUtil.sentRequstToPingAnPayment(account,appId, FshowsLiquidationBestpayMerchantAccountRequest.class);
		if(accountResult==null){
			log.info("------平安子商户Id为"+sub_merchant_id + "的商户，调用报备翼支付商户入驻接口，请求平安失败");
		}
		log.info("------平安子商户Id为"+sub_merchant_id + "的商户，调用报备翼支付商户入驻接口，返回的结果为:"+accountResult.toJSONString());
		if(!accountResult.getBoolean("success")){
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", accountResult.getString("error_message"));
			return returnJson;
		}else{
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", "开通成功");
			return returnJson;
		}
	}


	@Override
	public JSONObject queryPingAnMerInfo(String sub_merchant_id,String appId) {
		JSONObject returnJson = new JSONObject();
		FshowsLiquidationSubmerchantQueryRequest req = new FshowsLiquidationSubmerchantQueryRequest();
		req.setSub_merchant_id(sub_merchant_id);
		log.info("------平安子商户Id为"+sub_merchant_id + "的商户，调用查询商户信息开始");
		JSONObject accInfo = PinganPaymentUtil.sentRequstToPingAnPayment(req,appId , FshowsLiquidationSubmerchantQueryRequest.class);
		if(accInfo == null){
			log.info("------平安子商户Id为"+sub_merchant_id + "的商户，调用查询商户信息请求失败");
		}
		log.info("------平安子商户Id为"+sub_merchant_id + "的商户，调用查询商户信息返回结果为"+accInfo.toJSONString());
		if(!accInfo.containsKey("return_value")){
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", "查询失败");
			return returnJson;
		}else{
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", accInfo.getJSONObject("return_value"));
			return returnJson;
		}
	}
	
	
	
	

}
