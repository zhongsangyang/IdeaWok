package com.cn.flypay.service.account.impl;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.service.account.OrgAccountService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class OrgAccountServiceImplTest {
	@Autowired
	private OrgAccountService orgAccountService;

	@Test
	public void testIsAllowConsumeOrgAccount() {
		String acc = orgAccountService.isAllowConsumeOrgAccount(200, BigDecimal.valueOf(100l), "F20160001");
		System.out.println(acc);
	}

}
