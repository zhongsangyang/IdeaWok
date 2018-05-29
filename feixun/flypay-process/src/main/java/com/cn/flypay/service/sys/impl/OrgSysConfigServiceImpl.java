package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TorgSysConfig;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.OrgSysConfig;
import com.cn.flypay.service.sys.OrgSysConfigService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.utils.StringUtil;

@Service
public class OrgSysConfigServiceImpl implements OrgSysConfigService {

	@Autowired
	private BaseDao<TorgSysConfig> orgSysConfigDao;

	@Autowired
	private OrganizationService organizationService;

	@Override
	public List<OrgSysConfig> dataGrid(OrgSysConfig param, PageFilter ph) {
		List<OrgSysConfig> ul = new ArrayList<OrgSysConfig>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TorgSysConfig t left join t.organization org ";
		List<TorgSysConfig> l = orgSysConfigDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TorgSysConfig t : l) {
			OrgSysConfig u = new OrgSysConfig();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrgName(t.getOrganization().getName());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(OrgSysConfig param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TorgSysConfig t left join t.organization org ";
		return orgSysConfigDao.count("select count(t.id) " + hql + whereHql(param, params), params);
	}

	private String whereHql(OrgSysConfig param, Map<String, Object> params) {
		String hql = "";
		if (param != null) {
			hql += " where 1=1 ";
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

	@CacheEvict(value = { "orgSysConfigCache" }, allEntries = true)
	@Override
	public void edit(OrgSysConfig orgSysConfig) {
		TorgSysConfig t = orgSysConfigDao.get(TorgSysConfig.class, orgSysConfig.getId());
		t.setMsgCfg(orgSysConfig.getMsgCfg().replaceAll("&quot;", "\""));
		t.setJiguangCfg(orgSysConfig.getJiguangCfg().replaceAll("&quot;", "\""));
		orgSysConfigDao.update(t);
	}

	@Override
	public OrgSysConfig get(Long id) {
		TorgSysConfig t = orgSysConfigDao.get("select t from TorgSysConfig t left join t.organization org where t.id=" + id);
		if (t != null) {
			OrgSysConfig u = new OrgSysConfig();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrgName(t.getOrganization().getName());
				u.setAgentId(t.getOrganization().getCode());
			}
			return u;
		}
		return null;
	}

	@Override
	public OrgSysConfig getByOrgId(Long orgId) {
		TorgSysConfig t = orgSysConfigDao.get("select t from TorgSysConfig t left join t.organization org where org.id=" + orgId);
		if (t != null) {
			OrgSysConfig u = new OrgSysConfig();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrgName(t.getOrganization().getName());
				u.setAgentId(t.getOrganization().getCode());
			}
			return u;
		}
		return null;
	}

	@Cacheable(value = "orgSysConfigCache", key = "#agentId+'getMsgConfigJSONObject'")
	@Override
	public JSONObject getMsgConfigJSONObject(String agentId) {

		TorgSysConfig t = orgSysConfigDao.get("select t from TorgSysConfig t  where t.agentId='" + StringUtil.getAgentId(agentId) + "'");
		String msgConfigStr = t.getMsgCfg();
		if (StringUtil.isNotBlank(msgConfigStr)) {
			return JSONObject.parseObject(msgConfigStr);
		}
		return null;
	}

	@Cacheable(value = "orgSysConfigCache", key = "#agentId+'getJiGuangConfigJSONObject'")
	@Override
	public JSONObject getJiGuangConfigJSONObject(String agentId) {
		TorgSysConfig t = orgSysConfigDao.get("select t from TorgSysConfig t  where t.agentId='" + StringUtil.getAgentId(agentId) + "'");
		String jgConfigStr = t.getJiguangCfg();
		if (StringUtil.isNotBlank(jgConfigStr)) {
			return JSONObject.parseObject(jgConfigStr);
		}
		return null;
	}

	@Override
	public void initOrgSysConfig(Torganization t) {
		TorgSysConfig tsc = orgSysConfigDao.get("select t from TorgSysConfig t left join t.organization org where org.id=" + t.getOrganization().getId());
		if (tsc != null) {
			TorgSysConfig ntcs = new TorgSysConfig();
			BeanUtils.copyProperties(tsc, ntcs);
			ntcs.setId(null);
			ntcs.setOrganization(t);
			ntcs.setAgentId(StringUtil.getAgentId(t.getCode()));
			orgSysConfigDao.save(ntcs);
		}
	}
}
