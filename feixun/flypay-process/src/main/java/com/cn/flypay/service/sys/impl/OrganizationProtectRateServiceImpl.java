package com.cn.flypay.service.sys.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TOrganizationProtectRate;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.sys.OrganizationProtectRate;
import com.cn.flypay.service.sys.OrganizationProtectRateService;
/**
 * 运营商保本费率相关
 * @author liangchao
 *
 */
@Service
public class OrganizationProtectRateServiceImpl implements OrganizationProtectRateService{

	@Autowired
	private BaseDao<TOrganizationProtectRate> protectRateDao;
	@Autowired
	private BaseDao<Torganization> organizationDao;
	
	@Override
	public void add(OrganizationProtectRate r) {
		TOrganizationProtectRate t = new TOrganizationProtectRate();
		BeanUtils.copyProperties(r, t);
		t.setOrganization(organizationDao.get(Torganization.class,r.getOrganizationId()));
		protectRateDao.save(t);
	}

	@Override
	public TOrganizationProtectRate getTOrganizationProtectRate(Long id,String type) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("type", type);
		return protectRateDao.get("select t from TOrganizationProtectRate t left join t.organization g where g.id = :id and t.type = :type", params);
	}
	
}
