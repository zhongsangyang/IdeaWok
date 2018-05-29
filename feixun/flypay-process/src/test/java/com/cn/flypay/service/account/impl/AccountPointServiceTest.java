package com.cn.flypay.service.account.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.service.account.AccountPointService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class AccountPointServiceTest {

	@Autowired
	private AccountPointService accountPointService;

	public void testGetAccountPointByUserId() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddPointByUserId() throws Exception {
		accountPointService.updatePointByUserId(1l, "D", 1l, "test", "pointTypes_popularity");
		System.out.println();
	}

}
