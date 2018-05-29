package com.cn.flypay.service.task.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class OfflineDrawOrderServiceImplTest {

	@Autowired
	private com.cn.flypay.service.trans.OfflineDrawOrderService OfflineDrawOrderService;

	@Test
	public void download() {

		String fullFileName = OfflineDrawOrderService.dealDownloadOrder();
		System.out.println("fullFileName=" + fullFileName);

	}

}
