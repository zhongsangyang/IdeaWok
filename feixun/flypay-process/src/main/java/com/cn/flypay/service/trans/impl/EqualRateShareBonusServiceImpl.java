package com.cn.flypay.service.trans.impl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TbrokerageConfig;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.model.trans.TbrokerageDetail;
import com.cn.flypay.model.trans.TbrokerageLog;
import com.cn.flypay.model.trans.TorderBonusProcess;
import com.cn.flypay.model.trans.TorgBrokerage;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.common.ProducerService;
import com.cn.flypay.service.sys.BrokerageConfigService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.utils.SysConvert;
import com.cn.flypay.utils.jpush.api.JiguangUtil;

@Service(value = "equalRateShareBonusService")
public class EqualRateShareBonusServiceImpl extends AbstractShareBonusService {

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private BrokerageConfigService brokerageConfigService;

	@Autowired
	private BaseDao<TorgBrokerage> orgBkDao;

	@Autowired
	private BaseDao<Tbrokerage> bkDao;

	@Autowired
	private BaseDao<TbrokerageDetail> bkdDao;

	@Autowired
	private ProducerService producerService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private BaseDao<TbrokerageLog> brokerageLogDao;
	/**
	 * 更新用户的AB推荐人的佣金，同时添加AB的佣金明细。 若A被冻结，B可以继续获取佣金
	 * 
	 * @param user
	 *            支付流水用户
	 * @param amt
	 *            支付金额
	 * @param shareBK
	 *            机构分配的总利润
	 * @param appId
	 *            手机APP交易流水ID
	 * @param type
	 *            交易类型
	 * @param transDate
	 *            交易时间
	 */
	public void updateABUserWhenShare(Tuser user, BigDecimal shareBKbd, TorderBonusProcess bonusProcess, Integer cftType) {
		Torganization org = organizationService.getTorganizationInCacheByCode(user.getAgentId());
		/* 交易金额 机构分润表 */
		Map<Long, Set<TbrokerageConfig>> bcMap = null;
		if (cftType - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0) {
			bcMap = brokerageConfigService.getAgentBrokerageConfig();
		} else {
			bcMap = brokerageConfigService.getTransBrokerageConfig();
		}
		if (user.getParentUser() != null) {
			Double shareBK = shareBKbd.doubleValue();
			Long pid = user.getParentUser().getId();
			log.info(" user parent is ID=" + pid);
			Tbrokerage bk = bkDao.get("select t from Tbrokerage t  left join t.user u where u.id = " + pid + " and t.status=0");
			int level = 1;
			int firstRate = getABuserRate(bcMap, org.getId(), user.getParentUser().getUserType(), level, cftType);
			sendJiGuangUnAgentUserAlert(user, user.getParentUser(), level, cftType);
			// 获取机构分配的金额=机构的分润 * 分润比例
			Double b = SysConvert.convertDoubleDigit(shareBK * firstRate / 100);
			if (b >= 0.01) {
				log.info(" user parent is ID=" + pid + ",he/she get " + b + "yuan");
				updateParentBrokerageAccount(user, user.getParentUser(), bonusProcess, bk, level, firstRate, b, cftType);
			} else {
				log.info(" user parent is ID=" + pid + ",he/she get " + b + " yuan,  but b < 0.01 ,it ignores! ");
			}
			Tuser ppuser = user.getParentUser().getParentUser();
			if (ppuser != null) {
				Long ppid = ppuser.getId();
				Tbrokerage pbk = super.bkDao.get("from Tbrokerage t where t.user = " + ppuser.getId() + " and t.status=0");
				level = 2;
				int secondRate = getABuserRate(bcMap, org.getId(), ppuser.getUserType(), level, cftType);
				sendJiGuangUnAgentUserAlert(user, ppuser, level, cftType);
				if (pbk != null) {
					Double pb = SysConvert.convertDoubleDigit(shareBK * secondRate / 100);
					if (pb >= 0.01) {
						log.info(" user parent-->parent is ID=" + ppid + ",he/she get " + pb + "yuan");
						updateParentBrokerageAccount(user, ppuser, bonusProcess, pbk, level, secondRate, pb, cftType);
					} else {
						log.info(" user parent-->parent is ID=" + ppid + ",he/she get " + pb + " yuan,  but b < 0.01 ,it ignores! ");
					}
				} else {
					log.info("user parent-->parent is freeze, user parent is ID=" + pid + ", he/she can not get brokeage");
				}
				if (org.getShareBonusLevelType() == 0 || org.getShareBonusLevelType() == 1) {
					Tuser pppuser = user.getParentUser().getParentUser().getParentUser();
					if (pppuser != null) {
						Long pppid = pppuser.getId();
						Tbrokerage ppbk = super.bkDao.get("from Tbrokerage t where t.user = " + pppuser.getId() + " and t.status=0");
						level = 3;
						int thirdRate = getABuserRate(bcMap, org.getId(), pppuser.getUserType(), level, cftType);
						sendJiGuangUnAgentUserAlert(user, pppuser, level, cftType);
						if (pbk != null) {
							Double ppb = SysConvert.convertDoubleDigit(shareBK * thirdRate / 100);
							if (ppb >= 0.01) {
								log.info(" user parent-->parent-->parent is ID=" + pppid + ",he/she get " + ppb + "yuan");
								updateParentBrokerageAccount(user, pppuser, bonusProcess, ppbk, level, thirdRate, ppb, cftType);
							} else {
								log.info(" user parent-->parent is ID=" + pppid + ",he/she get " + ppb + " yuan,  but b < 0.01 ,it ignores! ");
							}
						} else {
							log.info("user parent-->parent-->parent is freeze, user parent is ID=" + pppid + ", he/she can not get brokeage");
						}
					}
				}
			}

		}
	}

