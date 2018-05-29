package com.cn.flypay.utils.yibao.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 * 易宝--子商户限额查询--请求组件类
 * @author liangchao
 *
 */
public class LimitAmountQueryPartsBuilder {

    private List<Part> parts = new ArrayList<Part>();

    public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }

    // [start] jun.lin 2015-03-20 这里是普通入参

    /**
     * @param mainCustomerNumber the mainCustomerNumber to set
     */
    public LimitAmountQueryPartsBuilder setTradeLimitConfigKey(
            String tradeLimitConfigKey) {
        this.parts
                .add(new StringPart("tradeLimitConfigKey",
                        tradeLimitConfigKey == null ? "" : tradeLimitConfigKey,
                        "UTF-8"));
        return this;
    }

    public LimitAmountQueryPartsBuilder setCustomernumber(String customerNumber) {
        this.parts.add(new StringPart("customerNumber",
                customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }

    public LimitAmountQueryPartsBuilder setMainCustomerNumber(
            String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber",
                mainCustomerNumber == null ? "" : mainCustomerNumber, "UTF-8"));
        return this;
    }

    public LimitAmountQueryPartsBuilder setBankCardType(String bankCardType) {
        this.parts.add(new StringPart("bankCardType", bankCardType == null ? ""
                : bankCardType, "UTF-8"));
        return this;
    }

    public LimitAmountQueryPartsBuilder setBankCardNo(String bankCardNo) {
        this.parts.add(new StringPart("bankCardNo", bankCardNo == null ? ""
                : bankCardNo, "UTF-8"));
        return this;
    }

    public LimitAmountQueryPartsBuilder setHmac(String hmac) {
        this.parts
                .add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
    }
}
