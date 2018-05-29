package com.cn.flypay.utils.yibao;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.LendTargetFeeQueryBuilder;


/**
 * 易宝结算手续费查询
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring*.xml"})
public class YiBaoLendTargetFeeQueryTest {
	
		
	@Test
	public void testLendTargetFeeQuery(){
		
		try {
			//传递所需参数
			String behavior = "lendTargetFeeQuery";
			
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
			String customerNumber = "";	//子商户编号
			
			String transType = "";	//1:T0自助结算  2：T1自助结算
			String transAmount = "";	//结算金额
			
			StringBuilder signature = new StringBuilder();
			signature
			    .append(mainCustomerNumber == null ? "" : mainCustomerNumber)
			    .append(customerNumber == null ? "" : customerNumber)
			    .append(transType == null ? "" : transType)
			    .append(transAmount == null ? "" : transAmount);
			
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			
			
			Part[] parts = new LendTargetFeeQueryBuilder()
			        .setMainCustomerNumber(mainCustomerNumber)
			        .setCustomerNumber(customerNumber)
			        .setTransAmount(transAmount).setTransType(transType)
			        .setHmac(hmac).generateParams();
			
			//调用公用请求模块
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
}
