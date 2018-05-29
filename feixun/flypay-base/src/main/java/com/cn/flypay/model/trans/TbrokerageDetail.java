package com.cn.flypay.model.trans;

// default package

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cn.flypay.model.sys.Tuser;

@Entity
@Table(name = "TRANS_BROKERAGE_DETAIL")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TbrokerageDetail implements java.io.Serializable {

	private static final long serialVersionUID = 9126434083736782263L;
	private Long id;
	private Long version;
	private String phone;
	private String userCode;
	private Integer brokerageType;
	private Integer transType;
	private BigDecimal amt;
	private Date transDatetime;
	private BigDecimal brokerage;
	/**
	 * 受益者
	 */
	private Tuser brokerageUser;

	private Integer brokerageUserType;
	private Double brokerageUserRate;
	private String description;

	private Tbrokerage tbrokerage;
	private TorderBonusProcess bonusProcess;

	public TbrokerageDetail() {
		// TODO Auto-generated constructor stub
	}

	public TbrokerageDetail(Integer transType, BigDecimal transAmt, Date transDatetime, BigDecimal brokerage,
			Integer brokerageUserType, String description,Integer brokerageType) {
		this.transType = transType;
		this.amt = transAmt;
		this.transDatetime = transDatetime;
		this.brokerage = brokerage;
		this.brokerageUserType = brokerageUserType;
		this.description = description;
		this.brokerageType = brokerageType;
	}

	@Id
	@GenericGenerator(name = "generator", strategy = "increment")
	@GeneratedValue(generator = "generator")
	@Column(name = "ID", unique = true, nullable = false, precision = 15, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false, precision = 10, scale = 0)
	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Column(name = "PHONE", nullable = false, length = 256)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "brokerage_user_id")
	public Tuser getBrokerageUser() {
		return this.brokerageUser;
	}

	public void setBrokerageUser(Tuser brokerageUser) {
		this.brokerageUser = brokerageUser;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bonus_process_id")
	public TorderBonusProcess getBonusProcess() {
		return bonusProcess;
	}

	public void setBonusProcess(TorderBonusProcess bonusProcess) {
		this.bonusProcess = bonusProcess;
	}

	@Column(name = "user_code", length = 128)
	public String getUserCode() {
		return this.userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	@Column(name = "TRANS_TYPE", nullable = false, precision = 1, scale = 0)
	public Integer getTransType() {
		return this.transType;
	}

	public void setTransType(Integer transType) {
		this.transType = transType;
	}
	
	@Column(name = "BROKERAGE_TYPE", nullable = false, precision = 1, scale = 0)
	public Integer getBrokerageType() {
		return brokerageType;
	}

	public void setBrokerageType(Integer brokerageType) {
		this.brokerageType = brokerageType;
	}

	@Column(name = "AMT", nullable = false, precision = 15)
	public BigDecimal getAmt() {
		return this.amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	@Column(name = "brokerage_user_type", nullable = false)
	public Integer getBrokerageUserType() {
		return brokerageUserType;
	}

	public void setBrokerageUserType(Integer brokerageUserType) {
		this.brokerageUserType = brokerageUserType;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TRANS_DATETIME", nullable = false)
	public Date getTransDatetime() {
		return this.transDatetime;
	}

	public void setTransDatetime(Date transDatetime) {
		this.transDatetime = transDatetime;
	}

	@Column(name = "BROKERAGE", nullable = false, precision = 9, scale = 2)
	public BigDecimal getBrokerage() {
		return this.brokerage;
	}

	public void setBrokerage(BigDecimal brokerage) {
		this.brokerage = brokerage;
	}

	@Column(name = "DESCRIPTION", length = 2048)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BROKERAGE_ID")
	public Tbrokerage getTbrokerage() {
		return tbrokerage;
	}

	public void setTbrokerage(Tbrokerage tbrokerage) {
		this.tbrokerage = tbrokerage;
	}

	@Column(name = "BROKERAGE_USER_RATE", nullable = false, precision = 6, scale = 4)
	public Double getBrokerageUserRate() {
		return brokerageUserRate;
	}

	public void setBrokerageUserRate(Double brokerageUserRate) {
		this.brokerageUserRate = brokerageUserRate;
	}

}