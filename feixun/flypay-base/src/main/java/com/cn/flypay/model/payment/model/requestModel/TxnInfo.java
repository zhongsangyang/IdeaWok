package com.cn.flypay.model.payment.model.requestModel;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 代付request 交易信息
 * Created by zhoujifeng1 on 16/8/3.
 */
public class TxnInfo {

    /**
     * 订单号
     */
    @NotNull
    @JsonProperty(value = "OrderID")
    private String orderID;

    /**
     * 交易日期 YYYYMMDD
     */
    @NotNull
    @JsonProperty(value = "TxnDate")
    private String txnDate;

    /**
     * 交易时间 hhmmss
     */
    @NotNull
    @JsonProperty(value = "TxnTime")
    private String txnTime ;

    @JsonProperty(value = "OldOrderID")
    private String odOrderID ;

    @JsonProperty(value = "OldTxnDate")
    private String oldTxnDate ;

    @JsonProperty(value = "OldTxnTime")
    private String oldTxnTime ;



    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }

    public String getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(String txnTime) {
        this.txnTime = txnTime;
    }

    public String getOdOrderID() {
        return odOrderID;
    }

    public void setOdOrderID(String odOrderID) {
        this.odOrderID = odOrderID;
    }

    public String getOldTxnDate() {
        return oldTxnDate;
    }

    public void setOldTxnDate(String oldTxnDate) {
        this.oldTxnDate = oldTxnDate;
    }

    public String getOldTxnTime() {
        return oldTxnTime;
    }

    public void setOldTxnTime(String oldTxnTime) {
        this.oldTxnTime = oldTxnTime;
    }

    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
