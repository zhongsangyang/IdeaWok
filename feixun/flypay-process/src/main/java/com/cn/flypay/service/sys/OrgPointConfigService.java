package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.TuserSettlementConfig;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.OrgPointConfig;

public interface OrgPointConfigService {

	public List<OrgPointConfig> dataGrid(OrgPointConfig param, PageFilter ph);

	public Long count(OrgPointConfig param, PageFilter ph);

	public void edit(OrgPointConfig param);

	public OrgPointConfig get(Long id);

	/**
	 * 获取运营商积分费率配置信息
	 * 
	 * @param agentId
	 *            运营商
	 * @param payType
	 *            支付类型
	 * @param accountType
	 *          入账类型  T1 或者D0  11 T1大额 10 D0大额
	 * @return
	 */
	public OrgPointConfig getByAgentIdAndPayType(String agentId, Integer payType, Integer accountType);

	public List<OrgPointConfig> findOrgPointConfigsByAgentId(String agentId,Boolean flag);
	
	public List<OrgPointConfig> findOrgPointConfigsByAgentIdPayType(String agentId,String payType,String InfoType);
	
	public List<OrgPointConfig> findOrgPointConfigsByAgentIdAgentId(String agentId);


	public void initOrgPointConfigs(Torganization t);

	void initUserSettlementWhenPoint(String agentId, TuserSettlementConfig sc);
	
	

}
