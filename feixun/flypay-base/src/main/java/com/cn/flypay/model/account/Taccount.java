package com.cn.flypay.model.account;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.cn.flypay.model.sys.Tuser;

@Entity
@Table(name = "account")
public class Taccount implements java.io.Serializable {

	private static final long serialVersionUID = -4189330937580051585L;
	private Long id;
	private Long version;
	private Tuser user;
	private Integer type;
	private BigDecimal perMonthInAmt;
	private BigDecimal perMonthOutAmt;
	private BigDecimal lockOutAmt;
	private BigDecimal throughAmt;
	/**
	 * 历史佣金
	 */
	private BigDecimal historyAmt;
	/**
	 * 昨日佣金
	 */
	private BigDecimal yesterdayAmt;
	private BigDecimal avlAmt;
	/**
	 * 当日提现金额
	 */
	private BigDecimal todayOutAmt;

	private Integer status;
	
	private BigDecimal d1Amt;
	private BigDecimal d2Amt;
	
	
	private BigDecimal t11Amt;
	private BigDecimal t10Amt;
	private BigDecimal t9Amt;
	private BigDecimal t8Amt;
	private BigDecimal t7Amt;

	private BigDecimal t6Amt;
	private BigDecimal t5Amt;

	private BigDecimal t4Amt;

	private BigDecimal t3Amt;

	private BigDecimal t2Amt;

	private BigDecimal t1Amt;

	private String remark;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false, precision = 15, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	public Tuser getUser() {
		return this.user;
	}

	public void setUser(Tuser user) {
		this.user = user;
	}

	@Column(name = "Type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "PER_MONTH_IN_AMT", precision = 15, scale = 2)
	public BigDecimal getPerMonthInAmt() {
		return this.perMonthInAmt;
	}

	public void setPerMonthInAmt(BigDecimal perMonthInAmt) {
		this.perMonthInAmt = perMonthInAmt;
	}

	@Column(name = "PER_MONTH_OUT_AMT", precision = 15, scale = 2)
	public BigDecimal getPerMonthOutAmt() {
		return this.perMonthOutAmt;
	}

	public void setPerMonthOutAmt(BigDecimal perMonthOutAmt) {
		this.perMonthOutAmt = perMonthOutAmt;
	}

	@Column(name = "lock_out_amt", precision = 12, scale = 2)
	public BigDecimal getLockOutAmt() {
		return this.lockOutAmt;
	}

	public void setLockOutAmt(BigDecimal lockOutAmt) {
		this.lockOutAmt = lockOutAmt;
	}

	@Column(name = "AVL_AMT", precision = 12, scale = 2)
	public BigDecimal getAvlAmt() {
		return this.avlAmt;
	}
	
	@Column(name = "D1_AMT", precision = 12, scale = 2)
	public BigDecimal getD1Amt() {
		return d1Amt;
	}

	public void setD1Amt(BigDecimal d1Amt) {
		this.d1Amt = d1Amt;
	}

	@Column(name = "D2_AMT", precision = 12, scale = 2)
	public BigDecimal getD2Amt() {
		return d2Amt;
	}

	public void setD2Amt(BigDecimal d2Amt) {
		this.d2Amt = d2Amt;
	}

	@Column(name = "T11_AMT", precision = 12, scale = 2)
	public BigDecimal getT11Amt() {
		return t11Amt;
	}

	public void setT11Amt(BigDecimal t11Amt) {
		this.t11Amt = t11Amt;
	}
	@Column(name = "T10_AMT", precision = 12, scale = 2)
	public BigDecimal getT10Amt() {
		return t10Amt;
	}
	
	public void setT10Amt(BigDecimal t10Amt) {
		this.t10Amt = t10Amt;
	}

	public void setAvlAmt(BigDecimal avlAmt) {
		this.avlAmt = avlAmt;
	}
	
    @Column(name = "T9_AMT", precision = 12, scale = 2)
	public BigDecimal getT9Amt() {
		return t9Amt;
	}

	public void setT9Amt(BigDecimal t9Amt) {
		this.t9Amt = t9Amt;
	}
    
	@Column(name = "T8_AMT", precision = 12, scale = 2)
	public BigDecimal getT8Amt() {
		return t8Amt;
	}

	public void setT8Amt(BigDecimal t8Amt) {
		this.t8Amt = t8Amt;
	}
	
	@Column(name = "T7_AMT", precision = 12, scale = 2)
	public BigDecimal getT7Amt() {
		return t7Amt;
	}

	public void setT7Amt(BigDecimal t7Amt) {
		this.t7Amt = t7Amt;
	}

	@Column(name = "T6_AMT", precision = 12, scale = 2)
	public BigDecimal getT6Amt() {
		return t6Amt;
	}

	public void setT6Amt(BigDecimal t6Amt) {
		this.t6Amt = t6Amt;
	}

	@Column(name = "T5_AMT", precision = 12, scale = 2)
	public BigDecimal getT5Amt() {
		return t5Amt;
	}

	public void setT5Amt(BigDecimal t5Amt) {
		this.t5Amt = t5Amt;
	}

	@Column(name = "T4_AMT", precision = 12, scale = 2)
	public BigDecimal getT4Amt() {
		return t4Amt;
	}

	public void setT4Amt(BigDecimal t4Amt) {
		this.t4Amt = t4Amt;
	}

	@Column(name = "T3_AMT", precision = 12, scale = 2)
	public BigDecimal getT3Amt() {
		return t3Amt;
	}

	public void setT3Amt(BigDecimal t3Amt) {
		this.t3Amt = t3Amt;
	}

	@Column(name = "T2_AMT", precision = 12, scale = 2)
	public BigDecimal getT2Amt() {
		return t2Amt;
	}

	public void setT2Amt(BigDecimal t2Amt) {
		this.t2Amt = t2Amt;
	}

	@Column(name = "T1_AMT", precision = 12, scale = 2)
	public BigDecimal getT1Amt() {
		return t1Amt;
	}

	public void setT1Amt(BigDecimal t1Amt) {
		this.t1Amt = t1Amt;
	}

	@Column(name = "STATUS")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "history_amt", nullable = false, precision = 15, scale = 2)
	public BigDecimal getHistoryAmt() {
		return historyAmt;
	}

	public void setHistoryAmt(BigDecimal historyAmt) {
		this.historyAmt = historyAmt;
	}

	@Column(name = "yesterday_amt", nullable = false, precision = 15, scale = 2)
	public BigDecimal getYesterdayAmt() {
		return yesterdayAmt;
	}

	public void setYesterdayAmt(BigDecimal yesterdayAmt) {
		this.yesterdayAmt = yesterdayAmt;
	}

	@Column(name = "today_out_amt", nullable = false, precision = 15, scale = 2)
	public BigDecimal getTodayOutAmt() {
		return todayOutAmt;
	}

	public void setTodayOutAmt(BigDecimal todayOutAmt) {
		this.todayOutAmt = todayOutAmt;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "through_amt", precision = 12, scale = 2)
	public BigDecimal getThroughAmt() {
		return throughAmt;
	}

	public void setThroughAmt(BigDecimal throughAmt) {
		this.throughAmt = throughAmt;
	}
	
	

}