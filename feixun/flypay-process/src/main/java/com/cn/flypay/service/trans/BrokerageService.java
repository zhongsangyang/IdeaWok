package com.cn.flypay.service.trans;

import java.util.List;
import java.util.Map;

import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.Brokerage;

public interface BrokerageService {

	public List<Brokerage> dataGrid(Brokerage appTransInfo, PageFilter ph);

	public Long count(Brokerage appTransInfo, PageFilter ph);

	/**
	 * 通过userId 获得佣金账户
	 * 
	 * @param userId
	 * @return
	 */
	public Brokerage getBrokerageByUserId(Long userId);

	/**
	 * 通过UserId 获得 分享有礼首页的用户信息
	 * 
	 * @param userId
	 * @return
	 */
	public Map<String, String> getRebateInfoByUserId(Long userId) throws Exception;

	/**
	 * 根据用户选择的类型分页查询用户信息
	 * 
	 * @param u
	 *            包含了查询U普通用户或者是 A、代理用户
	 * @param pf
	 * @return
	 */
	public List<Map<String, String>> getAgentListByUserId(User u, PageFilter pf);

	public List<Map<String, String>> getAgentListByUserIdTwo(User u, PageFilter pf, Integer type);

	public List<Map<String, String>> getAgentListByUserIdTherer(User u, PageFilter pf, Integer type);

	public List<Map<String, String>> getAgentListByUserIdFour(User u, PageFilter pf, Boolean b);

	public List<Map<String, String>> getAgentListByUserIdFive(User u, PageFilter pf, Boolean b);

	/**
	 * 推荐代理商查询
	 * 
	 * @param u
	 * @param pf
	 * @param b
	 * @param oType
	 *            =代理商 4=运营中心
	 * @return
	 */
	public List<Map<String, String>> getAgentListByUserIdFive(User u, PageFilter pf, Boolean b, String oType);

	public List<Map<String, String>> getAgentListByUserIdFourTwo(User u, PageFilter pf);

	public List<Map<String, String>> getAgentListByUserIdFave(User u, PageFilter pf, Integer type, Boolean b);

	public List<Map<String, String>> getAgentListByUserIdFour(User u, PageFilter pf, Integer type);

	public List<Map<String, String>> getAgentListByDer(User u, PageFilter pf, Integer type);

	public List<Map<String, String>> getAgentListByUserIdFex(User u, PageFilter pf, Integer type);

	Long getUserTotalNumsByUserId(Long userId, Boolean isAgent);

	Long getUserTotalNums(Long userId, Integer type);

	Long getUserTotalNumsTwo(Long userId, Integer type, String agentId);

	Long getUserEearningsNums(Long userId, Integer type, boolean isAgent);

	Long getUserEearningsFour(Long userId, Integer type);

	Long getUserEearningsNumsTwo(Long userId, Integer type, boolean isAgent);

	Long getUserEearningsNumsThere(Long userId, Integer type);

	Long getUserEearningsZums(Long userId, Integer type);

	Long getUserEearningsDZums(Long userId, Integer type);

	Long getUserTotal(Long userId, String agentId);

	Long getUserTotalTwo(Long userId);

	public List<Object[]> getUserTotalNumsByUser(Long userId, Boolean isAgent);

	public Long getUserTotalNums(Long userId, Boolean isAgent);

	public Long getUserTotalNumsThere(Long userId);

	/**
	 * 
	 * @param userId
	 * @param oType
	 *            5=代理商 4=运营中心
	 * @return
	 */
	public Long getUserTotalNumsThere(Long userId, String oType);

	public Long getUserTotalNumsFour(Long userId);

	/**
	 * 获取推荐代理商数量
	 * 
	 * @param userId
	 * @param oType
	 *            5=代理商 4=运营中心
	 * @return
	 */
	public Long getUserTotalNumsFour(Long userId, String oType);

	public Tbrokerage freeze(Long id);

	public void updateBrokerageAccount();

	/**
	 * 查询所有推广人
	 * @param userId
	 * @param type
	 * @param agentId
	 * @return
	 */
	public Long getUserTotalNums(Long userId, Integer type, String agentId);

	/**
	 * 查询间接推广的代理商
	 * @param userId
	 * @param agentId
	 * @return
	 */
	public Long getUserTotalTwo(Long userId, String agentId);

}
