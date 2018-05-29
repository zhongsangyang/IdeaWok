package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tbusiness;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Business;
import com.cn.flypay.service.sys.BusinessService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.utils.StringUtil;

@Service
public class BusinessServiceImpl implements BusinessService {

	@Autowired
	private BaseDao<Tbusiness> businessDao;
	@Autowired
	private OrganizationService organizationService;

	@Override
	public List<Business> dataGrid(Business param, PageFilter ph) {
		List<Business> ul = new ArrayList<Business>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from Tbusiness t left join t.organization org ";
		List<Tbusiness> l = businessDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (Tbusiness t : l) {
			Business u = new Business();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrganizationName(t.getOrganization().getName());
				u.setOrganizationAppName(t.getOrganization().getAppName());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(Business param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from Tbusiness t left join t.organization org ";
		return businessDao.count("select count(*) " + hql + whereHql(param, params), params);
	}

	private String whereHql(Business param, Map<String, Object> params) {
		String hql = "";
		if (param != null) {
			hql += " where 1=1 ";
			if (param.getOperateUser() != null) {
				hql += " and  org.id in(:operaterOrgIds)";
				params.put("operaterOrgIds", organizationService.getOwerOrgIds(param.getOperateUser().getOrganizationId()));
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
	public void add(Business param) {
		Tbusiness t = new Tbusiness();
		BeanUtils.copyProperties(param, t);
		if (StringUtil.isNotBlank(param.getAgentId())) {
			t.setOrganization(organizationService.getTorganizationInCacheByCode(param.getAgentId()));
		}
		businessDao.save(t);
	}

	@Override
	public void delete(Business id) {

	}

	@Override
	public void edit(Business infoList) {
		Tbusiness t = businessDao.get(Tbusiness.class, infoList.getId());

		businessDao.update(t);
	}
}