	/**
	 * 通知未升级的用户
	 * 
	 * @param loginName
	 * @param parentUser
	 * @param level
	 * @param cftType
	 */
	private void sendJiGuangUnAgentUserAlert(Tuser user, Tuser parentUser, int level, Integer cftType) {
		try {
			if (cftType - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0 && parentUser.getUserType() == 24) {
				String desc = String.format(JiguangUtil.ALTER_UNAGENT_USER_BROKERAGER, level, user.getLoginName(), convert(user.getUserType()));
				InfoList t = new InfoList(parentUser.getId(), JiguangUtil.ALTER_UNAGENT_USER_BROKERAGER_TITLE, InfoList.info_Type.person.getCode(), desc, 0, 0);
				producerService.sendInfoList(t);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	private void updateParentBrokerageAccount(Tuser user, Tuser puser, TorderBonusProcess bonusProcess, Tbrokerage bk, int level, int levelRate, Double b, Integer cftType) {

		try {
			if (bk != null) {
				Integer brokerageType =1;
				TuserOrder order = bonusProcess.getOrder();
				updateUserBrokerage(bk, b, cftType);// 更新客户的分润账户
				String desc = level + "级交易金额提成" + levelRate + "%";
				if (order.getTransPayType() - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0) {
					desc = level + "级" + convert(puser.getUserType()) + "提成" + levelRate + "%";
					brokerageType =2;
				}
				TbrokerageDetail bd = new TbrokerageDetail(order.getType(), order.getOrgAmt(), order.getCreateTime(), BigDecimal.valueOf(b), level, desc,brokerageType);
				bd.setBrokerageUserRate(Double.valueOf(levelRate) / 100);
				bd.setPhone(user.getLoginName());
				bd.setUserCode(user.getCode());
				bd.setBrokerage(BigDecimal.valueOf(b));
				bd.setTbrokerage(bk);
				bd.setBrokerageUser(puser);
				bd.setBonusProcess(bonusProcess);
				bkdDao.save(bd);
				//分拥日志
				TbrokerageLog brokerageLog = new TbrokerageLog(bk,UserOrder.cd_type.D.name(), BigDecimal.valueOf(b), desc);
				brokerageLog.setAvlAmt(bk.getBrokerage());
				brokerageLog.setLockOutAmt(bk.getLockBrokerage());
				brokerageLog.setOrdernum(bonusProcess.getOrderNum());
				brokerageLogDao.save(brokerageLog);
				try {
					String title = "您收获了佣金" + b + "元";
					desc = String.format(JiguangUtil.ALTER_USER_BROKERAGER, user.getLoginName(), b, user.getOrganization().getAppName());
					InfoList t = new InfoList(puser.getId(), title, InfoList.info_Type.person.getCode(), desc, 0, 0);
					producerService.sendInfoList(t);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				log.info(puser.getLoginName() + "佣金账号已经被冻结，跳过本人的分润");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private String convert(int usertype) {
		String type = "普通会员";
		switch (usertype) {
		case 21:
			type = "钻石会员";
			break;
		case 22:
			type = "金牌会员";
			break;
		case 23:
			type = "银牌会员";
			break;

		default:
			break;
		}
		return type;
	}

	/**
	 * 更新用户分润表
	 * 
	 * @param bk
	 * @param b
	 */
	private void updateUserBrokerage(Tbrokerage bk, Double b, Integer cftType) {
		if (UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == cftType) {
			bk.setTotalAgentBrokerage(bk.getTotalAgentBrokerage().add(BigDecimal.valueOf(b)));
		} else if (UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode() == cftType) {
			bk.setTotalTransBrokerage(bk.getTotalTransBrokerage().add(BigDecimal.valueOf(b)));
		} else {
			return;
		}
		bk.setTotalBrokerage(bk.getTotalBrokerage().add(BigDecimal.valueOf(b)));
		bk.setBrokerage(bk.getBrokerage().add(BigDecimal.valueOf(b)));
		bkDao.update(bk);
	}

	private int getABuserRate(Map<Long, Set<TbrokerageConfig>> bcMap, Long orgId, int userType, int level, Integer cfgType) {
		Set<TbrokerageConfig> tcSet = bcMap.get(orgId);
		int returnRate = 0;
		for (TbrokerageConfig tc : tcSet) {
			if (Integer.parseInt(tc.getId().getCfgType()) - cfgType == 0 && Integer.parseInt(tc.getId().getAgentType()) - userType == 0) {
				switch (level) {
				case 1:
					returnRate = tc.getFirstRate();
					break;
				case 2:
					returnRate = tc.getSecRate();
					break;
				case 3:
					returnRate = tc.getThirdRate();
					break;
				default:
					break;
				}
				break;
			}
		}
		log.info("return rate =" + returnRate + " level=" + level);
		return returnRate;
	}
}
