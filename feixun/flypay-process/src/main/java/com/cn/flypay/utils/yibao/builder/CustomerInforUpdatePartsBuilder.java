package com.cn.flypay.utils.yibao.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 * 易宝--子商户信息修改--请求组件类
 * @author liangchao
 *
 */
public class CustomerInforUpdatePartsBuilder {

    private List<Part> parts = new ArrayList<Part>();

    public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }

    public CustomerInforUpdatePartsBuilder setMainCustomerNumber(
            String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber",
                mainCustomerNumber == null ? "" : mainCustomerNumber, "UTF-8"));
        return this;
    }

    public CustomerInforUpdatePartsBuilder setCustomerNumber(
            String customerNumber) {
        this.parts.add(new StringPart("customerNumber",
                customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }

    public CustomerInforUpdatePartsBuilder setWhiteList(String whiteList) {
        this.parts.add(new StringPart("whiteList", whiteList == null ? ""
                : whiteList, "UTF-8"));
        return this;
    }

    public CustomerInforUpdatePartsBuilder setFreezeDays(String freezeDays) {
        this.parts.add(new StringPart("freezeDays", freezeDays == null ? ""
                : freezeDays, "UTF-8"));
        return this;
    }

    public CustomerInforUpdatePartsBuilder setHmac(String hmac) {
        this.parts
                .add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
    }

    public CustomerInforUpdatePartsBuilder setModifyType(String modifyType) {
        this.parts.add(new StringPart("modifyType", modifyType == null ? ""
                : modifyType, "UTF-8"));
        return this;
    }

    public CustomerInforUpdatePartsBuilder setBankCardNumber(
            String bankCardNumber) {
        this.parts.add(new StringPart("bankCardNumber",
                bankCardNumber == null ? "" : bankCardNumber, "UTF-8"));
        return this;
    }

    public CustomerInforUpdatePartsBuilder setBankName(String bankName) {
        this.parts.add(new StringPart("bankName", bankName == null ? ""
                : bankName, "UTF-8"));
        return this;
    }

    public CustomerInforUpdatePartsBuilder setRiskReserveDay(
            String riskReserveDay) {
        this.parts.add(new StringPart("riskReserveDay",
                riskReserveDay == null ? "" : riskReserveDay, "UTF-8"));
        return this;
    }

    public CustomerInforUpdatePartsBuilder setManualSettle(String manualSettle) {
        this.parts.add(new StringPart("manualSettle", manualSettle == null ? ""
                : manualSettle, "UTF-8"));
        return this;
    }

    public CustomerInforUpdatePartsBuilder setSplitter(String splitter) {
        this.parts.add(new StringPart("splitter", splitter == null ? ""
                : splitter, "UTF-8"));
        return this;
    }

    public CustomerInforUpdatePartsBuilder setSplitterProfitFee(
            String splitterProfitFee) {
        this.parts.add(new StringPart("splitterProfitFee",
                splitterProfitFee == null ? "" : splitterProfitFee, "UTF-8"));
        return this;
    }

    public CustomerInforUpdatePartsBuilder setBusiness(String business) {
        this.parts.add(new StringPart("business", business == null ? ""
                : business, "UTF-8"));
        return this;
    }
    
}