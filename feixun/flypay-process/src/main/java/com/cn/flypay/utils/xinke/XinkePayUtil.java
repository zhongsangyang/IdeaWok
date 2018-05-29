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

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.DateUtil;

public class XinkePayUtil {

	private static final Logger logger = LoggerFactory.getLogger(XinkePayUtil.class);
	//欣客旧版本访问url
	//private static final String visit_pay_url = "http://www.xk-mall.com/refmobile/online/";
      
	private static final String visit_pay_url = "http://58.246.51.196:80/refmobile/online/";
	
	private static final String visit_create_merchant_url = "https://pay.xk-mall.com/Bpos/mer/";
	
	private static final String visit_query_merchant_url = "https://pay.xk-mall.com/merCenter/mer_query/";
	
	private static final String visit_waypay_merchant_url = "http://www.xk-mall.com/refmobile/online/";
	
	public static final String xinke_notify_url = ApplicatonStaticUtil.product_url + "/payment/xinkeNotify";
	
//	public static final String xinke_notify_url = "http://1k7387k300.iask.in:11688/flypayfx/payment/xinkeNotify";

	private static final String visit_settlement_url = "http://140.207.82.189:9811/outcome/";

	private static final String APP_ID = "00000021";
	private static final String private_key = "1BBD465ABC0BBE63";
	private static final String privateOutcome_key = "0123456789ABCDEF";
	
	

	private static final String weixin_account = "201701030002565";
	private static final String alipay_account = "201701030002566";

	public void testOutcome() throws Exception {
		// String[] keys = { "merId", "merDate", "merOrderId", "merTransId",
		// "acctNo", "acctName", "amount", "privateFlag", "remark" };
		// String[] params = { APP_ID + weixin_account, "20170103", "FF" +
		// DateUtil.convertCurrentDateTimeToString(),
		// DateUtil.convertCurrentDateTimeToString(), "6013820800101978854",
		// "孙月", "100", "P",
		// "WEIXIN" };
		String[] keys = { "merId", "merDate", "merOrderId", "merTransId", "remark" };
		String[] params = { APP_ID + weixin_account, "20170103", "FF" + DateUtil.convertCurrentDateTimeToString(), DateUtil.convertCurrentDateTimeToString(), "WEIXIN" };
		build(keys, params, "outcome");
	}

	public void testOutcomeQuery() throws Exception {
		String[] keys = { "merId", "merDate", "merOrderId" };
		String[] params = { APP_ID + weixin_account, "20170103", "FF20170103175248" };
		build(keys, params, "outcomeQuery");

	}

	public void testOrderQuery() throws Exception {
		String[] keys = { "ORD_ID", "TXNTYPE" };
		String[] params = { "P016121900000059", "03" };
		build(keys, params, "online_do_query.xml");

	}

	public void testWechatPay() throws Exception {
		String[] keys = { "INSTID", "USRID", "OUTORDERID", "TXAMT", "BODY", "TXNTYPE", "NOTIFYURL" };
		String[] params = { APP_ID, weixin_account, DateUtil.convertCurrentDateTimeToString(), "1.01", "飞付测试-支付", "03", "http://baidu.com" };
		build(keys, params, "online_order_dopay.xml");
	}

	public void testAlipayPay() throws Exception {
		String[] keys = { "INSTID", "USRID", "OUTORDERID", "TXAMT", "BODY", "TXNTYPE", "NOTIFYURL" };
		String[] params = { APP_ID, alipay_account, DateUtil.convertCurrentDateTimeToString(), "0.01", "tonny", "04", "http://baidu.com" };
		build(keys, params, "online_order_dopay.xml");
	}

