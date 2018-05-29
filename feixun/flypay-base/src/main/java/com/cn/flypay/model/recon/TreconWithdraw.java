package com.cn.flypay.model.recon;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * description:
 * <p>
 * author: thundern
 */

@Entity
@Table(name = "sys_recon_withdraw")
public class TreconWithdraw implements Serializable {

    private static final long serialVersionUID = 9016419875268155065L;

    private Long id;
    //对账类型
    public String reconType;
    // 事件发生时间
    public Date reconDate;
    // 姓名
    public String realName;
    // 手机号
    public String loginName;
    // 发生金额
    public BigDecimal applyAmt;
    // 运营商
    public String oemName;
    // 手续费
    private BigDecimal fee;
    // 实到金额
    private BigDecimal actualAmt;
    // 提现类型
    private String withdrawType;
    // 平台成本
    private BigDecimal platformCost;
    // 平台收益
    private BigDecimal platformProfit;
    // 运营商成本
    private BigDecimal oemCost;
    // 运营商收益
    private BigDecimal oemProfit;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "recon_type", nullable = false)
    public String getReconType() {
        return reconType;
    }

    public void setReconType(String reconType) {
        this.reconType = reconType;
    }

    @Column(name = "recon_date", nullable = false)
    public Date getReconDate() {
        return reconDate;
    }

    public void setReconDate(Date reconDate) {
        this.reconDate = reconDate;
    }

    @Column(name = "real_name", nullable = false)
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Column(name = "login_name", nullable = false)
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Column(name = "apply_amt", precision = 12, scale = 2)
    public BigDecimal getApplyAmt() {
        return applyAmt;
    }

    public void setApplyAmt(BigDecimal applyAmt) {
        this.applyAmt = applyAmt;
    }

    @Column(name = "oem_name", precision = 12, scale = 2)
    public String getOemName() {
        return oemName;
    }

    public void setOemName(String oemName) {
        this.oemName = oemName;
    }

    @Column(name = "fee", precision = 12, scale = 2)
    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    @Column(name = "actual_amt", precision = 12, scale = 2)
    public BigDecimal getActualAmt() {
        return actualAmt;
    }

    public void setActualAmt(BigDecimal actualAmt) {
        this.actualAmt = actualAmt;
    }

    @Column(name = "withdraw_type", nullable = false)
    public String getWithdrawType() {
        return withdrawType;
    }

    public void setWithdrawType(String withdrawType) {
        this.withdrawType = withdrawType;
    }

    @Column(name = "platform_cost", precision = 12, scale = 2)
    public BigDecimal getPlatformCost() {
        return platformCost;
    }

    public void setPlatformCost(BigDecimal platformCost) {
        this.platformCost = platformCost;
    }

    @Column(name = "platform_profit", precision = 12, scale = 2)
    public BigDecimal getPlatformProfit() {
        return platformProfit;
    }

    public void setPlatformProfit(BigDecimal platformProfit) {
        this.platformProfit = platformProfit;
    }

    @Column(name = "oem_cost", precision = 12, scale = 2)
    public BigDecimal getOemCost() {
        return oemCost;
    }

    public void setOemCost(BigDecimal oemCost) {
        this.oemCost = oemCost;
    }

    @Column(name = "oem_profit", precision = 12, scale = 2)
    public BigDecimal getOemProfit() {
        return oemProfit;
    }

    public void setOemProfit(BigDecimal oemProfit) {
        this.oemProfit = oemProfit;
    }
}
