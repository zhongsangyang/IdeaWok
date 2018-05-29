package com.cn.flypay.utils.yibao;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.CustomerBalanceQueryPartsBulider;

/**
 * 易宝可用余额查询测试类
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring*.xml"})
public class YiBaoCustomerBalanceQueryTest {

	
	@Test
	public void testCustomerBalanceQuery(){
		
		try {
			//传递所需参数
			String behavior = "customerBalanceQuery";
			
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
			String customerNumber = "10020967898";	//子商户编号
			String balanceType = "3";	//可用余额类型 1:T0自助结算可用余额 2：T1自助结算可用余额  3：账户余额
			
			StringBuilder signature = new StringBuilder();
			signature.append(mainCustomerNumber == null ? "" : mainCustomerNumber)
					.append(customerNumber == null ? "" : customerNumber)
			        .append(balanceType == null ? "" : balanceType);
			
			
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			
			Part[] parts = new CustomerBalanceQueryPartsBulider()
			        .setMainCustomerNumber(mainCustomerNumber)
			        .setCustomerNumber(customerNumber)
			        .setBalanceType(balanceType)
			        .setHmac(hmac)
			        .generateParams();
			
			//调用公用请求模块
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
			System.out.println(result.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
