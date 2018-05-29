package com.cn.flypay.service.sys.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.service.sys.MsgHistoryService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class MsgHistoryServiceImplTest {

	@Autowired
	private MsgHistoryService msgHistoryService;

	@Test
	public void testSendSmsToUserPhone() {
		try {
			msgHistoryService.sendSmsToUserPhone("13817117644", "F20160001", 30);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
