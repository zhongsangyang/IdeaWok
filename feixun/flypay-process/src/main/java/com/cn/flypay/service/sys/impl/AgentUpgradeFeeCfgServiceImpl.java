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
import com.cn.flypay.model.sys.TagentUpgradeFeeCfg;
import com.cn.flypay.model.sys.TagentUpgradeFeeCfgId;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.AgentUpgradeFeeCfg;
import com.cn.flypay.service.sys.AgentUpgradeFeeCfgService;
import com.cn.flypay.service.sys.OrganizationService;

@Service
public class AgentUpgradeFeeCfgServiceImpl implements AgentUpgradeFeeCfgService {

	@Autowired
	private BaseDao<TagentUpgradeFeeCfg> agentUpgradeFeeCfgDao;
	@Autowired
	private BaseDao<Torganization> toDao;
	@Autowired
	private OrganizationService organizationService;

	@CacheEvict(value = "agentSettlementRateConfigCache")
	@Override
	public void edit(AgentUpgradeFeeCfg bc) {
		Torganization tt = organizationService.getTorganizationInCacheById(bc.getOrganizationId());
		TagentUpgradeFeeCfgId id = new TagentUpgradeFeeCfgId(tt, bc.getAgentType());
		TagentUpgradeFeeCfg tbc = getTagentUpgradeFeeCfg(id);
		tbc.setAuthFee(bc.getAuthFee());
		tbc.setDiamondFee(bc.getDiamondFee());
		tbc.setGlodFee(bc.getGlodFee());
		tbc.setTixianT0Fee(bc.getTixianT0Fee());
		tbc.setTixianT1Fee(bc.getTixianT1Fee());
		agentUpgradeFeeCfgDao.update(tbc);
	}

	@Override
	public AgentUpgradeFeeCfg get(Integer agentType, Long organizationId) {
		Torganization tt = organizationService.getTorganizationInCacheById(organizationId);
		TagentUpgradeFeeCfgId id = new TagentUpgradeFeeCfgId(tt, agentType);
		TagentUpgradeFeeCfg tbc = getTagentUpgradeFeeCfg(id);
		AgentUpgradeFeeCfg bc = new AgentUpgradeFeeCfg();

		bc.setOrgName(tbc.getId().getOrganization().getName());
		bc.setOrganizationId(tbc.getId().getOrganization().getId());
		bc.setAgentType(tbc.getId().getAgentType());
		bc.setAuthFee(tbc.getAuthFee());
		bc.setDiamondFee(tbc.getDiamondFee());
		bc.setGlodFee(tbc.getGlodFee());
		bc.setTixianT0Fee(tbc.getTixianT0Fee());
		bc.setTixianT1Fee(tbc.getTixianT1Fee());
		return bc;
	}

	private TagentUpgradeFeeCfg getTagentUpgradeFeeCfg(TagentUpgradeFeeCfgId id) {
		TagentUpgradeFeeCfg bc = agentUpgradeFeeCfgDao.get(TagentUpgradeFeeCfg.class, id);
		return bc;
	}

	@Override
	public List<AgentUpgradeFeeCfg> dataGrid(AgentUpgradeFeeCfg bc, PageFilter ph) {
		List<AgentUpgradeFeeCfg> ul = new ArrayList<AgentUpgradeFeeCfg>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TagentUpgradeFeeCfg t left join t.id.organization";
		List<TagentUpgradeFeeCfg> bcl = agentUpgradeFeeCfgDao.find(hql + whereHql(bc, params), params, ph.getPage(), ph.getRows());
		for (TagentUpgradeFeeCfg tbc : bcl) {
			AgentUpgradeFeeCfg nbc = new AgentUpgradeFeeCfg();
			if (tbc.getId() != null && tbc.getId().getOrganization() != null) {
				nbc.setOrgName(tbc.getId().getOrganization().getName());
				nbc.setOrganizationId(tbc.getId().getOrganization().getId());
				nbc.setAgentType(tbc.getId().getAgentType());
			}
			nbc.setAuthFee(tbc.getAuthFee());
			nbc.setDiamondFee(tbc.getDiamondFee());
			nbc.setGlodFee(tbc.getGlodFee());
			nbc.setTixianT0Fee(tbc.getTixianT0Fee());
			nbc.setTixianT1Fee(tbc.getTixianT1Fee());
			ul.add(nbc);
		}
		return ul;
	}

	private String whereHql(AgentUpgradeFeeCfg bc, Map<String, Object> params) {
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
	public Long count(AgentUpgradeFeeCfg bc, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TagentUpgradeFeeCfg  t  left join t.id.organization";
		return agentUpgradeFeeCfgDao.count("select count(*) " + hql + whereHql(bc, params), params);
	}

	@Override
	public void initAgentUpgradeFeeCfgByCopyParent(Torganization t) {
		List<TagentUpgradeFeeCfg> orgList = agentUpgradeFeeCfgDao.find("select t from TagentUpgradeFeeCfg t left join t.id.organization org where org.id=" + t.getOrganization().getId());
		for (TagentUpgradeFeeCfg tof : orgList) {
			TagentUpgradeFeeCfg tof1 = new TagentUpgradeFeeCfg();
			BeanUtils.copyProperties(tof, tof1);
			tof1.getId().setOrganization(t);
			agentUpgradeFeeCfgDao.save(tof1);
		}
	}
}
