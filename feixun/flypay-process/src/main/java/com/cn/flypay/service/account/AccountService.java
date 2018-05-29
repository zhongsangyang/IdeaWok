package com.cn.flypay.service.account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;

import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.account.Account;
import com.cn.flypay.pageModel.account.AccountAdjust;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.UserOrder;

public interface AccountService {

	public List<Account> dataGrid(Account param, PageFilter ph);

	public List<Account> searchAccount();

	public Long count(Account param, PageFilter ph);

	public Taccount freeze(Account account);

	public void freeze(Long accountId);

	public Account getAccountByUserId(long userId);

	public Taccount getAccountByUserIdTwo(long userId);

	/**
	 * 用户提现（T0/T1）
	 * 
	 * @param userId
	 * @param liqType
	 * @param orgAmt
	 * @return
	 */
	public String updateAccountBeforeLiq(Long userId, String liqType, BigDecimal orgAmt);

	/**
	 * 提现失败后对账户的处理
	 * 
	 * @param userId
	 * @param order
	 * @return
	 */
	public String updateAccountAfterLiqFailure(Long userId, TuserOrder order);

	/**
	 * 提现成功后对账户的处理
	 * 
	 * @param userId
	 * @param order
	 */
	public String updateAccountAfterLiqSuccess(Long userId, TuserOrder order);

	/**
	 * 对账时出现异常，冻结改账户
	 * 
	 * @param userIds
	 * @return
	 */
	public String updateAccountWhenAccountException(Set<Long> userIds);

	public String updateBrokerageAccountWhenAccountException(Set<Long> userIds);

	/**
	 * 用户提现（T0/T1）
	 * 
	 * @param userId
	 * @param liqType
	 * @param amt
	 * @return
	 */
	public String updateBrokerageAccountBeforeLiq(Long userId, Double amt);

	/**
	 * 佣金提现单笔成功
	 * 
	 * @param userId
	 * @param order
	 */
	public String updateBrokerageAccountAfterLiqSuccess(Long userId, TuserOrder order);

	/**
	 * 佣金提现单笔失败
	 * 
	 * @param userId
	 * @param order
	 */
	public String updateBrokerageAccountAfterLiqFailure(Long userId, TuserOrder order);

	/**
	 * 获得账户
	 * 
	 * @param accountId
	 * @return
	 */
	public Account get(Long accountId);

	/**
	 * 页面上完成调账功能
	 * 
	 * @param user
	 * @param adjust
	 */
	public void dealAdjustAccount(User user, AccountAdjust adjust);

	/**
	 * 收款成功后，账户中添加 金额
	 * 
	 * @param id
	 * @param orgAmt
	 * @param string
	 * @throws Exception
	 */
	public Taccount updateIncreaseAccountAfterSuccessInfo(String ordernum, Long id, Integer inputAccType, BigDecimal orgAmt, String desc) throws Exception;

	/**
	 * 直通车收款成功后，账户减去进行中金额
	 * 
	 * @param id
	 * @param orgAmt
	 * @param desc
	 * @return
	 * @throws Exception
	 */
	public Taccount updateAccountTroughInfo(Long id, BigDecimal orgAmt, String desc) throws Exception;

	/**
	 * 每个月清理的账户月收入和月支出
	 */
	public void dealCleanPerMonthAccount();

	/**
	 * 将昨日账户金额移动至历史金额中，将可用金额+T1至T5的金额的和 移动至昨日金额中，将今日提现额度归0；
	 */
	public void updateAllUserAccount();

	/**
	 * 工作日期间移动T5---》T1--》acct
	 */
	public void updateUserT5Account();

	void editRemark(Account param);

	/**
	 * 更新T1账户到可用金额中
	 * 
	 * @see 分两种情况，人工更新，系统自动更新，但是必须都要在更新T5之前，保证T1清空后，T2资金才在更新T5账户时进入T1
	 */
	void updateUserT1AccountToAvl();

	void updateUserT1AccountToAvlTwo();

	public void addAccountLogProfit();

	public void downloadAccount() throws Exception;

	public void downloadBrokerage();
   
	public Workbook exportExcel(Account account);
}
