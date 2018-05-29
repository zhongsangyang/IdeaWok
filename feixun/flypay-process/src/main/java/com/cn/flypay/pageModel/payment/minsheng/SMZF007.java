package com.cn.flypay.pageModel.payment.minsheng;

/**
 * 商户查询
 * 
 * @author sunyue
 * 
 */
public class SMZF007 extends CommonSMZF {
	public static String operate_name = "SMZF007";
	/** 合作方商户编码 必填项 */
	private String merchantId;
	/** 银行商户编码 */
	private String merchantCode;
	/** 原交易应答类型 */
	private String oriRespType;
	/** 原交易应答码 */
	private String oriRespCode;
	/** 原交易应答描述 */
	private String oriRespMsg;
	/** 支付通道 */
	private String payWay;
	/** 商户名称 */
	private String merchantName;
	/** 商户简称 */
	private String shortName;
	/** 商户地址 */
	private String merchantAddress;
	/** 客服电话 */
	private String servicePhone;
	/** 组织机构代码 */
	private String orgCode;
	/** 联系人名词 */
	private String contactName;
	/** 联系人电话 */
	private String contactPhone;
	/** 联系人手机号 */
	private String contactMobile;
	/** 联系人邮箱 */
	private String contactEmail;
	/** 经营类目 */
	private String category;
	/** 备注说明 */
	private String remark;
	/** 状态 */
	private String status;
	/** 备用域1 */
	private String extend1;
	/** 备用域2 */
	private String extend2;
	/** 备用域3 */
	private String extend3;

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getOriRespType() {
		return oriRespType;
	}

	public void setOriRespType(String oriRespType) {
		this.oriRespType = oriRespType;
	}

	public String getOriRespCode() {
		return oriRespCode;
	}

	public void setOriRespCode(String oriRespCode) {
		this.oriRespCode = oriRespCode;
	}

	public String getOriRespMsg() {
		return oriRespMsg;
	}

	public void setOriRespMsg(String oriRespMsg) {
		this.oriRespMsg = oriRespMsg;
	}

	public String getPayWay() {
		return payWay;
	}

	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getMerchantAddress() {
		return merchantAddress;
	}

	public void setMerchantAddress(String merchantAddress) {
		this.merchantAddress = merchantAddress;
	}

	public String getServicePhone() {
		return servicePhone;
	}

	public void setServicePhone(String servicePhone) {
		this.servicePhone = servicePhone;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExtend1() {
		return extend1;
	}

	public void setExtend1(String extend1) {
		this.extend1 = extend1;
	}

	public String getExtend2() {
		return extend2;
	}

	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}

	public String getExtend3() {
		return extend3;
	}

	public void setExtend3(String extend3) {
		this.extend3 = extend3;
	}

	@Override
	public String getOperateName() {
		return operate_name;
	}
}
