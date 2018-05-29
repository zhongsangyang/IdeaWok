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

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TbrokerageConfig;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.TorgChannel;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.TorgBrokerage;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.base.Tree;
import com.cn.flypay.pageModel.sys.Organization;
import com.cn.flypay.service.base.ServiceException;
import com.cn.flypay.service.sys.AgentSettlementRateConfigService;
import com.cn.flypay.service.sys.AgentUpgradeFeeCfgService;
import com.cn.flypay.service.sys.BrokerageConfigService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.OrgChannelService;
import com.cn.flypay.service.sys.OrgChannelUserRateConfigService;
import com.cn.flypay.service.sys.OrgPointConfigService;
import com.cn.flypay.service.sys.OrgSysConfigService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.PayTypeLimitConfigService;
import com.cn.flypay.service.sys.PlatformOrgConfigService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.StringUtil;

@Service
public class OrganizationServiceImpl implements OrganizationService {

	@Autowired
	private BaseDao<Tuser> userDao;

	@Autowired
	private BaseDao<TbrokerageConfig> brokerageConfigDao;

	@Autowired
	private BaseDao<Torganization> organizationDao;

	@Autowired
	private BaseDao<TorgBrokerage> orgBrokerageDao;

	@Autowired
	private OrgChannelService orgChannelService;

	@Autowired
	private ChannelService channelService;
	@Autowired
	private BrokerageConfigService brokerageConfigService;
	@Autowired
	private OrgPointConfigService orgPointConfigService;
	@Autowired
	private PlatformOrgConfigService platformOrgConfigService;
	@Autowired
	private OrgSysConfigService orgSysConfigService;
	@Autowired
	private UserService userService;
	@Autowired
	private OrgChannelUserRateConfigService orgChannelUserRateConfigService;
	@Autowired
	private AgentSettlementRateConfigService agentSettlementRateConfigService;
	@Autowired
	private AgentUpgradeFeeCfgService agentUpgradeFeeCfgService;
	@Autowired
	private PayTypeLimitConfigService payTypeLimitConfigService;

	@Cacheable(value = "organizationCache", key = "'treeGrid'")
	@Override
	public List<Organization> treeGrid() {
		List<Organization> lr = new ArrayList<Organization>();
		List<Torganization> l = organizationDao.find("from Torganization t left join fetch t.organization  order by t.seq");
		if ((l != null) && (l.size() > 0)) {
			for (Torganization t : l) {
				Organization r = new Organization();
				BeanUtils.copyProperties(t, r);
				if (t.getOrganization() != null) {
					r.setPid(t.getOrganization().getId());
					r.setPname(t.getOrganization().getName());
				}
				r.setIconCls(t.getIcon());
				lr.add(r);
			}
		}
		return lr;
	}

	// @Cacheable(value = "organizationCache", key = "#orgId+'treeGrid'")
	@Override
	public List<Organization> treeGrid(Long orgId) {
		Set<Long> orgIds = getOwerOrgIds(orgId);
		List<Organization> lr = new ArrayList<Organization>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orgIds", orgIds);
		List<Torganization> l = organizationDao.find("select t from Torganization t left join fetch t.organization where t.id in(:orgIds) order by t.seq", params);
		if ((l != null) && (l.size() > 0)) {
			for (Torganization t : l) {
				Organization r = new Organization();
				BeanUtils.copyProperties(t, r);
				if (t.getOrganization() != null) {
					r.setPid(t.getOrganization().getId());
					r.setPname(t.getOrganization().getName());
				}
				r.setIconCls(t.getIcon());
				lr.add(r);
			}
		}
		return lr;
	}

