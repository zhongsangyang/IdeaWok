package com.cn.flypay.service.payment.impl;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.payment.RouteService;
import com.cn.flypay.service.sys.ServiceMerchantService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class ZanshanfuPayment_V2_ServiceImplTest {
	@Autowired
	private ChannelPaymentService zanshanfuPayment_V2_Service;
	@Autowired
	private RouteService routeService;
	@Autowired
	private ServiceMerchantService serviceMerchantService;

	@Test
	public void testCreateYLZXOrder_v2() {
		User u = new User();
		u.setId(4l);
		try {
			ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.YLZX.getCode(), BigDecimal.valueOf(1), 21, 4l, 0);
			Map<String, String> resultMap = zanshanfuPayment_V2_Service.createYLZXOrder_v2(u, cpr, 0, 1l, "sss", 5.01d, 10, 0, "test");
			System.out.println(resultMap.get("html"));
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
