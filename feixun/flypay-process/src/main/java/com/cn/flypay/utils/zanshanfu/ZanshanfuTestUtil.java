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
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.MD5Util;

public class ZanshanfuTestUtil {

	private static final Logger logger = LoggerFactory.getLogger(ZanshanfuTestUtil.class);

	private static final String visit_pay_url = "https://api.zhifujiekou.com";

	private static final String app_id = "89900100";
	private static final String key = "00000000000000000000000000000000";
	public static String success_pay_url = "http://klrsheng.51vip.biz/payment/successedpay?orderNum=%s";

	public static void main(String[] args) {
		ZanshanfuTestUtil controllerTest = new ZanshanfuTestUtil();
		try {
			JSONObject json = new JSONObject();
			json.put("action", "goAndPay");
			json.put("txnamt", "1000");
			json.put("merid", app_id);
			json.put("orderid", "ff" + DateUtil.convertCurrentDateTimeToString());
			json.put("backurl", "http://www.abcd.com/Notice.html");
			json.put("fronturl", "http://www.abcd.com/Result.html");
			json.put("accname", "孙月");
			json.put("accno", "370322198703213112");
			json.put("cardno", "4392260033229160");

			System.out.println("json===" + json.toJSONString());
			String bs64 = Base64Utils.encodeToString(json.toJSONString().getBytes());
			System.out.println("base64===" + bs64);
			String bs64_2 = bs64 + key;
			System.out.println("添加商户秘钥===" + bs64_2);
			String md5 = MD5Util.md5(bs64_2);
			System.out.println("MD5===" + md5);

			String ulrEncode = URLEncoder.encode(bs64, "utf-8");
			System.out.println("ulrEncode===" + ulrEncode);
			String url = "https://api.zhifujiekou.com/api/gateway?req=%s&sign=%s";
			String url2 = "req=%s&sign=%s";

			String response = zanshanfuBuild("", String.format(url2, ulrEncode, md5));

			System.out.println(response);
			/*
			 * String req = resposeJson.getString("resp");
			 * System.out.println(MD5Util.md5(req + key));
			 * System.out.println(req);
			 * System.out.println(URLDecoder.decode(req, "utf-8"));
			 * System.out.println(new String(Base64Utils.decodeFromString(req),
			 * "utf-8"));
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String zanshanfuBuild(String method, String content) {
		InputStream is = null;
		HttpURLConnection httpUrlConnection = null;
		try {
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
