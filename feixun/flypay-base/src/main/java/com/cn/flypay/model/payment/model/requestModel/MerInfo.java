package com.cn.flypay.model.payment.model.requestModel;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 代付renquest 商户信息
 * Created by zhoujifeng1 on 16/8/3.
 *
 */
public class MerInfo {
    /**M
     * 渠道代码
     * */
    @NotNull
    @JsonProperty(value = "ChlID")
    private String chlID;

    /**
     *渠道名称 M
     */
    @NotNull
    @JsonProperty(value = "ChlNanme")
    private String chlNanme;

    /**
     * 商户代码 O
     */
    @JsonProperty(value = "MerID")
    private String merID;
    /**
     * 商户名称
     */
    @JsonProperty(value = "MerName")
    private String merName;

    /**
     * 二级商户代码
     */
    @JsonProperty(value = "SubMerID")
    private String subMerID;
    /**
     * 二级商户名称
     */
    @JsonProperty(value = "SubMerName")
    private String subMerName;


    public String getChlID() {
        return chlID;
    }

    public void setChlID(String chlID) {
        this.chlID = chlID;
    }

    public String getChlNanme() {
        return chlNanme;
    }

    public void setChlNanme(String chlNanme) {
        this.chlNanme = chlNanme;
    }

    public String getMerID() {
        return merID;
    }

    public void setMerID(String merID) {
        this.merID = merID;
    }

    public String getMerName() {
        return merName;
    }

    public void setMerName(String merName) {
        this.merName = merName;
    }

    public String getSubMerID() {
        return subMerID;
    }

    public void setSubMerID(String subMerID) {
        this.subMerID = subMerID;
    }

    public String getSubMerName() {
        return subMerName;
    }

    public void setSubMerName(String subMerName) {
        this.subMerName = subMerName;
    }

    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

