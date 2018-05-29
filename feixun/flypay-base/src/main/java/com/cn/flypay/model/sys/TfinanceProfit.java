package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * SysFinanceProfit entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_finance_profit")
public class TfinanceProfit implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 4291317747830640112L;
	private Long id;
	private Integer version;
	private String sysDate;
	private BigDecimal yesterdayUserAmt;
	private BigDecimal historyUserAmt;
	private BigDecimal sysProfit;
	private BigDecimal ylzxProfit;
	private BigDecimal weixinProfit;
	private BigDecimal zhifubaoProfit;
	private BigDecimal tixianProfit;
	private BigDecimal brokerageAmt;
	private BigDecimal brokerageAgent;
	private BigDecimal brokerageProfit;
	private Integer globNum;
	private Integer diamondNum;
	private Date createTime;

	private String statemtentDateStart;
	private String statemtentDateEnd;
	private BigDecimal yesterdayT1Amt;
	private BigDecimal yesterdayLockAmt;

	private BigDecimal agentFeeAmt;
	private BigDecimal tradeFeeAmt;
	
	private Long tixianT1Num;
	private Long tixianT0Num;
	private BigDecimal totalInputAmt;
	private BigDecimal totalOutAmt;
	

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "version")
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "sys_date", length = 32)
	public String getSysDate() {
		return this.sysDate;
	}

	public void setSysDate(String sysDate) {
		this.sysDate = sysDate;
	}

	@Column(name = "yesterday_user_amt", precision = 15)
	public BigDecimal getYesterdayUserAmt() {
		return this.yesterdayUserAmt;
	}

	public void setYesterdayUserAmt(BigDecimal yesterdayUserAmt) {
		this.yesterdayUserAmt = yesterdayUserAmt;
	}
	@Column(name = "yesterday_Lock_Amt", precision = 15)
	public BigDecimal getYesterdayLockAmt() {
		return yesterdayLockAmt;
	}

	public void setYesterdayLockAmt(BigDecimal yesterdayLockAmt) {
		this.yesterdayLockAmt = yesterdayLockAmt;
	}

	@Column(name = "statemtent_Date_Start")
	public String getStatemtentDateStart() {
		return statemtentDateStart;
	}

	public void setStatemtentDateStart(String statemtentDateStart) {
		this.statemtentDateStart = statemtentDateStart;
	}

	@Column(name = "statemtent_Date_End")
	public String getStatemtentDateEnd() {
		return statemtentDateEnd;
	}

	public void setStatemtentDateEnd(String statemtentDateEnd) {
		this.statemtentDateEnd = statemtentDateEnd;
	}

	@Column(name = "yesterday_t1_amt", precision = 15)
	public BigDecimal getYesterdayT1Amt() {
		return yesterdayT1Amt;
	}

	public void setYesterdayT1Amt(BigDecimal yesterdayT1Amt) {
		this.yesterdayT1Amt = yesterdayT1Amt;
	}
	@Column(name = "agent_Fee_Amt", precision = 15)
	public BigDecimal getAgentFeeAmt() {
		return agentFeeAmt;
	}

	public void setAgentFeeAmt(BigDecimal agentFeeAmt) {
		this.agentFeeAmt = agentFeeAmt;
	}
	@Column(name = "trade_Fee_Amt", precision = 15)
	public BigDecimal getTradeFeeAmt() {
		return tradeFeeAmt;
	}

	public void setTradeFeeAmt(BigDecimal tradeFeeAmt) {
		this.tradeFeeAmt = tradeFeeAmt;
	}

	@Column(name = "history_user_amt", precision = 15)
	public BigDecimal getHistoryUserAmt() {
		return this.historyUserAmt;
	}

	public void setHistoryUserAmt(BigDecimal historyUserAmt) {
		this.historyUserAmt = historyUserAmt;
	}

	@Column(name = "sys_profit", precision = 15)
	public BigDecimal getSysProfit() {
		return this.sysProfit;
	}

	public void setSysProfit(BigDecimal sysProfit) {
		this.sysProfit = sysProfit;
	}

	@Column(name = "ylzx_profit", precision = 15)
	public BigDecimal getYlzxProfit() {
		return this.ylzxProfit;
	}

	public void setYlzxProfit(BigDecimal ylzxProfit) {
		this.ylzxProfit = ylzxProfit;
	}

	@Column(name = "weixin_profit", precision = 15)
	public BigDecimal getWeixinProfit() {
		return this.weixinProfit;
	}

	public void setWeixinProfit(BigDecimal weixinProfit) {
		this.weixinProfit = weixinProfit;
	}

	@Column(name = "zhifubao_profit", precision = 15)
	public BigDecimal getZhifubaoProfit() {
		return this.zhifubaoProfit;
	}

	public void setZhifubaoProfit(BigDecimal zhifubaoProfit) {
		this.zhifubaoProfit = zhifubaoProfit;
	}

	@Column(name = "tixian_profit", precision = 15)
	public BigDecimal getTixianProfit() {
		return this.tixianProfit;
	}

	public void setTixianProfit(BigDecimal tixianProfit) {
		this.tixianProfit = tixianProfit;
	}

	@Column(name = "brokerage_amt", precision = 15)
	public BigDecimal getBrokerageAmt() {
		return this.brokerageAmt;
	}

	public void setBrokerageAmt(BigDecimal brokerageAmt) {
		this.brokerageAmt = brokerageAmt;
	}

	@Column(name = "brokerage_agent", precision = 15)
	public BigDecimal getBrokerageAgent() {
		return this.brokerageAgent;
	}

	public void setBrokerageAgent(BigDecimal brokerageAgent) {
		this.brokerageAgent = brokerageAgent;
	}

	@Column(name = "brokerage_profit", precision = 15)
	public BigDecimal getBrokerageProfit() {
		return this.brokerageProfit;
	}

	public void setBrokerageProfit(BigDecimal brokerageProfit) {
		this.brokerageProfit = brokerageProfit;
	}
	@Column(name = "tixian_t1_Num")
	public Long getTixianT1Num() {
		return tixianT1Num;
	}

	public void setTixianT1Num(Long tixianT1Num) {
		this.tixianT1Num = tixianT1Num;
	}
	@Column(name = "tixian_t0_Num")
	public Long getTixianT0Num() {
		return tixianT0Num;
	}

	public void setTixianT0Num(Long tixianT0Num) {
		this.tixianT0Num = tixianT0Num;
	}

	@Column(name = "total_Input_Amt", precision = 15)
	public BigDecimal getTotalInputAmt() {
		return totalInputAmt;
	}

	public void setTotalInputAmt(BigDecimal totalInputAmt) {
		this.totalInputAmt = totalInputAmt;
	}
	@Column(name = "total_Out_Amt", precision = 15)
	public BigDecimal getTotalOutAmt() {
		return totalOutAmt;
	}

	public void setTotalOutAmt(BigDecimal totalOutAmt) {
		this.totalOutAmt = totalOutAmt;
	}

	@Column(name = "glob_num")
	public Integer getGlobNum() {
		return this.globNum;
	}

	public void setGlobNum(Integer globNum) {
		this.globNum = globNum;
	}

	@Column(name = "diamond_num")
	public Integer getDiamondNum() {
		return this.diamondNum;
	}

	public void setDiamondNum(Integer diamondNum) {
		this.diamondNum = diamondNum;
	}

	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}