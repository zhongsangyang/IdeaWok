package com.cn.flypay.utils.yibao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.pingan.PinganPaymentUtil;

import net.sf.json.JSONArray;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

/**
 * 易宝通道连接基础类
 * 
 * @author liangchao
 *
 */
public class YiBaoBaseUtil {

	private static final Logger logger = LoggerFactory.getLogger(YiBaoBaseUtil.class);

	// 易宝--对接方为：涩零
	// 生产基础地址，搭配方法名进行使用
	public static String baseRequestUrl = "https://skb.yeepay.com/skb-app/";
	// 生产代理商编号
	public static String customerNumber = "10017732140";
	// 生产代理商秘钥
	public static String hmacKey = "0ePk8Inq694DH6KzP12s3QzhV4uGn68oE2oP5UxDD3C84b574j1daJ1i7Q38";
	// 收款成功异步回调地址
	public static String callBackUrl = "https://bbpurse.com/flypayfx/payment/yibao_ylzx_Notify";
	// public static String callBackUrl =
	// "http://1g83849h98.iask.in:34530/flypayfx/payment/yibao_ylzx_Notify";

	//
	// //易宝--对接方为：夏商
	// //生产基础地址，搭配方法名进行使用
	// public static String baseRequestUrl = "https://skb.yeepay.com/skb-app/";
	// //生产代理商编号
	// public static String customerNumber = "10015935908";
	// //生产代理商秘钥
	// public static String hmacKey =
	// "752z9ooV95l19G3U87PS5q588jZ751242q65xX189283RzyID238z51oCQzV";
	// //收款成功异步回调地址
	// public static String callBackUrl =
	// "https://bbpurse.com/flypayfx/payment/yibao_ylzx_Notify";
	//// public static String callBackUrl =
	// "http://1g83849h98.iask.in:34530/flypayfx/payment/yibao_ylzx_Notify";

	// //测试基础地址，搭配方法名进行使用
	// private static String baseRequestUrl = "https://skb.yeepay.com/skb-app/";
	// //测试代理商编号
	// public static String customerNumber = "10001674445";
	// //测试代理商秘钥
	// public static String hmacKey =
	// "h8VV8k2337HgrHL9gBn8K2615KXa710vKd4IB27953nqq5PP0gnhVsQ0H900";
	// //收款成功异步回调地址
	// public static String callBackUrl =
	// "http://1g83849h98.iask.in:34530/flypayfx/payment/yibao_ylzx_Notify";

