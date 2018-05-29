package com.cn.flypay.pageModel.trans;

import java.util.Date;

public class UserOrderOperationRecord implements java.io.Serializable {
	private static final long serialVersionUID = -9044465745462098889L;
	private Long id;
	private String loginName;
	private String realName;
	private Long organizationId;
	private String organizationName;
	private String orderNum;
	private Integer orderType;
	private Integer transPayType;
	private Integer orderStatus;
	private String operator;
	private Date operationDatetime;
	private String operationName;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getTransPayType() {
		return transPayType;
	}

	public void setTransPayType(Integer transPayType) {
		this.transPayType = transPayType;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Date getOperationDatetime() {
		return operationDatetime;
	}

	public void setOperationDatetime(Date operationDatetime) {
		this.operationDatetime = operationDatetime;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

}