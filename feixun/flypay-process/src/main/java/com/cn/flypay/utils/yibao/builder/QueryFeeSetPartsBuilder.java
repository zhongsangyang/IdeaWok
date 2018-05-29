package com.cn.flypay.utils.yibao.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 * 易宝--子商户费率查询接口--请求组件类
 * @author liangchao
 *
 */
public class QueryFeeSetPartsBuilder {

    private List<Part> parts = new ArrayList<Part>(11);

    public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }


    public QueryFeeSetPartsBuilder setCustomerNumber(String customerNumber) {
        this.parts.add(new StringPart("customerNumber", customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }

    public QueryFeeSetPartsBuilder setMainCustomerNumber(String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber", mainCustomerNumber == null ? ""
                : mainCustomerNumber, "UTF-8"));
        return this;
    }

    public QueryFeeSetPartsBuilder setProductType(String productType) {
        this.parts.add(new StringPart("productType", productType == null ? ""
                : productType, "UTF-8"));
        return this;
    }

    public QueryFeeSetPartsBuilder setRate(String rate) {
        this.parts.add(new StringPart("rate",
                rate == null ? "" : rate, "UTF-8"));
        return this;
    }

    public QueryFeeSetPartsBuilder setHmac(String hmac) {
        this.parts
                .add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
    }
}
