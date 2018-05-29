package com.cn.flypay.pageModel.trans;

import java.math.BigDecimal;

import com.cn.flypay.pageModel.sys.User;

public class FinanceStatement implements java.io.Serializable {
	private static final long serialVersionUID = 352226L;
	/* 0出账 1入账 */
	private Integer isInput;
	private String statemtentDate;
	private String statemtentDateStart;
	private String statemtentDateEnd;
	private String stsType;
	/** 发送给通道的总金额 */
	private BigDecimal tradeAmt;
	private Long tradeNum;

	/**
	 * 通道方收取的总费用
	 */
	private BigDecimal feeAmt;
	/**
	 * 通道方收取的总的单笔手续费
	 */
	private BigDecimal tradeFee;
	/**
	 * 通道方收取的总的单笔操作费，例如民生D0提现单笔0.2元
	 */
	private BigDecimal operateFee;
	/**
	 * 通道方返佣
	 */
	private BigDecimal brokerageAmt;

	/**
	 * 实际收到的总金额= 发送给通道的总金额 -通道方收取的手续费
	 */
	private BigDecimal realInputAmt;

	private Integer status;
	/** 10普通 流量 20代理 */
	private Integer transPayType;
	private Long amtTradeNum;
	private Long agentTradeNum;
	/**
	 * 0立即下单，1 隔日下单
	 */
	private Integer payType;

	/**
	 * 用户前天剩余的钱
	 */
	private BigDecimal historyUserAmt;
	/**
	 * 用户昨天剩余的钱=昨天收入-昨天支出
	 */
	private BigDecimal yesterdayUserAmt;
	private BigDecimal yesterdayT1Amt;

	private User OperateUser;

	public Long getAmtTradeNum() {
		return amtTradeNum;
	}

	public void setAmtTradeNum(Long amtTradeNum) {
		this.amtTradeNum = amtTradeNum;
	}

	public BigDecimal getYesterdayT1Amt() {
		return yesterdayT1Amt;
	}

	public void setYesterdayT1Amt(BigDecimal yesterdayT1Amt) {
		this.yesterdayT1Amt = yesterdayT1Amt;
	}

	public Long getAgentTradeNum() {
		return agentTradeNum;
	}

	public BigDecimal getRealInputAmt() {
		if (realInputAmt == null) {
			return BigDecimal.ZERO;
		}
		return realInputAmt;
	}

	public User getOperateUser() {
		return OperateUser;
	}

	public void setOperateUser(User operateUser) {
		OperateUser = operateUser;
	}

	public void setRealInputAmt(BigDecimal realInputAmt) {
		this.realInputAmt = realInputAmt;
	}

	public BigDecimal getTradeFee() {
		if (tradeFee == null) {
			tradeFee = BigDecimal.ZERO;
		}
		return tradeFee;
	}

	public void setTradeFee(BigDecimal tradeFee) {
		this.tradeFee = tradeFee;
	}

	public BigDecimal getOperateFee() {
		if (operateFee == null) {
			operateFee = BigDecimal.ZERO;
		}
		return operateFee;
	}

	public void setOperateFee(BigDecimal operateFee) {
		this.operateFee = operateFee;
	}

	public BigDecimal getBrokerageAmt() {
		if (brokerageAmt == null) {
			brokerageAmt = BigDecimal.ZERO;
		}
		return brokerageAmt;
	}

	public void setBrokerageAmt(BigDecimal brokerageAmt) {
		this.brokerageAmt = brokerageAmt;
	}

	public void setAgentTradeNum(Long agentTradeNum) {
		this.agentTradeNum = agentTradeNum;
	}

	public FinanceStatement(Integer isInput, String statemtentDateStart, String statemtentDateEnd, String stsType) {
		this.isInput = isInput;
		this.statemtentDateStart = statemtentDateStart;
		this.statemtentDateEnd = statemtentDateEnd;
		this.stsType = stsType;
		this.status = 0;
		this.payType = 0;
	}

	public FinanceStatement() {
		// TODO Auto-generated constructor stub
	}

	public String getStatemtentDateStart() {
		return statemtentDateStart;
	}

	public void setStatemtentDateStart(String statemtentDateStart) {
		this.statemtentDateStart = statemtentDateStart;
	}

	public Integer getIsInput() {
		return isInput;
	}

	public void setIsInput(Integer isInput) {
		this.isInput = isInput;
	}

	public String getStatemtentDateEnd() {
		return statemtentDateEnd;
	}

	public Integer getTransPayType() {
		return transPayType;
	}

	public void setTransPayType(Integer transPayType) {
		this.transPayType = transPayType;
	}

	public void setStatemtentDateEnd(String statemtentDateEnd) {
		this.statemtentDateEnd = statemtentDateEnd;
	}

	public String getStsType() {
		return stsType;
	}

	public void setStsType(String stsType) {
		this.stsType = stsType;
	}

	public BigDecimal getTradeAmt() {
		if (tradeAmt == null) {
			tradeAmt = BigDecimal.ZERO;
		}
		return tradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	public BigDecimal getFeeAmt() {
		if (feeAmt == null) {
			feeAmt = BigDecimal.ZERO;
		}
		return feeAmt;
	}

	public BigDecimal getHistoryUserAmt() {
		return historyUserAmt;
	}

	public void setHistoryUserAmt(BigDecimal historyUserAmt) {
		this.historyUserAmt = historyUserAmt;
	}

	public BigDecimal getYesterdayUserAmt() {
		return yesterdayUserAmt;
	}

	public void setYesterdayUserAmt(BigDecimal yesterdayUserAmt) {
		this.yesterdayUserAmt = yesterdayUserAmt;
	}

	public void setFeeAmt(BigDecimal feeAmt) {

		this.feeAmt = feeAmt;
	}

	public Integer getStatus() {
		return status;
	}

	public Long getTradeNum() {
		return tradeNum;
	}

	public void setTradeNum(Long tradeNum) {
		this.tradeNum = tradeNum;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatemtentDate() {
		return statemtentDate;
	}

	public void setStatemtentDate(String statemtentDate) {
		this.statemtentDate = statemtentDate;
	}

}