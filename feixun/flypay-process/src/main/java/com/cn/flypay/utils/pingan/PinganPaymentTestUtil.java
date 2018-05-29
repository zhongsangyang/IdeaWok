package com.cn.flypay.utils.pingan;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.cn.flypay.pageModel.payment.pingan.CommonPinganRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationAlipayTradeQueryRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationBestpayH5payRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationFinanceDownloadbillRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationJdpayH5payRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantAlipayTradePayRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantAlipayTradePrecreateRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantBankBindRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantCreateRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantQueryRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantRateQuery;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantRateSetRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationWxTradePayRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationWxTradePrecreateRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationWxpayMppayRequest;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.DateUtil;

public class PinganPaymentTestUtil {

	private static String prikey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMdeZrOgsBuOhAP6oYf4nfnLjKl1o+rfqWrg9qRzb8o2M9O/9cScNdL5PtK7KqdcstIrqIraxma4TbDESt2ShLAPSahfG9QTOatDLB0u1pdoOUAWW6Q595yJKXhZza842Yb7hO8Uw+yDB6W5OwTb/o9oZUX2YaJqpdgpmjsc8yjzAgMBAAECgYEAhVi0pIxjferyjZ7DD6jQMNVePbG7c0spi5zwGspDCSL7wzNvdJNuxK+Ev3oEa3BrAnvE/Sqa7PV0sTh5Qn3PVKqVMwe4vXMgx/YCB3I4KZ4vLSOVD+YUxU6ByqQaq3P5tTJjnqWJ8NrXtIabdOaNy9Yi5hBfT2GyFruvDO1ONBECQQD9RYaa8e1v5ccYRZbHRh+9iDj7e7ieCVJ3mdapQARnizXZHdLHNZwzX3udqjrEDRkd5L2QjA0iuMBZPVE8t81tAkEAyYQ4xXYn1sRYF1vzmntKsfC/mskxeYorEq0MQzSJVnTAGRC6TCCh1gN8XASvf0Okr6N9vOFqtitM7Rka6ISz3wJBANaXWf1ejjcJES/XhnFBURNdoCo0IbCFZYJArkipHRI+OVnEkxqGqdo9RJfJ7BDAqE9Tx+n5QAfzUcZK1dIESeECQCFVnvKDFhn/xraufkCKrpN6yKc5Ktb5FSD0wTeIxEMp8vDyhG69Yyf80aCDIejCbsajG9SX0UgrJ4F9Cqd19C8CQQCYxlkTirnSUiUjlRycyH/bAiKteVkDzI8ajTlpCsW/8gK8MKwFy5vTpx7T15RVkayN/b9Yt3VzTrObNEkF+lTt";
	// private static String pubkey =
	// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDUItNHcdm7GZXlSeuyccm7qLZLMLgN6e5HASujDwlpDP51vzxT7JCcIB7I3HMmMYxws90l3F61Nt0N+o7CzmMi2/IukN70zYsp3W82GYYcw8/Ay7rHKeKh5yyV3Ux1ZIqIhapURhENpXbOOrdQBQD/c9TEZvpO5VuHOM5NyzLvfwIDAQAB";
	private static String pubkey_test = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCvfF60WVN2DerwRB2OdMkDCZKeatSmTVyc/XrjiZ3/T6uC33xghbDDL+dy1LXmQufVUzKlJwh8Q9HVaBdtiPQGRqZCsuL+gNRKc3P2L9RMP1dG1QQ/Uv23U7/Z/Lm0QbSZXcuMOV0Y8fWfgYCjoJ1CEoxnQiFQ2rq58cf0h2hAiwIDAQAB";
	// private static String visit_product_url =
	// "https://openapi-liquidation.51fubei.com/gateway";
	private static String visit_product_url = "https://openapi-liquidation-test.51fubei.com/gateway";
	private static String visit_product_url_test = "https://openapi-liquidation-test.51fubei.com/gateway";

	public static String online_wx_pay_product_url = "https://openapi-liquidation.51fubei.com/payPage/?prepay_id=%s&callback_url=%s";
	public static String online_wx_pay_test_url = "https://openapi-liquidation-test.51fubei.com/payPage/?prepay_id=%s&callback_url=%s";
	// public static String online_jd_pay_product_url =
	// "https://openapi-liquidation.51fubei.com/jdPayPage?prepay_id=%s&trade_no=%s&body=%s&callback_url=%s";
	public static String jd_pay_product_url = "https://openapi-liquidation-test.51fubei.com/jdPayPage?prepay_id=%s&trade_no=%s&body=%s&callback_url=%s";
	public static String jd_pay_test_url = "https://openapi-liquidation-test.51fubei.com/jdPayPage?prepay_id=%s&trade_no=%s&body=%s&callback_url=%s";

