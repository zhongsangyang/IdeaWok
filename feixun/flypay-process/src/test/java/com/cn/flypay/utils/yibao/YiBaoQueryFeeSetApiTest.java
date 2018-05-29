package com.cn.flypay.utils.yibao;


import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.QueryFeeSetPartsBuilder;


/**
 * 易宝测试子商户费率查询接口
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring*.xml"})
public class YiBaoQueryFeeSetApiTest {
	
	@Test
	public void testQueryFeeSetApi(){
		try {
			//传递所需参数
			String behavior = "queryFeeSetApi";	//子商户费率查询接口
			
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
			String customerNumber = "10021802302";	//子商户编号
			//1,无卡支付  2，T1自助结算  3，T0自助结算基本  4，T0自助结算工作日额外  5,T0自助结算非工作日额外
			String productType = "1";	//产品类型
			
			StringBuffer signature = new StringBuffer();
			signature.append(customerNumber == null ? "" : customerNumber)
			        .append(mainCustomerNumber == null ? "" : mainCustomerNumber)
			        .append(productType == null ? "" : productType);
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			
			
			Part[] parts = new QueryFeeSetPartsBuilder()
			        .setCustomerNumber(customerNumber)
			        .setMainCustomerNumber(mainCustomerNumber)
			        .setProductType(productType).setHmac(hmac).generateParams();
			
			//调用公用请求模块
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
