package com.cn.flypay.utils.yibao.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 * 易宝--分润查询--请求组件类
 * @author liangchao
 *
 */
public class AccountTradeQueryPartsBuilder {
    private List<Part> parts = new ArrayList<Part>();

    public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }

    public AccountTradeQueryPartsBuilder setMainCustomerNumber(
            String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber",
                mainCustomerNumber == null ? "" : mainCustomerNumber, "UTF-8"));
        return this;
    }

    public AccountTradeQueryPartsBuilder setCustomerNumber(String customerNumber) {
        this.parts.add(new StringPart("customerNumber",
                customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }

    public AccountTradeQueryPartsBuilder setOrderNo(String orderNo) {
        this.parts.add(new StringPart("orderNo",
                orderNo == null ? "" : orderNo, "UTF-8"));
        return this;
    }

    public AccountTradeQueryPartsBuilder setBeginDate(String beginDate) {
        this.parts.add(new StringPart("beginDate", beginDate == null ? ""
                : beginDate, "UTF-8"));
        return this;
    }

    public AccountTradeQueryPartsBuilder setEndDate(String endDate) {
        this.parts.add(new StringPart("endDate",
                endDate == null ? "" : endDate, "UTF-8"));
        return this;
    }

    public AccountTradeQueryPartsBuilder setPageNo(String pageNo) {
        this.parts.add(new StringPart("pageNo", pageNo == null ? "" : pageNo,
                "UTF-8"));
        return this;
    }

    public AccountTradeQueryPartsBuilder setSplitterType(String splitterType) {
        this.parts.add(new StringPart("splitterType", splitterType == null ? ""
                : splitterType, "UTF-8"));
        return this;
    }

    public AccountTradeQueryPartsBuilder setHmac(String hmac) {
        this.parts
                .add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
    }
}

