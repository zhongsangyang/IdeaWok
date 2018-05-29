package com.cn.flypay.service.payment.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.yilian.YiLianYlzxUtil;
import com.rd.constant.ValueConstant;
import com.rd.model.MerchantPayQryReq;

/**
 * 易联银联积分D0直通车测试类
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class FYianZXXEPaymentServiceImplTest {
	@Autowired
	private ChannelPaymentService yfyianZXXEPaymentService;
	@Autowired
	ChannelService channelService;
	@Autowired
	UserOrderService userOrderService;
	/**
	 * 测试查询订单信息
	 */
	@Test
	public void TestQueryYiLianJFOrder(){
		try {
			String orderNum = "YF20171228164448";
			MerchantPayQryReq req = new MerchantPayQryReq();
			//标识此次查询的商户订单号
//			req.setTransactionId("YFYLZXCX"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			req.setTransactionId("YFYLZXCX"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			//原商户订单号
			req.setRefTxnId(orderNum);
			//调用易联通道查询接口
		
			JSONObject result = YiLianYlzxUtil.send(req, ValueConstant.TRANS_CODE_Q01001, YiLianYlzxUtil.merachnetId);
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println();
	}
	
	
	
	
}
