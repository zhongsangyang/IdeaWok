package com.cn.flypay;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.cloopen.rest.sdk.utils.encoder.BASE64Encoder;
import com.cn.flypay.model.payment.enums.TxnNoEnum;
import com.cn.flypay.model.payment.response.FundOutResponse;
import com.cn.flypay.utils.ApiUtil;
import com.cn.flypay.utils.JsonUtil;
import com.cn.flypay.utils.StringUtil;

/**
 * Created by zhoujifeng1 on 16/8/3.
 */
public class ZanshanfuTest {
	private static Log logger = LogFactory.getLog(ZanshanfuTest.class);

	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("VerInter", "1.0");
		map.put("TxnNo", TxnNoEnum.PAY201.getCode());
		map.put("ChlID", "100002");
		map.put("ChlNanme", "飞付");
		map.put("OrderID", "333110348160001");
		map.put("TxnDate", "20160810");
		map.put("TxnTime", "200101");
		map.put("CurrencyCode", "156");
		map.put("AccType", "01");
		map.put("AccNo", "6222600910079421979");
		map.put("AccName", "cc");
		map.put("IssInsCode", "301100000701");
		map.put("TxnAmt", "1");
		map.put("Remark", "daifu");

		String tt="";
		try {
			tt = generateSignature(map, "8934e7d15453e97507ef794cf7b0519d");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JSONObject m2 = new JSONObject();
		JSONObject m21 = new JSONObject();
		m21.put("VerInter", "1.0");
		m2.put("VerInfo", m21);
		m2.put("TxnNo", TxnNoEnum.PAY201.getCode());

		JSONObject m22 = new JSONObject();
		m22.put("ChlID", "100002");
		m22.put("ChlNanme", "飞付");
		m2.put("MerInfo", m22);

		JSONObject m24 = new JSONObject();
		m24.put("OrderID", "333110348160001");
		m24.put("TxnDate", "20160810");
		m24.put("TxnTime", "200101");
		m2.put("TxnInfo", m24);

		JSONObject m25 = new JSONObject();
		m25.put("CurrencyCode", "156");
		m25.put("AccType", "01");
		m25.put("AccNo", "6222600910079421979");
		m25.put("AccName", "cc");
		m25.put("IssInsCode", "301100000701");
		m25.put("TxnAmt", 1);
		m25.put("Remark", "daifu");
		
		m2.put("PayInfo", m25);
		
		m2.put("Signature", tt);
		FundOutResponse response = new FundOutResponse();
		String url = "http://124.207.150.197:8665/IFP/ChanelReController/chanelRecievePA.do";
		ApiUtil apiUtil = new ApiUtil();
		String res = "";
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
			HttpPost post = new HttpPost(url);
			HttpResponse resq2 = null;
			String rsp = "";
			try {
				String str = JsonUtil.fromObject(m2);
				System.out.println(str);
				StringEntity seReq = new StringEntity(str, "utf-8");
				post.setEntity(seReq);
				resq2 = httpClient.execute(post);
				rsp = EntityUtils.toString(resq2.getEntity());
				System.out.println(rsp);
				int statusCode = resq2.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					post.abort();
					throw new Exception("HttpClient,error status code :" + statusCode);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				httpClient.getConnectionManager().shutdown();
			}

			System.out.println(res);
		} catch (Throwable t) {
			System.out.println("error");

		}
	}

	public static String generateSignature(Map<String, String> map, String secretKey) throws NoSuchAlgorithmException, UnsupportedEncodingException {

		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (StringUtil.isNotEmpty(entry.getValue())) {
				if (StringUtil.isNotBlank(entry.getValue())) {
					list.add((String) entry.getKey() + "=" + entry.getValue() + "&");
				}
			}
		}
		int size = list.size();
		String[] arrayToSort = (String[]) list.toArray(new String[size]);
		Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(arrayToSort[i]);
		}
		String result = sb.toString();
		System.out.println(result);
		result = result + "key=" + secretKey;
		
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(result.getBytes("UTF-8"));
		System.out.println("MD5加密前字符串   "+result);
		byte[] b = md.digest();
//		String asc=bcd2str(b,0,16);
//		logger.info("MD5加密后字符串   "+asc);
		String signStr = (new BASE64Encoder()).encode(b).toUpperCase();
		System.out.println("base64编码后的字符串   "+signStr);

		
//		String signature = MD5Util.md5(result);

		return signStr;

	}
}
