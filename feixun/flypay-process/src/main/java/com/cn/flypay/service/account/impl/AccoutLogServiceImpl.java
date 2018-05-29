package com.cn.flypay.service.account.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.account.TaccountLog;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.account.AccountLog;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.service.account.AccoutLogService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.utils.StringUtil;

@Service
public class AccoutLogServiceImpl implements AccoutLogService {
	
	
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
		List<TaccountLog> l = accountLogDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(),
				ph.getRows());
		for (TaccountLog t : l) {
			Tuser user = t.getAccount().getUser();
			if (user != null) {
				AccountLog al = new AccountLog(user.getLoginName(), user.getRealName(), t.getType(), t.getAmt(),
						t.getCreateTime(), t.getDescription());
				if (user.getOrganization() != null) {
					al.setOrganizationName(user.getOrganization().getName());
				}
				ul.add(al);
			}
		}
		return ul;
	}

	@Override
	public void editadjust(String param, String mon) {
		Taccount t = accountDao.get("select t from Taccount t left join t.user u where u.loginName= '"+param+"'");
		t.setAvlAmt(t.getAvlAmt().add(new BigDecimal(mon)));
		accountDao.update(t);
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
				params.put("operaterOrgIds",
						organizationService.getOwerOrgIds(accLog.getOperateUser().getOrganizationId()));
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
