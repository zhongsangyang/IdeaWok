package com.cn.flypay.utils;

import org.springframework.beans.factory.InitializingBean;

/**
 * Created by zhoujifeng1 on 16/8/2.
 */
public class AppProperties implements InitializingBean {
    //支付url
    private String doPayUrl ;
    //支付查询Url
    private String doQueryUrl;
    //出款Url
    private String fundOutUrl;
    //验证银行信息Url
    private String validateUrl;

    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getDoPayUrl() {
        return doPayUrl;
    }

    public void setDoPayUrl(String doPayUrl) {
        this.doPayUrl = doPayUrl;
    }

    public String getDoQueryUrl() {
        return doQueryUrl;
    }

    public void setDoQueryUrl(String doQueryUrl) {
        this.doQueryUrl = doQueryUrl;
    }

    public String getFundOutUrl() {
        return fundOutUrl;
    }

    public void setFundOutUrl(String fundOutUrl) {
        this.fundOutUrl = fundOutUrl;
    }

    public String getValidateUrl() {
        return validateUrl;
    }

    public void setValidateUrl(String validateUrl) {
        this.validateUrl = validateUrl;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
