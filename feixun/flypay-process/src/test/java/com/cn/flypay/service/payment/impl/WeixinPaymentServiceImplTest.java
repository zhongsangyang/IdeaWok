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
public class WeixinPaymentServiceImplTest {

	@Autowired
	private ChannelPaymentService weixinPaymentService;
	@Autowired
	private RouteService routeService;

	public void testCreateUnifiedOrder() {
		User u = new User();
		u.setId(1l);
		try {
			ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.WXQR.getCode(), BigDecimal.valueOf(1), 21, 4l, 0);
			weixinPaymentService.createUnifiedOrder(u, cpr, 5, 1d, 10,0, "test");
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
		weixinPaymentService.dealDownloadStatement("20161109");
	}

}
