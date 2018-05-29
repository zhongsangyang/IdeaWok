package com.cn.flypay.pageModel.trans;

import java.util.Date;

/**
 * 平台费率相关操作记录表
 * @author liangchao
 *
 */
public class SysRateOperationHistory implements java.io.Serializable {

	private static final long serialVersionUID = -3391792242575891322L;
	private Long id;
	/**
	 * 行为释放者ID 对应 sys_user - id
	 */
	private Long creatorId;
	/**
	 * 行为释放者手机号
	 */
	private String creatorPhone;
	
	/**
	 * 行为释放者 姓名
	 */
	private String creatorName;
	
	/**
	 * 记录的所属类型
	 */
	private Integer recordType;
	
	/**
	 * 行为类型
	 */
	private Integer behaviorType;
	/**
	 * 行为接受者的信息
	 */
	private String targetInfo;
	/**
	 * 行为接受者在自身信息存放表中的ID信息
	 */
	private Long targetId;
	/**
	 * 操作详情信息
	 */
	private String details;
	/**
	 * 行为产生时间
	 */
	private Date createTime;
	/**
	 * 查询开始时间
	 */
	private String searchDateStart;
	/**
	 * 查询结束时间
	 */
	private String searchDateEnd;
	
	public SysRateOperationHistory(){}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorPhone() {
		return creatorPhone;
	}

	public void setCreatorPhone(String creatorPhone) {
		this.creatorPhone = creatorPhone;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Integer getRecordType() {
		return recordType;
	}

	public void setRecordType(Integer recordType) {
		this.recordType = recordType;
	}

	public Integer getBehaviorType() {
		return behaviorType;
	}

	public void setBehaviorType(Integer behaviorType) {
		this.behaviorType = behaviorType;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getSearchDateStart() {
		return searchDateStart;
	}

	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}

	public String getSearchDateEnd() {
		return searchDateEnd;
	}

	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}

	public String getTargetInfo() {
		return targetInfo;
	}

	public void setTargetInfo(String targetInfo) {
		this.targetInfo = targetInfo;
	}
	
}
