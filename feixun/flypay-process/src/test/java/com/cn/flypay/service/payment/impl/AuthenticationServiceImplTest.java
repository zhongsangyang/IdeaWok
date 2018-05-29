package com.cn.flypay.service.payment.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.pageModel.sys.AuthenticationLog;
import com.cn.flypay.service.payment.AuthenticationService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class AuthenticationServiceImplTest {
	@Autowired
	private AuthenticationService authenticationService;

	@Test
	public void testSendInfoToAuthentication() {
		AuthenticationLog authLog = new AuthenticationLog();
		try {
			authLog.setCardNo("5124669941150809");
			authLog.setIdNo("130133198405120026");
			authLog.setRealName("高亚敏");
			authLog.setPhone("13315130060");
			authenticationService.sendInfoToAuthentication(authLog);
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void testisAllowAuthentication() {
		boolean b = authenticationService.isAllowAuthentication(4l, "370322198703213112");
		System.out.println(b);
	}

}
