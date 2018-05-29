package com.cn.flypay.service.account;

import java.math.BigDecimal;
import java.util.List;

import com.cn.flypay.pageModel.account.OrgAccount;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.User;

public interface OrgAccountService {

	public List<OrgAccount> dataGrid(OrgAccount param, PageFilter ph);

	public Long count(OrgAccount param, PageFilter ph);

	public OrgAccount get(Long id);

	public void edit(OrgAccount param);

	public String adjustOrgAccount(Long id, Double amt, User user, String desc);
	
	/**
	 * 对运营商账户的消费
	 * 
	 * @param accountType
	 * @param amt
	 * @param agentId
	 * @param consumerId
	 * @return
	 */
	public String isAllowConsumeOrgAccount(Integer accountType, BigDecimal amt, String agentId);

	public String updateOrgAccountAfterConsumeSuccess(Integer accountType, BigDecimal amt, String agentId, Long consumerId);

}
