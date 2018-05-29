package com.cn.flypay.service.sys.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserSettlementConfig;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.OrgChannelUserRateConfig;
import com.cn.flypay.pageModel.sys.OrgPointConfig;
import com.cn.flypay.pageModel.sys.Organization;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.sys.OrgChannelUserRateConfigService;
import com.cn.flypay.service.sys.OrgPointConfigService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.utils.StringUtil;

@Service
public class UserSettlementConfigServiceImpl implements UserSettlementConfigService {

	@Autowired
	private BaseDao<TuserSettlementConfig> userSettlementConfigDao;
	@Autowired
	private BaseDao<Torganization> organizationDao;
	@Autowired
	private BaseDao<Tuser> userDao;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private OrgPointConfigService orgPointConfigService;
	@Autowired
	private OrgChannelUserRateConfigService orgChannelUserRateConfigService;

	@Autowired
	private UserService userService;

	@Override
	public List<UserSettlementConfig> dataGrid(UserSettlementConfig param, PageFilter ph) {
		List<UserSettlementConfig> ul = new ArrayList<UserSettlementConfig>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TuserSettlementConfig t left join t.user u left join u.organization g ";
		List<TuserSettlementConfig> l = userSettlementConfigDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TuserSettlementConfig t : l) {
			UserSettlementConfig u = new UserSettlementConfig();
			BeanUtils.copyProperties(t, u);
			if (t.getUser() != null) {
				u.setRealName(t.getUser().getRealName());
				u.setLoginName(t.getUser().getLoginName());
				Torganization org = t.getUser().getOrganization();
				if (org != null) {
					u.setOrganizationAppName(org.getAppName());
					u.setOrganizationName(org.getName());
				}
			}

			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(UserSettlementConfig param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TuserSettlementConfig t left join t.user u left join u.organization g  ";
		return userSettlementConfigDao.count("select count(t.id) " + hql + whereHql(param, params), params);
	}

	private String whereHql(UserSettlementConfig param, Map<String, Object> params) {
		String hql = "";
		if (param != null) {
			hql += " where 1=1 ";
			if(param.getId()!=0l){
				hql +=" and t.id = :id";
				params.put("id", param.getId());
			}
			
			if (StringUtil.isNotBlank(param.getLoginName())) {
				hql += " and u.loginName = :loginName";
				params.put("loginName", param.getLoginName());
			}
			if (param.getOperateUser() != null) {
				hql += " and  g.id in(:operaterOrgIds)";
				params.put("operaterOrgIds", organizationService.getOwerOrgIds(param.getOperateUser().getOrganizationId()));
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
	public void edit(UserSettlementConfig param) {

		TuserSettlementConfig tc = userSettlementConfigDao.get("select t from TuserSettlementConfig t left join t.user u where t.id=" + param.getId());
		if (param.getMaxRabaleAmt() != null) {
			tc.setMaxRabaleAmt(param.getMaxRabaleAmt());
		}
		if (param.getMinRabaleAmt() != null) {
			tc.setMinRabaleAmt(param.getMinRabaleAmt());
		}
		if (param.getRabaleFee() != null) {
			tc.setRabaleFee(param.getRabaleFee());
		}
		if (param.getMaxT1Amt() != null) {
			tc.setMaxT1Amt(param.getMaxT1Amt());
		}
		if (param.getMinT1Amt() != null) {
			tc.setMinT1Amt(param.getMinT1Amt());
		}
		if (param.getT1Fee() != null) {
			tc.setT1Fee(param.getT1Fee());
		}
		if (param.getMaxT0Amt() != null) {
			tc.setMaxT0Amt(param.getMaxT0Amt());
		}
		if (param.getMinT0Amt() != null) {
			tc.setMinT0Amt(param.getMinT0Amt());
		}
		if (param.getT0Fee() != null) {
			tc.setT0Fee(param.getT0Fee());
		}
		if (param.getInputFee() != null && param.getInputFee().compareTo(BigDecimal.ONE) == -1) {
			tc.setInputFee(param.getInputFee());
		}
		if (param.getInputFeeAlipay() != null) {
			tc.setInputFeeAlipay(param.getInputFeeAlipay());
		}
		if (param.getInputFeeWeixin() != null) {
			tc.setInputFeeWeixin(param.getInputFeeWeixin());
		}
		if (param.getInputFeeYinlian() != null) {
			tc.setInputFeeYinlian(param.getInputFeeYinlian());
		}
		if (param.getInputFeeJingDong() != null) {
			tc.setInputFeeJingDong(param.getInputFeeJingDong());
		}
		if (param.getInputFeeBaidu() != null) {
			tc.setInputFeeBaidu(param.getInputFeeBaidu());
		}
		if (param.getInputFeeYizhifu() != null) {
			tc.setInputFeeYizhifu(param.getInputFeeYizhifu());
		}
		if (param.getInputFeeYinLianzhifu() != null) {
			tc.setInputFeeYinLianzhifu(param.getInputFeeYinLianzhifu());
		}

		if (param.getInputFeeD0Alipay() != null) {
			tc.setInputFeeD0Alipay(param.getInputFeeD0Alipay());
		}
		if (param.getInputFeeD0Weixin() != null) {
			tc.setInputFeeD0Weixin(param.getInputFeeD0Weixin());
		}
		if (param.getInputFeeD0Yinlian() != null) {
			tc.setInputFeeD0Yinlian(param.getInputFeeD0Yinlian());
		}
		if (param.getInputFeeD0JingDong() != null) {
			tc.setInputFeeD0JingDong(param.getInputFeeD0JingDong());
		}
		if (param.getInputFeeD0Baidu() != null) {
			tc.setInputFeeD0Baidu(param.getInputFeeD0Baidu());
		}
		if (param.getInputFeeD0Yizhifu() != null) {
			tc.setInputFeeD0Yizhifu(param.getInputFeeD0Yizhifu());
		}
		if (param.getInputFeeD0YinLianzhifu() != null) {
			tc.setInputFeeD0YinLianzhifu(param.getInputFeeD0YinLianzhifu());
		}

		if (param.getInputFeeBigAlipay() != null) {
			tc.setInputFeeBigAlipay(param.getInputFeeBigAlipay());
		}
		if (param.getInputFeeBigWeixin() != null) {
			tc.setInputFeeBigWeixin(param.getInputFeeBigWeixin());
		}
		if (param.getInputFeeBigYinlian() != null) {
			tc.setInputFeeBigYinlian(param.getInputFeeBigYinlian());
		}
		if (param.getInputFeeBigJingDong() != null) {
			tc.setInputFeeBigJingDong(param.getInputFeeBigJingDong());
		}
		if (param.getInputFeeBigBaidu() != null) {
			tc.setInputFeeBigBaidu(param.getInputFeeBigBaidu());
		}
		if (param.getInputFeeBigYizhifu() != null) {
			tc.setInputFeeBigYizhifu(param.getInputFeeBigYizhifu());
		}
		if (param.getInputFeeBigYinLianzhifu() != null) {
			tc.setInputFeeBigYinLianzhifu(param.getInputFeeBigYinLianzhifu());
		}

		if (param.getInputFeeD0BigAlipay() != null) {
			tc.setInputFeeD0BigAlipay(param.getInputFeeD0BigAlipay());
		}
		if (param.getInputFeeD0BigWeixin() != null) {
			tc.setInputFeeD0BigWeixin(param.getInputFeeD0BigWeixin());
		}
		if (param.getInputFeeD0BigYinlian() != null) {
			tc.setInputFeeD0BigYinlian(param.getInputFeeD0BigYinlian());
		}
		if (param.getInputFeeD0BigJingDong() != null) {
			tc.setInputFeeD0BigJingDong(param.getInputFeeD0BigJingDong());
		}
		if (param.getInputFeeD0BigBaidu() != null) {
			tc.setInputFeeD0BigBaidu(param.getInputFeeD0BigBaidu());
		}
		if (param.getInputFeeD0BigYizhifu() != null) {
			tc.setInputFeeD0BigYizhifu(param.getInputFeeD0BigYizhifu());
		}
		if (param.getInputFeeD0BigYinLianzhifu() != null) {
			tc.setInputFeeD0BigYinLianzhifu(param.getInputFeeD0BigYinLianzhifu());
		}
		if (param.getInputFeeD0BigQQzhifu() != null) {
			tc.setInputFeeD0BigQQzhifu(param.getInputFeeD0BigQQzhifu());
		}
		if (param.getInputFeeBigQQzhifu() != null) {
			tc.setInputFeeBigQQzhifu(param.getInputFeeBigQQzhifu());
		}
		if(param.getInputFeeZtAlipay() !=null){
			tc.setInputFeeZtAlipay(param.getInputFeeZtAlipay());
		}
		if(param.getInputFeeD0ZtAlipay() !=null){
			tc.setInputFeeD0ZtAlipay(param.getInputFeeD0ZtAlipay());
		}
		if(param.getInputFeeZtWeixin() !=null){
			tc.setInputFeeZtWeixin(param.getInputFeeZtWeixin());
		}
		if(param.getInputFeeD0ZtWeixin() !=null){
			tc.setInputFeeD0ZtWeixin(param.getInputFeeD0ZtWeixin());
		}
		if(param.getInputFeeZtQQzhifu() !=null){
			tc.setInputFeeZtQQzhifu(param.getInputFeeZtQQzhifu());
		}
		if(param.getInputFeeD0ZtQQzhifu() !=null){
			tc.setInputFeeD0ZtQQzhifu(param.getInputFeeD0ZtQQzhifu());
		}
		if(param.getInputFeeZtYinlian() !=null){
			tc.setInputFeeZtYinlian(param.getInputFeeZtYinlian());
		}
		if(param.getInputFeeD0ZtYinlian() !=null){
			tc.setInputFeeD0ZtYinlian(param.getInputFeeD0ZtYinlian());
		}
		if(param.getInputFeeZtYinlianJf() !=null){
			tc.setInputFeeZtYinlianJf(param.getInputFeeZtYinlianJf());
		}
		if(param.getInputFeeD0ZtYinlianJf() !=null){
			tc.setInputFeeD0ZtYinlianJf(param.getInputFeeD0ZtYinlianJf());
		}
		userSettlementConfigDao.update(tc);
	}

	@Override
	public UserSettlementConfig get(Long id) {
		TuserSettlementConfig tsc = userSettlementConfigDao.get(TuserSettlementConfig.class, id);
		if (tsc != null) {
			UserSettlementConfig usc = new UserSettlementConfig();
			BeanUtils.copyProperties(tsc, usc);
			return usc;
		}
		return null;
	}

	@Override
	public TuserSettlementConfig getTuserSettlementConfigByUserId(Long userId) {
		return userSettlementConfigDao.get("select t from TuserSettlementConfig t left join t.user u where u.id=" + userId);
	}

	@Override
	public UserSettlementConfig getByUserId(Long userId) {
		TuserSettlementConfig tsc = getTuserSettlementConfigByUserId(userId);
		if (tsc != null) {
			UserSettlementConfig usc = new UserSettlementConfig();
			BeanUtils.copyProperties(tsc, usc);
			usc.setAgentId(tsc.getUser().getAgentId());
			return usc;
		}
		return null;
	}

	@Override
	public BigDecimal getUserInputRate(Integer orderType, Long userId, Integer inputAccType) {
		//根据用户id获取用户费率表中配置的信息
		UserSettlementConfig usc = getByUserId(userId);
		if(orderType == 550||orderType == 552){
			// at  2017-11-10  by liangchao
			return usc.getInputFeeByPayType(orderType, inputAccType);
		}
		return usc.getInputFeeByPayType(UserOrder.getUserPayChannelType(orderType), inputAccType);
	}

	@Override
	public String isAllowReduceInputRateWhenPoint(Long userId, String agentId, Integer payType, Integer accountType, Integer exchangeType, Integer point) {
		String flag = GlobalConstant.RESP_CODE_SUCCESS;
		Torganization org = organizationService.getTorganizationInCacheByCode(agentId);
		if (org != null && org.getReductionUserRateType() == Organization.reduction_user_rate_type.POINT_SHOPPING.getCode()) {

			OrgPointConfig opc = orgPointConfigService.getByAgentIdAndPayType(agentId, payType, accountType);
			UserSettlementConfig usc = getByUserId(userId);
			boolean realPoint = false;
			if (exchangeType == 1 && usc.getInputFeeByPayType(payType, accountType).compareTo(opc.getTopRate()) == 0) {
				realPoint = opc.getToMidNum() == point;
			} else if (exchangeType == 2 && usc.getInputFeeByPayType(payType, accountType).compareTo(opc.getMidRate()) == 0) {
				realPoint = opc.getToLowNum() == point;
			} else if (exchangeType == 3 && usc.getInputFeeByPayType(payType, accountType).compareTo(opc.getTopRate()) == 0) {
				realPoint = opc.getToMidNum() + opc.getToLowNum() == point;
			} 
//			else {
//				flag = "用户信息与通道降费率信息不一致，请重新登录";
//				realPoint = true;
//			}
//			if (!realPoint) {
//				flag = "通道降费率的扣分数量与系统不一致";
//			}
		} else {
			flag = "该运营商不支持积分降低费率";
		}
		return flag;
	}

	@Override
	public void updateAllUserMaxT0Amt(BigDecimal maxT0Amt) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("maxT0Amt", maxT0Amt);
		String hql = "update TuserSettlementConfig set maxT0Amt=:maxT0Amt";
		userSettlementConfigDao.executeHql(hql, params);

		String orgHql = "update Torganization set maxT0Amt=:maxT0Amt";
		organizationDao.executeHql(orgHql, params);
	}

	/**
	 * 根据用户选择的支付方式以及入账类型确定用户分润的比例
	 * 
	 * @see 用户所属运营商若不支持升级降费率，那么分润比例为用户配置信息中的比例
	 * @see 用户所属运营商支持升级降费率，那么分润比例应为用户当前比例-钻石用户比例
	 */
	@Override
	public BigDecimal getUserShareRate(Long userId, Integer channelType, Integer accountType) {
		BigDecimal bd = BigDecimal.ZERO;
		Tuser user = userDao.get(Tuser.class, userId);
		if (user != null) {
			Torganization org = organizationService.getTorganizationInCacheByCode(user.getAgentId());
			if (org.getShareBonusType() == 1) {
				/* 固定比例分润 例如0.001 */
				bd = org.getDefaultShareFee();
			} else if (org.getShareBonusType() == 2) {
				if (user.getAgentId().equals("F20160017")) {
					//厦商特殊处理
					bd = orgChannelUserRateConfigService.getUserShareRateInCache(channelType, user.getUserType(), 3, accountType, org.getId());
					
				} else {
					/* 按照利率差设置分润比例 */
					bd = orgChannelUserRateConfigService.getUserShareRateInCache(channelType, user.getUserType(), 21, accountType, org.getId());
				}
			}
		}
		return bd;
	}

	@Override
	public BigDecimal[] getUserInputRateAndShareRate(Long userId, Integer orderType, Integer accountType) {
		BigDecimal[] bd = { BigDecimal.ZERO, BigDecimal.ZERO };
		bd[0] = getUserInputRate(orderType, userId, accountType);
		bd[1] = getUserShareRate(userId, orderType, accountType);
		return bd;
	}

	/**
	 * 读取升级降费率的配置信息，存放至用户费率表中
	 */
	@Override
	public void setSettlementConfigWhenUserUpdate(TuserSettlementConfig sc, Integer userType, Long orgId) {
		//通过机构名称、支付类型、用户类型  获取升级降费率配置信息
		OrgChannelUserRateConfig rateConfig = orgChannelUserRateConfigService.getOrgChannelUserRateConfigInCache(200, userType, orgId);
		sc.setInputFeeAlipay(rateConfig.getT1Rate());
		sc.setInputFeeD0Alipay(rateConfig.getD0Rate());
		sc.setInputFeeBigAlipay(rateConfig.getT1BigRate());
		sc.setInputFeeD0BigAlipay(rateConfig.getD0BigRate());
		sc.setInputFeeZtAlipay(rateConfig.getT1Rate());
		sc.setInputFeeD0ZtAlipay(rateConfig.getD0Rate());
		rateConfig = orgChannelUserRateConfigService.getOrgChannelUserRateConfigInCache(300, userType, orgId);
		sc.setInputFeeWeixin(rateConfig.getT1Rate());
		sc.setInputFeeD0Weixin(rateConfig.getD0Rate());
		sc.setInputFeeBigWeixin(rateConfig.getT1BigRate());
		sc.setInputFeeD0BigWeixin(rateConfig.getD0Rate());
		sc.setInputFeeZtWeixin(rateConfig.getT1BigRate());
		sc.setInputFeeD0ZtWeixin(rateConfig.getD0Rate());
		rateConfig = orgChannelUserRateConfigService.getOrgChannelUserRateConfigInCache(500, userType, orgId);
		sc.setInputFeeYinlian(rateConfig.getT1Rate());
		sc.setInputFeeD0Yinlian(rateConfig.getD0Rate());
		sc.setInputFeeBigYinlian(rateConfig.getT1BigRate());
		sc.setInputFeeD0BigYinlian(rateConfig.getD0Rate());
		sc.setInputFeeZtYinlian(rateConfig.getT1Rate());
		sc.setInputFeeD0ZtYinlian(rateConfig.getD0Rate());
		sc.setInputFeeZtYinlianJf(rateConfig.getT1Rate());
		sc.setInputFeeD0ZtYinlianJf(rateConfig.getD0Rate());
		rateConfig = orgChannelUserRateConfigService.getOrgChannelUserRateConfigInCache(552, userType, orgId);
		sc.setInputFeeZtYinlianJfZY(rateConfig.getT1Rate());
		sc.setInputFeeD0ZtYinlianJfZY(rateConfig.getD0Rate());
		
		//at 2017-11-14  by liangchao
		//暂时银联积分的费率分配只针对宝贝钱袋开放
		if(orgId==2l || orgId == 116l){ //添加厦商
			//银联积分费率单独分配，不再规划到银联在线500范畴   by liangchao  at 2017-11-10
			rateConfig = orgChannelUserRateConfigService.getOrgChannelUserRateConfigInCache(550, userType, orgId);
			sc.setInputFeeZtYinlianJf(rateConfig.getT1Rate());
			sc.setInputFeeD0ZtYinlianJf(rateConfig.getD0Rate());
		}
		
		
		rateConfig = orgChannelUserRateConfigService.getOrgChannelUserRateConfigInCache(900, userType, orgId);
		sc.setInputFeeJingDong(rateConfig.getT1Rate());
		sc.setInputFeeD0JingDong(rateConfig.getD0Rate());
		sc.setInputFeeBigJingDong(rateConfig.getT1BigRate());
		sc.setInputFeeD0BigJingDong(rateConfig.getD0BigRate());
		rateConfig = orgChannelUserRateConfigService.getOrgChannelUserRateConfigInCache(1000, userType, orgId);
		sc.setInputFeeBaidu(rateConfig.getT1Rate());
		sc.setInputFeeD0Baidu(rateConfig.getD0Rate());
		sc.setInputFeeBigBaidu(rateConfig.getT1BigRate());
		sc.setInputFeeD0BigBaidu(rateConfig.getD0BigRate());
		rateConfig = orgChannelUserRateConfigService.getOrgChannelUserRateConfigInCache(1100, userType, orgId);
		sc.setInputFeeYizhifu(rateConfig.getT1Rate());
		sc.setInputFeeD0Yizhifu(rateConfig.getD0Rate());
		sc.setInputFeeBigYizhifu(rateConfig.getT1BigRate());
		sc.setInputFeeD0BigYizhifu(rateConfig.getD0BigRate());
		rateConfig = orgChannelUserRateConfigService.getOrgChannelUserRateConfigInCache(1200, userType, orgId);
		sc.setInputFeeYinLianzhifu(rateConfig.getT1Rate());
		sc.setInputFeeD0YinLianzhifu(rateConfig.getD0Rate());
		sc.setInputFeeBigYinLianzhifu(rateConfig.getT1BigRate());
		sc.setInputFeeD0BigYinLianzhifu(rateConfig.getD0BigRate());
		rateConfig = orgChannelUserRateConfigService.getOrgChannelUserRateConfigInCache(1300, userType, orgId);
		sc.setInputFeeBigQQzhifu(rateConfig.getT1BigRate());
		sc.setInputFeeD0BigQQzhifu(rateConfig.getD0BigRate());
        sc.setInputFeeZtQQzhifu(rateConfig.getT1Rate());
		sc.setInputFeeD0ZtQQzhifu(rateConfig.getD0BigRate());
	}
}
