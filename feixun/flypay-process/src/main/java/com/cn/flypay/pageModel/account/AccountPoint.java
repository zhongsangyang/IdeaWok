package com.cn.flypay.pageModel.account;

import com.cn.flypay.model.account.TaccountPoint;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.sys.User;

public class AccountPoint implements java.io.Serializable {

	private static final long serialVersionUID = -8771034564069L;
	/**
	 * 推广得积分类型
	 */
	public static String pointTypes_popularity = "point_popularity_score";

	/**
	 * 消耗积分类型
	 */
	public static String pointTypes_consume = "point_consume_score";

	private Long id;
	private Long version;
	private Long point;
	private Long lockPoint;
	private Integer status;
	private String name;
	private String realName;
	private String loginName;

	private Long organizationId;

	private String organizationName;

	private Integer subPersonNum;
	private User operateUser;

	public AccountPoint() {
	}

	public AccountPoint(TaccountPoint t) {

		Tuser user = t.getUser();
		if (user != null) {
			this.name = t.getUser().getName();
			this.realName = t.getUser().getRealName();
			this.loginName = t.getUser().getLoginName();
			if (user.getOrganization() != null) {
				this.organizationName = user.getOrganization().getName();
			}
		}
		this.point = t.getPoint();
		this.lockPoint = t.getLockPoint();
		this.status = t.getStatus();
		this.id = t.getId();
		this.subPersonNum = t.getSubPersonNum();
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

	public Long getPoint() {
		return point;
	}

	public void setPoint(Long point) {
		this.point = point;
	}

	public Long getLockPoint() {
		return lockPoint;
	}

	public void setLockPoint(Long lockPoint) {
		this.lockPoint = lockPoint;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRealName() {
		return realName;
	}

	public Integer getSubPersonNum() {
		if (subPersonNum == null) {
			subPersonNum = 0;
		}
		return subPersonNum;
	}

	public void setSubPersonNum(Integer subPersonNum) {
		this.subPersonNum = subPersonNum;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
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

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

}