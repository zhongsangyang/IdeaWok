package com.cn.flypay.pageModel.trans;

import java.util.Date;

/**
 * 用户冻结历史操作
 * @author liangchao
 *
 */
public class SysFreezeHistory implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4134200878807257456L;
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
	 * 行为释放者 
	 */
	private String creatorName;
	/**
	 * 行为  冻结/解冻
	 */
	private Integer behaviorType;
	/**
	 * 行为接受者ID  对应 sys_user - id
	 */
	private Long targetId;
	/**
	 * 行为接受者手机号
	 */
	private String targetPhone;
	/**
	 * 行为接受者
	 */
	private String targetName;
	/**
	 * 备注信息
	 */
	private String remark;
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
	
	public SysFreezeHistory(){}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Integer getBehaviorType() {
		return behaviorType;
	}

	public void setBehaviorType(Integer behaviorType) {
		this.behaviorType = behaviorType;
	}

	public String getTargetPhone() {
		return targetPhone;
	}

	public void setTargetPhone(String targetPhone) {
		this.targetPhone = targetPhone;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
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


	
	
}
