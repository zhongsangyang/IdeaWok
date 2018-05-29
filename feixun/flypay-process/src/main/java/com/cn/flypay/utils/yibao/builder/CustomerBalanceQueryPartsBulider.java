package com.cn.flypay.utils.yibao.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
/**
 * 易宝--可用余额查询--请求组件类
 * @author liangchao
 *
 */
public class CustomerBalanceQueryPartsBulider {
	private List<Part> parts = new ArrayList<Part>();
	
	public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }
	
	public CustomerBalanceQueryPartsBulider setMainCustomerNumber(String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber", mainCustomerNumber == null ? ""
                : mainCustomerNumber, "UTF-8"));
        return this;
    }
	
	
	public CustomerBalanceQueryPartsBulider setCustomerNumber(String customerNumber) {
        this.parts.add(new StringPart("customerNumber", customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }
	
	public CustomerBalanceQueryPartsBulider setBalanceType(String balanceType) {
		this.parts.add(new StringPart("balanceType", balanceType == null ? "" : balanceType, "UTF-8"));
		return this;
	}
	
	public CustomerBalanceQueryPartsBulider setHmac(String hmac) {
        this.parts.add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
	}
	
	
	
}
