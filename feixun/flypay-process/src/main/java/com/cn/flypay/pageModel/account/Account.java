package com.cn.flypay.pageModel.account;

import java.math.BigDecimal;
import java.util.Date;

import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.sys.User;

public class Account implements java.io.Serializable {

	@Override
	public String toString() {
		return "Account [id=" + id + ", version=" + version + ", userId=" + userId + ", type=" + type
				+ ", perMonthInAmt=" + perMonthInAmt + ", perMonthOutAmt=" + perMonthOutAmt + ", lockOutAmt="
				+ lockOutAmt + ", avlAmt=" + avlAmt + ", throughAmt=" + throughAmt + ", status=" + status + ", name="
				+ name + ", realName=" + realName + ", loginName=" + loginName + ", createDatetime=" + createDatetime
				+ ", dateForYM=" + dateForYM + ", organizationId=" + organizationId + ", organizationName="
				+ organizationName + ", operateUser=" + operateUser + "]";
	}

	private static final long serialVersionUID = 3172183553147132990L;
	private Long id;
	private Long version;
	private Long userId;
	private Integer type;
	private BigDecimal perMonthInAmt;
	private BigDecimal perMonthOutAmt;
	private BigDecimal lockOutAmt;
	private BigDecimal avlAmt;
	private BigDecimal throughAmt;
	private Integer status;

