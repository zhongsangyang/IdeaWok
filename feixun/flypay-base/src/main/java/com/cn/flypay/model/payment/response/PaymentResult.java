package com.cn.flypay.model.payment.response;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.cn.flypay.model.payment.enums.PayStatus;
import com.cn.flypay.model.payment.enums.TradeStatus;

/**
 * Created by zhoujifeng1 on 16/8/1.
 */
public class PaymentResult {
    /**
     * 请求支付是否成功
     */
    private boolean isSuccess = false;

    /**
     * 支付凭证号
     */
    private String paymenrVoucherNo;

    /**
     * 交易状态
     */
    private TradeStatus tradeStatus;

    /**
     * 支付结果信息
     */
    private String msg;
    /**
     * 实际支付金额
     */
    private BigDecimal mActual;
    /**
     * 额外信息
     */
    private String extra;
    /**
     * 支付状态
     */
    private PayStatus sPay;
    /**
     * 商户订单号
     */
    private String appOrderId;

    public String getPaymenrVoucherNo() {
        return paymenrVoucherNo;
    }

    public void setPaymenrVoucherNo(String paymenrVoucherNo) {
        this.paymenrVoucherNo = paymenrVoucherNo;
    }

    public TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public BigDecimal getmActual() {
        return mActual;
    }

    public void setmActual(BigDecimal mActual) {
        this.mActual = mActual;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public PayStatus getsPay() {
        return sPay;
    }

    public void setsPay(PayStatus sPay) {
        this.sPay = sPay;
    }

    public String getAppOrderId() {
        return appOrderId;
    }

    public void setAppOrderId(String appOrderId) {
        this.appOrderId = appOrderId;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
