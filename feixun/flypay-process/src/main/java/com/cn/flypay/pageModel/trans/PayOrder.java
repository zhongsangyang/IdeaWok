package com.cn.flypay.pageModel.trans;

import java.math.BigDecimal;
import java.util.Date;

public class PayOrder implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8100454930331226866L;
	private Long id;
	private Long payChannelId;
	private String payNo;
	private BigDecimal payAmt;
	private BigDecimal realAmt;
	private Date payDate;
	private Date finishDate;
	private Integer status;

	public enum pay_status {
		/**
		 * 支付成功
		 */
		SUCCESS(1),
		/**
		 * 支付失败
		 */
		FAILURE(2),
		/**
		 * 等待支付
		 */
		PROCESSING(0),
		
		/**
		 * 冻结
		 * 易宝--银联积分--新增
		 */
		FROZEN(3),
		
		/**
		 * 解冻
		 * 易宝--银联积分--新增
		 */
		THAWED(4),
		/**
		 * 冲正
		 * 易宝--银联积分--新增
		 */
		REVERSE(5),
		
		/**
		 * 提现专用
		 * 已生成待交付提现表
		 */
		WAIT_DOWNLOAD(6),
		/**
		 * 提现专用
		 * 订单已经被下载到File中
		 */
		DOWNLOADED(7);
		
		
		private pay_status(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	private String errorCode;
	private String errorInfo;
	private String payFinishDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPayChannelId() {
		return payChannelId;
	}

	public void setPayChannelId(Long payChannelId) {
		this.payChannelId = payChannelId;
	}

	public String getPayNo() {
		return payNo;
	}

	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	public BigDecimal getPayAmt() {
		return payAmt;
	}

	public void setPayAmt(BigDecimal payAmt) {
		this.payAmt = payAmt;
	}

	public BigDecimal getRealAmt() {
		return realAmt;
	}

	public void setRealAmt(BigDecimal realAmt) {
		this.realAmt = realAmt;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public String getPayFinishDate() {
		return payFinishDate;
	}

	public void setPayFinishDate(String payFinishDate) {
		this.payFinishDate = payFinishDate;
	}

}