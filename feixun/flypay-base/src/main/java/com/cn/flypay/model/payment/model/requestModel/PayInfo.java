package com.cn.flypay.model.payment.model.requestModel;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by zhoujifeng1 on 16/8/3.
 */
public class PayInfo {
    /**
     * 交易币种
     */
    @NotNull
    @JsonProperty(value = "CurrencyCode")
    private String currencyCode;

    /**
     * 入账账户类型
     */
    @NotNull
    @JsonProperty(value = "AccType")
    private String accType ;

    /**
     * 入账账户
     */
    @NotNull
    @JsonProperty(value = "AccNo")
    private String accNo;

    /**
     * 入账账户名称
     */
    @NotNull
    @JsonProperty(value = "AccName")
    private String accName ;
    /**
     * 入账开户机构代码
     */
    @NotNull
    @JsonProperty(value = "IssInsCode")
    private String issInsCode ="1" ;

    /**
     * 入账开户行市
     */
    @JsonProperty(value = "IssInsCity")
    private String issInsCity;

    /**
     * 入账开户行名称
     */
    @JsonProperty(value = "IssInsName")
    private String issInsName;

    /**
     * 入账证件类型
     */
    @JsonProperty(value = "CertifTp")
    private String certifTp;

    /**
     *入账证件号码
     */
    @JsonProperty(value = "CertifID")
    private String  certifID;

    /**
     * 入账手机号
     */
    @JsonProperty(value = "PhoneNo")
    private String phoneNo;

    /**
     * 交易金额
     */
    @NotNull
    @JsonProperty(value = "TxnAmt")
    private BigDecimal txnAmt;
    /**
     *摘要
     */
    @NotNull
    @JsonProperty(value = "Remark")
    private String remark ;
    /**
     * 保留域
     */
    @JsonProperty(value = "Reserved")
    private String reserved;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getIssInsCode() {
        return issInsCode;
    }

    public void setIssInsCode(String issInsCode) {
        this.issInsCode = issInsCode;
    }

    public String getIssInsCity() {
        return issInsCity;
    }

    public void setIssInsCity(String issInsCity) {
        this.issInsCity = issInsCity;
    }

    public String getIssInsName() {
        return issInsName;
    }

    public void setIssInsName(String issInsName) {
        this.issInsName = issInsName;
    }

    public String getCertifTp() {
        return certifTp;
    }

    public void setCertifTp(String certifTp) {
        this.certifTp = certifTp;
    }

    public String getCertifID() {
        return certifID;
    }

    public void setCertifID(String certifID) {
        this.certifID = certifID;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
