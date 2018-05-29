package com.cn.flypay.service.payment.impl;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.HashMap;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class MinshengPaymentServiceImplTest {

	@Autowired
	private ChannelPaymentService minshengPaymentService;
	@Autowired
	private RouteService routeService;

	public void testCreateUnifiedOrder() {
		User u = new User();
		u.setId(4l);
		u.setOrganizationAppName("APPTest");
		try {
			ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.WXQR.getCode(), BigDecimal.valueOf(10), 21, 4l, 0);
			System.out.println();
			minshengPaymentService.createUnifiedOrder(u, cpr, 5, 1d, 10, 0, "test");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testCreateSmUnifiedOrder() {
		fail("Not yet implemented");
	}

	public void testCreateOnLineOrder() {
		fail("Not yet implemented");
	}

	public void testSentOrderNumToChannelForSearchStatus() {
		fail("Not yet implemented");
	}

	@Test
	public void testDealDownloadStatement() {
		minshengPaymentService.dealDownloadStatement("20170322");
	}

	public void testdealChannelT0Tixian() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("operator_id", "2");
		params.put("operator_name", "sy");

		minshengPaymentService.dealChannelT0Tixian(44l, params);
		System.out.println();
	}
}
