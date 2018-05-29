package com.cn.flypay.service.sys.impl;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.service.sys.UserCardService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class UserCardServiceImplTest {

	@Autowired
	private UserCardService userCardService;

	@Test
	public void testSendCardInfoToBankValidate() throws Exception{
		// String cardNo = json.getString("cardNo");// 卡号
		// String cvv = json.getString("cvv");// cvv
		// String openBankId = json.getString("openBankId");// 虽所在银行
		//
		// String openProvId = json.getString("openProvId");
		// String openAreaId = json.getString("openAreaId");// 城市
		// String openBranchId = json.getString("openBranchId");// 联行号
		// String openBranchName = json.getString("openBranchName");// 支行名称
		//
		// String reservedPhone = json.getString("reservedPhone");// 预留手机号
		// String expiryDate = json.getString("expiryDate");// 有效期
		// String cardType = json.getString("cardType");
		// String chkValue = json.getString("chkValue");
		// String appType = json.getString("appType");

		Map<String, String> cardInfos = new HashMap<String, String>();
		cardInfos.put("merId", "4");
		cardInfos.put("cardNo", "4392260033229160");
		cardInfos.put("reservedPhone", "18068089860");
		cardInfos.put("cardType", "J");
		cardInfos.put("openBankId", "51");

		userCardService.sendCardInfoToBankValidate(cardInfos, true, true, "F20160002");
	}
}
