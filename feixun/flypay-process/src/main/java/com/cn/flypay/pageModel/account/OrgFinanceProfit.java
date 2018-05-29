package com.cn.flypay.pageModel.account;

import java.math.BigDecimal;

/**
 * 运营商收益
 * 
 * @author sunyue
 * 
 */
public class OrgFinanceProfit implements java.io.Serializable {

	private static final long serialVersionUID = 429740112L;

	private String statemtentDateStart;
	private String statemtentDateEnd;
	/**
	 * 系统总收益 = 交易手续费收益+ 提现收益+ 代理分润收益-T0代付成本-短信成本-实名认证成本
	 */
	private BigDecimal sysProfit = BigDecimal.ZERO;
	/**
	 * 交易手续费收益 =T0介于0.3-0.49 间利润 - T5的手续费-购买代理手续费
	 */
	private BigDecimal tradeFeeProfit = BigDecimal.ZERO;
	/**
	 * 金牌个数
	 */
	private Integer globNum;
	/**
	 * 钻石个数
	 */
	private Integer diamondNum;
	/**
	 * 金生钻个数
	 */
	private Integer globToDiamondNum;
	/**
	 * T0介于0.3-0.49 间利润
	 */
	private BigDecimal tradeT0FeeAmt = BigDecimal.ZERO;
	/**
	 * T5的手续费
	 */
	private BigDecimal tradeT5FeeAmt = BigDecimal.ZERO;
	/**
	 * D0的手续费
	 */
	private BigDecimal tradeD0FeeAmt = BigDecimal.ZERO;
	/**
	 * T1的手续费
	 */
	private BigDecimal tradeT1FeeAmt = BigDecimal.ZERO;
	/**
	 * 购买代理手续费
	 */
	private BigDecimal tradeAgentFeeAmt = BigDecimal.ZERO;

	/**
	 * 提现收益=T0提现手续费- 系统扣除T0 T1的成本
	 */
	private BigDecimal tixianProfit = BigDecimal.ZERO;
	/**
	 * T1提现手续费
	 */
	private BigDecimal tixianFee = BigDecimal.ZERO;
	/**
	 * T1提现手续费
	 */
	private BigDecimal tixianT1Profit = BigDecimal.ZERO;
	/**
	 * T0提现手续费
	 */
	private BigDecimal tixianT0Profit = BigDecimal.ZERO;

	/**
	 * 分润收益 = 流量+代理
	 */
	private BigDecimal brokerageProfit = BigDecimal.ZERO;
	/**
	 * 分润流量收益
	 */
	private BigDecimal brokerageAmt = BigDecimal.ZERO;
	/**
	 * 分润代理收益
	 */
	private BigDecimal brokerageAgent = BigDecimal.ZERO;

	/**
	 * T0代付成本=T0*0.0007
	 */
	private BigDecimal tixianAmtFee = BigDecimal.ZERO;
	/**
	 * T0代付总金额
	 */
	private BigDecimal tixianT0Amt = BigDecimal.ZERO;

	/**
	 * 短信成本
	 */
	private BigDecimal msgFee = BigDecimal.ZERO;

	/**
	 * 实名认证成本
	 */
	private BigDecimal authFee = BigDecimal.ZERO;
	/**
	 * 运营商名称
	 */
	private String organizationName;
	/**
	 * 运营商编号
	 */
	private String agentId;

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

	public BigDecimal getSysProfit() {
		if (sysProfit == null) {
			return BigDecimal.ZERO;
		}
		return sysProfit;
	}

	public void setSysProfit(BigDecimal sysProfit) {
		this.sysProfit = sysProfit;
	}

	public BigDecimal getTixianFee() {
		return tixianFee;
	}

	public void setTixianFee(BigDecimal tixianFee) {
		this.tixianFee = tixianFee;
	}

	public BigDecimal getTradeFeeProfit() {
		if (tradeFeeProfit == null) {
			return BigDecimal.ZERO;
		}
		return tradeFeeProfit;
	}

	public void setTradeFeeProfit(BigDecimal tradeFeeProfit) {
		this.tradeFeeProfit = tradeFeeProfit;
	}

	public BigDecimal getTradeT0FeeAmt() {
		if (tradeT0FeeAmt == null) {
			return BigDecimal.ZERO;
		}
		return tradeT0FeeAmt;
	}

	public Integer getGlobNum() {
		return globNum;
	}

	public void setGlobNum(Integer globNum) {
		this.globNum = globNum;
	}

