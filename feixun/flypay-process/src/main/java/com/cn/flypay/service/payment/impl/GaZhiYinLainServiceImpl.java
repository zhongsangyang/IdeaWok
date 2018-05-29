package com.cn.flypay.service.payment.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.service.payment.GaZhiYinLainService;
import com.cn.flypay.utils.gazhiyinlian.GaZhiYinLianUtil;
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
@Service
public class GaZhiYinLainServiceImpl implements GaZhiYinLainService {
	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private BaseDao<Tchannel> channelDao;
	
	
	@Override
	public Boolean addCreateGaZhiYinLianZhiTongCheChannel(String detailName, Integer type, String config, Long userId) {
		try {
			Tchannel t = new Tchannel();
			t.setVersion(Long.parseLong("0"));
			t.setType(type);
			t.setName("GAZHIYINLIANJIFENZHITONGCHE");
			t.setRealRate(new BigDecimal(0.0026));
			t.setRealRate(new BigDecimal(0.0026));
			t.setShowRate(new BigDecimal(0.0049));
			t.setShareRate(new BigDecimal(0.0010));
			t.setMaxTradeAmt(new BigDecimal(100000.00));
			t.setMinTradeAmt(new BigDecimal(0.00));
			t.setStatus(10);
			t.setMaxChannelAmt(new BigDecimal(20000.00));
			t.setMinChannelAmt(new BigDecimal(0.00));
			t.setTodayAmt(new BigDecimal(0.00));
			t.setMaxAmtPerDay(new BigDecimal(200000.00));
			t.setAccount(GaZhiYinLianUtil.rateCode);
			t.setConfig(config);
			t.setDetailName(detailName);
			t.setSeq(880);
			t.setUserType(700);
			t.setCommissionRate(new BigDecimal(0.00));
			t.setUserId(userId);
			t.setMerchantName("嘎吱银联积分直通车");
			t.setShortName("嘎吱银联积分直通车");
			t.setCreateDate(new Date());
			channelDao.save(t);
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	@Override
	public JSONObject createMerchant(RegisterForGaZhiYinLian req) {
		log.info("---嘎吱(银联)通道---调用商户注册接口--开始");
		String encryptStr = JSON.toJSONString(req);
		log.info("---嘎吱(银联)通道---调用商户注册接口--请求参数为"+encryptStr);
		JSONObject res = GaZhiYinLianUtil.send(encryptStr);
		log.info("---嘎吱(银联)通道---调用商户注册接口--接口返回参数为"+res.toJSONString());
		log.info("---嘎吱(银联)通道---调用商户注册接口--结束");
		return res;
	}

	@Override
	public JSONObject bindCard(BindCardForGaZhiYinLian req) {
		log.info("---嘎吱(银联)通道---调用银联侧绑卡开通接口--开始");
		String encryptStr = JSON.toJSONString(req);
		log.info("---嘎吱(银联)通道---调用银联侧绑卡开通接口--请求参数为"+encryptStr);
		JSONObject res = GaZhiYinLianUtil.send(encryptStr);
		log.info("---嘎吱(银联)通道---调用银联侧绑卡开通接口--接口返回参数为"+res.toJSONString());
		log.info("---嘎吱(银联)通道---调用银联侧绑卡开通接口--结束");
		return res;
	}

	@Override
	public JSONObject queryBindCardinfo(QueryCardInfoForGaZhiYinLian req) {
		log.info("---嘎吱(银联)通道---调用卡开通状态查询接口--开始");
		String encryptStr = JSON.toJSONString(req);
		log.info("---嘎吱(银联)通道---调用卡开通状态查询接口--请求参数为"+encryptStr);
		JSONObject res = GaZhiYinLianUtil.send(encryptStr);
		log.info("---嘎吱(银联)通道---调用卡开通状态查询接口--接口返回参数为"+res.toJSONString());
		log.info("---嘎吱(银联)通道---调用卡开通状态查询接口--结束");
		return res;
	}

	@Override
	public JSONObject sendSMS(SendSMSForGaZhiYinLian req) {
		log.info("---嘎吱(银联)通道---发送支付短信接口--开始");
		String encryptStr = JSON.toJSONString(req);
		log.info("---嘎吱(银联)通道---发送支付短信接口--请求参数为"+encryptStr);
		JSONObject res = GaZhiYinLianUtil.send(encryptStr);
		log.info("---嘎吱(银联)通道---发送支付短信接口--接口返回参数为"+res.toJSONString());
		log.info("---嘎吱(银联)通道---发送支付短信接口--结束");
		return res;
	}

	@Override
	public JSONObject orderPay(OrderPayForGaZhiYinLian req) {
		log.info("---嘎吱(银联)通道---消费支付接口接口--开始");
		String encryptStr = JSON.toJSONString(req);
		log.info("---嘎吱(银联)通道---消费支付接口接口--请求参数为"+encryptStr);
		JSONObject res = GaZhiYinLianUtil.send(encryptStr);
		log.info("---嘎吱(银联)通道---消费支付接口接口--接口返回参数为"+res.toJSONString());
		log.info("---嘎吱(银联)通道---消费支付接口接口--结束");
		return res;
	}

	@Override
	public JSONObject queryOrderInfo(QueryOrderInfoForGaZhiYinLian req) {
		log.info("---嘎吱(银联)通道---支付状态查询接口--开始");
		String encryptStr = JSON.toJSONString(req);
		log.info("---嘎吱(银联)通道---支付状态查询接口--请求参数为"+encryptStr);
		JSONObject res = GaZhiYinLianUtil.send(encryptStr);
		log.info("---嘎吱(银联)通道---支付状态查询接口--接口返回参数为"+res.toJSONString());
		log.info("---嘎吱(银联)通道---支付状态查询接口--结束");
		return res;
	}

	@Override
	public JSONObject changeRateAndCardJInfo(RateAndCardJChangeForGaZhiYinLian req) {
		log.info("---嘎吱(银联)通道---商户费率、结算银行卡变更接口--开始");
		String encryptStr = JSON.toJSONString(req);
		log.info("---嘎吱(银联)通道---商户费率、结算银行卡变更接口--请求参数为"+encryptStr);
		JSONObject res = GaZhiYinLianUtil.send(encryptStr);
		log.info("---嘎吱(银联)通道---商户费率、结算银行卡变更接口--接口返回参数为"+res.toJSONString());
		log.info("---嘎吱(银联)通道---商户费率、结算银行卡变更接口--结束");
		return res;
	}

	

}
