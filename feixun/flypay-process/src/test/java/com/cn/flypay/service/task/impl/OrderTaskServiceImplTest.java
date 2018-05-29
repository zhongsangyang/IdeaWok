package com.cn.flypay.service.task.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.service.task.FuMiTaskService;
import com.cn.flypay.service.task.OrderTaskService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class OrderTaskServiceImplTest {

	@Autowired
	private OrderTaskService orderTaskService;
	
	
	@Autowired
	private FuMiTaskService fuMiTaskService;

	@Test
	public void testDealProcessOrderAfterOneHours() {
		//orderTaskService.dealProcessOrderAfterOneHours();
		fuMiTaskService.updateFuMiTaskUpgrade();
	}

}
