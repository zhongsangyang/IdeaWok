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
 * 平台费率相关操作记录表
 * @author liangchao
 *
 */
@Entity
@Table(name = "sys_rate_operation_history")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TsysRateOperationHistory implements java.io.Serializable{
	
	private static final long serialVersionUID = 7615275152352587354L;
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
	 * 被操作目标的信息
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
	
	public TsysRateOperationHistory(){}

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

	@Column(name = "target_info")
	public String getTargetInfo() {
		return targetInfo;
	}

	public void setTargetInfo(String targetInfo) {
		this.targetInfo = targetInfo;
	}

	@Column(name = "target_id")
	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
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
