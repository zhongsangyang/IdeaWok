package com.cn.flypay.service.sys.impl;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.pageModel.sys.UserSettlementConfig;
import com.cn.flypay.service.sys.UserSettlementConfigService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class UserSettlementConfigServiceImplTest {
	@Autowired
	private UserSettlementConfigService configService;

	public void testGet() {
		UserSettlementConfig uc = configService.getByUserId(7819l);
		System.out.println();
	}

	public void testgetUserInputRate() {
		BigDecimal bd = configService.getUserInputRate(900, 4l, 1);
		System.out.println(bd);
		System.out.println();
	}

	public void testisAllowReduceInputRate() {
		String falg = configService.isAllowReduceInputRateWhenPoint(4l, "F20160001", 200, 1, 1, 1);
		System.out.println(falg);
	}

	@Test
	public void testgetUserInputRateAndShareRate() {
		BigDecimal[] bds = configService.getUserInputRateAndShareRate(2l, 200, 0);
		System.out.println();
	}
}
