package com.cn.flypay.model.payment.model.responseModel;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by zhoujifeng1 on 16/8/3.
 */
public class MerInfo {
    /**
     * 渠道代码
     */
    @JsonProperty(value = "ChlID")
    private String chlID;

    public String getChlID() {
        return chlID;
    }

    public void setChlID(String chlID) {
        this.chlID = chlID;
    }

    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
