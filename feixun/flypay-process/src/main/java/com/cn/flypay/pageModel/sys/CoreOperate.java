package com.cn.flypay.pageModel.sys;

import java.util.Date;

public class CoreOperate implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 2093103324L;
	private Long id;
	private Long version;
	private String dateTime;
	private Integer t1ToAvlFlag;
	private Integer t1PayFlag;
	private Integer status;
	private String remark;
	private String creator;
	private Date createDate;

	public CoreOperate(Long version, String dateTime, Integer t1ToAvlFlag, Integer t1PayFlag, String creator, Date createDate) {
		this.dateTime = dateTime;
		this.t1ToAvlFlag = t1ToAvlFlag;
		this.t1PayFlag = t1PayFlag;
		this.status = 1;
		this.creator = creator;
		this.createDate = new Date();
	}

	public CoreOperate() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public Integer getT1ToAvlFlag() {
		return t1ToAvlFlag;
	}

	public void setT1ToAvlFlag(Integer t1ToAvlFlag) {
		this.t1ToAvlFlag = t1ToAvlFlag;
	}

	public Integer getT1PayFlag() {
		return t1PayFlag;
	}

	public void setT1PayFlag(Integer t1PayFlag) {
		this.t1PayFlag = t1PayFlag;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}