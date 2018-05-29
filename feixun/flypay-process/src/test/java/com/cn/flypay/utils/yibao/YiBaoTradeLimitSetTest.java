package com.cn.flypay.utils.yibao;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.LimitAmountSetPartsBuilder;


/**
 * 易宝测试子商户设置限额接口
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring*.xml"})
public class YiBaoTradeLimitSetTest {
	
	@Test
	public void testTradeLimitSet(){
		
		try {
			//传递所需参数
			String behavior = "tradeLimitSet";	//子商户设置限额接口
			
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
			String customerNumber = "";	//子商户编号
			
			//1,店主代付  2，非店主支付  3，小商户  4，非店主支付卡
			String tradeLimitConfigKey = "";	//限额类型
			String bankCardType = "";	//银行卡类型		DEBIT 借记卡  CREDIT 信用卡
			
			//可选
			String bankCardNo = "";	//支付银行卡卡号	
			
			String singleAmount = "";	//单笔限额
			String dayAmount = "";	//日限额
			String monthAmount = "";	//月限额
			String dayCount = "";	//日累计次数
			String monthCount = "";	//月累计次数
			
			StringBuffer signature = new StringBuffer();
			signature.append(customerNumber == null ? "" : customerNumber)
			    .append(mainCustomerNumber == null ? ""
			            : mainCustomerNumber)
			    .append(bankCardType == null ? "" : bankCardType)
			    .append(bankCardNo == null ? "" : bankCardNo)
			    .append(tradeLimitConfigKey == null ? "" : tradeLimitConfigKey)
			    .append(singleAmount == null ? "" : singleAmount)
			    .append(dayAmount == null ? "" : dayAmount)
			    .append(monthAmount == null ? "" : monthAmount)
			    .append(dayCount == null ? "" : dayCount)
			    .append(monthCount == null ? "" : monthCount);
			
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			Part[] parts = new LimitAmountSetPartsBuilder()
			        .setTradeLimitConfigKey(tradeLimitConfigKey)
			        .setCustomernumber(customerNumber)
			        .setMainCustomerNumber(mainCustomerNumber)
			        .setBankCardType(bankCardType).setBankCardNo(bankCardNo)
			        .setSingleAmount(singleAmount).setDayAmount(dayAmount)
			        .setMonthAmount(monthAmount).setDayCount(dayCount)
			        .setMonthCount(monthCount).setHmac(hmac).generateParams();
			
			//调用公用请求模块
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		
		
	}
}
