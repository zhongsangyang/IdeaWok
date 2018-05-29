package com.cn.flypay.utils.yibao;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.WithDrawApiPartsBulider;

/**
 * 易宝结算测试
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring*.xml"})
public class YiBaoWithDrawApiTest {
	@Test
	public void testWithDrawApi(){
		
		String externalNo = "YBJS"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());	//银联在线积分  //结算请求唯一号
		String transferWay = "1";	//1：T0自助结算  2：T1自助结算
		String amount = "11946.00";	//结算金额
		String behavior = "withDrawApi";
		String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
		String customerNumber = "10020967898";	//子商户编号
		
		try {
			//传递所需参数
			
			//可选
			String callBackUrl = "";	//结算回调地址
			StringBuilder signature = new StringBuilder();
			signature
					.append(amount == null ? "" : amount)
					.append(customerNumber == null ? "" : customerNumber)
					.append(externalNo == null ? "" : externalNo)
					.append(mainCustomerNumber == null ? "" : mainCustomerNumber)
					.append(transferWay == null ? "" : transferWay)
					.append(callBackUrl == null ? "" : callBackUrl);
			
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			
			Part[] parts = new WithDrawApiPartsBulider()
					.setMainCustomerNumber(mainCustomerNumber)
					.setCustomerNumber(customerNumber)
					.setExternalNo(externalNo)
					.setTransferWay(transferWay)
					.setAmount(amount)
					.setCallBackUrl(callBackUrl)
					.setHmac(hmac)
					.generateParams();
			
			//调用公用请求模块
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
