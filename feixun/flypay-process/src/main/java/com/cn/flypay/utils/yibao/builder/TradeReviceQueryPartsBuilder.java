package com.cn.flypay.utils.yibao.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
/**
 * 易宝--交易查询--请求组件类
 * @author liangchao
 *
 */
public class TradeReviceQueryPartsBuilder {
	private List<Part> parts = new ArrayList<Part>();
	
	public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }
	
	public TradeReviceQueryPartsBuilder setMainCustomerNumber(String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber", mainCustomerNumber == null ? ""
                : mainCustomerNumber, "UTF-8"));
        return this;
    }
	
	
	public TradeReviceQueryPartsBuilder setCustomerNumber(String customerNumber) {
        this.parts.add(new StringPart("customerNumber", customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }
	
	public TradeReviceQueryPartsBuilder setRequestId(String requestId) {
		this.parts.add(new StringPart("requestId", requestId == null ? "" : requestId, "UTF-8"));
		return this;
	}
	
	public TradeReviceQueryPartsBuilder setCreateTimeBegin(String createTimeBegin) {
		this.parts.add(new StringPart("createTimeBegin", createTimeBegin == null ? "" : createTimeBegin, "UTF-8"));
		return this;
	}
	
	public TradeReviceQueryPartsBuilder setCreateTimeEnd(String createTimeEnd) {
		this.parts.add(new StringPart("createTimeEnd", createTimeEnd == null ? "" : createTimeEnd, "UTF-8"));
		return this;
	}
	
	public TradeReviceQueryPartsBuilder setPayTimebegin(String payTimebegin) {
		this.parts.add(new StringPart("payTimebegin", payTimebegin == null ? "" : payTimebegin, "UTF-8"));
		return this;
	}
	
	public TradeReviceQueryPartsBuilder setPayTimeEnd(String payTimeEnd) {
		this.parts.add(new StringPart("payTimeEnd", payTimeEnd == null ? "" : payTimeEnd, "UTF-8"));
		return this;
	}
	
	public TradeReviceQueryPartsBuilder setLastUpdateTimeBegin(String lastUpdateTimeBegin) {
		this.parts.add(new StringPart("lastUpdateTimeBegin", lastUpdateTimeBegin == null ? "" : lastUpdateTimeBegin, "UTF-8"));
		return this;
	}
	
	public TradeReviceQueryPartsBuilder setLastUpdateTimeEnd(String lastUpdateTimeEnd) {
		this.parts.add(new StringPart("lastUpdateTimeEnd", lastUpdateTimeEnd == null ? "" : lastUpdateTimeEnd, "UTF-8"));
		return this;
	}
	
	public TradeReviceQueryPartsBuilder setStatus(String status) {
		this.parts.add(new StringPart("status", status == null ? "" : status, "UTF-8"));
		return this;
	}
	
	public TradeReviceQueryPartsBuilder setBusiType(String busiType) {
		this.parts.add(new StringPart("busiType", busiType == null ? "" : busiType, "UTF-8"));
		return this;
	}
	
	public TradeReviceQueryPartsBuilder setPageNo(String pageNo) {
		this.parts.add(new StringPart("pageNo", pageNo == null ? "" : pageNo, "UTF-8"));
		return this;
	}
	
	public TradeReviceQueryPartsBuilder setHmac(String hmac) {
        this.parts.add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
	}
	
	
}
