package com.cn.flypay.utils.yibao.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 * 易宝--子商户设置限额接口--基础类
 * @author Administrator
 *
 */
public class LimitAmountSetPartsBuilder {

    private List<Part> parts = new ArrayList<Part>();

    public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }

    // [start] jun.lin 2015-03-20 这里是普通入参
    /**
     * @param mainCustomerNumber
     *            the mainCustomerNumber to set
     */
    public LimitAmountSetPartsBuilder setTradeLimitConfigKey(
            String tradeLimitConfigKey) {
        this.parts
                .add(new StringPart("tradeLimitConfigKey",
                        tradeLimitConfigKey == null ? "" : tradeLimitConfigKey,
                        "UTF-8"));
        return this;
    }

    public LimitAmountSetPartsBuilder setSingleAmount(String singleAmount) {
        this.parts.add(new StringPart("singleAmount", singleAmount == null ? ""
                : singleAmount, "UTF-8"));
        return this;
    }

    public LimitAmountSetPartsBuilder setDayAmount(String dayAmount) {
        this.parts.add(new StringPart("dayAmount", dayAmount == null ? ""
                : dayAmount, "UTF-8"));
        return this;
    }

    public LimitAmountSetPartsBuilder setMonthAmount(String monthAmount) {
        this.parts.add(new StringPart("monthAmount", monthAmount == null ? ""
                : monthAmount, "UTF-8"));
        return this;
    }

    public LimitAmountSetPartsBuilder setDayCount(String dayCount) {
        this.parts.add(new StringPart("dayCount", dayCount == null ? ""
                : dayCount, "UTF-8"));
        return this;
    }

    public LimitAmountSetPartsBuilder setMonthCount(String monthCount) {
        this.parts.add(new StringPart("monthCount", monthCount == null ? ""
                : monthCount, "UTF-8"));
        return this;
    }

    public LimitAmountSetPartsBuilder setCustomernumber(String customerNumber) {
        this.parts.add(new StringPart("customerNumber",
                customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }

    public LimitAmountSetPartsBuilder setMainCustomerNumber(
            String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber",
                mainCustomerNumber == null ? "" : mainCustomerNumber, "UTF-8"));
        return this;
    }

    public LimitAmountSetPartsBuilder setBankCardType(String bankCardType) {
        this.parts.add(new StringPart("bankCardType", bankCardType == null ? ""
                : bankCardType, "UTF-8"));
        return this;
    }

    public LimitAmountSetPartsBuilder setBankCardNo(String bankCardNo) {
        this.parts.add(new StringPart("bankCardNo", bankCardNo == null ? ""
                : bankCardNo, "UTF-8"));
        return this;
    }

    public LimitAmountSetPartsBuilder setHmac(String hmac) {
        this.parts
                .add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
    }
}

