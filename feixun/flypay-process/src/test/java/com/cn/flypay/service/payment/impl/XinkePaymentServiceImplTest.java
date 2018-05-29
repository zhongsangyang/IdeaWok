package com.cn.flypay.service.payment.impl;

import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.model.sys.TserviceMerchant;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserMerchantConfig;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.payment.RouteService;
import com.cn.flypay.service.sys.ServiceMerchantService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class XinkePaymentServiceImplTest {

	@Autowired
	private ChannelPaymentService xinkePaymentService;
	@Autowired
	private RouteService routeService;
	@Autowired
	private ServiceMerchantService serviceMerchantService;

	
	public void testCreateUnifiedOrder() {
		User u = new User();
		u.setId(3l);
		u.setOrganizationAppName("飞付支付");
		try {
			ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.WXQR.getCode(), BigDecimal.valueOf(10), 21, 3l, 0);
			xinkePaymentService.createUnifiedOrder(u, cpr, 5, 1d, 10,0, "您正在向商户-冯梁支付1.0元");
			System.out.println();
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
		xinkePaymentService.sendOrderNumToChannelForSearchStatus("WXQR201612200935089730000000004");
	}

	public void testDealDownloadStatement() {
		xinkePaymentService.dealDownloadStatement("20161202");
	}

	public void testDealdealChannelT0Tixian() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("operator_id", "1");
		params.put("operator_name", "13817117644");
		xinkePaymentService.dealChannelT0Tixian(54l, params);
	}

	public void testsendT0TixianSearch() {
		xinkePaymentService.sendT0TixianSearch("XKTX201701051344225100000000001");
	}

	
	@Test
	public void testcreateSubMerchant() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("merchantName", "个体户浦东店麻辣烫");
		params.put("shortName", "浦东新区纳贤路58号");
		TserviceMerchant sm = serviceMerchantService.getTserviceMerchant(4l);
		ServiceMerchant sms = new ServiceMerchant();
		BeanUtils.copyProperties(sm, sms);
		UserMerchantConfig sub = xinkePaymentService.createSubMerchant(sms, params);
	}
}
