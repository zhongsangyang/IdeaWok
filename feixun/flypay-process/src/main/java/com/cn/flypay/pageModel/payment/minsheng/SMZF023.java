package com.cn.flypay.pageModel.payment.minsheng;

/**
 * 商户提现接口
 * 
 * @author sunyue
 * 
 */

public class SMZF023 extends CommonSMZF {
	public static String operate_name = "SMZF023";
	/** 实际提现金额 */
	private String drawAmount;
	/** 提现手续费 */
	private String drawFee;
	/** 交易手续费 */
	private String tradeFee;
	/** 合作方请求流水 */
	private String reqMsgId;
	/** 对账日期 */
	private String settleDate;
	/** 备用域1 */
	private String extend1;
	/** 备用域2 */
	private String extend2;
	/** 备用域3 */
	private String extend3;

	public String getDrawAmount() {
		return drawAmount;
	}

	public void setDrawAmount(String drawAmount) {
		this.drawAmount = drawAmount;
	}

	public String getDrawFee() {
		return drawFee;
	}

	public void setDrawFee(String drawFee) {
		this.drawFee = drawFee;
	}

	public String getTradeFee() {
		return tradeFee;
	}

	public void setTradeFee(String tradeFee) {
		this.tradeFee = tradeFee;
	}

	public String getReqMsgId() {
		return reqMsgId;
	}

	public void setReqMsgId(String reqMsgId) {
		this.reqMsgId = reqMsgId;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
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
