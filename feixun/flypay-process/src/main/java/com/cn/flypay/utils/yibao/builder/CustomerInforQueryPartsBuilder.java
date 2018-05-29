package com.cn.flypay.utils.yibao.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;


/**
 * 易宝--子商户信息查询--请求组件类
 * @author liangchao
 *
 */
public class CustomerInforQueryPartsBuilder {

    private List<Part> parts = new ArrayList<Part>();

    public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }

    public CustomerInforQueryPartsBuilder setMainCustomerNumber(
            String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber",
                mainCustomerNumber == null ? "" : mainCustomerNumber, "UTF-8"));
        return this;
    }

    public CustomerInforQueryPartsBuilder setMobilePhone(String mobilePhone) {
        this.parts.add(new StringPart("mobilePhone", mobilePhone == null ? ""
                : mobilePhone, "UTF-8"));
        return this;
    }

    public CustomerInforQueryPartsBuilder setHmac(String hmac) {
        this.parts
                .add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
    }

    public CustomerInforQueryPartsBuilder setCustomerNumber(
            String customerNumber) {
        this.parts.add(new StringPart("customerNumber",
                customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }

    public CustomerInforQueryPartsBuilder setCustomerType(String customerType) {
        this.parts.add(new StringPart("customerType", customerType == null ? ""
                : customerType, "UTF-8"));
        return this;
    }
}