	public void register() throws Exception {

		String[] keys = { "insNum", "payType", "merName", "regShortName", "merAddress", "merStat", "funcStat", "merType", "legalPerson", "legalPersonCertType", "legalPersonCertNm",
				"legalPersonCertExpire", "contactPerson", "contactMobile", "debitCardName", "debitCardLines", "debitCardNum", "WXT0", "ZFBT0", "WXT1", "ZFBT1", "factorageT0", "factorageT1",
				"bankName", "bankBranchName", "provName", "cityName", "isPrivate" };
		// String[] params = { APP_ID, "01", "微信收款", "微信收款", "上海浦东新区纳贤路800号",
		// "1", "YYYYYYYYYY", "0", "芦强", "0", "152822199012293814", "20250914",
		// "冯梁", "13052222696", "芦强", "03080000","6214852111454099", "0.26",
		// "0.26", "0.24", "0.24", "0.26", "0.24", "招商银行", "上海晨晖支行", "上海", "上海",
		// "N" };
		//
		String[] params = { APP_ID, "02", "支付宝收款", "支付宝收款", "上海浦东新区纳贤路800号", "1", "YYYYYYYYYY", "0", "芦强", "0", "152822199012293814", "20250914", "冯梁", "13052222696", "芦强", "03080000",
				"6214852111454099", "0.26", "0.26", "0.24", "0.24", "0", "0", "招商银行", "上海晨晖支行", "上海", "上海", "N" };

		build(keys, params, "enter");
	}

	public static String build(String[] keys, String[] params, String service) {

		String str = _MakeURL(keys, params);
		String sign = null;
		if(service.contains("outcome")){
			sign = XinKeMD5Util.getMD5Str(str + privateOutcome_key);
		}else{
			sign = XinKeMD5Util.getMD5Str(str + private_key);
		}
		logger.info("xingke sign: " + sign);
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
			if ("enter".equals(service)) {
				urlStr = visit_create_merchant_url + service;
			}else if("out_mer_query".equals(service)){
				urlStr = visit_query_merchant_url + service;
			}else if (service.contains("outcome")) {
				urlStr = visit_settlement_url + service;
			}else if("online_gate_waypay".equals(service)){
				urlStr = visit_waypay_merchant_url + service;
			}else {
				urlStr = visit_pay_url + service;
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
	
	
	
	public static String buildTwo(String[] keys, String[] params, String service) {

		String str = _MakeURL(keys, params);
		StringBuilder sb = new StringBuilder(str.toString());
		logger.info("xingke message: " + sb.toString());
		InputStream is = null;
		HttpURLConnection httpUrlConnection = null;
		try {
			String param =sb.toString();
			String urlStr = visit_query_merchant_url + service;

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
		
		String[] keys = { "instCode", "merId", "orderNo", "orderTime", "currencyCode", "orderAmount", "name",
				           "idNumber","acctNo","tellNo","productType","paymentType","frontUrl","backUrl"};
		String[] params = { "00000021", "201707050003771", "FF"+DateUtil.convertCurrentDateTimeToString(),DateUtil.convertCurrentDateTimeToString(),
				"156","8888","asdfsd","341126197709218366","6216261000000000018","13552535506","100000","2008",
				"https://www.2345.com","https://www.2345.com/"};
		String result = build(keys, params, "online_gate_waypay");
		result = new String(Base64.encodeBase64(result.getBytes()));
		result = URLEncoder.encode(result);
		System.out.println("http://101.200.34.95:26370/flypayfx/mobile/ShenFuResponse?result="+result);
		
	}
	
	

//	public static void main(String[] args) {
//		XinkePayUtil controllerTest = new XinkePayUtil();
//		try {
//			controllerTest.testWechatPay();
//			// controllerTest.testAlipayPay();
//			// controllerTest.testOutcome();
//			// controllerTest.testOutcomeQuery();
//			// controllerTest.register();
//			// controllerTest.testOrderQuery();
//			// String[] keys = { "ORD_ID", "TXNTYPE" };
//			// String[] params = { "11112016121614403270", "04" };
//			// String responseStr = XinkePayUtil.build(keys, params,
//			// "online_pay_query.xml");
//
//			String str = "INSTID=00000021&USRID=201701030002566&OUTORDERID=ALQR201701061722163640000000004&TXAMT=1.0&BODY=您正在向商户-孙月支付1.0元&TXNTYPE=04&NOTIFYURL=http://xymtian.6655.la/flypayfx/payment/xinkeNotify";
//			String sign = XinKeMD5Util.getMD5Str(str + private_key);
//			System.out.println(sign);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}