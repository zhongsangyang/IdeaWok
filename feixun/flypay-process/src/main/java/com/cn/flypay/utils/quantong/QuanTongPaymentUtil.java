package com.cn.flypay.utils.quantong;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.MD5Util;




public class QuanTongPaymentUtil {
	
	
	private static final Logger logger = LoggerFactory.getLogger(QuanTongPaymentUtil.class);
	
	public static String cooperatorId = "QT201708180011";
	
	public static String createSubMerchant ="http://wpay.transgem.cn/WechatPay/Join";
	
	public static String UploadFile = "http://wpay.transgem.cn/WechatPay/UploadFile";
	
	public static String GetJoinResult = "http://wpay.transgem.cn/WechatPay/GetJoinResult";
	
	public static String GetQrCodeForApp ="http://wpay.transgem.cn/WechatPay/GetQrCodeForApp";
	
	public static String GetVA="http://wpay.transgem.cn/WechatPay/GetVA";
	
	
	public static String UpdateImage(String url,String filePath,String suffix,String fileType,String merchantId) throws Exception{
		Map<String, String> params = new HashMap<String, String>();
		FileInputStream fileInputStream = new FileInputStream(filePath);
		byte[] src = null;
		ByteArrayOutputStream baos = null;
		baos = new ByteArrayOutputStream();
	    int len = 0;  
	    byte [] buffer = new byte[1024];
	    while( (len = fileInputStream.read(buffer)) != -1){
	    	baos.write(buffer, 0, len);  
	    }  
		src = baos.toByteArray();
		fileInputStream.close();
		params.put("cooperatorId", cooperatorId);
		params.put("merchantId", merchantId);
		params.put("mobile", "13816111195");
		//证件图片文件类型： 1011-身份证正面  1012-身份证反面   1013-手持身份证照片（个人商户） 1014-营业执照（企业商户）
		params.put("fileType", fileType);
		params.put("resType", "1");
		//文件后缀
		params.put("suffix", suffix);
		//字节的起始索引
		params.put("startIndex", "0");
		//字节的截止索引
		params.put("endIndex", "" + src.length);
		//文件总的字节大小
		params.put("totalLength", "" + src.length);
		//文件的内容
		params.put("content", byte2hex(src));
		//1个字节的校验值，十六进制形式
		params.put("checkValue", generateCheckValue(src));
		Set<String> keySet = params.keySet();
		String enString = "";
		for (String key : keySet) {
			enString += key +"="+ params.get(key)+"&";
		}
		String signStr = enString.substring(0, enString.length()-1);
		logger.info("全通发送报文:"+signStr);
		String rspStr = sendPost(url, signStr);
		logger.info("全通返回报文:"+rspStr);
		return rspStr;
	}
	
	
	
	public static void createSubMerchant(String merchantId) throws Exception{
		Map<String, String> params = new HashMap<String, String>();
		params.put("mId", "");
		params.put("merchantId", merchantId);
		params.put("merchantName", "个体户王美凤花语店");
		params.put("shortName", "王美凤花语店");
		params.put("merchantAddress", "北京市朝阳区潘家园");
		params.put("merchantType", "0");
		params.put("categoryForAlipay", "2015091000052157");
		params.put("categoryForWeChat", "210");
		params.put("contractName", "王美凤");
		params.put("idCard", "142222199110061529");
		params.put("accName", "芦强");
		params.put("bankName", "上海中远两湾城支行");
		params.put("bankId", "308290003484");
		params.put("bankNumber", "6214851213282739");
		params.put("mobileForBank", "13816111195");
		params.put("t0DrawFee", "2");
		params.put("t0TradeRate", "0.003");
		params.put("t1DrawFee", "1");
		params.put("t1TradeRate", "0.003");
		params.put("provinceCode", "110000");
		params.put("cityCode", "110100");
		params.put("districtCode", "110102");
		String param = MakeURL(params);
		logger.info("发送全通参数:"+param);
		String resp = sendPost(createSubMerchant, param, cooperatorId);
		logger.info("全通返回报文:"+resp);
	}
	
	
	
