package com.cn.flypay.utils.weilianbao.entity;

public class OrderPayForWeiLianBaoYinLian {
	
//	trxType	接口类型	是	固定值	CONSUME
//	merchantNo	商户编号	是	10位	B1000001
//	token	卡开通的token	是		3des加密后，使用base64，utf8编码做加密。
//	goodsName	订单描述	是	<150	一条裤子
//	serverDfUrl	消费代付后台回调地址	是		
//	serverCallbackUrl	消费后台回调地址	是	<300	http://c.a.com/server
//	orderNum
//		订单号，同获取消费短信的订单号一致	是		
//	trxTime	提交时间，同获取消费短信验证码的提交时间一致	是		
//	smsCode	短信验证码	是		消费短信收到的验证码，3des加密后，使用base64，utf8编码做加密。
//	sign	签名	是	=32	签名
	
	private String trxType;
	private String merchantNo;
	private String token;
	private String goodsName;
	private String serverDfUrl;
	private String serverCallbackUrl;
	private String orderNum;
	private String trxTime;
	private String smsCode;
	private String sign;
	public String getTrxType() {
		return trxType;
	}
	public void setTrxType(String trxType) {
		this.trxType = trxType;
	}
	public String getMerchantNo() {
		return merchantNo;
	}
	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getServerDfUrl() {
		return serverDfUrl;
	}
	public void setServerDfUrl(String serverDfUrl) {
		this.serverDfUrl = serverDfUrl;
	}
	public String getServerCallbackUrl() {
		return serverCallbackUrl;
	}
	public void setServerCallbackUrl(String serverCallbackUrl) {
		this.serverCallbackUrl = serverCallbackUrl;
	}
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	public String getTrxTime() {
		return trxTime;
	}
	public void setTrxTime(String trxTime) {
		this.trxTime = trxTime;
	}
	public String getSmsCode() {
		return smsCode;
	}
	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	

}
