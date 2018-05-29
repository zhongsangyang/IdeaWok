package com.cn.flypay.service.account.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.service.account.AccountPointHistoryService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class AccountPointHistoryServiceImplTest {
	@Autowired
	private AccountPointHistoryService accountPointHistoryService;

	@Test
	public void testAddPointHistory() {
		accountPointHistoryService.addPointHistory(1l, "C", 1l, "ss");
	}

}
