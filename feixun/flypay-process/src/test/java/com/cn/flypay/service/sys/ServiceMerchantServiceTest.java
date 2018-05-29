package com.cn.flypay.service.sys;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class ServiceMerchantServiceTest {
	@Autowired
	private ServiceMerchantService serviceMerchantService;
	
	@Autowired
	private HolidayService holidayService;

	@Test
	public void testEditServiceMerchantStatus() {
		if(holidayService.getisYILIAND0Work()){
			System.out.println("关闭了");
		}else{
			System.out.println("开启了");
		}
	}
}
