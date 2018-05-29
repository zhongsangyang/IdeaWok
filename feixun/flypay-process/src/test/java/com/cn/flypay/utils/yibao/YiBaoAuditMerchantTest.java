package com.cn.flypay.utils.yibao;



import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.AuditMerchantPartsBuilder;



/**
 * 易宝测试子商户审核
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring*.xml"})
public class YiBaoAuditMerchantTest {
	
	
	@Test
	public void testAuditMerchant(){
		try {
			//传递所需参数
			String behavior = "auditMerchant";	//子商户审核
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
			String customerNumber = "";	//子商户编号
			String status = "SUCCESS";		//审核状态	FALIED	审核不通过  SUCCESS	审核通过
			//可选
			String reason = "";	//审核原因 	审核状态为FALIED时必填
			
			
			StringBuilder signature = new StringBuilder();
			signature.append(mainCustomerNumber == null ? "" : mainCustomerNumber)
					.append(customerNumber == null ? "" : customerNumber)
			        .append(status == null ? "" : status)
			        .append(reason == null ? "" : reason);
			
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			
			Part[] parts = new AuditMerchantPartsBuilder()
			        .setMainCustomerNumber(mainCustomerNumber)
			        .setCustomerNumber(customerNumber).setStatus(status)
			        .setReason(reason)
			        .setHmac(hmac).generateParams();
			
			
			//调用公用请求模块
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
}
