package com.cn.flypay.pageModel.sys;

import java.util.Date;

public class InfoList implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5628060373534804888L;
	// Fields

	private Long id;
	private Integer version;
	private String title;
	private String content;
	private Date createTime;
	private String creator;
	private String userName;
	private Long userId;
	private String phone;

	private Long msgId;
	private Long sendNo;
	/**
	 * 0 初始化，1 发布，2 撤销
	 */
	private Integer status;

	private Long organizationId;

	private String organizationName;

	private String agentId;

	private Integer isForce;
	private Integer isShow;
	private Date createdatetimeStart;
	private Date createdatetimeEnd;

	// Fields
	public enum is_force {
		/**
		 * 初始化
		 */
		not_force(0),
		/**
		 * 发布
		 */
		force(1);
		private is_force(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	private Integer forceHours;

	// Fields
	public enum info_status {
		/**
		 * 初始化
		 */
		init(0),
		/**
		 * 发布
		 */
		release_success(1),
		/**
		 * 发布失败
		 */
		release_failure(12),
		/**
		 * 撤回
		 */
		back(2);
		private info_status(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	/**
	 * 1 个人 2 公司公告
	 */
	private Integer infoType;

	// Fields
	public enum info_Type {
		/**
		 * 个人
		 */
		person(1),
		/**
		 * 公司公告
		 */
		company(2);
		private info_Type(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	/**
	 * 0 未读 1已读
	 */
	private Integer isRead;

	// Fields
	public enum is_Read {
		/**
		 * 未读
		 */
		unread(0),
		/**
		 * 已读
		 */
		readed(1);
		private is_Read(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	private Date updateTime;

	private User operateUser;

	public InfoList() {
	}

	public InfoList(Long userId, String title, int infoType, String content, Integer isForce, Integer forceHours) {
		this.userId = userId;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public Integer getIsShow() {
		return isShow;
	}

	public void setIsShow(Integer isShow) {
		this.isShow = isShow;
	}

	public Date getCreatedatetimeStart() {
		return createdatetimeStart;
	}

	public void setCreatedatetimeStart(Date createdatetimeStart) {
		this.createdatetimeStart = createdatetimeStart;
	}

	public Date getCreatedatetimeEnd() {
		return createdatetimeEnd;
	}

	public void setCreatedatetimeEnd(Date createdatetimeEnd) {
		this.createdatetimeEnd = createdatetimeEnd;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getInfoType() {
		return infoType;
	}

	public void setInfoType(Integer infoType) {
		this.infoType = infoType;
	}

	public Integer getIsRead() {
		return isRead;
	}

	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public String getUserName() {
		return userName;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Long getMsgId() {
		return msgId;
	}

	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}

	public Long getSendNo() {
		return sendNo;
	}

	public void setSendNo(Long sendNo) {
		this.sendNo = sendNo;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public Integer getIsForce() {
		return isForce;
	}

	public void setIsForce(Integer isForce) {
		this.isForce = isForce;
	}

	public Integer getForceHours() {
		return forceHours;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public void setForceHours(Integer forceHours) {
		this.forceHours = forceHours;
	}

}