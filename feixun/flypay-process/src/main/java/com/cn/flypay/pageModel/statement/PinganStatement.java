package com.cn.flypay.pageModel.statement;

import java.util.ArrayList;
import java.util.List;

public class PinganStatement {

	// 总笔数
	private Long totalNum;
	// 总金额

	private Double totalAmt;
	// 成功笔数

	private Long totalSuccessNum;
	// 成功金额

	private Double totalSuccessAmt;
	// 失败笔数

	private Long totalFailNum;
	// 失败金额

	private Double totalFailAmt;

	private List<PinganStatementDetail> details = new ArrayList<PinganStatementDetail>();

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

	public Long getTotalSuccessNum() {
		return totalSuccessNum;
	}

	public void setTotalSuccessNum(Long totalSuccessNum) {
		this.totalSuccessNum = totalSuccessNum;
	}

	public List<PinganStatementDetail> getDetails() {
		return details;
	}

	public void setDetails(List<PinganStatementDetail> details) {
		this.details = details;
	}

	public Double getTotalSuccessAmt() {
		return totalSuccessAmt;
	}

	public void setTotalSuccessAmt(Double totalSuccessAmt) {
		this.totalSuccessAmt = totalSuccessAmt;
	}

	public Long getTotalFailNum() {
		return totalFailNum;
	}

	public void setTotalFailNum(Long totalFailNum) {
		this.totalFailNum = totalFailNum;
	}

	public Double getTotalFailAmt() {
		return totalFailAmt;
	}

	public void setTotalFailAmt(Double totalFailAmt) {
		this.totalFailAmt = totalFailAmt;
	}

}
