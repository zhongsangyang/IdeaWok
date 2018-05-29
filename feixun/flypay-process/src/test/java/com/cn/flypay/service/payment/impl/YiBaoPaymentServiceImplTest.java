package com.cn.flypay.service.payment.impl;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.payment.YiBaoService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.yibao.YiBaoBaseUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class YiBaoPaymentServiceImplTest {
	@Autowired
	UserOrderService userOrderService;
	@Autowired
	private ChannelPaymentService yiBaoPaymentService;
	@Autowired
	private YiBaoService yiBaoService;
	
	/**
	 * 测试更改子商户结算卡
	 */
	
	@Test
	public void testChangeCard(){
		
		JSONObject res = yiBaoService.changeMerchantCard( "10017949988", "6216910203870123", "民生银行");
		
	}
	
	/**
	 * 测试查询子商户信息
	 */
	@Test
	public void testQueryMerInfo(){
		
		JSONObject res = yiBaoService.queryMerchantInfo("13799798325" ,"10018031640");
		System.out.println("233333"+res.toJSONString());
	}
	
	
	
	/**
	 * 测试查询订单
	 */
	@Test
	public void testCreateMer(){
		
		Long userId = 77924l;
		Map<String, String> res = yiBaoPaymentService.createSubMerchantByUserId(userId);
		
		System.out.println(res.toString());
		
	}
	
	@Test
	public void tsetQueryOrder(){
		String orderNum = "YB20171224193859";
		Map<String, String> res = yiBaoPaymentService.sendOrderNumToChannelForSearchStatus(orderNum);
		
		
	}
	
}
