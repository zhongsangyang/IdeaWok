package com.cn.flypay.model.payment.response;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

import com.cn.flypay.model.payment.model.VerInfo;
import com.cn.flypay.model.payment.model.responseModel.MerInfo;
import com.cn.flypay.model.payment.model.responseModel.PayInfo;
import com.cn.flypay.model.payment.model.responseModel.RespInfo;
import com.cn.flypay.model.payment.model.responseModel.TxnInfo;

/**
 * Created by zhoujifeng1 on 16/8/2.
 */
public class FundOutResponse {
    @JsonProperty(value = "VerInfo")
    private VerInfo  verInfo;

    /**
     * 交易码
     */
    @JsonProperty(value = "TxnNo")
    private String txnNo;

    @JsonProperty(value = "MerInfo")
    private MerInfo merInfo;

    @JsonProperty(value = "TxnInfo")
    private TxnInfo txnInfo;

    @JsonProperty(value = "RespInfo")
    private RespInfo respInfo;

    @JsonProperty(value = "PayInfo")
    private PayInfo payInfo;

    @JsonProperty(value = "Signature")
    private String signature;

    public VerInfo getVerInfo() {
        return verInfo;
    }

    public void setVerInfo(VerInfo verInfo) {
        this.verInfo = verInfo;
    }

    public String getTxnNo() {
        return txnNo;
    }

    public void setTxnNo(String txnNo) {
        this.txnNo = txnNo;
    }

    public MerInfo getMerInfo() {
        return merInfo;
    }

    public void setMerInfo(MerInfo merInfo) {
        this.merInfo = merInfo;
    }

    public TxnInfo getTxnInfo() {
        return txnInfo;
    }

    public void setTxnInfo(TxnInfo txnInfo) {
        this.txnInfo = txnInfo;
    }

    public RespInfo getRespInfo() {
        return respInfo;
    }

    public void setRespInfo(RespInfo respInfo) {
        this.respInfo = respInfo;
    }

    public PayInfo getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(PayInfo payInfo) {
        this.payInfo = payInfo;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
