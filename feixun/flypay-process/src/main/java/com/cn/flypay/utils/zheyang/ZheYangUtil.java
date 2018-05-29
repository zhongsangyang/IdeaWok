package com.cn.flypay.utils.zheyang;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;

/**
 * 哲扬通道
 * 
 * @author liangchao
 *
 */
public class ZheYangUtil {

	private static Logger LOG = LoggerFactory.getLogger(ZheYangUtil.class);

	private final static Integer apiVersion = 1;// api版本号
	// 测试环境url
	private static final String serverUrl = "https://www.znyoo.com/oss-transaction/gateway/";

	// 正式环境url https://www.znyoo.com/oss-transaction/gateway/{method}
	// private static final String serverUrl =
	// "https://www.znyoo.com/oss-transaction/gateway/";

	// 通道通用处理入口
	public static JSONObject execute(JSONObject obj) throws Exception {

		// 拼接请求参数 {"content":"{xxx}","sign":"xxx"}
		// 通用参数
		String key = obj.getString("key");

		obj.put("apiVersion", apiVersion);
		obj.put("txnDate", Calendar.getInstance().getTimeInMillis());

		LOG.info("ZheYang Request:{}", obj.toJSONString());
		JSONObject content = new JSONObject();
		content.put("content", JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue));
		content.put("key", key); // 生成签名，需要key
		String contentJsonStr = JSON.toJSONString(content, SerializerFeature.WriteMapNullValue);
		String sign = MD5Util.md5(contentJsonStr, "utf-8");
		content.remove("key"); // 请求服务器，不需要key
		content.put("sign", sign);
		// 请求服务器，返回结果
		// {"result":"{"code":"000000","data":{xxx},"message":"成功"}","sign":"xxx"}
		String result = sendHttpsPost(serverUrl + obj.get("method"), JSON.toJSONString(content));
		JSONObject resultObj = JSONObject.parseObject(result);
		String resultSignStr = resultObj.getString("sign");

		// 验证签名
		resultObj.remove("sign");
		String result2 = JSON.toJSONString(resultObj, SerializerFeature.WriteMapNullValue);
		System.out.println("去掉sign参数后的resultStr = " + result2);
		// 验证签名时，JSONObject 的key和result顺序会乱掉，导致本地md5验签不对，所以手动拼数据，保证顺序正确
		StringBuffer splitStr = new StringBuffer();
		splitStr.append("{\"key\":\"" + key + "\",");
		splitStr.append(result2.substring(1)); // 去掉括号{
		System.out.println("拼接验签参数的结果为" + splitStr.toString());

		// JSONObject resultSignObj = new JSONObject();
		// resultSignObj.put("result", resultObj.getString("result"));
		// resultSignObj.put("key", key);

		String resultSignLocal = MD5Util.md5(splitStr.toString(), "utf-8");
		// {"code":"000000","data":{xxx},"message":"成功"}
		JSONObject data = JSONObject.parseObject(resultObj.getString("result"));
		if (resultSignLocal.equals(resultSignStr)) {
			System.out.println("签名校验成功");
			if (data.get("code").equals("000000")) {
				System.out.println("成功");
			} else {
				System.out.println("哲扬返回结果显示处理失败，返回错误信息：" + data.getString("message"));
			}
			// data 数据格式为为 {"code":"000000","data":{xxx},"message":"成功"}
			// 注意，不要和json里面的data搞混
			return data;

		} else {
			System.out.println("签名校验失败");
			return null;
		}

	}

	/**
	 * 哲扬下单异步返回验签
	 */
	public static boolean judgeSign(JSONObject reqParams) throws Exception {
		StringBuffer splitStr = new StringBuffer();
		System.out.println("拼接验签参数的结果为" + splitStr.toString());

		splitStr.append("bizOrderNumber=");
		splitStr.append(reqParams.getString("bizOrderNumber") + "&");

		splitStr.append("completedTime=");
		splitStr.append(reqParams.getString("completedTime") + "&");

		splitStr.append("mid=");
		splitStr.append(reqParams.getString("mid") + "&");

		splitStr.append("srcAmt=");
		splitStr.append(reqParams.getString("srcAmt") + "&");

		splitStr.append("key=");
		splitStr.append(reqParams.getString("key"));

		System.out.println("-----哲扬下单异步返回验签,拼接验签参数的结果为" + splitStr.toString());

		String sign = reqParams.getString("sign");
		String resultSignLocal = MD5Util.md5(splitStr.toString(), "utf-8");
		if (sign.equals(resultSignLocal)) {
			return true;
		}
		return false;
	}

	/**
	 * 向哲扬服务器发送请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String sendHttpsPost(String url, String params) {
		DataOutputStream out = null;
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		URL u = null;
		HttpsURLConnection con = null;
		// 尝试发送请求
		try {
			System.out.println(params);
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
			u = new URL(url);
			// 打开和URL之间的连接
			con = (HttpsURLConnection) u.openConnection();
			// 设置通用的请求属性
			con.setSSLSocketFactory(sc.getSocketFactory());
			con.setHostnameVerifier(new TrustAnyHostnameVerifier());
			// con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json"); //
			con.setUseCaches(false);
			// 发送POST请求必须设置如下两行
			con.setDoOutput(true);
			con.setDoInput(true);

			con.connect();
			out = new DataOutputStream(con.getOutputStream());
			out.write(params.getBytes("utf-8"));
			// 刷新、关闭
			out.flush();
			out.close();
			// 读取返回内容
			// InputStream is = con.getInputStream();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line).append(System.lineSeparator());
			}
			System.out.println(result);
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (con != null) {
					con.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result.toString();
	}

	private static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}

	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
}
