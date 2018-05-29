package com.cn.flypay.service.task;

public interface UserTaskService {

	void dealUpdateUserAuthErrorNum();

	void dealCleanPerMonthAccount();

	void updateBrokerageAccount();

	void updateAllUserAccount();

	void updateFinanceProfitAccount();

	void dealCleanChannelTodayAmt();

	/**
	 * T5资金逐工作日步进
	 */
	void updateUserT5Account();

	/**
	 * 更新T1账户到可用金额中
	 * 
	 * @see 分两种情况，人工更新，系统自动更新，但是必须都要在更新T5之前，保证T1清空后，T2资金才在更新T5账户时进入T1
	 */
	void updateUserT1AccountToAvl();

	void updateThroughChannel();

	void addAccountLogProfit();

	void updateThroughChannelPN();

	/**
	 * 生成用户的账户余额
	 */
	void downloadUserAccount();

	/**
	 * 生成用户的佣金信息
	 */
	void downloadUserBrokerage();

}
