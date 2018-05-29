package com.cn.flypay.service.sys.impl;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.model.sys.TinfoList;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.service.sys.JiguangPushService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class JiguangPushServiceImplTest {

	@Autowired
	private JiguangPushService jiguangPushService;

	public void testSendMsgInfoToPersonInfoList() {
		TinfoList infoList = new TinfoList();
		infoList.setTitle("小孙来也！！！");
		infoList.setContent("启禀小主，您推荐的18670364008为您赚得1元佣金，现金已经存入您的账户！详见宝贝钱袋分享有礼账户。");
		Tuser user = new Tuser();
		user.setId(4l);
		infoList.setUser(user);
		infoList.setCreateTime(new Date());
		jiguangPushService.sendMsgInfoToPerson(infoList);
	}

	@Test
	public void testsendMsgSoundInfoToPerson() {
		jiguangPushService.sendMsgSoundInfoToPerson(4l);
	}
}