	// Fields
	public enum account_status {
		/**
		 * 支付成功
		 */
		NORMAL(0),
		/**
		 * 1级冻结，仅限于本运营商
		 */
		FREEZE(1),
		/**
		 * 2级冻结，整个平台
		 */
		SERIOUS_FREEZE(100);
		private account_status(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	private String name;
	private String realName;
	private String loginName;
	private Date createDatetime; // 创建时间
	private String dateForYM; // 年月

	private Long organizationId;

	private String organizationName;
	private User operateUser;
	private BigDecimal t11Amt;
	private BigDecimal t10Amt;
	private BigDecimal t9Amt;
	private BigDecimal t8Amt;
	private BigDecimal t7Amt;
	private BigDecimal t6Amt;
	private BigDecimal t5Amt;

	private BigDecimal t4Amt;

	private BigDecimal t3Amt;

	private BigDecimal t2Amt;

	private BigDecimal t1Amt;

	private String remark;

	public Account() {
		super();
	}

	public Account(Taccount t) {
		Tuser user = t.getUser();
		if (user != null) {
			this.userId = t.getUser().getId();
			this.name = t.getUser().getName();
			this.realName = t.getUser().getRealName();
			this.loginName = t.getUser().getLoginName();
			this.createDatetime = t.getUser().getCreateDatetime();
			if (user.getOrganization() != null) {
				this.organizationName = user.getOrganization().getName();
			}
		}
		this.type = t.getType();
		this.perMonthInAmt = t.getPerMonthInAmt();
		this.perMonthOutAmt = t.getPerMonthOutAmt();
		this.lockOutAmt = t.getLockOutAmt();
		this.avlAmt = t.getAvlAmt();
		this.status = t.getStatus();
		this.id = t.getId();
		this.t1Amt = t.getT1Amt();
		this.t2Amt = t.getT2Amt();
		this.t3Amt = t.getT3Amt();
		this.t4Amt = t.getT4Amt();
		this.t5Amt = t.getT5Amt();
		this.t6Amt = t.getT6Amt();
		this.t7Amt = t.getT7Amt();
		this.t8Amt = t.getT8Amt();
		this.t9Amt = t.getT9Amt();
		this.t10Amt = t.getT10Amt();
		this.t11Amt = t.getT11Amt();
		this.throughAmt = t.getThroughAmt();
	}

	public Account(Taccount t, String r) {
//		Tuser user = t.getUser();
		this.userId = t.getUser().getId();
		this.name = t.getUser().getName();
		this.realName = t.getUser().getRealName();
		this.loginName = t.getUser().getLoginName();
		this.createDatetime = t.getUser().getCreateDatetime();

		this.type = t.getType();
		this.perMonthInAmt = t.getPerMonthInAmt();
		this.perMonthOutAmt = t.getPerMonthOutAmt();
		this.lockOutAmt = t.getLockOutAmt();
		this.avlAmt = t.getAvlAmt();
		this.status = t.getStatus();
		this.id = t.getId();
		this.t1Amt = t.getT1Amt();
		this.t2Amt = t.getT2Amt();
		this.t3Amt = t.getT3Amt();
		this.t4Amt = t.getT4Amt();
		this.t5Amt = t.getT5Amt();
		this.t6Amt = t.getT6Amt();
		this.t7Amt = t.getT7Amt();
		this.t8Amt = t.getT8Amt();
		this.t9Amt = t.getT9Amt();
		this.t10Amt = t.getT10Amt();
		this.t11Amt = t.getT11Amt();
		this.throughAmt = t.getThroughAmt();
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

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public BigDecimal getPerMonthInAmt() {
		return perMonthInAmt;
	}

	public void setPerMonthInAmt(BigDecimal perMonthInAmt) {
		this.perMonthInAmt = perMonthInAmt;
	}

	public BigDecimal getPerMonthOutAmt() {
		return perMonthOutAmt;
	}

	public void setPerMonthOutAmt(BigDecimal perMonthOutAmt) {
		this.perMonthOutAmt = perMonthOutAmt;
	}

	public BigDecimal getLockOutAmt() {
		return lockOutAmt;
	}

	public void setLockOutAmt(BigDecimal lockOutAmt) {
		this.lockOutAmt = lockOutAmt;
	}

	public BigDecimal getAvlAmt() {
		return avlAmt;
	}

	public void setAvlAmt(BigDecimal avlAmt) {
		this.avlAmt = avlAmt;
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

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	public String getDateForYM() {
		return dateForYM;
	}

	public void setDateForYM(String dateForYM) {
		this.dateForYM = dateForYM;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public BigDecimal getT5Amt() {
		return t5Amt;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public void setT5Amt(BigDecimal t5Amt) {
		this.t5Amt = t5Amt;
	}

	public BigDecimal getT4Amt() {
		return t4Amt;
	}

	public void setT4Amt(BigDecimal t4Amt) {
		this.t4Amt = t4Amt;
	}

	public BigDecimal getT3Amt() {
		return t3Amt;
	}

	public void setT3Amt(BigDecimal t3Amt) {
		this.t3Amt = t3Amt;
	}

	public BigDecimal getT2Amt() {
		return t2Amt;
	}

	public void setT2Amt(BigDecimal t2Amt) {
		this.t2Amt = t2Amt;
	}

	public BigDecimal getT1Amt() {
		return t1Amt;
	}

	public void setT1Amt(BigDecimal t1Amt) {
		this.t1Amt = t1Amt;
	}

	public BigDecimal getT6Amt() {
		return t6Amt;
	}

	public void setT6Amt(BigDecimal t6Amt) {
		this.t6Amt = t6Amt;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getThroughAmt() {
		return throughAmt;
	}

	public void setThroughAmt(BigDecimal throughAmt) {
		this.throughAmt = throughAmt;
	}

	public BigDecimal getT11Amt() {
		return t11Amt;
	}

	public void setT11Amt(BigDecimal t11Amt) {
		this.t11Amt = t11Amt;
	}

	public BigDecimal getT10Amt() {
		return t10Amt;
	}

	public void setT10Amt(BigDecimal t10Amt) {
		this.t10Amt = t10Amt;
	}

	public BigDecimal getT9Amt() {
		return t9Amt;
	}

	public void setT9Amt(BigDecimal t9Amt) {
		this.t9Amt = t9Amt;
	}

	public BigDecimal getT8Amt() {
		return t8Amt;
	}

	public void setT8Amt(BigDecimal t8Amt) {
		this.t8Amt = t8Amt;
	}

	public BigDecimal getT7Amt() {
		return t7Amt;
	}

	public void setT7Amt(BigDecimal t7Amt) {
		this.t7Amt = t7Amt;
	}

}