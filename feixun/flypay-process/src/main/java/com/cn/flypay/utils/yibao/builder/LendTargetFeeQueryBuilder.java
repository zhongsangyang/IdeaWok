package com.cn.flypay.utils.yibao.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 * 易宝--易宝结算手续费查询--请求组件类
 * @author liangchao
 *
 */
public class LendTargetFeeQueryBuilder {

    private List<Part> parts = new ArrayList<Part>(11);

    public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }

    public LendTargetFeeQueryBuilder setMainCustomerNumber(String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber", mainCustomerNumber == null ? "" : mainCustomerNumber, "UTF-8"));
        return this;
    }

    public LendTargetFeeQueryBuilder setCustomerNumber(String customerNumber) {
        this.parts.add(new StringPart("customerNumber", customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }

    public LendTargetFeeQueryBuilder setHmac(String hmac) {
        this.parts.add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
    }

    public LendTargetFeeQueryBuilder setTransAmount(String transAmount) {
        this.parts.add(new StringPart("transAmount", transAmount == null ? "" : transAmount, "UTF-8"));
        return this;
    }
    public LendTargetFeeQueryBuilder setTransType(String transType) {
        this.parts.add(new StringPart("transType", transType == null ? "" : transType, "UTF-8"));
        return this;
    }

}