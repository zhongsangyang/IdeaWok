package com.cn.flypay.service.sys.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.service.sys.UserMerchantConfigService;
import com.cn.flypay.service.sys.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class UserMerchantConfigServiceImplTest {
	@Autowired
	private UserMerchantConfigService userMerchantConfigService;
	@Autowired
	private UserService userService;

	@Test
	public void testCreateUserMerchants() {
		Tuser user = userService.getTuser(4l);
		userMerchantConfigService.createUserMerchants(user);
	}

}
