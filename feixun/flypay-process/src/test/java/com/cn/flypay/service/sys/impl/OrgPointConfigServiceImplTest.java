package com.cn.flypay.service.sys.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.pageModel.sys.OrgPointConfig;
import com.cn.flypay.service.sys.OrgPointConfigService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class OrgPointConfigServiceImplTest {

	@Autowired
	private OrgPointConfigService orgPointConfigService;

	@Test
	public void testGetByAgentId() {
		OrgPointConfig oc = orgPointConfigService.getByAgentIdAndPayType("F20160001", 200,1);
		System.out.println(oc.getToLowNum());
	}

}
