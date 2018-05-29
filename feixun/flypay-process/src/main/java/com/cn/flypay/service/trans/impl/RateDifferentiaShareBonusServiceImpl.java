package com.cn.flypay.service.trans.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.model.trans.TorderBonusProcess;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.sys.OrgChannelUserRateConfigService;
import com.cn.flypay.utils.SysConvert;

@Service(value = "rateDifferentiaShareBonusService")
public class RateDifferentiaShareBonusServiceImpl extends AbstractShareBonusService {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private OrgChannelUserRateConfigService orgChannelUserRateConfigService;

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
		if (cftType - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0) {
			log.error("this method can not support agent share bonus !");
		} else {
			shareTradeBonus(user, shareBKbd, bonusProcess, cftType);
		}
	}

	private void shareTradeBonus(Tuser user, BigDecimal shareBKbd, TorderBonusProcess bonusProcess, Integer cftType) {
		if (user.getUserType() == 21) {
			log.info("用户代理级别为最高级，无利润分配给上级");
		} else {
			if (user.getParentUser() != null) {
				Torganization org = super.organizationService.getTorganizationInCacheByCode(user.getAgentId());
				Tuser puser = user.getParentUser();
				TuserOrder order = bonusProcess.getOrder();
				int level = 1;
				Long pid = user.getParentUser().getId();
				if (puser.getUserType() >= user.getUserType()) {
					log.info("一级推荐人级别低于刷卡人，无利润分配");
				} else {
					log.info(" user parent is ID=" + pid);
					Double b = SysConvert.convertDoubleDigit(getPtradeBonus(shareBKbd, order, user.getUserType(), puser.getUserType(), org));
					if (b >= 0.01) {
						log.info(" user parent is ID=" + pid + ",he/she get " + b + "yuan");
						Tbrokerage bk = super.bkDao.get("select t from Tbrokerage t  left join t.user u where u.id = " + pid + " and t.status=0");
						if (bk != null) {
							updateParentBrokerageAccount(user, user.getParentUser(), bonusProcess, bk, level, b, cftType);
						} else {
							log.info("user parent is freeze, user  is ID=" + pid + ", he/she can not get brokeage");
						}
					} else {
						log.info(" user parent is ID=" + pid + ",he/she get " + b + " yuan,  but b < 0.01 ,it ignores! ");
					}
				}

				Tuser ppuser = puser.getParentUser();
				if (ppuser != null) {
					Long ppid = ppuser.getId();
					level = 2;

					if (ppuser.getUserType() >= user.getUserType() || ppuser.getUserType() >= puser.getUserType()) {
						log.info("二级推荐人级别低于刷卡人或一级推荐人，无利润分配");
					} else {
						int minType = puser.getUserType() < user.getUserType() ? puser.getUserType() : user.getUserType();
						Double pb = SysConvert.convertDoubleDigit(getPtradeBonus(shareBKbd, order, minType, ppuser.getUserType(), org));
						System.out.println();
						if (pb >= 0.01) {
							log.info(" user parent-->parent is ID=" + ppid + ",he/she get " + pb + "yuan");
							Tbrokerage pbk = super.bkDao.get("from Tbrokerage t where t.user = " + ppid + " and t.status=0");
							if (pbk != null) {
								updateParentBrokerageAccount(user, ppuser, bonusProcess, pbk, level, pb, cftType);
							} else {
								log.info("user parent-->parent is freeze, user parent is ID=" + pid + ", he/she can not get brokeage");
							}
						} else {
							log.info(" user parent-->parent is ID=" + ppid + ",he/she get " + pb + " yuan,  but b < 0.01 ,it ignores! ");
						}
					}
					if (org.getShareBonusLevelType() == 0) {
						Tuser pppuser = ppuser.getParentUser();
						if (pppuser != null) {
							Long pppid = pppuser.getId();
							level = 3;
							if (pppuser.getUserType() >= user.getUserType() || pppuser.getUserType() >= puser.getUserType() || pppuser.getUserType() >= ppuser.getUserType()) {
								log.info("三级推荐人级别低于刷卡人或一二级推荐人，无利润分配");
							} else {
								int minType = puser.getUserType() < user.getUserType() ? puser.getUserType() : user.getUserType();
								minType = ppuser.getUserType() < minType ? ppuser.getUserType() : minType;
								Double ppb = SysConvert.convertDoubleDigit(getPtradeBonus(shareBKbd, order, minType, pppuser.getUserType(), org));
								if (ppb >= 0.01) {
									log.info(" user parent-->parent-->parent is ID=" + pppid + ",he/she get " + ppb + "yuan");
									Tbrokerage ppbk = super.bkDao.get("from Tbrokerage t where t.user = " + pppid + " and t.status=0");
									if (ppbk != null) {
										updateParentBrokerageAccount(user, pppuser, bonusProcess, ppbk, level, ppb, cftType);
									} else {
										log.info("user parent-->parent-->parent is freeze, user parent is ID=" + pppid + ", he/she can not get brokeage");
									}
								} else {
									log.info(" user parent-->parent is ID=" + pppid + ",he/she get " + ppb + " yuan,  but b < 0.01 ,it ignores! ");
								}
							}
						}
					}
				}
			}
		}

	}

	private Double getPtradeBonus(BigDecimal shareBKbd, TuserOrder order, int sourceUserType, int targetUserType, Torganization org) {
		BigDecimal rate = BigDecimal.ZERO;
		MathContext mc = new MathContext(4, RoundingMode.FLOOR);
		rate = orgChannelUserRateConfigService.getUserShareRateInCache(UserOrder.getUserPayChannelType(order.getType()), sourceUserType, targetUserType, order.getInputAccType(), org.getId());
		if (rate.compareTo(BigDecimal.ZERO) == 1) {
			rate = rate.divide(order.getShareRate(), mc);
		}
		return shareBKbd.multiply(rate).doubleValue();
	}

}
