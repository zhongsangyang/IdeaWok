package com.cn.flypay.service.sys;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cn.flypay.model.sys.TbrokerageConfig;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.BrokerageConfig;

public interface BrokerageConfigService {

	public void edit(BrokerageConfig bc);

	public BrokerageConfig get(String agentType, Long organizationId, String cfgType);

	public List<BrokerageConfig> dataGrid(BrokerageConfig bc, PageFilter ph);

	public Long count(BrokerageConfig bc, PageFilter ph);

	public void batchSaveBrokerageConfigList(List<TbrokerageConfig> bc);

	/**
	 * 获取交易分润AB比例
	 * 
	 * @return
	 */
	public Map<Long, Set<TbrokerageConfig>> getTransBrokerageConfig();

	/**
	 * 获取代理分润AB比例
	 * 
	 * @return
	 */
	public Map<Long, Set<TbrokerageConfig>> getAgentBrokerageConfig();

	/**
	 * 根据运营商的上级，设置下级代理的分润标准
	 * 
	 * @param t
	 */
	public void initBrokerageConfigByCopyParent(Torganization t);

}
