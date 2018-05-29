package com.cn.flypay.service.sys;

import java.math.BigDecimal;
import java.util.List;

import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.OrgChannelUserRateConfig;

/**
 * @category 暂时不启用该功能
 * @author Administrator
 * 
 */
public interface OrgChannelUserRateConfigService {

	public void edit(OrgChannelUserRateConfig bc);

	public OrgChannelUserRateConfig get(Integer channelType, Integer agentType, Long organizationId);

	public List<OrgChannelUserRateConfig> dataGrid(OrgChannelUserRateConfig bc, PageFilter ph);

	public Long count(OrgChannelUserRateConfig bc, PageFilter ph);

	/**
	 * 根据运营商的上级，设置下级代理的用户费率标准
	 * 
	 * @param t
	 */
	public void initOrgChannelUserRateConfigByCopyParent(Torganization t);

	/**
	 * 
	 * @param agentId
	 * @param payType
	 * @param accountType
	 * @return
	 */
	public OrgChannelUserRateConfig getOrgChannelUserRateConfigInCache(Integer channelType, Integer agentType, Long organizationId);

	/**
	 * 根据支付类型，用户类型，入账类型以及机构号确定分润比例（入账类型不同导致分润比例不同）
	 * 
	 * @param channelType
	 * @param sourceUserType
	 * @param targetUserType
	 * @param accountType
	 * @param organizationId
	 * @return
	 */
	public BigDecimal getUserShareRateInCache(Integer channelType, Integer sourceUserType, Integer targetUserType, Integer accountType, Long organizationId);

    
	/**
	 * 根据支付类型，用户类型，入账类型以及机构号确定分润比例（入账类型不同导致分润比例不同）
	 * 
	 * @param channelType
	 * @param sourceUserType
	 * @param targetUserType
	 * @param accountType
	 * @param organizationId
	 * @return
	 */
	public BigDecimal getUserShareRateInCacheC(Integer channelType, Integer sourceUserType, Integer targetUserType, Integer accountType, Long organizationId);

	
	public void updateOrgChannelUserRate(Integer type,Long userId,String agendId);
}
