package com.cn.flypay.pageModel.payment.minsheng;

/**
 * 撤销交易
 * 
 * @author sunyue
 * 
 */
public class SMZF005 extends CommonSMZF {
	public static String operate_name = "SMZF005";

	private String oriReqMsgId;
	private String settleDate;
	private String isClearOrCancel;
	private String extend1;
	private String extend2;
	private String extend3;

	public String getOriReqMsgId() {
		return oriReqMsgId;
	}

	public void setOriReqMsgId(String oriReqMsgId) {
		this.oriReqMsgId = oriReqMsgId;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public String getIsClearOrCancel() {
		return isClearOrCancel;
	}

	public void setIsClearOrCancel(String isClearOrCancel) {
		this.isClearOrCancel = isClearOrCancel;
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
