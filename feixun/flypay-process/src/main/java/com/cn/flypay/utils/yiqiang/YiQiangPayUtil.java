package com.cn.flypay.utils.yiqiang;

import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.AESCodeUtil;
import com.cn.flypay.utils.DateUtil;
import sun.misc.BASE64Decoder;

public class YiQiangPayUtil {

	private static final Logger LOG = LoggerFactory.getLogger(YiQiangPayUtil.class);

	public static String registerYiQiangMer(Map<String, String> data) {
		try {
			ApplyMerInfo mer = new ApplyMerInfo();
			mer.setTranType("HPMERENT");
			mer.setTranDate(data.get("tranDate"));
			mer.setTranTime(data.get("tranTime"));
			mer.setMerTrace(data.get("merTrace"));
			mer.setFrontMid(data.get("frontMid"));
			mer.setMerName(data.get("merName"));
			mer.setRealName(data.get("realName"));
			mer.setMerState(data.get("merState"));
			mer.setMerCity(data.get("merCity"));
			mer.setMerAddress(data.get("merAddress"));
			mer.setCertType(data.get("certType"));
			mer.setCertId(data.get("certId"));
			mer.setMobile(data.get("mobile"));
			mer.setAccountId(data.get("accountId"));
			mer.setAccountName(data.get("accountName"));
			mer.setBankName(data.get("bankName"));
			mer.setBankCode(data.get("bankCode"));
			// mer.setOpenBName ("");
			// mer.setOpenBCode ("");
			// mer.setOpenBState("");
			// mer.setOpenBCity ("");
			// mer.setPosCemage ("");
			// mer.setBackCemage("");
			// mer.setHandCemage("");
			// mer.setFirBuzmage("");
			// mer.setSecBuzmage("");
			// mer.setBuzLicmage("");
			// mer.setOpenPemage("");
			mer.setOperFlag(data.get("operFlag"));
			mer.setT0drawFee(data.get("t0drawFee"));
			mer.setT0drawRate(data.get("t0drawRate"));
			mer.setT1consFee(data.get("t1consFee"));
			mer.setT1consRate(data.get("t1consRate"));
			String encryptStr = JSON.toJSONString(mer);
			String respContext = sendPost(encryptStr, "HPMERENT");
			return respContext;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	public static String sendPayYiQiangD0(Map<String, String> data) {
		// 交易码 tranType String M HPCONPAY
		// 商户流水 merTrace String M 商户流水号，商户须保证流水唯一
		// 合作商户编号 merNo String M 合作商户的唯一标识
		// 商户订单号 orderNo String M 商户交易订单号
		// 订单币种 currencyCode String M 默认为156
		// 订单金额 orderAmount String M 单位(分)
		// 姓名 name String C 如果四要素校验，此项为必录项； 如果同名校验，此项为必录项，录入姓名必须和入网时真实姓名一致
		// 身份证号 idNumber String C 15位或18位身份证号； 如需实名认证和同名校验，此项为必录项；
		// 录入身份证号必须和入网时身份证号一致。
		// 卡号 accNo String C 银行卡号； 如需实名认证，此项为必录项；
		// 手机号 telNo String C 如需实名认证，此项为必录项；
		// 产品类型 productType String M 默认值：100000
		// 支付类型 paymentType String M 默认值：2008
		// 商户类型 merGroup String C
		// 前台通知地址url frontUrl String M 支付结果通知url，也可通过查询获取支付结果
		// 后台通知地址url backUrl String M 支付结果通知url，也可通过查询获取支付结果
		NoCardDebit ncd = new NoCardDebit();
		ncd.setTranType("HPCONPAY");
		ncd.setMerTrace(data.get("merTrace"));
		ncd.setMerNo(data.get("merNo"));
		ncd.setOrderNo(data.get("orderNo"));
		ncd.setCurrencyCode(data.get("currencyCode"));
		ncd.setOrderAmount(data.get("orderAmount"));
		ncd.setName(data.get("name"));
		ncd.setIdNumber(data.get("idNumber"));
		ncd.setAccNo(data.get("accNo"));
		ncd.setTelNo(data.get("telNo"));
		ncd.setProductType(data.get("productType"));
		ncd.setPaymentType(data.get("paymentType"));
		ncd.setMerGroup(data.get(""));
		ncd.setFrontUrl(data.get("frontUrl"));
		ncd.setBackUrl(data.get("backUrl"));

		String encryptStr = JSON.toJSONString(ncd);
		String respContext = sendPost(encryptStr, "HPCONPAY");

		JSONObject jsonResp = JSONObject.parseObject(respContext, JSONObject.class);
		String htmlContext = jsonResp.get("html").toString();
		try {
			String encryptHtml = AESCodeUtil.encrypt(htmlContext);
			jsonResp.put("html", encryptHtml);
		} catch (Exception e) {
			jsonResp.put("html", "");
			e.printStackTrace();
		}
		return jsonResp.toJSONString();
	}

	public static String searchOrder(Map<String, String> data) {
		TransQuery tq = new TransQuery();
		tq.setTranType(data.get("tranType"));
		tq.setMerTrace(data.get("merTrace"));
		tq.setMerNo(data.get("merNo"));
		tq.setOrderNo(data.get("orderNo"));

		String encryptStr = JSON.toJSONString(tq);
		String respContext = sendPost(encryptStr, "HPCONQRY");
		return respContext;
	}

	public static String sendPost(String reqcontext, String tranType) {
		// String prikeyPath = "/flypay.pri";
		// String pubkeyPath = "/flypay.pub";
		// RSAPublicKey pubKey = null;
		// RSAPrivateKey priKey = null;
		// // 加载公钥
		// try {
		// InputStream in = RSAEncrypt.class.getResourceAsStream(pubkeyPath);
		// pubKey = RSAEncrypt.loadPublicKey(in);
		// LOG.info("YiQiangPay 加载公钥成功");
		// } catch (Exception e) {
		// System.err.println(e.getMessage());
		// LOG.error("YiQiangPay 加载公钥失败");
		// }
		// // 加载私钥
		// try {
		// InputStream in = RSAEncrypt.class.getResourceAsStream(prikeyPath);
		// priKey = RSAEncrypt.loadPrivateKey(in);
		// LOG.info("YiQiangPay 加载私钥成功");
		// } catch (Exception e) {
		// System.err.println(e.getMessage());
		// LOG.error("YiQiangPay 加载私钥失败");
		// }
		try {
			// String sign = RSAEncrypt.sign(reqcontext, priKey, "SHA1WithRSA");
			String sign = sign(reqcontext);
			// String cipher = RSAEncrypt.encrypt(pubKey,
			// reqcontext.getBytes("utf-8"));
			String cipher = encrypt(reqcontext);
			RequestInfo req = new RequestInfo(YiQiangApplication.ORG_CODE, sign, cipher);
			JSONObject result = HttpRequestUtils.httpPost(YiQiangApplication.TARGET_URL, req);
			// 解密
			String body = (String) result.get("body");
			String plainText = decrypt(body);
			return plainText;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	public static String sign(String reqcontext) {
		String prikeyPath = "/flypay.pri";
		RSAPrivateKey priKey = null;
		// 加载私钥
		try {
			InputStream in = RSAEncrypt.class.getResourceAsStream(prikeyPath);
			priKey = RSAEncrypt.loadPrivateKey(in);
			LOG.info("YiQiangPay 加载私钥成功");
			String sign = RSAEncrypt.sign(reqcontext, priKey, "SHA1WithRSA");
			return sign;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			LOG.error("YiQiangPay 加载私钥失败");
		}
		return null;
	}

	public static String encrypt(String reqcontext) {
		String pubkeyPath = "/flypay.pub";
		RSAPublicKey pubKey = null;
		// 加载公钥
		try {
			InputStream in = RSAEncrypt.class.getResourceAsStream(pubkeyPath);
			pubKey = RSAEncrypt.loadPublicKey(in);
			LOG.info("YiQiangPay 加载公钥成功");
			String cipher = RSAEncrypt.encrypt(pubKey, reqcontext.getBytes("utf-8"));
			return cipher;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			LOG.error("YiQiangPay 加载公钥失败");
		}
		return null;
	}

	public static String decrypt(String body) {
		String prikeyPath = "/flypay.pri";
		RSAPrivateKey priKey = null;
		// 加载私钥
		try {
			InputStream in = RSAEncrypt.class.getResourceAsStream(prikeyPath);
			priKey = RSAEncrypt.loadPrivateKey(in);
			LOG.info("YiQiangPay decrypt 加载私钥成功");
			String plainText = new String(RSAEncrypt.decrypt(priKey, new BASE64Decoder().decodeBuffer(body)), "utf-8");
			// return sign;
			LOG.info("YiQiangPay decrypt plainText={}", plainText);
			return plainText;
		} catch (Exception e) {
			LOG.error("YiQiangPay 加载私钥失败-{}",e.getMessage());
		}

		// System.out.println("result:" + plainText);
		return null;
	}

	public static void main(String[] args) {
		Map<String, String> params = new HashMap<String, String>();
//		params.put("tranType", "HPCONQRY");
//		params.put("merTrace", DateUtil.getyyyyMMddHHmmssStringFromDate(new Date()));
//		params.put("merNo", "528888800000745");
//		params.put("orderNo", "YiQZTCYLZXE201801031908289040000000011");
//		params.put("productType", "100000");
//		params.put("paymentType", "2008");
//		searchOrder(params);
		
		String body = "X6aAFBKzOl8Pf8xz9Lx7UumsyuwAyDBunlZD4isf+cmgvu0QgcoOY1BRPObKzgMTdM2DKTjKqydt5g0ZqmBtwX53shzMbK5pBkAhY2qQeUzON0mappkvKS25TUD0yGaNH00F0a60DzifiZLYHppswJG793iBIBPkK5Vq7kb3DHswEV70CkpW3bLvOG3II9/tF374wb12l0/ka7YJwHdO5xudOffafuNDJtX1ejPP/uB9cRNmOjq4ZoUIXmiDHp1+zaVwRApceM9cr9b7K9bosE8bePNLVfJFpI05m/HPgB5O7bMsiVK9xB3yMLTGVU2Pu1EmAbnltdoJt0BihMB5Wg==";
		
		decrypt(body);
		
		
	}
}
