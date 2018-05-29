package com.cn.flypay.service.sys.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.account.TaccountPoint;
import com.cn.flypay.model.sys.TfeedBack;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tresource;
import com.cn.flypay.model.sys.Trole;
import com.cn.flypay.model.sys.TsysImage;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserBlackList;
import com.cn.flypay.model.sys.TuserSettlementConfig;
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.model.util.CollectionUtil;
import com.cn.flypay.pageModel.account.AccountPoint;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.AuthenticationLog;
import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.pageModel.sys.Organization;
import com.cn.flypay.pageModel.sys.SysImage;
import com.cn.flypay.pageModel.sys.SysParameter;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserBlackList;
import com.cn.flypay.pageModel.sys.UserCard;
import com.cn.flypay.pageModel.sys.UserImage;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.account.AccountPointService;
import com.cn.flypay.service.common.ProducerService;
import com.cn.flypay.service.payment.AuthenticationService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.MsgHistoryService;
import com.cn.flypay.service.sys.OrgChannelUserRateConfigService;
import com.cn.flypay.service.sys.OrgPointConfigService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.RoleService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.service.sys.UserImageService;
import com.cn.flypay.service.sys.UserMerchantConfigService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.ExcelUtil;
import com.cn.flypay.utils.MD5Util;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.SysConvert;
import com.cn.flypay.utils.jpush.api.JiguangUtil;

