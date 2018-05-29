package com.cn.flypay.service.payment.impl;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.sys.ChannelService;
/**
 * 哲扬通道测试类3
 * 测试新版本进件和创建订单合并  截止到2017-9-25 
 * 测试信息为芦强
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class ZheYangPaymentServiceImplTest3 {
	@Autowired
	private ChannelPaymentService zheYangPaymentService;
	@Autowired
	ChannelService channelService;
	
	
	/**
	 * 测试哲扬通道--线上支付/快捷支付
	 * 哲扬通道第二种方案，给代理商一个公用商户A,之后交易商户不再进件，全部挂载在A下进行下单，下单时直接传递商户信息。
	 * params中包含的参数
	 */
	@Test
	public void testCreateOnLineOrder2(){
		User u = new User();
		u.setId(2L);
		//拼接通道参数    下面的示例通道，只是为了占位置，具体参数用params存放，因为不知以后的参数存放在何处，暂时这么做
		Channel channel = new Channel();
		channel.setType(200);	//支付类型    快捷支付--对应银联在线  500
		channel.setId(55550L);
		channel.setName("哲扬快捷支付(进件下单整合版)");
		JSONObject channelConfig = channelService.getChannelConfig(55549L);
		channel.setConfig(channelConfig.toString());
		ChannelPayRef cpr = new ChannelPayRef();
		cpr.setChannel(channel);
		cpr.setConfig(channelConfig);
		
		//代理类型
		Integer angentType=0;
		//支付金额
		Double money =200d;	
		//描述
		String desc = "哲扬快捷支付(进件下单整合版)通道测试--芦强";
		//支付目的
		Integer transPayType  = 10;	//支付类型   10  10 普通支付订单
		// 入账类型 T5 T1 D0等
		Integer inputAccType  = 0;
		
		//所有通道config相关信息，存放进params中，因为一个大商户中的银行卡可能会一直增多
		Map<String,String> params = new HashMap<String,String>();
		params.put("mid", "000040000000064");	//配置给代理商的中间商户号
		params.put("encryptId", "000040000000064");   //商户自身的标识
		params.put("key", "663dfe081d2c47a92d931cd2360c87a8");
		params.put("accountNumber", "4218717016411786");	//支付卡号  4218717016411786
		params.put("tel", "12345678910");		//支付银行卡预留手机号  13816111195   可以随便穿	
		params.put("expired", "2105");	//支付卡有效期 YYMM 格式
		params.put("frontUrl", "www.baidu.com");	//支付成功后跳转地址
		params.put("cvv2", "805");
		params.put("fastpayFee", "0.56");	//必填   交易手续费 0.5%传 0.5
		params.put("agencyType", "h5");	//通道标示 由哲扬提供
		params.put("holderName", "芦强");	//支付人姓名
		params.put("idcard", "152822199012293814");	//支付人身份证号  152822199012293814
		params.put("settAccountNumber", "6214851213282739");	//结算卡卡号
		params.put("settAccountTel", "12345678910");	// 必须 结算卡预留手机号（部分通道可以不必填）
		params.put("bankName", "招商银行");	// 必须  结算卡银行名 （部分通道可以不必填）  我们这边必须填写
		//params.put("extraFee", "");	//不必须  额外手续费 （部分通道支持）
		
		
		try {
			Map<String, String> result= zheYangPaymentService.createOnLineOrder2(u, cpr, inputAccType, money, transPayType, desc, params);
			System.out.println(result.get("openHtml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