	@CacheEvict(value = { "organizationCache", "brokerageConfigCache" }, allEntries = true)
	@Override
	public void add(Organization org) throws Exception {
		Torganization t = new Torganization();
		BeanUtils.copyProperties(org, t);
		if (org.getPid() != null) {
			Torganization porg = organizationDao.get(Torganization.class, org.getPid());
			if (org.getOperateUser() != null && userService.isSuperAdmin(org.getOperateUser().getId())) {
				// 超级管理员可以自定义机构编号
			} else {
				if (StringUtil.isNotBlank(org.getCode()) && !org.getCode().startsWith(porg.getCode()) && (org.getCode().length() > porg.getCode().length())) {
					//throw new Exception("下级机构编号必须以上级机构编号开头");
				}
			}
			String icon = "icon-folder";
			if (org.getAgentType() == Organization.agent_type.OEM.getCode()) {
				icon = "icon-home";
			} else if (org.getAgentType() == Organization.agent_type.SERVICE_PROVIDER.getCode()) {
				icon = "icon-folder";
			} else if (org.getAgentType() == Organization.agent_type.AGENT.getCode()) {
				icon = "icon-man";
			}
			t.setIcon(icon);
			t.setOrganization(porg);
			if (StringUtil.isEmpty(org.getCountry())) {
				t.setCountry(org.getCountry());
			} else {
				t.setCountry(porg.getCountry());
			}
			if (org.getDiamondAgent() != null) {
				t.setDiamondAgent(org.getDiamondAgent());
			} else {
				t.setDiamondAgent(porg.getDiamondAgent());
			}
			if (org.getGoldAgent() != null) {
				t.setGoldAgent(org.getGoldAgent());
			} else {
				t.setGoldAgent(porg.getGoldAgent());
			}
			if (org.getStatus() != null) {
				t.setStatus(org.getStatus());
			} else {
				t.setStatus(porg.getStatus());
			}
			if (org.getMaxRabaleAmt() != null) {
				t.setMaxRabaleAmt(org.getMaxRabaleAmt());
			} else {
				t.setMaxRabaleAmt(porg.getMaxRabaleAmt());
			}
			if (org.getMinRabaleAmt() != null) {
				t.setMinRabaleAmt(org.getMinRabaleAmt());
			} else {
				t.setMinRabaleAmt(porg.getMinRabaleAmt());
			}
			if (org.getRabaleFee() != null) {
				t.setRabaleFee(org.getRabaleFee());
			} else {
				t.setRabaleFee(porg.getRabaleFee());
			}
			if (org.getMaxTodayOutAmt() != null) {
				t.setMaxTodayOutAmt(org.getMaxTodayOutAmt());
			} else {
				t.setMaxTodayOutAmt(porg.getMaxTodayOutAmt());
			}

			if (org.getMaxT0Amt() != null) {
				t.setMaxT0Amt(org.getMaxT0Amt());
			} else {
				t.setMaxT0Amt(porg.getMaxT0Amt());
			}
			if (org.getPointType() != null) {
				t.setPointType(org.getPointType());
			} else {
				t.setPointType(porg.getPointType());
			}
			if (org.getMinT0Amt() != null) {
				t.setMinT0Amt(org.getMinT0Amt());
			} else {
				t.setMinT0Amt(porg.getMinT0Amt());
			}
			if (org.getT0Fee() != null) {
				t.setT0Fee(org.getT0Fee());
			} else {
				t.setT0Fee(porg.getT0Fee());
			}
			if (org.getDiamondFee() != null) {
				t.setDiamondFee(org.getDiamondFee());
			} else {
				t.setDiamondFee(porg.getDiamondFee());
			}
			if (org.getGoldFee() != null) {
				t.setGoldFee(org.getGoldFee());
			} else {
				t.setGoldFee(porg.getGoldFee());
			}
			if (org.getMaxT1Amt() != null) {
				t.setMaxT1Amt(org.getMaxT1Amt());
			} else {
				t.setMaxT1Amt(porg.getMaxT1Amt());
			}
			if (org.getMinT1Amt() != null) {
				t.setMinT1Amt(org.getMinT1Amt());
			} else {
				t.setMinT1Amt(porg.getMinT1Amt());
			}
			if (org.getT1Fee() != null) {
				t.setT1Fee(org.getT1Fee());
			} else {
				t.setT1Fee(porg.getT1Fee());
			}
			if (org.getPrincipalAgentRate() != null) {
				t.setPrincipalAgentRate(org.getPrincipalAgentRate());
			} else {
				t.setPrincipalAgentRate(porg.getPrincipalAgentRate());
			}
			if (org.getPrincipalRate() != null) {
				t.setPrincipalRate(org.getPrincipalRate());
			} else {
				t.setPrincipalRate(porg.getPrincipalRate());
			}
			if (StringUtil.isEmpty(org.getProvince())) {
				t.setProvince(org.getProvince());
			} else {
				t.setProvince(porg.getProvince());
			}

			if (org.getRate() != null) {
				t.setRate(org.getRate());
			} else {
				t.setRate(porg.getRate());
			}
			if (org.getDefaultInputFee() != null) {
				t.setDefaultInputFee(org.getDefaultInputFee());
			} else {
				t.setDefaultInputFee(porg.getDefaultInputFee());
			}
			if (StringUtil.isNotEmpty(org.getAppName())) {
				t.setAppName(org.getAppName());
			} else {
				t.setAppName(porg.getAppName());
			}
			if (org.getDefaultShareFee() != null) {
				t.setDefaultShareFee(org.getDefaultShareFee());
			} else {
				t.setDefaultShareFee(porg.getDefaultShareFee());
			}
			if (org.getDefaultInputDiamondRate() != null) {
				t.setDefaultInputDiamondRate(org.getDefaultInputDiamondRate());
			} else {
				t.setDefaultInputDiamondRate(porg.getDefaultInputDiamondRate());
			}
			if (org.getDefaultInputGoldRate() != null) {
				t.setDefaultInputGoldRate(org.getDefaultInputGoldRate());
			} else {
				t.setDefaultInputGoldRate(porg.getDefaultInputGoldRate());
			}
			if (org.getPointType() != null) {
				t.setPointType(org.getPointType());
			} else {
				t.setPointType(porg.getPointType());
			}
			if (org.getReductionUserRateType() != null) {
				t.setReductionUserRateType(org.getReductionUserRateType());
			} else {
				t.setReductionUserRateType(porg.getReductionUserRateType());
			}
			if (org.getShareBonusType() != null) {
				t.setShareBonusType(org.getShareBonusType());
			} else {
				t.setShareBonusType(porg.getShareBonusType());
			}
			if (org.getUserUpgradeType() != null) {
				t.setUserUpgradeType(org.getUserUpgradeType());
			} else {
				t.setUserUpgradeType(porg.getUserUpgradeType());
			}

			if (org.getShareBonusLevelType() != null) {
				t.setShareBonusLevelType(org.getShareBonusLevelType());
			} else {
				t.setShareBonusLevelType(porg.getShareBonusLevelType());
			}

			if (org.getDiamondNum() != null) {
				t.setDiamondNum(org.getDiamondNum());
			} else {
				t.setDiamondNum(porg.getDiamondNum());
			}
			if (org.getGoldNum() != null) {
				t.setGoldNum(org.getGoldNum());
			} else {
				t.setGoldNum(porg.getGoldNum());
			}
		}
		t.setCreateDatetime(new Date());
		t.setRate(BigDecimal.ONE);
		t.setPrincipalRate(BigDecimal.ZERO);
		t.setPrincipalAgentRate(BigDecimal.ZERO);
		t.setSeq(0);

		organizationDao.save(t);

		/**
		 * 对于运营商，提现运营商分配比例，和运营商通道表 运营商积分表
		 */
		if (t.getAgentType() == 0 || t.getAgentType() == 1) {
			brokerageConfigService.initBrokerageConfigByCopyParent(t);
			agentSettlementRateConfigService.initAgentSettlementRateConfigByCopyParent(t);

			agentUpgradeFeeCfgService.initAgentUpgradeFeeCfgByCopyParent(t);

			orgChannelUserRateConfigService.initOrgChannelUserRateConfigByCopyParent(t);
			/*
			 * 运营商通道表
			 */
			List<Tchannel> cls = channelService.searchTchannels();
			List<TorgChannel> tcs = new ArrayList<TorgChannel>();
			for (Tchannel channel : cls) {
				TorgChannel tc = new TorgChannel(channel.getRealRate(), 0, new Date(), new Date(), "system");
				tc.setChannel(channel);
				tc.setOrganization(t);
				tcs.add(tc);
			}
			orgChannelService.addTorgChannelList(tcs);
			/*
			 * 复制一份运营商积分表
			 */
			orgPointConfigService.initOrgPointConfigs(t);
			/* 平台运营商费率设置 */
			platformOrgConfigService.initPlatformOrgConfig(t);
			/* 初始化短信与极光账户 */
			orgSysConfigService.initOrgSysConfig(t);
			/* 初始化 支付方式费率 */
			payTypeLimitConfigService.initPayTypeLimitConfig(t);

		} else {

		}
		/*
		 * 机构佣金
		 */
		TorgBrokerage orgBrokerage = new TorgBrokerage();
		orgBrokerage.setBrokerage(BigDecimal.ZERO);
		orgBrokerage.setOrganization(t);
		orgBrokerage.setStatus(1);
		orgBrokerage.setTotalAgentBrokerage(BigDecimal.ZERO);
		orgBrokerage.setTotalBrokerage(BigDecimal.ZERO);
		orgBrokerage.setTotalTransBrokerage(BigDecimal.ZERO);
		orgBrokerageDao.save(orgBrokerage);
	}

