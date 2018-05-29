package com.cn.flypay.service.sys.impl;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONArray;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.InfoListService;
import com.cn.flypay.utils.XmlMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class ChannelServiceImplTest {

	@Autowired
	private ChannelService channelService;

	@Test
	public void testFindShowChannelLimit() {
		List<Map<String, String>> mls = channelService.findShowChannelLimit(4l, "F20160001");
		System.out.println(JSONArray.toJSONString(mls));
	}

}
