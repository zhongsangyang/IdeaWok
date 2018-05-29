package com.cn.flypay.model.payment.model.responseModel;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by zhoujifeng1 on 16/8/3.
 */
public class PayInfo {
    /**
     * 扣款账户
     */
    @JsonProperty(value = "AccNo")
    private String accNo;
    /**
     *扣款开户机构代码
     */
    @JsonProperty(value = "IssInsCode")
    private String issInsCode;
    /**
     * 交易金额
     */
    @JsonProperty(value = "TxnAmt")
    private BigDecimal txnAmt;

    /**
     *清算日期
     */

    @JsonProperty(value = "SettleDate")
    private String settleDate;

    /**
     * 保留域
     */
    @JsonProperty(value = "Reserved")
    private String reserved;

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getIssInsCode() {
        return issInsCode;
    }

    public void setIssInsCode(String issInsCode) {
        this.issInsCode = issInsCode;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
