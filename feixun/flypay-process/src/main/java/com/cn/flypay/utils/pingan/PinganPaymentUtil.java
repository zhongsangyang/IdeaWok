package com.cn.flypay.utils.pingan;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.cn.flypay.pageModel.payment.pingan.CommonPinganRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationAlipayTradeQueryRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationBestpayMerchantAccountRequest;
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
import com.cn.flypay.utils.DateUtil;

public class PinganPaymentUtil {
	//生产参数配置
	//生产--私钥
	private static String prikey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMdeZrOgsBuOhAP6oYf4nfnLjKl1o+rfqWrg9qRzb8o2M9O/9cScNdL5PtK7KqdcstIrqIraxma4TbDESt2ShLAPSahfG9QTOatDLB0u1pdoOUAWW6Q595yJKXhZza842Yb7hO8Uw+yDB6W5OwTb/o9oZUX2YaJqpdgpmjsc8yjzAgMBAAECgYEAhVi0pIxjferyjZ7DD6jQMNVePbG7c0spi5zwGspDCSL7wzNvdJNuxK+Ev3oEa3BrAnvE/Sqa7PV0sTh5Qn3PVKqVMwe4vXMgx/YCB3I4KZ4vLSOVD+YUxU6ByqQaq3P5tTJjnqWJ8NrXtIabdOaNy9Yi5hBfT2GyFruvDO1ONBECQQD9RYaa8e1v5ccYRZbHRh+9iDj7e7ieCVJ3mdapQARnizXZHdLHNZwzX3udqjrEDRkd5L2QjA0iuMBZPVE8t81tAkEAyYQ4xXYn1sRYF1vzmntKsfC/mskxeYorEq0MQzSJVnTAGRC6TCCh1gN8XASvf0Okr6N9vOFqtitM7Rka6ISz3wJBANaXWf1ejjcJES/XhnFBURNdoCo0IbCFZYJArkipHRI+OVnEkxqGqdo9RJfJ7BDAqE9Tx+n5QAfzUcZK1dIESeECQCFVnvKDFhn/xraufkCKrpN6yKc5Ktb5FSD0wTeIxEMp8vDyhG69Yyf80aCDIejCbsajG9SX0UgrJ4F9Cqd19C8CQQCYxlkTirnSUiUjlRycyH/bAiKteVkDzI8ajTlpCsW/8gK8MKwFy5vTpx7T15RVkayN/b9Yt3VzTrObNEkF+lTt";
	//生产--公钥
	private static String pubkey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDUItNHcdm7GZXlSeuyccm7qLZLMLgN6e5HASujDwlpDP51vzxT7JCcIB7I3HMmMYxws90l3F61Nt0N+o7CzmMi2/IukN70zYsp3W82GYYcw8/Ay7rHKeKh5yyV3Ux1ZIqIhapURhENpXbOOrdQBQD/c9TEZvpO5VuHOM5NyzLvfwIDAQAB";
	//生产--访问平安--域名
	private static String visit_product_url = "https://openapi-liquidation.51fubei.com/gateway";
	public static String online_wx_pay_product_url = "https://openapi-liquidation.51fubei.com/payPage/?prepay_id=%s&callback_url=%s";	//微信公众号支付页面
	public static String jd_pay_product_url = "https://openapi-liquidation.51fubei.com/jdPayPage?prepay_id=%s&trade_no=%s&body=%s&callback_url=%s";
	public static String best_pay_product_url = "https://openapi-liquidation.51fubei.com/bestpayPage?trade_no=%s&body=%s&callback_url=%s";
	public static String wxpa = "20161222095318023406";// 20161206103840028076
														// 支付宝
	// 20161222095318023406 京东

	//生产平安分配的清算平台APPID
	public static String appId = "20161102104403706";
	public static String wxpa_self = "WX2016001";
	
	
	//额外配置
	//微信配置子商户的授权目录
	public static String jsapi_path = "https://openapi-liquidation.51fubei.com/payPage/";
	//微信子商户配置的微信公众号
	public static String wx_app_id = "wxe19bf8e18dace583";
	//微信子商户配置的微信公众号对应的主体名称
	public static String merchant_name = "上海福别信息技术服务有限公司";
	
	
	
