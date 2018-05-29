package com.cn.flypay.pingan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.cn.flypay.utils.DateUtil;

/**
 * 项目：fs_liquidator_platform_openapi
 * 包名：com.fshows.liquidator.platform.openapi.controller 功能： 时间：2016-08-23 作者：呱牛
 */
public class ApiDemo {

	public static void main(String args[]) throws AlipayApiException, IOException {
		/**
		 * 利用OpenSSL工具生成RSA密钥步骤
		 * 
		 * 首先进入OpenSSL工具，再输入以下命令。 OpenSSL> genrsa -out app_private_key.pem 1024
		 * #生成私钥 OpenSSL> pkcs8 -topk8 -inform PEM -in app_private_key.pem
		 * -outform PEM -nocrypt -out app_private_key_pkcs8.pem
		 * #Java开发者需要将私钥转换成PKCS8格式 OpenSSL> rsa -in app_private_key.pem -pubout
		 * -out app_public_key.pem #生成公钥 OpenSSL> exit #退出OpenSSL程序
		 * 
		 * 经过以上步骤，开发者可以在当前文件夹中（OpenSSL运行文件夹），看到app_private_key.pem（开发者RSA私钥）、
		 * app_private_key_pkcs8
		 * .pem（pkcs8格式开发者RSA私钥）和app_public_key.pem（开发者RSA公钥）3个文件。
		 * 开发者将私钥保留，将公钥去除头尾、换行和空格，仅需填入字符串,提交给清算平台配置到平台中，用于验证签名。
		 * 
		 * 注意：对于使用Java的开发者，将pkcs8在console中输出的私钥去除头尾、换行和空格，
		 * 作为开发者私钥，对于.NET和PHP的开发者来说，无需进行pkcs8命令行操作。
		 * 
		 * 
		 * 标准的私钥文件示例（PHP、.NET使用） -----BEGIN RSA PRIVATE KEY-----
		 * MIICXQIBAAKBgQC+
		 * L0rfjLl3neHleNMOsYTW8r0QXZ5RVb2p/vvY3fJNNugvJ7lo4+fdBz+LN4mDx
		 * Tz4MTOhi5e2yeAqx
		 * +v3nKpNmPzC5LmDjhHZURhwbqFtIpZD51mOfno2c3MDwlrsVi6mTypbNu4uaQ zw/
		 * TOpwufSLWF7k6p2pLoVmmqJzQiD0QIDAQABAoGAakB1risquv9D4zX7hCv9MTFwGyKSfpJOYhk
		 * IjwKAik7wrNeeqFEbisqv35FpjGq3Q1oJpGkem4pxaLVEyZOHONefZ9MGVChT
		 * /MNH5b0FJYWl392R Zy8KCdq376Vt4gKVlABvaV1DkapL+nLh7LMo/
		 * bENudARsxD55IGObMU19lkCQQDwHmzWPMHfc3kdY
		 * 6AqiLrOss+MVIAhQqZOHhDe0aW2gZtwiWeYK1wB
		 * /fRxJ5esk1sScOWgzvCN/oGJLhU3kipHAkEAys
		 * NoSdG2oWADxlIt4W9kUiiiqNgimHGMHPwp4JMxupHMTm7D9XtGUIiDijZxunHv3kvktNfWj3Yji06
		 * 61zHVJwJBAM8TDf077F4NsVc9AXVs8N0sq3xzqwQD/HPFzfq6hdR8tVY5yRMb4X7+
		 * SX4EDPORKKsg nYcur5lk8MUi7r072iUCQQC8xQvUne+
		 * fcdpRyrR4StJlQvucogwjTKMbYRBDygXkIlTJOIorgudFl
		 * rKP/HwJDoY4uQNl8gQJb/1L
		 * drKwIe7FAkBl0TNtfodGrDXBHwBgtN/t3pyi+sz7OpJdUklKE7zMSB
		 * uLd1E3O4JMzvWP9wEE7JDb+brjgK4/cxxUHUTkk592 -----END RSA PRIVATE
		 * KEY-----
		 * 
		 * 
		 * PKCS8处理后的私钥文件示例（Java使用） -----BEGIN PRIVATE KEY-----
		 * MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAN0yqPkLXlnhM
		 * +2H/57aHsYHaHXaz
		 * r9pFQun907TMvmbR04wHChVsKVgGUF1hC0FN9hfeYT5v2SXg1WJSg2tSgk7F29SpsF0I36oSLCIsz
		 * xdu7ClO7c22mxEVuCjmYpJdqb6XweAZzv4Is661jXP4PdrCTHRdVTU5zR9xUByiLSVAgMBAAECgYE
		 * AhznORRonHylm9oKaygEsqQGkYdBXbnsOS6busLi6xA
		 * +iovEUdbAVIrTCG9t854z2HAgaISoRUKyz tJoOtJfI1wJaQU+XL+U3JIh4jmNx/
		 * k5UzJijfvfpT7Cv3ueMtqyAGBJrkLvXjiS7O5ylaCGuB0Qz7
		 * 11bWGkRrVoosPM3N6ECQQD8hVQUgnHEVHZYtvFqfcoq2g
		 * /onPbSqyjdrRu35a7PvgDAZx69Mr/Xgg
		 * GNTgT3jJn7+2XmiGkHM1fd1Ob/3uAdAkEA4D7aE3ZgXG
		 * /PQqlm3VbE/+4MvNl8xhjqOkByBOY2ZFf
		 * WKhlRziLEPSSAh16xEJ79WgY9iti+guLRAMravGrs2QJBAOmKWYeaWKNNxiIoF7
		 * /4VDgrcpkcSf3u RB44UjFSn8kLnWBUPo6WV+x1FQBdjqRviZ4NFGIP+
		 * KqrJnFHzNgJhVUCQFzCAukMDV4PLfeQJSmna 8PFz2UKva8fvTutTryyEYu+
		 * PauaX5laDjyQbc4RIEMU0Q29CRX3BA8WDYg7YPGRdTkCQQCG+pjU2F
		 * B17ZLuKRlKEdtXNV6zQFTmFc1TKhlsDTtCkWs
		 * /xwkoCfZKstuV3Uc5J4BNJDkQOGm38pDRPcUDUh 2/ -----END PRIVATE KEY-----
		 * 
		 * 
		 * 公钥文件示例 -----BEGIN PUBLIC KEY-----
		 * MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQWiDVZ7XYxa4CQsZoB3n7bfxLDkeGKjyQPt2FU
		 * tm4TWX9OYrd523iw6UUqnQ
		 * +Evfw88JgRnhyXadp+vnPKP7unormYQAfsM/CxzrfMoVdtwSiGtIJB4
		 * pfyRXjA+KL8nIa2hdQy5nLfgPVGZN4WidfUY/QpkddCVXnZ4bAUaQjXQIDAQAB
		 * -----END PUBLIC KEY-----
		 * 
		 */

		/**
		 * 请求OPEN API接口步骤 1.本地生成签名。 2.发起API请求, 请求参数参考open api文档,
		 * sign值为第一步中得到的签名。 接口返回值参考OPEN API接口文档中各个接口的响应值, json格式。
		 */

		/**
		 * 本地生成签名 调用支付宝SDK AlipaySignature.rsaSign()方法 参数: 类型 说明 示例 app_id
		 * String 清算方唯一ID 123456789 method String 具体API名称
		 * fshows.liquidation.submerchant.query version String 接口版本号 1.0 content
		 * String 具体接口参数 {"sub_merchant_id":"20160815045834023630"}
		 * 
		 */
		Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put("app_id", "20161028140554435");
		paramsMap.put("method", "fshows.liquidation.submerchant.alipay.trade.precreate");
		paramsMap.put("version", "1.0");

		// content对象, 例如javaBean, map等
		JSONObject json = new JSONObject();

		String num = DateUtil.getyyyyMMddHHmmssStringFromDate(new Date());
		String merchant_id = "20161028163421024954";
		String subject = "test";
		json.put("total_amount", "0.01");
		json.put("subject", "test");
		json.put("notify_url", "http://xymtian.6655.la/flypayfx/payment/pinganFeedback");
		json.put("sub_merchant", JSONObject.parseObject("{\"merchant_id\":\"20161028163421024954\"}"));
		json.put("out_trade_no", num);
		System.out.println(json);
		// out_trade_no="2016083011235601299248666254";
		// content="{\"out_trade_no\":\"1475025587776617117\",\"sub_merchant\":{\"merchant_id\":\"20160927170157029364\"},\"subject\":\"iphone7 Plus 128G\",\"total_amount\":0.01}";
		String content = "{\"out_trade_no\":\"" + num + "\",\"sub_merchant\":{\"merchant_id\":\""
				+ merchant_id + "\"},\"subject\":\"" + subject + "\",\"total_amount\":\"" + 0.01 + "\"}";
		
		System.out.println(content);
		// 对象转json, 工具包括jackson, fastjson, gson等
		paramsMap.put("content", json.toJSONString());

		String prikey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMdeZrOgsBuOhAP6oYf4nfnLjKl1o+rfqWrg9qRzb8o2M9O/9cScNdL5PtK7KqdcstIrqIraxma4TbDESt2ShLAPSahfG9QTOatDLB0u1pdoOUAWW6Q595yJKXhZza842Yb7hO8Uw+yDB6W5OwTb/o9oZUX2YaJqpdgpmjsc8yjzAgMBAAECgYEAhVi0pIxjferyjZ7DD6jQMNVePbG7c0spi5zwGspDCSL7wzNvdJNuxK+Ev3oEa3BrAnvE/Sqa7PV0sTh5Qn3PVKqVMwe4vXMgx/YCB3I4KZ4vLSOVD+YUxU6ByqQaq3P5tTJjnqWJ8NrXtIabdOaNy9Yi5hBfT2GyFruvDO1ONBECQQD9RYaa8e1v5ccYRZbHRh+9iDj7e7ieCVJ3mdapQARnizXZHdLHNZwzX3udqjrEDRkd5L2QjA0iuMBZPVE8t81tAkEAyYQ4xXYn1sRYF1vzmntKsfC/mskxeYorEq0MQzSJVnTAGRC6TCCh1gN8XASvf0Okr6N9vOFqtitM7Rka6ISz3wJBANaXWf1ejjcJES/XhnFBURNdoCo0IbCFZYJArkipHRI+OVnEkxqGqdo9RJfJ7BDAqE9Tx+n5QAfzUcZK1dIESeECQCFVnvKDFhn/xraufkCKrpN6yKc5Ktb5FSD0wTeIxEMp8vDyhG69Yyf80aCDIejCbsajG9SX0UgrJ4F9Cqd19C8CQQCYxlkTirnSUiUjlRycyH/bAiKteVkDzI8ajTlpCsW/8gK8MKwFy5vTpx7T15RVkayN/b9Yt3VzTrObNEkF+lTt";// RSA密钥私钥,
																																																																																																																																																																																																																											// 去除头尾、换行和空格
		// 生成签名, 用于调用具体接口
		String sign = AlipaySignature.rsaSign(paramsMap, prikey, "utf-8");
		// 参数为生成签名时的参数, 增加签名 sign参数
		paramsMap.put("sign", sign);


		// 发起post请求, 返回值为接口响应值, json格式
		String result;
		/*try {
			result = PinganHttpUtil.getInstance().doPostForString(
					"https://openapi-liquidation-test.51fubei.com/gateway", paramsMap);
			System.out.println(result);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

	/**
	 * 调用APP服务端的client服务
	 * 
	 * @param requestUrl
	 *            请求的URL
	 * @param requestMethod
	 *            请求的类型POST/GET
	 * @param outputStr
	 *            请求的数据（JSON）
	 * @return
	 * @throws Exception
	 */
	private static String httpJsonRequest(String requestUrl, String requestMethod, String outputStr) throws Exception {
		String jsonObject = null;
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			conn.setRequestProperty("Content-Length", String.valueOf(outputStr.length()));
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(30000);
			conn.setRequestMethod(requestMethod);
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				// if (str.startsWith("{")) {
				buffer.append(str);
				// }
			}
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			String bs = buffer.toString();
			// XmlMapper.map2Xml(m);
			System.out.println(bs);
		} catch (Exception e) {
			throw e;
		}
		return jsonObject;
	}

}
