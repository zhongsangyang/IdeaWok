package com.cn.flypay.pageModel.trans;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 通道编辑操作记录表
 * @author liangchao
 *
 */
public class SysChannelOperationHistory implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 351806240734619553L;

	private Long id;
	
	/**
	 * 行为释放者ID 对应 sys_user - id
	 */
	private Long creatorId;
	/**
	 * 行为释放者手机号
	 */
	private String creatorPhone;
	
	/**
	 * 行为释放者 姓名
	 */
	private String creatorName;
	
	/**
	 * 记录的所属类型
	 */
	private Integer recordType;
	
	/**
	 * 行为类型
	 */
	private Integer behaviorType;
	/**
	 * 被操作的通道D 
	 */
	private Long targetId;
	
	/**
	 * 备注信息
	 */
	private String details;
	
	/**
	 * 行为产生时间
	 */
	private Date createTime;
	/**
	 * 查询开始时间
	 */
	private String searchDateStart;
	/**
	 * 查询结束时间
	 */
	private String searchDateEnd;
	
	
	//--------------通道快照---------
	//通道的总名称例如民生、微信、赞善赋
	private String name;
	//详细名称
	private String detailName;
	//渠道账户
	private String account;
	//支付类型
	private Integer type;
	//用户类型
	private Integer userType;
	//大小额设置
	private Integer limitType;
	//真实费率
	private BigDecimal realRate;
	//展示用户费率
	private BigDecimal showRate;
	//返佣费率
	private BigDecimal shareRate;
	//通道返佣比例
	private BigDecimal commissionRate;
	//单笔最低限额
	private BigDecimal minTradeAmt;
	//单笔最高限额
	private BigDecimal maxTradeAmt;
	//通道最低限额
	private BigDecimal minChannelAmt;
	//通道最高限额
	private BigDecimal maxChannelAmt;
	//每日最高值
	private BigDecimal maxAmtPerDay;
	//每日每人使用次数
	private Long maxNumPerPersonPerDay;
	//今日累计	
	private BigDecimal todayAmt;
	//序号
	private Integer seq;
	//是否启用
	private Integer status;
	//配置(账户基础配置信息,json形式存储)
	private String config;
	
	
	public SysChannelOperationHistory (){}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorPhone() {
		return creatorPhone;
	}

	public void setCreatorPhone(String creatorPhone) {
		this.creatorPhone = creatorPhone;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Integer getRecordType() {
		return recordType;
	}

	public void setRecordType(Integer recordType) {
		this.recordType = recordType;
	}

	public Integer getBehaviorType() {
		return behaviorType;
	}

	public void setBehaviorType(Integer behaviorType) {
		this.behaviorType = behaviorType;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getSearchDateStart() {
		return searchDateStart;
	}

	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}

	public String getSearchDateEnd() {
		return searchDateEnd;
	}

	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetailName() {
		return detailName;
	}

	public void setDetailName(String detailName) {
		this.detailName = detailName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
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

	public BigDecimal getCommissionRate() {
		return commissionRate;
	}

	public void setCommissionRate(BigDecimal commissionRate) {
		this.commissionRate = commissionRate;
	}

	public BigDecimal getMinTradeAmt() {
		return minTradeAmt;
	}

	public void setMinTradeAmt(BigDecimal minTradeAmt) {
		this.minTradeAmt = minTradeAmt;
	}

	public BigDecimal getMaxTradeAmt() {
		return maxTradeAmt;
	}

	public void setMaxTradeAmt(BigDecimal maxTradeAmt) {
		this.maxTradeAmt = maxTradeAmt;
	}

	public BigDecimal getMinChannelAmt() {
		return minChannelAmt;
	}

	public void setMinChannelAmt(BigDecimal minChannelAmt) {
		this.minChannelAmt = minChannelAmt;
	}

	public BigDecimal getMaxChannelAmt() {
		return maxChannelAmt;
	}

	public void setMaxChannelAmt(BigDecimal maxChannelAmt) {
		this.maxChannelAmt = maxChannelAmt;
	}

	public BigDecimal getMaxAmtPerDay() {
		return maxAmtPerDay;
	}

	public void setMaxAmtPerDay(BigDecimal maxAmtPerDay) {
		this.maxAmtPerDay = maxAmtPerDay;
	}

	public Long getMaxNumPerPersonPerDay() {
		return maxNumPerPersonPerDay;
	}

	public void setMaxNumPerPersonPerDay(Long maxNumPerPersonPerDay) {
		this.maxNumPerPersonPerDay = maxNumPerPersonPerDay;
	}

	public BigDecimal getTodayAmt() {
		return todayAmt;
	}

	public void setTodayAmt(BigDecimal todayAmt) {
		this.todayAmt = todayAmt;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	};
	
	

}
