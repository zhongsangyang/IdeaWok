package com.cn.flypay.pageModel.statement;

import java.util.ArrayList;
import java.util.List;

public class AlipayStatement {

	// 总笔数
	private Long totalNum;
	// 总交易金额

	private Double totalTradeAmt;

	// 商户收费金额

	private Double totalMerchantAmt;
	// 实际收到金额

	private Double totalRealAmt;
	
	// 手续费总金额
	private Double totalFeeAmt;

	private List<AlipayStatementDetail> details = new ArrayList<AlipayStatementDetail>();

	public Long getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Long totalNum) {
		this.totalNum = totalNum;
	}


	public Double getTotalTradeAmt() {
		return totalTradeAmt;
	}

	public void setTotalTradeAmt(Double totalTradeAmt) {
		this.totalTradeAmt = totalTradeAmt;
	}

	public Double getTotalMerchantAmt() {
		return totalMerchantAmt;
	}

	public void setTotalMerchantAmt(Double totalMerchantAmt) {
		this.totalMerchantAmt = totalMerchantAmt;
	}

	public Double getTotalRealAmt() {
		return totalRealAmt;
	}

	public void setTotalRealAmt(Double totalRealAmt) {
		this.totalRealAmt = totalRealAmt;
	}

	public Double getTotalFeeAmt() {
		return totalFeeAmt;
	}

	public void setTotalFeeAmt(Double totalFeeAmt) {
		this.totalFeeAmt = totalFeeAmt;
	}

	public List<AlipayStatementDetail> getDetails() {
		return details;
	}

	public void setDetails(List<AlipayStatementDetail> details) {
		this.details = details;
	}

}
