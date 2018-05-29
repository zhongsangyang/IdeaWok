package com.cn.flypay.service.trans.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.model.trans.TbrokerageDetail;
import com.cn.flypay.model.util.CollectionUtil;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.Brokerage;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.BrokerageService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

@Service
public class BrokerageServiceImpl implements BrokerageService {

	@Autowired
	private BaseDao<Tbrokerage> brokerageDao;

	@Autowired
	private BaseDao<TbrokerageDetail> brokerageDetailDao;

	@Autowired
	private BaseDao<Tuser> userDao;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private UserService userservice;

	@Override
	public List<Brokerage> dataGrid(Brokerage app, PageFilter ph) {
		List<Brokerage> ul = new ArrayList<Brokerage>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from Tbrokerage t left join t.user tu left join t.user.organization tuo";
		List<Tbrokerage> l = brokerageDao.find(hql + whereHql(app, params) + orderHql(ph), params, ph.getPage(),
				ph.getRows());
		for (Tbrokerage t : l) {
			Brokerage u = new Brokerage();
			BeanUtils.copyProperties(t, u);
			if (t.getUser() != null) {
				u.setUserCode(t.getUser().getCode());
				u.setUserName(t.getUser().getRealName());
				u.setLoginName(t.getUser().getLoginName());
				if (t.getUser().getOrganization() != null) {
					u.setOrganizationName(t.getUser().getOrganization().getName());
				}
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(Brokerage app, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "from Tbrokerage t left join t.user tu left join t.user.organization tuo ";
		return brokerageDao.count("select count(*) " + hql + whereHql(app, params), params);
	}

	private String whereHql(Brokerage app, Map<String, Object> params) {
		String hql = "";
		if (app != null) {
			hql += " where 1=1 ";
			if (StringUtil.isNotBlank(app.getLoginName())) {
				hql += " and tu.loginName = :userName";
				params.put("userName", app.getLoginName());
			}
			if (StringUtil.isNotBlank(app.getUserCode())) {
				hql += " and tu.code = :code";
				params.put("code", app.getUserCode());
			}
			if (app.getStatus() != null) {
				hql += " and t.status = :status";
				params.put("status", app.getStatus());
			}
			if (StringUtil.isNotBlank(app.getOrganizationName())) {
				hql += " and tuo.name like :organizationName";
				params.put("organizationName", "%%" + app.getOrganizationName() + "%%");
			}
			if (app.getOperateUser() != null) {

				hql += " and  tuo.id in(:operaterOrgIds)";
				params.put("operaterOrgIds",
						organizationService.getOwerOrgIds(app.getOperateUser().getOrganizationId()));
			}
		}
		return hql;
	}

	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}

	@Override
	public Brokerage getBrokerageByUserId(Long userId) {

		String hql = " select t from Tbrokerage t left join t.user tu  where tu.id=" + userId;
		Tbrokerage t = brokerageDao.get(hql);
		if (t != null) {
			Brokerage u = new Brokerage();
			BeanUtils.copyProperties(t, u);
			return u;
		}
		return null;
	}

	@Override
	public Map<String, String> getRebateInfoByUserId(Long userId) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String hql = " select t from Tbrokerage t left join t.user tu left join t.user.organization tuo where tu.id="
				+ userId;
		Tbrokerage t = brokerageDao.get(hql);
		if (t != null) {
			if (t.getUser() != null) {
				Tuser u = t.getUser();
				map.put("merType", u.getUserType().toString());
				map.put("tgCodeNo", u.getCode());

				map.put("merCode", u.getCode());
				map.put("merName", StringUtil.isNotBlank(u.getMerchantName()) ? u.getMerchantName()
						: StringUtil.getShortName(u.getRealName()));
				if (u.getOrganization() != null) {
					if (u.getAgentId().equals("F00060009")) {
						map.put("diamondFee", u.getOrganization().getDiamondAgent().toString());
						map.put("goldFee", u.getOrganization().getGoldAgent().toString());
					} else {
						Long[] ls = payAmtToAgent(u.getUserType(), u.getOrganization());
						map.put("diamondFee", ls[0].toString());
						map.put("goldFee", ls[1].toString());
					}
				}
			}
			map.put("avlBrokerage", t.getBrokerage().toString());
			map.put("totalBrokerage", t.getTotalBrokerage().toString());
			map.put("totalPersonNum", getUserTotalNumsByUserId(userId, null).toString());

			Date date = new Date();
			Date startDate = DateUtil.getHoursbyInterval(DateUtil.getBeforeDate(date, 0), -1);
			Date endDate = DateUtil.getHoursbyInterval(DateUtil.getBeforeDate(date, 0), 23);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("startDate", startDate);
			params.put("endDate", endDate);
			params.put("userId", userId);
			String hql2 = "select sum(d.BROKERAGE) from trans_brokerage_detail d where d.BROKERAGE_USER_ID=:userId and d.TRANS_DATETIME BETWEEN :startDate and :endDate";

			List<Object[]> obj = brokerageDetailDao.findBySql(hql2, params);
			if (obj.get(0) != null && obj.get(0) != null) {
				map.put("todayBrokerage", String.valueOf(obj.get(0)));
			} else {
				map.put("todayBrokerage", "0.00");
			}

			params.put("startDate", DateUtil.DateOne());
			params.put("endDate", DateUtil.getStringFromDate(new Date(), "yyyyMMdd"));
			List<Object[]> objyesterday = brokerageDetailDao.findBySql(hql2, params);
			if (objyesterday.get(0) != null && objyesterday.get(0) != null) {
				map.put("yesterdayBrokerage", String.valueOf(objyesterday.get(0)));
			} else {
				map.put("yesterdayBrokerage", "0.00");
			}
		}
		return map;
	}

	@Override
	public Long getUserTotalNumsByUserId(Long userId, Boolean isAgent) {
		StringBuffer sb = new StringBuffer();
		sb.append("select id , 1 from sys_user u where u.PID=:userId %s  ");
		sb.append("union  select p.ID ,2 from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId %s");
		sb.append(
				"union  select t.ID ,3 from SYS_USER t inner join SYS_USER  p on t.PID=p.ID inner join SYS_USER  u on p.PID=u.ID  where u.PID=:userId %s");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		String sql = String.format(sb.toString(), " ", " ", " ");
		if (isAgent != null && isAgent) {
			sql = String.format(sb.toString(), " and u.user_type!=24 ", "  and p.user_type!=24 ",
					"  and t.user_type!=24 ");
		} else if (isAgent != null && !isAgent) {
			sql = String.format(sb.toString(), " and u.user_type=24 ", "  and p.user_type=24 ",
					"  and t.user_type=24 ");
		}
		List<Object[]> ids = userDao.findBySql(sql, params);
		if (ids != null) {
			return new Long(ids.size());
		}
		return 0l;
	}	

	@Override
	public List<Object[]> getUserTotalNumsByUser(Long userId, Boolean isAgent) {
		StringBuffer sb = new StringBuffer();
		sb.append("select id , 1 from sys_user u where u.PID=:userId %s  ");
		sb.append("union  select p.ID ,2 from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId %s");
		sb.append(
				"union  select t.ID ,3 from SYS_USER t inner join SYS_USER  p on t.PID=p.ID inner join SYS_USER  u on p.PID=u.ID  where u.PID=:userId %s");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		String sql = String.format(sb.toString(), " ", " ", " ");
		if (isAgent != null && isAgent) {
			sql = String.format(sb.toString(), " and u.user_type!=24 ", "  and p.user_type!=24 ",
					"  and t.user_type!=24 ");
		} else if (isAgent != null && !isAgent) {
			sql = String.format(sb.toString(), " and u.user_type=24 ", "  and p.user_type=24 ",
					"  and t.user_type=24 ");
		}
		List<Object[]> ids = userDao.findBySql(sql, params);
		return ids;
	}

	@Override
	public Long getUserTotalNums(Long userId, Boolean isAgent) {
		Long idf = 0l;
		StringBuffer sb = new StringBuffer();
		sb.append("select id , 1 from sys_user u where u.PID=:userId %s  ");
		sb.append("union  select p.ID ,2 from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId %s");
		sb.append(
				"union  select t.ID ,3 from SYS_USER t inner join SYS_USER  p on t.PID=p.ID inner join SYS_USER  u on p.PID=u.ID  where u.PID=:userId %s");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		String sql = String.format(sb.toString(), " ", " ", " ");
		if (isAgent != null && isAgent) {
			sql = String.format(sb.toString(), " and u.user_type!=24 ", "  and p.user_type!=24 ",
					"  and t.user_type!=24 ");
		} else if (isAgent != null && !isAgent) {
			sql = String.format(sb.toString(), " and u.user_type=24 ", "  and p.user_type=24 ",
					"  and t.user_type=24 ");
		}
		List<Object[]> ids = userDao.findBySql(sql, params);
		for (int i = 0; i < ids.size(); i++) {
			Object[] obj = ids.get(i);
			if (obj[1] != null) {
				Torganization org = organizationService.getTorganizationInCode(String.valueOf(obj[1]));
				if (org != null) {
					idf++;
				}
			}
		}
		return idf;
	}

	@Override
	public Long getUserTotalNumsThere(Long userId) {
		// Long idf = 0l;
		StringBuffer sb = new StringBuffer();
		// sb.append("select id , 1,LOGIN_NAME from sys_user u where
		// u.PID=:userId %s ");
		sb.append(
				"select count(g.ID) from sys_organization g where g.user_phone in(select p.LOGIN_NAME from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId) ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		String sql = sb.toString();
		List<Object[]> ids = userDao.findBySql(sql, params);
		// for (int i = 0; i < ids.size(); i++) {
		// Object[] obj = ids.get(i);
		// if(obj[0]!=null){
		// Torganization org =
		// organizationService.getTorganizationInCodeTwo(String.valueOf(obj[2]));
		// if(org!=null){
		// idf++;
		// }
		// }
		// }
		Object obj = ids.get(0);
		return Long.valueOf(String.valueOf(obj));
	}

	//查询所有的推荐代理商和运营中心
	@Override
	public Long getUserTotalNumsThere(Long userId, String oType) {
		StringBuffer sb = new StringBuffer();
		/*if ("5".equals(oType)) {// 代理商
			sb.append(
					"select count(g.ID) from sys_organization g where g.org_type=5 and g.user_phone in(select p.LOGIN_NAME from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId) ");
		} else if ("4".equals(oType)) {// 运营中心
			sb.append(
					"select count(g.ID) from sys_organization g where g.org_type=4 and g.user_phone in(select p.LOGIN_NAME from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId) ");
		}*/
		if ("5".equals(oType)) {// 代理商
			sb.append(
					"select count(g.ID) from sys_organization g where g.org_type=5 and g.user_phone in(select p.LOGIN_NAME from SYS_USER P  where p.PID in(:userIds)) ");
		} else if ("4".equals(oType)) {// 运营中心
			sb.append(
					"select count(g.ID) from sys_organization g where g.org_type=4 and g.user_phone in(select p.LOGIN_NAME from SYS_USER P  where p.PID in(:userIds)) ");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		
		
		List<String> userIds = new ArrayList<String>();
		userIds.add(String.valueOf(userId));	//输入当前登录的用户ID，这样会查询当前用户的直接推荐代理商
		
		//查询直接推荐的代理商
//		List<Object[]> list = getAgentUserIdByPhone(userId);
//		if (CollectionUtil.isNotEmpty(list)) {
//			for (Object[] obj : list) {
//				userIds.add(String.valueOf(obj[1]));
//			}
//		}
		
		
		
		//查询直接推荐的所有用户的ID
		List<Object[]> list = getUserIdByParentPhone(userId);	//输入当前登录的用户的下级所有用户，这样会查询当前用户的间接推荐代理商
		if (CollectionUtil.isNotEmpty(list)) {
			for (Object[] obj : list) {
				userIds.add(String.valueOf(obj[1]));
			}
		}
		
		params.put("userIds", userIds);
		String sql = sb.toString();
		List<Object[]> ids = userDao.findBySql(sql, params);
		Object obj = ids.get(0);
		return Long.valueOf(String.valueOf(obj));
	}
	
	/**
	 * 查询直接推荐代理商的ID
	 * @param userId
	 * @return
	 */
	private List<Object[]> getAgentUserIdByPhone(Long userId){
		Tuser tuser = userservice.getTuser(userId);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", tuser.getId());
		String sql = "SELECT u.LOGIN_NAME,u.ID FROM sys_user u  LEFT JOIN sys_organization o ON u.ORGANIZATION_ID = o.ID WHERE u.PID=:userId AND u.LOGIN_NAME = o.user_phone";
		return userDao.findBySql(sql, params);
	}
	
	/**
	 * 查询直接推荐所有用户的ID
	 * @param userId
	 * @return
	 */
	private List<Object[]> getUserIdByParentPhone(Long userId){
		Tuser tuser = userservice.getTuser(userId);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", tuser.getId());
		String sql = "SELECT u.LOGIN_NAME,u.ID FROM sys_user u  WHERE u.PID=:userId ";
		return userDao.findBySql(sql, params);
	}
	
	
	@Override
	public Long getUserTotalNumsFour(Long userId) {
		// Long idf = 0l;
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select count(g.ID) from sys_organization g where g.user_phone in(select u.LOGIN_NAME from sys_user u where u.PID=:userId)");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		String sql = sb.toString();
		List<Object[]> ids = userDao.findBySql(sql, params);
		// for (int i = 0; i < ids.size(); i++) {
		// Object[] obj = ids.get(i);
		// if(obj[2]!=null){
		// Torganization org =
		// organizationService.getTorganizationInCodeTwo(String.valueOf(obj[2]));
		// if(org!=null){
		// idf++;
		// }
		// }
		// }
		Object obj = ids.get(0);
		return Long.valueOf(String.valueOf(obj));
	}

	@Override
	public Long getUserTotalNumsFour(Long userId, String oType) {
		StringBuffer sb = new StringBuffer();
		if ("5".equals(oType)) {
			sb.append(
					"select count(g.ID) from sys_organization g where g.org_type=5 and g.user_phone in(select u.LOGIN_NAME from sys_user u where u.PID=:userId)");
		} else if ("4".equals(oType)) {
			sb.append(
					"select count(g.ID) from sys_organization g where g.org_type=4 and g.user_phone in(select u.LOGIN_NAME from sys_user u where u.PID=:userId)");
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		String sql = sb.toString();
		List<Object[]> ids = userDao.findBySql(sql, params);
		Object obj = ids.get(0);
		return Long.valueOf(String.valueOf(obj));
	}

	@Override
	public Long getUserTotalNums(Long userId, Integer type) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ( ");
		sb.append("select id , 1,LOGIN_NAME from sys_user u where u.PID=:userId %s  ");
		sb.append(
				"union  select p.ID ,2,p.LOGIN_NAME from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId %s");
		sb.append(
				"union  select t.ID ,3,t.LOGIN_NAME from SYS_USER t inner join SYS_USER  p on t.PID=p.ID inner join SYS_USER  u on p.PID=u.ID  where u.PID=:userId %s");
		sb.append(" ) as a  group by a.LOGIN_NAME ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		String sql = String.format(sb.toString(), " ", " ", " ");
		if (type == 24) {
			sql = String.format(sb.toString(), " and u.user_type=24 ", "  and p.user_type=24 ",
					"  and t.user_type=24 ");
		} else if (type == 23) {
			sql = String.format(sb.toString(), " and u.user_type=23 ", "  and p.user_type=23 ",
					"  and t.user_type=23 ");
		} else if (type == 22) {
			sql = String.format(sb.toString(), " and u.user_type=22 ", "  and p.user_type=22 ",
					"  and t.user_type=22 ");
		} else if (type == 21) {
			sql = String.format(sb.toString(), " and u.user_type=21 ", "  and p.user_type=21 ",
					"  and t.user_type=21 ");
		}
		List<Object[]> ids = userDao.findBySql(sql, params);
		if (ids != null) {
			return new Long(ids.size());
		}
		return 0l;
	}
	
	//查询所有推广人数,加上agentId提高效率
	@Override
	public Long getUserTotalNums(Long userId, Integer type, String agentId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ( ");
		sb.append("select id , 1,LOGIN_NAME from sys_user u where u.agent_id =:agentId and u.PID=:userId %s  ");
		sb.append(
				" union select p.ID ,2,p.LOGIN_NAME from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where p.agent_id =:agentId and u.PID=:userId %s");
		sb.append(
				" union  select t.ID ,3,t.LOGIN_NAME from SYS_USER t inner join SYS_USER  p on t.PID=p.ID inner join SYS_USER  u on p.PID=u.ID  where t.agent_id =:agentId and u.PID=:userId %s");
		sb.append(" ) as a  group by a.LOGIN_NAME ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("agentId", agentId);
		params.put("userId", userId);
		String sql = String.format(sb.toString(), " ", " ", " ");
		if (type == 24) {
			sql = String.format(sb.toString(), " and u.user_type=24 ", "  and p.user_type=24 ",
					"  and t.user_type=24 ");
		} else if (type == 23) {
			sql = String.format(sb.toString(), " and u.user_type=23 ", "  and p.user_type=23 ",
					"  and t.user_type=23 ");
		} else if (type == 22) {
			sql = String.format(sb.toString(), " and u.user_type=22 ", "  and p.user_type=22 ",
					"  and t.user_type=22 ");
		} else if (type == 21) {
			sql = String.format(sb.toString(), " and u.user_type=21 ","  and p.user_type=21 ",
					"  and t.user_type=21 ");
		}
		List<Object[]> ids = userDao.findBySql(sql, params);
		if (ids != null) {
			return new Long(ids.size());
		}
		return 0l;
	}
	
	//查询直接推广人，加上agentId提高效率
	@Override
	public Long getUserTotalNumsTwo(Long userId, Integer type, String agentId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select id , 1 from sys_user u where u.agent_id =:agentId and u.PID=:userId %s  ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("agentId", agentId);
		params.put("userId", userId);
		String sql = String.format(sb.toString(), " ");
		if (type == 24) {
			sql = String.format(sb.toString(), " and u.user_type=24 ");
		} else if (type == 23) {
			sql = String.format(sb.toString(), " and u.user_type=23 ");
		} else if (type == 22) {
			sql = String.format(sb.toString(), " and u.user_type=22 ");
		} else if (type == 21) {
			sql = String.format(sb.toString(), " and u.user_type=21 ");
		}
		List<Object[]> ids = userDao.findBySql(sql, params);
		if (ids != null) {
			return new Long(ids.size());
		}
		return 0l;
	}

	@Override
	public Long getUserEearningsNums(Long userId, Integer type, boolean isAgent) {
		StringBuffer sb = new StringBuffer();
		if (isAgent) {
			sb.append(
					"select id , 1 from sys_user u where u.PID=:userId and u.authentication_status=:authenticationstatus ");
		} else {
			sb.append(
					"select p.ID ,2 from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId and p.authentication_status=:authenticationstatus ");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("authenticationstatus", type);
		String sql = sb.toString();
		List<Object[]> ids = userDao.findBySql(sql, params);
		if (ids.size() != 0) {
			return new Long(ids.size());
		}
		return 0l;
	}

	@Override
	public Long getUserEearningsFour(Long userId, Integer type) {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select t.ID ,3 from SYS_USER t inner join SYS_USER  p on t.PID=p.ID inner join SYS_USER  u on p.PID=u.ID inner join SYS_USER r on u.PID=r.ID  where r.ID=:userId and t.authentication_status=:authenticationstatus and t.agent_id='F20160001'");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("authenticationstatus", type);
		String sql = sb.toString();
		List<Object[]> ids = userDao.findBySql(sql, params);
		if (ids.size() != 0) {
			return new Long(ids.size());
		}
		return 0l;
	}
	
	@Override
	public Long getUserEearningsZums(Long userId, Integer type) {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select p.ID ,2 from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId and p.authentication_status=:authenticationstatus ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("authenticationstatus", type);
		String sql = sb.toString();
		List<Object[]> ids = userDao.findBySql(sql, params);
		if (ids.size() != 0) {
			return new Long(ids.size());
		}
		return 0l;
	}

	//之前的逻辑是只查询代理商下的用户，现在要求也查询直接推荐的代理商下的用户
	@Override
	public Long getUserEearningsDZums(Long userId, Integer type) {
		Tuser tuser = userservice.getTuser(userId);
		Map<String, Object> params = new HashMap<String, Object>();
		List<String> orgIds = new ArrayList<String>();
		orgIds.add(String.valueOf(tuser.getOrganization().getId()));
		params.put("authenticationstatus", type);
		List<Object[]> ids = getAgentUserByPhone(userId);
		String sql = "select t.ID,1 from SYS_USER t where t.ORGANIZATION_ID in(:orgIds) and t.authentication_status=:authenticationstatus";
		//获取直接推荐代理商
		if (CollectionUtil.isNotEmpty(ids)) {
			for (Object[] obj : ids) {
				orgIds.add(String.valueOf(obj[1]));
			}
		}
		params.put("orgIds",orgIds);
		List<Object[]> list = userDao.findBySql(sql, params);
		if (type == 1 && list.size() != 0) {
			return new Long(list.size() - 1);//已认证减去自身
		} else if(type == -1 && list.size() != 0){
			return new Long(list.size());
		}
		return 0l;
	}
	
	/**
	 * 查询直接推荐的代理商用户
	 * @param userId
	 * @return
	 */
	private List<Object[]> getAgentUserByPhone(Long userId){
		Tuser tuser = userservice.getTuser(userId);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", tuser.getId());
		String sql = "select g.user_phone,g.ID from sys_organization g where g.user_phone in(select u.LOGIN_NAME from sys_user u where u.PID=:userId) ";
		return userDao.findBySql(sql, params);
	}
	
	@Override
	public Long getUserEearningsNumsThere(Long userId, Integer type) {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select id , 1 from sys_user u where u.PID=:userId and u.authentication_status=:authenticationstatus %s");
		sb.append(
				"union  select p.ID ,2 from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId and p.authentication_status=:authenticationstatus %s");
		sb.append(
				"union  select t.ID ,3 from SYS_USER t inner join SYS_USER  p on t.PID=p.ID inner join SYS_USER  u on p.PID=u.ID  where u.PID=:userId and u.authentication_status=:authenticationstatus %s");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("authenticationstatus", type);
		String sql = String.format(sb.toString(), " ", " ", " ");
		List<Object[]> ids = userDao.findBySql(sql, params);
		if (ids.size() != 0) {
			return new Long(ids.size());
		}
		return 0l;
	}

	@Override
	public Long getUserEearningsNumsTwo(Long userId, Integer type, boolean isAgent) {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select id , 1 from sys_user u where u.PID=:userId and  u.authentication_status=:authenticationstatus %s");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("authenticationstatus", type);
		String sql = String.format(sb.toString(), " ", " ", " ");
		if (isAgent) {
			sql = String.format(sb.toString(), " and u.user_type!=24 ", "  and p.user_type!=24 ",
					"  and t.user_type!=24 ");
		} else if (!isAgent) {
			sql = String.format(sb.toString(), " and u.user_type=24 ", "  and p.user_type=24 ",
					"  and t.user_type=24 ");
		}
		List<Object[]> ids = userDao.findBySql(sql, params);
		if (ids.size() != 0) {
			return new Long(ids.size());
		}
		return 0l;
	}

	@Override
	public Long getUserTotal(Long userId, String agentId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select g.user_phone,g.ID,g.org_type  from sys_organization g where g.user_phone in( ");
		sb.append("select LOGIN_NAME from sys_user u where u.PID=:userId and u.agent_id =:agentId )");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("agentId", agentId);
		String sql = sb.toString();
		List<Object[]> ids = userDao.findBySql(sql, params);
		return Long.parseLong(String.valueOf(ids.size()));
	}

	@Override
	public Long getUserTotalTwo(Long userId, String agentId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select g.user_phone,g.ID,g.org_type  from sys_organization g where g.user_phone in( ");
		sb.append(
				" select p.LOGIN_NAME from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId and p.agent_id =:agentId ");
		sb.append(
				"union select t.LOGIN_NAME from SYS_USER t inner join SYS_USER  p on t.PID=p.ID inner join SYS_USER  u on p.PID=u.ID  where u.PID=:userId and t.agent_id =:agentId ");
		sb.append(" ) ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("agentId", agentId);
		String sql = sb.toString();
		List<Object[]> ids = userDao.findBySql(sql, params);
		return Long.parseLong(String.valueOf(ids.size()));
	}
	
	@Override
	public Long getUserTotalTwo(Long userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select g.user_phone,g.ID,g.org_type  from sys_organization g where g.user_phone in( ");
		sb.append("select LOGIN_NAME from sys_user u where u.PID=:userId ");
		sb.append(
				"union  select p.LOGIN_NAME from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId ");
		sb.append(
				"union select t.LOGIN_NAME from SYS_USER t inner join SYS_USER  p on t.PID=p.ID inner join SYS_USER  u on p.PID=u.ID  where u.PID=:userId ");
		sb.append(" ) ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		String sql = sb.toString();
		List<Object[]> ids = userDao.findBySql(sql, params);
		return Long.parseLong(String.valueOf(ids.size()));
	}

	private Long[] payAmtToAgent(int type, Torganization org) {
		Long[] fees = new Long[] { 0l, 0l };
		switch (type) {
		case 24:
			fees[0] = org.getDiamondAgent();
			fees[1] = org.getGoldAgent();
			break;
		case 22:
			fees[0] = org.getDiamondAgent() - org.getGoldAgent();
			break;
		default:
			break;
		}
		return fees;
	}

	@Override
	public List<Map<String, String>> getAgentListByUserId(User u, PageFilter pf) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from ( ");
		sb.append(
				" select id , 1  ,u.LOGIN_NAME, u.USER_TYPE, u.authentication_status from sys_user u where u.PID=:uid %s ");
		sb.append(
				" union  select p.ID ,2 ,p.LOGIN_NAME, p.USER_TYPE, p.authentication_status from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:uid %s ");
		sb.append(
				" union  select t.ID ,3 ,t.LOGIN_NAME, t.USER_TYPE, t.authentication_status  from SYS_USER t inner join SYS_USER  p on p.ID=t.PID inner join SYS_USER  u on u.ID=p.PID where u.PID=:uid %s ");
		sb.append(" ) as a ORDER BY a.id desc limit ");
		sb.append((pf.getPage() - 1) * pf.getRows() + "," + pf.getRows());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", u.getId());
		String isAgent = "!=24"; // 代理
		if (u.getIsChnl() != 1) {
			isAgent = "=24";// 普通会员
		}
		String sql = String.format(sb.toString(), " ", " ", " ");
		if (u.getIsChnl() != null && u.getIsChnl() == 1) {
			sql = String.format(sb.toString(), " and u.user_type!=24 ", "  and p.user_type!=24 ",
					"  and t.user_type!=24 ");
		} else if (isAgent != null && u.getIsChnl() != 1) {
			sql = String.format(sb.toString(), " and u.user_type=24 ", "  and p.user_type=24 ",
					"  and t.user_type=24 ");
		}

		List<Object[]> ids = userDao.findBySql(sql, params);
		List<Map<String, String>> lms = new ArrayList<Map<String, String>>();
		for (Object[] objs : ids) {
			Map<String, String> ms = new HashMap<String, String>();
			ms.put("level", ((BigInteger) objs[1]).toString());
			ms.put("name", (String) objs[2]);
			ms.put("type", ((Integer) objs[3]).toString());
			ms.put("authenticationStatus", ((Integer) objs[4]).toString());
			lms.add(ms);
		}
		return lms;
	}

	@Override
	public List<Map<String, String>> getAgentListByUserIdTwo(User u, PageFilter pf, Integer type) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from ( ");
		sb.append(
				" select id , 1  ,u.LOGIN_NAME, u.USER_TYPE, u.authentication_status,u.CREATE_DATETIME,u.nickname,u.real_name from sys_user u where u.PID=:uid %s ");
		sb.append(
				" union  select p.ID ,2 ,p.LOGIN_NAME, p.USER_TYPE, p.authentication_status,p.CREATE_DATETIME,p.nickname,p.real_name from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:uid %s ");
		sb.append(
				" union  select t.ID ,3 ,t.LOGIN_NAME, t.USER_TYPE, t.authentication_status,t.CREATE_DATETIME,t.nickname,t.real_name  from SYS_USER t inner join SYS_USER  p on p.ID=t.PID inner join SYS_USER  u on u.ID=p.PID where u.PID=:uid %s ");
		sb.append(" ) as a  group by a.LOGIN_NAME order by a.CREATE_DATETIME desc limit ");//修改按注册时间排序
		sb.append((pf.getPage() - 1) * pf.getRows() + "," + pf.getRows());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", u.getId());
		String sql = String.format(sb.toString(), " ", " ", " ");
		if (type == 24) {
			sql = String.format(sb.toString(), " and u.user_type=24 ", "  and p.user_type=24 ",
					"  and t.user_type=24 ");
		} else if (type == 23) {
			sql = String.format(sb.toString(), " and u.user_type=23 ", "  and p.user_type=23 ",
					"  and t.user_type=23 ");
		} else if (type == 22) {
			sql = String.format(sb.toString(), " and u.user_type=22 ", "  and p.user_type=22 ",
					"  and t.user_type=22 ");
		} else if (type == 21) {
			sql = String.format(sb.toString(), " and u.user_type=21 ", "  and p.user_type=21 ",
					"  and t.user_type=21 ");
		}
		List<Object[]> ids = userDao.findBySql(sql, params);
		List<Map<String, String>> lms = new ArrayList<Map<String, String>>();
		for (Object[] objs : ids) {
			 Map<String, String> ms = new HashMap<String, String>();
			 ms.put("nickname", String.valueOf(objs[6]));
			 ms.put("name", (String) objs[2]);
			 ms.put("type", ((Integer) objs[3]).toString());
			 ms.put("authenticationStatus", ((Integer) objs[4]).toString());
			 ms.put("createDate", (objs[5]).toString());
			 ms.put("realName", String.valueOf(objs[7]));
			 lms.add(ms);
		}
		return lms;
	}
	
	private List<Map<String, String>> descByCreateDate(List<Map<String, String>> lms) {
		//根据注册日期降序排序
		for (int i = 0; i < lms.size()-1; i++) {
			for (int j = i+1; j < lms.size(); j++) {
				Map<String, String> mapi = lms.get(i);
				Map<String, String> mapj = lms.get(j);
				//取出注册日期
				Date datei = DateUtil.getDateFromString(mapi.get("createDate"), DateUtil.FORMAT_YYYY_MM_DD_HH_mm_ss);
				Date datej = DateUtil.getDateFromString(mapj.get("createDate"), DateUtil.FORMAT_YYYY_MM_DD_HH_mm_ss);
				if(datej.getTime()>datei.getTime()){
					//前面的日期小于后面日期，交换位置
					lms.set(i, mapj);
					lms.set(j, mapi);
				}
			}
		}
		return lms;
	}	
	
	
	@Override
	public List<Map<String, String>> getAgentListByUserIdFave(User u, PageFilter pf, Integer type, Boolean b) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from ( ");
		if (b) {
			sb.append(
					" select id , 1  ,u.LOGIN_NAME, u.USER_TYPE, u.authentication_status,u.CREATE_DATETIME,u.nickname,u.real_name from sys_user u where u.PID=:uid and u.authentication_status=:authenticationstatus %s ");
		} else {
			// sb.append(" select id , 1 ,u.LOGIN_NAME, u.USER_TYPE,
			// u.authentication_status,u.CREATE_DATETIME,u.nickname,u.real_name
			// from sys_user u where u.PID=:uid and
			// u.authentication_status=:authenticationstatus %s ");
			sb.append(
					" select p.ID ,2 ,p.LOGIN_NAME, p.USER_TYPE, p.authentication_status,p.CREATE_DATETIME,p.nickname,p.real_name from SYS_USER p inner join SYS_USER  u on p.PID=u.ID where u.PID=:uid and p.authentication_status=:authenticationstatus %s ");
			// sb.append(" union select t.ID ,3 ,t.LOGIN_NAME, t.USER_TYPE,
			// t.authentication_status,t.CREATE_DATETIME,t.nickname,t.real_name
			// from SYS_USER t inner join SYS_USER p on p.ID=t.PID inner join
			// SYS_USER u on u.ID=p.PID where u.PID=:uid and
			// p.authentication_status=:authenticationstatus %s");
		}
		// sb.append(" ) as a ORDER BY a.id desc limit ");
		sb.append(" ) as a ORDER BY a.CREATE_DATETIME desc limit ");// 修改为按注册时间排序
		sb.append((pf.getPage() - 1) * pf.getRows() + "," + pf.getRows());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", u.getId());
		params.put("authenticationstatus", type);
		String sql = String.format(sb.toString(), " ", " ", " ");
		List<Object[]> ids = userDao.findBySql(sql, params);
		List<Map<String, String>> lms = new ArrayList<Map<String, String>>();
		for (Object[] objs : ids) {
			Map<String, String> ms = new HashMap<String, String>();
			ms.put("nickname", String.valueOf(objs[6]));
			ms.put("name", (String) objs[2]);
			ms.put("type", ((Integer) objs[3]).toString());
			ms.put("authenticationStatus", ((Integer) objs[4]).toString());
			ms.put("createDate", (objs[5]).toString());
			ms.put("realName", String.valueOf(objs[7]));
			lms.add(ms);
		}
		return lms;
	}

	@Override
	public List<Map<String, String>> getAgentListByUserIdFour(User u, PageFilter pf, Integer type) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from ( ");
		sb.append(
				" select t.ID ,3 ,t.LOGIN_NAME, t.USER_TYPE, t.authentication_status,t.CREATE_DATETIME,t.nickname,t.real_name from SYS_USER t inner join SYS_USER  p on t.PID=p.ID inner join SYS_USER  u on p.PID=u.ID inner join SYS_USER r on u.PID=r.ID  where r.ID=:uid and t.authentication_status=:authenticationstatus and t.agent_id='F20160001'");
		// sb.append(" ) as a ORDER BY a.id desc limit ");
		sb.append(" ) as a ORDER BY a.CREATE_DATETIME desc limit ");// 修改为按注册时间排序
		sb.append((pf.getPage() - 1) * pf.getRows() + "," + pf.getRows());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", u.getId());
		params.put("authenticationstatus", type);
		String sql = String.format(sb.toString(), " ", " ", " ");
		List<Object[]> ids = userDao.findBySql(sql, params);
		List<Map<String, String>> lms = new ArrayList<Map<String, String>>();
		for (Object[] objs : ids) {
			Map<String, String> ms = new HashMap<String, String>();
			ms.put("nickname", String.valueOf(objs[6]));
			ms.put("name", (String) objs[2]);
			ms.put("type", ((Integer) objs[3]).toString());
			ms.put("authenticationStatus", ((Integer) objs[4]).toString());
			ms.put("createDate", (objs[5]).toString());
			ms.put("realName", String.valueOf(objs[7]));
			lms.add(ms);
		}
		return lms;
	}

	//之前的逻辑为查询代理商的所有下级用户，现在追加推荐的代理商的所有下级用户
	@Override
	public List<Map<String, String>> getAgentListByDer(User u, PageFilter pf, Integer type) {
		Tuser tuser = userservice.getTuser(u.getId());
		Map<String, Object> params = new HashMap<String, Object>();
		List<String> orgIds = new ArrayList<String>();
		orgIds.add(String.valueOf(tuser.getOrganization().getId()));
		//查询直接推荐的代理商
		List<Object[]> list = getAgentUserByPhone(u.getId());
		if (CollectionUtil.isNotEmpty(list)) {
			for (Object[] obj : list) {
				orgIds.add(String.valueOf(obj[1]));
			}
		}
		params.put("orgIds", orgIds);
		params.put("authenticationstatus", type);
		StringBuffer sb = new StringBuffer();
		// 查询代理商的所有下级用户以及推荐的代理商的所有下级用户，按注册时间排序
		sb.append(" select * from ( ");
		sb.append(
				"select t.ID ,3 ,t.LOGIN_NAME, t.USER_TYPE, t.authentication_status,t.CREATE_DATETIME,t.nickname,t.real_name from SYS_USER t where t.ORGANIZATION_ID in(:orgIds) and t.authentication_status=:authenticationstatus ");
		sb.append(" ) as a ORDER BY a.CREATE_DATETIME desc limit ");
		sb.append((pf.getPage() - 1) * pf.getRows() + "," + pf.getRows());
		List<Object[]> ids = userDao.findBySql(sb.toString(), params);
		List<Map<String, String>> lms = new ArrayList<Map<String, String>>();
		for (Object[] objs : ids) {
			//减去自身
			if (!StringUtil.equals(String.valueOf(u.getId()), String.valueOf(objs[0]))) {
				Map<String, String> ms = new HashMap<String, String>();
				ms.put("nickname", String.valueOf(objs[6]));
				ms.put("name", (String) objs[2]);
				ms.put("type", ((Integer) objs[3]).toString());
				ms.put("authenticationStatus", ((Integer) objs[4]).toString());
				ms.put("createDate", (objs[5]).toString());
				ms.put("realName", String.valueOf(objs[7]));
				lms.add(ms);
			}
		}
		return lms;
	}

	@Override
	public List<Map<String, String>> getAgentListByUserIdFex(User u, PageFilter pf, Integer type) {
		StringBuffer sb = new StringBuffer();
		sb.append(
				" select s.id,2,s.LOGIN_NAME,s.USER_TYPE,s.authentication_status,s.CREATE_DATETIME,s.nickname,s.real_name from  sys_user s where s.ORGANIZATION_ID=:ORGANIZATIONID and s.authentication_status=:authenticationstatus limit ");
		sb.append((pf.getPage() - 1) * pf.getRows() + "," + pf.getRows());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ORGANIZATIONID", u.getOrganizationId());
		params.put("authenticationstatus", type);
		String sql = sb.toString();
		List<Object[]> ids = userDao.findBySql(sql, params);
		List<Map<String, String>> lms = new ArrayList<Map<String, String>>();
		for (Object[] objs : ids) {
			Map<String, String> ms = new HashMap<String, String>();
			ms.put("nickname", String.valueOf(objs[6]));
			ms.put("name", (String) objs[2]);
			ms.put("type", ((Integer) objs[3]).toString());
			ms.put("authenticationStatus", ((Integer) objs[4]).toString());
			ms.put("createDate", (objs[5]).toString());
			ms.put("realName", String.valueOf(objs[7]));
			lms.add(ms);
		}
		return lms;
	}

	//update:2017.12.2 修改厦商我的商户 老板详情按注册时间排序	
	@Override
	public List<Map<String, String>> getAgentListByUserIdTherer(User u, PageFilter pf, Integer type) {
		StringBuffer sb = new StringBuffer();
		sb.append("select u.ID,u.LOGIN_NAME,u.CREATE_DATETIME,u.real_name,u.nickname,u.authentication_status  from sys_user u where u.LOGIN_NAME in (");
		sb.append("select g.user_phone from sys_organization g where g.user_phone in( ");
		sb.append("select LOGIN_NAME from sys_user u where u.PID=:userId ");
		sb.append(
				"union  select p.LOGIN_NAME from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId ");
		sb.append(
				"union select t.LOGIN_NAME from SYS_USER t inner join SYS_USER  p on t.PID=p.ID inner join SYS_USER  u on p.PID=u.ID  where u.PID=:userId ");
		sb.append(" )) order by u.CREATE_DATETIME desc limit ");
		sb.append((pf.getPage() - 1) * pf.getRows() + "," + pf.getRows());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", u.getId());
		String sql = sb.toString();
		List<Object[]> ids = userDao.findBySql(sql, params);
		List<Map<String, String>> lms = new ArrayList<Map<String, String>>();
		for (int i = 0; i < ids.size(); i++) {
			Object[] obj = ids.get(i);
			Torganization tor = organizationService.getTorganizationInCacheByMobile(String.valueOf(obj[1]));
			Map<String, String> ms = new HashMap<String, String>();
			ms.put("name", String.valueOf(obj[1]));
			ms.put("createDate", String.valueOf(obj[2]));
			ms.put("realName", String.valueOf(obj[3]));
			ms.put("nickname", String.valueOf(obj[4]));
			ms.put("authenticationStatus", String.valueOf(obj[5]));
			ms.put("type", tor.getOrgType());
			lms.add(ms);
		}
		return lms;
	}

	@Override
	public List<Map<String, String>> getAgentListByUserIdFour(User u, PageFilter pf, Boolean b) {
		StringBuffer sb = new StringBuffer();
		if (b) {
			sb.append(
					"select id,LOGIN_NAME,agent_id,USER_TYPE,authentication_status,CREATE_DATETIME,nickname,real_name,1 from sys_user u where u.PID=:userId %s  ");
		} else {
			sb.append(
					"select p.id,p.LOGIN_NAME,p.agent_id,p.USER_TYPE,p.authentication_status,p.CREATE_DATETIME,p.nickname,p.real_name,2 from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId %s");
			sb.append(
					"union  select t.id,t.LOGIN_NAME,t.agent_id,t.USER_TYPE,t.authentication_status,t.CREATE_DATETIME,t.nickname,t.real_name,3 from SYS_USER t inner join SYS_USER  p on t.PID=p.ID inner join SYS_USER  u on p.PID=u.ID  where u.PID=:userId %s");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", u.getId());
		String sql = String.format(sb.toString(), " ", " ", " ");
		List<Object[]> ids = userDao.findBySql(sql, params);
		List<Map<String, String>> lms = new ArrayList<Map<String, String>>();
		for (int i = 0; i < ids.size(); i++) {
			Object[] obj = ids.get(i);
			Torganization org = organizationService.getTorganizationInCode(String.valueOf(obj[1]));
			if (org != null) {
				Map<String, String> ms = new HashMap<String, String>();
				ms.put("nickname", String.valueOf(obj[6]));
				ms.put("name", (String) obj[1]);
				ms.put("type", ((Integer) obj[3]).toString());
				ms.put("authenticationStatus", ((Integer) obj[4]).toString());
				ms.put("createDate", (obj[5]).toString());
				ms.put("realName", String.valueOf(obj[7]));
				lms.add(ms);
			}
		}
		return lms;
	}

	@Override
	public List<Map<String, String>> getAgentListByUserIdFive(User u, PageFilter pf, Boolean b) {
		StringBuffer sb = new StringBuffer();
		if (b) {
			sb.append(
					"select g.user_phone,g.ID,g.org_type  from sys_organization g where g.user_phone in(select u.LOGIN_NAME from sys_user u where u.PID=:userId) limit ");
			sb.append((pf.getPage() - 1) * pf.getRows() + "," + pf.getRows());
		} else {
			sb.append(
					"select g.user_phone,g.ID,g.org_type  from sys_organization g where g.user_phone in(select p.LOGIN_NAME from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId ) limit ");
			sb.append((pf.getPage() - 1) * pf.getRows() + "," + pf.getRows());
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", u.getId());
		String sql = String.format(sb.toString(), " ", " ", " ");
		List<Object[]> ids = userDao.findBySql(sql, params);

		List<Map<String, String>> lms = new ArrayList<Map<String, String>>();
		for (int i = 0; i < ids.size(); i++) {
			Object[] obj = ids.get(i);
			Tuser tuser = userservice.getUserLoginName(String.valueOf(obj[0]));
			Map<String, String> ms = new HashMap<String, String>();
			ms.put("nickname", tuser.getNickname());
			ms.put("name", tuser.getLoginName());
			ms.put("type", (String.valueOf(obj[2])));
			ms.put("authenticationStatus", String.valueOf(tuser.getAuthenticationStatus()));
			ms.put("createDate", String.valueOf(tuser.getCreateDatetime()));
			ms.put("realName", String.valueOf(tuser.getRealName()));
			lms.add(ms);
		}
		return descByCreateDate(lms);
	}

	@Override
	public List<Map<String, String>> getAgentListByUserIdFive(User u, PageFilter pf, Boolean b, String oType) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		if (b) {
			if ("3".equals(oType)) {// 代理商
				sb.append(
						"select g.user_phone,g.ID,g.org_type  from sys_organization g where g.org_type=5 and g.user_phone in(select u.LOGIN_NAME from sys_user u where u.PID=:userId) limit ");
			} else if ("4".equals(oType)) {// 运营中心
				sb.append(
						"select g.user_phone,g.ID,g.org_type  from sys_organization g where g.org_type=4 and g.user_phone in(select u.LOGIN_NAME from sys_user u where u.PID=:userId) limit ");
			}
			sb.append((pf.getPage() - 1) * pf.getRows() + "," + pf.getRows());
			params.put("userId", u.getId());
		} else {
			if ("3".equals(oType)) {//所有推荐代理商
				sb.append(
						"select g.user_phone,g.ID,g.org_type  from sys_organization g where g.org_type=5 and g.user_phone in(select p.LOGIN_NAME from SYS_USER P  where p.PID in(:userIds) ) limit ");
			} else if ("4".equals(oType)) {
				sb.append(//所有推荐运营中心
						"select g.user_phone,g.ID,g.org_type  from sys_organization g where g.org_type=4 and g.user_phone in(select p.LOGIN_NAME from SYS_USER P  where p.PID in(:userIds) ) limit ");
			}
			sb.append((pf.getPage() - 1) * pf.getRows() + "," + pf.getRows());
			//查询直接推荐的用户
			List<Object[]> list = getUserIdByParentPhone(u.getId());
			List<String> userIds = new ArrayList<String>();
			userIds.add(String.valueOf(u.getId()));
			if (CollectionUtil.isNotEmpty(list)) {
				for (Object[] obj : list) {
					userIds.add(String.valueOf(obj[1]));
				}
			}
			params.put("userIds", userIds);
		}
		String sql = String.format(sb.toString(), " ", " ", " ");
		List<Object[]> ids = userDao.findBySql(sql, params);

		List<Map<String, String>> lms = new ArrayList<Map<String, String>>();
		for (int i = 0; i < ids.size(); i++) {
			Object[] obj = ids.get(i);
			Tuser tuser = userservice.getUserLoginName(String.valueOf(obj[0]));
			Map<String, String> ms = new HashMap<String, String>();
			ms.put("nickname", tuser.getNickname());
			ms.put("name", tuser.getLoginName());
			ms.put("type", (String.valueOf(obj[2])));
			ms.put("authenticationStatus", String.valueOf(tuser.getAuthenticationStatus()));
			ms.put("createDate", String.valueOf(tuser.getCreateDatetime()));
			ms.put("realName", String.valueOf(tuser.getRealName()));
			lms.add(ms);
		}
		return descByCreateDate(lms);
	}

	@Override
	public List<Map<String, String>> getAgentListByUserIdFourTwo(User u, PageFilter pf) {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"select id,LOGIN_NAME,agent_id,USER_TYPE,authentication_status,CREATE_DATETIME,nickname,real_name,1 from sys_user u where u.PID=:userId %s  ");
		sb.append(
				"select p.id,p.LOGIN_NAME,p.agent_id,p.USER_TYPE,p.authentication_status,p.CREATE_DATETIME,p.nickname,p.real_name,2 from SYS_USER P inner join SYS_USER  u on p.PID=u.ID where u.PID=:userId %s");
		sb.append(
				"union  select t.id,t.LOGIN_NAME,t.agent_id,t.USER_TYPE,t.authentication_status,t.CREATE_DATETIME,t.nickname,t.real_name,3 from SYS_USER t inner join SYS_USER  p on t.PID=p.ID inner join SYS_USER  u on p.PID=u.ID  where u.PID=:userId %s");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", u.getId());
		String sql = String.format(sb.toString(), " ", " ", " ");
		List<Object[]> ids = userDao.findBySql(sql, params);
		List<Map<String, String>> lms = new ArrayList<Map<String, String>>();
		for (int i = 0; i < ids.size(); i++) {
			Object[] obj = ids.get(i);
			Torganization org = organizationService.getTorganizationInCode(String.valueOf(obj[1]));
			if (org != null) {
				Map<String, String> ms = new HashMap<String, String>();
				ms.put("nickname", String.valueOf(obj[6]));
				ms.put("name", (String) obj[1]);
				ms.put("type", ((Integer) obj[3]).toString());
				ms.put("authenticationStatus", ((Integer) obj[4]).toString());
				ms.put("createDate", (obj[5]).toString());
				ms.put("realName", String.valueOf(obj[7]));
				lms.add(ms);
			}
		}
		return lms;
	}

	@Override
	public Tbrokerage freeze(Long id) {
		Tbrokerage ta = brokerageDao.get(Tbrokerage.class, id);
		if (ta != null) {
			int status = ta.getStatus() == 0 ? 1 : 0;
			brokerageDao.executeHql("update Tbrokerage set status=" + status + " where id=" + id);
			ta.setStatus(status);
		}
		return ta;
	}

	@Override
	public void updateBrokerageAccount() {
		brokerageDao.executeHql(
				"update Tbrokerage set historyBrokerage=yesterdayBrokerage , yesterdayBrokerage=brokerage+lockBrokerage");

	}
}
