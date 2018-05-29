package com.cn.flypay.service.sys.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.pageModel.account.PlatformOrgConfig;
import com.cn.flypay.service.sys.PlatformOrgConfigService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class PlatformOrgConfigServiceImplTest {

	@Autowired
	private PlatformOrgConfigService PlatformOrgConfigService;

	@Test
	public void testGetPlatformOrgConfig() {
		PlatformOrgConfig poc = PlatformOrgConfigService.getPlatformOrgConfig("F20160001");
		System.out.println(poc.getOrgName());
	}
}
