package com.cn.flypay.utils.yibao.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
/**
 * 易宝--结算记录查询--请求组件类
 * @author liangchao
 *
 */
public class TransferQueryPartsBulider {

	private List<Part> parts = new ArrayList<Part>();
	
	public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }
	
	
	public TransferQueryPartsBulider setMainCustomerNumber(String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber", mainCustomerNumber == null ? ""
                : mainCustomerNumber, "UTF-8"));
        return this;
    }
	
	
	public TransferQueryPartsBulider setCustomerNumber(String customerNumber) {
        this.parts.add(new StringPart("customerNumber", customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }
	
	
	public TransferQueryPartsBulider setExternalNo(String externalNo) {
		this.parts.add(new StringPart("externalNo", externalNo == null ? "" : externalNo, "UTF-8"));
		return this;
	}
	
	public TransferQueryPartsBulider setSerialNo(String serialNo) {
		this.parts.add(new StringPart("serialNo", serialNo == null ? "" : serialNo, "UTF-8"));
		return this;
	}
	
	public TransferQueryPartsBulider setRequestDataSectionBegin(String requestDataSectionBegin) {
		this.parts.add(new StringPart("requestDateSectionBegin", requestDataSectionBegin == null ? "" : requestDataSectionBegin, "UTF-8"));
		return this;
	}
	
	
	public TransferQueryPartsBulider setrequestDataSectionEnd(String requestDataSectionEnd) {
		this.parts.add(new StringPart("requestDateSectionEnd", requestDataSectionEnd == null ? "" : requestDataSectionEnd, "UTF-8"));
		return this;
	}
	
	public TransferQueryPartsBulider setTransferStatus(String transferStatus) {
		this.parts.add(new StringPart("transferStatus", transferStatus == null ? "" : transferStatus, "UTF-8"));
		return this;
	}
	
	public TransferQueryPartsBulider setTransferWay(String transferWay) {
		this.parts.add(new StringPart("transferWay", transferWay == null ? "" : transferWay, "UTF-8"));
		return this;
	}
	
	public TransferQueryPartsBulider setpageNo(String pageNo) {
		this.parts.add(new StringPart("pageNo", pageNo == null ? "" : pageNo, "UTF-8"));
		return this;
	}
	
	public TransferQueryPartsBulider setHmac(String hmac) {
        this.parts.add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
	}
	
}
