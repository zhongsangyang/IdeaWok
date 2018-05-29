package com.cn.flypay.model.payment.response;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by zhoujifeng1 on 16/8/3.
 */
public class PaymentResponse {
	/**
	 * 支付结果
	 */
	private PaymentResult result;
	/**
	 * 自动跳转html
	 */
	private String htmlStr;

	private String flag;

	public PaymentResult getResult() {
		return result;
	}

	public void setResult(PaymentResult result) {
		this.result = result;
	}

	public String getHtmlStr() {
		return htmlStr;
	}

	public void setHtmlStr(String htmlStr) {
		this.htmlStr = htmlStr;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
