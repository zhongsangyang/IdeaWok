package com.cn.flypay.service.payment;

import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.model.sys.TweiLianBaoMerchantReport;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.utils.weilianbao.entity.OrderPayForWeiLianBaoYinLian;

public interface WeiLianBaoYinLainService {
	
	public TweiLianBaoMerchantReport findMerchantReport(String merchantNo);
	
	public JSONObject openCard(TweiLianBaoMerchantReport merchReport, Map<String, String> resultMap, ChannelPayRef cpr);

	public JSONObject sendSmsCodeOpenCard(Map<String, String> params);
	
	public JSONObject sendSmsCodeTxn(Map<String, String> params,String signKey);
	/**
	 * 消费支付接口
	 * @param reqPar
	 * @return
	 */
	public JSONObject consume(OrderPayForWeiLianBaoYinLian reqPar, String cardNo);
	
	
}
