package com.cn.flypay.utils.yibao.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 * 易宝--子商户审核--请求组件类
 * @author liangchao
 *
 */
public class AuditMerchantPartsBuilder {

    private List<Part> parts = new ArrayList<Part>(11);

    public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }

    public AuditMerchantPartsBuilder setStatus(String status) {
        this.parts.add(new StringPart("status", status == null ? "" : status,
                "UTF-8"));
        return this;
    }

    public AuditMerchantPartsBuilder setMainCustomerNumber(
            String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber",
                mainCustomerNumber == null ? "" : mainCustomerNumber, "UTF-8"));
        return this;
    }

    public AuditMerchantPartsBuilder setCustomerNumber(String customerNumber) {
        this.parts.add(new StringPart("customerNumber",
                customerNumber == null ? "" : customerNumber, "UTF-8"));
        return this;
    }

    public AuditMerchantPartsBuilder setHmac(String hmac) {
        this.parts
                .add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
    }

    public AuditMerchantPartsBuilder setReason(String reason) {
        this.parts.add(new StringPart("reason", reason == null ? "" : reason,
                "UTF-8"));
        return this;
    }
    
}