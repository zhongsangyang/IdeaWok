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
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.sys.ChannelService;

/**
 * 哲扬通道测试类2
 * 测试信息为芦强
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class ZheYangPaymentServiceImplTest2 {

	
	@Autowired
	private ChannelPaymentService zheYangPaymentService;
	@Autowired
	ChannelService channelService;
	
	/**
	 * 测试创建子商户
	 * 
	 */
	@Test
	public void testCreateSubMerchant() {
		Map<String,String> params = new HashMap<String,String>();
		
		//通用参数    这里是代理商的信息
		params.put("encryptId", "000600002");   //平台商户自身的标识
		params.put("key", "1caf52928f1ba24d71e744fe02e5621d"); //平台商户自身的公钥
		//代理商号
		params.put("aid",  "000600002");	//我们公司就是一个商户性质的代理商
		
		
		//调用的方法
		params.put("method", "addPersonalMerchant");
		//商户姓名
		params.put("name", "芦强");
		//联系电话   13816111195   
		params.put("cellPhone", "12345678910");
		//身份证号
		params.put("idCard", "152822199012293814");
		//银行卡号
		params.put("accountNumber", "6214851213282739");
		//预留手机号  13816111195
		params.put("bankAccountTel", "12345678910");
		//交易手续费   可选
		//params.put("fastpayFee", "0.5D");
		
		ServiceMerchant sm = new ServiceMerchant();
		
		zheYangPaymentService.createSubMerchant(sm,params);
	}
	
	/**
	 * 测试哲扬通道--商户新增信用卡
	 */
	@Test
	public void addCreditCard(){
		Map<String, String> params = new HashMap<String,String>();
		
		//通用参数
		params.put("encryptId", "000600002000002");   //商户自身的标识
		params.put("mid", "000600002000002");
		params.put("key", "56c4ca18ee04b3f111677d3d6d0be98c"); //商户自身的公钥
		
		//信用卡卡号
		params.put("accountNumber", "4218717016411786");
		//预留手机号 13816111195
		params.put("tel", "12345678910");
		//手持信用卡正面 使用图片上传返回的 imageId
		//params.put("creditCardImageId", "");
		//信用卡有效期  格式 yyMM 如 1905
		params.put("expired", "2105");
		zheYangPaymentService.addCreditCard(params);
		
	}
	
	
	/**
	 * 测试哲扬通道--开通支付接口   平台不用
	 */
	/*@Test
	public void fastpayOpenToken(){
		Map<String, String> params = new HashMap<String,String>();
		
		//通用参数
		params.put("encryptId", "000040036000021");   //商户自身的标识
		params.put("key", "c68d30ce3a769c29b13c92f8ad5e7073"); //商户自身的公钥
		
		//代理商号
		params.put("mid", "000040036000021");
		//银行卡号
		params.put("accountNumber", "6212261001045119887");
		//开卡成功前端回调地址
		//params.put("frontUrl", "");
		
		zheYangPaymentService.fastpayOpenToken(params);
		
	}*/
	
	/**
	 * 测试哲扬通道--线上支付/快捷支付
	 */
	@Test
	public void testCreateOnLineOrder(){
		User u = new User();
		u.setId(2L);
		//拼接通道参数    下面的示例通道，只是为了占位置，具体参数用params存放，因为不知以后的参数存放在何处，暂时这么做
		Channel channel = new Channel();
		channel.setType(200);	//支付类型    快捷支付--对应银联在线  500
		channel.setId(55549L);
		channel.setName("哲扬通道");
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
		String desc = "哲扬通道测试--卢强";
		//支付目的
		Integer transPayType  = 10;	//支付类型   10  10 普通支付订单
		// 入账类型 T5 T1 D0等
		Integer inputAccType  = 0;
		
		//所有通道config相关信息，存放进params中，因为一个大商户中的银行卡可能会一直增多
		Map<String,String> params = new HashMap<String,String>();
		params.put("encryptId", "000600002000002");   //商户自身的标识
		params.put("mid", "000600002000002");	//商户号
		params.put("key", "56c4ca18ee04b3f111677d3d6d0be98c");
		params.put("accountNumber", "4218717016411786");	//卡号  4218717016411786
		params.put("tel", "12345678910");		//预留手机号  13816111195
		params.put("expired", "2105");	//卡有效期 YYMM 格式
		params.put("cvv2", "805");
		params.put("frontUrl", "www.baidu.com");	//支付成功后跳转地址
		//交易手续费   可选
		//params.put("fastpayFee", "0.5D");
		
		try {
			Map<String, String> result= zheYangPaymentService.createOnLineOrder(u, cpr, inputAccType, money, transPayType, desc, params);
			System.out.println(result.get("openHtml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试确认支付
	 */
	@Test
	public void testFastpayCheckMessage(){
		Map<String, Object> params = new HashMap<String,Object>();
		
		//通用参数
		params.put("encryptId", "000040036000025");
		params.put("mid", "000040036000025");
		params.put("key", "ba7d6c12c05166489980a5c80a4abc8d");
		
		params.put("bizOrderNumber", "B0000202873471381962");	//商户订单号
		params.put("tn", "1709011713390014");	//第三方订单号
		params.put("smsCode", "551214");	//短信验证码
		params.put("accountNumber", "4218717016411786");	//卡号
		params.put("tel", "13816111195");	//预留手机号
		params.put("expired", "2105");	//有效期
		params.put("cvv2", "805");	//Cvv2
		params.put("srcAmt", 100d);	///订单金额
		
		Map<String,String> result = zheYangPaymentService.fastpayCheckMessage(params);
		
	}
	
	
	/**
	 * 测试哲扬通道--查询订单状态
	 */
	@Test
	public void testSendOrderNumToChannelForSearchStatus(){
		
		try {
			String bizOrderNumber = "B0000884104971357694";
			Map<String, String> result = zheYangPaymentService.sendOrderNumToChannelForSearchStatus(bizOrderNumber);
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * 修改结算卡接口
	 */
	@Test
	public void testupdateBankAccountInfo(){
		try {
			Map<String,String> params = new HashMap<String,String>();
			//提供公共参数
			params.put("encryptId", "000600002");	
			
			params.put("mid", "000600002");		//商户号
			params.put("key", "1caf52928f1ba24d71e744fe02e5621d");	//key
			params.put("accountNumber", "6214851213282739");	//银行卡号    借记卡    信用卡    6214851213282739
			params.put("tel", "12345678910");	//预留手机号 13816111195
			
			Map<String,String> result =zheYangPaymentService.updateBankAccountInfo(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 *修改商户费率接口(第二天生效)，哲扬无参数结果返回
	 */
	@Test
	public void testUpdateBankAccountInfo(){
		try {
			Map<String,Object> params = new HashMap<String,Object >();
			//提供公共参数
			params.put("encryptId", "000040036000031");	
			params.put("key", "4c5c6e87928bfcdf4211d235b2f007b1");	//key
			params.put("mid", "000040036000031");
			params.put("fastpayFee", 0.12D);
			Map<String,String> result =zheYangPaymentService.updateFastPayFee(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
