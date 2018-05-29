package com.cn.flypay.pageModel.trans;

import java.math.BigDecimal;

public class FinanceStatementDetail implements java.io.Serializable {
	private static final long serialVersionUID = 352226L;

	/**
	 * 用户前天剩余的钱
	 */
	private BigDecimal historyUserAmt;
	/**
	 * 用户昨天剩余的钱=昨天收入-昨天支出
	 */
	private BigDecimal yesterdayUserAmt;
	private BigDecimal historySysAmt;
	private String statemtentDateStart;
	private String statemtentDateEnd;
	/** 发送给通道的总金额 */
	private BigDecimal tradeAmt;

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

	public BigDecimal getHistorySysAmt() {
		return historySysAmt;
	}

	public void setHistorySysAmt(BigDecimal historySysAmt) {
		this.historySysAmt = historySysAmt;
	}

	public String getStatemtentDateStart() {
		return statemtentDateStart;
	}

	public void setStatemtentDateStart(String statemtentDateStart) {
		this.statemtentDateStart = statemtentDateStart;
	}

	public String getStatemtentDateEnd() {
		return statemtentDateEnd;
	}

	public void setStatemtentDateEnd(String statemtentDateEnd) {
		this.statemtentDateEnd = statemtentDateEnd;
	}

	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

}