package com.cn.flypay.model.payment.model.responseModel;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by zhoujifeng1 on 16/8/3.
 */
public class RespInfo {
    /**
     * 响应码
     */
    @JsonProperty(value = "RespCode")
    private String  respCode;

    /**
     *响应信息
     */

    @JsonProperty(value = "RespMsg")
    private String respMsg;

    /**
     * 原交易响应码
     */
    @JsonProperty(value = "OldRespCode")
    private String oldRespCode;

    /**
     * 原交易响应信息
     */
    @JsonProperty(value = "OldRespMsg")
    private String oldRespMsg;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
