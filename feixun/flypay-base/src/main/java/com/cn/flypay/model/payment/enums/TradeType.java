package com.cn.flypay.model.payment.enums;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by zhoujifeng1 on 16/8/1.
 */
public enum TradeType {
    DEPOSIT("deposit","充值"),
    FUND_OUT("fundOut","提现"),
    TRANSFER("transfer","转账"),
    INSTANT_TRADE("instantTrade","即时到账"),
    ;

    private String code;
    private String msg;

    TradeType(String code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getCode(){
        return this.code;
    }

    public String getMsg(){
        return this.msg;
    }

    public static TradeType get(String code){
        for(TradeType status : TradeType.values()){
            if(status.getCode().toUpperCase().equals(code.toUpperCase())){
                return status;
            }
        }
        return null;
    }

    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