	public static void createSubMerchantResult(String merchantId) throws Exception{
		Map<String, String> params = new HashMap<String, String>();
		params.put("cooperatorId", cooperatorId);
		params.put("merchantId", merchantId);
		String param = MakeURL(params);
		logger.info("发送全通参数:"+param);
		JSONObject returnJson = new JSONObject();
		returnJson.put("cooperatorId", cooperatorId);
		returnJson.put("merchantId", merchantId);
		returnJson.put("sign", MD5Util.md5(cooperatorId+merchantId));
		returnJson.put("resType", "1");
		logger.info("发送全通报文:"+returnJson);
		String resp = doPost(GetJoinResult, returnJson);
		logger.info("全通返回报文:"+resp);
	}
	
	
	
	
	public static String GetQrCodeForApp(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", "5121213212132125iuyiuyiuyui");
		params.put("Fee", "3205");
		params.put("Time", DateUtil.convertCurrentDateTimeToString());
		params.put("clearType", "1");
		String param = MakeURL(params);
		logger.info("发送全通参数:"+params);
		JSONObject returnJson = new JSONObject();
		returnJson.put("mId", "QT20161211001");
		returnJson.put("data", AESUtil.encrypt(param, AESUtil.AES_KEY));
		returnJson.put("sign", MD5Util.md5(param));
		returnJson.put("resType", "1");
		logger.info("发送全通报文:"+returnJson);
		String resp = doPost(GetQrCodeForApp, returnJson);
		logger.info("全通返回报文:"+resp);
		return resp;
	}
	
	
	public static JSONObject PostQrCodeForApp(Map<String, String> params,String mId){
		String param = MakeURL(params);
		logger.info("发送全通参数:"+params);
		JSONObject returnJson = new JSONObject();
		returnJson.put("mId", mId);
		returnJson.put("data", AESUtil.encrypt(param, AESUtil.AES_KEY));
		returnJson.put("sign", MD5Util.md5(param));
		returnJson.put("resType", "1");
		logger.info("发送全通报文:"+returnJson);
		String resp = doPost(GetQrCodeForApp, returnJson);
		logger.info("全通返回报文:"+resp);
		return JSONObject.parseObject(resp);
	}
	
	
	
	public static String GetVA(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("cooperatorId", cooperatorId);
		params.put("date", "20170821");
		logger.info("发送全通参数:"+params);
		String param = MakeURL(params);
		JSONObject returnJson = new JSONObject();
		returnJson.put("cooperatorId", cooperatorId);
		returnJson.put("date", "20170821");
		returnJson.put("sign", MD5Util.md5(param));
		returnJson.put("resType", "1");
		logger.info("发送全通报文:"+returnJson);
		String resp = doPost(GetVA, returnJson);
		logger.info("全通返回报文:"+resp);
		return resp;
	}
	
	
	