	@Override
	public void delete(Long id) {
		Torganization t = organizationDao.get(Torganization.class, id);
		del(t);
	}

	private void del(Torganization t) {
		List<Tuser> list = userDao.find("from Tuser t left join t.organization org where org.id=" + t.getId());
		if (list != null && list.size() > 0) {
			throw new ServiceException("该运营商已经被用户使用,无法删除");
		} else {
			// TODO update 处理
			/*
			 * if ((t.getOrganizations() != null) &&
			 * (t.getOrganizations().size() > 0)) { for (Torganization r :
			 * t.getOrganizations()) { del(r); } }
			 */

			organizationDao.delete(t);
		}
	}

	@CacheEvict(value = "organizationCache", allEntries = true)
	@Override
	public void edit(Organization editOrg) {
		Torganization original = organizationDao.get(Torganization.class, editOrg.getId());
		String orgType  = original.getOrgType();
		String userphone  = original.getUserPhone();
		// 冻结/解冻 用户
		if (!editOrg.getStatus().equals(original.getStatus())) {
			List<Torganization> orgList = new ArrayList<Torganization>();
			getAllSubOrganizationByOrgId(orgList, editOrg.getId());
			orgList.add(original);
			for (Torganization org : orgList) {
				try {
					TorgBrokerage tob = orgBrokerageDao.get("select t from TorgBrokerage t left join t.organization tor where tor.id=" + org.getId());
					tob.setStatus(editOrg.getStatus());
					orgBrokerageDao.update(tob);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		editOrg.setPrincipalAgentRate(original.getPrincipalAgentRate());
		editOrg.setRate(original.getRate());
		editOrg.setPrincipalRate(original.getPrincipalRate());
		BeanUtils.copyProperties(editOrg, original);
		if ((editOrg.getPid() != null) && !"".equals(editOrg.getPid())) {
			original.setOrganization(organizationDao.get(Torganization.class, editOrg.getPid()));
		}
		original.setOrgType(orgType);
		original.setUserPhone(userphone);
		organizationDao.update(original);
	}
	
	
	@Override
	public void updateUserphone(Torganization t) {
		organizationDao.update(t);
	}
	

	@Override
	public void editAgentOrganization(Organization editOrg) {

		Torganization original = organizationDao.get(Torganization.class, editOrg.getId());
		// 冻结/解冻 用户
		if (!editOrg.getStatus().equals(original.getStatus())) {
			List<Torganization> orgList = new ArrayList<Torganization>();
			getAllSubOrganizationByOrgId(orgList, editOrg.getId());
			orgList.add(original);
			for (Torganization org : orgList) {
				try {
					TorgBrokerage tob = orgBrokerageDao.get("select t from TorgBrokerage t left join t.organization tor where tor.id=" + org.getId());
					tob.setStatus(editOrg.getStatus());
					orgBrokerageDao.update(tob);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		original.setAgentLevel(editOrg.getAgentLevel());
		original.setAddress(editOrg.getAddress());
		original.setStatus(editOrg.getStatus());
		if ((editOrg.getPid() != null) && !"".equals(editOrg.getPid())) {
			original.setOrganization(organizationDao.get(Torganization.class, editOrg.getPid()));
		}
		original.setName(editOrg.getName());
		original.setOrgType(editOrg.getOrgType());
		organizationDao.update(original);

	}

	private List<Torganization> getAllSubOrganizationByOrgId(List<Torganization> orgList, Long orgId) {
		List<Torganization> orgList1 = organizationDao.find("select t from Torganization t left join  t.organization porg where porg.id = " + orgId);
		for (Torganization tz : orgList1) {
			orgList.add(tz);
			getAllSubOrganizationByOrgId(orgList, tz.getId());
		}
		return orgList;
	}

	@Override
	public Organization get(Long id) {
		Torganization t = organizationDao.get(Torganization.class, id);
		Organization r = new Organization();
		BeanUtils.copyProperties(t, r);
		if (t.getOrganization() != null) {
			r.setPid(t.getOrganization().getId());
			r.setPname(t.getOrganization().getName());
		}
		return r;
	}
	
	
	@Override
	public Organization getByCode(String code){
		Torganization t = organizationDao.get("select t from Torganization t where t.code = "+code);
		Organization r = new Organization();
		BeanUtils.copyProperties(t, r);
		return r;
	};

	@Override
	public List<Tree> tree(HttpSession session) {
		Map<String, Object> params = new HashMap<String, Object>();
		List<Tree> list = findOrgTrees("select distinct t from Torganization t order by t.seq", params);
		return list;
	}

	@Override
	public List<Tree> tree(Long orgId) {
		Set<Long> orgIds = getOwerOrgIds(orgId);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orgIds", orgIds);
		return findOrgTrees("select t from Torganization t left join fetch t.organization where t.id in(:orgIds) order by t.seq", params);
	}

	private List<Tree> findOrgTrees(String hql, Map<String, Object> params) {
		List<Torganization> l = organizationDao.find(hql, params);
		List<Tree> lt = new ArrayList<Tree>();

		if ((l != null) && (l.size() > 0)) {
			for (Torganization r : l) {
				Tree tree = new Tree();
				tree.setId(r.getId().toString());
				if (r.getOrganization() != null) {
					tree.setPid(r.getOrganization().getId().toString());
				}
				tree.setCode(r.getCode());
				tree.setText(r.getName());
				tree.setIconCls(r.getIcon());
				lt.add(tree);
			}
		}
		return lt;
	}

	public List<Tree> treeServiceProviders(Long orgId, Integer angentType) {
		Set<Long> orgIds = getOwerOrgIds(orgId);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orgIds", orgIds);
		
		Set<Integer> agentType = new HashSet<Integer>();
		if (angentType == Organization.agent_type.SERVICE_PROVIDER.getCode()) {
			agentType.add(1);
		} else if (angentType == Organization.agent_type.OEM.getCode()) {
			agentType.add(0);
			agentType.add(1);
		} else {
			agentType.add(0);
			agentType.add(1);
			agentType.add(2);
		}
		params.put("agentType", agentType);
		return findOrgTrees("select t from Torganization t left join fetch t.organization where t.agentType in(:agentType)  and t.id in(:orgIds) order by t.seq", params);
	}

	@Cacheable(value = "organizationCache", key = "#orgId+'getServiceProviderInCacheByOrgId'")
	public Organization getServiceProviderInCacheByOrgId(Long orgId) {

		return null;
	}

	@Cacheable(value = "organizationCache", key = "'getOrgTransPrincipalRate'")
	public Map<Long, BigDecimal> getOrgTransPrincipalRate() {
		System.out.println("-----getOrgRate-----");
		Map<Long, BigDecimal> orgMap = new HashMap<Long, BigDecimal>();
		List<Torganization> orgList = organizationDao.find("select distinct t from Torganization t order by t.seq");
		for (Torganization torg : orgList) {
			orgMap.put(torg.getId(), BigDecimal.ONE);
			calTransPrincipalRate(orgMap, torg, torg.getId());
		}
		return orgMap;
	}

	/**
	 * 递归计算各个机构的佣金
	 * 
	 * @param orgMap
	 *            存储机构佣金比例
	 * @param torg
	 *            递归者
	 * @param orgId
	 *            待计算佣金的机构
	 */
	private void calTransPrincipalRate(Map<Long, BigDecimal> orgMap, Torganization torg, Long orgId) {
		Torganization porg = torg.getOrganization();
		/*
		 * 若父机构不存在，表示该机构已经递归到根目录。 root 成本比例0.2 分拥比例0.8 root-1
		 * 成本0.2（1-0.2），佣金比例（1-0.2）*（1-0.2）
		 */
		if (porg == null) {
			orgMap.put(orgId, orgMap.get(orgId).multiply(torg.getPrincipalRate()));// 递归的比例
			// *
			// 自身的成本比例
		} else {
			orgMap.put(orgId, orgMap.get(orgId).multiply(BigDecimal.ONE.subtract(torg.getPrincipalRate())));// 递归计算整个机构链的比例
			calTransPrincipalRate(orgMap, porg, orgId);
		}
	}

	@Cacheable(value = "organizationCache", key = "'getOrgTransPrincipalRate'")
	public Map<Long, BigDecimal> getOrgAgentPrincipalRate() {
		Map<Long, BigDecimal> orgMap = new HashMap<Long, BigDecimal>();
		List<Torganization> orgList = organizationDao.find("select distinct t from Torganization t order by t.seq");
		for (Torganization torg : orgList) {
			orgMap.put(torg.getId(), BigDecimal.ONE);
			calAgentPrincipalRate(orgMap, torg, torg.getId());
		}
		return orgMap;
	}

	/**
	 * 递归计算各个机构的佣金
	 * 
	 * @param orgMap
	 *            存储机构佣金比例
	 * @param torg
	 *            递归者
	 * @param orgId
	 *            待计算佣金的机构
	 */
	private void calAgentPrincipalRate(Map<Long, BigDecimal> orgMap, Torganization torg, Long orgId) {
		Torganization porg = torg.getOrganization();
		/*
		 * 若父机构不存在，表示该机构已经递归到根目录。 root 成本比例0.2 分拥比例0.8 root-1
		 * 成本0.2（1-0.2），佣金比例（1-0.2）*（1-0.2）
		 */
		if (porg == null) {
			orgMap.put(orgId, orgMap.get(orgId).multiply(torg.getPrincipalAgentRate()));// 递归的比例
			// *
			// 自身的成本比例
		} else {
			orgMap.put(orgId, orgMap.get(orgId).multiply(BigDecimal.ONE.subtract(porg.getPrincipalAgentRate())));// 递归计算整个机构链的比例
			calTransPrincipalRate(orgMap, porg, orgId);
		}
	}

	@Cacheable(value = "organizationCache", key = "#orgId+'getOwerOrgIds'")
	@Override
	public Set<Long> getOwerOrgIds(Long orgId) {
		String sql = "select id,1 from sys_organization where FIND_IN_SET(id, getChildOrg(" + orgId + "))";
		List<Object[]> objs = organizationDao.findBySql(sql);
		Set<Long> ls = new HashSet<Long>();
		for (Object[] obj : objs) {
			ls.add(((BigInteger) obj[0]).longValue());
		}
		return ls;
	}

	
	
	@Override
	public Torganization getTorganizationInCode(String code) {
		String hql = "select t from Torganization t where t.orgType=5 and t.userPhone='" + code + "'";
		List<Torganization> objs = organizationDao.find(hql);
		for (Torganization torg : objs) {
			return torg;
		}
		return null;
	}
	
	
	@Override
	public Torganization getTorganizationInCodeTwo(String code) {
		String hql = "select t from Torganization t where t.userPhone='" + code + "'";
		List<Torganization> objs = organizationDao.find(hql);
		for (Torganization torg : objs) {
			return torg;
		}
		return null;
	}
	
	@Cacheable(value = "organizationCache", key = "#code+'getOwerOrgIds'")
	@Override
	public Torganization getTorganizationInCacheByCode(String code) {
		String hql = "select t from Torganization t where t.code='" + code + "'";
		List<Torganization> objs = organizationDao.find(hql);
		for (Torganization torg : objs) {
			return torg;
		}
		return null;
	}
	
	
	
	@Override
	public Torganization getTorganizationInCacheByMobile(String mobile) {
		String hql = "select t from Torganization t where t.userPhone='" + mobile + "'";
		List<Torganization> objs = organizationDao.find(hql);
		for (Torganization torg : objs) {
			return torg;
		}
		return null;
	}

	@Cacheable(value = "organizationCache", key = "#id+'getTorganizationById'")
	@Override
	public Torganization getTorganizationInCacheById(Long id) {
		return organizationDao.get(Torganization.class, id);
	}

	@Cacheable(value = "organizationCache", key = "#agentId+'isPointType'")
	@Override
	public Boolean isPointType(String agentId) {
		Torganization tot = getTorganizationInCacheByCode(agentId);
		if (tot.getPointType() == 1) {
			return true;
		}
		return false;
	}

	@Override
	public List<Organization> getOrganiztions() {
		List<Organization> lr = new ArrayList<Organization>();
		List<Torganization> l = getTorganiztions();
		if ((l != null) && (l.size() > 0)) {
			for (Torganization t : l) {
				Organization r = new Organization();
				BeanUtils.copyProperties(t, r);
				r.setIconCls(t.getIcon());
				lr.add(r);
			}
		}
		return lr;
	}

	@Override
	public List<Torganization> getTorganiztions() {
		return organizationDao.find("from Torganization t where t.agentType=0  order by t.seq");
	}

	@Override
	public Torganization getTorganizationByAgentIdAndPhone(String agentId, String loginName) {
		//根据agentId查询所属代理商平台
		String hql1 = "select t from Torganization t where t.code = '"+agentId+"'";
		Torganization torganization = organizationDao.get(hql1);
		if (torganization != null ) {
			HashMap<String, Object> params = new HashMap<String,Object>();
			//根据手机号以及app名称查询对应平台下该用户是否是代理商
			String hql = "select t from Torganization t where t.userPhone = :userPhone and t.appName = :appName";
			params.put("userPhone", loginName);
			params.put("appName", torganization.getAppName());
			List<Torganization> objs = organizationDao.find(hql, params);
			for (Torganization torg : objs) {
				return torg;
			}
		}
		return null;
	}
	
}
