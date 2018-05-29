package com.cn.flypay.model.payment.enums;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by zhoujifeng1 on 16/8/4.
 */
public enum ReturnCode {
    FAIL("F001","失败"),
    SUCCESS("0000","成功");
    private  String code;

    private String msg;

    ReturnCode(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getCode(){
        return this.code;
    }

    public String getMsg(){
        return this.msg;
    }

    public static ReturnCode getByCode(String code){
        for(ReturnCode status : ReturnCode.values()){
            if(status.getCode() .equals(code) ){
                return status;
            }
        }
        return null;
    }

    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
