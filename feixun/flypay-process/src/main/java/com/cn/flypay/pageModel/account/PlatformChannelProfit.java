package com.cn.flypay.pageModel.account;

import java.math.BigDecimal;

public class PlatformChannelProfit extends PlatformFinanceProfit {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4336594091113592880L;

	private String channelName;
	private String channelDetailName;
	/**
	 * 交易流水利润
	 */
	private BigDecimal tradeProfit;
	/**
	 * 用户交易手续费
	 */
	private BigDecimal userFee;
	/**
	 * 真实手续费
	 */
	private BigDecimal realFee;
	/**
	 * T0交易手续费
	 */
	private BigDecimal t0TradeFee;
	/**
	 * T1交易手续费
	 */
	private BigDecimal t5TradeFee;
	/**
	 * 代付交易手续费
	 */
	private BigDecimal agentTradeFee;
	/**
	 * 返佣
	 */
	private BigDecimal commissionTradeFee;

	public BigDecimal getUserFee() {
		if (userFee == null) {
			return BigDecimal.ZERO;
		}
		return userFee;
	}

	public void setUserFee(BigDecimal userFee) {

		this.userFee = userFee;
	}

	public BigDecimal getT0TradeFee() {
		if (t0TradeFee == null) {
			return BigDecimal.ZERO;
		}
		return t0TradeFee;
	}

	public BigDecimal getCommissionTradeFee() {
		if (commissionTradeFee == null) {
			return BigDecimal.ZERO;
		}
		return commissionTradeFee;
	}

	public void setCommissionTradeFee(BigDecimal commissionTradeFee) {
		this.commissionTradeFee = commissionTradeFee;
	}

	public BigDecimal getAgentTradeFee() {
		if (agentTradeFee == null) {
			return BigDecimal.ZERO;
		}
		return agentTradeFee;
	}

	public void setAgentTradeFee(BigDecimal agentTradeFee) {
		this.agentTradeFee = agentTradeFee;
	}

	public void setT0TradeFee(BigDecimal t0TradeFee) {
		this.t0TradeFee = t0TradeFee;
	}

	public BigDecimal getT5TradeFee() {
		if (t5TradeFee == null) {
			return BigDecimal.ZERO;
		}
		return t5TradeFee;
	}

	public void setT5TradeFee(BigDecimal t5TradeFee) {

		this.t5TradeFee = t5TradeFee;
	}

	public BigDecimal getRealFee() {
		if (realFee == null) {
			return BigDecimal.ZERO;
		}
		return realFee;
	}

	public void setRealFee(BigDecimal realFee) {
		this.realFee = realFee;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelDetailName() {
		return channelDetailName;
	}

	public void setChannelDetailName(String channelDetailName) {
		this.channelDetailName = channelDetailName;
	}

	@Override
	public BigDecimal getTradeProfit() {
		if (tradeProfit == null) {
			return BigDecimal.ZERO;
		}
		return tradeProfit;
	}

	@Override
	public void setTradeProfit(BigDecimal tradeProfit) {
		this.tradeProfit = tradeProfit;
	}

}
