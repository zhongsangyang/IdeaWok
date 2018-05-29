package com.cn.flypay.service.payment.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.service.payment.PingAnExpenseService;
import com.cn.flypay.service.sys.ServiceMerchantService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class PingAnExpenseServiceImplTest {
	@Autowired
	private PingAnExpenseService pingAnExpenseService;

	@Test
	public void testDealBatchT1Result() throws Exception {
		pingAnExpenseService.dealDownLoadStatement("20170330");
	}

}
