package com.cn.flypay.service.payment.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.service.payment.PaymentService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class PaymentServiceImplTest {

	@Autowired
	private PaymentService paymentService;

	@Test
	public void testUpdateAccountWhenTransferAccount() {
		paymentService.updateAccountWhenTransferAccount(2l, 4l, 10d, "test");
	}

}
