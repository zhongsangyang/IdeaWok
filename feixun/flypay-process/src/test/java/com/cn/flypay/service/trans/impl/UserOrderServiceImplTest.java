package com.cn.flypay.service.trans.impl;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.pageModel.trans.FinanceStatement;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.trans.UserOrderService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class UserOrderServiceImplTest {
	@Autowired
	private UserOrderService userOrderService;

	public void testFindFinanceStatement() {
		FinanceStatement statement = new FinanceStatement();
		statement.setStatemtentDateStart("20161112");
		statement.setStatemtentDateEnd("20161112");
		statement.setStatemtentDate("20161112");
		userOrderService.findFinanceStatement(statement);
	}

	public void testexprotExcel() {
		UserOrder userOrder = new UserOrder();
		userOrder.setUserPhone("18068089860");
		userOrder.setStatus(100);
		userOrderService.exportExcel(userOrder);
	}

	@Test
	public void testFinishInputOrderStatus() {
		PayOrder payOrder = new PayOrder();
		payOrder.setPayAmt(BigDecimal.valueOf(168d));
		payOrder.setRealAmt(BigDecimal.valueOf(168d));
		String time_end = "2016-01-01";

		payOrder.setPayNo("123456789");
		payOrder.setPayFinishDate(time_end);

		payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
		try {
			userOrderService.finishInputOrderStatus("ALQR201701231521466840000012837", payOrder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
