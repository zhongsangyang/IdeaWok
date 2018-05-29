package com.cn.flypay.service.payment;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.gazhiyinlian.entities.BindCardForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.OrderPayForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.QueryCardInfoForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.QueryOrderInfoForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.RateAndCardJChangeForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.RegisterForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.SendSMSForGaZhiYinLian;
/**
 * 嘎吱（银联） 通道基础接口
 * @author liangchao
 *
 */
public interface GaZhiYinLainService {
	
	/**
	 * 保存嘎吱(银联)直通车子商户
	 * @param detailName
	 * @param type
	 * @param config
	 * @param userId
	 * @return
	 */
	public Boolean addCreateGaZhiYinLianZhiTongCheChannel(String detailName,Integer type,String config,Long userId);
	/**
	 * 商户注册
	 * @param reqPar
	 * @return
	 */
	public JSONObject createMerchant(RegisterForGaZhiYinLian reqPar);
	
	
	/**
	 * 银联侧绑卡开通
	 * @param reqPar
	 * @return
	 */
	public JSONObject bindCard(BindCardForGaZhiYinLian reqPar);
	
	
	
	/**
	 * 卡开通状态查询
	 * @param reqPar
	 * @return
	 */
	public JSONObject queryBindCardinfo(QueryCardInfoForGaZhiYinLian reqPar);
	
	/**
	 * 发送支付短信
	 * @param reqPar
	 * @return
	 */
	public JSONObject sendSMS(SendSMSForGaZhiYinLian reqPar);
	
	/**
	 * 消费支付接口
	 * @param reqPar
	 * @return
	 */
	public JSONObject orderPay(OrderPayForGaZhiYinLian reqPar);
	
	/**
	 * 支付状态查询
	 * @param reqPar
	 * @return
	 */
	public JSONObject queryOrderInfo(QueryOrderInfoForGaZhiYinLian reqPar);
	
	/**
	 * 商户费率、结算银行卡变更
	 * @param reqPar
	 * @return
	 */
	public JSONObject changeRateAndCardJInfo(RateAndCardJChangeForGaZhiYinLian reqPar);
	
}
