package com.cn.flypay.model.trans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cn.flypay.model.sys.Tuser;
/**
 * 用户角色权限分配记录
 * @author liangchao
 *
 */
@Entity
@Table(name = "sys_role_history")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TsysRoleHistory implements java.io.Serializable{
	private static final long serialVersionUID = -3787590442330730778L;
	private Long id;
	/**
	 * 行为释放者
	 */
	private Tuser creator;
	/**
	 * 行为释放者 名称
	 */
	private String creatorName;
	
	/**
	 * 记录的所属类型
	 */
	private Integer recordType;
	/**
	 * 参数含义见数据库备注信息
	 */
	private Integer behaviorType;
	/**
	 * 行为接受者在自身信息存放表中的ID信息
	 */
	private Long targetId;
	/**
	 * 行为接受者在自身信息存放表中的名称
	 */
	private String targetName;
	/**
	 * 操作详情信息
	 */
	private String details;
	/**
	 * 行为产生时间
	 */
	private Date createTime;
	
	public TsysRoleHistory(){}
	
	@Id
	@GenericGenerator(name = "generator", strategy = "increment")
	@GeneratedValue(generator = "generator")
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator")
	public Tuser getCreator() {
		return creator;
	}

	public void setCreator(Tuser creator) {
		this.creator = creator;
	}

	@Column(name = "creator_name")
	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	@Column(name = "record_type")
	public Integer getRecordType() {
		return recordType;
	}

	public void setRecordType(Integer recordType) {
		this.recordType = recordType;
	}

	@Column(name = "behavior_type")
	public Integer getBehaviorType() {
		return behaviorType;
	}

	public void setBehaviorType(Integer behaviorType) {
		this.behaviorType = behaviorType;
	}

	@Column(name = "target_id")
	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	@Column(name = "target_name")
	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	@Column(name = "details")
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	
	
	
}
