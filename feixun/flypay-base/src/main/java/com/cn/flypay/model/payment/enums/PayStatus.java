package com.cn.flypay.model.payment.enums;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by zhoujifeng1 on 16/8/2.
 */
public enum  PayStatus {
    UN_PAY(0,"未付款"),
    PAY(1,"已付款");


    private int code;

    private String msg;

    PayStatus(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode(){
        return this.code;
    }

    public String getMsg(){
        return this.msg;
    }

    public static PayStatus getByCode(int code){
        for(PayStatus status : PayStatus.values()){
            if(status.getCode() == code){
                return status;
            }
        }
        return null;
    }

    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
