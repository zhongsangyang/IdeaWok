package com.cn.flypay.model.payment.enums;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by zhoujifeng1 on 16/8/1.
 */
public enum TradeStatus {
    INIT(300,"初始化订单,等待用户支付"),
    PAY_PROCESS(301,"支付中"),
    PAY_FAIL(200,"支付失败"),
    PAY_SUCCESS(100,"支付成功"),
    ;

    private int code;
    private String msg;

    TradeStatus(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode(){
        return this.code;
    }

    public String getMsg(){
        return this.msg;
    }

    public static TradeStatus get(int code){
        for(TradeStatus status : TradeStatus.values()){
            if(status.getCode()== code){
                return status;
            }
        }
        return null;
    }

    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
