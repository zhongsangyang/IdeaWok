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
 * 用户冻结历史操作
 * @author liangchao
 *
 */
@Entity
@Table(name = "sys_freeze_history")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TsysFreezeHistory  implements java.io.Serializable{
	
	private static final long serialVersionUID = -6144006601364815535L;
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
	 * 行为  0 解冻   1冻结
	 */
	private Integer behaviorType;
	/**
	 * 行为接受者
	 */
	private Tuser target;
	/**
	 * 行为接受者名称
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
	
	public TsysFreezeHistory(){}

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

	@Column(name = "behavior_type")
	public Integer getBehaviorType() {
		return behaviorType;
	}

	public void setBehaviorType(Integer behaviorType) {
		this.behaviorType = behaviorType;
	}
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target")
	public Tuser getTarget() {
		return target;
	}
	public void setTarget(Tuser target) {
		this.target = target;
	}
	
	@Column(name = "target_name")
	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	
	
	
}
