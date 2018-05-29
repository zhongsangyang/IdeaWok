package com.cn.flypay.service.common.impl;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.service.common.ProducerService;
import com.cn.flypay.service.sys.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class ProducerServiceImplTest {

	@Autowired
	private ProducerService producerService;
	@Autowired
	private UserService userService;

	@Test
	public void testSendInfoList() {
		InfoList infoList = new InfoList();
		infoList.setUserId(4l);
		infoList.setTitle("test");
		infoList.setCreateTime(new Date());
		infoList.setInfoType(1);
		infoList.setContent("test2");
		infoList.setIsForce(0);
		infoList.setForceHours(4);
		infoList.setOrganizationId(1l);
		producerService.sendInfoList(infoList);
	}
}