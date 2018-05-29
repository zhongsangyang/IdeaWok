package com.cn.flypay.model.payment.model.responseModel;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by zhoujifeng1 on 16/8/3.
 */
public class TxnInfo {
    /**
     * 订单号
     */
    @JsonProperty(value = "OrderID")
    private String orderID;

    /**
     * 交易日期
     */
    @JsonProperty(value = "TxnDate")
    private String txnDate;

    /**
     * 交易时间
     */
    @JsonProperty(value = "TxnTime")
    private String txnTime;

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

    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
