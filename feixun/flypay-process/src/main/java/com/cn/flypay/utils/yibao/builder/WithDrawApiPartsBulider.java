package com.cn.flypay.utils.yibao.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
/**
 * 易宝--结算接口--请求组件类
 * @author liangchao
 *
 */
public class WithDrawApiPartsBulider {

	
	
	private List<Part> parts = new ArrayList<Part>();
	
	public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }
	
	public WithDrawApiPartsBulider setMainCustomerNumber(String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber", mainCustomerNumber == null ? ""
                : mainCustomerNumber, "UTF-8"));
        return this;
    }
	
	public WithDrawApiPartsBulider setCustomerNumber(String customerNumber) {
        this.parts.add(new StringPart("customerNumber", customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }
	
	public WithDrawApiPartsBulider setExternalNo(String externalNo) {
        this.parts.add(new StringPart("externalNo", externalNo == null ? "" : externalNo, "UTF-8"));
        return this;
    }
	
	
	public WithDrawApiPartsBulider setTransferWay(String transferWay) {
        this.parts.add(new StringPart("transferWay", transferWay == null ? "" : transferWay, "UTF-8"));
        return this;
    }
	
	
	public WithDrawApiPartsBulider setAmount(String amount) {
        this.parts.add(new StringPart("amount", amount == null ? "" : amount, "UTF-8"));
        return this;
    }
	
	public WithDrawApiPartsBulider setCallBackUrl(String callBackUrl) {
        this.parts.add(new StringPart("callBackUrl", callBackUrl == null ? "" : callBackUrl, "UTF-8"));
        return this;
    }
	
	public WithDrawApiPartsBulider setHmac(String hmac) {
        this.parts
                .add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
	}
	
	
	
	
	
	
}
