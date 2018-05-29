package com.cn.flypay.service.sys;

import java.util.List;
import java.util.Set;

import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.TpayTypeLimitConfig;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.PayTypeLimitConfig;

public interface PayTypeLimitConfigService {

	public List<PayTypeLimitConfig> dataGrid(PayTypeLimitConfig param, PageFilter ph);

	public Long count(PayTypeLimitConfig param, PageFilter ph);

	public void edit(PayTypeLimitConfig param);

	public PayTypeLimitConfig get(Long id);
	
	public TpayTypeLimitConfig findByCode(String code, Long orgId);

	public List<PayTypeLimitConfig> findPayTypeLimitConfigs(Set<Integer> transTypes, String agentId);
	
	public List<PayTypeLimitConfig> findPayTypeLimitConfigsZTC(Set<Integer> transTypes, String agentId);
	
	public List<PayTypeLimitConfig> findPayTypeLimitConfigsXTC(Set<Integer> transTypes, String agentId);
	
	public List<PayTypeLimitConfig> findPayTypeLimitConfigsFTC(Integer PayType, String agentId);

	public void initPayTypeLimitConfig(Torganization t);
	
	public PayTypeLimitConfig getPayType(int payAmtType,String agentId,int payType);

}
