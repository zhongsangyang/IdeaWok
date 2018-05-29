package com.cn.flypay.pageModel.account;

import java.math.BigDecimal;

/**
 * 平台收益
 * 
 * @author sunyue
 * 
 */
public class PlatformFinanceProfit implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 429747830640112L;

	/**
	 * 系统总收益
	 */
	private BigDecimal sysProfit;
	/**
	 * 流量的总收益
	 */
	private BigDecimal tradeProfit;
	/**
	 * 流量的总金额
	 */
	private BigDecimal tradeAmt;
	/**
	 * 提现手续费总收益
	 */
	private BigDecimal tixianProfit;
	/**
	 * 提现总笔数
	 */
	private Long tixianNum;
	/**
	 * 提现T0总笔数
	 */
	private Long t0Num;
	/**
	 * 提现T1总笔数
	 */
	private Long t1Num;

	/**
	 * T0代付总收益
	 */
	private BigDecimal t0Profit;
	/**
	 * 短信总收益
	 */
	private BigDecimal msgProfit;
	/**
	 * 短信总笔数
	 */
	private Long msgNum;

	/**
	 * 实名认证总收益
	 */
	private BigDecimal authProfit;
	/**
	 * 认证总笔数
	 */
	private Long authNum;
	/**
	 * 手动实名认证总笔数
	 */
	private Long manualAuthNum;
	/**
	 * 自动实名认证总笔数
	 */
	private Long automaticAuthNum;

	private String statemtentDateStart;
	private String statemtentDateEnd;
	/**
	 * 代理名称
	 */
	private String orgName;

	public BigDecimal getSysProfit() {
		if (sysProfit == null) {
			return BigDecimal.ZERO;
		}
		return sysProfit;
	}

	public void setSysProfit(BigDecimal sysProfit) {

		this.sysProfit = sysProfit;
	}

	public BigDecimal getTradeProfit() {
		if (tradeProfit == null) {
			return BigDecimal.ZERO;
		}
		return tradeProfit;
	}

	public void setTradeProfit(BigDecimal tradeProfit) {
		this.tradeProfit = tradeProfit;
	}

	public BigDecimal getTradeAmt() {
		if (tradeAmt == null) {
			return BigDecimal.ZERO;
		}
		return tradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	public BigDecimal getTixianProfit() {
		if (tixianProfit == null) {
			return BigDecimal.ZERO;
		}
		return tixianProfit;
	}

	public void setTixianProfit(BigDecimal tixianProfit) {
		this.tixianProfit = tixianProfit;
	}

	public Long getTixianNum() {
		if (tixianNum == null) {
			return 0l;
		}
		return tixianNum;
	}

	public void setTixianNum(Long tixianNum) {
		this.tixianNum = tixianNum;
	}

	public Long getT0Num() {
		if (t0Num == null) {
			return 0l;
		}
		return t0Num;
	}

	public void setT0Num(Long t0Num) {
		this.t0Num = t0Num;
	}

	public Long getT1Num() {
		if (t1Num == null) {
			return 0l;
		}
		return t1Num;
	}

	public void setT1Num(Long t1Num) {
		this.t1Num = t1Num;
	}

	public BigDecimal getT0Profit() {
		if (t0Profit == null) {
			return BigDecimal.ZERO;
		}
		return t0Profit;
	}

	public void setT0Profit(BigDecimal t0Profit) {
		this.t0Profit = t0Profit;
	}

	public BigDecimal getMsgProfit() {
		if (msgProfit == null) {
			return BigDecimal.ZERO;
		}
		return msgProfit;
	}

	public void setMsgProfit(BigDecimal msgProfit) {
		this.msgProfit = msgProfit;
	}

	public Long getMsgNum() {
		if (msgNum == null) {
			return 0l;
		}
		return msgNum;
	}

	public void setMsgNum(Long msgNum) {
		this.msgNum = msgNum;
	}

	public BigDecimal getAuthProfit() {
		if (authProfit == null) {
			return BigDecimal.ZERO;
		}
		return authProfit;
	}

	public void setAuthProfit(BigDecimal authProfit) {
		this.authProfit = authProfit;
	}

	public Long getAuthNum() {
		if (authNum == null) {
			return 0l;
		}
		return authNum;
	}

	public void setAuthNum(Long authNum) {
		this.authNum = authNum;
	}

	public Long getManualAuthNum() {
		if (manualAuthNum == null) {
			return 0l;
		}
		return manualAuthNum;
	}

	public void setManualAuthNum(Long manualAuthNum) {
		this.manualAuthNum = manualAuthNum;
	}

	public Long getAutomaticAuthNum() {
		if (automaticAuthNum == null) {
			return 0l;
		}
		return automaticAuthNum;
	}

	public void setAutomaticAuthNum(Long automaticAuthNum) {
		this.automaticAuthNum = automaticAuthNum;
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

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

}
