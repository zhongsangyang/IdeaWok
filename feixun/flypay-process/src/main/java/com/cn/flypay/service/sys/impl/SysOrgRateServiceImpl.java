package com.cn.flypay.service.sys.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TsysOrgRate;
import com.cn.flypay.service.sys.SysOrgRateService;




@Service
public class SysOrgRateServiceImpl implements SysOrgRateService {
	
	
	@Autowired
	private BaseDao<TsysOrgRate> sysorgrateDao;

	
	@Override
	public TsysOrgRate getSysOrgRate(String agentId, String orgType) {
		
		String hql = "select t from TsysOrgRate t left join t.organization g where g.code =:agentId and t.orgType =:orgType";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("agentId", agentId);
		params.put("orgType", orgType);
		TsysOrgRate org = sysorgrateDao.get(hql,params);
		return org;
	}

}
