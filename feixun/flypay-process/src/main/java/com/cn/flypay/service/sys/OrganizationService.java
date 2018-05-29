package com.cn.flypay.service.sys;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.base.Tree;
import com.cn.flypay.pageModel.sys.Organization;

public interface OrganizationService {

	public List<Organization> treeGrid();

	public void add(Organization organization) throws Exception;

	public void delete(Long id);

	public void edit(Organization organization);
	
	public void updateUserphone(Torganization t);

	public Organization get(Long id);
	
	public Organization getByCode(String code);

	public List<Tree> tree(HttpSession session);

	/**
	 * 获取各个机构自身交易获得的总佣金的比例
	 * 
	 * @return
	 */
	public Map<Long, BigDecimal> getOrgTransPrincipalRate();

	/**
	 * 获取各个机构自身代理获得的总佣金的比例
	 * 
	 * @return
	 */
	public Map<Long, BigDecimal> getOrgAgentPrincipalRate();

	public Set<Long> getOwerOrgIds(Long orgId);

	public List<Organization> treeGrid(Long organizationId);

	public List<Tree> tree(Long orgId);

	/**
	 * 查询所有运营商
	 * 
	 * @return
	 */
	public List<Tree> treeServiceProviders(Long organizationId, Integer angentType);

	/**
	 * 根据orgId 查询出其所属的运营商
	 * 
	 * @param orgId
	 * @return
	 */
	public Organization getServiceProviderInCacheByOrgId(Long orgId);

	public Torganization getTorganizationInCacheById(Long id);

	public void editAgentOrganization(Organization org);

	/**
	 * 判断是否使用积分制
	 * 
	 * @param agentId
	 * @return
	 */
	public Boolean isPointType(String agentId);

	/**
	 * 获取所有的运营商
	 * 
	 * @return
	 */
	public List<Organization> getOrganiztions();

	public Torganization getTorganizationInCacheByCode(String code);
	
	public Torganization getTorganizationInCode(String userPone);
	
	public Torganization getTorganizationInCodeTwo(String userPone);
	
	public Torganization getTorganizationInCacheByMobile(String mobile);

	List<Torganization> getTorganiztions();

	/**
	 * 查询用户是否是代理商
	 * @param agentId
	 * @param loginName
	 * @return
	 */
	public Torganization getTorganizationByAgentIdAndPhone(String agentId, String loginName);

}
