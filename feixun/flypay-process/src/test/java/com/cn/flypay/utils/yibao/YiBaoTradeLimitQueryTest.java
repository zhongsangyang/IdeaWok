package com.cn.flypay.utils.yibao;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.LimitAmountQueryPartsBuilder;


/**
 * 易宝测试子商户限额查询接口
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring*.xml"})
public class YiBaoTradeLimitQueryTest {
	@Test
	public void testTradeLimitQuery(){
		try {
			//传递所需参数
			String behavior = "tradeLimitQuery";	//子商户设置限额接口
			
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
			String customerNumber = "";	//子商户编号
			
			//1,店主代付  2，非店主支付  3，小商户  4，非店主支付卡
			String tradeLimitConfigKey = "";	//限额类型
			String bankCardType = "";	//银行卡类型		DEBIT 借记卡  CREDIT 信用卡
			//可选
			String bankCardNo = "";	//支付银行卡卡号	
			
			StringBuffer signature = new StringBuffer();
			signature.append(customerNumber == null ? "" : customerNumber)
			        .append(mainCustomerNumber == null ? "" : mainCustomerNumber)
			        .append(bankCardType == null ? "" : bankCardType)
			        .append(bankCardNo == null ? "" : bankCardNo)
			        .append(tradeLimitConfigKey == null ? "" : tradeLimitConfigKey);
			
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			
			Part[] parts = new LimitAmountQueryPartsBuilder()
			        .setTradeLimitConfigKey(tradeLimitConfigKey)
			        .setCustomernumber(customerNumber)
			        .setMainCustomerNumber(mainCustomerNumber)
			        .setBankCardType(bankCardType).setBankCardNo(bankCardNo)
			        .setHmac(hmac).generateParams();
			
			//调用公用请求模块
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
