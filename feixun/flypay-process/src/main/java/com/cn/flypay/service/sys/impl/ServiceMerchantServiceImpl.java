package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TserviceMerchant;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.service.sys.ServiceMerchantService;

@Service
public class ServiceMerchantServiceImpl implements ServiceMerchantService {

	@Autowired
	private BaseDao<TserviceMerchant> serviceMerchantDao;

	@Override
	public List<ServiceMerchant> dataGrid(ServiceMerchant param, PageFilter ph) {
		List<ServiceMerchant> ul = new ArrayList<ServiceMerchant>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TserviceMerchant t ";
		List<TserviceMerchant> l = serviceMerchantDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TserviceMerchant t : l) {
			ServiceMerchant u = new ServiceMerchant();
			BeanUtils.copyProperties(t, u);
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(ServiceMerchant param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select count(t.id)   from TserviceMerchant t  ";
		return serviceMerchantDao.count(hql + whereHql(param, params), params);
	}

	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}

	private String whereHql(ServiceMerchant infoList, Map<String, Object> params) {
		String hql = "";
		if (infoList != null) {
			hql += " where 1=1 ";
			if (infoList.getStatus() != null) {
				hql += " and t.status=:status ";
				params.put("status", infoList.getStatus());
			}
		}
		return hql;
	}

	@Override
	public List<ServiceMerchant> findAllServiceMerchant() {
		List<TserviceMerchant> ls = serviceMerchantDao.find("select t from TserviceMerchant t where t.status=1");
		List<ServiceMerchant> sml = new ArrayList<ServiceMerchant>();
		for (TserviceMerchant t : ls) {
			ServiceMerchant sm = new ServiceMerchant();
			BeanUtils.copyProperties(t, sm);
			sml.add(sm);
		}
		return sml;
	}

	@Override
	public ServiceMerchant findServiceMerchant(String serviceName) {
		List<TserviceMerchant> ls = serviceMerchantDao.find("select t from TserviceMerchant t where t.status=1 and t.name='" + serviceName + "'");
		if (ls == null || ls.size() < 1) {
			return null;
		}
		TserviceMerchant s = ls.get(0);
		ServiceMerchant sm = new ServiceMerchant();
		BeanUtils.copyProperties(s, sm);
		return sm;
	}

	@Override
	public List<ServiceMerchant> findAllServiceMerchant(String serviceName) {
		List<TserviceMerchant> ls = serviceMerchantDao.find("select t from TserviceMerchant t where t.status=1 and t.name='" + serviceName + "'");
		List<ServiceMerchant> sml = new ArrayList<ServiceMerchant>();
		for (TserviceMerchant t : ls) {
			ServiceMerchant sm = new ServiceMerchant();
			BeanUtils.copyProperties(t, sm);
			sml.add(sm);
		}
		return sml;
	}

	// @Cacheable(value = "serviceMerchantCache", key =
	// "#serviceMerchantId+'getTserviceMerchant'")
	@Override
	public TserviceMerchant getTserviceMerchant(Long serviceMerchantId) {
		return serviceMerchantDao.get("select t from TserviceMerchant t where t.id=" + serviceMerchantId);
	}

	@Override
	public void editServiceMerchantStatus(Long id) {
		TserviceMerchant t = serviceMerchantDao.get(TserviceMerchant.class, id);
		if (t.getStatus() == 0) {
			t.setStatus(1);
		} else {
			t.setStatus(0);
		}
		serviceMerchantDao.update(t);
	}
}
