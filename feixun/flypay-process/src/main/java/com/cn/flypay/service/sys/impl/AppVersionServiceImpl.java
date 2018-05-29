package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TappVersion;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.AppVersion;
import com.cn.flypay.service.sys.AppVersionService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.utils.StringUtil;

@Service
public class AppVersionServiceImpl implements AppVersionService {

	@Autowired
	private BaseDao<TappVersion> appVersionDao;
	@Autowired
	private OrganizationService organizationService;

	@Override
	public List<AppVersion> dataGrid(AppVersion param, PageFilter ph) {
		List<AppVersion> ul = new ArrayList<AppVersion>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TappVersion t left join t.organization org ";
		List<TappVersion> l = appVersionDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TappVersion t : l) {
			AppVersion u = new AppVersion();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setAppName(t.getOrganization().getAppName());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(AppVersion param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TappVersion t left join t.organization org ";
		return appVersionDao.count("select count(t.id) " + hql + whereHql(param, params), params);
	}

	private String whereHql(AppVersion param, Map<String, Object> params) {
		String hql = "";
		if (param != null) {
			hql += " where 1=1 ";
			if (StringUtil.isNotBlank(param.getAgentId())) {
				hql += " and org.code like :orgCode ";
				params.put("orgCode", StringUtil.getAgentId(param.getAgentId()) + "%");
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
	public void add(AppVersion param) {
		TappVersion t = new TappVersion();
		BeanUtils.copyProperties(param, t);
		t.setOrganization(organizationService.getTorganizationInCacheById(param.getOrganizationId()));
		t.setCreateTime(new Date());
		appVersionDao.save(t);
	}

	@Override
	public void delete(Long id) {

	}

	@Override
	public void edit(AppVersion appversion) {
		TappVersion t = appVersionDao.get(TappVersion.class, appversion.getId());
		t.setAppType(appversion.getAppType());
		t.setContent(appversion.getContent());
		t.setStatus(appversion.getStatus());
		t.setUpdateUrl(appversion.getUpdateUrl());
		t.setVersionName(appversion.getVersionName());
		t.setDownloadNet(appversion.getDownloadNet());
		t.setIsForce(appversion.getIsForce());
		appVersionDao.update(t);
	}

	@Override
	public AppVersion getNewestAppVersion(String appType, String agentId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", 1);
		params.put("appType", appType);
		params.put("orgCode", StringUtil.getAgentId(agentId) );
		List<TappVersion> ts = appVersionDao.find(
				"select t from TappVersion t left join t.organization g where t.status=:status and t.appType=:appType and g.code= :orgCode order by t.createTime desc", params);
		for (TappVersion t : ts) {
			AppVersion av = new AppVersion();
			BeanUtils.copyProperties(t, av);

			return av;
		}
		return null;
	}

	@Override
	public AppVersion get(Long id) {
		TappVersion t = appVersionDao.get(TappVersion.class, id);
		if (t != null) {
			AppVersion av = new AppVersion();
			BeanUtils.copyProperties(t, av);
			return av;
		}
		return null;
	}

	@Override
	public AppVersion getNewestApp(String appType, String agentId,String versionId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("versionName", versionId);
		params.put("appType", appType);
		params.put("orgCode", StringUtil.getAgentId(agentId) );
		List<TappVersion> ts = appVersionDao.find(
				"select t from TappVersion t left join t.organization g where t.versionName=:versionName and t.appType=:appType and g.code= :orgCode order by t.createTime desc", params);
		for (TappVersion t : ts) {
			AppVersion av = new AppVersion();
			BeanUtils.copyProperties(t, av);
            if(av.getStatus()==1){
            	av.setBool(true);
            }else{
            	av.setBool(false);
            }
			return av;
		}
		return null;
	}
}
