package com.cn.flypay.model.trans;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "trans_order_operation_record")
public class TuserOrderOperationRecord implements java.io.Serializable {

	private static final long serialVersionUID = 1808357617969944499L;
	private Long id;
	private String loginName;
	private String realName;
	private String organizationName;
	private String orderNum;
	private Integer orderType;
	private Integer transPayType;
	private Integer orderStatus;
	private String operator;
	private Date operationDatetime;
	private String operationName;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "login_name")
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Column(name = "real_name")
	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	@Column(name = "organization_name")
	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	@Column(name = "order_num")
	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	@Column(name = "order_type")
	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	@Column(name = "trans_pay_type")
	public Integer getTransPayType() {
		return transPayType;
	}

	public void setTransPayType(Integer transPayType) {
		this.transPayType = transPayType;
	}

	@Column(name = "order_status")
	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	@Column(name = "operator")
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	@Column(name = "operation_datetime")
	public Date getOperationDatetime() {
		return operationDatetime;
	}

	public void setOperationDatetime(Date operationDatetime) {
		this.operationDatetime = operationDatetime;
	}

	@Column(name = "operation_name")
	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	

}