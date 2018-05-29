package com.cn.flypay.pageModel.sys;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 用于查询通道的当日流入金额
 * @author liangchao
 *
 */
public class ChannelDayAmt implements java.io.Serializable {
	
	private static final long serialVersionUID = -8695775776749359847L;
	/**
	 * 通道当日流入金额
	 */
	private BigDecimal sumTodayAmt;
	/**
	 * 通道的渠道名称		例如：民生、民生直通车等
	 */
	private String name;
	/**
	 * 通道的类型		例如:支付宝，微信等
	 */
	private Integer type;
	
	/**
	 * 用于查询记录起始日期
	 */
	private String createTimeStart;
	
	/**
	 * 用于查询记录终止日期
	 */
	private String createTimeEnd;
	
	public ChannelDayAmt(){};
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public BigDecimal getSumTodayAmt() {
		return sumTodayAmt;
	}
	public void setSumTodayAmt(BigDecimal sumTodayAmt) {
		this.sumTodayAmt = sumTodayAmt;
	}
	public String getCreateTimeStart() {
		return createTimeStart;
	}
	public void setCreateTimeStart(String createTimeStart) {
		this.createTimeStart = createTimeStart;
	}
	public String getCreateTimeEnd() {
		return createTimeEnd;
	}
	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}
	
	
	
}
