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

import com.cn.flypay.utils.DateUtil;

public class XinkePayTestUtil {

	private static final Logger logger = LoggerFactory.getLogger(XinkePayTestUtil.class);

	private static final String visit_pay_url = "http://27.115.99.214:8091/refmobile/online/";
//	 private static final String visit_pay_url = "http://27.115.99.214:8091/outcome/";
	private static final String visit_create_merchant_url = "http://27.115.99.214:9931/Bpos/mer/";

	public static final String xinke_notify_url = "http://ffy.ngrok.sapronlee.com/flypayfx/payment/xinkeNotify";
	
	private static final String visit_settlement_url = "http://27.115.99.214:9934/outcome/";
		
	public void testOutcome() throws Exception {
		String[] keys = { "merId", "merDate", "merOrderId", "merTransId","acctNo", "acctName", "amount" ,"privateFlag","remark"};
		String[] params = { "00000008201612160002556", "20161222", "FF"+DateUtil.convertCurrentDateTimeToString(), DateUtil.convertCurrentDateTimeToString(),"6013820800101978854", "孙月", "5","P","WEIXIN" };
		build(keys, params, "outcome");
	}

	public void testOutcomeQuery() throws Exception {
		String[] keys = { "merId", "merDate", "merOrderId" };
		String[] params = { "201612160002556", "20161222", "201612160002556" };
		build(keys, params, "outcomeQuery");

	}

	public void testOrderQuery() throws Exception {
		String[] keys = { "ORD_ID", "TXNTYPE" };
		String[] params = { "P016121900000059", "03" };
		build(keys, params, "online_do_query.xml");

	}

	public void testWechatPay() throws Exception {
		String[] keys = { "INSTID", "USRID", "OUTORDERID", "TXAMT", "BODY", "TXNTYPE", "NOTIFYURL" };
		String[] params = { "00000008", "201612160002556", DateUtil.convertCurrentDateTimeToString(), "0.01", "tonny", "03", "http://baidu.com" };
		build(keys, params, "online_order_dopay.xml");
	}

	public void testAlipayPay() throws Exception {
		String[] keys = { "INSTID", "USRID", "OUTORDERID", "TXAMT", "BODY", "TXNTYPE", "NOTIFYURL" };
		String[] params = { "00000008", "201612160002556", DateUtil.convertCurrentDateTimeToString(), "0.01", "tonny", "04", "http://baidu.com" };
		build(keys, params, "online_order_dopay.xml");
	}

	public void register() throws Exception {

		String[] keys = { "insNum", "payType", "merName", "regShortName", "merAddress", "merStat", "funcStat", "merType", "legalPerson", "legalPersonCertType", "legalPersonCertNm",
				"legalPersonCertExpire", "contactPerson", "contactMobile", "debitCardName", "debitCardLines", "debitCardNum", "WXT0", "ZFBT0", "WXT1", "ZFBT1", "factorageT0", "factorageT1",
				"bankName", "bankBranchName", "provName", "cityName", "isPrivate" };
		String[] params = { "00000008", "01", "小孙水果店", "小孙水果店", "上海浦东新区纳贤路800号", "1", "YYYYYYYYYY", "0", "孙月", "0", "370322198703213112", "20360102", "孙月", "13817117644", "孙月", "13817117644",
				"6013820800101978854", "0.26", "0.26", "0.24", "0.24", "0.26", "0.26", "中国银行", "中国银行上海市南京市东路支行", "上海", "上海", "N" };

		build(keys, params, "enter");
	}

	public static String build(String[] keys, String[] params, String service) {
		String str = _MakeURL(keys, params);
		String sign = XinKeMD5Util.getMD5Str(str + "0123456789ABCDEF");
		StringBuilder sb = new StringBuilder(str.toString());
		sb.append("&sign=");
		sb.append(sign);
		logger.info("xingke message: " + sb.toString());
		InputStream is = null;
		HttpURLConnection httpUrlConnection = null;
		try {
			String finalStr = Base64Util.encode(CryptUtil.GetEncodeStr(sb.toString()).getBytes());
			finalStr = filter(finalStr);
			finalStr = URLEncoder.encode(finalStr, "UTF-8");
			String param = "sText=" + finalStr;
			String urlStr = null;
			if("enter".equals(service)){
				urlStr=visit_create_merchant_url+ service;
			}else if(service.contains("outcome")){
				urlStr=visit_settlement_url+ service;
			}else{
				urlStr=visit_pay_url+ service;
			}
			
			URL url = new URL(urlStr);

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
				StringBuffer returnSb = new StringBuffer();
				String readLine = new String();
				BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream(), "UTF-8"));
				while ((readLine = responseReader.readLine()) != null) {
					returnSb.append(readLine).append("\n");
				}
				responseReader.close();
				logger.info(returnSb.toString());
				return returnSb.toString();
			} else {
				logger.info("code:" + resultCode);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("xinke msg encoding error", e);
		} catch (Exception e) {
			logger.error("xinke visit error", e);
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
		XinkePayTestUtil controllerTest = new XinkePayTestUtil();
		try {
			// controllerTest.testWechatPay();
			// controllerTest.testAlipayPay();
//			controllerTest.testOutcome();
			 controllerTest.testOutcomeQuery();
			// controllerTest.register();
			// controllerTest.testOrderQuery();
			// String[] keys = { "ORD_ID", "TXNTYPE" };
			// String[] params = { "11112016121614403270", "04" };
			// String responseStr = XinkePayUtil.build(keys, params,
			// "online_pay_query.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
