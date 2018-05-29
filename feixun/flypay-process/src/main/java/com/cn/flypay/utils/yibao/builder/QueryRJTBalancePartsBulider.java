package com.cn.flypay.utils.yibao.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 * 易宝--垫资额度查询接口--请求组件类
 * @author liangchao
 *
 */
public class QueryRJTBalancePartsBulider {

    private List<Part> parts = new ArrayList<Part>(11);

    public Part[] generateParams() {
        return parts.toArray(new Part[parts.size()]);
    }

    public QueryRJTBalancePartsBulider setMainCustomerNumber(String mainCustomerNumber) {
        this.parts.add(new StringPart("mainCustomerNumber", mainCustomerNumber == null ? "" : mainCustomerNumber, "UTF-8"));
        return this;
    }

    public QueryRJTBalancePartsBulider setHmac(String hmac) {
        this.parts.add(new StringPart("hmac", hmac == null ? "" : hmac, "UTF-8"));
        return this;
    }
}