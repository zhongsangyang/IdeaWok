package com.cn.flypay.model.payment.request;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by zhoujifeng1 on 16/8/1.
 */
public class PaymentRequest {

	public PaymentRequest(String bankUrl, String appId, String channel) {
		this.bankUrl = bankUrl;
		this.appId = appId;
		this.channel = channel;
	}

	public PaymentRequest() {
		super();
	}

	/**
	 * 支付成功后,自动返回的地址
	 */
	@NotNull(message = "支付自动返回地址不能为空")
	@Valid
	private String bankUrl;
	/**
	 * 支付成功后用户点击返回的地址
	 */
	@NotNull(message = "支付成功后用户点击返回的地址不能为空")
	@Valid
	private String frontUrl;

	/**
	 * 支付金额
	 */
	@NotNull(message = "支付金额不能为空")
	@Valid
	private BigDecimal mPay;

	/**
	 * 应用的ID(mpay_config.php中)
	 */
	@NotNull(message = "应用的ID不能为空")
	@Valid
	private String appId;

	/**
	 * 支付通道(0,1,2等) 需要和商务咨询开通权限
	 */
	private String channel;

	/**
	 * 额外的信息(字符串,不超过128字节)
	 */
	@Size(max = 128, message = "额外的信息不能超过128位")
	private String extra;

	/**
	 * 银行卡号
	 */
	private String accNo;

	/**
	 * 商户订单ID
	 */
	private String orderId;

	/**
	 * 注意：channel=3时可用，默认指定使用交行编码，编码对照表下载地址api.yue-net.com/dzb.xlsx
	 */
	private String bankCode;

	/**
	 * bank_code 不为空则必填，默认指定使用交行卡类型，编码对照表下载地址：api.yue-net.com/dzb.xlsx
	 */
	private String payType;

	/**
	 * 银行卡号
	 */
	private String cardNo;
	/**
	 * 姓名
	 */
	private String owner;
	/**
	 * 手机
	 */
	private String phone;
	/**
	 * 证件号码
	 */
	private String certNo;

	public String getBankUrl() {
		return bankUrl;
	}

	public void setBankUrl(String bankUrl) {
		this.bankUrl = bankUrl;
	}

	public String getFrontUrl() {
		return frontUrl;
	}

	public void setFrontUrl(String frontUrl) {
		this.frontUrl = frontUrl;
	}

	public BigDecimal getmPay() {
		return mPay;
	}

	public void setmPay(BigDecimal mPay) {
		this.mPay = mPay;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
