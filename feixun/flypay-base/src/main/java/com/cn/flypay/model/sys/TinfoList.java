package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * SysInfoList entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_info_list")
public class TinfoList implements java.io.Serializable {

	// Fields
	private static final long serialVersionUID = -5416905291829395280L;
	private Long id;
	private Integer version;
	private String title;
	private String content;
	private Date createTime;
	private String creator;
	private Long msgId;
	private Long sendNo;
	/**
	 * 1 个人 2 公司公告
	 */
	private Integer infoType;
	/**
	 * 0 未读 1已读
	 */
	private Integer isRead;
	/**
	 * 0 不展示 1展示
	 */
	private Integer isShow;
	private Date updateTime;
	/**
	 * 0 初始化，1 发布，12 失败,2 撤销
	 */
	private Integer status;

	private Tuser user;

	private Torganization organization;

	private Integer isForce;

	private Integer forceHours;

	// Constructors

	/** default constructor */
	public TinfoList() {
	}

	/** full constructor */
	public TinfoList(Tuser user, String title, int infoType, String content, Integer isForce, Integer forceHours) {
		this.user = user;
		if (user.getOrganization() != null) {
			this.organization = user.getOrganization();
		}
		this.title = title;
		this.infoType = infoType;
		this.isRead = 0;
		this.content = content;
		this.createTime = new Date();
		this.status = 0;
		this.isForce = isForce;
		this.isShow = 1;
		this.forceHours = forceHours;
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
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "title", length = 128)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "msg_Id")
	public Long getMsgId() {
		return msgId;
	}

	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}

	@Column(name = "send_No")
	public Long getSendNo() {
		return sendNo;
	}

	public void setSendNo(Long sendNo) {
		this.sendNo = sendNo;
	}

	@Column(name = "content", length = 256)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "creator", length = 32)
	public String getCreator() {
		return this.creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "is_show")
	public Integer getIsShow() {
		return isShow;
	}

	public void setIsShow(Integer isShow) {
		this.isShow = isShow;
	}

	@Column(name = "info_type")
	public Integer getInfoType() {
		return infoType;
	}

	public void setInfoType(Integer infoType) {
		this.infoType = infoType;
	}

	@Column(name = "is_read")
	public Integer getIsRead() {
		return isRead;
	}

	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}

	@Column(name = "update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	public Tuser getUser() {
		return this.user;
	}

	public void setUser(Tuser user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	public Torganization getOrganization() {
		return organization;
	}

	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}

	@Column(name = "is_force")
	public Integer getIsForce() {
		return isForce;
	}

	public void setIsForce(Integer isForce) {
		this.isForce = isForce;
	}

	@Column(name = "force_hours")
	public Integer getForceHours() {
		return forceHours;
	}

	public void setForceHours(Integer forceHours) {
		this.forceHours = forceHours;
	}

}