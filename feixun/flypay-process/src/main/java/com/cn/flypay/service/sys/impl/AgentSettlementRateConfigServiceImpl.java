package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TagentSettlementRateCfg;
import com.cn.flypay.model.sys.TagentSettlementRateCfgId;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.AgentSettlementRateCfg;
import com.cn.flypay.service.sys.AgentSettlementRateConfigService;
import com.cn.flypay.service.sys.OrganizationService;

@Service
public class AgentSettlementRateConfigServiceImpl implements AgentSettlementRateConfigService {

	@Autowired
	private BaseDao<TagentSettlementRateCfg> agentSettlementRateDao;
	@Autowired
	private BaseDao<Torganization> toDao;
	@Autowired
	private OrganizationService organizationService;

	@CacheEvict(value = "agentSettlementRateConfigCache")
	@Override
	public void edit(AgentSettlementRateCfg bc) {
		Torganization tt = organizationService.getTorganizationInCacheById(bc.getOrganizationId());
		TagentSettlementRateCfgId id = new TagentSettlementRateCfgId(tt, bc.getAgentType(), bc.getPayType());
		TagentSettlementRateCfg tbc = getTagentSettlementRateCfg(id);
		tbc.setSettlementRate(bc.getSettlementRate());
		agentSettlementRateDao.update(tbc);
	}

	@Override
	public AgentSettlementRateCfg get(Integer agentType, Long organizationId, Integer payType) {
		Torganization tt = organizationService.getTorganizationInCacheById(organizationId);
		TagentSettlementRateCfgId id = new TagentSettlementRateCfgId(tt, agentType, payType);
		TagentSettlementRateCfg tbc = getTagentSettlementRateCfg(id);
		AgentSettlementRateCfg bc = new AgentSettlementRateCfg();

		bc.setOrgName(tbc.getId().getOrganization().getName());
		bc.setOrganizationId(tbc.getId().getOrganization().getId());
		bc.setAgentType(tbc.getId().getAgentType());
		bc.setPayType(tbc.getId().getPayType());

		bc.setSettlementRate(tbc.getSettlementRate());
		return bc;
	}

	private TagentSettlementRateCfg getTagentSettlementRateCfg(TagentSettlementRateCfgId id) {
		TagentSettlementRateCfg bc = agentSettlementRateDao.get(TagentSettlementRateCfg.class, id);
		return bc;
	}

	@Override
	public List<AgentSettlementRateCfg> dataGrid(AgentSettlementRateCfg bc, PageFilter ph) {
		List<AgentSettlementRateCfg> ul = new ArrayList<AgentSettlementRateCfg>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TagentSettlementRateCfg t left join t.id.organization";
		List<TagentSettlementRateCfg> bcl = agentSettlementRateDao.find(hql + whereHql(bc, params), params, ph.getPage(), ph.getRows());
		for (TagentSettlementRateCfg tbc : bcl) {
			AgentSettlementRateCfg nbc = new AgentSettlementRateCfg();
			nbc.setOrgName(tbc.getId().getOrganization().getName());
			nbc.setOrganizationId(tbc.getId().getOrganization().getId());
			nbc.setAgentType(tbc.getId().getAgentType());
			nbc.setPayType(tbc.getId().getPayType());

			nbc.setSettlementRate(tbc.getSettlementRate());
			ul.add(nbc);
		}
		return ul;
	}

	private String whereHql(AgentSettlementRateCfg bc, Map<String, Object> params) {
		String hql = "";
		if (bc != null) {
			hql += " where 1=1 ";
			if (bc.getOrganizationId() != null) {
				hql += " and t.id.organization.id = :organizationId";
				params.put("organizationId", bc.getOrganizationId());
			}
		}
		return hql;
	}

	@Override
	public Long count(AgentSettlementRateCfg bc, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TagentSettlementRateCfg  t  left join t.id.organization";
		return agentSettlementRateDao.count("select count(*) " + hql + whereHql(bc, params), params);
	}

	@Override
	public void batchSaveAgentSettlementRateConfigList(List<TagentSettlementRateCfg> bc) {
		// bcDao.saveAll(bc);
	}

	@Override
	public void initAgentSettlementRateConfigByCopyParent(Torganization t) {
		List<TagentSettlementRateCfg> orgList = agentSettlementRateDao.find("select t from TagentSettlementRateCfg t left join t.id.organization org where org.id=" + t.getOrganization().getId());
		for (TagentSettlementRateCfg tof : orgList) {
			TagentSettlementRateCfg tof1 = new TagentSettlementRateCfg();
			BeanUtils.copyProperties(tof, tof1);
			tof1.getId().setOrganization(t);
			agentSettlementRateDao.save(tof1);
		}
	}
}