	public static String best_pay_product_url = "https://openapi-liquidation-test.51fubei.com/bestpayPage?prepay_id=%s&trade_no=%s&body=%s&callback_url=%s";
	public static String beat_pay_test_url = "https://openapi-liquidation-test.51fubei.com/bestpayPage?trade_no=%s&body=%s&callback_url=%s";

	public static String success_pay_url = ApplicatonStaticUtil.test_url + "/payment/successedpay?orderNum=%s";
	public static String wxpa = "20161121150906027835";// "20161114135927025263";//
														// "20161108142250021320";//
														// 20161108142250021320

	public static String wxpa_test = "20161212175953028529";
	public static String appId = "20161102104403706";
	public static String appId_test = "20161028140554435";
	public static String wxpa_self = "WX2016001";
	private final static Logger logger = LoggerFactory.getLogger(PinganPaymentUtil.class);

	private static Map<String, String> methodMap = new HashMap<String, String>();

	static {
		methodMap.put("FshowsLiquidationSubmerchantAlipayTradePayRequest", "fshows.liquidation.submerchant.alipay.trade.pay");
		methodMap.put("FshowsLiquidationAlipayTradeQueryRequest", "fshows.liquidation.alipay.trade.query");
		methodMap.put("FshowsLiquidationFinanceDownloadbillRequest", "fshows.liquidation.finance.downloadbill");
		methodMap.put("FshowsLiquidationSubmerchantAlipayTradePrecreateRequest", "fshows.liquidation.submerchant.alipay.trade.precreate");
		methodMap.put("FshowsLiquidationSubmerchantBankBindRequest", "fshows.liquidation.submerchant.bank.bind");
		methodMap.put("FshowsLiquidationSubmerchantCreateRequest", "fshows.liquidation.submerchant.create");
		methodMap.put("FshowsLiquidationSubmerchantQueryRequest", "fshows.liquidation.submerchant.query");
		methodMap.put("FshowsLiquidationWxpayMppayRequest", "fshows.liquidation.wxpay.mppay");
		methodMap.put("FshowsLiquidationWxTradePayRequest", "fshows.liquidation.wx.trade.pay");
		methodMap.put("FshowsLiquidationWxTradePrecreateRequest", "fshows.liquidation.wx.trade.precreate");
		methodMap.put("FshowsLiquidationJdpayH5payRequest", "fshows.liquidation.jdpay.h5pay");
		methodMap.put("FshowsLiquidationSubmerchantRateSetRequest", "fshows.liquidation.submerchant.rate.set");
		methodMap.put("FshowsLiquidationBestpayH5payRequest", "fshows.liquidation.bestpay.h5pay");
		methodMap.put("FshowsLiquidationSubmerchantRateQuery", "fshows.liquidation.submerchant.rate.query");
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		// System.out.println(URLEncoder.encode("我真棒","UTF-8"));
		// createPinganSubMerchent();
		// zhifubaoSMpay();
		// zhifubaoQRpay();
		// submerchantRateSet();
		// jingdongQRpay();
//		yizhifuQRpay();
		// zhifubaoOnLinepay();
		// submerchantQuery();
		// tradeQuery();
		// bankBind();
		// wxpayMppay();//TODO
		// wxTradeSMPay();//TODO
		// wxTradePrecreate();

		// downloadbill();
//		 String rt =
//		 "trade_no=2017021014252201919293395102&out_trade_no=YIQR201702101425235800000000004&retn_code=0000&retn_info=0000&tran_date=20170210&total_fee=0.01&net_receipt_amount=0.01&attach_amount=0.00&cur_type=RMB&bank_commission_fee=0.00000&bank_commission_rate=0.00050&liquidator_commission_fee=0.00000&liquidator_commission_rate=0.00350&pay_platform_fee=0.00000&pay_platform_rate=0.00200&sign=n259ITqSRqUxLUz%2Bn7IcZwhPP8U1Nsj5iyrHkTzTAaCqZJZLwa8FV2n9jOf2RDXGwLQ0QVqnnXs0h83VrHqT%2F18l96zib8udP0P%2FRjayK4f7MbtzA4zbF%2FDhulqHgN0TNZ9gGZmU0atWyP%2FinAwqVs0ddYRpu3jtVQkk7XfKAy0%3D&sign_type=RSA";
//		
//		System.out.println(isValidSign(rt));
		
		submerchantRateget();

	}

