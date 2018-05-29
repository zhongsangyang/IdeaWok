package com.cn.flypay.service.payment.impl;

import static org.junit.Assert.fail;

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
public class ZanshanfuPaymentServiceImplTest {
	@Autowired
	private ChannelPaymentService zanshanfuPaymentService;
	@Autowired
	private RouteService routeService;

	@Test
	public void testCreateYLZXOrder() {
		User u = new User();
		u.setId(1l);
		try {
			ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.YLZX.getCode(), BigDecimal.valueOf(11), 21, 4l, 0);
			zanshanfuPaymentService.createYLZXOrder(u, cpr, 5, 5l, "sss", 25d, 10, 0, "test");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testSentOrderNumToChannelForSearchStatus() {
		fail("Not yet implemented");
	}

}
