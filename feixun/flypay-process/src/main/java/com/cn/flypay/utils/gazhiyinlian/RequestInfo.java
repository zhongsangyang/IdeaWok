package com.cn.flypay.utils.gazhiyinlian;
/**
 * 嘎吱（银联）通道基础类
 * @author liangchao
 *
 */
public class RequestInfo {

	private String orgCode;
	
	private String sign;
	
	private String body;
	
	public RequestInfo() {
		super();
	}

	public RequestInfo(String orgCode, String sign, String body) {
		super();
		this.orgCode = orgCode;
		this.sign = sign;
		this.body = body;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "RequestInfo [orgCode=" + orgCode + ", sign=" + sign + ", body=" + body + "]";
	}
	
	
}
