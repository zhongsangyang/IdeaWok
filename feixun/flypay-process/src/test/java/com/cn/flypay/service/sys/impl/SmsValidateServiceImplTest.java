package com.cn.flypay.service.sys.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.pageModel.sys.SysMsgHistory;
import com.cn.flypay.service.sys.JiguangPushService;
import com.cn.flypay.service.sys.SmsValidateService;
import com.cn.flypay.utils.StringUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class SmsValidateServiceImplTest {
	@Autowired
	private SmsValidateService smsValidateService;

	public void testSendMsgValidate() {
		fail("Not yet implemented");
	}

	@Test
	public void testsendMsgValidate() {
		smsValidateService.sendMsgValidate("13817117644",
				SysMsgHistory.SMS_TYPE_TEMPLES.get(StringUtil.getAgentId("F20160001") + "_" + 50), "6325", "F20160001");
	}

}
