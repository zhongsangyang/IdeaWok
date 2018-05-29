package com.cn.flypay.model.trans;

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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.Tuser;
/**
 * 通道编辑操作记录表
 * @author liangchao
 *
 */
@Entity
@Table(name = "sys_channel_operation_history")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TsysChannelOperationHistory implements java.io.Serializable{

	private static final long serialVersionUID = 7575256291094588200L;
	private Long id;
	/**
	 * 行为释放者
	 */
	private Tuser creator;
	/**
	 * 行为释放者 名称
	 */
	private String creatorName;
	
	/**
	 * 记录的所属类型
	 */
	private Integer recordType;
	/**
	 * 参数含义见数据库备注信息
	 */
	private Integer behaviorType;
	/**
	 * 通道标识ID
	 */
	private Tchannel target;
	
	/**
	 * 详情信息
	 */
	private String details;
	/**
	 * 行为产生时间
	 */
	private Date createTime;
	
	
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
	
	
	public TsysChannelOperationHistory(){}
	
	@Id
	@GenericGenerator(name = "generator", strategy = "increment")
	@GeneratedValue(generator = "generator")
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator")
	public Tuser getCreator() {
		return creator;
	}

	public void setCreator(Tuser creator) {
		this.creator = creator;
	}

	@Column(name = "creator_name")
	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	@Column(name = "record_type")
	public Integer getRecordType() {
		return recordType;
	}

	public void setRecordType(Integer recordType) {
		this.recordType = recordType;
	}

	@Column(name = "behavior_type")
	public Integer getBehaviorType() {
		return behaviorType;
	}

	public void setBehaviorType(Integer behaviorType) {
		this.behaviorType = behaviorType;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target")
	public Tchannel getTarget() {
		return target;
	}

	public void setTarget(Tchannel target) {
		this.target = target;
	}
	
	@Column(name = "details")
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "detail_name")
	public String getDetailName() {
		return detailName;
	}

	public void setDetailName(String detailName) {
		this.detailName = detailName;
	}

	@Column(name = "account")
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	@Column(name = "real_rate")
	public BigDecimal getRealRate() {
		return realRate;
	}

	public void setRealRate(BigDecimal realRate) {
		this.realRate = realRate;
	}
	@Column(name = "show_rate")
	public BigDecimal getShowRate() {
		return showRate;
	}

	public void setShowRate(BigDecimal showRate) {
		this.showRate = showRate;
	}

	@Column(name = "share_rate")
	public BigDecimal getShareRate() {
		return shareRate;
	}

	public void setShareRate(BigDecimal shareRate) {
		this.shareRate = shareRate;
	}

	@Column(name = "commission_rate")
	public BigDecimal getCommissionRate() {
		return commissionRate;
	}

	public void setCommissionRate(BigDecimal commissionRate) {
		this.commissionRate = commissionRate;
	}

	@Column(name = "min_trade_amt")
	public BigDecimal getMinTradeAmt() {
		return minTradeAmt;
	}

	public void setMinTradeAmt(BigDecimal minTradeAmt) {
		this.minTradeAmt = minTradeAmt;
	}

	@Column(name = "max_trade_amt")
	public BigDecimal getMaxTradeAmt() {
		return maxTradeAmt;
	}

	public void setMaxTradeAmt(BigDecimal maxTradeAmt) {
		this.maxTradeAmt = maxTradeAmt;
	}
	
	@Column(name = "min_channel_amt")
	public BigDecimal getMinChannelAmt() {
		return minChannelAmt;
	}

	public void setMinChannelAmt(BigDecimal minChannelAmt) {
		this.minChannelAmt = minChannelAmt;
	}

	@Column(name = "max_channel_amt")
	public BigDecimal getMaxChannelAmt() {
		return maxChannelAmt;
	}

	public void setMaxChannelAmt(BigDecimal maxChannelAmt) {
		this.maxChannelAmt = maxChannelAmt;
	}
	
	@Column(name = "max_Amt_Per_Day")
	public BigDecimal getMaxAmtPerDay() {
		return maxAmtPerDay;
	}

	public void setMaxAmtPerDay(BigDecimal maxAmtPerDay) {
		this.maxAmtPerDay = maxAmtPerDay;
	}
	
	@Column(name = "max_num_per_person_per_day")
	public Long getMaxNumPerPersonPerDay() {
		return maxNumPerPersonPerDay;
	}

	public void setMaxNumPerPersonPerDay(Long maxNumPerPersonPerDay) {
		this.maxNumPerPersonPerDay = maxNumPerPersonPerDay;
	}

	@Column(name = "today_Amt")
	public BigDecimal getTodayAmt() {
		return todayAmt;
	}

	public void setTodayAmt(BigDecimal todayAmt) {
		this.todayAmt = todayAmt;
	}

	@Column(name = "seq")
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "config")
	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}
	
	
	
}
