package com.cn.flypay.service.statement;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cn.flypay.model.sys.TfinanceProfit;
import com.cn.flypay.pageModel.account.FinanceProfit;
import com.cn.flypay.pageModel.base.PageFilter;

public interface FinanceStatementService {

	public List<FinanceProfit> dataGrid(FinanceProfit financeProfit, PageFilter ph);

	public Long count(FinanceProfit financeProfit, PageFilter ph);

	/**
	 * 每日定时统计系统的入账和出账，保证收支平衡
	 * 
	 * @return
	 */
	TfinanceProfit saveFinanceProfit();

	/**
	 * 
	 * @param date
	 * @return
	 */
	TfinanceProfit saveFinanceProfit(Date date);
	
	TfinanceProfit saveFinanceProfitV2(Date date);

	public BigDecimal getOrgAmtSum(Map<String, Object> params);

}
