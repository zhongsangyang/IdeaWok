package com.cn.flypay.pageModel.payment.zanshanfu;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

public class ZanshanfuGatewayRequest implements java.io.Serializable {

	private static final long serialVersionUID = 5535709358964707423L;
	@JsonProperty(value = "action")
	String action;
	@JsonProperty(value = "txnamt")
	String txnamt;
	@JsonProperty(value = "merid")
	String merid;
	@JsonProperty(value = "orderid")
	String orderid;
	@JsonProperty(value = "backurl")
	String backurl;
	@JsonProperty(value = "fronturl")
	String fronturl;
	@JsonProperty(value = "accname")
	String accname;
	@JsonProperty(value = "accno")
	String accno;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTxnamt() {
		return txnamt;
	}

	public void setTxnamt(String txnamt) {
		this.txnamt = txnamt;
	}

	public String getMerid() {
		return merid;
	}

	public void setMerid(String merid) {
		this.merid = merid;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getBackurl() {
		return backurl;
	}

	public void setBackurl(String backurl) {
		this.backurl = backurl;
	}

	public String getFronturl() {
		return fronturl;
	}

	public void setFronturl(String fronturl) {
		this.fronturl = fronturl;
	}

	public String getAccname() {
		return accname;
	}

	public void setAccname(String accname) {
		this.accname = accname;
	}

	public String getAccno() {
		return accno;
	}

	public void setAccno(String accno) {
		this.accno = accno;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}


}
