package com.cn.flypay.utils.yibao.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 * 易宝--结算手续费查询--请求组件类
 * @author liangchao
 *
 */
public class LendTargetFeeQueryPartsBulider {
	private List<Part> parts = new ArrayList<Part>(11);

    public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }

    public LendTargetFeeQueryPartsBulider setMainCustomerNumber(String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber", mainCustomerNumber == null ? "" : mainCustomerNumber, "UTF-8"));
        return this;
    }

    public LendTargetFeeQueryPartsBulider setCustomerNumber(String customerNumber) {
        this.parts.add(new StringPart("customerNumber", customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }

    public LendTargetFeeQueryPartsBulider setHmac(String hmac) {
        this.parts.add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
    }

    public LendTargetFeeQueryPartsBulider setTransAmount(String transAmount) {
        this.parts.add(new StringPart("transAmount", transAmount == null ? "" : transAmount, "UTF-8"));
        return this;
    }
    public LendTargetFeeQueryPartsBulider setTransType(String transType) {
        this.parts.add(new StringPart("transType", transType == null ? "" : transType, "UTF-8"));
        return this;
    }
    
    
    
}
