package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.AgentUpgradeFeeCfg;

public interface AgentUpgradeFeeCfgService {

	public void edit(AgentUpgradeFeeCfg bc);

	public AgentUpgradeFeeCfg get(Integer agentType, Long organizationId);

	public List<AgentUpgradeFeeCfg> dataGrid(AgentUpgradeFeeCfg bc, PageFilter ph);

	public Long count(AgentUpgradeFeeCfg bc, PageFilter ph);

	public void initAgentUpgradeFeeCfgByCopyParent(Torganization t);

}