	/**
	 * 查询订单信息专用 请求易宝--公用模块,使用part[] 传递参数 适用于生成签名时的参数集和传递给易宝的参数集不一致时使用,
	 * 比如：子商户注册接口的照片文件不作为生成签名的参数
	 * 
	 * @param behavior
	 *            请求行为
	 * @param parts
	 *            请求参数集合
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	// public static JSONArray registerRequesterByPartForTradeReviceQuery(String
	// behavior,Part[] parts){
	// try {
	// HttpClient client = new HttpClient();
	// PostMethod postMethod = new PostMethod(baseRequestUrl +
	// behavior+".action");
	// postMethod.setRequestEntity(new
	// MultipartRequestEntity(parts,postMethod.getParams()));
	// int status = client.executeMethod(postMethod);
	// JSONArray res = null;
	// if(status == HttpStatus.SC_OK) {
	// String result = postMethod.getResponseBodyAsString();
	// System.out.println(result);
	// System.out.println("成功");
	// //对于查询订单的接口，要单独处理
	// JSONArray myJsonArray = JSONArray.fromObject(result);
	//
	// return res;
	//
	//
	// }else if (status == HttpStatus.SC_MOVED_PERMANENTLY
	// || status == HttpStatus.SC_MOVED_TEMPORARILY) {
	//
	// Header locationHeader = postMethod.getResponseHeader("location");
	// String location = null;
	// if (locationHeader != null) {
	// location = locationHeader.getValue();
	// System.out
	// .println("The page was redirected to:" + location);
	// } else {
	// System.err.println("Location field value is null.");
	// }
	// System.out.println("重定向");
	//
	// }else{
	// res.put("msg", "请求易宝返回结果失败");
	// System.out.println("请求失败");
	// }
	// } catch (Exception e) {
	// logger.error("易宝支付请求失败", e);
	// }
	// return null;
	// }

	/**
	 * 请求易宝--公用模块,使用part[] 传递参数 适用于生成签名时的参数集和传递给易宝的参数集不一致时使用,
	 * 比如：子商户注册接口的照片文件不作为生成签名的参数
	 * 
	 * @param behavior
	 *            请求行为
	 * @param parts
	 *            请求参数集合
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static JSONObject registerRequesterByPart(String behavior, Part[] parts) {
		try {
			HttpClient client = new HttpClient();
			PostMethod postMethod = new PostMethod(baseRequestUrl + behavior + ".action");
			postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
			int status = client.executeMethod(postMethod);
			JSONObject res = null;
			if (status == HttpStatus.SC_OK) {
				String result = postMethod.getResponseBodyAsString();
				System.out.println(result);
				res = JSONObject.parseObject(result);
				if (behavior.equals("tradeReviceQuery")) {
					int a = result.indexOf("[");
					int b = result.indexOf("]");
					if (res.containsKey("tradeReceives")) {
						// 对待查询用户订单的方法，要防止JSONObject将参数顺序打乱
						res.remove("tradeReceives");
						String tradeReceives = result.substring(a, b + 1);
						res.put("tradeReceives", tradeReceives);
					}
				}
				return res;

			} else if (status == HttpStatus.SC_MOVED_PERMANENTLY || status == HttpStatus.SC_MOVED_TEMPORARILY) {

				Header locationHeader = postMethod.getResponseHeader("location");
				String location = null;
				if (locationHeader != null) {
					location = locationHeader.getValue();
					System.out.println("The page was redirected to:" + location);
				} else {
					System.err.println("Location field value is null.");
				}
				System.out.println("重定向");

			} else {
				res.put("msg", "请求易宝返回结果失败");
				System.out.println("请求失败");
			}
		} catch (Exception e) {
			logger.error("易宝支付请求失败", e);
		}
		return null;
	}

	/**
	 * 请求易宝--公用模块，使用NameValuePair[]传递参数 适用于生成签名时的参数集和传递给易宝的参数集一致时使用
	 * 
	 * @param behavior
	 *            请求行为
	 * @param parts
	 *            请求参数集合
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static JSONObject registerRequesterByNameValuePair(String behavior, NameValuePair[] param) {

		try {
			// 加上签名
			param[param.length - 1].setValue(hmacSign(param));

			HttpClient client = new HttpClient();

			PostMethod postMethod = new PostMethod(baseRequestUrl + behavior + ".action");
			postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			postMethod.setRequestBody(param);

			int status = client.executeMethod(postMethod);
			JSONObject res = null;
			if (status == HttpStatus.SC_OK) {
				String result = postMethod.getResponseBodyAsString();
				System.out.println(result);
				System.out.println("成功");
				res = JSONObject.parseObject(result);
				return res;
			} else if (status == HttpStatus.SC_MOVED_PERMANENTLY || status == HttpStatus.SC_MOVED_TEMPORARILY) {

				Header locationHeader = postMethod.getResponseHeader("location");
				String location = null;
				if (locationHeader != null) {
					location = locationHeader.getValue();
					System.out.println("The page was redirected to:" + location);
				} else {
					System.err.println("Location field value is null.");
				}
				System.out.println("重定向");

			} else {
				res.put("msg", "请求易宝返回结果失败");
				System.out.println("请求失败");
			}
		} catch (Exception e) {
			logger.error("易宝支付请求失败", e);
		}
		return null;
	}

	/**
	 * 签名
	 *
	 * @return
	 */
	private static String hmacSign(NameValuePair[] param) {
		StringBuilder hmacStr = new StringBuilder();
		for (NameValuePair nameValuePair : param) {
			if (nameValuePair.getName().equals("hmac")) {
				continue;
			}
			hmacStr.append(nameValuePair.getValue() == null ? "" : nameValuePair.getValue());
		}
		System.out.println("----易宝---生成签名的参数为：" + hmacStr);
		String hmac = Digest.hmacSign(hmacStr.toString(), hmacKey);
		System.out.println("----易宝---生成签名为：" + hmacStr);
		return hmac;
	}

}
