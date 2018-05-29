package com.cn.flypay.service.trans.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.service.trans.ShareBonusService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class ShareBonusServiceImplTest {

	@Autowired
	private ShareBonusService shareBonusService;

	@Test
	public void testDealBonusWhenOrder() {
		String orderNum = "YLZX201609291634470160000000004";
		// shareBonusService.dealBonusWhenOrder(orderNum);

	}
}
