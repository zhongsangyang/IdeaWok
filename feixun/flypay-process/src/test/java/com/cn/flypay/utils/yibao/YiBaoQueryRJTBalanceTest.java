package com.cn.flypay.utils.yibao;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.QueryRJTBalancePartsBulider;
/**
 * 易宝垫资额度查询接口
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring*.xml"})
public class YiBaoQueryRJTBalanceTest {
	
	@Test
	public void testQueryRJTBalance(){
		try {
			//传递所需参数
			String behavior = "queryRJTBalance";
			
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
			
			
			StringBuilder signature = new StringBuilder();
			signature.append(mainCustomerNumber == null ? "" : mainCustomerNumber);
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			
			Part[] parts = new QueryRJTBalancePartsBulider()
			        .setMainCustomerNumber(mainCustomerNumber).setHmac(hmac)
			        .generateParams();
			
			//调用易宝公用请求模块
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
