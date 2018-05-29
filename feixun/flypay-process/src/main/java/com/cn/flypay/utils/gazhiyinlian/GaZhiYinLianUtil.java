package com.cn.flypay.utils.gazhiyinlian;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.cloopen.rest.sdk.utils.encoder.BASE64Decoder;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.model.util.StringUtil;

/**
 * 嘎吱（银联）通道基础类
 * 
 * @author liangchao
 *
 */
public class GaZhiYinLianUtil {
	private static final Logger logger = LoggerFactory.getLogger(GaZhiYinLianUtil.class);

	// 嘎吱银联通道 --测试环境参数-----开始
	// public static String prikeyString =
	// "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANj9FdzBepnWgqNKM6ZLnXhg0nDjren5dA44miXtV+pDW+RuMx1xFo0z4PzR5DrHHLZuar6dUZd1NomyfM3mo27WHL4n8T1X6MX6rKBT4SltI/uvfG4aWDyi2dnQZk+vQK7PX4/Wp0pOilK4sQ/aaQ5wTwcm1BtTGTUJvoK2zxpXAgMBAAECgYEAqAlWJN6wUvILuOh9lwp3qAFS9pla1RaZCWpL9ZMXwhLn1r6AVwx8qlzCnwbr95i6Y4yenX628NOUqI3QmKNDa+SvbyYjgXNX2WnwrCz3NFOKEO8Z0iC4C2/PE4VqxgqHeqKKlYpbutuGLOkbgf1/2R6dEyNfevy4VoKi6+H5kgECQQDy2uzXy5s5jrWj9NT1FvyPGqM5Lw8dTbhMImi0y3azmuhIlnGmwrKAPu9/7gsmuioNRU88Ve5b6bPyQBHencqVAkEA5Lu+zaG4md22QvmKApsyn71MOq/TjkLJ3itRsMIspgfjXmf81JubuU5AHj9pTAwTCvTOgu8C2ynrU5KLSptCOwJAGnctNF8l7WHV/aYixwrYPygh/YtZVFIx8WoWiw1ZRD1vbummPQwes8coBoMEiBXlm0igV+ztaurPB2lgCSic+QJAZV8Dp/S1cJO/NB2N2avYmsz1XMkR9GmLN8hxEupriUXTscJaXgt8z3prljw6OXbcda2bXJH67RC997Lpwq3egwJACDXoMBsBADqGIWBhH+hyYR19cDA8gWACMmVOcZApi9wTwwTqW6aYpf5qFuCdGHdBq5eu7XfNE0sAv09VEJ6ZZg==";
	// public static String pubkeyString =
	// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCiKIbbzNJeGeNPH3WHhYUfbOsIVOGnviNECWdp75uRmUIA+sVtD7UR4yfWnIcHBLh3hW+7OPDqYqgeGM7359W5X0uGrt84ebRsvaZXJWOZQBT2QRSBYW2heqRxM6vjoFXaxY2ByTM21Ry1x8BGDR4QTG/bLRn9SvvGfmbh+wu0FQIDAQAB";
	// 嘎吱银联通道 --测试环境参数-----结束

