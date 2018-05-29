package com.cn.flypay.pageModel.account;

import java.math.BigDecimal;

/**
 * 运营商收益
 * 
 * @author sunyue
 * 
 */
public class AgentFinanceProfit extends OrgFinanceProfit implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7874586344453905390L;
	/**
	 * 分润流量收益
	 */
	private BigDecimal brokerageAmt = BigDecimal.ZERO;
	/**
	 * 分润代理收益
	 */
	private BigDecimal brokerageAgent = BigDecimal.ZERO;
	/**
	 * 分润收益 = 流量+代理
	 */
	private BigDecimal brokerageProfit = BigDecimal.ZERO;

	private BigDecimal tradeAmt = BigDecimal.ZERO;
	private BigDecimal tradeT0Amt = BigDecimal.ZERO;
	private BigDecimal tradeT1Amt = BigDecimal.ZERO;
	private BigDecimal tradeT5Amt = BigDecimal.ZERO;
	/**
	 * T1提现数量
	 */
	private Long tixianT1Num = 0l;
	/**
	 * T0提现数量
	 */
	private Long tixianT0Num = 0l;
	/**
	 * 提现总金额
	 */
	private BigDecimal tixianTotalAmt = BigDecimal.ZERO;
	/**
	 * T0提现金额
	 */
	private BigDecimal tixianT0Amt = BigDecimal.ZERO;

	/**
	 * 认证总笔数
	 */
	private Long authNum = 0l;
	/**
	 * 手动实名认证总笔数
	 */
	private Long manualAuthNum = 0l;
	/**
	 * 自动实名认证总笔数
	 */
	private Long automaticAuthNum = 0l;

	private BigDecimal wxAmt = BigDecimal.ZERO;
	private BigDecimal alAmt = BigDecimal.ZERO;
	private BigDecimal jdAmt = BigDecimal.ZERO;
	private BigDecimal ylzxAmt = BigDecimal.ZERO;

	public Long getTixianT1Num() {
		return tixianT1Num;
	}

	public void setTixianT1Num(Long tixianT1Num) {
		this.tixianT1Num = tixianT1Num;
	}

	public Long getTixianT0Num() {
		return tixianT0Num;
	}

	public void setTixianT0Num(Long tixianT0Num) {
		this.tixianT0Num = tixianT0Num;
	}

	public BigDecimal getTixianTotalAmt() {
		return tixianTotalAmt;
	}

	public void setTixianTotalAmt(BigDecimal tixianTotalAmt) {
		this.tixianTotalAmt = tixianTotalAmt;
	}

	@Override
	public BigDecimal getTixianT0Amt() {
		return tixianT0Amt;
	}

	@Override
	public BigDecimal getBrokerageAmt() {
		return brokerageAmt;
	}

	@Override
	public void setBrokerageAmt(BigDecimal brokerageAmt) {
		this.brokerageAmt = brokerageAmt;
	}

	@Override
	public BigDecimal getBrokerageAgent() {
		return brokerageAgent;
	}

	@Override
	public void setBrokerageAgent(BigDecimal brokerageAgent) {
		this.brokerageAgent = brokerageAgent;
	}

	@Override
	public BigDecimal getBrokerageProfit() {
		return brokerageProfit;
	}

	@Override
	public void setBrokerageProfit(BigDecimal brokerageProfit) {
		this.brokerageProfit = brokerageProfit;
	}

	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	public BigDecimal getTradeT0Amt() {
		return tradeT0Amt;
	}

	public void setTradeT0Amt(BigDecimal tradeT0Amt) {
		this.tradeT0Amt = tradeT0Amt;
	}

	public BigDecimal getTradeT5Amt() {
		return tradeT5Amt;
	}

	public void setTradeT5Amt(BigDecimal tradeT5Amt) {
		this.tradeT5Amt = tradeT5Amt;
	}

	@Override
	public void setTixianT0Amt(BigDecimal tixianT0Amt) {
		this.tixianT0Amt = tixianT0Amt;
	}

	public Long getAuthNum() {
		return authNum;
	}

	public void setAuthNum(Long authNum) {
		this.authNum = authNum;
	}

	public Long getManualAuthNum() {
		return manualAuthNum;
	}

	public void setManualAuthNum(Long manualAuthNum) {
		this.manualAuthNum = manualAuthNum;
	}

	public Long getAutomaticAuthNum() {
		return automaticAuthNum;
	}

	public void setAutomaticAuthNum(Long automaticAuthNum) {
		this.automaticAuthNum = automaticAuthNum;
	}

	public BigDecimal getWxAmt() {
		return wxAmt;
	}

	public void setWxAmt(BigDecimal wxAmt) {
		this.wxAmt = wxAmt;
	}

	public BigDecimal getAlAmt() {
		return alAmt;
	}

	public void setAlAmt(BigDecimal alAmt) {
		this.alAmt = alAmt;
	}

	public BigDecimal getJdAmt() {
		return jdAmt;
	}

	public void setJdAmt(BigDecimal jdAmt) {
		this.jdAmt = jdAmt;
	}

	public BigDecimal getYlzxAmt() {
		return ylzxAmt;
	}

	public void setYlzxAmt(BigDecimal ylzxAmt) {
		this.ylzxAmt = ylzxAmt;
	}

	public BigDecimal getTradeT1Amt() {
		return tradeT1Amt;
	}

	public void setTradeT1Amt(BigDecimal tradeT1Amt) {
		this.tradeT1Amt = tradeT1Amt;
	}
	

}