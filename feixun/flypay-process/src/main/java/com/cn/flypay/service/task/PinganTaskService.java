package com.cn.flypay.service.task;

public interface PinganTaskService {

	public void searchOrderToShenfu();

	/**
	 * 1.发起T1订单列表给平安
	 */
	public void dealT1OrderToPingan();

	/**
	 * 2.发起批量代付指令
	 */
	public void dealT1OrderSendBatchDealToPingan();

	/**
	 * 3.发起批量代付的查询指令，若已处理完成，系统自动下载处理的结果
	 */
	public void dealT1OrderSendBatchSearchToPingan();

	/**
	 * 4.处理平安给出的T1回馈结果
	 */
	public void dealT1Result();

	/**
	 * 1.下载对账单
	 */
	public void dealDownLoadStatement();

	/**
	 * 2.处理每天的对账文件
	 */
	public void dealStatement();

	/**
	 * 实时异步查询未出代付结果的订单
	 */
	void dealSearchOrderToPingan();

	/**
	 * 实时异步查询未出代付结果的订单长时间
	 */
	void dealSearchOrderToPinganLong();
}
