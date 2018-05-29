/**
 * 
 */
package com.cn.flypay.utils.xinke;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 *
 */
public class ControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(ControllerTest.class);
	
	public void testOutcome() throws Exception {
		String[] keys = { "merId", "merDate", "merOrderId","acctNo","acctName","amount" };
		String[] params = { "201612160002556", "20161020", "2016102000000001","6226090000000048","张三","100" };
		build(keys, params, "outcome");
	}
	
	public void testOutcomeQuery() throws Exception {
		String[] keys = { "merId", "merDate", "merOrderId"};
		String[] params = { "000001", "20161020", "2016102000000001" };
		build(keys, params, "outcomeQuery");
	}

	private void build(String[] keys, String[] params, String service) throws UnsupportedEncodingException {
		String str = _MakeURL(keys, params);
		String sign = XinKeMD5Util.getMD5Str(str + "0123456789ABCDEF");

		StringBuilder sb = new StringBuilder(str.toString());
		sb.append("&");
		sb.append("sign");
		sb.append('=');
		sb.append(sign);

		String finalStr = Base64Util.encode(sb.toString().getBytes());
		try {
			finalStr = filter(finalStr);
			finalStr = URLEncoder.encode(finalStr, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		String param = "sText=" + finalStr;

		InputStream is = null;
		HttpURLConnection httpUrlConnection = null;
		try {
			URL url = new URL("http://27.115.99.214:9934/outcome/" + service);
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
			dos.writeBytes(param);
			dos.flush();
			dos.close();

			int resultCode = httpUrlConnection.getResponseCode();
			if (HttpURLConnection.HTTP_OK == resultCode) {
				StringBuffer sb1 = new StringBuffer();
				String readLine = new String();
				BufferedReader responseReader = new BufferedReader(
						new InputStreamReader(httpUrlConnection.getInputStream()));
				while ((readLine = responseReader.readLine()) != null) {
					sb1.append(readLine).append("\n");
				}
				responseReader.close();
				System.out.println(sb1.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	}

	public static String _MakeURL(String[] keys, String[] params) {
		if (keys.length != params.length) {
			return null;
		}

		StringBuilder url = new StringBuilder();
		for (int i = 0; i < params.length; i++) {
			url.append('&');
			url.append(keys[i]);
			url.append('=');
			url.append(params[i]);
		}

		return url.toString().replaceFirst("&", "");
	}
	
	private static String filter(String str) throws UnsupportedEncodingException {
		String output = null;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			int asc = str.charAt(i);
			if ((asc != 10) && (asc != 13))
				sb.append(str.subSequence(i, i + 1));
		}
		output = new String(sb.toString().getBytes(), "UTF-8");
		return output;
	}
	
	public static void main(String[] args) {
		ControllerTest controllerTest = new ControllerTest();
		try {
			controllerTest.testOutcome();
			//controllerTest.testOutcomeQuery();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
