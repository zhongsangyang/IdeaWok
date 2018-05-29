package com.cn.flypay.pageModel.payment;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by zhoujifeng1 on 16/8/1.
 */
public enum OrderTransType {
    AGENT(0,"代理费用"),
    ALIPAY(1,"支付宝"),
    WE_CHAT(2,"微信"),
    NFC(3,"NFC"),
    ONLINE_BANK(4,"银联在线"),
    CARD_TOP(5,"卡头"),
    FUND_OUT(6,"提现"),
    COMMISSION_FUND_OUT(7,"佣金提现");


    private int code;

    private String msg;

    OrderTransType(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode(){
        return this.code;
    }

    public String getMsg(){
        return this.msg;
    }
    public static OrderTransType get(int code){
        for(OrderTransType type : OrderTransType.values()){
            if(type.getCode() == code){
                return type;
            }
        }
        return null;
    }

	public static OrderTransType getByMsg(String code){
        for(OrderTransType type : OrderTransType.values()){
            if(type.getMsg() == code){
                return type;
            }
        }
        return null;
    }
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}