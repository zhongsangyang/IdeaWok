package com.cn.flypay.service.statement;

import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.cn.flypay.pageModel.account.AgentFinanceProfit;
import com.cn.flypay.pageModel.account.FinanceProfit;
import com.cn.flypay.pageModel.account.OrgFinanceProfit;
import com.cn.flypay.pageModel.account.PlatformChannelProfit;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.FinanceAccount;

public interface FinanceService {

	/**
	 * 系统实时账户信息
	 * 
	 * @param financeAccount
	 * @param ph
	 * @return
	 */
	public List<FinanceAccount> findRealTimeAccount();

	/**
	 * 查询公司收益
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	FinanceProfit findFinanceProfitByDateInterval(Date startDate, Date endDate);

	/**
	 * 运营商收益
	 * 
	 * @param startDate
	 * @param endDate
	 * @param operator
	 * @return
	 */
	public List<OrgFinanceProfit> findFinanceProfitListByDateInterval(Date startDate, Date endDate, User operator);

	/**
	 * 平台收益，因数据量比较多，只提供导出
	 * 
	 * @category 暂停使用
	 * @param startDate
	 * @param endDate
	 * @param operator
	 * @return
	 */
	public List<PlatformChannelProfit> findPlatformChannelProfitListByDateInterval(Date startDate, Date endDate, User operator);

	/**
	 * 导出 平台收益
	 * 
	 * @param startDate
	 * @param endDate
	 * @param operator
	 * @return
	 */
	Workbook exportPlatformChannelProfitListByDateInterval(Date startDate, Date endDate, User operator);

	/**
	 * 计算代理商收益
	 * 
	 * @param startDate
	 * @param endDate
	 * @param operator
	 * @return
	 */
	List<AgentFinanceProfit> findAgentFinanceProfitListByDateInterval(Date startDate, Date endDate, User operator);

	public Workbook exportAgentProfitListByDateInterval(Date startDate, Date endDate, User user);
	
	
	/**
	 * 民生对账单下载
	 * 
	 * @param startDate
	 * @param endDate
	 * @param operator
	 * @return
	 */
	public Workbook getMinShendowoan(String statemtentDate) throws Exception;
	
	
	/**
	 * 民生对账单下载
	 * 
	 * @param startDate
	 * @param endDate
	 * @param operator
	 * @return
	 */
	public Workbook getShenFudowoan(String statemtentDate) throws Exception;
	
	
	/**
	 * 每日盈利下载
	 * 
	 * @param startDate
	 * @param endDate
	 * @param operator
	 * @return
	 */
	public Workbook exportDailyProfitDowoan(String statemtentDate) throws Exception;
	
	
	
	/**
	 * 盈利明细下载
	 * 
	 * @param startDate
	 * @param endDate
	 * @param operator
	 * @return
	 */
	public Workbook exportProfitDetaildowoan(String statemtentDate) throws Exception;

	/**
	 * 获取平安对账单
	 * @param statemtentDate
	 * @return
	 */
	public List<String> findPinganStatement(String statemtentDate);
	
	
	

}
