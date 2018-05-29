package com.cn.flypay.model.sys;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
/**
 * 订单异步返回通知保存
 * @author liangchao
 *
 */
@Entity
@Table(name = "sys_asynchronous_return_info")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TsysAsynchronousReturnInfo implements java.io.Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6047978425477354365L;
	private Long id;
	private String orderNum;
	private String returnInfo;
	private Date createTime;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "order_num")
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	
	@Column(name = "return_info")
	public String getReturnInfo() {
		return returnInfo;
	}
	public void setReturnInfo(String returnInfo) {
		this.returnInfo = returnInfo;
	}
	
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	
	
	
	
}