	// 嘎吱银联通道 --生产环境参数-----开始
	// 飞付私钥 用于签名和解密
	public static String prikeyString = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALfaZF9+7J+67ZMuUFzz6bvnl/DvAwhUvGtBddL2n3nFXgQGkCr+95Urb3sAPthEfbL95tclVrIEeJwlzlzWoPet4WmofgD90ihpIWH2n4FPYPKNphJVjtuWWQipOEQ4oaY8PmNXkrHVCAR4zeyv+zlWlub+P8C1y3VaZmSHPiIvAgMBAAECgYA/cU5Hoc3XhLKsokO6/7Q9UjqdOm0Kd+sY+9rDtLTtWU0LCJTBMYCP6hArGreU+3WAyZrZzRmwjlhsUaN3Z9zdZRMc5gN5H28wdrGVjMWnmMTBRt/j3Y0Flh914OIRG+yzq3/hzB648ksAttj25a2+yAC6x2LI3Flw+0+0u4IF8QJBAOaKAZ7RZK/dkdT1dsbNMw9O+kQib7OWrnD4Cq3sMVvIagmgl4BqTreBbbMg1meq1hMjgLBbiPZF85IwKvnT3gcCQQDMKHC65QzWPmUuE5+WnYoSPkC9JTI1poBnAuDXG6Wru8vvj+/zc6FC8zEzQa/ALGKcGkTgDymqJWWnpd9o0hCZAkEAvSLF940kxN/Lz+JeQvWAiXOrAREbgUQC822cHVpkmD9RN3rIMLOGxyhpXod3P2+519PNtWIzeJClJu9bosD+KwJBAJA8HfCcdCZErL0R1hTVtj9PRfObfM6UlHCyguDWQoSCyAC3A0JPNeYT5HWe+ajecshwcqqNa9UuWJRJxv3KMzkCQAmxWpQAtJNXjax99l8OBl0yRueYkei6nLsn6xGa6DZFWK6u3XqoDxvOJOuqFo5JdMJGATFAcuMmP3hP7Aq+klE=";
	// 飞付公钥 无用
	// public static String pubkeyString =
	// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC32mRffuyfuu2TLlBc8+m755fw7wMIVLxrQXXS9p95xV4EBpAq/veVK297AD7YRH2y/ebXJVayBHicJc5c1qD3reFpqH4A/dIoaSFh9p+BT2DyjaYSVY7bllkIqThEOKGmPD5jV5Kx1QgEeM3sr/s5Vpbm/j/Atct1WmZkhz4iLwIDAQAB";
	// 嘎吱公钥 用于加密和验签
	public static String pubkeyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDB3Gv2spSfWjdY6UHf6dK/UZ+2r/pQxiIInoG/xRZNchVGl2duzd38Y3DWoQm2XD3PuGSiRaovpPgzbQh/XrZzrCDoUgMKoZz0CqFsoahqstVsrVRfpf8hOmCqkuFKzLLCoXOmvidmG/PhdjoHasKiQbnJF/2BKVHh8rxV6zLDNQIDAQAB";
	// 嘎吱银联通道 --生产环境参数-----结束

	// 请求地址
	// private static String requestUrl =
	// "http://27.115.103.86:9832/web/inform/";// 测试
	private static String requestUrl = "http://43.254.47.11:9832/web/inform/";// 生产
	// 费率编号
	// public static String rateCode = "1001002";// 测试
	public static String rateCode = "1001001";// 生产
	// 机构编号
	// public static String orgCode = "12345678";// 测试
	public static String orgCode = "52850001";// 生产
	// 银联侧绑卡开通后台通知url
	// public static String bindCardNotifyUrl =
	// "http://liguangchun211.51vip.biz:30483/flypayfx/payment/gaZhiYinLianOpenCardNotifyUrl";//测试
	public static String bindCardNotifyUrl = "https://bbpurse.com/flypayfx/payment/gaZhiYinLianOpenCardNotifyUrl";// 生产

	// public static String bindCardResponseUrl =
	// "http://liguangchun211.51vip.biz:30483/flypayfx/mobile/GaZhiResponse?result=";//
	// 测试
	public static String bindCardResponseUrl = "https://bbpurse.com/flypayfx/mobile/GaZhiResponse?result=";// 生产

	// 交易异步通知地址
	// public static String notifyUrl =
	// "http://liguangchun211.51vip.biz:30483/payment/gazhi_ylzx_notify";
	public static String notifyUrl = "https://bbpurse.com/flypayfx/payment/gazhi_ylzx_notify";// 生产
	// 嘎吱银联通道 --测试环境参数-----结束

	// 嘎吱银联通道支持的银行卡信息(借记卡、信用卡都适用)
	public static final Map<String, String> map = new HashMap<String, String>();

	// 未避免银行的字母代码对应不上，特别适用名称进行对应
	public static final Map<String, String> map2 = new HashMap<String, String>();

	// 嘎吱支持的银行卡的总行联行号 对于未知联行号的银行，设置值为weizhi ,被调用时强制替换成 102100099996
	public static final Map<String, String> map3 = new HashMap<String, String>();

