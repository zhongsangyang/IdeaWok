package com.cn.flypay.controller.payment;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.sys.ChannelService;
/**
 * 哲扬测试
 * @author liangchao
 *
 */
@Controller
@RequestMapping("/testZheYangController")
public class TestZheYangController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ChannelPaymentService zheYangPaymentService;
	@Autowired
	ChannelService channelService;
	
	/**
	 * 测试哲扬通道下单返回跳转至银联支付页面
	 * @return
	 */
	@RequestMapping(value = "/enter")
	public String enter(){
		return "/trans/zheYangYinLianReturnTest";
	}
	
//	@RequestMapping(value = "/testCreateOnLineOrder")
//	public String testCreateOnLineOrder(){
//		User u = new User();
//		u.setId(2L);
//		//拼接通道参数    下面的示例通道，只是为了占位置，具体参数用params存放，因为不知以后的参数存放在何处，暂时这么做
//		Channel channel = new Channel();
//		channel.setType(200);	//支付类型    快捷支付--对应银联在线  500
//		channel.setId(55549L);
//		channel.setName("哲扬通道");
//		JSONObject channelConfig = channelService.getChannelConfig(55549L);
//		channel.setConfig(channelConfig.toString());
//		ChannelPayRef cpr = new ChannelPayRef();
//		cpr.setChannel(channel);
//		cpr.setConfig(channelConfig);
//		
//		//代理类型
//		Integer angentType=0;
//		//支付金额
//		Double money =200d;	
//		//描述
//		String desc = "哲扬通道测试--卢强";
//		//支付目的
//		Integer transPayType  = 10;	//支付类型   10  10 普通支付订单
//		// 入账类型 T5 T1 D0等
//		Integer inputAccType  = 0;
//		
//		//所有通道config相关信息，存放进params中，因为一个大商户中的银行卡可能会一直增多
//		Map<String,String> params = new HashMap<String,String>();
//		params.put("encryptId", "000600002000001");   //商户自身的标识
//		params.put("mid", "000600002000001");	//商户号
//		params.put("key", "083d373c8d9a432f7716af60763eef79");
//		params.put("accountNumber", "4218717016411786");	//卡号
//		params.put("tel", "12345678910");		//预留手机号  13816111195
//		params.put("expired", "2105");	//卡有效期 YYMM 格式
//		params.put("cvv2", "805");
//		params.put("frontUrl", "www.baidu.com");	//支付成功后跳转地址
//		
//		
//		try {
//			Map<String, String> result= zheYangPaymentService.createOnLineOrder(u, cpr, inputAccType, money, transPayType, desc, params);
//			System.out.println(result.get("openHtml"));
//			return result.get("openHtml");
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
	
}
