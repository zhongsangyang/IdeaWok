package com.cn.flypay.model.payment.request;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

import com.cn.flypay.model.payment.model.VerInfo;
import com.cn.flypay.model.payment.model.requestModel.MerInfo;
import com.cn.flypay.model.payment.model.requestModel.PayInfo;
import com.cn.flypay.model.payment.model.requestModel.TxnInfo;

/**
 * Created by zhoujifeng1 on 16/8/3.
 */
public class FundOutRequest {

	@JsonProperty(value = "VerInfo")
	private VerInfo verInfo = new VerInfo();
	/**
	 * j交易码
	 */
	@JsonProperty(value = "TxnNo")
	private String txnNo;

	@JsonProperty(value = "MerInfo")
	private MerInfo merInfo;

	@JsonProperty(value = "TxnInfo")
	private TxnInfo txnInfo;

	@JsonProperty(value = "PayInfo")
	private PayInfo payInfo;
	/**
	 * 签名
	 * 首先，对报文中出现签名域（signature）之外的所有数据元采用key=value的形式按照名称排序，然后以&作为连接符拼接成待签名串；其次
	 * ，在待签名串尾拼写&KEY=商户入网时银商提供的秘钥；对待签名串使用MD5算法做加密操作
	 */
	@NotNull
	@JsonProperty(value = "Signature")
	private String signature;

	public VerInfo getVerInfo() {
		return verInfo;
	}

	public void setVerInfo(VerInfo verInfo) {
		this.verInfo = verInfo;
	}

	public String getTxnNo() {
		return txnNo;
	}

	public void setTxnNo(String txnNo) {
		this.txnNo = txnNo;
	}

	public MerInfo getMerInfo() {
		return merInfo;
	}

	public void setMerInfo(MerInfo merInfo) {
		this.merInfo = merInfo;
	}

	public TxnInfo getTxnInfo() {
		return txnInfo;
	}

	public void setTxnInfo(TxnInfo txnInfo) {
		this.txnInfo = txnInfo;
	}

	public PayInfo getPayInfo() {
		return payInfo;
	}

	public void setPayInfo(PayInfo payInfo) {
		this.payInfo = payInfo;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
