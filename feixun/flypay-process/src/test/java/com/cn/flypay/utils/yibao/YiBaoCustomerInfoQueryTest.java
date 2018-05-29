package com.cn.flypay.utils.yibao;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.CustomerInforQueryPartsBuilder;

/**
 * 易宝测试子商户信息查询
 * 
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class YiBaoCustomerInfoQueryTest {
	@Test
	public void testCustomerInfoQuery() {

		try {
			// 传递所需参数
			String behavior = "customerInforQuery"; // 子商户信息查询
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber; // 代理商编号
			String mobilePhone = "13697791286"; // 注册手机号17602370555
			// 可选
			String customerNumber = "10021802302"; // 子商户编号/分润方编号
			// 可选
			String customerType = "CUSTOMER"; // 商户类型 不传默认为： CUSTOMER 子商户
												// SPLITTER 分润方

			// 按照参数顺序进行排列，不包括上传的文件
			StringBuffer signature = new StringBuffer();
			signature.append(mainCustomerNumber == null ? "" : mainCustomerNumber)
					.append(mobilePhone == null ? "" : mobilePhone)
					.append(customerNumber == null ? "" : customerNumber)
					.append(customerType == null ? "" : customerType);
			// 生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);

			Part[] parts = new CustomerInforQueryPartsBuilder().setMainCustomerNumber(mainCustomerNumber).setMobilePhone(mobilePhone)
					 .setCustomerNumber(customerNumber)
					.setCustomerType(customerType).setHmac(hmac).generateParams();

			// 调用公用请求模块
			JSONObject result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
			System.out.println(result.toJSONString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
