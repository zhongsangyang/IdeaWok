package com.cn.flypay.pageModel.payment.pingan;

/**
 * 商户绑卡
 * 
 * @author sunyue
 * 
 */
public class FshowsLiquidationSubmerchantBankBindRequest  {
	private String sub_merchant_id;
	private String bank_card_no;
	private String card_holder;

	public String getSub_merchant_id() {
		return sub_merchant_id;
	}

	public void setSub_merchant_id(String sub_merchant_id) {
		this.sub_merchant_id = sub_merchant_id;
	}

	public String getBank_card_no() {
		return bank_card_no;
	}

	public void setBank_card_no(String bank_card_no) {
		this.bank_card_no = bank_card_no;
	}

	public String getCard_holder() {
		return card_holder;
	}

	public void setCard_holder(String card_holder) {
		this.card_holder = card_holder;
	}

}