package com.cn.flypay.model.payment.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 代付request 版本
 * Created by zhoujifeng1 on 16/8/3.
 */
public class VerInfo {
    /**接口版本号*/@JsonProperty(value = "VerInter")
    private String verInter = "1.0";

    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