@Service
public class UserServiceImpl implements UserService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String USERCODE = "userCode";
	@Autowired
	private BaseDao<Tuser> userDao;
	@Autowired
	private BaseDao<TuserBlackList> userBlackListDao;

	@Autowired
	private BaseDao<TuserSettlementConfig> userSettlementConfigDao;

	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private BaseDao<TsysImage> imageDao;

	@Autowired
	private BaseDao<Trole> roleDao;
	@Autowired
	private RoleService roleService;
	@Autowired
	private BaseDao<Tbrokerage> brokerageDao;

	@Autowired
	private BaseDao<Taccount> accDao;
	@Autowired
	private BaseDao<TaccountPoint> accPointDao;

	@Autowired
	private BaseDao<TuserSettlementConfig> scDao;
	@Autowired
	private BaseDao<TfeedBack> feedBackDao;

	@Autowired
	private BaseDao<Torganization> organizationDao;

	@Autowired
	private SysParamService paramService;

	@Autowired
	private UserImageService imageService;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private ProducerService producerService;
	@Autowired
	private MsgHistoryService msgHistoryService;
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private AccountPointService accountPointService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private OrgPointConfigService orgPointConfigService;
	@Value("${login_error_num}")
	private String login_error_num;
	@Autowired
	private UserMerchantConfigService userMerchantConfigService;
	@Autowired
	private OrgChannelUserRateConfigService orgChannelUserRateConfigService;
	@Autowired
	private UserSettlementConfigService userSettlementConfigService;

	@Override
	@Deprecated
	public void addShenfuD0Merch() {
		List<Tuser> userList = userDao.find(" from Tuser t where t.isShenfuD0Open NOT IN(1,9) and t.state = 0 and t.settlementStatus = 1 ORDER BY t.id ASC ", 0, 10);
		System.out.println("Register ShenFu D0 size=" + userList.size());
		if (CollectionUtil.isNotEmpty(userList)) {
			for (Tuser user : userList) {
				userMerchantConfigService.createShenFuUserMerchants(user);
			}
		}
	}

	/**
	 * 添加新注册用户
	 */
	@Override
	public void addUser(User u) {
		try {
			Tuser t = new Tuser();
			BeanUtils.copyProperties(u, t);
			initUserSettlementConfig(t);
			List<Trole> roles = new ArrayList<Trole>();
			Set<Trole> troles = new HashSet<Trole>();
			if (StringUtil.isNotBlank(u.getRoleIds())) {
				for (String roleId : u.getRoleIds().split(",")) {
					roles.add(roleService.findAllRole().get(Long.parseLong(roleId)));
				}
			}
			if (roles.size() > 0) {
				troles.addAll(roles);
			} else {
				troles.add(roleService.findAllRole().get(2l));
			}
			t.setRoles(troles);

			String usercode = getUserCode();

			t.setCode(usercode);
			if (u.getPid() != null) {
				t.setParentUser(userDao.get(Tuser.class, u.getPid()));
			}
			t.setPassword(MD5Util.md5(u.getPassword()));
			t.setState(GlobalConstant.ENABLE);
			t.setIsDefault(GlobalConstant.DEFAULT);
			t.setCreateDatetime(new Date());
			t.setLastDateTime(new Date());

			userDao.save(t);

			saveInitUserAccountAndBrokerage(t);
		} catch (Exception e) {
			logger.error("add user error", e);
		}

	}

	private String getUserCode() {
		SysParameter sp = paramService.getByName(USERCODE);
		Long usercode = Long.parseLong(sp.getParaValue());
		sp.setParaValue(String.valueOf(usercode + 1));
		paramService.edit(sp);
		return StringUtil.fillString(SysConvert.numericToString(usercode, 32), 6);
	}

	@Override
	public void delete(Long id) {
		Tuser t = userDao.get(Tuser.class, id);
		del(t);
	}

	private void del(Tuser t) {
		userDao.delete(t);
	}

	public String editToAgent(Long id) {
		Tuser tuser = userDao.get("select u from Tuser u left join u.organization o where u.id=" + id);
		String loginName = tuser.getLoginName();
		logger.info("upgrade user loginName={} to Agent", loginName);
		Torganization tOrg = organizationService.getTorganizationInCacheByMobile(loginName);
		String agentId = tuser.getAgentId();// 一个用户可能在多个平台注册
		if (tOrg != null && tOrg.getCode().startsWith(agentId)) {
			return "该用户已是代理商,无需升级";
		}
		Torganization oldOrg = tuser.getOrganization();
		// TODO 加代理
		tOrg = new Torganization();
		BeanUtils.copyProperties(oldOrg, tOrg);
		tOrg.setOrganization(oldOrg);
		tOrg.setVersion(1L);
		String code = tuser.getAgentId() + "_" + tuser.getLoginName() + "_" + tuser.getRealName();
		tOrg.setCode(code);
		tOrg.setName(code);
		tOrg.setAddress("中国内地");
		tOrg.setIcon("icon-man");
		tOrg.setCreateDatetime(new Date());
		tOrg.setUserPhone(loginName);
		tOrg.setUserUpgradeType(3);// 后台升级
		tOrg.setAgentType(2);
		tOrg.setAgentLevel(2);// 代理级别
		tOrg.setOrgType("5");
		organizationDao.save(tOrg);
		tuser.setOrganization(tOrg);
		tuser.setIsAdmin(1);
		Set<Trole> roleSet = new HashSet<Trole>();
		Trole role = roleDao.get(Trole.class, Long.valueOf(23));// 2.0代理商权限
		roleSet.add(role);
		tuser.setRoles(roleSet);
		tuser.setIsChnl(1);
		try {
			// 没机构的1级 + 没机构的1级剩下的没机构的2级 + 没机构的1级生下的的没机构的2级生下的3级
			String sql = " select id ,LOGIN_NAME, 1 from sys_user u where u.PID=:userId and u.LOGIN_NAME not in "
					+ " (select g.user_phone from sys_organization g where g.user_phone in( select LOGIN_NAME from sys_user u where u.PID=:userId)) " + " union " + " select u2.ID ,u2.LOGIN_NAME,2 from sys_user u2 where u2.PID in "
					+ " (select id  from sys_user u where u.PID=:userId and u.LOGIN_NAME not in " + " (select g.user_phone from sys_organization g where g.user_phone in "
					+ " (select LOGIN_NAME from sys_user u where u.PID=:userId)) ) and u2.LOGIN_NAME not in " + " (select g.user_phone from sys_organization g where g.user_phone in"
					+ " (select u2.LOGIN_NAME from sys_user u2 where u2.PID in (select id from sys_user u where u.PID=:userId))) " + " union " + " select u3.ID ,u3.LOGIN_NAME,3 from sys_user u3 where u3.PID in "
					+ " (select u2.ID  from sys_user u2 where u2.PID in " + " (select id  from sys_user u where u.PID=:userId and u.LOGIN_NAME not in " + " (select g.user_phone from sys_organization g where g.user_phone in"
					+ " (select LOGIN_NAME from sys_user u where u.PID=:userId))) and u2.LOGIN_NAME not in " + " (select g.user_phone from sys_organization g where g.user_phone in"
					+ " (select u2.LOGIN_NAME from sys_user u2 where u2.PID in " + " (select id from sys_user u where u.PID=:userId))))";

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", tuser.getId());
			Tuser ptuser = getTuser(tuser.getId());
			List<Object[]> ids = userDao.findBySql(sql, params);
			for (int i = 0; i < ids.size(); i++) {
				Object[] obj = ids.get(i);
				String updateUserAgent = "update  sys_user set  organization_id=" + tOrg.getId() + " , agent_id='" + ptuser.getAgentId() + "'  where id = " + obj[0];
				organizationDao.executeSql(updateUserAgent);
			}
		} catch (Exception e) {
			// throw e;
			logger.error("AgentUpgrade user={} Failed", tuser.getLoginName(), e);
		}
		orgChannelUserRateConfigService.updateOrgChannelUserRate(22, tuser.getId(), tuser.getAgentId());
		tuser.setUserType(22);
		userDao.update(tuser);
		return "升级成功";
	}

	public String editToAgent2(Long id) {
		Tuser tuser = userDao.get("select u from Tuser u left join u.organization o where u.id=" + id);
		String loginName = tuser.getLoginName();
		logger.info("upgrade user loginName={} to Agent2", loginName);
		Torganization tOrg = organizationService.getTorganizationInCacheByMobile(loginName);
		String agentId = tuser.getAgentId();// 一个用户可能在多个平台注册
		if (tOrg != null && tOrg.getCode().startsWith(agentId)) {
			if ("4".equals(tOrg.getOrgType())) {
				return "该用户已是运营中心,无需升级";
			} else if ("5".equals(tOrg.getOrgType())) {
				tOrg.setUserUpgradeType(3);// 后台升级
				tOrg.setAgentType(1);
				tOrg.setOrgType("4");
				organizationDao.save(tOrg);
			} else {
				return "该用户已是代理但数据异常,请先核对OrgType=" + tOrg.getOrgType();
			}
		} else {
			Torganization oldOrg = tuser.getOrganization();
			tOrg = new Torganization();
			BeanUtils.copyProperties(oldOrg, tOrg);
			tOrg.setOrganization(oldOrg);
			tOrg.setVersion(1L);
			String code = tuser.getAgentId() + "_" + tuser.getLoginName() + "_" + tuser.getRealName();
			tOrg.setCode(code);
			tOrg.setName(code);
			tOrg.setAddress("中国内地");
			tOrg.setIcon("icon-man");
			tOrg.setCreateDatetime(new Date());
			tOrg.setUserPhone(loginName);
			tOrg.setUserUpgradeType(3);// 后台升级
			tOrg.setAgentType(1);
			tOrg.setOrgType("4");
			organizationDao.save(tOrg);
			tuser.setOrganization(tOrg);
			tuser.setIsAdmin(1);
			Set<Trole> roleSet = new HashSet<Trole>();
			Trole role = roleDao.get(Trole.class, Long.valueOf(23));// 2.0代理商权限
			roleSet.add(role);
			tuser.setRoles(roleSet);
			tuser.setIsChnl(1);
			try {
				// 没机构的1级 + 没机构的1级剩下的没机构的2级 + 没机构的1级生下的的没机构的2级生下的3级
				String sql = " select id ,LOGIN_NAME, 1 from sys_user u where u.PID=:userId and u.LOGIN_NAME not in "
						+ " (select g.user_phone from sys_organization g where g.user_phone in( select LOGIN_NAME from sys_user u where u.PID=:userId)) " + " union " + " select u2.ID ,u2.LOGIN_NAME,2 from sys_user u2 where u2.PID in "
						+ " (select id  from sys_user u where u.PID=:userId and u.LOGIN_NAME not in " + " (select g.user_phone from sys_organization g where g.user_phone in "
						+ " (select LOGIN_NAME from sys_user u where u.PID=:userId)) ) and u2.LOGIN_NAME not in " + " (select g.user_phone from sys_organization g where g.user_phone in"
						+ " (select u2.LOGIN_NAME from sys_user u2 where u2.PID in (select id from sys_user u where u.PID=:userId))) " + " union " + " select u3.ID ,u3.LOGIN_NAME,3 from sys_user u3 where u3.PID in "
						+ " (select u2.ID  from sys_user u2 where u2.PID in " + " (select id  from sys_user u where u.PID=:userId and u.LOGIN_NAME not in " + " (select g.user_phone from sys_organization g where g.user_phone in"
						+ " (select LOGIN_NAME from sys_user u where u.PID=:userId))) and u2.LOGIN_NAME not in " + " (select g.user_phone from sys_organization g where g.user_phone in"
						+ " (select u2.LOGIN_NAME from sys_user u2 where u2.PID in " + " (select id from sys_user u where u.PID=:userId))))";

				Map<String, Object> params = new HashMap<String, Object>();
				params.put("userId", tuser.getId());
				Tuser ptuser = getTuser(tuser.getId());
				List<Object[]> ids = userDao.findBySql(sql, params);
				for (int i = 0; i < ids.size(); i++) {
					Object[] obj = ids.get(i);
					String updateUserAgent = "update  sys_user set  organization_id=" + tOrg.getId() + " , agent_id='" + ptuser.getAgentId() + "'  where id = " + obj[0];
					organizationDao.executeSql(updateUserAgent);
				}
			} catch (Exception e) {
				logger.error("AgentUpgrade user={} Failed", tuser.getLoginName(), e);
			}
		}
		orgChannelUserRateConfigService.updateOrgChannelUserRate(21, tuser.getId(), tuser.getAgentId());
		tuser.setUserType(21);
		userDao.update(tuser);
		return "升级成功";
	}

	// TODO edit
	@Override
	public void edit(User user) throws Exception {
		Tuser t = userDao.get(Tuser.class, user.getId());
		t.setIsAdmin(user.getIsAdmin());
		t.setLoginName(user.getLoginName());
		t.setName(user.getName());
		if (t.getOrganization().getId() != user.getOrganizationId()) {
			Set<Long> orgIds = organizationService.getOwerOrgIds(organizationService.getTorganizationInCacheByCode(t.getAgentId()).getId());
			// if (!orgIds.contains(user.getOrganizationId())) {
			// throw new Exception("用户迁移不允许跨OEM");
			// }
			t.setOrganization(organizationDao.get(Torganization.class, user.getOrganizationId()));
		}
		List<Trole> roles = new ArrayList<Trole>();
		if (user.getRoleIds() != null) {
			for (String roleId : user.getRoleIds().split(",")) {
				roles.add(roleDao.get(Trole.class, Long.valueOf(roleId)));
			}
		}
		t.setRoles(new HashSet<Trole>(roles));
		// t.setUserType(user.getUserType()); 用户类型不允许修改，影响账务
		t.setPhone(user.getPhone());
		// 冻结/解冻 用户
		if (!user.getState().equals(t.getState())) {
			t.setState(user.getState());
			Tbrokerage bk = brokerageDao.get("select t from Tbrokerage t left join t.user where t.user.id=" + user.getId());
			if (user.getState() == 0) {
				bk.setStatus(0);
			} else {
				bk.setStatus(1);
			}
			brokerageDao.update(bk);
		}
		if ((user.getPassword() != null) && !"".equals(user.getPassword())) {
			t.setPassword(MD5Util.md5(user.getPassword()));
		}
		if ((user.getStmPsw() != null) && !"".equals(user.getStmPsw())) {
			t.setStmPsw(MD5Util.md5(user.getStmPsw()));
		}
		if (StringUtil.isNotBlank(user.getPcode())) {
			Tuser userBycode = userDao.get("from Tuser t  where t.code = '" + user.getPcode() + "'");
			t.setParentUser(userBycode);
		} else {
			t.setParentUser(null);
		}
		t.setLastDateTime(new Date());
		t.setProvince(user.getProvince());
		t.setCountry(user.getCountry());
		t.setMerchantCity(user.getMerchantName());
		t.setMerchantName(user.getMerchantName());
		t.setIsAdmin(user.getIsAdmin());
		t.setAuthenticationStatus(user.getAuthenticationStatus());
		/* 当用户有非代理商变成代理商的时候设置其下面所有用户的归属 */
		if (t.getIsChnl() == 0 && user.getIsChnl() == 1) {
			try {
				// createSubAgent(user.getId(), user.getOrganizationId());

				// String sql = " select id ,LOGIN_NAME, 1 from sys_user u where
				// u.PID=:userId "
				// + " union "
				// + " select p.ID ,p.LOGIN_NAME,2 from SYS_USER P inner join
				// SYS_USER u on p.PID=u.ID where u.PID=:userId "
				// + " union select t.ID, t.LOGIN_NAME ,3 from SYS_USER t inner
				// join SYS_USER p on t.PID=p.ID inner join SYS_USER u on
				// p.PID=u.ID inner join SYS_USER r on u.PID=r.ID where
				// r.ID=:userId ";
				// 没机构的1级 + 没机构的1级剩下的没机构的2级 + 没机构的1级生下的的没机构的2级生下的3级
				String sql = " select id ,LOGIN_NAME, 1 from sys_user u where u.PID=:userId and u.LOGIN_NAME not in "
						+ " (select g.user_phone from sys_organization g where g.user_phone in( select LOGIN_NAME from sys_user u where u.PID=:userId)) " + " union " + " select u2.ID ,u2.LOGIN_NAME,2 from sys_user u2 where u2.PID in "
						+ " (select id  from sys_user u where u.PID=:userId and u.LOGIN_NAME not in " + " (select g.user_phone from sys_organization g where g.user_phone in "
						+ " (select LOGIN_NAME from sys_user u where u.PID=:userId)) ) and u2.LOGIN_NAME not in " + " (select g.user_phone from sys_organization g where g.user_phone in"
						+ " (select u2.LOGIN_NAME from sys_user u2 where u2.PID in (select id from sys_user u where u.PID=:userId))) " + " union " + " select u3.ID ,u3.LOGIN_NAME,3 from sys_user u3 where u3.PID in "
						+ " (select u2.ID  from sys_user u2 where u2.PID in " + " (select id  from sys_user u where u.PID=:userId and u.LOGIN_NAME not in " + " (select g.user_phone from sys_organization g where g.user_phone in"
						+ " (select LOGIN_NAME from sys_user u where u.PID=:userId))) and u2.LOGIN_NAME not in " + " (select g.user_phone from sys_organization g where g.user_phone in"
						+ " (select u2.LOGIN_NAME from sys_user u2 where u2.PID in " + " (select id from sys_user u where u.PID=:userId))))";

				Map<String, Object> params = new HashMap<String, Object>();
				params.put("userId", user.getId());
				params.put("userId", user.getId());
				Tuser tuser = getTuser(user.getId());
				List<Object[]> ids = userDao.findBySql(sql, params);
				for (int i = 0; i < ids.size(); i++) {
					Object[] obj = ids.get(i);
					String updateUserAgent = "update  sys_user set  organization_id=" + user.getOrganizationId() + " , agent_id='" + tuser.getAgentId() + "'  where id = " + obj[0];
					organizationDao.executeSql(updateUserAgent);
				}
			} catch (Exception e) {
				throw e;
			}
		}
		t.setIsChnl(user.getIsChnl());
		t.setSettlementStatus(user.getSettlementStatus());
		t.setAuthErrorNum(user.getAuthErrorNum());
		t.setLoginErrorNum(user.getLoginErrorNum());
		t.setAuthCardErrorNum(user.getAuthCardErrorNum());
		userDao.update(t);
	}

	@Override
	//添加黑名单
	public void addUserBlackList(String realName, String idNo, Integer status, String agentId) {
		TuserBlackList t = new TuserBlackList();
		t.setIdNo(idNo);
		t.setRealName(realName);
		t.setCreateTime(new Date());
		t.setStatus(status);
		if (StringUtil.isNotBlank(agentId)) {
			t.setOrganization(organizationService.getTorganizationInCacheByCode(agentId));
		}
		userBlackListDao.save(t);
	}

	@Override
	public void editBlackStatus(String idNo, long id) {
		int blackStatus = 0;
		List<Tuser> list = userDao.find("from Tuser t where t.idNo ='" + idNo + "'");
		if (list.size() > 1) {
			for (Tuser t : list) {
				blackStatus = t.getBlackStatus() == 0 ? 1 : 0;
				userDao.executeHql("update Tuser set blackStatus =" + blackStatus + " where idNo = '" + idNo + "'");
				logger.info("用户ID=" + id + "加入黑名单或移除黑名单，唯一用户ID为:" + t.getId() + ",身份证ID为：=" + idNo);
			}
		} else {
			blackStatus = list.get(0).getBlackStatus() == 0 ? 1 : 0;
			userDao.executeHql("update Tuser set blackStatus =" + blackStatus + " where idNo ='" + idNo + "'");
			logger.info("用户ID=" + id + "加入黑名单或移除黑名单，唯一用户ID为:" + list.get(0).getId() + ",身份证ID为：=" + idNo);
		}
		if (blackStatus != 0) {
			Long count = userBlackListDao.count("select count(t.id) from TuserBlackList t where t.status = 1 and t.idNo = '" + idNo + "'");
			if (count != 0) {
				userBlackListDao.executeHql("update TuserBlackList set status = " + 1 + " where idNo='" + idNo + "'");
			} else {
				addUserBlackList(list.get(0).getRealName(), list.get(0).getIdNo(), 1, list.get(0).getAgentId());
			}
		} else {
			userBlackListDao.executeHql("update TuserBlackList set status = " + 0 + " where idNo='" + idNo + "'");
		}
	}

	@Override
	public void createSubAgent(Long userId, Long orgId) throws Exception {
		BigInteger count = organizationDao.countBySql("select count(id) from tmplst");
		if (count.intValue() == 0) {
			organizationDao.executeSql("delete from tmplst");
			String sql = "call showAllUserByUserId2(" + userId + ")";
			organizationDao.executeSql(sql);
			Torganization org = organizationService.getTorganizationInCacheById(orgId);
			String updateUserAgent = "update  sys_user set  organization_id=" + orgId + " , agent_id='" + StringUtil.getAgentId(org.getCode()) + "'  where id   in (select id from tmpLst)";
			organizationDao.executeSql(updateUserAgent);
			organizationDao.executeSql("delete from tmplst");
		} else {
			throw new Exception("系统下线用户清算中，请稍后重试！");
		}
	}

	public void createSubAgentTwo(Long userId, Long orgId, String agentId) throws Exception {
		BigInteger count = organizationDao.countBySql("select count(id) from tmplst");
		if (count.intValue() == 0) {
			organizationDao.executeSql("delete from tmplst");
			String sql = "call showAllUserByUserId2(" + userId + ")";
			organizationDao.executeSql(sql);
			Torganization org = organizationService.getTorganizationInCacheById(orgId);
			String updateUserAgent = "update  sys_user set  organization_id=" + orgId + " , agent_id='" + agentId + "'  where id   in (select id from tmpLst)";
			organizationDao.executeSql(updateUserAgent);
			organizationDao.executeSql("delete from tmplst");
		} else {
			throw new Exception("系统下线用户清算中，请稍后重试！");
		}
	}

	@Override
	public void editAgent(User user) {

		Tuser t = userDao.get(Tuser.class, user.getId());
		t.setIsAdmin(user.getIsAdmin());
		t.setPhone(user.getPhone());
		// 冻结/解冻 用户
		if (!user.getState().equals(t.getState())) {
			t.setState(user.getState());
			Tbrokerage bk = brokerageDao.get("select t from Tbrokerage t left join t.user where t.user.id=" + user.getId());
			if (user.getState() == 0) {
				bk.setStatus(0);
			} else {
				bk.setStatus(1);
			}
			brokerageDao.update(bk);
		}
		if (StringUtil.isNotBlank(user.getPcode())) {
			Tuser userBycode = userDao.get("from Tuser t  where t.code = '" + user.getPcode() + "'");
			t.setParentUser(userBycode);
		}
		t.setLastDateTime(new Date());
		t.setMerchantCity(user.getMerchantName());
		t.setMerchantName(user.getMerchantName());
		t.setIsAdmin(user.getIsAdmin());
		t.setAuthenticationStatus(user.getAuthenticationStatus());
		t.setSettlementStatus(user.getSettlementStatus());
		t.setAuthErrorNum(user.getAuthErrorNum());
		t.setLoginErrorNum(user.getLoginErrorNum());
		t.setOrganization(organizationService.getTorganizationInCacheById(user.getOrganizationId()));
		userDao.update(t);
	}

	@Override
	public User get(Long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		Tuser t = userDao.get("select t from Tuser t  left join fetch t.roles role left join t.organization org where t.id = :id", params);
		User u = new User();
		BeanUtils.copyProperties(t, u);

		if (t.getOrganization() != null) {
			u.setOrganizationId(t.getOrganization().getId());
			// u.setAgentId(t.getOrganization().getCode());
			u.setAgentId(t.getAgentId());
			u.setOrganizationName(t.getOrganization().getName());
			u.setOrganizationAppName(t.getOrganization().getAppName());
		}

		if ((t.getRoles() != null) && !t.getRoles().isEmpty()) {
			String roleIds = "";
			String roleNames = "";
			boolean b = false;
			for (Trole role : t.getRoles()) {
				if (b) {
					roleIds += ",";
					roleNames += ",";
				} else {
					b = true;
				}
				roleIds += role.getId();
				roleNames += role.getName();
			}
			u.setRoleIds(roleIds);
			u.setRoleNames(roleNames);
		}
		if (t.getParentUser() != null) {
			u.setParentName(t.getParentUser().getName());
			u.setPcode(t.getParentUser().getCode());
			u.setPid(t.getParentUser().getId());
		}
		return u;
	}

	@Override
	public Tuser getTuser(Long id) {
		System.out.println("userid:"+id);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		return userDao.get("select t from Tuser t left join t.organization g where t.id = :id", params);
	}

	@Override
	public User getSimpleUser(Long id) {
		Tuser t = getTuser(id);
		if (t != null) {
			User u = new User();
			BeanUtils.copyProperties(t, u);
			return u;
		}
		return null;
	}

	@Override
	public User loginApp(String appName, String agentId, String loginPsw) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("loginname", appName);
			params.put("password", loginPsw);
			params.put("orgCode", StringUtil.getAgentId(agentId));
			Tuser t = userDao.get("select t from Tuser t left join t.organization g where  t.loginName = :loginname and t.password = :password and t.agentId = :orgCode", params);
			if (t != null) {
				if (t.getLoginErrorNum() - Integer.parseInt(login_error_num) >= 0) {
					return null;
				}
				/* 更新用户登录 */
				User u = new User();
				BeanUtils.copyProperties(t, u);
				t.setLastDateTime(new Date());
				t.setLoginErrorNum(0);
				userDao.update(t);
				return u;
			} else {
				params.remove("password");
				Tuser tt = userDao.get("select t from Tuser t left join t.organization g where  t.loginName = :loginname and t.agentId = :orgCode", params);
				if (tt != null && tt.getLoginErrorNum() - Integer.parseInt(login_error_num) <= 0) {
					tt.setLoginErrorNum(tt.getLoginErrorNum() + 1);
					userDao.update(tt);
				}
			}
		} catch (Exception e) {
			logger.error("add user error", e);
		}
		return null;
	}

	@Override
	public User loginManagerSystem(User user) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginname", user.getLoginName());
		params.put("password", MD5Util.md5(user.getPassword()));
		List<Tuser> ts = userDao.find("select t from Tuser t left join t.roles where t.isAdmin=1 and t.loginName = :loginname and t.password = :password", params);
		if (CollectionUtil.isEmpty(ts)) {
			return null;
		}

		Tuser tUser = ts.get(0);
		Set<Trole> tRoles = tUser.getRoles();
		Set<Long> roleIds = new HashSet<Long>();
		for (Trole tr : tRoles) {
			roleIds.add(tr.getId());
		}

		String roleIdStr = com.cn.flypay.model.util.StringUtil.join(roleIds);
		logger.info("troles=" + roleIdStr);
		// update：2017.11.23 登录的是代理商，登录不通过
		if (StringUtil.equals(roleIdStr, "23")) {
			return null;
		}
		User u = new User();
		BeanUtils.copyProperties(tUser, u);
		u.setRoleIds(roleIdStr);
		return u;
	}

	// update：2017.11.23 代理商登录
	@Override
	public User loginAgentManagerSystem(User user) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginname", user.getLoginName());
		params.put("password", MD5Util.md5(user.getPassword()));
		// 校验登录名、密码是否正确，是否有登录权限
		List<Tuser> ts = userDao.find("select t from Tuser t left join fetch t.roles where t.isAdmin=1 and t.loginName = :loginname and t.password = :password", params);
		if (CollectionUtil.isEmpty(ts)) {
			return null;
		}
		// 查询到的用户
		Tuser tUser = ts.get(0);
		// 校验用户角色roleID是否仅为代理商--23，否者不允许登录
		Set<Trole> roles = tUser.getRoles();
		Set<Long> roleIds = new HashSet<Long>();
		for (Trole tr : roles) {
			roleIds.add(tr.getId());
		}
		String roleIdStr = com.cn.flypay.model.util.StringUtil.join(roleIds);
		logger.info("troles=" + roleIdStr);
		if (StringUtil.equals(roleIdStr, "23")) {
			User u = new User();
			BeanUtils.copyProperties(tUser, u);
			u.setRoleIds(roleIdStr);
			return u;
		}
		return null;
	}

	@Override
	public List<String> listResource(Long id) {
		List<String> resourceList = new ArrayList<String>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		Tuser t = userDao.get("from Tuser t join fetch t.roles role join fetch role.resources resource where t.isAdmin=1 and t.id = :id", params);
		if (t != null) {
			Set<Trole> roles = t.getRoles();
			if ((roles != null) && !roles.isEmpty()) {
				for (Trole role : roles) {
					Set<Tresource> resources = role.getResources();
					if ((resources != null) && !resources.isEmpty()) {
						for (Tresource resource : resources) {
							if ((resource != null) && (resource.getUrl() != null)) {
								resourceList.add(resource.getUrl());
							}
						}
					}
				}
			}
		}
		return resourceList;
	}

	public Set<Long> listUserResIds(Long userId) {
		Set<Long> resListIds = new HashSet<Long>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		Tuser t = userDao.get("from Tuser t join fetch t.roles role join fetch role.resources resource where t.isAdmin=1 and t.id = :userId", params);
		if (t != null) {
			Set<Trole> roles = t.getRoles();
			if ((roles != null) && !roles.isEmpty()) {
				for (Trole role : roles) {
					Set<Tresource> resources = role.getResources();
					if ((resources != null) && !resources.isEmpty()) {
						for (Tresource resource : resources) {
							if ((resource != null) && (resource.getUrl() != null)) {
								resListIds.add(resource.getId());
							}
						}
					}
				}
			}
		}
		return resListIds;
	}

	@Override
	public List<User> dataGrid(User user, PageFilter ph) {
		List<User> ul = new ArrayList<User>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from Tuser t left join  t.parentUser p left join t.organization tog";
		if (user.getSubLevel() != null) {
			System.out.println("user.getSubLevel():"+user.getSubLevel());
			hql = " select t from Tuser t left join  t.parentUser p left join t.parentUser.parentUser pp left join t.parentUser.parentUser.parentUser ppp left join t.organization tog";
		}
		List<Tuser> l = new ArrayList<Tuser>();

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = request.getSession();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		List<Object[]> accs = roleDao.findBySql("select s.ROLE_ID from sys_user_role s where s.USER_ID=" + sessionInfo.getId());
		if (String.valueOf(accs.get(0)).equals("2") || String.valueOf(accs.get(0)).equals("4") || String.valueOf(accs.get(0)).equals("16") || String.valueOf(accs.get(0)).equals("17")) {
			System.out.println("accs.get(0):"+accs.get(0));
			if (ph != null) {
				l = userDao.find(hql + whereHql(user, params) + whereHqlTwo(user, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
			} else {
				l = userDao.find(hql + whereHql(user, params) + whereHqlTwo(user, params), params);
			}

		} else {
			if (ph != null) {
				l = userDao.find(hql + whereHql(user, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
			} else {
				l = userDao.find(hql + whereHql(user, params), params);
			}
		}

		for (Tuser t : l) {
			User u = new User();
			BeanUtils.copyProperties(t, u);
			Set<Trole> roles = t.getRoles();
			if ((roles != null) && !roles.isEmpty()) {
				String roleIds = "";
				String roleNames = "";
				boolean b = false;
				for (Trole tr : roles) {
					if (b) {
						roleIds += ",";
						roleNames += ",";
					} else {
						b = true;
					}
					roleIds += tr.getId();
					roleNames += tr.getName();
				}
				u.setRoleIds(roleIds);
				u.setRoleNames(roleNames);
			}
			if (t.getOrganization() != null) {
				u.setOrganizationId(t.getOrganization().getId());
				u.setOrganizationName(t.getOrganization().getName());
				if (u.getLoginName().equals(t.getOrganization().getUserPhone())) {// 同名代理
					u.setUserUpgradeType(t.getOrganization().getUserUpgradeType());
				} else {
					u.setUserUpgradeType(-1);
				}
			}
			if (t.getParentUser() != null) {
				Tuser tu = userDao.get(Tuser.class, t.getParentUser().getId());
				u.setParentName(tu.getName());
				u.setPid(tu.getId());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public List<UserBlackList> dataGrid(UserBlackList param, PageFilter ph) {
		List<UserBlackList> ul = new ArrayList<UserBlackList>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TuserBlackList t";
		List<TuserBlackList> l = userBlackListDao.find(hql + whereHqlThree(param, params), ph.getPage(), ph.getRows());
		for (TuserBlackList t : l) {
			UserBlackList u = new UserBlackList();
			BeanUtils.copyProperties(t, u);
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(User user, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from Tuser t  left join  t.parentUser p left join t.organization tog";
		if (user.getSubLevel() != null) {
			hql = " from Tuser t  left join  t.parentUser p left join t.parentUser.parentUser pp left join t.parentUser.parentUser.parentUser ppp left join t.organization tog";
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = request.getSession();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		List<Object[]> accs = roleDao.findBySql("select s.ROLE_ID from sys_user_role s where s.USER_ID=" + sessionInfo.getId());
		Long l = null;
		if (String.valueOf(accs.get(0)).equals("2") || String.valueOf(accs.get(0)).equals("4") || String.valueOf(accs.get(0)).equals("16") || String.valueOf(accs.get(0)).equals("17")) {
			l = userDao.count("select count(t.id) " + hql + whereHql(user, params) + whereHqlTwo(user, params), params);
		} else {
			l = userDao.count("select count(t.id) " + hql + whereHql(user, params), params);
		}

		return l;
	}

	@Override
	public Long count(UserBlackList userBlackList, PageFilter ph) {
		String hql = "select count(*) from TuserBlackList t";
		return userBlackListDao.count(hql);
	}

	private String whereHql(User user, Map<String, Object> params) {
		String hql = "";
		if (user != null) {
			hql += " where 1=1 ";
			if (user.getName() != null) {
				hql += " and t.name like :name";
				params.put("name", "%%" + user.getName() + "%%");
			}

			if (user.getLoginName() != null && user.getSubLevel() != null) {
				boolean flag = false;
				try {
					List<Object[]> list = userDao
							.findBySql("SELECT sr.id FROM sys_user su LEFT JOIN sys_user_role sur ON sur.USER_ID = su.ID LEFT JOIN sys_role sr ON sr.ID = sur.ROLE_ID WHERE su.LOGIN_NAME = '" + user.getOperateUser().getLoginName() + "'");
					if (String.valueOf(list.get(0)).equals("1")) {
						flag = true;
					} else {
						List<Object[]> l1 = userDao.findBySql("SELECT su.agent_id FROM sys_user su WHERE su.LOGIN_NAME = '" + user.getOperateUser().getLoginName() + "'");
						List<Object[]> l2 = userDao.findBySql("SELECT su.agent_id FROM sys_user su WHERE su.LOGIN_NAME = '" + user.getLoginName() + "'");
						String agentId1 = String.valueOf(l1.get(0));
						String agentId2 = String.valueOf(l2.get(0));
						if (agentId1.equals(agentId2)) {
							flag = true;
						}
					}
				} catch (Exception e) {
					logger.equals(e);
				}
				if (flag) {
					User u = null;
					if (user.getLoginName() != null && StringUtil.isNotBlank(user.getLoginName())) {
						StringBuffer sb = new StringBuffer();
						sb.append("select t from Tuser t where t.loginName='" + user.getLoginName() + "'");
						if (user.getOrganizationId() != null) {
							Organization org = organizationService.get(user.getOrganizationId());
							if (org.getCode() != null) {
								sb.append(" and t.agentId='" + org.getCode() + "'");
							}
						}
						Tuser t = userDao.get(sb.toString());
						if (t != null) {
							u = get(t.getId());
							user.setOperateUser(u);
							user.getOperateUser().setOperateUser(user.getOperateUser());
							user.getOperateUser().getOperateUser().setOperateUser(user.getOperateUser().getOperateUser());
						}
					}
				}

				if (user.getSubLevel() == 1) {
					System.out.println("user.getSubLevel() == 1");
					hql += " and p.loginName = :pLoginName";
					params.put("pLoginName", user.getOperateUser().getLoginName());
				}
				if (user.getSubLevel() == 2) {
					System.out.println("user.getSubLevel() == 2");
					hql += " and pp.loginName = :ppLoginName";
					params.put("ppLoginName", user.getOperateUser().getOperateUser().getLoginName());
				}
				if (user.getSubLevel() == 3) {
					System.out.println("user.getSubLevel() == 3");
					hql += " and ppp.loginName = :pppLoginName";
					params.put("pppLoginName", user.getOperateUser().getOperateUser().getOperateUser().getLoginName());
				}
			} else if (user.getLoginName() != null) {
				hql += " and t.loginName like :loginName";
				params.put("loginName", "%%" + user.getLoginName() + "%%");
			}
			if (StringUtil.isNotBlank(user.getRealName())) {
				hql += " and t.realName like :realName";
				params.put("realName", "%%" + user.getRealName().trim() + "%%");
			}
			if (StringUtil.isNotBlank(user.getIdNo())) {
				hql += " and t.idNo like :idNo";
				params.put("idNo", "%%" + user.getIdNo().trim() + "%%");
			}
			if (user.getAuthenticationStatus() != null) {
				hql += " and t.authenticationStatus = :authenticationStatus";
				params.put("authenticationStatus", user.getAuthenticationStatus());
			}
			if (user.getMerchantType() != null) {
				hql += " and t.merchantType = :merchantType";
				params.put("merchantType", user.getMerchantType());
			}
			if (user.getUserType() != null) {
				hql += " and t.userType = :userType";
				params.put("userType", user.getUserType());
			}
			if (user.getIsAdmin() != null) {
				hql += " and t.isAdmin =:isAdmin";
				params.put("isAdmin", user.getIsAdmin());
			}
			if (StringUtil.isNotEmpty(user.getCreatedatetimeStart())) {
				hql += " and t.createDatetime >= :createdatetimeStart";
				try {
					params.put("createdatetimeStart", DateUtil.getDateFromString(user.getCreatedatetimeStart()));
				} catch (Exception e) {
				}
			}
			if (StringUtil.isNotEmpty(user.getCreatedatetimeEnd())) {
				hql += " and t.createDatetime <= :createdatetimeEnd";
				try {
					params.put("createdatetimeEnd", DateUtil.getDateFromString(user.getCreatedatetimeEnd()));
				} catch (Exception e) {
				}
			}
			if (user.getOrganizationId() != null) {
				hql += " and  t.organization.id in(:orgIds)";
				params.put("orgIds", organizationService.getOwerOrgIds(user.getOrganizationId()));
			}
			if (user.getUserUpgradeType() != null) {
				hql += " and  tog.userUpgradeType = :userUpgradeType";
				params.put("userUpgradeType", user.getUserUpgradeType());
			}
			if (user.getOperateUser() != null) {
				hql += " and  tog.id in(:operaterOrgIds)";
				params.put("operaterOrgIds", organizationService.getOwerOrgIds(user.getOperateUser().getOrganizationId()));
				if (user.getSubLevel() != null) {
					if (user.getSubLevel() == 1) {
						params.put("operaterOrgIds", organizationService.getOwerOrgIds(user.getOperateUser().getOrganizationId()));
					}
					if (user.getSubLevel() == 2) {
						if (user.getOperateUser().getOperateUser() != null) {
							params.put("operaterOrgIds", organizationService.getOwerOrgIds(user.getOperateUser().getOperateUser().getOrganizationId()));
						}
					}
					if (user.getSubLevel() == 3) {
						if (user.getOperateUser().getOperateUser().getOperateUser() != null) {
							params.put("operaterOrgIds", organizationService.getOwerOrgIds(user.getOperateUser().getOperateUser().getOperateUser().getOrganizationId()));
						}
					}
				}
			}

		}
		return hql;
	}

	private String whereHqlTwo(User user, Map<String, Object> params) {
		String hql = "";
		if (user != null) {
			if (user.getOperateUser().getId() != null) {
				hql += " and t.pid = :id";
				params.put("id", user.getOperateUser().getId());// user.getOperateUser().getId()
			}
		}
		return hql;
	}

	private String whereHqlThree(UserBlackList userBlackList, Map<String, Object> params) {
		String hql = "";
		if (userBlackList != null) {
			hql += " where 1=1 ";
			if (userBlackList.getStatus() != null) {
				hql += " and status = 1";
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
	public boolean editUserPwd(SessionInfo sessionInfo, String oldPwd, String pwd) {
		Tuser u = userDao.get(Tuser.class, sessionInfo.getId());
		if (u.getPassword().equalsIgnoreCase(MD5Util.md5(oldPwd))) {// 说明原密码输入正确
			u.setPassword(MD5Util.md5(pwd));
			return true;
		}
		return false;
	}

	@Override
	public boolean updateUserWeiXinCode(String openid, String nickname, String unionid, Long userid) {
		Tuser u = userDao.get(Tuser.class, userid);
		u.setOpenId(openid);
		u.setNickname(nickname);
		u.setUnionid(unionid);
		userDao.update(u);
		return false;
	}

	@Override
	public List<User> getByLoginName(User user) {

		List<Tuser> list = userDao.find("select t from Tuser t  where t.loginName = '" + user.getLoginName() + "' and t.agentId='" + user.getAgentId() + "'");
		List<User> users = new ArrayList<User>();
		for (int i = 0; i < list.size(); i++) {
			User u = new User();
			BeanUtils.copyProperties(list.get(i), u);
			users.add(u);
		}
		return users;
	}

	@Override
	public List<User> getUserListByUserType() {
		List<Tuser> list = userDao.find("from Tuser t where t.userType=1 and t.state=0");
		List<User> users = new ArrayList<User>();
		for (int i = 0; i < list.size(); i++) {
			User u = new User();
			BeanUtils.copyProperties(list.get(i), u);
			users.add(u);
		}
		return users;
	}

	@Override
	public User getMemntUser(User user) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginname", user.getLoginName());
		List<Tuser> ts = userDao.find("from Tuser t where t.loginName = :loginname", params);
		for (Tuser t : ts) {
			User u = new User();
			BeanUtils.copyProperties(t, u);
			return u;
		}
		return null;
	}

	@Override
	public String[] getUserListNameByUserType() {
		List<Tuser> list = userDao.find("from Tuser t where t.userType=1 and t.state=0");
		String[] users = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			users[i] = list.get(i).getName();
		}
		return users;
	}

	@Override
	public List<User> getByPhone(User user) {
		List<Tuser> list = userDao.find("from Tuser t  where t.name = '" + user.getName() + "'");
		List<User> users = new ArrayList<User>();
		for (int i = 0; i < list.size(); i++) {
			User u = new User();
			BeanUtils.copyProperties(list.get(i), u);
			users.add(u);
		}
		return users;
	}

	@Override
	public User getByCode(String code) {
		Tuser t = userDao.get("select t from Tuser t  left join t.organization g where t.code = '" + code + "'");
		User u = new User();
		if (t != null) {
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrganizationName(t.getOrganization().getName());
				u.setOrganizationAppName(t.getOrganization().getAppName());
			}
		} else {
			return null;
		}
		return u;
	}

	@Override
	public boolean editUserIp(SessionInfo sessionInfo) {
		Tuser u = userDao.get(Tuser.class, sessionInfo.getId());
		userDao.update(u);
		return true;
	}

	@Override
	public List<UserImage> findToCheckImagesByUserId(Long id) {
		Set<Integer> types = new HashSet<Integer>();
		types.add(SysImage.image_type.front_ID.getCode());
		types.add(SysImage.image_type.back_ID.getCode());
		types.add(SysImage.image_type.hand_ID.getCode());
		return findUserImagesByUserIdAndTypes(id, -1, types);
	}

	@Override
	public List<UserImage> findToCheckMerchantImagesByUserId(Long id) {
		Set<Integer> types = new HashSet<Integer>();
		types.add(SysImage.image_type.front_ID.getCode());
		types.add(SysImage.image_type.back_ID.getCode());
		types.add(SysImage.image_type.LICENSE_ID.getCode());
		types.add(SysImage.image_type.HAND_IN_CASHIER_DESK.getCode());
		types.add(SysImage.image_type.INTERIOR_VIEW_1.getCode());
		types.add(SysImage.image_type.INTERIOR_VIEW_2.getCode());
		types.add(SysImage.image_type.INTERIOR_VIEW_3.getCode());
		types.add(SysImage.image_type.SHOP_ID.getCode());

		return findUserImagesByUserIdAndTypes(id, -1, types);
	}

	private List<UserImage> findUserImagesByUserIdAndTypes(Long id, Integer status, Set<Integer> types) {
		List<UserImage> images = new ArrayList<UserImage>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", id);
		params.put("type", types);
		List<TsysImage> tImsages = imageDao.find("select  t from TsysImage t where t.user.id=:uid and t.type in(:type) order by id desc", params);
		for (TsysImage t : tImsages) {
			if (types.contains(t.getType())) {
				// types.remove(t.getType());
				UserImage u = new UserImage();
				BeanUtils.copyProperties(t, u);
				images.add(u);
			}
		}
		return images;
	}

	@Override
	public void updateUserAuthStatus(Long id, Boolean status, String errorInfo) {
		if (id != null && status != null) {
			Tuser user = getTuser(id);
			user.setAuthenticationStatus(status ? User.authentication_status.SUCCESS.getCode() : User.authentication_status.FAILURE.getCode());// 认证状态
			if (status) {
				user.setAuthDateTime(new Date());
			}
			userDao.update(user);

			AuthenticationLog authLog = new AuthenticationLog(id, 1, 0, user.getIdNo(), user.getRealName(), user.getLoginName(), null, errorInfo, user.getAgentId());
			authLog.setStatus(status ? User.authentication_status.SUCCESS.getCode() : User.authentication_status.FAILURE.getCode());
			authenticationService.saveAuthentication(authLog);

			imageDao.executeHql("update TsysImage t set t.status=" + (status ? 1 : 2) + " where  t.status=-1 and  t.user.id=" + id);
			try {
				if (!status) {
					msgHistoryService.sendSmsToUserPhone(user.getLoginName(), StringUtil.getAgentId(user.getAgentId()), 50);
				} else {
					accountPointService.updatePoint(user.getParentUser().getId(), AccountPoint.pointTypes_popularity, "您推荐的" + user.getLoginName() + "_" + user.getRealName() + "实名认证通过了");

					Tuser puser = user.getParentUser();
					Torganization torg = organizationService.getTorganizationInCacheByCode(puser.getAgentId());
					/* 运营商升级方式 为推荐人认证成功后在升级的方式 */
					if (torg != null && torg.getUserUpgradeType() != Organization.user_upgrade_type.SHOPPING.getCode()) {

						AccountPoint accountPoint = accountPointService.getAccountPointByUserId(puser.getId());
						if (accountPoint.getSubPersonNum() == torg.getDiamondNum() || accountPoint.getSubPersonNum() == torg.getGoldNum()) {
							try {
								updateUserType(puser.getId(), null, accountPoint.getSubPersonNum());
							} catch (Exception e) {
								logger.error("用户升级异常", e);
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error("发短信失败", e);
			}
		}
	}

	public void updateTuser(Tuser u) {
		userDao.update(u);
	}

	public String updateUserAuthMerchantStatus(Long id, Integer merchantType, String errorInfo) {
		if (id != null && merchantType != null) {

			Tuser user = getTuser(id);
			user.setMerchantType(merchantType);// 认证状态
			user.setMerchantAuthMsg(errorInfo);
			userDao.update(user);

			AuthenticationLog authLog = new AuthenticationLog(id, AuthenticationLog.auth_type.manual_merchant.getCode(), 0, user.getIdNo(), user.getMerchantName(), user.getLoginName(), null, errorInfo, user.getAgentId());
			authLog.setStatus(
					(merchantType == User.merchant_type.REAL_MERCHANT.getCode() || merchantType == User.merchant_type.NONE_MERCHANT.getCode()) ? User.authentication_status.SUCCESS.getCode() : User.authentication_status.FAILURE.getCode());
			authenticationService.saveAuthentication(authLog);

			imageDao.executeHql(
					"update TsysImage t set t.status=" + ((merchantType == User.merchant_type.REAL_MERCHANT.getCode() || merchantType == User.merchant_type.NONE_MERCHANT.getCode()) ? 1 : 2) + " where  t.user.id=" + id + " and t.status=-1");
			try {
				if (merchantType == User.merchant_type.REAL_MERCHANT.getCode() || merchantType == User.merchant_type.NONE_MERCHANT.getCode()) {
					userMerchantConfigService.createUserMerchants(user);
				} else {
					msgHistoryService.sendSmsToUserPhone(user.getLoginName(), StringUtil.getAgentId(user.getAgentId()), 60);
				}
			} catch (Exception e) {
				logger.error("发信发送失败", e);
			}
		}
		return "";
	}

	@Override
	public User getUserByToken(String token) {

		Tuser t = userDao.get("select t from Tuser t ,TuserToken tk  where t.id=tk.userId and tk.token = '" + token + "'");
		if (t != null) {
			User u = new User();
			BeanUtils.copyProperties(t, u);
			return u;
		} else {
			return null;
		}
	}

	@Override
	public Tuser findTuserByUserCodeOrPhone(String pCode, String agentId) {
		Tuser puser = null;
		if (StringUtil.isNotBlank(pCode) && pCode.trim().length() == 6) {
			puser = userDao.get("select t from Tuser t left join t.organization org where t.code = '" + pCode + "' and t.agentId='" + agentId + "'");

		} else if (StringUtil.isNotBlank(pCode) && pCode.trim().length() == 11) {
			puser = userDao.get("select t from Tuser t left join t.organization org where t.loginName = '" + pCode + "' and t.agentId='" + agentId + "'");
		}
		return puser;
	}

	@Override
	public Tuser findTuserByUserCode(String userCode) {
		Tuser puser = null;
		if (StringUtil.isNotBlank(userCode) && userCode.trim().length() == 6) {
			puser = userDao.get("select t from Tuser t left join t.organization org where t.code = '" + userCode + "'");
		}
		return puser;
	}

	@Override
	public User findUserByUserCodeOrPhone(String userCodeOrPhone, String agentId) {
		Tuser puser = null;
		if (StringUtil.isNotBlank(userCodeOrPhone) && userCodeOrPhone.trim().length() == 6) {
			puser = userDao.get("select t from Tuser t left join t.organization org where t.code = '" + userCodeOrPhone + "'");

		} else if (StringUtil.isNotBlank(userCodeOrPhone) && userCodeOrPhone.trim().length() == 11) {
			puser = userDao.get("select t from Tuser t left join t.organization org where t.loginName = '" + userCodeOrPhone + "'  and org.code like '" + StringUtil.getAgentId(agentId) + "%'");
		}
		if (puser != null) {
			User user = new User();
			BeanUtils.copyProperties(puser, user);
			if (puser.getOrganization() != null) {
				user.setOrganizationId(puser.getOrganization().getId());
				user.setOrganizationName(puser.getOrganization().getName());
			}
			return user;
		}
		return null;
	}

	@Override
	public User addUser(String appLoginName, String loginPwd, String pCode, String agentId) {

		try {
			logger.info("--- add user start----");
			Torganization org = null;
			Tuser puser = findTuserByUserCodeOrPhone(pCode, agentId);
			if (puser == null) {
				if (agentId.equals("F00060009")) {
					org = organizationDao.get(Torganization.class, 51l);
					puser = userDao.get(Tuser.class, 71491l);
				} else if (agentId.equals("F20160013")) {
					org = organizationDao.get(Torganization.class, 110l);
					puser = userDao.get(Tuser.class, 89150l);
				} else {
					org = organizationDao.get(Torganization.class, 1l);
					puser = userDao.get(Tuser.class, 1l);
				}
			} else {
				org = puser.getOrganization();
			}

			logger.info("parent name" + puser.getName());
			Tuser t = initUserAttr(appLoginName, loginPwd, org, agentId);

			try {
				updateParentGrade(t, puser);
			} catch (Exception e) {
				logger.error("更新用户推荐人等级时异常", e);
			}
			t.setParentUser(puser);

			userDao.save(t);

			// 配置用户费率
			initUserSettlementConfig(t);

			initUserAccountPoint(t);
			logger.info("create user settlement config ,id=" + t.getId());

			saveInitUserAccountAndBrokerage(t);

			logger.info("create user account and brokerage");
			User u = new User();
			BeanUtils.copyProperties(t, u);
			return u;
		} catch (Exception e) {
			logger.error("add user error", e);
			throw e;
		}
	}

	@Override
	public User addRootUserForOrganization(String appLoginName, String loginPwd, String pCode, Long orgId) throws Exception {

		try {
			logger.info("--- add root  user start----");
			Torganization org = organizationDao.get(Torganization.class, orgId);
			if (org.getAgentType() != Organization.agent_type.OEM.getCode()) {
				throw new Exception("只能为OEM添加根用户");
			}
			Tuser puser = findTuserByUserCode(pCode);
			if (puser == null) {
				throw new Exception("推荐人不存在，请确认后再重试");
			}
			logger.info("parent name" + puser.getName());
			Tuser t = initUserAttr(appLoginName, loginPwd, org, StringUtil.getAgentId(org.getCode()));

			t.setParentUser(puser);
			userDao.save(t);

			/* 根据用户所在运营商设置用户的结算配置信息 */
			initUserSettlementConfig(t);
			/* 积分账户 */
			initUserAccountPoint(t);

			logger.info("create user settlement config ,id=" + t.getId());
			/* 账户和佣金账户 */
			saveInitUserAccountAndBrokerage(t);

			logger.info("create user account and brokerage");
			User u = new User();
			BeanUtils.copyProperties(t, u);
			return u;
		} catch (Exception e) {
			logger.error("add user error", e);
			throw e;
		}
	}

	private Tuser initUserAttr(String appLoginName, String loginPwd, Torganization org, String agentId) {
		Tuser t = new Tuser();
		t.setUserType(24);// 普通用户
		t.setAuthenticationStatus(User.authentication_status.INIT.getCode());
		t.setCode(getUserCode());
		t.setCreateDatetime(new Date());
		t.setIsAdmin(GlobalConstant.ZERO);
		t.setIsChnl(GlobalConstant.ZERO);
		t.setIsDefault(GlobalConstant.ZERO);
		t.setLastDateTime(new Date());
		t.setState(GlobalConstant.ENABLE);
		t.setLoginName(appLoginName);
		t.setName(appLoginName);
		t.setOrganization(org);
		t.setPassword(loginPwd);
		t.setPhone(appLoginName);
		t.setSettlementStatus(GlobalConstant.ZERO);// 非商家
		t.setVoiceType(0);
		/* 用户标识 */
		t.setAgentId(agentId);
		/* 默认为个体 */
		t.setMerchantType(User.merchant_type.PERSON.getCode());
		/* 默认角色为普通用户 */
		Set<Trole> roleSet = new HashSet<Trole>();
		roleSet.add(roleService.findAllRole().get(2l));
		t.setRoles(roleSet);
		t.setPrivacyType(1);// 隐私权限，默认开启
		return t;
	}

	/**
	 * 更新推广人信息 /发送消息给推广人
	 * 
	 * @param newUser
	 * @param puser
	 */
	private void updateParentGrade(Tuser newUser, Tuser puser) {
		String title = "您推荐的" + newUser.getLoginName() + "来了！";
		String desc = String.format(JiguangUtil.ALTER_PARENT_POPULAR, newUser.getLoginName(), puser.getOrganization().getAppName());
		InfoList t = new InfoList(puser.getId(), title, InfoList.info_Type.person.getCode(), desc, 0, 0);
		try {
			producerService.sendInfoList(t);
		} catch (Exception e) {
			logger.error("send jiguang msg error", e);
		}
	}

	/**
	 * 初始化用户的积分账户
	 * 
	 * @param newUser
	 * @param puser
	 */
	private void initUserAccountPoint(Tuser newUser) {
		try {
			accountPointService.initAccountPoint(newUser);
			// 送推广人的积分
			// accountPointService.updatePoint(puser.getId(),
			// AccountPoint.pointTypes_popularity, title);
		} catch (Exception e) {
			logger.error("初始化积分账户异常", e);
		}
	}

	/**
	 * 初始化用户的基本户 和佣金户
	 * 
	 * @param t
	 */
	private void saveInitUserAccountAndBrokerage(Tuser t) {
		/* 用户佣金账户 */
		Tbrokerage bk = new Tbrokerage();
		bk.setStatus(0);
		bk.setUser(t);
		bk.setHistoryBrokerage(BigDecimal.ZERO);
		bk.setYesterdayBrokerage(BigDecimal.ZERO);
		bk.setLockBrokerage(BigDecimal.ZERO);
		bk.setBrokerage(BigDecimal.ZERO);
		bk.setTotalAgentBrokerage(BigDecimal.ZERO);
		bk.setTotalBrokerage(BigDecimal.ZERO);
		bk.setTotalTransBrokerage(BigDecimal.ZERO);
		bk.setTotalLeadBrokerage(BigDecimal.ZERO);
		brokerageDao.save(bk);
		/* 用户账户 */
		Taccount account = new Taccount();
		account.setAvlAmt(BigDecimal.ZERO);
		account.setUser(t);
		account.setStatus(0);
		account.setLockOutAmt(BigDecimal.ZERO);
		account.setThroughAmt(BigDecimal.ZERO);
		account.setPerMonthInAmt(BigDecimal.ZERO);
		account.setPerMonthOutAmt(BigDecimal.ZERO);
		account.setHistoryAmt(BigDecimal.ZERO);
		account.setYesterdayAmt(BigDecimal.ZERO);
		account.setTodayOutAmt(BigDecimal.ZERO);
		account.setT1Amt(BigDecimal.ZERO);
		account.setT2Amt(BigDecimal.ZERO);
		account.setT3Amt(BigDecimal.ZERO);
		account.setT4Amt(BigDecimal.ZERO);
		account.setT5Amt(BigDecimal.ZERO);
		account.setT6Amt(BigDecimal.ZERO);
		account.setT7Amt(BigDecimal.ZERO);
		account.setT8Amt(BigDecimal.ZERO);
		account.setT9Amt(BigDecimal.ZERO);
		account.setT10Amt(BigDecimal.ZERO);
		account.setT11Amt(BigDecimal.ZERO);
		account.setD1Amt(BigDecimal.ZERO);
		account.setD2Amt(BigDecimal.ZERO);
		accDao.save(account);
	}

	/**
	 * 用户与用户提现额度配置一一对应 初始化用户费率配置
	 * 
	 * @param org
	 * @param t
	 */
	private void initUserSettlementConfig(Tuser t) {
		TuserSettlementConfig sc = new TuserSettlementConfig();
		Torganization org = organizationService.getTorganizationInCacheByCode(t.getAgentId());
		if (org != null) {
			/* 用户提现配置信息 */
			sc.setMaxRabaleAmt(org.getMaxRabaleAmt());
			sc.setMinRabaleAmt(org.getMinRabaleAmt());
			sc.setRabaleFee(org.getRabaleFee());

			sc.setMaxT0Amt(org.getMaxT0Amt());
			sc.setMinT0Amt(org.getMinT0Amt());
			sc.setT0Fee(org.getT0Fee());

			sc.setMaxT1Amt(org.getMaxT1Amt());
			sc.setMinT1Amt(org.getMinT1Amt());
			sc.setT1Fee(org.getT1Fee());
			sc.setMaxTodayOutAmt(org.getMaxTodayOutAmt());
			sc.setInputFee(org.getDefaultInputFee());
			sc.setShareFee(org.getDefaultShareFee());
			if (org.getReductionUserRateType() == Organization.reduction_user_rate_type.POINT_SHOPPING.getCode()) {
				// 积分降费率
				orgPointConfigService.initUserSettlementWhenPoint(t.getAgentId(), sc);
			} else if (org.getReductionUserRateType() == Organization.reduction_user_rate_type.USER_UPGRADE.getCode()) {
				// 升级降费率
				userSettlementConfigService.setSettlementConfigWhenUserUpdate(sc, 24, org.getId());
				sc.setShareFee(BigDecimal.ZERO);
			}
		}
		sc.setUser(t);
		t.setSettlementConfig(sc);
		userSettlementConfigDao.save(sc);
	}

	@Override
	public UserSettlementConfig getStmConfigByUserId(Long userId) {
		TuserSettlementConfig t = scDao.get("select t from TuserSettlementConfig t left join t.user u where u.id=" + userId);
		UserSettlementConfig usc = new UserSettlementConfig();
		BeanUtils.copyProperties(t, usc);
		if (t.getUser() != null) {
			User u = new User();
			BeanUtils.copyProperties(t.getUser(), u);
			usc.setStmUser(u);
		}
		return usc;
	}

	@Override
	public User findUserByPhone(String phone, String agentId) {
		List<Tuser> list = userDao.find("select t from Tuser t  left join t.organization org where t.loginName = '" + phone + "' and t.agentId = '" + StringUtil.getAgentId(agentId) + "'");
		if (list != null && list.size() > 0) {
			User u = new User();
			BeanUtils.copyProperties(list.get(0), u);
			return u;
		}
		return null;
	}

	@Override
	public void addUserImage(Long userId, String imagePath, Integer imageType, String imageName) {
		try {
			Tuser user = userDao.get(Tuser.class, userId);
			if (user != null) {
				TsysImage image = new TsysImage();
				image.setCreateTime(new Date());
				image.setImageName(imageName);
				image.setPath(imagePath);
				// image.setUrl(url);
				image.setType(imageType);
				image.setUser(user);
				imageDao.save(image);
			}
		} catch (Exception e) {
			logger.error("insert User Image error", e);
			throw e;
		}

	}

	@Override
	public boolean isOrgIdNo(long userId, String realIdNo) {
		Tuser user = userDao.get(Tuser.class, userId);
		if (user != null) {
			if (StringUtil.isNotBlank(user.getIdNo())) {
				if (user.getIdNo().equals(realIdNo)) {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isOrgRealName(Long userId, String realName) {
		Tuser user = userDao.get(Tuser.class, userId);
		if (user != null) {
			if (StringUtil.isNotBlank(user.getRealName())) {
				if (user.getRealName().equals(realName)) {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	public Boolean updateUserPsw(String appLoginName, String oldPwd, String newPwd, boolean isLoginPsw, String agentId) {
		boolean flag = false;
		Tuser user = userDao.get("select t from Tuser t where t.loginName='" + appLoginName + "'  and t.agentId= '" + StringUtil.getAgentId(agentId) + "'");
		if (isLoginPsw) {
			if (StringUtil.isBlank(oldPwd) || user.getPassword().equals(oldPwd)) {
				user.setPassword(newPwd);
				userDao.update(user);
				flag = true;
			}
		} else {
			if (StringUtil.isBlank(oldPwd) || user.getStmPsw().equals(oldPwd)) {
				user.setStmPsw(newPwd);
				userDao.update(user);
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public void updateUserHeadIcon(Long userId, String attachPath) {

		Tuser t = userDao.get(Tuser.class, userId);
		if (t != null) {
			t.setIconPath(attachPath);
			userDao.update(t);
		}
	}

	@Override
	public String isTransAccount(Long userId, String transPwd, Double amt, Boolean isBrokerage) {
		String flag = GlobalConstant.RESP_CODE_SUCCESS;
		Tuser t = userDao.get(Tuser.class, userId);
		if (t != null) {
			if (StringUtil.isNotEmpty(t.getStmPsw()) && t.getStmPsw().equals(transPwd)) {
				if (t.getState() != 0) {
					flag = GlobalConstant.RESP_CODE_023;
				} else {
					if (t.getSettlementStatus() == 1) {

						UserCard settlementCard = userCardService.getSettlementCarsByUserId(userId);
						if (settlementCard != null) {

							if (t.getAuthenticationStatus() != 1) {
								flag = GlobalConstant.RESP_CODE_021;
							} else {
								if (isBrokerage) {

									Tbrokerage tbk = brokerageDao.get("select t from Tbrokerage t left join t.user u where u.id=" + userId);
									if (tbk.getStatus() == 0) {
										if (tbk.getBrokerage().doubleValue() - amt < 0) {
											flag = GlobalConstant.RESP_CODE_014;
										}
									} else {
										flag = GlobalConstant.RESP_CODE_035;
									}
								} else {

									Taccount acc = accDao.get("select t from Taccount t left join t.user u where u.id=" + userId);
									if (acc.getStatus() == 0) {
										if (acc.getAvlAmt().doubleValue() - amt < 0) {
											flag = GlobalConstant.RESP_CODE_014;
										}
									} else {
										flag = GlobalConstant.RESP_CODE_035;
									}
								}
							}
						} else {
							flag = GlobalConstant.RESP_CODE_022;
						}
					} else {
						flag = GlobalConstant.RESP_CODE_024;
					}
				}
			} else {
				flag = GlobalConstant.RESP_CODE_003;
			}

		} else {
			flag = GlobalConstant.RESP_CODE_012;
		}
		return flag;
	}

	@Override
	public String isConsumePoint(Long userId, String transPwd, Long point) {
		String flag = GlobalConstant.RESP_CODE_SUCCESS;
		Tuser t = userDao.get(Tuser.class, userId);
		if (t != null) {
			if (StringUtil.isNotEmpty(t.getStmPsw()) && t.getStmPsw().equals(transPwd)) {
				if (t.getState() != 0) {
					flag = GlobalConstant.RESP_CODE_023;
				} else {
					TaccountPoint accPoint = accPointDao.get("select t from TaccountPoint t where t.user.id=" + userId);
					if (accPoint.getStatus() == 0) {
						if (accPoint.getPoint() - point < 0) {
							flag = GlobalConstant.RESP_CODE_074;
						}
					} else {
						flag = GlobalConstant.RESP_CODE_075;
					}
				}
			} else {
				flag = GlobalConstant.RESP_CODE_003;
			}

		} else {
			flag = GlobalConstant.RESP_CODE_012;
		}
		return flag;
	}

	@Override
	public String isAllowUserPay(Long userId, String payType) {
		String flag = GlobalConstant.RESP_CODE_SUCCESS;
		Tuser t = userDao.get(Tuser.class, userId);
		if (t != null) {
			/* 银联在线 提现 转账 必须实名认证 */
			if ((UserOrder.trans_type.YLZX.name().equals(payType) || UserOrder.trans_type.XJTX.name().equals(payType) || UserOrder.trans_type.YJTX.name().equals(payType) || UserOrder.trans_type.QBZZ.name().equals(payType))
					&& t.getAuthenticationStatus() != 1) {
				flag = GlobalConstant.RESP_CODE_021;
			} else {
				if (t.getState() != 0) {
					flag = GlobalConstant.RESP_CODE_023;
				}
			}
		} else {
			flag = GlobalConstant.RESP_CODE_012;
		}
		return flag;
	}

	@Override
	public User findUserByLoginName(String phone, String agentId) {
		Tuser t = userDao.get("select t from Tuser t  left join t.organization g where t.loginName='" + phone + "' and t.agentId ='" + agentId + "'");
		if (t != null) {
			User u = new User();
			BeanUtils.copyProperties(t, u);
			return u;
		}
		return null;
	}

	@Override
	public User getOpen(String openid, String unionid, String agentId) {
		Tuser t = userDao.get("select t from Tuser t where t.openId='" + openid + "' and t.unionid ='" + unionid + "' and t.agentId ='" + agentId + "'");
		if (t != null) {
			User u = new User();
			BeanUtils.copyProperties(t, u);
			return u;
		}
		return null;
	}

	@Override
	public Tuser findUserByLoginNameT(String phone, String agentId) {
		Tuser t = userDao.get("select t from Tuser t  left join t.organization g where t.loginName='" + phone + "' and t.agentId ='" + agentId + "'");
		return t;
	}

	@Override
	public Tuser getUserLoginName(String phone) {
		Tuser t = userDao.get("select t from Tuser t where t.loginName='" + phone + "'");
		return t;
	}

	@Override
	public String updateUserAuthWhenManualAuth(Long userId, Map<String, String> cardInfos) {
		String flag = GlobalConstant.RESP_CODE_SUCCESS;
		String realName = cardInfos.get("realName");
		String idNo = cardInfos.get("idNo");
		String merName = cardInfos.get("merName");// 商户名称
		String frontIDPath = cardInfos.get("frontIDPath");// 身份证正面
		String backIDPath = cardInfos.get("backIDPath");// 身份证反面
		String handIDPath = cardInfos.get("handIDPath");// 手持身份证
		String url = cardInfos.get("url");// 手持身份证

		String merchantCity = cardInfos.get("merchantCity");// 店铺所在城市
		String merchantName = cardInfos.get("merchantName");// 店铺名称
		try {
			Tuser t = userDao.get(Tuser.class, userId);
			t.setRealName(realName.trim());
			t.setIdNo(idNo.trim());
			if (StringUtil.isNotBlank(merchantCity)) {
				t.setMerchantCity(merchantCity);
			}
			if (StringUtil.isNotBlank(merchantName)) {
				t.setMerchantName(merchantName);
			}

			t.setAuthenticationStatus(User.authentication_status.PROCESSING.getCode());
			userDao.update(t);

			List<TsysImage> images = new ArrayList<TsysImage>();
			images.add(new TsysImage(t, SysImage.image_type.front_ID.getCode(), frontIDPath, url));
			images.add(new TsysImage(t, SysImage.image_type.back_ID.getCode(), backIDPath, url));
			images.add(new TsysImage(t, SysImage.image_type.hand_ID.getCode(), handIDPath, url));
			flag = imageService.saveTsysImage(images);
		} catch (Exception e) {
			logger.error("实名认证时保存图片失败", e);
			flag = GlobalConstant.RESP_CODE_999;
		}
		return flag;
	}

	@Override
	public String updateUserMerchantAuthWhenManualAuth(Long userId, Map<String, String> cardInfos) {
		String flag = GlobalConstant.RESP_CODE_SUCCESS;
		String frontIDPath = cardInfos.get("frontIDPath");// 身份证正面
		String backIDPath = cardInfos.get("backIDPath");// 身份证反面

		String licensePath = cardInfos.get("licensePath");// 营业执照
		String handInCashierDeskPath = cardInfos.get("handInCashierDeskPath");// 申请人手持身份证在收银台内照片
		String interiorView1Path = cardInfos.get("interiorView1Path");// 内景照1
		String interiorView2Path = cardInfos.get("interiorView2Path");// 内景照2
		String interiorView3Path = cardInfos.get("interiorView3Path");// 内景照3
		String shopPath = cardInfos.get("shopPath");// 申请人与门头合照

		String url = cardInfos.get("url");// 手持身份证

		String address = cardInfos.get("address");// 店铺所在城市
		String merchantName = cardInfos.get("merchantName");// 店铺名称
		try {
			Tuser t = userDao.get(Tuser.class, userId);
			t.setMerchantName(merchantName.trim());
			t.setMerchantShortName(merchantName.trim());
			t.setAddress(address);

			List<TsysImage> images = new ArrayList<TsysImage>();
			images.add(new TsysImage(t, SysImage.image_type.front_ID.getCode(), frontIDPath, url));
			images.add(new TsysImage(t, SysImage.image_type.back_ID.getCode(), backIDPath, url));
			int merchantType = User.merchant_type.WAITING_NONE.getCode();
			if (StringUtil.isNotBlank(licensePath)) {
				images.add(new TsysImage(t, SysImage.image_type.LICENSE_ID.getCode(), licensePath, url));
				merchantType = User.merchant_type.WAITING_REAL.getCode();
			}
			if (StringUtil.isNotBlank(handInCashierDeskPath)) {
				images.add(new TsysImage(t, SysImage.image_type.HAND_IN_CASHIER_DESK.getCode(), handInCashierDeskPath, url));
			}
			images.add(new TsysImage(t, SysImage.image_type.INTERIOR_VIEW_1.getCode(), interiorView1Path, url));
			if (interiorView2Path != null) {
			}
			// images.add(new TsysImage(t,
			// SysImage.image_type.INTERIOR_VIEW_2.getCode(), interiorView2Path,
			// url));
			// images.add(new TsysImage(t,
			// SysImage.image_type.INTERIOR_VIEW_3.getCode(), interiorView3Path,
			// url));
			images.add(new TsysImage(t, SysImage.image_type.SHOP_ID.getCode(), shopPath, url));
			flag = imageService.saveTsysImage(images);
			t.setMerchantType(merchantType);
			userDao.update(t);
		} catch (Exception e) {
			logger.error("实名认证时保存图片失败", e);
			flag = GlobalConstant.RESP_CODE_999;
		}
		return flag;
	}

	@Override
	public String isOverLimit(Long userId, String transType, Double amt) {
		String flag = GlobalConstant.RESP_CODE_028;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userId);
		Tuser user = userDao.get("select t from Tuser t left join t.settlementConfig s where t.id=:uid ", params);
		if (user != null && user.getSettlementConfig() != null) {
			TuserSettlementConfig tsc = user.getSettlementConfig();
			if (UserSettlementConfig.settlement_type.T0.name().equals(transType)) {
				if (tsc.getMinT0Amt().doubleValue() <= amt && amt <= tsc.getMaxT0Amt().doubleValue()) {
					flag = GlobalConstant.RESP_CODE_SUCCESS;

					Taccount acc = accDao.get("select t from Taccount t left join t.user u where u.id=" + userId);
					if (acc.getTodayOutAmt().add(BigDecimal.valueOf(amt)).subtract(tsc.getMaxTodayOutAmt()).doubleValue() > 0) {
						flag = "您今日提现额度为" + tsc.getMaxTodayOutAmt().setScale(2, BigDecimal.ROUND_DOWN) + "元,剩余额度为" + tsc.getMaxTodayOutAmt().subtract(acc.getTodayOutAmt()).setScale(2, BigDecimal.ROUND_DOWN) + "元";
					}
				}
			} else if (UserSettlementConfig.settlement_type.T1.name().equals(transType)) {
				if (tsc.getMinT1Amt().doubleValue() <= amt && amt <= tsc.getMaxT1Amt().doubleValue()) {
					flag = GlobalConstant.RESP_CODE_SUCCESS;
				}
			} else if (UserSettlementConfig.settlement_type.RABALE.name().equals(transType)) {
				if (tsc.getMinRabaleAmt().doubleValue() <= amt && amt <= tsc.getMaxRabaleAmt().doubleValue()) {
					flag = GlobalConstant.RESP_CODE_SUCCESS;
				}
			}
		}
		return flag;
	}

	@Override
	public Boolean isTransPsw(Long userId, String transPwd) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userId);
		params.put("transPwd", transPwd);
		Tuser user = userDao.get("select t from Tuser t where t.id=:uid and  t.stmPsw=:transPwd", params);
		return user == null ? false : true;
	}

	/**
	 * 根据订单的金额更新用户的类型
	 */
	@Override
	public void updateUserType(Long userId, BigDecimal orgAmt, Integer subPersonNum) throws Exception {
		Tuser user = userDao.get("select t from Tuser t left join t.parentUser pu left join t.settlementConfig sc left join t.organization r where t.id=" + userId);
		Torganization org = organizationService.getTorganizationInCacheByCode(user.getAgentId());
		int userType = 24;
		if (orgAmt != null && org != null && subPersonNum == null && org.getUserUpgradeType() != Organization.user_upgrade_type.POPULARIZE.getCode()) {
			// 购买代理升级
			if ((org.getDiamondAgent() - org.getGoldAgent()) == orgAmt.longValue()) { // 判断
																						// 用户升级钻石费率-用户升级金牌费率
																						// =
																						// 升级金额
				userType = 21;
				user.getSettlementConfig().setInputFee(org.getDefaultInputDiamondRate());
				// 为了动态的改动泊力的分润比例，特别设置了此功能
				if ("F20160002".equals(StringUtil.getAgentId(org.getCode()))) {
					user.getSettlementConfig().setShareFee(org.getDefaultInputFee().subtract(org.getDefaultInputDiamondRate()));
				}

			}
			if (org.getDiamondAgent() == orgAmt.longValue()) { // 判断 用户升级钻石费率 =
																// 升级金额
				userType = 21;
				user.getSettlementConfig().setInputFee(org.getDefaultInputDiamondRate());
				// 为了动态的改动泊力的分润比例，特别设置了此功能
				if ("F20160002".equals(StringUtil.getAgentId(org.getCode()))) {
					user.getSettlementConfig().setShareFee(org.getDefaultInputFee().subtract(org.getDefaultInputDiamondRate()));
				}
			}
			if (org.getGoldAgent() == orgAmt.longValue()) { // 判断 用户升级金牌费率 =
															// 升级费率
				userType = 22;
				user.getSettlementConfig().setInputFee(org.getDefaultInputGoldRate());
				// 为了动态的改动泊力的分润比例，特别设置了此功能
				if ("F20160002".equals(StringUtil.getAgentId(org.getCode()))) {
					user.getSettlementConfig().setShareFee(org.getDefaultInputFee().subtract(org.getDefaultInputGoldRate()));
				}
			}
		} else if (org != null && orgAmt == null && subPersonNum != null && org.getUserUpgradeType() != Organization.user_upgrade_type.SHOPPING.getCode()) {
			// 推广用户升级 TODO
			// 判断用户是否满足升级条件
			if (user.getUserType() != 21 && subPersonNum >= org.getDiamondNum()) {
				if (user.getAgentId().equals("F20160001") // 宝贝钱袋
						|| user.getAgentId().equals("F20160010") // 天一猫
						|| user.getAgentId().equals("F20160003") // 云付
						|| user.getAgentId().equals("F20160004") // 宝库
						|| user.getAgentId().equals("F20160011")) { // 云汇宝
					userType = 23;
				} else if (!user.getAgentId().equals("F20160017")) {
					// 厦商不能推广用户升级店长
					userType = 21;
				}
			} else if (user.getUserType() == 24 && subPersonNum >= org.getGoldNum()) {
				if (user.getAgentId().equals("F20160001") || user.getAgentId().equals("F20160010") // 天一猫
						|| user.getAgentId().equals("F20160003") // 云付
						|| user.getAgentId().equals("F20160004") // 宝库
						|| user.getAgentId().equals("F20160011")) { // 云汇宝
					userType = 23;
				} else {
					userType = 22;
				}
			}
		}
		if (userType >= user.getUserType()) {
			logger.info("用户的身份类型与升级后的等级一致，无法完成升级");
		} else {
			if (userType == 21) {
				user.setDiamondDateTime(new Date());
			} else if (userType == 22) {
				user.setGoldDateTime(new Date());
			}
			user.setUserType(userType);
			userDao.update(user);

			if (org.getReductionUserRateType() == Organization.reduction_user_rate_type.USER_UPGRADE.getCode()) {
				if (user.getAgentId().equals("F20160001") || user.getAgentId().equals("F20160010") // 天一猫
						|| user.getAgentId().equals("F20160003") // 云付
						|| user.getAgentId().equals("F20160004") // 宝库
						|| user.getAgentId().equals("F20160011")) { // 云汇宝
					if (userType == 22) {
						Long orgId = user.getOrganization().getId();
						Organization org1 = new Organization();
						String code = user.getAgentId() + "_" + user.getLoginName() + "_" + user.getRealName();
						Tuser puser = user.getParentUser();
						String porgType = puser.getOrganization().getOrgType();
						org1.setCode(code);
						org1.setName(code);
						org1.setPid(orgId);
						// org1.setAgentLevel(1);
						org1.setUserPhone(user.getLoginName());
						org1.setOrgType("5");
						org1.setAddress("上海市浦东新区纳贤路");
						// if (porgType.equals("4")) {
						// org1.setPid(puser.getOrganization().getId());
						// } else {
						// Tuser ppuser = puser.getParentUser();
						// if (ppuser != null) {
						// if
						// (ppuser.getOrganization().getOrgType().equals("4")) {
						// org1.setPid(ppuser.getOrganization().getId());
						// } else {
						// Tuser pppuser = ppuser.getParentUser();
						// if (pppuser != null) {
						// if
						// (pppuser.getOrganization().getOrgType().equals("4"))
						// {
						// org1.setPid(pppuser.getOrganization().getId());
						// } else {
						// org1.setPid(158l);
						// }
						// } else {
						// org1.setPid(158l);
						// }
						// }
						// } else {
						// org1.setPid(158l);
						// }
						// }
						org1.setStatus(0);
						org1.setAgentType(2);
						org1.setAgentLevel(2);
						organizationService.add(org1);
						Torganization t = organizationService.getTorganizationInCacheByCode(code);
						user.setOrganization(t);
						user.setIsAdmin(1);

						List<Trole> roles = new ArrayList<Trole>();
						roles.add(roleDao.get(Trole.class, Long.valueOf(23)));
						user.setRoles(new HashSet<Trole>(roles));
						userDao.update(user);
						// createSubAgentTwo(user.getId(),
						// t.getId(),user.getAgentId());

						// String sql = "select id ,LOGIN_NAME, 1 from sys_user
						// u where u.PID=:userId "
						// + " union "
						// + " select p.ID ,p.LOGIN_NAME,2 from SYS_USER P inner
						// join SYS_USER u on p.PID=u.ID where u.PID=:userId "
						// + " union select t.ID ,t.LOGIN_NAME,3 from SYS_USER t
						// inner join SYS_USER p on t.PID=p.ID inner join
						// SYS_USER u on p.PID=u.ID inner join SYS_USER r on
						// u.PID=r.ID where r.ID=:userId ";
						//

						String sql = " select id ,LOGIN_NAME, 1 from sys_user u where u.PID=:userId and u.LOGIN_NAME not in "
								+ " (select g.user_phone from sys_organization g where g.user_phone in( select LOGIN_NAME from sys_user u where u.PID=:userId)) " + " union "
								+ " select u2.ID ,u2.LOGIN_NAME,2 from sys_user u2 where u2.PID in " + " (select id  from sys_user u where u.PID=:userId and u.LOGIN_NAME not in "
								+ " (select g.user_phone from sys_organization g where g.user_phone in " + " (select LOGIN_NAME from sys_user u where u.PID=:userId)) ) and u2.LOGIN_NAME not in "
								+ " (select g.user_phone from sys_organization g where g.user_phone in" + " (select u2.LOGIN_NAME from sys_user u2 where u2.PID in (select id from sys_user u where u.PID=:userId))) " + " union "
								+ " select u3.ID ,u3.LOGIN_NAME,3 from sys_user u3 where u3.PID in " + " (select u2.ID  from sys_user u2 where u2.PID in " + " (select id  from sys_user u where u.PID=:userId and u.LOGIN_NAME not in "
								+ " (select g.user_phone from sys_organization g where g.user_phone in" + " (select LOGIN_NAME from sys_user u where u.PID=:userId))) and u2.LOGIN_NAME not in "
								+ " (select g.user_phone from sys_organization g where g.user_phone in" + " (select u2.LOGIN_NAME from sys_user u2 where u2.PID in " + " (select id from sys_user u where u.PID=:userId))))";

						Map<String, Object> params = new HashMap<String, Object>();
						params.put("userId", userId);
						List<Object[]> ids = userDao.findBySql(sql, params);
						for (int i = 0; i < ids.size(); i++) {
							Object[] obj = ids.get(i);
							Torganization tt = organizationService.getTorganizationInCodeTwo(String.valueOf(obj[1]));
							if (tt == null) {
								Map<String, Object> par = new HashMap<String, Object>();
								par.put("loginName", String.valueOf(obj[1]));
								Tuser tuser = userDao.get("select t from Tuser t where t.loginName=:loginName", par);
								if (orgId == tuser.getOrganization().getId()) {
									String updateUserAgent = "update  sys_user set  organization_id=" + t.getId() + " , agent_id='" + user.getAgentId() + "'  where id = " + obj[0];
									organizationDao.executeSql(updateUserAgent);
								}
							}
						}
						orgChannelUserRateConfigService.updateOrgChannelUserRate(22, user.getId(), user.getAgentId());
					} else if (userType == 23) {
						orgChannelUserRateConfigService.updateOrgChannelUserRate(23, user.getId(), user.getAgentId());
					}
				} else {
					// 更新用户使用通道的费率,读取升级降费率的配置信息，存放至用户费率表中
					TuserSettlementConfig tuscs = userSettlementConfigService.getTuserSettlementConfigByUserId(userId);
					userSettlementConfigService.setSettlementConfigWhenUserUpdate(tuscs, userType, org.getId());
					userSettlementConfigDao.update(tuscs);
				}
			}
		}
	}

	@Override
	public void dealClearUserAuthErrorNum() {
		String updateHql = "update Tuser set authErrorNum=0 ,  authCardErrorNum=0 ,  loginErrorNum=0" + " where authErrorNum>0 or authCardErrorNum>0 or loginErrorNum>0";
		Integer num = userDao.executeHql(updateHql);
		logger.info("总共执行了" + num + "条数据");
	}

	@Override
	public void updateUserVoiceType(Long userId, Integer voiceType) {
		userDao.executeHql("update Tuser set voiceType= " + voiceType + " where id=" + userId);
	}

	@Cacheable(value = "userCache", key = "#userId+'isSuperAdmin'")
	@Override
	public Boolean isSuperAdmin(Long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", userId);
		Tuser t = userDao.get("select t from Tuser t  left join fetch t.roles role left join t.organization org where t.id = :id and t.isAdmin=1 ", params);
		if (t != null && t.getRoles() != null && t.getRoles().size() > 0) {
			for (Trole role : t.getRoles()) {
				/* 超级管理员是1 */
				if (role.getId() == 1) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Workbook exportUserList(User user) {
		List<User> users = dataGrid(user, null);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String[] keys = new String[] { "orgName0", "loginName1", "realName2", "authStatus3", "userType4", "code5", "parent6", "merchantType7", "diamondTime8", "globTime9", "authTime10", "createTime11" };
		String[] columnNames = new String[] { "代理商名称", "用户登录名", "真实姓名", "认证状态", "用户类型", "用户编码", "推广人", "商家类型", "升钻时间", "升金时间", "认证时间", "创建时间" };
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("sheetName", "用户列表");
		list.add(m);

		Map<String, String> userTypes = dictionaryService.comboxMap("userType");
		Map<String, String> merchantTypes = dictionaryService.comboxMap("merchantType");
		for (User u : users) {
			Map<String, Object> contents = new HashMap<String, Object>();
			contents.put(keys[0], u.getOrganizationName());
			contents.put(keys[1], u.getLoginName());
			contents.put(keys[2], u.getRealName());
			contents.put(keys[3], u.getAuthenticationStatus() == 1 ? "是" : "否");
			contents.put(keys[4], userTypes.get(u.getUserType().toString()));
			contents.put(keys[5], u.getCode());
			contents.put(keys[6], u.getParentName());
			contents.put(keys[7], merchantTypes.get(u.getMerchantType().toString()));
			contents.put(keys[8], DateUtil.getStringFromDate(u.getDiamondDateTime(), "yyyy-MM-dd HH:mm:ss"));
			contents.put(keys[9], DateUtil.getStringFromDate(u.getGoldDateTime(), "yyyy-MM-dd HH:mm:ss"));
			contents.put(keys[10], DateUtil.getStringFromDate(u.getAuthDateTime(), "yyyy-MM-dd HH:mm:ss"));
			contents.put(keys[11], DateUtil.getStringFromDate(u.getCreateDatetime(), "yyyy-MM-dd HH:mm:ss"));
			list.add(contents);
		}
		Workbook wb = ExcelUtil.createWorkBook(list, keys, columnNames);
		return wb;
	}

	@Override
	public boolean getUserRole() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = request.getSession();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		List<Object[]> accs = userDao.findBySql("select s.ROLE_ID from sys_user_role s where s.USER_ID=" + sessionInfo.getId());
		if (String.valueOf(accs.get(0)).equals("18") || String.valueOf(accs.get(0)).equals("17") || String.valueOf(accs.get(0)).equals("4") || String.valueOf(accs.get(0)).equals("16")) {
			return true;
		}
		return false;
	}

	@Override
	public String getUserRole(Long userId) {
		List<Object[]> accs = roleDao.findBySql("select s.ROLE_ID from sys_user_role s where s.USER_ID=" + userId);
		return String.valueOf(accs.get(0));
	}

	@Override
	public boolean editSettlementStauts(Long userId, int status) {
		try {
			Tuser t = userDao.get("select t from Tuser t where t.id = " + userId);
			t.setSettlementStatus(status);
			userDao.save(t);
			return true;
		} catch (Exception e) {
			logger.error("edit  SettlementStauts error", e);
			return false;
		}
	}

	@Override
	public boolean updatePrivacyType(long userId, int privacyType) {
		try {
			Tuser t = userDao.get("select t from Tuser t where t.id = " + userId);
			t.setPrivacyType(privacyType);
			userDao.update(t);
			return true;
		} catch (Exception e) {
			logger.error("update  privacyType error", e);
			return false;
		}
	}

	@Override
	public boolean updateLoanType(long userId, int loanType) {
		try {
			Tuser t = userDao.get("select t from Tuser t where t.id = " + userId);
			t.setLoanType(loanType);
			userDao.update(t);
			return true;
		} catch (Exception e) {
			logger.error("update  loanType error", e);
			return false;
		}
	}

	@Override
	public boolean updateSpeechType(long userId, String speechType) {
		try {
			Tuser t = userDao.get("select t from Tuser t where t.id = " + userId);
			t.setSpeechType(speechType);
			userDao.update(t);
			return true;
		} catch (Exception e) {
			logger.error("update  loanType error", e);
			return false;
		}
	}

}
