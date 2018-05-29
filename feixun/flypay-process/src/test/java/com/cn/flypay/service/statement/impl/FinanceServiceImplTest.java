package com.cn.flypay.service.statement.impl;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.pageModel.account.AgentFinanceProfit;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.statement.FinanceService;
import com.cn.flypay.utils.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class FinanceServiceImplTest {
	@Autowired
	private FinanceService financeService;

	public void testFindRealTimeAccount() {
		fail("Not yet implemented");
	}

	public void testFindFinanceProfit() throws ParseException {
		financeService.findFinanceProfitByDateInterval(DateUtil.convertStringToDate("yyyyMMdd", "20161101"), new Date());
	}

	public void testfindFinanceProfitListByDateInterval() throws ParseException {
		User user = new User();
		user.setAgentId("F20160001");
		financeService.findFinanceProfitListByDateInterval(DateUtil.convertStringToDate("yyyyMMdd", "20161101"), new Date(), user);
	}

	public void testfindPlatformChannelProfitListByDateInterval() throws ParseException {
		financeService.findPlatformChannelProfitListByDateInterval(DateUtil.convertStringToDate("yyyyMMdd", "20161101"), new Date(), null);
	}

	@Test
	public void testfindAgentFinanceProfitListByDateInterval() throws ParseException {
		User user = new User();
		user.setAgentId("F20160001");
		List<AgentFinanceProfit> aps = financeService.findAgentFinanceProfitListByDateInterval(DateUtil.convertStringToDate("yyyyMMdd", "20170210"), new Date(), user);
		for (AgentFinanceProfit ap : aps) {
			JSONObject json = (JSONObject) JSONObject.toJSON(ap);
			System.out.println(json);

		}
	}

	public void testexportPlatformChannelProfitListByDateInterval() throws ParseException, IOException {
		Workbook wb = financeService.exportPlatformChannelProfitListByDateInterval(DateUtil.convertStringToDate("yyyyMMdd", "20161105"), new Date(), null);
		FileOutputStream ouputStream = new FileOutputStream(new File("d:/test1.xls"));
		wb.write(ouputStream);
	}
}