	public static void createPinganSubMerchent() {
		FshowsLiquidationSubmerchantCreateRequest req = new FshowsLiquidationSubmerchantCreateRequest();
		req.setExternal_id("FF_JD2016001");
		req.setName("京东收款");
		req.setAlias_name("京东收款");
		req.setService_phone("13052222696");
		req.setCategory_id("2016062900190068");
		// 20161108142250021320
		// JSONObject json = sentRequstToPingAnPayment(req, appId,
		// FshowsLiquidationSubmerchantCreateRequest.class);
		// System.out.println(json);
		JSONObject json2 = sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantCreateRequest.class);
		System.out.println(json2.toJSONString());
	}

	public static void zhifubaoSMpay() {
		String auth = "286549589217175482";
		FshowsLiquidationSubmerchantAlipayTradePayRequest req = new FshowsLiquidationSubmerchantAlipayTradePayRequest();
		req.setOut_trade_no("SY20161028063");
		req.setNotify_url("http://ffy.ngrok.sapronlee.com/flypayfx/payment/pinganFeedback");
		req.setScene("bar_code");
		req.setAuth_code(auth);
		req.setTotal_amount("0.01");
		req.setSubject("test");
		req.setSub_merchant(JSONObject.parseObject("{\"merchant_id\":\"" + wxpa + "\"}"));
		sentRequstToPingAnPayment(req, "20161028140554435", FshowsLiquidationSubmerchantAlipayTradePayRequest.class);
		// req.setContent(JSONObject.toJSONString(req));
		// req.setApp_id("20161028140554435");
		// sentRequstToPingAnPayment(req);
	}

	public static void zhifubaoQRpay() {

		FshowsLiquidationSubmerchantAlipayTradePrecreateRequest req = new FshowsLiquidationSubmerchantAlipayTradePrecreateRequest();

		String num = DateUtil.getyyyyMMddHHmmssStringFromDate(new Date());
		req.setOut_trade_no(num);
		logger.info(num);
		req.setNotify_url("http://xymtian.6655.la/flypayfx/payment/pinganFeedback");
		req.setTotal_amount("0.01");
		req.setSubject("test");

		req.setSub_merchant(JSONObject.parseObject("{\"merchant_id\":\"" + wxpa + "\"}"));

		JSONObject json2 = sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantAlipayTradePrecreateRequest.class);
		System.out.println(json2.toJSONString());
	}

	public static void jingdongQRpay() {

		FshowsLiquidationJdpayH5payRequest req = new FshowsLiquidationJdpayH5payRequest();

		String num = DateUtil.getyyyyMMddHHmmssStringFromDate(new Date());
		req.setOut_trade_no(num);
		logger.info(num);
		req.setSub_merchant_id(wxpa_test);
		req.setNotify_url("http://ffy.ngrok.sapronlee.com/flypayfx/payment/pinganPayNotify");
		req.setBody("我真棒");
		req.setTotal_fee("0.01");

		JSONObject json2 = sentTestRequstToPingAnPayment(req, appId_test, FshowsLiquidationJdpayH5payRequest.class);
		System.out.println(json2.toJSONString());
	}

	public static void yizhifuQRpay() {

		FshowsLiquidationBestpayH5payRequest req = new FshowsLiquidationBestpayH5payRequest();

		String num = DateUtil.getyyyyMMddHHmmssStringFromDate(new Date());
		req.setOut_trade_no(num);
		logger.info(num);
		req.setSub_merchant_id(wxpa_test);
		req.setNotify_url("http://klrsheng.51vip.biz/flypayfx/payment/pinganPayNotify");
		req.setBody("我真棒");
		req.setTotal_fee("0.01");

		JSONObject json2 = sentTestRequstToPingAnPayment(req, appId_test, FshowsLiquidationBestpayH5payRequest.class);
		System.out.println(json2.toJSONString());
	}

	public static void submerchantRateSet() {

		FshowsLiquidationSubmerchantRateSetRequest req = new FshowsLiquidationSubmerchantRateSetRequest();

		req.setSub_merchant_id("20170223112339026759");
		req.setMerchant_rate("0.0038");

		JSONObject json2 = sentTestRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantRateSetRequest.class);
		System.out.println(json2.toJSONString());
	}
	
	
	public static void submerchantRateget() {

		FshowsLiquidationSubmerchantRateQuery req = new FshowsLiquidationSubmerchantRateQuery();

		req.setSub_merchant_id("20170223112339026759");

		JSONObject json2 = sentTestRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantRateQuery.class);
		System.out.println(json2.toJSONString());
	}

	public static void zhifubaoOnLinepay() {

		FshowsLiquidationSubmerchantAlipayTradePrecreateRequest req = new FshowsLiquidationSubmerchantAlipayTradePrecreateRequest();

		String num = DateUtil.getyyyyMMddHHmmssStringFromDate(new Date());
		req.setOut_trade_no(num);
		logger.info(num);
		req.setNotify_url("http://xymtian.6655.la/flypayfx/payment/pinganFeedback");
		req.setTotal_amount("0.01");
		req.setSubject("test");

		req.setSub_merchant(JSONObject.parseObject("{\"merchant_id\":\"" + wxpa + "\"}"));

		JSONObject json = sentRequstToPingAnPayment(req, "20161028140554435", FshowsLiquidationSubmerchantAlipayTradePrecreateRequest.class);
		String qrCode = json.getJSONObject("return_value").getString("qrCode");
		System.out.println(qrCode);

		// req.setContent(JSONObject.toJSONString(req));
		// req.setApp_id("20161028140554435");
		// sentRequstToPingAnPayment(req);
	}

	public static void submerchantQuery() {

		FshowsLiquidationSubmerchantQueryRequest req = new FshowsLiquidationSubmerchantQueryRequest();
		// req.setExternal_id(wxpa);
		req.setSub_merchant_id("20161114135927025263");

		JSONObject json2 = sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantQueryRequest.class);
		System.out.println(json2.toJSONString());
	}

	public static void tradeQuery() {
		CommonPinganRequest cpr = new CommonPinganRequest();
		cpr.setApp_id("20161028140554435");
		cpr.setMethod("fshows.liquidation.alipay.trade.query");
		cpr.setVersion("1.0");

		FshowsLiquidationAlipayTradeQueryRequest req = new FshowsLiquidationAlipayTradeQueryRequest();
		// req.setTrade_no(trade_no);
		req.setOut_trade_no("SY20161028002");

		cpr.setContent(JSONObject.toJSONString(req));

		Map<String, String> map = JSONObject.parseObject(JSONObject.toJSONString(cpr), Map.class);
		// 去除头尾、换行和空格
		// 生成签名, 用于调用具体接口
		String sign;
		try {
			sign = AlipaySignature.rsaSign(map, prikey, "utf-8");

			// 参数为生成签名时的参数, 增加签名 sign参数
			map.put("sign", sign);

			// 发起post请求, 返回值为接口响应值, json格式
			String result;
			try {
				result = PinganHttpUtil.getInstance().doPostForString("https://openapi-liquidation-test.51fubei.com/gateway", map);
				System.out.println(result);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (AlipayApiException e1) {
			e1.printStackTrace();
		}

	}

	public static void bankBind() {
		String wxpa = "20161222095318023406";
		FshowsLiquidationSubmerchantBankBindRequest req = new FshowsLiquidationSubmerchantBankBindRequest();
		req.setSub_merchant_id(wxpa);
		// req.setBank_card_no("622468000101082622");
		// req.setCard_holder("叶明秀");// 6214851213282739

		req.setBank_card_no("6214852111454099");
		req.setCard_holder("芦强");// 6214851213282739

		// req.setBank_card_no("6013820800101978854");
		// req.setCard_holder("孙月");

		JSONObject json2 = sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantBankBindRequest.class);
		System.out.println(json2.toJSONString());
	}

	public static void wxTradeSMPay() {
		String auth = "130226525092664230";

		FshowsLiquidationWxTradePayRequest req = new FshowsLiquidationWxTradePayRequest();
		req.setBody("宝贝钱袋");
		req.setOut_trade_no("SY201610281662");
		req.setTotal_fee("0.01");
		req.setSpbill_create_ip("127.0.0.1");
		req.setAuth_code(auth);
		req.setStore_id(wxpa);

		sentRequstToPingAnPayment(req, "20161028140554435", FshowsLiquidationWxTradePayRequest.class);
	}

	public static void wxpayMppay() {
		String openId = "oLhI2uPYqpHlTj5RGiHmL13WnUSA";

		FshowsLiquidationWxpayMppayRequest req = new FshowsLiquidationWxpayMppayRequest();
		req.setSub_merchant_id("20161028163421024954");
		req.setBody("宝贝钱袋");
		req.setOut_trade_no("SY2016102816621");
		req.setTotal_fee("0.01");
		req.setSpbill_create_ip("127.0.0.1");
		req.setSub_openid(openId);
		// req.notify_url(wxpa);

		sentRequstToPingAnPayment(req, "20161028140554435", FshowsLiquidationWxpayMppayRequest.class);
	}

	public static void wxTradePrecreate() {

		FshowsLiquidationWxTradePrecreateRequest req = new FshowsLiquidationWxTradePrecreateRequest();
		req.setBody("宝贝钱袋");
		String num = DateUtil.getyyyyMMddHHmmssStringFromDate(new Date());

		req.setOut_trade_no("SY" + num);
		req.setTotal_fee("0.01");
		req.setSpbill_create_ip("127.0.0.1");
		logger.info(req.getOut_trade_no());
		req.setNotify_url("http://xymtian.6655.la/flypayfx/payment/pinganFeedback");
		req.setStore_id(wxpa);

		sentRequstToPingAnPayment(req, appId, FshowsLiquidationWxTradePrecreateRequest.class);

	}

	public static JSONObject sentRequstToPingAnPayment(Object obj, String appId, Class T) {
		try {
			CommonPinganRequest cpr = new CommonPinganRequest();
			cpr.setApp_id(appId);
			cpr.setMethod(methodMap.get(T.getSimpleName()));
			cpr.setContent(JSONObject.toJSONString(obj));
			cpr.setVersion("1.0");
			Map<String, String> map = JSONObject.parseObject(JSONObject.toJSONString(cpr), Map.class);
			// 去除头尾、换行和空格
			// 生成签名, 用于调用具体接口
			String sign = AlipaySignature.rsaSign(map, prikey, "utf-8");
			// 参数为生成签名时的参数, 增加签名 sign参数
			map.put("sign", sign);

			// 发起post请求, 返回值为接口响应值, json格式
			String result = PinganHttpUtil.getInstance().doPostForString(visit_product_url_test, map);
			logger.info(result);
			return JSONObject.parseObject(result);
		} catch (AlipayApiException e) {
			logger.error("平安支付签名失败", e);
		} catch (Exception e) {
			logger.error("平安支付请求失败", e);
		}
		return null;
	}

	public static JSONObject sentTestRequstToPingAnPayment(Object obj, String appId, Class T) {
		try {
			CommonPinganRequest cpr = new CommonPinganRequest();
			cpr.setApp_id(appId);
			cpr.setMethod(methodMap.get(T.getSimpleName()));
			cpr.setContent(JSONObject.toJSONString(obj));
			cpr.setVersion("1.0");
			Map<String, String> map = JSONObject.parseObject(JSONObject.toJSONString(cpr), Map.class);
			// 去除头尾、换行和空格
			// 生成签名, 用于调用具体接口
			String sign = AlipaySignature.rsaSign(map, prikey, "utf-8");
			// 参数为生成签名时的参数, 增加签名 sign参数
			map.put("sign", sign);

			// 发起post请求, 返回值为接口响应值, json格式
			String result = PinganHttpUtil.getInstance().doPostForString(visit_product_url_test, map);
			logger.info(result);
			return JSONObject.parseObject(result);
		} catch (AlipayApiException e) {
			logger.error("平安支付签名失败", e);
		} catch (Exception e) {
			logger.error("平安支付请求失败", e);
		}
		return null;
	}

	public static boolean isValidSign(String content) {
		try {
			return AlipaySignature.rsaCheckV1(convertParamtFromPinganPay(content), pubkey_test, "utf-8");
		} catch (Exception e) {
			logger.error("平安支付验证签名异常", e);
		}
		return false;
	}

	public static Map<String, String> convertParamtFromPinganPay(String content) throws Exception {
		String ct = URLDecoder.decode(content, "utf-8");
		logger.info(ct);
		String[] keyValues = ct.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String kv : keyValues) {
			if (!kv.startsWith("sign=")) {
				String[] s1 = kv.split("=");
				map.put(s1[0], s1[1]);
			} else {
				map.put("sign", kv.substring(5));
			}
		}
		return map;
	}

	public static void downloadbill() {

		FshowsLiquidationFinanceDownloadbillRequest req = new FshowsLiquidationFinanceDownloadbillRequest();
		req.setBill_date("20161028");
		req.setPay_platform("1");// 1、支付宝 2、微信

		System.out.println(sentRequstToPingAnPayment(req, "20161028140554435", FshowsLiquidationFinanceDownloadbillRequest.class));
	}

}
