package com.cn.flypay.service.task;

public interface StatementTaskService {

	/**
	 * 下载微信对账单，并且完成对账
	 */
	void dealWeixinStatement();

	/**
	 * 下载支付宝对账单，并且完成对账
	 */
	void dealAlipayStatement();

	/**
	 * 下载民生对账单，并且完成对账
	 */
	void dealMinshengStatement();

	void dealProcessingZanShanFuOrderBeforeTwoDays();

	void dealProcessingWeixinOrderBeforeTwoDays();

	void dealProcessingZhifubaoOrderBeforeTwoDays();

	void dealPinganPayStatement();

	void dealProcessingYinLianOrderBeforeTwoDays();

	void dealProcessingYiZhifuOrderBeforeTwoDays();

	void dealProcessingBaiduOrderBeforeTwoDays();

	void dealProcessingJingdongOrderBeforeTwoDays();

}
