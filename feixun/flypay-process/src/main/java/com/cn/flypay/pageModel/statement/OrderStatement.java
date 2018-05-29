package com.cn.flypay.pageModel.statement;

import java.util.Date;

public class OrderStatement implements java.io.Serializable {

	private static final long serialVersionUID = 772560885028341L;
	private Long id;
	private Long orderId;
	private String orderNo;
	/**
	 * 0 正常 1 异常
	 * */
	private Integer status;
	private String errorInfo;
	private String dealStatus;
	private String dealDesc;
	private String statementNo;

	private String loginName;
	private String realName;

	private Integer statementType;

	// Fields
	public enum statement_type {
		/**
		 * 攒善付
		 */
		ZANSHANFU(100),
		/**
		 * 微信
		 */
		WEIXIN(200),
		/**
		 * 支付宝
		 */
		ALIPAY(300),
		/**
		 * 平安
		 */
		PINGAN(400),
		/**
		 * 民生
		 */
		MINSHENG(500),
		/**
		 * 民生D0提现
		 */
		MINSHENG_D0_TIXIAN(510),
		/**
		 * 平安支付
		 */
		PINGANPAY(600),
		/**
		 * 欣客支付
		 */
		XINKE(700);
		private statement_type(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	private String statementDate;
	private String statementDateStart;
	private String statementDateEnd;
	private Date createTime;

	public String getStatementNo() {
		return statementNo;
	}

	public void setStatementNo(String statementNo) {
		this.statementNo = statementNo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStatementDateStart() {
		return statementDateStart;
	}

	public void setStatementDateStart(String statementDateStart) {
		this.statementDateStart = statementDateStart;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getStatementDateEnd() {
		return statementDateEnd;
	}

	public void setStatementDateEnd(String statementDateEnd) {
		this.statementDateEnd = statementDateEnd;
	}

	public Integer getStatementType() {
		return statementType;
	}

	public void setStatementType(Integer statementType) {
		this.statementType = statementType;
	}

	public String getStatementDate() {
		return statementDate;
	}

	public void setStatementDate(String statementDate) {
		this.statementDate = statementDate;
	}

	public String getOrderNo() {
		return orderNo;
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

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public String getDealStatus() {
		return dealStatus;
	}

	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}

	public String getDealDesc() {
		return dealDesc;
	}

	public void setDealDesc(String dealDesc) {
		this.dealDesc = dealDesc;
	}

}