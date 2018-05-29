package com.cn.flypay.service.payment.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.cn.flypay.utils.gazhi.GaZhiUtilTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class GaZhiPaymentserviceImplTest {
	@Autowired
	private ChannelPaymentService gaZhiPaymentService;
	@Autowired
	ChannelService channelService;
	public static void main(String[] args) {
		new GaZhiUtilTest().demoPay();
	}
	
	/**
	 * 测试 创建嘎吱的订单，并生成二维码
	 */
	@Test
	public void testCreateUnifiedOrder(){
		//拼接参数
		User u = new User();
		u.setId(2L);
		
		//拼接通道参数
		Channel channel = new Channel();
		channel.setType(200);
		channel.setId(55548L);
		channel.setName("嘎吱支付宝二维码");
		JSONObject channelConfig = channelService.getChannelConfig(55548L);
		channel.setConfig(channelConfig.toString());
		ChannelPayRef cpr = new ChannelPayRef();
		cpr.setChannel(channel);
		cpr.setConfig(channelConfig);
		//代理类型
		Integer angentType=0;
		//支付金额
		Double money =100d;	
		//描述
		String desc = "不超过32位";
		//支付目的
		Integer transPayType  = 10;	//支付类型   10
		// 入账类型 T5 T1 D0等
		Integer inputAccType  = 0;
		
		try {
			Map<String, String> result = gaZhiPaymentService.createUnifiedOrder(u, cpr,inputAccType, money, transPayType ,  angentType,  desc);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 根据订单号，向嘎吱通道发送请求查询订单状态信息
	 */
	@Test
    public void testSendOrderNumToChannelForSearchStatus(){
    	String orderNum = "B0000035944666904805";
    	Map<String, String> result = gaZhiPaymentService.sendOrderNumToChannelForSearchStatus(orderNum);
    	System.out.println(result);
    	
    	
    }
    
    
    /**
	 * 查询嘎吱商户余额
	 */
	@Test
    public void testQueryGaZhiBalance(){
		String merNo = "528531006700089";
    	Map<String, String> result = gaZhiPaymentService.queryGaZhiBalance(merNo);
    	System.out.println(result);
    }
	
	/**
	 * 查询商户提款手续费查询
	 */
	@Test
	public void testQueryGaZhiWithdrawFee(){
		String merNo="528531006700089";
		//String amount = "000000000010";
		String amount = "100000";
		Map<String, String> result = gaZhiPaymentService.queryGaZhiWithdrawFee(merNo,amount);
		System.out.println(result);
	}
	
	
	/**
	 * 嘎吱商户提款
	 */
	@Test
	public void testDrawGaZhi(){
		String merNo ="528531006700089";
		String tranAmt ="100000";
		String factAmt = "99974";
		String feeAmt = "26";
		Map<String, String> result = gaZhiPaymentService.drawGaZhi(merNo,tranAmt,factAmt,feeAmt);
		System.out.println(result);
	}
	
	
	/**
	 * 嘎吱商户提款结果查询
	 */
	@Test
	public void testQueryGaZhiDrawResult(){
		String merNo="528531006700089";
		String oldMerTrace = "B0000021648391445037";
		String oldTransDate=new SimpleDateFormat("yyyyMMdd").format(new Date());
		String oldTransTime=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		Map<String, String> result = gaZhiPaymentService.queryGaZhiDrawResult(merNo,oldMerTrace,oldTransDate,oldTransTime);
		System.out.println(result);
	}
	
	
	
	
	
	
}
