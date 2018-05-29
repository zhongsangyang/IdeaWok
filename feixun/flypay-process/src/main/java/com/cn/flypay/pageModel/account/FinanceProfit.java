package com.cn.flypay.pageModel.account;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 运营商收益
 * 
 * @author sunyue
 * 
 */
public class FinanceProfit implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 429747830640112L;
	private Long id;
	private Integer version;
	private String sysDate;
	/**
	 * 用户账户昨日余额
	 */
	private BigDecimal yesterdayUserAmt;
	/**
	 * 用户账户历史余额
	 */
	private BigDecimal historyUserAmt;
	/**
	 * 系统总收益
	 */
	private BigDecimal sysProfit;
	/**
	 * 银联在线收益
	 */
	private BigDecimal ylzxProfit;
	/**
	 * 微信收益
	 */
	private BigDecimal weixinProfit;
	/**
	 * 支付宝收益
	 */
	private BigDecimal zhifubaoProfit;
	/**
	 * 提现收益
	 */
	private BigDecimal tixianProfit;
	/**
	 * 分润流量收益
	 */
	private BigDecimal brokerageAmt;
	/**
	 * 分润代理收益
	 */
	private BigDecimal brokerageAgent;
	/**
	 * 分润收益 = 流量+代理
	 */
	private BigDecimal brokerageProfit;
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
	private Date createTime;
	private String statemtentDateStart;
	private String statemtentDateEnd;
	private BigDecimal yesterdayT1Amt;

	private BigDecimal agentFeeAmt;
	private BigDecimal tradeT0FeeAmt;
	private BigDecimal tradeT5FeeAmt;
	private BigDecimal tradeFeeAmt;
	private BigDecimal yesterdayLockAmt;

	/**
	 * T1提现数量
	 */
	private Long tixianT1Num;
	/**
	 * T0提现数量
	 */
	private Long tixianT0Num;
	/**
	 * 提现总金额
	 */
	private BigDecimal tixianTotalAmt;
	/**
	 * T0提现金额
	 */
	private BigDecimal tixianT0Amt;
	/**
	 * 总收入
	 */
	private BigDecimal totalInputAmt;
	/**
	 * 总支出
	 */
	private BigDecimal totalOutAmt;

	/**
	 * 误差金额
	 */
	private BigDecimal errorAmt;

	/**
	 * 代理名称
	 */
	private String organizationName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getStatemtentDateStart() {
		return statemtentDateStart;
	}

	public BigDecimal getAgentFeeAmt() {
		return agentFeeAmt;
	}

	public BigDecimal getYesterdayLockAmt() {
		return yesterdayLockAmt;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public void setYesterdayLockAmt(BigDecimal yesterdayLockAmt) {
		this.yesterdayLockAmt = yesterdayLockAmt;
	}

	public BigDecimal getTotalInputAmt() {
		return totalInputAmt;
	}

	public void setTotalInputAmt(BigDecimal totalInputAmt) {
		this.totalInputAmt = totalInputAmt;
	}

	public BigDecimal getTotalOutAmt() {
		return totalOutAmt;
	}

	public Integer getGlobToDiamondNum() {
		return globToDiamondNum;
	}

	public void setGlobToDiamondNum(Integer globToDiamondNum) {
		this.globToDiamondNum = globToDiamondNum;
	}

	public void setTotalOutAmt(BigDecimal totalOutAmt) {
		this.totalOutAmt = totalOutAmt;
	}

	public void setAgentFeeAmt(BigDecimal agentFeeAmt) {
		this.agentFeeAmt = agentFeeAmt;
	}

	public BigDecimal getTradeFeeAmt() {
		return tradeFeeAmt;
	}

	public BigDecimal getTradeT0FeeAmt() {
		return tradeT0FeeAmt;
	}

	public void setTradeT0FeeAmt(BigDecimal tradeT0FeeAmt) {
		this.tradeT0FeeAmt = tradeT0FeeAmt;
	}

	public BigDecimal getTradeT5FeeAmt() {
		return tradeT5FeeAmt;
	}

	public void setTradeT5FeeAmt(BigDecimal tradeT5FeeAmt) {
		this.tradeT5FeeAmt = tradeT5FeeAmt;
	}

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

	public void setTradeFeeAmt(BigDecimal tradeFeeAmt) {
		this.tradeFeeAmt = tradeFeeAmt;
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

	public BigDecimal getYesterdayT1Amt() {
		return yesterdayT1Amt;
	}

	public void setYesterdayT1Amt(BigDecimal yesterdayT1Amt) {
		this.yesterdayT1Amt = yesterdayT1Amt;
	}

	public String getSysDate() {
		return sysDate;
	}

	public void setSysDate(String sysDate) {
		this.sysDate = sysDate;
	}

	public BigDecimal getYesterdayUserAmt() {
		return yesterdayUserAmt;
	}

	public void setYesterdayUserAmt(BigDecimal yesterdayUserAmt) {
		this.yesterdayUserAmt = yesterdayUserAmt;
	}

	public BigDecimal getHistoryUserAmt() {
		return historyUserAmt;
	}

	public void setHistoryUserAmt(BigDecimal historyUserAmt) {
		this.historyUserAmt = historyUserAmt;
	}

	public BigDecimal getSysProfit() {
		return sysProfit;
	}

	public void setSysProfit(BigDecimal sysProfit) {
		this.sysProfit = sysProfit;
	}

	public BigDecimal getYlzxProfit() {
		return ylzxProfit;
	}

	public void setYlzxProfit(BigDecimal ylzxProfit) {
		this.ylzxProfit = ylzxProfit;
	}

	public BigDecimal getWeixinProfit() {
		return weixinProfit;
	}

	public void setWeixinProfit(BigDecimal weixinProfit) {
		this.weixinProfit = weixinProfit;
	}

	public BigDecimal getZhifubaoProfit() {
		return zhifubaoProfit;
	}

	public void setZhifubaoProfit(BigDecimal zhifubaoProfit) {
		this.zhifubaoProfit = zhifubaoProfit;
	}

	public BigDecimal getTixianProfit() {
		return tixianProfit;
	}

	public void setTixianProfit(BigDecimal tixianProfit) {
		this.tixianProfit = tixianProfit;
	}

	public BigDecimal getBrokerageAmt() {
		return brokerageAmt;
	}

	public void setBrokerageAmt(BigDecimal brokerageAmt) {
		this.brokerageAmt = brokerageAmt;
	}

	public BigDecimal getBrokerageAgent() {
		return brokerageAgent;
	}

	public void setBrokerageAgent(BigDecimal brokerageAgent) {
		this.brokerageAgent = brokerageAgent;
	}

	public BigDecimal getBrokerageProfit() {
		return brokerageProfit;
	}

	public void setBrokerageProfit(BigDecimal brokerageProfit) {
		this.brokerageProfit = brokerageProfit;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public BigDecimal getErrorAmt() {
		return errorAmt;
	}

	public void setErrorAmt(BigDecimal errorAmt) {
		this.errorAmt = errorAmt;
	}

	public BigDecimal getTixianTotalAmt() {
		return tixianTotalAmt;
	}

	public void setTixianTotalAmt(BigDecimal tixianTotalAmt) {
		this.tixianTotalAmt = tixianTotalAmt;
	}

	public BigDecimal getTixianT0Amt() {
		return tixianT0Amt;
	}

	public void setTixianT0Amt(BigDecimal tixianT0Amt) {
		this.tixianT0Amt = tixianT0Amt;
	}

}