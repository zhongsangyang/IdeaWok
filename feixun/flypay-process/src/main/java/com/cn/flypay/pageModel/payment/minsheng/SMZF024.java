package com.cn.flypay.pageModel.payment.minsheng;

/**
 * 商户提现接口
 * 
 * @author sunyue
 * 
 */

public class SMZF024 extends CommonSMZF {
	public static String operate_name = "SMZF024";
	/** 对账日期 */
	private String settleDate;
	/** 文件返回类型 */
	private String fileType;
	/** 内容 */
	private String content;
	/** 备注说明 */
	private String remark;
	/** 备用域1 */
	private String extend1;
	/** 备用域2 */
	private String extend2;
	/** 备用域3 */
	private String extend3;

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