	public Integer getDiamondNum() {
		return diamondNum;
	}

	public void setDiamondNum(Integer diamondNum) {
		this.diamondNum = diamondNum;
	}

	public Integer getGlobToDiamondNum() {
		return globToDiamondNum;
	}

	public void setGlobToDiamondNum(Integer globToDiamondNum) {
		this.globToDiamondNum = globToDiamondNum;
	}

	public void setTradeT0FeeAmt(BigDecimal tradeT0FeeAmt) {
		this.tradeT0FeeAmt = tradeT0FeeAmt;
	}

	public BigDecimal getTradeT5FeeAmt() {
		if (tradeT5FeeAmt == null) {
			return BigDecimal.ZERO;
		}
		return tradeT5FeeAmt;
	}

	public void setTradeT5FeeAmt(BigDecimal tradeT5FeeAmt) {
		this.tradeT5FeeAmt = tradeT5FeeAmt;
	}

	public BigDecimal getTradeAgentFeeAmt() {
		if (tradeAgentFeeAmt == null) {
			return BigDecimal.ZERO;
		}
		return tradeAgentFeeAmt;
	}

	public void setTradeAgentFeeAmt(BigDecimal tradeAgentFeeAmt) {
		this.tradeAgentFeeAmt = tradeAgentFeeAmt;
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

	public BigDecimal getTixianT1Profit() {
		if (tixianT1Profit == null) {
			return BigDecimal.ZERO;
		}
		return tixianT1Profit;
	}

	public void setTixianT1Profit(BigDecimal tixianT1Profit) {
		this.tixianT1Profit = tixianT1Profit;
	}

	public BigDecimal getTixianT0Profit() {
		if (tixianT0Profit == null) {
			return BigDecimal.ZERO;
		}
		return tixianT0Profit;
	}

	public void setTixianT0Profit(BigDecimal tixianT0Profit) {
		this.tixianT0Profit = tixianT0Profit;
	}

	public BigDecimal getBrokerageProfit() {
		if (brokerageProfit == null) {
			return BigDecimal.ZERO;
		}
		return brokerageProfit;
	}

	public void setBrokerageProfit(BigDecimal brokerageProfit) {
		this.brokerageProfit = brokerageProfit;
	}

	public BigDecimal getBrokerageAmt() {
		if (brokerageAmt == null) {
			return BigDecimal.ZERO;
		}
		return brokerageAmt;
	}

	public void setBrokerageAmt(BigDecimal brokerageAmt) {
		this.brokerageAmt = brokerageAmt;
	}

	public BigDecimal getBrokerageAgent() {
		if (brokerageAgent == null) {
			return BigDecimal.ZERO;
		}
		return brokerageAgent;
	}

	public void setBrokerageAgent(BigDecimal brokerageAgent) {
		this.brokerageAgent = brokerageAgent;
	}

	public BigDecimal getTixianAmtFee() {
		if (tixianAmtFee == null) {
			return BigDecimal.ZERO;
		}
		return tixianAmtFee;
	}

	public void setTixianAmtFee(BigDecimal tixianAmtFee) {
		this.tixianAmtFee = tixianAmtFee;
	}

	public BigDecimal getTixianT0Amt() {
		if (tixianT0Amt == null) {
			return BigDecimal.ZERO;
		}
		return tixianT0Amt;
	}

	public void setTixianT0Amt(BigDecimal tixianT0Amt) {
		this.tixianT0Amt = tixianT0Amt;
	}

	public BigDecimal getMsgFee() {
		if (msgFee == null) {
			return BigDecimal.ZERO;
		}
		return msgFee;
	}

	public void setMsgFee(BigDecimal msgFee) {
		this.msgFee = msgFee;
	}

	public BigDecimal getAuthFee() {
		if (authFee == null) {
			return BigDecimal.ZERO;
		}
		return authFee;
	}

	public void setAuthFee(BigDecimal authFee) {
		this.authFee = authFee;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public BigDecimal getTradeD0FeeAmt() {
		return tradeD0FeeAmt;
	}

	public void setTradeD0FeeAmt(BigDecimal tradeD0FeeAmt) {
		this.tradeD0FeeAmt = tradeD0FeeAmt;
	}

	public BigDecimal getTradeT1FeeAmt() {
		return tradeT1FeeAmt;
	}

	public void setTradeT1FeeAmt(BigDecimal tradeT1FeeAmt) {
		this.tradeT1FeeAmt = tradeT1FeeAmt;
	}
	
	
	

}