	//本地测试环境参数配置
	
//	//私钥--本地测试
//	private static String prikey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALSvPbiNi4eCT7pmD8uaXcyU+FPM7SqTErhxcDeYg7y/OAM8WGlxL4pQfu0AbsoKigqIvhMTSVOfV2xw+5YQxX13nXlu7HTHu/abh1yMrueq8lttWnrCmegkwruMTB8UK3U97FDhNxae9M7z6ms45wFgSO6L84V+65GNXa+ETDLxAgMBAAECgYEAhhggWF1EFjeR8CZaxqIzxucwPREzWot4M0aNH9Wiv2uyqEQa4XZM7/u5ly7hCjU0KYk+d22y18rD2QkozkB4L9uugg3EvRvlNH2tKR2hCn1gVOpjL0N5iYiXazFx+cKidyf6Xqvphdy4DqS4hDLGDVuMS1xt+piU3haXV34b1hkCQQDlwCMSLrNN90pMrbKbfejPXGkC5h8j94e4OMe0nmSzVjp+5et6g917EkHRiOMTVJ7HPEkpY+GVqq4RnT53i5obAkEAyVP/Cpt8kHDzqgjnWd5ZcuHip0xxOkSewI7xfZBB4BbwRGFdZjxjTx5mM8k/PgN73TjyxhsCT+RgQoKgyqB34wJAI2PAQzm8VuMjp8INxBPV+7MBXffyjgi/+CmO4YyQeGts4UUrS5jqUd3TE6KhT8X2Gzx3DJAdHH/JBl+RUrrHRwJANjYO891AYmh3xC5XlYRg06VtcZ0M1CLMGI+lCSmCA4RJuC/7hV73YsONw54KEHnBraT7AksdiUO2g9JG16jz/wJBAM6cAVyYVagBFTlCJEaHR7G1Z6RpRuQQSqIcyt0UjqgpL7b3WnNV2o3HFsM0b2+2ylRWqhmSr3zqgLluz4NIBUc=";
//	//公钥--本地测试
//	private static String pubkey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCvfF60WVN2DerwRB2OdMkDCZKeatSmTVyc/XrjiZ3/T6uC33xghbDDL+dy1LXmQufVUzKlJwh8Q9HVaBdtiPQGRqZCsuL+gNRKc3P2L9RMP1dG1QQ/Uv23U7/Z/Lm0QbSZXcuMOV0Y8fWfgYCjoJ1CEoxnQiFQ2rq58cf0h2hAiwIDAQAB";
//	//测试环境请求域名
//	private static String visit_product_url = "https://openapi-liquidation-test.51fubei.com/gateway";
//	
//	//平安配置的清算平台ID --测试
//	public static String appId = "20170928110004844";
//	
//	public static String wxpa_self = "WX2016001";
//	
//	//微信配置子商户的授权目录
//	public static String jsapi_path = "http://1g83849h98.iask.in:34530/flypayfx/popularizePay/";
//	//平安提供的测试账号对应的微信公众号
//	public static String wx_app_id = "wxba01e33510fcc661";
//	
//	
//	
//	public static String online_wx_pay_product_url = "https://openapi-liquidation.51fubei.com/payPage/?prepay_id=%s&callback_url=%s";
//	public static String jd_pay_product_url = "https://openapi-liquidation.51fubei.com/jdPayPage?prepay_id=%s&trade_no=%s&body=%s&callback_url=%s";
//	public static String best_pay_product_url = "https://openapi-liquidation.51fubei.com/bestpayPage?trade_no=%s&body=%s&callback_url=%s";
//	public static String wxpa = "20161222095318023406";// 20161206103840028076
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(PinganPaymentUtil.class);

