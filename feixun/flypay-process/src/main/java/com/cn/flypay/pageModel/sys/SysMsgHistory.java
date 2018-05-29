package com.cn.flypay.pageModel.sys;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SysMsgHistory implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3414177492981423548L;
	private Long id;
	private Long version;
	private String msgCode;
	private Integer msgType;
	private String phone;
	private String validateCode;
	private String content;
	private Date createTime;

	private Integer status;

	private Integer validateNum;
	private String agentCode;

	private String agentId;
	private User operateUser;
	private Long organizationId;
	private String organizationName;

	public enum SMS_MSG_TYPE {
		regist(10), forget_psw(11), login_psw(20), trans_psw(30);
		private Integer type;

		private SMS_MSG_TYPE(Integer type) {
			this.type = type;
		}

		public int getType() {
			return this.type.intValue();
		}

	}

	public static Map<String, String> SMS_TYPE_TEMPLES = new HashMap<String, String>();
	static {
		SMS_TYPE_TEMPLES.put("10", "114310");
		SMS_TYPE_TEMPLES.put("11", "114316");
		SMS_TYPE_TEMPLES.put("20", "114318");
		SMS_TYPE_TEMPLES.put("30", "114318");
		SMS_TYPE_TEMPLES.put("40", "101010");
		SMS_TYPE_TEMPLES.put("50", "114319");
		SMS_TYPE_TEMPLES.put("60", "142355");
	}
	public static Map<String, String> SMS_TYPE_CONTENT = new HashMap<String, String>();
	static {
		/* 注册 */
		SMS_TYPE_CONTENT.put("10", "验证码：%s 您正在注册宝贝钱袋账号，请勿转发，非本人操作请致电客服【宝贝钱袋】");
		/* 忘记密码 */
		SMS_TYPE_CONTENT.put("11", "验证码 : %s 您正在申请密码找回，请勿转发，非本人操作请致电客服【宝贝钱袋】");
		/* 登录密码 */
		SMS_TYPE_CONTENT.put("20", "验证码：%s 您正在申请修改登录密码，请勿转发，非本人操作请致电客服【宝贝钱袋】");
		/* 交易密码 */
		SMS_TYPE_CONTENT.put("30", "验证码：%s 您正在申请修改交易密码，请勿转发，非本人操作请致电客服【宝贝钱袋】");
		/* 短信支付 */
		SMS_TYPE_CONTENT.put("40", "验证码：%s 请勿转发，非本人操作请致电客服【宝贝钱袋】");
		/* 实名认证未通过 */
		SMS_TYPE_CONTENT.put("50", "启禀小主，您提交的实名认证未通过，请您移步APP查看原因并未重新提交，感谢您的使用【宝贝钱袋】");
		/* 实名认证未通过 */
		SMS_TYPE_CONTENT.put("60", "启禀小主，您提交的商户认证未通过，请您移步APP查看原因并未重新提交，感谢您的使用【宝贝钱袋】");
	}
	/**
	 * 有效期 （分钟）
	 */
	private Long expiresIn;

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

	public String getMsgCode() {
		return msgCode;
	}

	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}

	public Integer getMsgType() {
		return msgType;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public Integer getValidateNum() {
		return validateNum;
	}

	public void setValidateNum(Integer validateNum) {
		this.validateNum = validateNum;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

}