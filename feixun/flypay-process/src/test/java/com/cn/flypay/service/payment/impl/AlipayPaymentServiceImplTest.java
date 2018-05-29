package com.cn.flypay.service.payment.impl;

import java.math.BigDecimal;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class AlipayPaymentServiceImplTest {
	@Autowired
	private ChannelPaymentService alipayPaymentService;
	@Autowired
	private RouteService routeService;

	@Test
	public void testCreateUnifiedOrder() {
		User u = new User();
		u.setId(4l);
		u.setOrganizationAppName("APPTest");
		try {
			ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.ALQR.getCode(), BigDecimal.valueOf(10), 21, 2l, 0);
			alipayPaymentService.createUnifiedOrder(u, cpr, 5, 0.01d, 10, 0, "test");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testDealDownloadStatement() {
		alipayPaymentService.dealDownloadStatement("20161109");
	}

}
