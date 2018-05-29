package com.cn.flypay.model.payment.enums;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by zhoujifeng1 on 16/8/4.
 */
public enum TxnNoEnum {
    PAY001("PAY001","单笔结果查询"),
    PAY011("PAY011","批量结果查询"),
    PAY012("PAY012","批量结果文件获取"),
    PAY101("PAY101","单笔代扣"),

    PAY111("PAY111","批量代扣文件上传"),
    PAY201("PAY201","单笔代付"),
    PAY211("PAY211","批量代付文件上传"),
    PAY311("PAY311","批量文件审核"),

    PAY901("PAY901","备付金查询"),
    PAY902("PAY902","备付金充值");
    private String code;
    private String msg;

    TxnNoEnum(String code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getCode(){
        return this.code;
    }

    public String getMsg(){
        return this.msg;
    }

    public static TxnNoEnum get(String code){
        for(TxnNoEnum status : TxnNoEnum.values()){
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
