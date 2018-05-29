package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.model.sys.TagentSettlementRateCfg;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.AgentSettlementRateCfg;

public interface AgentSettlementRateConfigService {

	public void edit(AgentSettlementRateCfg bc);

	public AgentSettlementRateCfg get(Integer agentType, Long organizationId, Integer payType);

	public List<AgentSettlementRateCfg> dataGrid(AgentSettlementRateCfg bc, PageFilter ph);

	public Long count(AgentSettlementRateCfg bc, PageFilter ph);

	public void batchSaveAgentSettlementRateConfigList(List<TagentSettlementRateCfg> bc);

	public void initAgentSettlementRateConfigByCopyParent(Torganization t);

}
