package com.cn.flypay.utils;

import java.util.HashMap;
import java.util.Map;

public class ApplicatonStaticUtil {
	public static String THEME = "_app_static_theme_";
	private static Map<String, Object> appMap = new HashMap<String, Object>();
	public static String product_url = "https://bbpurse.com/flypayfx";
	public static String test_url = "http://ffy.ngrok.sapronlee.com/flypayfx";
	public static String success_pay_url = product_url + "/payment/successedpay?orderNum=%s";
	public static String success_wxpay_url = "http://www.rong66.com/xxqdff.php?id=102&channel=ff";

	public static void addAppStaticData(String key, Object obj) {
		appMap.put(key, obj);
	}

	public static Object getAppStaticData(String key) {
		return appMap.get(key);
	}

	public static boolean hasAppStaticData(String key) {
		return appMap.containsKey(key);
	}

	static {
		addAppStaticData("wxaccount.appid", "wxc7efde5ed05f36da");
		addAppStaticData("wxaccount.mchId", "1292535601");
		addAppStaticData("wxaccount.subMchId", "1397017302");
		addAppStaticData("wxaccount.ip", "127.0.0.1");
		addAppStaticData("wxaccount.notifyUrl", product_url + "/flypayfx/payment/wxqrnotify");
		addAppStaticData("wxaccount.appPaySecret", "ff123456789012345678901234567890");

		addAppStaticData("ylaccount.bankUrl", "http://101.200.34.95:26370/flypayfx/payment/ylNotify");
		addAppStaticData("ylaccount.appId", "71");
		addAppStaticData("ylaccount.channel", "0");

		addAppStaticData("pinganAccount.accNo", "00901027000000013000");
		addAppStaticData("pinganAccount.signNo", "11017374240002");
		addAppStaticData("pinganAccount.protocol", "Q000001283");
		addAppStaticData("pinganAccount.feeCode", "00000");
		// 生产环境
		addAppStaticData("jiguang.appKey", "4894a7a4ecee72698d674385");
		addAppStaticData("jiguang.masterSecret", "70619416bae31a9241729329");

		// 测试环境
		// addAppStaticData("jiguang.appKey", "f6b4639a64620216871ffb54");
		// addAppStaticData("jiguang.masterSecret", "5a34930a512f1486ae5911db");

		// addAppStaticData("alipay.alipayPublicKey",
		// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB");
		// addAppStaticData("alipay.privateKey",
		// "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAO1jQNWPRGi2EmHHjoowWVOKjhERjblBuHi+yaYtW+EWrpqhlsPhd++B2TJtGZ6mulDIOIiwbMMdHm4IM1Gp8/oNVezyU2Myms0kcP+Yyu67Zowc6xrnE3GL3f+oRlAUe7HFEUjz/1c/6qunlfVUVCSOCQZ9LMNzK/lRnqRAqWRpAgMBAAECgYEA3j5J+KZEeXPcCcb2X9GcD2ZsUvaIJKSuu/yCdR2KI1VE6mfV6lUwH/8FBPy3O50EWZTZuL7TVGfykTkEMrRWQDqKtM8sL9Vsd1x85FeYoLKdweRxdLKrvvX1Nyxq+pOC/VDV4snwX9IpcFTr7yOwIql5J2ajgge5tKwSNNz2vLECQQD648+IWVY5O+4iaiuFQt7aWqA+ZD5w/X0DXd4G6oQYAxcFQDREylMBC+cOumrwUsdqjqJ8Km08Rryz2jAdpi2bAkEA8jkKITkf19taznjH386GGAc2eo88CpqHmAVgICgDd+Zmsg0dTAIffSqx2E9U/GIi5vq+7dqpv53nfhQooxKYSwJALxo2DFM5bOVjf/KO2SYE0NnS2+PWFUCCB+/Hj2TYB+oMmF/FcGTL0JZ7erhk+1mPNuIwfz834ukk5F9CSU65oQJBALjMa74NJw44fH8qBtPpbMTIO/glb1lnrCt/bmcaIAtJ7O/KsZ9bv0pvWF//pocnGBnBFAnD2d4nSoYWOyqu9SECQQCDqWq4H2xWVNPswaaWAoEpxU1E21bF85wfJehC65HOlYhVN5mc8/4v8c8c0ULPmcOtuZQ/d4N0+wiq++mZ+o1x");
		// addAppStaticData("alipay.appId", "2016090801867163");

		ApplicatonStaticUtil
				.addAppStaticData(
						"alipay.alipayPublicKey",
						"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB");
		ApplicatonStaticUtil
				.addAppStaticData(
						"alipay.privateKey",
						"MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALtVLa4Iwbtread4NF+oi3JCIBzFBUtWaioRNe27FrmkZVt7lvmXcnrGl5l9IbqqR7ZoVQPZcXygq/bU+qSrTA2sHGx2JAN25M+Q2XBwZcdLgLn8yfVGJc5uLn5XTMp6K5D1OFpeWbk0QVuG9JOvnsFLoHxlEh9SFS5jG5uBWk11AgMBAAECgYAotmtWDlskQ0hOKCahv3yG2W4nMn5jECXEfLcOsoFO2TaZ+jMmjU549l4OIo7JBabOMsB/NKugXT7iRwkt7nh5hFy8k25XfnJTgqltNNAu9hn7inKhG7AJ6k/5y7fF1vhzwNFpl7f9w9OA5ErnCx/DiFbosbBwyFXwkGKBp3PIZQJBAPZOQMibmx+JgZvw2xoP9wLSUSdjlyMl06RRbFz5ptvabPPtAeL9YYWpq/6GrL6McvZyMUfkB6zU2ljs5e5NmqcCQQDCtLiNk7bj6h/ofWPsUa8HAZkxdVRxc+GnqVcApCw26KVydDp/01+4NCJaVJIx7JpuhBBB2BRcPzc4101hocaDAkEA2dFoP0BFFcW/5g1BZjH6KvedpyzOitm36dfL8gkhw8nNkoXlpjyX0uGy5dtpxvVj8YgZBs+k7fYqxntx96WPfwJBAL+noJpLspU6GlqBQuCVrxHYcvEKKyraBF9DAw7wi00pR+IXopW8fNYXC1GbOaTG32S1vkLx7Wi4GSVsWREnav0CQHKuHngVGJZelZxdYBDGc5SfdhURlrLoDrVWF87r0Tqh3TdnxaT1sErrj8HDMI8nx3REru20bWCHGGr+0qJwqn8=");
		ApplicatonStaticUtil.addAppStaticData("alipay.appId", "2016090801867163");// 涩零
																					// 2016092701985014

		ApplicatonStaticUtil.addAppStaticData("alipay.isv.pid", "2088121262042213");

		addAppStaticData("alipay.url", "https://openapi.alipay.com/gateway.do");
		addAppStaticData("alipay.qr_pay_notify", product_url + "/flypayfx/payment/alipayNotify");

		// 线上支付 授权回调地址，需要公共号中回调地址一致
		addAppStaticData("wxaccount.ggh.auth_url", product_url + "/popularizePay/weinxinAuth");
		// addAppStaticData("wxaccount.ggh.auth_url",
		// "http://flypay.ngrok.sapronlee.com/flypayfx/popularizePay/weinxinAuth");
		// addAppStaticData("wxaccount.ggh.appid", "wx35c834469e74c27e");
		// addAppStaticData("wxaccount.ggh.key",
		// "ef889b6c5a03ff151c1378fa5b57d7a9");
//		addAppStaticData("wxaccount.ggh.appid", "wxe360f770a9d69209");
//		addAppStaticData("wxaccount.ggh.key", "93e918f4112e48e3f31b9cae9707af5a");
		
		
		addAppStaticData("wxaccount.ggh.appid", "wxc7efde5ed05f36da");
		addAppStaticData("wxaccount.ggh.key", "0c9cf2683658d00c37ce610950d34627");
		

		// 线上支付 授权回调地址，需要支付宝中回调地址一致
		addAppStaticData("alipay.auth_url", product_url + "/popularizePay/alipayAuth");
	}
}
