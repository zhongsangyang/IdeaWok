package com.cn.flypay.pageModel.payment.minsheng;

/**
 * 获取对账文件
 * 
 * @author sunyue
 * 
 */

public class SMZF020 extends CommonSMZF {
	public static String operate_name = "SMZF020";
	/** 对账日期 必填项 */
	private String settleDate;
	/**
	 * 文件返回类型 必填项 1.响应流同步返回对账 2.以文件链接返回，由合作方获取该链接下载（当前还未支持）
	 * 
	 * */
	private String fileType;
	/**
	 * 内容 如果fileType=1存放对账文件流 如果fileType=2存放对账文件下载的地址
	 * 
	 * */
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