	static {
		map.put("ICBC", "102"); // 中国工商银行
		map.put("ABC", "103"); // 中国农业银行
		map.put("BOC", "104"); // 中国银行
		map.put("CCB", "105"); // 中国建设银行
		map.put("CDB", "201"); // 国家开发银行
		map.put("EXIMBANK", "202"); // 中国进出口银行
		map.put("ADBC", "203"); // 中国农业发展银行
		map.put("BCOM", "301"); // 交通银行
		map.put("CITIC", "302"); // 中信银行
		map.put("CEB", "303"); // 中国光大银行
		map.put("HXB", "304"); // 华夏银行
		map.put("CMBC", "305"); // 中国民生银行
		map.put("CGB", "306"); // 广东发展银行
		map.put("PAB", "307"); // 平安银行
		map.put("CMB", "308"); // 招商银行
		map.put("CIB", "309"); // 兴业银行
		map.put("SPDB", "310"); // 上海浦东发展银行
		map.put("BIN", "313"); // 宁波国际银行
		map.put("EGBANK", "315"); // 恒丰银行
		map.put("CZB", "316"); // 浙商银行
		map.put("PSBC", "403"); // 中国邮政储蓄银行
		map.put("HSBC", "501"); // 汇丰银行
		map.put("HKBEA", "502"); // 东亚银行
		map.put("NCBCHINA", "503"); // 南洋商业银行
		map.put("HANGSENG", "504"); // 恒生银行
		map.put("SDB", "783"); // 深圳发展银行
		map.put("UNIONPAY", "905"); // 中国银联
		map.put("BOB", "317"); // 北京银行
		map.put("HCCB", "318"); // 杭州银行
		map.put("NJCB", "319"); // 南京银行
		map.put("BRCB", "314"); // 北京农村商业银行

		map2.put("中国工商银行", "ICBC");
		map2.put("中国农业银行", "ABC");
		map2.put("中国银行", "BOC");
		map2.put("中国建设银行", "CCB");
		map2.put("国家开发银行", "CDB");
		map2.put("中国进出口银行", "EXIMBANK");
		map2.put("中国农业发展银行", "ADBC");
		map2.put("交通银行", "BCOM");
		map2.put("中信银行", "CITIC");
		map2.put("中国光大银行", "CEB");
		map2.put("华夏银行", "HXB");
		map2.put("中国民生银行", "CMBC");
		map2.put("广东发展银行", "CGB");
		map2.put("平安银行", "PAB");
		map2.put("招商银行", "CMB");
		map2.put("兴业银行", "CIB");
		map2.put("上海浦东发展银行", "SPDB");
		map2.put("宁波国际银行", "BIN");
		map2.put("恒丰银行", "EGBANK");
		map2.put("浙商银行", "CZB");
		map2.put("中国邮政储蓄银行", "PSBC");
		map2.put("汇丰银行", "HSBC");
		map2.put("东亚银行", "HKBEA");
		map2.put("南洋商业银行", "NCBCHINA");
		map2.put("恒生银行", "HANGSENG");
		map2.put("深圳发展银行", "SDB");
		map2.put("中国银联", "UNIONPAY");
		map2.put("北京银行", "BOB");
		map2.put("杭州银行", "HCCB");
		map2.put("南京银行", "NJCB");
		map2.put("北京农村商业银行", "BRCB");

		map3.put("ICBC", "102100099996"); // 中国工商银行
		map3.put("ABC", "103100000018"); // 中国农业银行
		map3.put("BOC", "104100000004"); // 中国银行
		map3.put("CCB", "105100000017"); // 中国建设银行
		map3.put("CDB", "201100000017"); // 国家开发银行
		map3.put("EXIMBANK", "202290000012"); // 中国进出口银行
		map3.put("ADBC", "203100000019"); // 中国农业发展银行
		map3.put("BCOM", "301290011110"); // 交通银行
		map3.put("CITIC", "302100011106"); // 中信银行
		map3.put("CEB", "303100000006"); // 中国光大银行
		map3.put("HXB", "304100040000"); // 华夏银行
		map3.put("CMBC", "305100000013"); // 中国民生银行
		map3.put("CGB", "306581000003"); // 广东发展银行
		map3.put("PAB", "307584099984"); // 平安银行
		map3.put("CMB", "308584000013"); // 招商银行
		map3.put("CIB", "309391000011"); // 兴业银行
		map3.put("SPDB", "310290000013"); // 上海浦东发展银行
		map3.put("BIN", "weizhi"); // 宁波国际银行
		map3.put("EGBANK", "315456000105"); // 恒丰银行
		map3.put("CZB", "weizhi"); // 浙商银行
		map3.put("PSBC", "403100000004"); // 中国邮政储蓄银行
		map3.put("HSBC", "989584000407"); // 汇丰银行
		map3.put("HKBEA", "502100011829"); // 东亚银行
		map3.put("NCBCHINA", "503100000015"); // 南洋商业银行
		map3.put("HANGSENG", "989584002409"); // 恒生银行
		map3.put("SDB", "weizhi"); // 深圳发展银行
		map3.put("UNIONPAY", "weizhi"); // 中国银联
		map3.put("BOB", "weizhi"); // 北京银行
		map3.put("HCCB", "weizhi"); // 杭州银行
		map3.put("NJCB", "weizhi"); // 南京银行
		map3.put("BRCB", "weizhi"); // 北京农村商业银行
	}

