package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * SysCoreOperate entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_core_operate", catalog = "flypayfx")
public class TcoreOperate implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 2093104433057243324L;
	private Long id;
	private Long version;
	private String dateTime;
	private Integer t1ToAvlFlag;
	private Integer t1PayFlag;
	private Integer status;
	private String remark;
	private String creator;
	private Date createDate;

	// Constructors

	/** default constructor */
	public TcoreOperate() {
	}

	/** full constructor */
	public TcoreOperate(String dateTime, Integer t1ToAvlFlag, Integer t1PayFlag, Integer status, String remark, String creator) {
		this.dateTime = dateTime;
		this.t1ToAvlFlag = t1ToAvlFlag;
		this.t1PayFlag = t1PayFlag;
		this.status = 1;
		this.remark = remark;
		this.creator = creator;
		this.createDate = new Date();
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "version")
	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Column(name = "date_time", length = 16)
	public String getDateTime() {
		return this.dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	@Column(name = "t1_to_avl_flag")
	public Integer getT1ToAvlFlag() {
		return t1ToAvlFlag;
	}

	public void setT1ToAvlFlag(Integer t1ToAvlFlag) {
		this.t1ToAvlFlag = t1ToAvlFlag;
	}

	@Column(name = "t1_pay_flag")
	public Integer getT1PayFlag() {
		return this.t1PayFlag;
	}

	public void setT1PayFlag(Integer t1PayFlag) {
		this.t1PayFlag = t1PayFlag;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "remark", length = 256)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "creator", length = 32)
	public String getCreator() {
		return this.creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Column(name = "create_date", length = 19)
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}