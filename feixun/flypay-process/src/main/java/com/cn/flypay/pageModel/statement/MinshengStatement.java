package com.cn.flypay.pageModel.statement;

import java.util.ArrayList;
import java.util.List;

public class MinshengStatement {

	// 总笔数
	private Long totalNum;
	// 总金额

	private Double totalAmt;

	// 成功金额

	// 手续费总金额
	private Double totalFeeAmt;

	private List<MinshengStatementDetail> details = new ArrayList<MinshengStatementDetail>();

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

	public Double getTotalFeeAmt() {
		return totalFeeAmt;
	}

	public void setTotalFeeAmt(Double totalFeeAmt) {
		this.totalFeeAmt = totalFeeAmt;
	}

	public List<MinshengStatementDetail> getDetails() {
		return details;
	}

	public void setDetails(List<MinshengStatementDetail> details) {
		this.details = details;
	}
}
