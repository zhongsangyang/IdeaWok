package com.cn.flypay.pageModel.account;

import java.math.BigDecimal;

public class AccountOrderError implements java.io.Serializable{


	private static final long serialVersionUID = 1972128236284692383L;
	
	
	private Long id;
	private Long userID;
	private String type;
	private String orderNum;
	private BigDecimal orderAmt;
	private BigDecimal accountAmt;
	private String createTime;
	private String remark;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserID() {
		return userID;
	}
	public void setUserID(Long userID) {
		this.userID = userID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	public BigDecimal getOrderAmt() {
		return orderAmt;
	}
	public void setOrderAmt(BigDecimal orderAmt) {
		this.orderAmt = orderAmt;
	}
	public BigDecimal getAccountAmt() {
		return accountAmt;
	}
	public void setAccountAmt(BigDecimal accountAmt) {
		this.accountAmt = accountAmt;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	
	
	

}
