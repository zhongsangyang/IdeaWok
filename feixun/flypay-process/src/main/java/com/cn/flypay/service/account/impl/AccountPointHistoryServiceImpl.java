package com.cn.flypay.service.account.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.TaccountPointHistory;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.account.AccountPointHistory;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.service.account.AccountPointHistoryService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.utils.StringUtil;

@Service
public class AccountPointHistoryServiceImpl implements AccountPointHistoryService {
	@Autowired
	private BaseDao<TaccountPointHistory> accountPointHistoryDao;
	@Autowired
	private OrganizationService organizationService;

	@Override
	public List<AccountPointHistory> dataGrid(AccountPointHistory param, PageFilter ph) {

		List<AccountPointHistory> ul = new ArrayList<AccountPointHistory>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TaccountPointHistory t left join t.accountPoint c left join c.user u left join u.organization tog ";
		List<TaccountPointHistory> l = accountPointHistoryDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TaccountPointHistory t : l) {
			Tuser user = t.getAccountPoint().getUser();
			if (user != null) {
				AccountPointHistory al = new AccountPointHistory(user.getRealName(), user.getLoginName(), t.getCdType(), t.getPoint(), t.getDescription(), t.getCreateTime());
				if (user.getOrganization() != null) {
					al.setOrganizationName(user.getOrganization().getName());
				}
				ul.add(al);
			}
		}
		return ul;
	}

	@Override
	public Long count(AccountPointHistory param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "  from TaccountPointHistory t left join t.accountPoint c left join c.user u left join u.organization tog ";
		return accountPointHistoryDao.count("select count(t.id) " + hql + whereHql(param, params), params);
	}

	private String whereHql(AccountPointHistory accLog, Map<String, Object> params) {
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
	public void addPointHistory(Long userId, String cdType, Long addPoint, String desc) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("addPoint", addPoint);
		params.put("cdType", cdType);
		params.put("createTime", new Date());
		params.put("desc", desc);
		String sql = "insert into account_point_history(version,point_id,cd_type,point,create_time,description)  select 0,id,:cdType,:addPoint,:createTime,:desc from account_point where user_id=:userId";
		accountPointHistoryDao.executeSql(sql, params);
	}
}
