package com.cn.flypay.service.trans.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.trans.TorgBrokerage;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.OrgBrokerage;
import com.cn.flypay.service.trans.OrgBrokerageService;
import com.cn.flypay.utils.StringUtil;

@Service
public class OrgBrokerageServiceImpl implements OrgBrokerageService {

	@Autowired
	private BaseDao<TorgBrokerage> appTransInfoDao;

	@Override
	public List<OrgBrokerage> dataGrid(OrgBrokerage app, PageFilter ph) {
		List<OrgBrokerage> ul = new ArrayList<OrgBrokerage>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TorgBrokerage t left join t.organization tuo";
		List<TorgBrokerage> l = appTransInfoDao.find(hql + whereHql(app, params) + orderHql(ph), params, ph.getPage(),
				ph.getRows());
		for (TorgBrokerage t : l) {
			OrgBrokerage u = new OrgBrokerage();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrganizationName(t.getOrganization().getName());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(OrgBrokerage app, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TorgBrokerage t left join t.organization tuo";
		return appTransInfoDao.count("select count(t) " + hql + whereHql(app, params), params);
	}

	private String whereHql(OrgBrokerage app, Map<String, Object> params) {
		String hql = "";
		if (app != null) {
			hql += " where 1=1 ";
			if (StringUtil.isNotBlank(app.getOrganizationName())) {
				hql += " and tuo.name like :organizationName";
				params.put("organizationName", "%%" + app.getOrganizationName() + "%%");
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

}
