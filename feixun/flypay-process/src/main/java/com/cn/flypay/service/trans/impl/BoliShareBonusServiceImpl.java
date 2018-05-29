package com.cn.flypay.service.trans.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.model.trans.TbrokerageDetail;
import com.cn.flypay.model.trans.TorderBonusProcess;
import com.cn.flypay.model.trans.TorgBrokerage;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.common.ProducerService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.utils.SysConvert;

@Service(value = "boliShareBonusService")
public class BoliShareBonusServiceImpl extends AbstractShareBonusService {
	/* 普通代理费 */
	private static Double[][] c_agent = { { 60d, 3000d }, { 0d, 0d }, { 0d, 0d } };
	/* 金牌代理费 */
	private static Double[][] b_agent = { { 70d, 3000d }, { 30d, 1000d }, { 70d, 2000d } };
	/* 钻石代理费 a_agent[0][1] 表示第一层 钻石代理分润 */
	private static Double[][] a_agent = { { 80d, 5000d }, { 40d, 2000d }, { 80d, 3000d } };

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private BaseDao<TorgBrokerage> orgBkDao;

	@Autowired
	private BaseDao<Tbrokerage> bkDao;

	@Autowired
	private ProducerService producerService;
	@Autowired
	private OrganizationService organizationService;

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
		Torganization org = organizationService.getTorganizationInCacheById(3l);
		if (cftType - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0) {
			shareAgentBonus(user, shareBKbd, bonusProcess, cftType, org);
		} else {
			shareTradeBonus(user, shareBKbd, bonusProcess, cftType, org);
		}
	}

	private void shareAgentBonus(Tuser user, BigDecimal shareBKbd, TorderBonusProcess bonusProcess, Integer cftType, Torganization org) {
		int type = 0;// 金牌
		if (shareBKbd.doubleValue() - org.getDiamondAgent().doubleValue() == 0) {
			type = 1;// 钻石
		}
		if (user.getParentUser() != null) {
			Tuser puser = user.getParentUser();
			Long pid = user.getParentUser().getId();
			log.info(" user parent is ID=" + pid);
			Tbrokerage bk = bkDao.get("select t from Tbrokerage t  left join t.user u where u.id = " + pid + " and t.status=0");
			int level = 1;
			if (bk != null) {
				Double b = SysConvert.convertDoubleDigit(getParentAgentBonus(puser, type, level));
				if (b >= 0.01) {
					log.info(" user parent is ID=" + pid + ",he/she get " + b + "yuan");
					updateParentBrokerageAccount(user, user.getParentUser(), bonusProcess, bk, level, b, cftType);
				} else {
					log.info(" user parent is ID=" + pid + ",he/she get " + b + " yuan,  but b < 0.01 ,it ignores! ");
				}
			} else {
				log.info("user parent is freeze, user parent is ID=" + pid + ", he/she can not get brokeage");
			}

			Tuser ppuser = user.getParentUser().getParentUser();
			if (ppuser != null) {
				Long ppid = ppuser.getId();
				Tbrokerage pbk = bkDao.get("from Tbrokerage t where t.user = " + ppid + " and t.status=0");
				level = 2;
				if (pbk != null) {
					Double pb = SysConvert.convertDoubleDigit(getParentAgentBonus(ppuser, type, level));
					if (pb >= 0.01) {
						log.info(" user parent-->parent is ID=" + ppid + ",he/she get " + pb + "yuan");
						updateParentBrokerageAccount(user, ppuser, bonusProcess, pbk, level, pb, cftType);
					} else {
						log.info(" user parent-->parent is ID=" + ppid + ",he/she get " + pb + " yuan,  but b < 0.01 ,it ignores! ");
					}
				} else {
					log.info("user parent-->parent is freeze, user parent is ID=" + pid + ", he/she can not get brokeage");
				}
				Tuser pppuser = user.getParentUser().getParentUser().getParentUser();
				if (pppuser != null) {
					Long pppid = pppuser.getId();
					Tbrokerage ppbk = bkDao.get("from Tbrokerage t where t.user = " + pppid + " and t.status=0");
					level = 3;
					if (pbk != null) {
						Double ppb = SysConvert.convertDoubleDigit(getParentAgentBonus(ppuser, type, level));
						if (ppb >= 0.01) {
							log.info(" user parent-->parent-->parent is ID=" + pppid + ",he/she get " + ppb + "yuan");
							updateParentBrokerageAccount(user, pppuser, bonusProcess, ppbk, level, ppb, cftType);
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

	private void shareTradeBonus(Tuser user, BigDecimal shareBKbd, TorderBonusProcess bonusProcess, Integer cftType, Torganization org) {

		if (user.getParentUser() != null) {
			Tuser puser = user.getParentUser();
			Long pid = user.getParentUser().getId();
			log.info(" user parent is ID=" + pid);
			Tbrokerage bk = bkDao.get("select t from Tbrokerage t  left join t.user u where u.id = " + pid + " and t.status=0");
			int level = 1;
			Double b = SysConvert.convertDoubleDigit(getPtradeBonus(shareBKbd, user, puser, org));
			if (b >= 0.01) {
				log.info(" user parent is ID=" + pid + ",he/she get " + b + "yuan");
				updateParentBrokerageAccount(user, user.getParentUser(), bonusProcess, bk, level, b, cftType);
			} else {
				log.info(" user parent is ID=" + pid + ",he/she get " + b + " yuan,  but b < 0.01 ,it ignores! ");
			}

			Tuser ppuser = user.getParentUser().getParentUser();
			if (ppuser != null) {
				Long ppid = ppuser.getId();
				Tbrokerage pbk = bkDao.get("from Tbrokerage t where t.user = " + ppid + " and t.status=0");
				level = 2;
				if (pbk != null) {
					Double pb = SysConvert.convertDoubleDigit(getPtradeBonus(shareBKbd, user, puser, ppuser, org));
					if (pb >= 0.01) {
						log.info(" user parent-->parent is ID=" + ppid + ",he/she get " + pb + "yuan");
						updateParentBrokerageAccount(user, ppuser, bonusProcess, pbk, level, pb, cftType);
					} else {
						log.info(" user parent-->parent is ID=" + ppid + ",he/she get " + pb + " yuan,  but b < 0.01 ,it ignores! ");
					}
				} else {
					log.info("user parent-->parent is freeze, user parent is ID=" + pid + ", he/she can not get brokeage");
				}
				Tuser pppuser = user.getParentUser().getParentUser().getParentUser();
				if (pppuser != null) {
					Long pppid = pppuser.getId();
					Tbrokerage ppbk = bkDao.get("from Tbrokerage t where t.user = " + pppid + " and t.status=0");
					level = 3;
					if (pbk != null) {
						Double ppb = SysConvert.convertDoubleDigit(getPtradeBonus(shareBKbd, user, puser, ppuser, pppuser, org));
						if (ppb >= 0.01) {
							log.info(" user parent-->parent-->parent is ID=" + pppid + ",he/she get " + ppb + "yuan");
							updateParentBrokerageAccount(user, pppuser, bonusProcess, ppbk, level, ppb, cftType);
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

	private Double getPtradeBonus(BigDecimal shareBKbd, Tuser user, Tuser puser, Tuser ppuser, Tuser pppuser, Torganization org) {
		BigDecimal allRate = org.getDefaultInputFee().subtract(org.getDefaultInputDiamondRate());
		BigDecimal rate = BigDecimal.ZERO;
		MathContext mc = new MathContext(4, RoundingMode.HALF_DOWN);
		if (pppuser.getUserType() == 21) {
			if (ppuser.getUserType() == 22 && puser.getUserType() == 22 && user.getUserType() == 22) {
			} else if (ppuser.getUserType() == 22 && puser.getUserType() == 22 && user.getUserType() == 24) {
				rate = org.getDefaultInputGoldRate().subtract(org.getDefaultInputDiamondRate()).divide(allRate, mc);
			} else if (ppuser.getUserType() == 22 && puser.getUserType() == 24 && user.getUserType() == 22) {
				rate = org.getDefaultInputGoldRate().subtract(org.getDefaultInputDiamondRate()).divide(allRate, mc);
			} else if (ppuser.getUserType() == 22 && puser.getUserType() == 24 && user.getUserType() == 24) {
				rate = org.getDefaultInputGoldRate().subtract(org.getDefaultInputDiamondRate()).divide(allRate, mc);
			} else if (ppuser.getUserType() == 24 && puser.getUserType() == 22 && user.getUserType() == 22) {
				rate = org.getDefaultInputGoldRate().subtract(org.getDefaultInputDiamondRate()).divide(allRate, mc);
			} else if (ppuser.getUserType() == 24 && puser.getUserType() == 22 && user.getUserType() == 24) {
				rate = org.getDefaultInputGoldRate().subtract(org.getDefaultInputDiamondRate()).divide(allRate, mc);
			} else if (ppuser.getUserType() == 24 && puser.getUserType() == 24 && user.getUserType() == 22) {
				rate = org.getDefaultInputGoldRate().subtract(org.getDefaultInputDiamondRate()).divide(allRate, mc);
			} else if (ppuser.getUserType() == 24 && puser.getUserType() == 24 && user.getUserType() == 24) {
				rate = org.getDefaultInputFee().subtract(org.getDefaultInputDiamondRate()).divide(allRate, mc);
			}
		} else if (pppuser.getUserType() == 22) {
			if (user.getUserType() == 24 && puser.getUserType() == 24 && ppuser.getUserType() == 24) {
				rate = org.getDefaultInputFee().subtract(org.getDefaultInputGoldRate()).divide(allRate, mc);
			}
		}
		return shareBKbd.multiply(rate).doubleValue();
	}

	private Double getPtradeBonus(BigDecimal shareBKbd, Tuser user, Tuser puser, Tuser ppuser, Torganization org) {
		BigDecimal rate = BigDecimal.ZERO;
		MathContext mc = new MathContext(4, RoundingMode.HALF_DOWN);
		BigDecimal allRate = org.getDefaultInputFee().subtract(org.getDefaultInputDiamondRate());
		if (ppuser.getUserType() == 21) {//
			if (puser.getUserType() == 22 && (user.getUserType() == 24 || user.getUserType() == 22)) {
				rate = org.getDefaultInputGoldRate().subtract(org.getDefaultInputDiamondRate()).divide(allRate, mc);
			} else if (puser.getUserType() == 24 && user.getUserType() == 22) {
				rate = org.getDefaultInputGoldRate().subtract(org.getDefaultInputDiamondRate()).divide(allRate, mc);
			} else if (puser.getUserType() == 24 && user.getUserType() == 24) {// 用户和第一级为普通
				rate = org.getDefaultInputFee().subtract(org.getDefaultInputDiamondRate()).divide(allRate, mc);
			}
		} else if (ppuser.getUserType() == 22) {
			if (user.getUserType() == 24 && puser.getUserType() == 24) {
				rate = org.getDefaultInputFee().subtract(org.getDefaultInputGoldRate()).divide(allRate, mc);
			}
		}
		return shareBKbd.multiply(rate).doubleValue();
	}

	private Double getPtradeBonus(BigDecimal shareBKbd, Tuser user, Tuser puser, Torganization org) {
		BigDecimal rate = BigDecimal.ZERO;
		BigDecimal allRate = org.getDefaultInputFee().subtract(org.getDefaultInputDiamondRate());
		MathContext mc = new MathContext(4, RoundingMode.HALF_DOWN);
		// 21 ,22, 24
		switch (puser.getUserType() - user.getUserType()) {
		case -3:// 21-24
			rate = org.getDefaultInputFee().subtract(org.getDefaultInputDiamondRate()).divide(allRate, mc);
			break;
		case -2:// 22-24
			rate = org.getDefaultInputFee().subtract(org.getDefaultInputGoldRate()).divide(allRate, mc);
			break;
		case -1:// 21-22
			rate = org.getDefaultInputGoldRate().subtract(org.getDefaultInputDiamondRate()).divide(allRate, mc);
			break;

		default:
			break;
		}
		return shareBKbd.multiply(rate).doubleValue();
	}

	/**
	 * 
	 * @param puser
	 * @param type
	 *            0表示金牌 1表示钻石
	 * @param level
	 *            表示所在层级
	 * @return
	 */
	private static Double getParentAgentBonus(Tuser puser, int type, int level) {
		Double bonus = 0d;
		switch (puser.getUserType()) {
		case 21:
			bonus = a_agent[level - 1][type];
			break;
		case 22:
			bonus = b_agent[level - 1][type];
			break;
		case 24:
			bonus = c_agent[level - 1][type];
			break;
		default:
			break;
		}
		return bonus;
	}

}
