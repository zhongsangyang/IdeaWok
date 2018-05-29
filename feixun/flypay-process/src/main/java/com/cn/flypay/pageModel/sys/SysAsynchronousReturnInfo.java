package com.cn.flypay.pageModel.sys;

import java.util.Date;
/**
 * 订单异步返回通知保存
 * @author liangchao
 *
 */
public class SysAsynchronousReturnInfo implements java.io.Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4243930386351819541L;
	private Long id;
	private String orderNum;
	private String returnInfo;
	private Date createTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	public String getReturnInfo() {
		return returnInfo;
	}
	public void setReturnInfo(String returnInfo) {
		this.returnInfo = returnInfo;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	
}
