package com.cn.flypay.model.sys;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

/**
 * SysChannel entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_channel")
public class Tchannel implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = -3721159061005952635L;
	private Long id;
	private Long version;
	private Integer type;
	/**
	 * 通道的总名称例如民生、微信、赞善赋
	 */
	private String name;
	/**
	 * 通道中账户名称描述例如民生-支付宝-涩琳
	 */
	private String detailName;
	private BigDecimal realRate;
	private BigDecimal showRate;
	private BigDecimal shareRate;
	/**
	 * 返佣比例
	 */
	private BigDecimal commissionRate;
	/**
	 * 0正常 1失效
	 */
	private Integer status;
	private BigDecimal maxTradeAmt;
	private BigDecimal minTradeAmt;

	/**
	 * 通道最大阈值
	 */
	private BigDecimal maxChannelAmt;
	/**
	 * 通道最小阈值
	 */
	private BigDecimal minChannelAmt;

	/**
	 * 通道今天流入量
	 */
	private BigDecimal todayAmt;
	/**
	 * 通道一天最大的流入量
	 */
	private BigDecimal maxAmtPerDay;
	/**
	 * 通道账户标示
	 */
	private String account;
	/**
	 * 账户基础配置信息,json形式存储
	 */
	private String config;

	private Integer seq;

	private Integer userType;

	private Integer limitType;

	private Long userNum = 0l;

	private Long userId;

	private Long payType;

	private Long maxNumPerPersonPerDay;

	private String merchantId;

	private String merchantName;

	private String shortName;

	private Date createDate;

	@Id
	@GenericGenerator(name = "generator", strategy = "increment")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "version")
	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "name", length = 64)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "real_rate", precision = 6, scale = 4)
	public BigDecimal getRealRate() {
		return realRate;
	}

	public void setRealRate(BigDecimal realRate) {
		this.realRate = realRate;
	}

	@Column(name = "show_rate", precision = 6, scale = 4)
	public BigDecimal getShowRate() {
		return showRate;
	}

	public void setShowRate(BigDecimal showRate) {
		this.showRate = showRate;
	}

	@Column(name = "share_rate", precision = 6, scale = 4)
	public BigDecimal getShareRate() {
		return shareRate;
	}

	public void setShareRate(BigDecimal shareRate) {
		this.shareRate = shareRate;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "max_trade_amt", precision = 16, scale = 2)
	public BigDecimal getMaxTradeAmt() {
		return maxTradeAmt;
	}

	public void setMaxTradeAmt(BigDecimal maxTradeAmt) {
		this.maxTradeAmt = maxTradeAmt;
	}

	@Column(name = "min_trade_amt", precision = 5, scale = 2)
	public BigDecimal getMinTradeAmt() {
		return minTradeAmt;
	}

	public void setMinTradeAmt(BigDecimal minTradeAmt) {
		this.minTradeAmt = minTradeAmt;
	}

	@Column(name = "max_channel_amt", precision = 15, scale = 2)
	public BigDecimal getMaxChannelAmt() {
		return maxChannelAmt;
	}

	public void setMaxChannelAmt(BigDecimal maxChannelAmt) {
		this.maxChannelAmt = maxChannelAmt;
	}

	@Column(name = "min_channel_amt", precision = 15, scale = 2)
	public BigDecimal getMinChannelAmt() {
		return minChannelAmt;
	}

	public void setMinChannelAmt(BigDecimal minChannelAmt) {
		this.minChannelAmt = minChannelAmt;
	}

	@Column(name = "today_Amt", precision = 15)
	public BigDecimal getTodayAmt() {
		return todayAmt;
	}

	public void setTodayAmt(BigDecimal todayAmt) {
		this.todayAmt = todayAmt;
	}

	@Column(name = "max_Amt_Per_Day", precision = 15)
	public BigDecimal getMaxAmtPerDay() {
		return maxAmtPerDay;
	}

	public void setMaxAmtPerDay(BigDecimal maxAmtPerDay) {
		this.maxAmtPerDay = maxAmtPerDay;
	}

	@Column(name = "account")
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Column(name = "config")
	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	@Column(name = "detail_name")
	public String getDetailName() {
		return detailName;
	}

	public void setDetailName(String detailName) {
		this.detailName = detailName;
	}

	@Column(name = "seq")
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "user_type")
	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	@Column(name = "limit_type")
	public Integer getLimitType() {
		return limitType;
	}

	public void setLimitType(Integer limitType) {
		this.limitType = limitType;
	}

	@Column(name = "commission_rate")
	public BigDecimal getCommissionRate() {
		return commissionRate;
	}

	public void setCommissionRate(BigDecimal commissionRate) {
		this.commissionRate = commissionRate;
	}

	@Column(name = "max_num_per_person_per_day")
	public Long getMaxNumPerPersonPerDay() {
		return maxNumPerPersonPerDay;
	}

	public void setMaxNumPerPersonPerDay(Long maxNumPerPersonPerDay) {
		this.maxNumPerPersonPerDay = maxNumPerPersonPerDay;
	}

	@Column(name = "user_num")
	public Long getUserNum() {
		return userNum;
	}

	public void setUserNum(Long userNum) {
		this.userNum = userNum;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "merchantId")
	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	@Column(name = "pay_Type")
	public Long getPayType() {
		return payType;
	}

	public void setPayType(Long payType) {
		this.payType = payType;
	}

	@Column(name = "merchantName")
	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	@Column(name = "shortName")
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Column(name = "createDate")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}