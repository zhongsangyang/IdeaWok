package com.cn.flypay.service.statement.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.service.statement.FinanceStatementService;
import com.cn.flypay.utils.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class FinanceStatementServiceImplTest {

	@Autowired
	private FinanceStatementService financeStatementService;

	public void testsaveFinanceProfit() {
//		financeStatementService.saveFinanceProfit();
	}
	
	@Test
	public void getOrgAmt() {
		System.out.println("===============Start===============");
		Map<String, Object> params = new HashMap<String, Object>();
		BigDecimal bd = financeStatementService.getOrgAmtSum(params);
		if(bd != null) {
			System.out.println("Success");
			System.out.println(bd);
			if(bd.equals("0.00")) {
				System.out.println(bd);
			}
				
		}
		System.out.println("===============End===============");
	}
	
}
