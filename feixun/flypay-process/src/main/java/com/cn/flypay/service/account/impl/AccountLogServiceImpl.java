package com.cn.flypay.service.account.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.account.TaccountLog;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.account.Account;
import com.cn.flypay.pageModel.account.AccountLog;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.service.account.AccountLogService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.utils.StringUtil;

@Service
public class AccountLogServiceImpl implements AccountLogService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BaseDao<Taccount> accountDao;
	@Autowired
	private BaseDao<TaccountLog> accountLogDao;
	@Autowired
	private OrganizationService organizationService;

	@Override
	public List<AccountLog> dataGrid(AccountLog param, PageFilter ph) {
		List<AccountLog> ul = new ArrayList<AccountLog>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TaccountLog t left join t.account c left join c.user u left join u.organization tog ";
		List<TaccountLog> l = accountLogDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TaccountLog t : l) {
			Tuser user = t.getAccount().getUser();
			if (user != null) {
				AccountLog al = new AccountLog(user.getLoginName(), user.getRealName(), t.getType(), t.getAmt(), t.getAvlAmt(), t.getLockOutAmt(), t.getCreateTime(), t.getDescription());
				if (user.getOrganization() != null) {
					al.setOrganizationName(user.getOrganization().getName());
				}
				ul.add(al);
			}
		}
		return ul;
	}

	private String whereHql(AccountLog accLog, Map<String, Object> params) {
		String hql = "";
		if (accLog != null) {
			hql += " where 1=1 ";
			if (StringUtil.isNotBlank(accLog.getLoginName())) {
				hql += " and u.loginName=:loginName ";
				params.put("loginName", accLog.getLoginName());
			}
			if (accLog.getOperateUser() != null) {

				hql += " and  tog.id in(:operaterOrgIds)";
				params.put("operaterOrgIds", organizationService.getOwerOrgIds(accLog.getOperateUser().getOrganizationId()));
			}
		}
		return hql;
	}

	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}

	@Override
	public Long count(AccountLog param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TaccountLog t left join t.account c left join c.user u left join u.organization tog ";
		return accountLogDao.count("select count(t.id) " + hql + whereHql(param, params), params);
	}

}
