package com.cn.flypay.pageModel.trans;

import java.math.BigDecimal;

import com.cn.flypay.pageModel.sys.User;

public class FinanceAccount implements java.io.Serializable {

	private static final long serialVersionUID = 3172183553147132990L;
	private BigDecimal lockOutAmt;
	private BigDecimal avlAmt;

	private String organizationName;
	private User operateUser;
	
	private BigDecimal d1Amt;
	private BigDecimal d2Amt;
	
	private BigDecimal t11Amt;
	private BigDecimal t10Amt;
	private BigDecimal t9Amt;
	private BigDecimal t8Amt;
	private BigDecimal t7Amt;
	private BigDecimal t6Amt;
	private BigDecimal t5Amt;

	private BigDecimal t4Amt;

	private BigDecimal t3Amt;

	private BigDecimal t2Amt;

	private BigDecimal t1Amt;

	public BigDecimal getLockOutAmt() {
		return lockOutAmt;
	}

	public void setLockOutAmt(BigDecimal lockOutAmt) {
		this.lockOutAmt = lockOutAmt;
	}

	public BigDecimal getAvlAmt() {
		return avlAmt;
	}

	public void setAvlAmt(BigDecimal avlAmt) {
		this.avlAmt = avlAmt;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public BigDecimal getT5Amt() {
		return t5Amt;
	}

	public void setT5Amt(BigDecimal t5Amt) {
		this.t5Amt = t5Amt;
	}

	public BigDecimal getT4Amt() {
		return t4Amt;
	}

	public BigDecimal getT6Amt() {
		return t6Amt;
	}

	public void setT6Amt(BigDecimal t6Amt) {
		this.t6Amt = t6Amt;
	}

	public void setT4Amt(BigDecimal t4Amt) {
		this.t4Amt = t4Amt;
	}

	public BigDecimal getT3Amt() {
		return t3Amt;
	}

	public void setT3Amt(BigDecimal t3Amt) {
		this.t3Amt = t3Amt;
	}

	public BigDecimal getT2Amt() {
		return t2Amt;
	}

	public void setT2Amt(BigDecimal t2Amt) {
		this.t2Amt = t2Amt;
	}

	public BigDecimal getT1Amt() {
		return t1Amt;
	}

	public void setT1Amt(BigDecimal t1Amt) {
		this.t1Amt = t1Amt;
	}

	public BigDecimal getT11Amt() {
		return t11Amt;
	}

	public void setT11Amt(BigDecimal t11Amt) {
		this.t11Amt = t11Amt;
	}

	public BigDecimal getT10Amt() {
		return t10Amt;
	}

	public void setT10Amt(BigDecimal t10Amt) {
		this.t10Amt = t10Amt;
	}

	public BigDecimal getT9Amt() {
		return t9Amt;
	}

	public void setT9Amt(BigDecimal t9Amt) {
		this.t9Amt = t9Amt;
	}
    

	public BigDecimal getT8Amt() {
		return t8Amt;
	}

	public void setT8Amt(BigDecimal t8Amt) {
		this.t8Amt = t8Amt;
	}

	public BigDecimal getT7Amt() {
		return t7Amt;
	}

	public void setT7Amt(BigDecimal t7Amt) {
		this.t7Amt = t7Amt;
	}

	public BigDecimal getD1Amt() {
		return d1Amt;
	}

	public void setD1Amt(BigDecimal d1Amt) {
		this.d1Amt = d1Amt;
	}

	public BigDecimal getD2Amt() {
		return d2Amt;
	}

	public void setD2Amt(BigDecimal d2Amt) {
		this.d2Amt = d2Amt;
	}
	
	

	
}