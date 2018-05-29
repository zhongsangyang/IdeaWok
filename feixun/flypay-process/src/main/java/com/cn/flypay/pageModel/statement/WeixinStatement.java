package com.cn.flypay.pageModel.statement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WeixinStatement {

	// 总笔数
	private Long totalNum;
	// 总金额

	private Double totalAmt;

	// 成功金额

	private Double totalRefundAmt;
	// 总企业红包退款金额

	private Double totalBonusRefundAmt;
	// 手续费总金额
	private Double totalFeeAmt;

	private BigDecimal feeRate;
	private List<WeixinStatementDetail> details = new ArrayList<WeixinStatementDetail>();

	public Long getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Long totalNum) {
		this.totalNum = totalNum;
	}

	public Double getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(Double totalAmt) {
		this.totalAmt = totalAmt;
	}

	public Double getTotalRefundAmt() {
		return totalRefundAmt;
	}

	public BigDecimal getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}

	public void setTotalRefundAmt(Double totalRefundAmt) {
		this.totalRefundAmt = totalRefundAmt;
	}

	public Double getTotalBonusRefundAmt() {
		return totalBonusRefundAmt;
	}

	public void setTotalBonusRefundAmt(Double totalBonusRefundAmt) {
		this.totalBonusRefundAmt = totalBonusRefundAmt;
	}

	public Double getTotalFeeAmt() {
		return totalFeeAmt;
	}

	public void setTotalFeeAmt(Double totalFeeAmt) {
		this.totalFeeAmt = totalFeeAmt;
	}

	public List<WeixinStatementDetail> getDetails() {
		return details;
	}

	public void setDetails(List<WeixinStatementDetail> details) {
		this.details = details;
	}

}
