package com.cn.flypay.utils.zanshanfu;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.ApiUtil;
import com.cn.flypay.utils.MD5Util;

public class ZanshanfuPayUtil {

	private static final Logger logger = LoggerFactory.getLogger(ZanshanfuPayUtil.class);

	private static final String visit_pay_url = "https://api.zhifujiekou.com";
	private static final String send_get_visit_pay_url = "https://api.zhifujiekou.com/apis/gateway";

	private static final String app_id = "89902741";
	private static final String key = "89e2ab0dba3145d80373acc90a5449a7";
	private static final String keyV5 = "e0e8c12a640af5bd40d99833ee9a7247";
	                                   
	public static String success_pay_url = "http://klrsheng.51vip.biz/payment/successedpay?orderNum=%s";

	public static String sendPayOrderToZanshanfu(JSONObject json,String accKey) throws Exception {
		try {
			logger.info("订单信息===" + json.toJSONString());
			String bs64 = Base64Utils.encodeToString(json.toJSONString().getBytes());
			String bs64_2 = bs64 + accKey;
			String md5 = MD5Util.md5(bs64_2);
			String ulrEncode = URLEncoder.encode(bs64, "utf-8");
			String url2 = "req=%s&sign=%s";
			System.out.println("https://api.zhifujiekou.com/apis/gateway?" + String.format(url2, ulrEncode, md5));
			// ApiUtil ap = new ApiUtil();
			return send_get_visit_pay_url + "?" + String.format(url2, ulrEncode, md5);
		} catch (Exception e) {
			logger.error("创建攒善付订单失败", e);
			throw e;
		}
	}

	public static JSONObject sendSearchOrderToZanshanfu(JSONObject json) throws Exception {
		try {
			logger.info("订单信息===" + json.toJSONString());
			String bs64 = Base64Utils.encodeToString(json.toJSONString().getBytes());
			String bs64_2 = bs64 + key;
			String md5 = MD5Util.md5(bs64_2);
			String ulrEncode = URLEncoder.encode(bs64, "utf-8");
			String url2 = "req=%s&sign=%s";
			String response = zanshanfuBuild("/api/query", String.format(url2, ulrEncode, md5));
			JSONObject response_json = JSONObject.parseObject(response);
			if (response_json.containsKey("resp") && response_json.containsKey("sign") && isValidateSign(response_json.getString("resp"), response_json.getString("sign"))) {
				return zansanfuDecodeResponse(response_json.getString("resp"));
			}
		} catch (Exception e) {
			logger.error("查询攒善付订单失败", e);
			throw e;
		}
		return null;
	}

	public static Boolean isValidateSign(String response, String sign) throws Exception {
		try {
			response = response + key;
			String md5 = MD5Util.md5(response);
			if (md5.equals(sign)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	public static Boolean isValidateSignV5(String response, String sign) throws Exception {
		try {
			response = response + keyV5;
			String md5 = MD5Util.md5(response);
			if (md5.equals(sign)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public static JSONObject zansanfuDecodeResponse(String response) throws Exception {
		try {
			response = new String(Base64Utils.decodeFromString(response), "utf-8");
			logger.info(response);
			return JSONObject.parseObject(response);
		} catch (Exception e) {
			throw e;
		}
	}

	public static String zanshanfuBuild(String method, String content) {
		InputStream is = null;
		HttpURLConnection httpUrlConnection = null;
		try {
			System.out.println(visit_pay_url + method + "?" + content);
			URL url = new URL(visit_pay_url + method);

			URLConnection urlConnection = url.openConnection();
			httpUrlConnection = (HttpURLConnection) urlConnection;

			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setRequestMethod("POST");

			httpUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			httpUrlConnection.setRequestProperty("Charset", "UTF-8");

			httpUrlConnection.connect();

			DataOutputStream dos = new DataOutputStream(httpUrlConnection.getOutputStream());
			dos.writeBytes(content);
			dos.flush();
			dos.close();

			int resultCode = httpUrlConnection.getResponseCode();
			if (HttpURLConnection.HTTP_OK == resultCode) {
				StringBuffer returnSb = new StringBuffer();
				String readLine = new String();
				BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream(), "UTF-8"));
				while ((readLine = responseReader.readLine()) != null) {
					returnSb.append(readLine).append("\n");
				}
				responseReader.close();
				logger.info(returnSb.toString());
				return returnSb.toString();
			} else if (HttpStatus.SC_MOVED_PERMANENTLY == resultCode || HttpStatus.SC_MOVED_TEMPORARILY == resultCode) {
				if (httpUrlConnection.getHeaderFields().get("Location") != null && httpUrlConnection.getHeaderFields().get("Location").size() > 0) {
					String yinlian_URL = httpUrlConnection.getHeaderFields().get("Location").get(0);
					ApiUtil api = new ApiUtil();
					String contents = api.doGet(yinlian_URL);
					return contents;
				}
			} else {
				logger.info("code:" + resultCode);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("zanshanfu msg encoding error", e);
		} catch (Exception e) {
			logger.error("zanshanfu visit error", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (httpUrlConnection != null) {
				httpUrlConnection.disconnect();
			}
		}
		return null;
	}
}
