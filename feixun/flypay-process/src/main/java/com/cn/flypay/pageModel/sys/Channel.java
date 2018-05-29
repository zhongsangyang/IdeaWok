package com.cn.flypay.pageModel.sys;

import java.math.BigDecimal;
import java.util.Date;

public class Channel implements java.io.Serializable {

	private static final long serialVersionUID = 1655L;
	private Long id;
	private Long version;
	/**
	 * 100：佣金_代理 110 佣金_流量 200：支付宝_二维码 210：支付宝_扫码 300、微信_二维码 310、微信_扫码 400、NFC
	 * 500、银联在线 600、卡头 700、提现_现金 710提现_佣金 800、转账
	 */
	private Integer type;
	/**
	 * 通道的总名称例如民生、微信、赞善赋
	 */
	private String name;
	/**
	 * 通道中账户名称描述例如民生-支付宝-涩琳
	 */
	private String detailName;
	/**
	 * 真实给到公司的费率
	 */
	private BigDecimal realRate;
	/**
	 * 软件向客户收取的费率
	 */
	private BigDecimal showRate;
	/**
	 * 真实给到公司的分润比例
	 */
	private BigDecimal shareRate;
	/**
	 * 0正常 1失效
	 */
	private Integer status;

	/**
	 * 通道单笔最大交易金额
	 */
	private BigDecimal maxTradeAmt;
	/**
	 * 通道单笔最小交易金额
	 */
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
	private BigDecimal commissionRate;
	/**
	 * 0 通用，1 小额，2 表示大额
	 * 
	 */
	private Integer limitType;

	private Long maxNumPerPersonPerDay;

	private Long userNum;

	private Integer channelType;

	private String channelAMT;

	private String countTodayAmt;
	private String tixianAmt;

	private Date createDate;
	
	private Long userId;
	
	private String merchantId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public Long getMaxNumPerPersonPerDay() {
		return maxNumPerPersonPerDay;
	}

	public void setMaxNumPerPersonPerDay(Long maxNumPerPersonPerDay) {
		this.maxNumPerPersonPerDay = maxNumPerPersonPerDay;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLimitType() {
		return limitType;
	}

	public void setLimitType(Integer limitType) {
		this.limitType = limitType;
	}

	public BigDecimal getRealRate() {
		return realRate;
	}

	public void setRealRate(BigDecimal realRate) {
		this.realRate = realRate;
	}

	public BigDecimal getShowRate() {
		return showRate;
	}

	public void setShowRate(BigDecimal showRate) {
		this.showRate = showRate;
	}

	public BigDecimal getShareRate() {
		return shareRate;
	}

	public void setShareRate(BigDecimal shareRate) {
		this.shareRate = shareRate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public BigDecimal getMaxTradeAmt() {
		return maxTradeAmt;
	}

	public void setMaxTradeAmt(BigDecimal maxTradeAmt) {
		this.maxTradeAmt = maxTradeAmt;
	}

	public BigDecimal getMinTradeAmt() {
		return minTradeAmt;
	}

	public void setMinTradeAmt(BigDecimal minTradeAmt) {
		this.minTradeAmt = minTradeAmt;
	}

	public BigDecimal getMaxChannelAmt() {
		return maxChannelAmt;
	}

	public void setMaxChannelAmt(BigDecimal maxChannelAmt) {
		this.maxChannelAmt = maxChannelAmt;
	}

	public BigDecimal getMinChannelAmt() {
		return minChannelAmt;
	}

	public void setMinChannelAmt(BigDecimal minChannelAmt) {
		this.minChannelAmt = minChannelAmt;
	}

	public BigDecimal getTodayAmt() {
		return todayAmt;
	}

	public void setTodayAmt(BigDecimal todayAmt) {
		this.todayAmt = todayAmt;
	}

	public BigDecimal getMaxAmtPerDay() {
		return maxAmtPerDay;
	}

	public void setMaxAmtPerDay(BigDecimal maxAmtPerDay) {
		this.maxAmtPerDay = maxAmtPerDay;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public String getDetailName() {
		return detailName;
	}

	public void setDetailName(String detailName) {
		this.detailName = detailName;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public BigDecimal getCommissionRate() {
		return commissionRate;
	}

	public void setCommissionRate(BigDecimal commissionRate) {
		this.commissionRate = commissionRate;
	}

	public Long getUserNum() {
		return userNum;
	}

	public void setUserNum(Long userNum) {
		this.userNum = userNum;
	}

	public Integer getChannelType() {
		return channelType;
	}

	public void setChannelType(Integer channelType) {
		this.channelType = channelType;
	}

	public String getChannelAMT() {
		return channelAMT;
	}

	public void setChannelAMT(String channelAMT) {
		this.channelAMT = channelAMT;
	}

	public String getCountTodayAmt() {
		return countTodayAmt;
	}

	public void setCountTodayAmt(String countTodayAmt) {
		this.countTodayAmt = countTodayAmt;
	}

	public String getTixianAmt() {
		return tixianAmt;
	}

	public void setTixianAmt(String tixianAmt) {
		this.tixianAmt = tixianAmt;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	
	

}