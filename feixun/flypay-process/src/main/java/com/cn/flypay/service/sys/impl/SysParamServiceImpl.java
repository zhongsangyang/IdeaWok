package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TsysParameter;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.SysParameter;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.utils.StringUtil;

@Service
public class SysParamServiceImpl implements SysParamService {

	@Autowired
	private BaseDao<TsysParameter> sysParamDao;

	@Override
	public void add(SysParameter u) {
		TsysParameter t = new TsysParameter();
		BeanUtils.copyProperties(u, t);
		t.setProcessStatus("0");
		t.setCreateTime(new Date());
		sysParamDao.save(t);
	}

	@Override
	public void delete(Long id) {
		TsysParameter t = sysParamDao.get(TsysParameter.class, id);
		t.setProcessStatus("1");
		sysParamDao.update(t);
	}

	// 修改参数后清除缓存
	// @Caching(evict = { @CacheEvict(value = "sysParamCache", key =
	// "'chargeRateConfig'"),
	// @CacheEvict(value = "sysParamCache", key = "'searchSysParameter'") })
	// @CacheEvict(value = "sysParamCache", key = "'chargeRateConfig'")
	@Override 
	public void edit(SysParameter u) {
		TsysParameter t = sysParamDao.get(TsysParameter.class, u.getId());
		System.out.println("u.getId():"+u.getId());
		t.setParaValue(u.getParaValue());
		t.setParaDesc(u.getParaDesc());
		t.setUpdateTime(new Date());
		t.setUpdateUser(u.getUpdateUser());
			
		sysParamDao.update(t);
	}

	@Override
	public SysParameter get(Long id) {
		TsysParameter t = sysParamDao.get(TsysParameter.class, id);
		SysParameter u = new SysParameter();
		BeanUtils.copyProperties(t, u);
		return u;
	}

	@Override
	public List<SysParameter> dataGrid(SysParameter param, PageFilter ph) {
		List<SysParameter> ul = new ArrayList<SysParameter>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TsysParameter t ";
		List<TsysParameter> l = sysParamDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TsysParameter t : l) {
			SysParameter u = new SysParameter();
			BeanUtils.copyProperties(t, u);
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(SysParameter param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TsysParameter t ";
		return sysParamDao.count("select count(*) " + hql + whereHql(param, params), params);
	}

	private String whereHql(SysParameter param, Map<String, Object> params) {
		String hql = "";
		if (param != null) {
			hql += " where 1=1 ";
			if (param.getParaDesc() != null) {
				hql += " and t.paraDesc like :paraDesc";
				params.put("paraDesc", "%%" + param.getParaDesc() + "%%");
			}
			if (param.getParaName() != null) {
				hql += " and t.paraName = :paraName";
				params.put("paraName", param.getParaName());
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
	public SysParameter getByName(String name) {
		TsysParameter t = sysParamDao.get("from TsysParameter t  where t.paraName = '" + name + "'");
		SysParameter u = new SysParameter();
		if (t != null) {
			BeanUtils.copyProperties(t, u);
		} else {
			return null;
		}
		return u;
	}

	@Cacheable(value = "sysParamCache", key = "'chargeRateConfig'")
	public Map<Long, Double> getChargeRateConfig() {
		List<Object[]> l = sysParamDao.findBySql("select para_name,para_value from sys_SYSTEM_PARAMETER where para_name like 'chargeRate_%'");
		Map<Long, Double> sysParamMap = new HashMap<Long, Double>();
		for (Object[] obj : l) {
			String typeStr = (String) obj[0];
			sysParamMap.put(Long.parseLong(typeStr.substring(11)), Double.parseDouble((String) obj[1]));
		}
		return sysParamMap;
	}

	@Cacheable(value = "sysParamCache", key = "'getMaxTaskNumber'")
	public Integer getMaxTaskNumber() {
		TsysParameter sp = sysParamDao.get(TsysParameter.class, 1l);
		if (sp != null && !StringUtil.isNullOrEmpty(sp.getParaValue())) {
			return Integer.parseInt(sp.getParaValue());
		}
		return 100;
	}

//	@Cacheable(value = "sysParamCache", key = "'searchSysParameter'")
	@Override
	public Map<String, String> searchSysParameter() {
		Map<String, String> ul = new HashMap<String, String>();
		String hql = " from TsysParameter t where t.processStatus=0";
		List<TsysParameter> l = sysParamDao.find(hql);
		for (TsysParameter t : l) {
			ul.put(t.getParaName(), t.getParaValue());
		}
		return ul;
	}

	@Override
	public boolean getThrough(String code) {
		String hql = " from TsysParameter t where t.processStatus=0 and t.paraName ='" + code + "'";
		TsysParameter tsysParameter = sysParamDao.get(hql);
		if (tsysParameter.getParaValue().equals("1")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean getD0YINLIAN() {
		String hql = " from TsysParameter t where t.processStatus=0 and t.paraName ='createPay_YILIAND0_seift_on'";
		TsysParameter tsysParameter = sysParamDao.get(hql);
		if (tsysParameter.getParaValue().equals("1")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean getTiXian() {
		String hql = " from TsysParameter t where t.processStatus=0 and t.paraName ='createPay_TiXian_seift_on'";
		TsysParameter tsysParameter = sysParamDao.get(hql);
		if (tsysParameter.getParaValue().equals("1")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean getSwitch(String code) {
		String hql = " from TsysParameter t where t.processStatus=0 and t.paraName ='" + code + "'";
		TsysParameter tsysParameter = sysParamDao.get(hql);
		if (tsysParameter.getParaValue().equals("0")) {
			return true;
		}
		return false;
	}

	@Override
	public String getSwitchSUM(String code) {
		String hql = " from TsysParameter t where t.processStatus=0 and t.paraName ='" + code + "'";
		TsysParameter tsysParameter = sysParamDao.get(hql);
		return tsysParameter.getParaValue();
	}

}
