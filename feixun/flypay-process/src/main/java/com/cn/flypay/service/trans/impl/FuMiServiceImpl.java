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
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.common.ProducerService;
import com.cn.flypay.service.sys.BrokerageConfigService;
import com.cn.flypay.service.sys.OrgChannelUserRateConfigService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.trans.FuMiService;
import com.cn.flypay.utils.SysConvert;
import com.cn.flypay.utils.jpush.api.JiguangUtil;

@Service(value = "fuMiServiceImpl")
public class FuMiServiceImpl implements FuMiService {

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private OrgChannelUserRateConfigService orgChannelUserRateConfigService;
	
	@Autowired
	private BrokerageConfigService brokerageConfigService;
	@Autowired
	protected OrganizationService organizationService;

	@Autowired
	private BaseDao<TorgBrokerage> orgBkDao;

	@Autowired
	BaseDao<Tbrokerage> bkDao;

	@Autowired
	private BaseDao<TbrokerageDetail> bkdDao;

	@Autowired
	private BaseDao<TorderBonusProcess> bonusProcessDao;

	@Autowired
	private ProducerService producerService;
	

	@Override
	public String updateABUserWhenShare(Tuser user, BigDecimal shareBKbd, TorderBonusProcess bonusProcess, Integer cftType) {
		if (user.getUserType() == 21) {
			log.info("========蚨米用户代理级别为最高级，无利润分配给上级========");
		} else {
			if (user.getParentUser() != null) {
				Torganization org = organizationService.getTorganizationInCacheByCode(user.getAgentId());
				Tuser puser = user.getParentUser();
				TuserOrder order = bonusProcess.getOrder();
				int level = 1;
				Long pid = user.getParentUser().getId();
				
				
				if (puser.getUserType() >= user.getUserType()) {
					log.info("========蚨米一级推荐人级别低于刷卡人，无利润分配========");
				} else {
					log.info(" user parent is ID=" + pid);
					Double b = SysConvert.convertDoubleDigit(getPtradeBonus(shareBKbd, order, user.getUserType(), puser.getUserType(), org));
					if (b >= 0.01) {
						log.info(" user parent is ID=" + pid + ",he/she get " + b + "yuan");
						Tbrokerage bk = bkDao.get("select t from Tbrokerage t  left join t.user u where u.id = " + pid + " and t.status=0");
						if (bk != null) {
							updateParentBrokerageAccount(user, user.getParentUser(), bonusProcess, bk, level, b, cftType);
						} else {
							log.info("user parent is freeze, user  is ID=" + pid + ", he/she can not get brokeage");
						}
					} else {
						log.info(" user parent is ID=" + pid + ",he/she get " + b + " yuan,  but b < 0.01 ,it ignores! ");
					}
					log.info("========蚨米一级推荐人利润分配完成========");
				return null;	
			 }
			
				
				
		     Tuser ppuser = puser.getParentUser();
		     if (ppuser != null) {
		    	 Long ppid = ppuser.getId();
				 level = 2;
				 if (ppuser.getUserType() >= user.getUserType() || ppuser.getUserType() >= puser.getUserType()) {
						log.info("========蚨米二级推荐人级别低于刷卡人或一级推荐人，无利润分配========");
				 }else{
					 int minType = puser.getUserType() < user.getUserType() ? puser.getUserType() : user.getUserType();
						Double pb = SysConvert.convertDoubleDigit(getPtradeBonus(shareBKbd, order, minType, ppuser.getUserType(), org));
						System.out.println();
						if (pb >= 0.01) {
							log.info(" user parent-->parent is ID=" + ppid + ",he/she get " + pb + "yuan");
							Tbrokerage pbk = bkDao.get("from Tbrokerage t where t.user = " + ppid + " and t.status=0");
							if (pbk != null) {
								updateParentBrokerageAccount(user, ppuser, bonusProcess, pbk, level, pb, cftType);
							} else {
								log.info("user parent-->parent is freeze, user parent is ID=" + pid + ", he/she can not get brokeage");
							}
						} else {
							log.info(" user parent-->parent is ID=" + ppid + ",he/she get " + pb + " yuan,  but b < 0.01 ,it ignores! ");
						}
						log.info("========蚨米二级推荐人利润分配完成========");
					return null;	
				 }
				 
				if (org.getShareBonusLevelType() == 0) {
					Tuser pppuser = ppuser.getParentUser();
					if (pppuser != null) {
						Long pppid = pppuser.getId();
						level = 3;
						if (pppuser.getUserType() >= user.getUserType() || pppuser.getUserType() >= puser.getUserType() || pppuser.getUserType() >= ppuser.getUserType()) {
							log.info("========蚨米三级推荐人级别低于刷卡人或一二级推荐人，无利润分配========");
						}else{
							int minType = puser.getUserType() < user.getUserType() ? puser.getUserType() : user.getUserType();
							minType = ppuser.getUserType() < minType ? ppuser.getUserType() : minType;
							Double ppb = SysConvert.convertDoubleDigit(getPtradeBonus(shareBKbd, order, minType, pppuser.getUserType(), org));
							if (ppb >= 0.01) {
								log.info(" user parent-->parent-->parent is ID=" + pppid + ",he/she get " + ppb + "yuan");
								Tbrokerage ppbk = bkDao.get("from Tbrokerage t where t.user = " + pppid + " and t.status=0");
								if (ppbk != null) {
									updateParentBrokerageAccount(user, pppuser, bonusProcess, ppbk, level, ppb, cftType);
								} else {
									log.info("user parent-->parent-->parent is freeze, user parent is ID=" + pppid + ", he/she can not get brokeage");
								}
								 return null;
							} else {
								log.info(" user parent-->parent is ID=" + pppid + ",he/she get " + ppb + " yuan,  but b < 0.01 ,it ignores! ");
							}
						}
					}
				}	
		     }
		}
	 }
	 return null;
		
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
	
	
	
	void updateParentBrokerageAccount(Tuser user, Tuser puser, TorderBonusProcess bonusProcess, Tbrokerage bk, int level, Double b, Integer cftType) {
		try {
			if (bk != null) {
				Integer brokerageType =1;
				TuserOrder order = bonusProcess.getOrder();
				updateUserBrokerage(bk, b, cftType);// 更新客户的分润账户
				String desc = level + "级" + convert(puser.getUserType()) + "交易金额提成" + b + "元";
				if (order.getTransPayType() - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0) {
					desc = level + "级" + convert(puser.getUserType()) + "提成" + b + "元";
					brokerageType =2;
				}
				TbrokerageDetail bd = new TbrokerageDetail(order.getType(), order.getOrgAmt(), order.getCreateTime(), BigDecimal.valueOf(b), level, desc,brokerageType);
				bd.setBrokerageUserRate(0d);// 不按照比例分成
				bd.setPhone(user.getLoginName());
				bd.setUserCode(user.getCode());
				bd.setBrokerage(BigDecimal.valueOf(b));
				bd.setTbrokerage(bk);
				bd.setBrokerageUser(puser);
				bd.setBonusProcess(bonusProcess);
				bkdDao.save(bd);
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
	
	
	
	private String convert(int usertype) {
		String type = "普通会员";
		switch (usertype) {
		case 21:
			type = "高级会员";
			break;
		case 22:
			type = "中级会员";
			break;
		default:
			break;
		}
		return type;
	}
	

	
}
