package com.cn.flypay.service.sys.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.service.sys.OrgChannelUserRateConfigService;
import com.cn.flypay.service.sys.OrganizationService;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class OrgChannelUserRateConfigServiceImplTest {
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private OrgChannelUserRateConfigService orgChannelUserRateConfigService;

	@Test
	public void testInitOrgChannelUserRateConfigByCopyParent() {
		orgChannelUserRateConfigService.initOrgChannelUserRateConfigByCopyParent(organizationService.getTorganizationInCacheById(3l));
	}

}