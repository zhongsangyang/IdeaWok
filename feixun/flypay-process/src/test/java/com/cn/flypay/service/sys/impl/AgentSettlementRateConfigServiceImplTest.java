package com.cn.flypay.service.sys.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.service.sys.AgentSettlementRateConfigService;
import com.cn.flypay.service.sys.OrganizationService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class AgentSettlementRateConfigServiceImplTest {
	@Autowired
	private AgentSettlementRateConfigService agentSettlementRateConfigService;
	@Autowired
	private OrganizationService organizationService;

	@Test
	public void testInitAgentSettlementRateConfigByCopyParent() {
		agentSettlementRateConfigService.initAgentSettlementRateConfigByCopyParent(organizationService.getTorganizationInCacheById(4l));
	}

}