	private static Map<String, String> methodMap = new HashMap<String, String>();
	static {
		methodMap.put("FshowsLiquidationSubmerchantAlipayTradePayRequest", "fshows.liquidation.submerchant.alipay.trade.pay");
		methodMap.put("FshowsLiquidationAlipayTradeQueryRequest", "fshows.liquidation.alipay.trade.query");
		methodMap.put("FshowsLiquidationFinanceDownloadbillRequest", "fshows.liquidation.finance.downloadbill");
		methodMap.put("FshowsLiquidationSubmerchantAlipayTradePrecreateRequest", "fshows.liquidation.submerchant.alipay.trade.precreate");
		methodMap.put("FshowsLiquidationSubmerchantBankBindRequest", "fshows.liquidation.submerchant.bank.bind");
		methodMap.put("FshowsLiquidationSubmerchantCreateRequest", "fshows.liquidation.merchant.create");	//商户入驻清算平台接口
//		methodMap.put("FshowsLiquidationSubmerchantCreateRequest", "fshows.liquidation.submerchant.create.with.auth");	//商户入驻接口
		
		methodMap.put("FshowsAliPayMerchantCreateRequest", "fshows.alipay.merchant.create");	//支付宝—商户入驻接口
		methodMap.put("FshowsLiquidationWxSubmerchantCreateSupplementRequest", "fshows.liquidation.wx.submerchant.create.supplement");	//微信子商户入驻补充接口
		methodMap.put("FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest", "fshows.liquidation.wx.submerchant.config.create.supplement");	//微信子商户配置接口
		methodMap.put("FshowsLiquidationJdMerchantCreate", "fshows.liquidation.jd.merchant.create");	//京东入驻接口
		methodMap.put("FshowsLiquidationSubmerchantRateSetResponse", "fshows.liquidation.submerchant.rate.set");	//设置商户终端费率接口
		methodMap.put("FshowsLiquidationSubmerchantQueryRequest", "fshows.liquidation.submerchant.query");
		methodMap.put("FshowsLiquidationWxpayMppayRequest", "fshows.liquidation.wxpay.mppay");	//微信公众号支付
		methodMap.put("FshowsLiquidationWxTradePayRequest", "fshows.liquidation.wx.trade.pay");
		methodMap.put("FshowsLiquidationWxTradePrecreateRequest", "fshows.liquidation.wx.trade.precreate");
		methodMap.put("FshowsLiquidationJdpayH5payRequest", "fshows.liquidation.jdpay.h5pay");
		methodMap.put("FshowsLiquidationSubmerchantRateSetRequest", "fshows.liquidation.submerchant.rate.set"); //设置商户终端费率接口
		methodMap.put("FshowsLiquidationBestpayH5payRequest", "fshows.liquidation.bestpay.h5pay");
		methodMap.put("FshowsLiquidationSubmerchantRateQuery", "fshows.liquidation.submerchant.rate.query");
		methodMap.put("FshowsLiquidationJdpayUniorderRequest", "fshows.liquidation.jdpay.uniorder");
		methodMap.put("FshowsLiquidationBestpayMerchantAccountRequest", "fshows.liquidation.bestpay.merchant.account");
	}

	public static void main(String[] args) {
//		createPinganSubMerchent();
//		 zhifubaoQRpay();
		// jingdongQRpay();
//		 bankBind();
		submerchantRateget();
//		submerchantRateSet();
	}

	public static void jingdongQRpay() {
		FshowsLiquidationJdpayH5payRequest req = new FshowsLiquidationJdpayH5payRequest();
		String num = DateUtil.getyyyyMMddHHmmssStringFromDate(new Date());
		req.setOut_trade_no(num);
		logger.info(num);
		req.setSub_merchant_id(wxpa);
		req.setNotify_url("http://ffy.ngrok.sapronlee.com/flypayfx/payment/pinganPayNotify");
		req.setBody("我真棒");
		req.setTotal_fee("0.01");

		JSONObject json2 = sentRequstToPingAnPayment(req, appId, FshowsLiquidationJdpayH5payRequest.class);
		System.out.println(json2.toJSONString());
	}