	public static RSAPublicKey pubKey = null;
	public static RSAPrivateKey priKey = null;

	public static JSONObject send(String encryptStr) {
		JSONObject res = new JSONObject();
		if (pubKey == null) {
			// 加载公钥
			try {
				InputStream in = new ByteArrayInputStream(pubkeyString.getBytes());
				pubKey = RSAEncrypt.loadPublicKey(in);
				logger.info("---嘎吱银联通道---加载公钥成功");
			} catch (Exception e) {
				logger.error("---嘎吱银联通道 --加载公钥失败:" + e.getMessage());
				res.put("code", GlobalConstant.RESP_CODE_999);
				res.put("message", "加载公钥失败");
				return res;
			}
		}

		if (priKey == null) {
			// 加载私钥
			try {
				InputStream in = new ByteArrayInputStream(prikeyString.getBytes());
				priKey = RSAEncrypt.loadPrivateKey(in);
				logger.info("---嘎吱银联通道---加载私钥成功");
			} catch (Exception e) {
				logger.error("---嘎吱银联通道 --加载私钥失败:" + e.getMessage());
				res.put("code", GlobalConstant.RESP_CODE_999);
				res.put("message", "加载私钥失败");
				return res;
			}
		}

		// 加密
		String sign = null;
		String cipher = null;
		try {
			sign = RSAEncrypt.sign(encryptStr, priKey, "utf-8", "SHA1WithRSA");
			cipher = RSAEncrypt.encrypt(pubKey, encryptStr.getBytes("utf-8"));
		} catch (Exception e) {
			logger.error("---嘎吱银联通道 --加密过程出错:" + e.getMessage());
			res.put("code", GlobalConstant.RESP_CODE_999);
			res.put("message", "加密过程出错");
			return res;
		}

		// 请求
		RequestInfo req = new RequestInfo(orgCode, sign, cipher);
		JSONObject result = HttpRequestUtils.httpPost(requestUrl, req);
		if (result == null || StringUtil.isBlank(result.getString("body"))) {
			res.put("code", GlobalConstant.RESP_CODE_999);
			res.put("message", "请求通道服务器异常");
			return res;
		}
		logger.info("---嘎吱银联通道--通道返回信息为:" + result.toJSONString());
		// 解密
		try {
			String b = (String) result.get("body");
			String plainText = new String(RSAEncrypt.decrypt(priKey, new BASE64Decoder().decodeBuffer(b)), "utf-8");
			System.out.println(plainText);
			logger.info("---嘎吱银联通道--解密后的信息为:" + plainText);
			JSONObject resultJson = JSONObject.parseObject(plainText);
			if (resultJson.getString("respCode").equals("000000")) {
				res.put("code", GlobalConstant.RESP_CODE_SUCCESS);
				res.put("message", resultJson.getString("result"));
				return res;
			} else {
				res.put("code", GlobalConstant.RESP_CODE_999);
				res.put("message", resultJson.getString("respMsg"));
				return res;
			}
		} catch (Exception e) {
			logger.error("---嘎吱银联通道 --解密过程出错:" + e.getMessage());
			res.put("code", GlobalConstant.RESP_CODE_999);
			res.put("message", "解密过程出错");
			return res;
		}
	}

	public static JSONObject decrypt(String body) {
		JSONObject res = new JSONObject();
		// 解密
		try {
			if (priKey == null) {
				// 加载私钥
				try {
					InputStream in = new ByteArrayInputStream(prikeyString.getBytes());
					priKey = RSAEncrypt.loadPrivateKey(in);
					logger.info("---嘎吱银联通道---加载私钥成功");
				} catch (Exception e) {
					logger.error("---嘎吱银联通道 --加载私钥失败:" + e.getMessage());
					res.put("code", GlobalConstant.RESP_CODE_999);
					res.put("message", "加载私钥失败");
					return res;
				}
			}

			String plainText = new String(RSAEncrypt.decrypt(priKey, new BASE64Decoder().decodeBuffer(body)), "utf-8");
			logger.info("---嘎吱银联通道--解密后的信息为:" + plainText);
			JSONObject resultJson = JSONObject.parseObject(plainText);
			return resultJson;
		} catch (Exception e) {
			logger.error("---嘎吱银联通道 --解密过程出错:" + e.getMessage());
			return null;
		}
	}

}
