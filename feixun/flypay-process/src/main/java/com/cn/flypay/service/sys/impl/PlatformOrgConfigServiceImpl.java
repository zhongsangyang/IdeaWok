package com.cn.flypay.service.sys.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.TplatformOrgConfig;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.account.OrgAccount;
import com.cn.flypay.pageModel.account.PlatformOrgConfig;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.PlatformOrgConfigService;
import com.cn.flypay.utils.StringUtil;

@Service
public class PlatformOrgConfigServiceImpl implements PlatformOrgConfigService {

	@Autowired
	private BaseDao<TplatformOrgConfig> platformOrgConfigDao;
	@Autowired
	private OrganizationService organizationService;

	@Override
	public List<PlatformOrgConfig> dataGrid(PlatformOrgConfig param, PageFilter ph) {
		List<PlatformOrgConfig> ul = new ArrayList<PlatformOrgConfig>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TplatformOrgConfig t left join t.organization org ";
		List<TplatformOrgConfig> l = platformOrgConfigDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TplatformOrgConfig t : l) {
			PlatformOrgConfig u = new PlatformOrgConfig();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrgName(t.getOrganization().getName());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(PlatformOrgConfig param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TplatformOrgConfig t left join t.organization org ";
		return platformOrgConfigDao.count("select count(t.id) " + hql + whereHql(param, params), params);
	}

	private String whereHql(PlatformOrgConfig param, Map<String, Object> params) {
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

	@Override
	public void add(PlatformOrgConfig param) {
		TplatformOrgConfig t = new TplatformOrgConfig();
		BeanUtils.copyProperties(param, t);
		t.setOrganization(organizationService.getTorganizationInCacheById(param.getId()));
		platformOrgConfigDao.save(t);
	}

	@Override
	public void initPlatformOrgConfig(Torganization t) {
		TplatformOrgConfig pc = platformOrgConfigDao.get("select t from TplatformOrgConfig t left join t.organization org where org.id=" + t.getOrganization().getId());
		TplatformOrgConfig nt = new TplatformOrgConfig();
		BeanUtils.copyProperties(pc, nt);
		nt.setId(null);
		nt.setVersion(0l);
		nt.setOrganization(t);
		platformOrgConfigDao.save(nt);
	}

	@Override
	public void delete(Long id) {

	}

	@CacheEvict(value = { "platformOrgConfigCache" }, allEntries = true)
	@Override
	public void edit(PlatformOrgConfig poc) {
		TplatformOrgConfig t = platformOrgConfigDao.get(TplatformOrgConfig.class, poc.getId());
		t.setPlatformAuthenticationFee(poc.getPlatformAuthenticationFee());
		t.setPlatformInputRate(poc.getPlatformInputRate());
		t.setPlatformMessageFee(poc.getPlatformMessageFee());
		t.setPlatformT0TixianRate(poc.getPlatformT0TixianRate());
		t.setPlatformTixianFee(poc.getPlatformTixianFee());
		platformOrgConfigDao.update(t);
	}

	@Cacheable(value = "platformOrgConfigCache", key = "#id+'get'")
	@Override
	public PlatformOrgConfig get(Long id) {
		TplatformOrgConfig t = platformOrgConfigDao.get("select t from TplatformOrgConfig t left join t.organization org where t.id=" + id);
		if (t != null) {
			PlatformOrgConfig u = new PlatformOrgConfig();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrgName(t.getOrganization().getName());
			}
			return u;
		}
		return null;
	}

	@Cacheable(value = "platformOrgConfigCache", key = "#agentId+'getPlatformOrgConfig'")
	@Override
	public PlatformOrgConfig getPlatformOrgConfig(String agentId) {
		TplatformOrgConfig t = platformOrgConfigDao.get("select t from TplatformOrgConfig t left join t.organization org where org.code='" + StringUtil.getAgentId(agentId) + "'");
		if (t != null) {
			PlatformOrgConfig u = new PlatformOrgConfig();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrgName(t.getOrganization().getName());
			}
			return u;
		}
		return null;
	}

	@Override
	public BigDecimal getPlatformOrgFee(String agentId, Integer type, BigDecimal amt) {
		BigDecimal fee = BigDecimal.ZERO;
		PlatformOrgConfig poc = getPlatformOrgConfig(agentId);
		/*
		 * 100 代理流量费 * 200 T0代付金额* 300 代付手续费 400 实名认证费 500 短信费
		 */
		switch (type) {
		case 100:
			if (amt.compareTo(fee) > 0) {
				fee = amt.multiply(poc.getPlatformInputRate());
			}
			break;
		case 200:
			fee = poc.getPlatformTixianFee();
			break;
		case 300:
			if (amt.compareTo(fee) > 0) {
				fee = amt.multiply(poc.getPlatformT0TixianRate());
			}
			break;
		case 400:
			fee = poc.getPlatformAuthenticationFee();
			break;
		case 500:
			fee = poc.getPlatformMessageFee();
			break;
		default:
			break;
		}
		return fee;
	}

}