	public static void createPinganSubMerchent() {
		FshowsLiquidationSubmerchantCreateRequest req = new FshowsLiquidationSubmerchantCreateRequest();
		req.setExternal_id("PA_FZB20170223001");
		req.setName("福别收款");
		req.setAlias_name("福别收款");
		req.setService_phone("13296134638");
		req.setCategory_id("2016062900190068");

		JSONObject json = sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantCreateRequest.class);
		System.out.println(json);
	}

	public static void zhifubaoSMpay() {
		String auth = "286549589217175482";
		FshowsLiquidationSubmerchantAlipayTradePayRequest req = new FshowsLiquidationSubmerchantAlipayTradePayRequest();
		req.setOut_trade_no("SY201610280613");
		req.setNotify_url("http://ffy.ngrok.sapronlee.com/flypayfx/payment/pinganFeedback");
		req.setScene("bar_code");
		req.setAuth_code(auth);
		req.setTotal_amount("0.01");
		req.setSubject("test");
		req.setSub_merchant(JSONObject.parseObject("{\"merchant_id\":\"20170223112339026759\"}"));
		sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantAlipayTradePayRequest.class);
	}

	public static void zhifubaoQRpay() {
		FshowsLiquidationSubmerchantAlipayTradePrecreateRequest req = new FshowsLiquidationSubmerchantAlipayTradePrecreateRequest();

		String num = DateUtil.getyyyyMMddHHmmssStringFromDate(new Date());
		req.setOut_trade_no(num);
		logger.info(num);
		req.setNotify_url("http://xymtian.6655.la/flypayfx/payment/pinganFeedback");
		req.setTotal_amount("0.01");
		req.setSubject("test");

		req.setSub_merchant(JSONObject.parseObject("{\"merchant_id\":\"20170223112339026759\"}"));

		JSONObject json2 = sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantAlipayTradePrecreateRequest.class);
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
	}

	public static void submerchantQuery() {
		FshowsLiquidationSubmerchantQueryRequest req = new FshowsLiquidationSubmerchantQueryRequest();

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

		req.setOut_trade_no("SY20161028002");

		cpr.setContent(JSONObject.toJSONString(req));

		Map map = (Map) JSONObject.parseObject(JSONObject.toJSONString(cpr), Map.class);
		try {
			String sign = AlipaySignature.rsaSign(map, prikey, "utf-8");

			map.put("sign", sign);
			try {
				String result = PinganHttpUtil.getInstance().doPostForString("https://openapi-liquidation-test.51fubei.com/gateway", map);
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
		String wxpa = "20170223112339026759";
		FshowsLiquidationSubmerchantBankBindRequest req = new FshowsLiquidationSubmerchantBankBindRequest();
		req.setSub_merchant_id(wxpa);

		req.setBank_card_no("6214852111454099");
		req.setCard_holder("芦强");

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
		logger.info("发送平安报文:"+JSONObject.toJSONString(obj));
		try {
			CommonPinganRequest cpr = new CommonPinganRequest();
			cpr.setApp_id(appId);
			cpr.setMethod((String) methodMap.get(T.getSimpleName()));
			cpr.setContent(JSONObject.toJSONString(obj));
			cpr.setVersion("1.0");
			Map map = (Map) JSONObject.parseObject(JSONObject.toJSONString(cpr), Map.class);
			String sign = AlipaySignature.rsaSign(map, prikey, "utf-8");
			map.put("sign", sign);
			String result = PinganHttpUtil.getInstance().doPostForString(visit_product_url, map);
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
			return AlipaySignature.rsaCheckV1(convertParamtFromPinganPay(content), pubkey, "utf-8");
		} catch (Exception e) {
			logger.error("平安支付验证签名异常", e);
		}
		return false;
	}

	public static Map<String, String> convertParamtFromPinganPay(String content) throws Exception {
		String ct = URLDecoder.decode(content, "utf-8");
		logger.info(ct);
		String[] keyValues = ct.split("&");
		Map map = new HashMap();
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
		req.setPay_platform("1");

		System.out.println(sentRequstToPingAnPayment(req, "20161028140554435", FshowsLiquidationFinanceDownloadbillRequest.class));
	}

	public static void submerchantRateget() {

		FshowsLiquidationSubmerchantRateQuery req = new FshowsLiquidationSubmerchantRateQuery();

		req.setSub_merchant_id("20170223112339026759");

		JSONObject json2 = sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantRateQuery.class);
		System.out.println(json2.toJSONString());
	}
	
	
	public static void submerchantRateSet() {

		FshowsLiquidationSubmerchantRateSetRequest req = new FshowsLiquidationSubmerchantRateSetRequest();

		req.setSub_merchant_id("20170223112339026759");
		req.setMerchant_rate("0.0038");

		JSONObject json2 = sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantRateSetRequest.class);
		System.out.println(json2.toJSONString());
	}
	
	
//	static {
//		methodMap.put("FshowsLiquidationSubmerchantAlipayTradePayRequest", "fshows.liquidation.submerchant.alipay.trade.pay");
//		methodMap.put("FshowsLiquidationAlipayTradeQueryRequest", "fshows.liquidation.alipay.trade.query");
//		methodMap.put("FshowsLiquidationFinanceDownloadbillRequest", "fshows.liquidation.finance.downloadbill");
//		methodMap.put("FshowsLiquidationSubmerchantAlipayTradePrecreateRequest", "fshows.liquidation.submerchant.alipay.trade.precreate");
//		methodMap.put("FshowsLiquidationSubmerchantBankBindRequest", "fshows.liquidation.submerchant.bank.bind");
////		methodMap.put("FshowsLiquidationSubmerchantCreateRequest", "fshows.liquidation.submerchant.create");
//		methodMap.put("FshowsLiquidationSubmerchantCreateRequest", "fshows.liquidation.submerchant.create.with.auth");
//		methodMap.put("FshowsLiquidationSubmerchantRateSetResponse", "fshows.liquidation.submerchant.rate.set");
//		methodMap.put("FshowsLiquidationSubmerchantQueryRequest", "fshows.liquidation.submerchant.query");
//		methodMap.put("FshowsLiquidationWxpayMppayRequest", "fshows.liquidation.wxpay.mppay");
//		methodMap.put("FshowsLiquidationWxTradePayRequest", "fshows.liquidation.wx.trade.pay");
//		methodMap.put("FshowsLiquidationWxTradePrecreateRequest", "fshows.liquidation.wx.trade.precreate");
//		methodMap.put("FshowsLiquidationSubmerchantRateQuery", "fshows.liquidation.submerchant.rate.query");
//	}
}