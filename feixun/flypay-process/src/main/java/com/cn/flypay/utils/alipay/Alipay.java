package com.cn.flypay.utils.alipay;

import com.cn.flypay.utils.ApplicatonStaticUtil;

public class Alipay {

	public static String alipayPublicKey = (String) ApplicatonStaticUtil.getAppStaticData("alipay.alipayPublicKey");
	public static String privateKey = (String) ApplicatonStaticUtil.getAppStaticData("alipay.privateKey");

	public static String appId = (String) ApplicatonStaticUtil.getAppStaticData("alipay.appId");
	public static String URL = (String) ApplicatonStaticUtil.getAppStaticData("alipay.url");
	public static String qr_pay_notify = (String) ApplicatonStaticUtil.getAppStaticData("alipay.qr_pay_notify");

	public static String RETURN_CODE_SUCCESS = "10000";
	public static String RETURN_CODE_FAILURE = "40004";

	public static String AUTH_CODE_URL = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=%s&scope=%s&state=%s&redirect_uri=%s";

}
