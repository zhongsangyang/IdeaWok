package com.cn.flypay.service.payment.impl;

import static org.junit.Assert.*;

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
public class RouteServiceImplTest {
	@Autowired
	private RouteService routeService;

	@Test
	public void testGetChannelPayRoute() {
		User u = new User();
		u.setId(1l);
		try {
			ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.WXQR.getCode(), BigDecimal.valueOf(1), 21, 4l, 0);
			System.out.println(cpr.getChannel());
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
