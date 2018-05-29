package com.cn.flypay.model.trans;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "trans_statement")
public class TransStatement implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 3522263444925457884L;
	private Long id;
	private Integer version;
	/** 对账时间 */
	private String statemtentDate;
	/** 对账类型 */
	private Integer stsType;
	/** 对账单交易数量 */
	private Long tradeNum;
	/** 对账单交易成功的数量 */
	private Long successNum;
	/** 对账单交易金额 */
	private BigDecimal tradeAmt;
	/** 对账单成功金额 */
	private BigDecimal successAmt;
	/** 对账单总费用 */
	private BigDecimal feeAmt;
	/** 对账单总费率 */
	private BigDecimal feeRate;
	/** 对账单状态 0正常 1异常 */
	private Integer status;
	/** 创建时间 */
	private Date createDate;
	/** 系统对账成功交易量 */
	private Integer stsSuccussNum;
	/** 系统对账失败交易量 */
	private Integer stsFailNum;
	/** 系统与对账单状态不一致，自动调整的数量 */
	private Integer stsAutoDealNum;
	/** 系统与对账单状态不一致,冻结的账户数量 */
	private Integer stsFreezeAccountNum;
	/** 系统与对账单状态不一致，冻结的账户佣金数量 */
	private Integer stsFreezeBrokerageNum;


	// Constructors

	/** default constructor */
	public TransStatement() {
	}

	/**
	 * 
	 * @param stsType
	 *            类型
	 * @param statemtentDate
	 *            对账日期
	 * @param tradeNum
	 *            对账单交易笔数
	 * @param successNum
	 *            对账单成功笔数
	 * @param tradeAmt
	 *            交易金额
	 * @param successAmt
	 *            对账单成功金额
	 * @param feeAmt
	 *            对账单的费用
	 * @param feeRate
	 *            对账单的费率
	 * @param stsSuccussNum
	 *            对账成功笔数
	 * @param stsAutoDealNum
	 *            对账自动处理笔数
	 * @param stsFailNum
	 *            对账失败的笔数
	 * @param stsFreezeAccountNum
	 *            冻结的账户数量
	 * @param stsFreezeBrokerageNum
	 *            冻结的用金账户数量
	 */
	public TransStatement(int stsType, String statemtentDate, Long tradeNum, Long successNum, BigDecimal tradeAmt,
			BigDecimal successAmt, BigDecimal feeAmt, BigDecimal feeRate, Integer stsSuccussNum,
			Integer stsAutoDealNum, Integer stsFailNum, Integer stsFreezeAccountNum, Integer stsFreezeBrokerageNum) {
		this.stsType = stsType;
		this.statemtentDate = statemtentDate;
		this.tradeNum = tradeNum;
		this.successNum = successNum;
		this.tradeAmt = tradeAmt;
		this.successAmt = successAmt;
		this.feeAmt = feeAmt;
		this.feeRate = feeRate;
		this.stsSuccussNum = stsSuccussNum;
		this.stsAutoDealNum = stsAutoDealNum;
		this.stsFailNum = stsFailNum;
		this.stsFreezeAccountNum = stsFreezeAccountNum;
		this.stsFreezeBrokerageNum = stsFreezeBrokerageNum;

		this.status = 0;
		this.createDate = new Date();
	}

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

	@Column(name = "sts_type")
	public Integer getStsType() {
		return stsType;
	}

	public void setStsType(Integer stsType) {
		this.stsType = stsType;
	}

	@Column(name = "statemtent_date", length = 32)
	public String getStatemtentDate() {
		return this.statemtentDate;
	}

	public void setStatemtentDate(String statemtentDate) {
		this.statemtentDate = statemtentDate;
	}

	@Column(name = "trade_num")
	public Long getTradeNum() {
		return this.tradeNum;
	}

	public void setTradeNum(Long tradeNum) {
		this.tradeNum = tradeNum;
	}

	@Column(name = "success_num")
	public Long getSuccessNum() {
		return this.successNum;
	}

	public void setSuccessNum(Long successNum) {
		this.successNum = successNum;
	}

	@Column(name = "trade_amt", precision = 15)
	public BigDecimal getTradeAmt() {
		return this.tradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	@Column(name = "success_amt", precision = 15)
	public BigDecimal getSuccessAmt() {
		return this.successAmt;
	}

	public void setSuccessAmt(BigDecimal successAmt) {
		this.successAmt = successAmt;
	}

	@Column(name = "fee_amt", precision = 15)
	public BigDecimal getFeeAmt() {
		return this.feeAmt;
	}

	public void setFeeAmt(BigDecimal feeAmt) {
		this.feeAmt = feeAmt;
	}

	@Column(name = "fee_rate", precision = 10, scale = 4)
	public BigDecimal getFeeRate() {
		return this.feeRate;
	}

	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "create_date", length = 19)
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Column(name = "sts_succuss_num")
	public Integer getStsSuccussNum() {
		return this.stsSuccussNum;
	}

	public void setStsSuccussNum(Integer stsSuccussNum) {
		this.stsSuccussNum = stsSuccussNum;
	}

	@Column(name = "sts_fail_num")
	public Integer getStsFailNum() {
		return this.stsFailNum;
	}

	public void setStsFailNum(Integer stsFailNum) {
		this.stsFailNum = stsFailNum;
	}

	@Column(name = "sts_freeze_account_num")
	public Integer getStsFreezeAccountNum() {
		return stsFreezeAccountNum;
	}

	public void setStsFreezeAccountNum(Integer stsFreezeAccountNum) {
		this.stsFreezeAccountNum = stsFreezeAccountNum;
	}

	@Column(name = "sts_freeze_brokerage_num")
	public Integer getStsFreezeBrokerageNum() {
		return stsFreezeBrokerageNum;
	}

	public void setStsFreezeBrokerageNum(Integer stsFreezeBrokerageNum) {
		this.stsFreezeBrokerageNum = stsFreezeBrokerageNum;
	}

	@Column(name = "sts_Auto_Deal_Num")
	public Integer getStsAutoDealNum() {
		return stsAutoDealNum;
	}

	public void setStsAutoDealNum(Integer stsAutoDealNum) {
		this.stsAutoDealNum = stsAutoDealNum;
	}
/*	@Column(name = "sys_Finish_Order_Num")
	public Integer getSysFinishOrderNum() {
		return sysFinishOrderNum;
	}

	public void setSysFinishOrderNum(Integer sysFinishOrderNum) {
		this.sysFinishOrderNum = sysFinishOrderNum;
	}
	@Column(name = "sys_Trade_Amt")
	public BigDecimal getSysTradeAmt() {
		return sysTradeAmt;
	}

	public void setSysTradeAmt(BigDecimal sysTradeAmt) {
		this.sysTradeAmt = sysTradeAmt;
	}
	@Column(name = "sys_Fee")
	public BigDecimal getSysFee() {
		return sysFee;
	}

	public void setSysFee(BigDecimal sysFee) {
		this.sysFee = sysFee;
	}
*/
}