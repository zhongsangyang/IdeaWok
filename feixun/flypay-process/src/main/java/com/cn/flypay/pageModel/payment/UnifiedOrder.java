package com.cn.flypay.pageModel.payment;


/**
 * 微信支付订单
 * 
 * @author sunyue
 * 
 */
public class UnifiedOrder {
	public enum STATE {
		SUCCESS, REFUND, NOTPAY, CLOSED, REVOKED, USERPAYING, PAYERROR;
	}

	public enum PAY_TRADE_TYPE {
		JSAPI, NATIVE, APP
	}

	private String appid;
	private String mch_id;
	private String sub_appid;
	private String sub_mch_id;
	private String device_info;
	private String nonce_str;
	private String sign;
	private String body;
	private String attach;
	private String out_trade_no;
	private Integer total_fee;
	private String spbill_create_ip;
	private String time_start;
	private String time_expire;
	private String goods_tag;
	private String notify_url;
	private String trade_type;
	private String openid;
	private String product_id;
	private String return_code;
	private String return_msg;
	private String result_code;
	private String err_code;
	private String err_code_des;
	private String prepay_id;
	private String code_url;

	/*public UnifiedOrder() {

		this.appid = (String) ApplicatonStaticUtil.getAppStaticData("wxaccount.appid");
		this.mch_id = (String) ApplicatonStaticUtil.getAppStaticData("wxaccount.mchId");
		this.sub_mch_id = (String) ApplicatonStaticUtil.getAppStaticData("wxaccount.subMchId");
		this.spbill_create_ip = (String) ApplicatonStaticUtil.getAppStaticData("wxaccount.ip");
		this.notify_url = (String) ApplicatonStaticUtil.getAppStaticData("wxaccount.notifyUrl");
	}
*/
	public UnifiedOrder(String appid, String mch_id, String sub_mch_id, String spbill_create_ip, String notify_url) {

		this.appid = appid;
		this.mch_id = mch_id;
		this.sub_mch_id = sub_mch_id;
		this.spbill_create_ip = spbill_create_ip;
		this.notify_url = notify_url;
	}

	public String getAppid() {
		return this.appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMch_id() {
		return this.mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getSub_appid() {
		return sub_appid;
	}

	public void setSub_appid(String sub_appid) {
		this.sub_appid = sub_appid;
	}

	public String getSub_mch_id() {
		return sub_mch_id;
	}

	public void setSub_mch_id(String sub_mch_id) {
		this.sub_mch_id = sub_mch_id;
	}

	public String getDevice_info() {
		return this.device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}

	public String getNonce_str() {
		return this.nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getSign() {
		return this.sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAttach() {
		return this.attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getOut_trade_no() {
		return this.out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public Integer getTotal_fee() {
		return this.total_fee;
	}

	public void setTotal_fee(Integer total_fee) {
		this.total_fee = total_fee;
	}

	public String getSpbill_create_ip() {
		return this.spbill_create_ip;
	}

	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}

	public String getTime_start() {
		return this.time_start;
	}

	public void setTime_start(String time_start) {
		this.time_start = time_start;
	}

	public String getTime_expire() {
		return this.time_expire;
	}

	public void setTime_expire(String time_expire) {
		this.time_expire = time_expire;
	}

	public String getGoods_tag() {
		return this.goods_tag;
	}

	public void setGoods_tag(String goods_tag) {
		this.goods_tag = goods_tag;
	}

	public String getNotify_url() {
		return this.notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getTrade_type() {
		return this.trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public String getOpenid() {
		return this.openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getProduct_id() {
		return this.product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getReturn_code() {
		return this.return_code;
	}

	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}

	public String getReturn_msg() {
		return this.return_msg;
	}

	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}

	public String getResult_code() {
		return this.result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getErr_code() {
		return this.err_code;
	}

	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}

	public String getErr_code_des() {
		return this.err_code_des;
	}

	public void setErr_code_des(String err_code_des) {
		this.err_code_des = err_code_des;
	}

	public String getPrepay_id() {
		return this.prepay_id;
	}

	public void setPrepay_id(String prepay_id) {
		this.prepay_id = prepay_id;
	}

	public String getCode_url() {
		return this.code_url;
	}

	public void setCode_url(String code_url) {
		this.code_url = code_url;
	}
}
