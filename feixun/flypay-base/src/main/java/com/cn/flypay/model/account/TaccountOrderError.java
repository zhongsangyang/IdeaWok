package com.cn.flypay.model.account;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "account_order_error")
public class TaccountOrderError implements java.io.Serializable {


	private static final long serialVersionUID = -7346363693581326984L;
	
	private Long id;
	private Long userID;
	private String type;
	private String orderNum;
	private BigDecimal orderAmt;
	private BigDecimal accountAmt;
	private Date createTime;
	private String remark;
	
	
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	@Column(name = "userID")
	public Long getUserID() {
		return userID;
	}
	public void setUserID(Long userID) {
		this.userID = userID;
	}
	
	
	@Column(name = "type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	@Column(name = "orderNum")
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	
	
	@Column(name = "orderAmt", precision = 12, scale = 2)
	public BigDecimal getOrderAmt() {
		return orderAmt;
	}
	public void setOrderAmt(BigDecimal orderAmt) {
		this.orderAmt = orderAmt;
	}
	
	
	@Column(name = "accountAmt", precision = 12, scale = 2)
	public BigDecimal getAccountAmt() {
		return accountAmt;
	}
	public void setAccountAmt(BigDecimal accountAmt) {
		this.accountAmt = accountAmt;
	}
	
	
	@Column(name = "createTime", length = 10)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}


	
	

	
	
	
	

}