	public static String sendPost(String url,String param,String cooperatorId) throws Exception{
		JSONObject returnJson = new JSONObject();
		returnJson.put("cooperatorId", cooperatorId);
		returnJson.put("data", AESUtil.encrypt(param, AESUtil.AES_KEY));
		returnJson.put("sign", MD5Util.md5(param));
		returnJson.put("resType", "1");
		logger.info("发送全通报文:"+returnJson);
		String rspStr = doPost(url, returnJson);
		return rspStr;
	}
	
	
	
	
	public static void main(String[] args) throws Exception {
		//图片上传
		//fileType  1011-身份证正面   1012-身份证反面   1013-手持身份证照片（个人商户） 1014-营业执照（企业商户）
//		String merchantId = getMerchentId();
//		UpdateImage(UploadFile, "D:/tupian/timg.jpg", "jpg", "1011", merchantId);
//		UpdateImage(UploadFile, "D:/tupian/timg.jpg", "jpg", "1012", merchantId);
		//商户入驻
//		createSubMerchant(merchantId);
		
		//全通返回报文:{"merchantId":"20170825102851589","mId":"QT201708250001","code":0,"msg":"商户待审核"}
		//全通返回报文:{"merchantId":"20170825103044820","mId":"QT201708250002","code":0,"msg":"商户待审核"}
		//全通返回报文:{"merchantId":"20170825103153083","mId":"QT201708250003","code":0,"msg":"商户待审核"}
		
//		logger.info("开通子商户号:"+merchantId);
		//子商户结果查询
		createSubMerchantResult("20170825102851589");
		//扫码接口
//		GetQrCodeForApp();
		//对账单接口
//		GetVA();
		
		
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("XINke", "abcdefg34234234234");
//		map.put("XINkeOne", "123456dfasdf");
//		map.put("XINkeTwo", "代付银行sss");
//		String p = MakeURL(map);
//		System.out.println(p);
//		Map<String, String> map2 = MakeMap(p);
//		System.out.println(map2);
	}
	
	
	
	public static String getMerchentId(){
		return DateUtil.convertCurrentDateTimeToString()+StringUtils.leftPad(String.valueOf(RandomUtils.nextInt(1000)), 3, "0");
	}
	
	
	
	public static String MakeURL(Map<String, String> params) {
		Set<String> keySet = params.keySet();
		String enString = "";
		for (String key : keySet) {
			enString += key +"="+ params.get(key)+"&";
		}
		String signStr = enString.substring(0, enString.length()-1);
		return signStr;
	}
	
	
	public static Map<String, String> MakeMap(String params){
		Map<String, String> map = new HashMap<String, String>();
		String[] strs = params.split("&");
		for (int i = 0; i < strs.length; i++) {
			int index = strs[i].indexOf("=");
			String left = strs[i].substring(0,index);
			String right = strs[i].substring(index+1);
			map.put(left, right);
		}
		return map;
	}
	
	
	public static String MakeURL(String[] keys, String[] params) {
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
	

	public static String generateCheckValue(byte[] src) {
		byte b = 0x00;
		for (int i = 0; i < src.length; i++) {
			b = (byte) (b ^ src[i]);
		}
		return byte2hex(new byte[] { b });
	}
	
	
	
	/**
	 * 二进制转字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String byte2hex(byte[] src) {
		final String HEX = "0123456789abcdef";
		StringBuilder sb = new StringBuilder(src.length * 2);
		for (byte b : src) {
			sb.append(HEX.charAt((b >> 4) & 0x0f));
			sb.append(HEX.charAt(b & 0x0f));
		}
		return sb.toString();
	}

	
	
	public static byte[] HexString2Bytes(String hexstr) {  
	    byte[] b = new byte[hexstr.length() / 2];  
	    int j = 0;  
	    for (int i = 0; i < b.length; i++) {  
	        char c0 = hexstr.charAt(j++);  
	        char c1 = hexstr.charAt(j++);  
	        b[i] = (byte) ((parse(c0) << 4) | parse(c1));  
	    }  
	    return b;  
	} 
	
	private static int parse(char c) {  
	    if (c >= 'a')  
	        return (c - 'a' + 10) & 0x0f;  
	    if (c >= 'A')  
	        return (c - 'A' + 10) & 0x0f;  
	    return (c - '0') & 0x0f;  
	}  
	
	
	
	public static String doPost(String urlStr,JSONObject param){
		HttpURLConnection httpUrlConnection = null;
		InputStream is = null;
		try {
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
			dos.writeBytes(param.toJSONString());
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
		} catch (Exception e) {
		    e.printStackTrace();
		}finally {
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
	
	
	
	public static String sendPost(String urlStr,String param){
		HttpURLConnection httpUrlConnection = null;
		InputStream is = null;
		try {
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
		} catch (Exception e) {
		    e.printStackTrace();
		}finally {
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
