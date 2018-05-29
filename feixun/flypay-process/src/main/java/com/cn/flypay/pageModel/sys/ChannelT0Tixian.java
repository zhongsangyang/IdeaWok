package com.cn.flypay.pageModel.sys;

import java.math.BigDecimal;
import java.util.Date;

import com.cn.flypay.pageModel.trans.UserOrder;

public class ChannelT0Tixian implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7310433532168959510L;
	private Long id;
	private Integer version;
	private String orderNum;
	private String busNum;
	private BigDecimal amt;
	private BigDecimal drawFee;
	private BigDecimal tradeFee;

	private Long channelId;
	private String channelName;
	private String channelDetailName;
	/* 100 成功 200 失败 300 进行中 */
	private Integer status;
	/* 100 成功 200 失败 300 未对账 */
	private Integer sltStatus;
	private String errorCode;
	private String errorDesc;
	private String cardNo;
	private String cardHolder;
	private Date createDate;
	private String createDatetimeStart;
	private String createDatetimeEnd;

	private Date finishDate;
	private String creator;
	private User operateUser;

	public ChannelT0Tixian() {
	}

	public ChannelT0Tixian(String orderNum, BigDecimal amt, BigDecimal drawFee, BigDecimal tradeFee, Long channelId, String creator) {
		this.orderNum = orderNum;
		this.amt = amt;
		this.drawFee = drawFee;
		this.tradeFee = tradeFee;
		this.channelId = channelId;
		this.status = UserOrder.order_status.PROCESSING.getCode();
		this.sltStatus = UserOrder.order_status.PROCESSING.getCode();
		this.createDate = new Date();
		this.creator = creator;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

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

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getBusNum() {
		return busNum;
	}

	public void setBusNum(String busNum) {
		this.busNum = busNum;
	}

	public BigDecimal getAmt() {
		return amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	public BigDecimal getDrawFee() {
		return drawFee;
	}

	public void setDrawFee(BigDecimal drawFee) {
		this.drawFee = drawFee;
	}

	public BigDecimal getTradeFee() {
		return tradeFee;
	}

	public void setTradeFee(BigDecimal tradeFee) {
		this.tradeFee = tradeFee;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCreateDatetimeStart() {
		return createDatetimeStart;
	}

	public void setCreateDatetimeStart(String createDatetimeStart) {
		this.createDatetimeStart = createDatetimeStart;
	}

	public String getCreateDatetimeEnd() {
		return createDatetimeEnd;
	}

	public void setCreateDatetimeEnd(String createDatetimeEnd) {
		this.createDatetimeEnd = createDatetimeEnd;
	}

	public Integer getSltStatus() {
		return sltStatus;
	}

	public void setSltStatus(Integer sltStatus) {
		this.sltStatus = sltStatus;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCardHolder() {
		return cardHolder;
	}

	public void setCardHolder(String cardHolder) {
		this.cardHolder = cardHolder;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
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

}