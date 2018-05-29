package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TbrokerageConfig;
import com.cn.flypay.model.sys.TbrokerageConfigId;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.BrokerageConfig;
import com.cn.flypay.service.sys.BrokerageConfigService;

@Service
public class BrokerageConfigServiceImpl implements BrokerageConfigService {

	@Autowired
	private BaseDao<TbrokerageConfig> bcDao;
	@Autowired
	private BaseDao<Torganization> toDao;

	@CacheEvict(value = "brokerageConfigCache")
	@Override
	public void edit(BrokerageConfig bc) {
		Torganization tt = toDao.get(Torganization.class, bc.getOrganizationId());
		TbrokerageConfigId id = new TbrokerageConfigId(bc.getAgentType(), tt, bc.getCfgType());
		TbrokerageConfig tbc = getTbrokerageConfig(id);
		tbc.setFirstRate(bc.getFirstRate());
		tbc.setSecRate(bc.getSecRate());
		tbc.setThirdRate(bc.getThirdRate());
		bcDao.update(tbc);
	}

	@Override
	public BrokerageConfig get(String agentType, Long organizationId, String cfgType) {
		TbrokerageConfigId id = new TbrokerageConfigId(agentType, toDao.get(Torganization.class, organizationId), cfgType);
		TbrokerageConfig tbc = getTbrokerageConfig(id);
		BrokerageConfig bc = new BrokerageConfig();

		bc.setOrgName(tbc.getId().getOrganization().getName());
		bc.setOrganizationId(tbc.getId().getOrganization().getId());
		bc.setAgentType(tbc.getId().getAgentType());
		bc.setCfgType(tbc.getId().getCfgType());

		bc.setFirstRate(tbc.getFirstRate());
		bc.setSecRate(tbc.getSecRate());
		bc.setThirdRate(tbc.getThirdRate());
		return bc;
	}

	private TbrokerageConfig getTbrokerageConfig(TbrokerageConfigId id) {
		TbrokerageConfig bc = bcDao.get(TbrokerageConfig.class, id);
		return bc;
	}

	@Override
	public List<BrokerageConfig> dataGrid(BrokerageConfig bc, PageFilter ph) {
		List<BrokerageConfig> ul = new ArrayList<BrokerageConfig>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TbrokerageConfig t left join t.id.organization";
		List<TbrokerageConfig> bcl = bcDao.find(hql + whereHql(bc, params), params, ph.getPage(), ph.getRows());
		for (TbrokerageConfig tbc : bcl) {
			BrokerageConfig nbc = new BrokerageConfig();
			nbc.setOrgName(tbc.getId().getOrganization().getName());
			nbc.setOrganizationId(tbc.getId().getOrganization().getId());
			nbc.setAgentType(tbc.getId().getAgentType());
			nbc.setCfgType(tbc.getId().getCfgType());

			nbc.setFirstRate(tbc.getFirstRate());
			nbc.setSecRate(tbc.getSecRate());
			nbc.setThirdRate(tbc.getThirdRate());
			ul.add(nbc);
		}
		return ul;
	}

	private String whereHql(BrokerageConfig bc, Map<String, Object> params) {
		String hql = "";
		if (bc != null) {
			hql += " where 1=1 ";
			if (bc.getOrganizationId() != 0) {
				hql += " and t.id.organization.id = :organizationId";
				params.put("organizationId", bc.getOrganizationId());
			}
		}
		return hql;
	}

	@Override
	public Long count(BrokerageConfig bc, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TbrokerageConfig  t ";
		return bcDao.count("select count(*) " + hql + whereHql(bc, params), params);
	}

	@Cacheable(value = "brokerageConfigCache", key = "'transBrokerageConfig'")
	@Override
	public Map<Long, Set<TbrokerageConfig>> getTransBrokerageConfig() {
		System.out.println("-----getBrokerageConfig-----");
		List<TbrokerageConfig> orgList = bcDao.find("select t from TbrokerageConfig t where t.id.cfgType='10' order by t.id.organization.id");
		Map<Long, Set<TbrokerageConfig>> scMap = new HashMap<Long, Set<TbrokerageConfig>>();
		for (TbrokerageConfig tof : orgList) {
			if (!scMap.containsKey(tof.getId().getOrganization().getId())) {
				Set<TbrokerageConfig> bcSet = new HashSet<TbrokerageConfig>();
				bcSet.add(tof);
				scMap.put(tof.getId().getOrganization().getId(), bcSet);
			} else {
				scMap.get(tof.getId().getOrganization().getId()).add(tof);
			}

		}
		return scMap;
	}

	@Cacheable(value = "brokerageConfigCache", key = "'agentBrokerageConfig'")
	@Override
	public Map<Long, Set<TbrokerageConfig>> getAgentBrokerageConfig() {
		System.out.println("-----getBrokerageConfig-----");
		List<TbrokerageConfig> orgList = bcDao.find("select t from TbrokerageConfig t where t.id.cfgType='20' order by t.id.organization.id");
		Map<Long, Set<TbrokerageConfig>> scMap = new HashMap<Long, Set<TbrokerageConfig>>();
		for (TbrokerageConfig tof : orgList) {
			if (!scMap.containsKey(tof.getId().getOrganization().getId())) {
				Set<TbrokerageConfig> bcSet = new HashSet<TbrokerageConfig>();
				bcSet.add(tof);
				scMap.put(tof.getId().getOrganization().getId(), bcSet);
			} else {
				scMap.get(tof.getId().getOrganization().getId()).add(tof);
			}
		}
		return scMap;
	}

	@Override
	public void batchSaveBrokerageConfigList(List<TbrokerageConfig> bc) {
		// bcDao.saveAll(bc);
	}

	@Override
	public void initBrokerageConfigByCopyParent(Torganization t) {
		List<TbrokerageConfig> orgList = bcDao.find("select t from TbrokerageConfig t left join t.id.organization org where org.id=" + t.getOrganization().getId());
		for (TbrokerageConfig tof : orgList) {
			TbrokerageConfig tof1 = new TbrokerageConfig();
			BeanUtils.copyProperties(tof, tof1);
			tof1.getId().setOrganization(t);
			bcDao.save(tof1);
		}
	}
}
