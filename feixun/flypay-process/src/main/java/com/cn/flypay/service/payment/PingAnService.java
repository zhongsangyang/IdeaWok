package com.cn.flypay.service.payment;

import com.alibaba.fastjson.JSONObject;
/**
 * 平安平台相关公用接口
 * @author Administrator
 *
 */
public interface PingAnService {

	/**
	 * 创建翼支付子商户
	 * @param sub_merchant_id	子商户id
	 * @param merchant_store_name	商户门店名称
	 * @param appId  清算平台id
	 * @return
	 */
	public JSONObject createYZFMer(String sub_merchant_id,String merchant_store_name,String appId);
	
	/**
	 * 查询平安的商户信息
	 * @param sub_merchant_id  子商户id
	 * @param appId  清算平台id
	 * @return
	 */
	public JSONObject queryPingAnMerInfo(String sub_merchant_id,String appId);
	
}
