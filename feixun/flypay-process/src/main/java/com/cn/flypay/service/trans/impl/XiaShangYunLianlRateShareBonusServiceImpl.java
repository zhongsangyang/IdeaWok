package com.cn.flypay.service.trans.impl;

import java.math.BigDecimal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.model.trans.TbrokerageDetail;
import com.cn.flypay.model.trans.TbrokerageLog;
import com.cn.flypay.model.trans.TorderBonusProcess;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.common.ProducerService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.jpush.api.JiguangUtil;





@Service(value = "xiaShangYunLianlRateShareBonusService")
public class XiaShangYunLianlRateShareBonusServiceImpl extends AbstractShareBonusService {

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private BaseDao<Tbrokerage> bkDao;
	@Autowired
	private BaseDao<TbrokerageDetail> bkdDao;
	@Autowired
	private ProducerService producerService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private UserService userservice;
	@Autowired
	private BaseDao<Tuser> userDao;
	@Autowired
	private BaseDao<TbrokerageLog> brokerageLogDao;
	
	
	public void updateABUserWhenShare(Tuser user, BigDecimal shareBKbd, TorderBonusProcess bonusProcess, Integer cftType) {
		if (cftType - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0) {
			Torganization org = organizationService.getTorganizationInCacheByCode(user.getAgentId());
			TuserOrder userOrder = bonusProcess.getOrder();
			BigDecimal OrgAmt = userOrder.getOrgAmt();
			Double bb = OrgAmt.doubleValue();
			int level = 1;
			if(OrgAmt.subtract(new BigDecimal("89")).compareTo(BigDecimal.ZERO)==0){
				countProfit(user, org, bb, 20.00, cftType, level, bonusProcess);
				Tuser puser = user.getParentUser();
				if(puser.getUserType()==24){
					puser.setRefreSum1(puser.getRefreSum1()+1);
					userDao.update(puser);
					if(puser.getRefreSum1()==5){
						puser.setUserType(22);
						userDao.update(puser);
					}
				}
			}else if(OrgAmt.subtract(new BigDecimal("369")).compareTo(BigDecimal.ZERO)==0){
				Tuser puser = user.getParentUser();
				if(puser !=null){
				  if(puser.getUserType()==21){
						Tbrokerage bk = bkDao.get("from Tbrokerage t where t.user = " + puser.getId() + " and t.status=0");
						if(bk!=null){
							updateParentBrokerageAccountTwo(user, puser, bonusProcess, bk, level, 60.00, cftType);
							bb = bb-60.00;
						}else{
							log.info("-------------购买代理订单一级推荐人无分佣账户不参与分佣------------------");
						}
					 }else{
						log.info("-------------购买代理订单用户无一级推荐人不是店长不参与分佣------------------");
					 }
	
						
						Tuser ppuser = puser.getParentUser();
						if(ppuser !=null){
							if(ppuser.getUserType()==21){
								Tbrokerage bbk = bkDao.get("from Tbrokerage t where t.user = " + ppuser.getId() + " and t.status=0");
								if(bbk!=null){
									updateParentBrokerageAccountTwo(user, ppuser, bonusProcess, bbk, level, 20.00, cftType);
									bb = bb-20.00;
								}else{
									log.info("-------------购买代理订单二级推荐人无分佣账户不参与分佣------------------");
								}
							}else{
								log.info("-------------购买代理订单用户无二级推荐人不是店长不参与分佣------------------");
							}
								
								
								
								Tuser pppuser = ppuser.getParentUser();
								if(pppuser !=null){
									if(pppuser.getUserType()==21){
										Tbrokerage bbbk = bkDao.get("from Tbrokerage t where t.user = " + pppuser.getId() + " and t.status=0");
										if(bbbk!=null){
											updateParentBrokerageAccountTwo(user, pppuser, bonusProcess, bbbk, level, 30.00, cftType);
											bb = bb-30.00;
										}else{
											log.info("-------------购买代理订单三级推荐人无分佣账户不参与分佣------------------");
										}
									}else{
										log.info("-------------购买代理订单用户无三级推荐人不是店长不参与分佣------------------");
									}
								}else{
									log.info("-------------购买代理订单用户无三级推荐人不参与分佣------------------");
								}
						}else{
							log.info("-------------购买代理订单用户无二级推荐人不参与分佣------------------");
						}
				}else{
					log.info("-------------购买代理订单用户无一级推荐人不参与分佣------------------");
				}
				countProfit(user, org, bb, 139.00, cftType, level, bonusProcess);
			}
		} 
	}

	
	
	
	
	
	private void updateParentBrokerageAccountTwo(Tuser user, Tuser puser, TorderBonusProcess bonusProcess, Tbrokerage bk, int level, Double b, Integer cftType) {
		try {
			if (bk != null) {
				Integer brokerageType =1;
				TuserOrder order = bonusProcess.getOrder();
				updateUserBrokerage(bk, b, cftType);// 更新客户的分润账户
				String desc = level + "级交易金额提成";
				if (order.getTransPayType() - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0) {
					if(level==1){
						desc = level + "级店员提成"+b+"元";
					}else{
						desc = level + "级店长提成"+b+"元";
					}
					brokerageType =2;
				}
				TbrokerageDetail bd = new TbrokerageDetail(order.getType(), order.getOrgAmt(), order.getCreateTime(), BigDecimal.valueOf(b), level, desc,brokerageType);
				bd.setBrokerageUserRate(0.00);
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
					String title = "您收获了分润" + b + "元";
					desc = String.format(JiguangUtil.ALTER_USER_BROKERAGERTWO, user.getLoginName(), b);
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
	
	
	
	
	private void countProfit(Tuser user,Torganization org,Double bb,Double bc,Integer cftType,Integer level,TorderBonusProcess bonusProcess){
		Torganization org1 = organizationService.getTorganizationInCacheById(user.getOrganization().getId());
		if(org1!=null){
			if(org1.getOrgType().equals("5")){
				Tuser luser = userservice.findUserByLoginNameT(org1.getUserPhone(), org.getCode());
				if(luser!=null){
					Tbrokerage lk = bkDao.get("from Tbrokerage t where t.user = " + luser.getId() + " and t.status=0");
					if(lk!=null){
						updateParentBrokerageAccountTwo(user, luser, bonusProcess, lk, level, bc, cftType);
						bb = bb-bc;
					}else{
						log.info("-------------购买代理订单推荐人无分佣账户不参与分佣------------------");
					}
				}else{
					log.info("-------------购买代理订单推荐人无分佣用户不参与分佣------------------");
				}
			}else{
				log.info("-------------购买代理订单推荐人不是老板级别不参与分佣------------------");
			}
		}else{
			log.info("-------------购买代理订单推荐人无代理商不参与分佣------------------");
		}
		
		
		
		Torganization org2 = organizationService.getTorganizationInCacheByCode(org.getCode());
		Tuser zuser = userservice.findUserByLoginNameT(org2.getUserPhone(), org.getCode());
		if(zuser!=null){
			level = 2;
			Tbrokerage bbk = bkDao.get("from Tbrokerage t where t.user = " + zuser.getId() + " and t.status=0");
			if(bbk!=null){
				updateParentBrokerageAccountTwo(user, zuser, bonusProcess, bbk, level, bb, cftType);
			}else{
				log.info("-------------购买代理订单推荐人无分佣账户不参与分佣------------------");
			}
		}else{
			log.info("-------------购买代理订单用户总运营商结算用户不参与分佣------------------");
		}
	}
	
	
	
	
	
}
