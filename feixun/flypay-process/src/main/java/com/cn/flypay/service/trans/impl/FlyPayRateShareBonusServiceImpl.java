package com.cn.flypay.service.trans.impl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TbrokerageConfig;
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
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.SysConvert;
import com.cn.flypay.utils.jpush.api.JiguangUtil;

@Service(value = "flyPayRateShareBonusService")
public class FlyPayRateShareBonusServiceImpl extends AbstractShareBonusService {

	private Log log = LogFactory.getLog(getClass());
	
	@Value("${memberType1}")
	private String memberType1;
	@Value("${memberType2}")
	private String memberType2;
	@Value("${memberType3}")
	private String memberType3;
	@Value("${Type1RateType1}")
	private String Type1RateType1;
	@Value("${Type1RateType2}")
	private String Type1RateType2;
	@Value("${Type1RateType3}")
	private String Type1RateType3;
	@Value("${Type2RateType1}")
	private String Type2RateType1;
	@Value("${Type2RateType2}")
	private String Type2RateType2;
	@Value("${Type2RateType3}")
	private String Type2RateType3;
	@Value("${Type2RateTypeZ1}")
	private String Type2RateTypeZ1;
	@Value("${Type3RateType1}")
	private String Type3RateType1;
	@Value("${Type3RateType2}")
	private String Type3RateType2;
	@Value("${Type3RateType3}")
	private String Type3RateType3;
	@Value("${Type3RateTypeZ1}")
	private String Type3RateTypeZ1;
	@Value("${Type3RateTypeZ2}")
	private String Type3RateTypeZ2;
	@Value("${Type2RateTypeZ2}")
	private String Type2RateTypeZ2;
	@Value("${Type3RateTypeZ3}")
	private String Type3RateTypeZ3;
	@Value("${typeSum}")
	private String typeSum;
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
	private UserService userService;
	@Autowired
	private BaseDao<Tuser> userDao;

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
		TuserOrder userOrder = bonusProcess.getOrder();
		BigDecimal OrgAmt = userOrder.getOrgAmt();
		if(OrgAmt.subtract(new BigDecimal(memberType1)).compareTo(BigDecimal.ZERO)==0){
			Tuser puser = user.getParentUser();
			if(puser==null){
				Torganization org1 = organizationService.getTorganizationInCacheByMobile(puser.getLoginName());
				if(org1==null){
					if(puser.getUserType()==21){
						Tbrokerage bk = super.bkDao.get("from Tbrokerage t where t.user = " + puser.getId() + " and t.status=0");
						if (bk != null) {
						 	Double pb = OrgAmt.multiply(new BigDecimal(Type1RateType1)).doubleValue();
						 	if(pb>=0.01){
						 	 	updateParentBrokerageAccount(user, user.getParentUser(), bonusProcess, bk, 1, Integer.parseInt(memberType1), pb, cftType);
						 	}else{
						 		log.info("上级推荐人ID:" + puser.getId() + "佣金小于0.01,不参与分佣");
						 	}
//						 	PuserAdd(user, puser, 1, bonusProcess, cftType, Double.valueOf(Type1RateType1), bk);
						}else{
							log.info("上级推荐人ID:" + puser.getId() + "没有佣金账户,不参与分佣");
						}
					}
					JianTuiFan(OrgAmt, user, puser, bonusProcess, cftType);
				}else{
					if(org1.getOrgType().equals("5")){
						Tuser user1 = userService.findUserByLoginNameT(org1.getUserPhone(), org1.getCode());
						 if(user1!=null){
							 Tbrokerage bbk = super.bkDao.get("from Tbrokerage t where t.user = " + user1.getId() + " and t.status=0");
							 if(bbk !=null){
								 Double ppb = OrgAmt.multiply(new BigDecimal(Type2RateType1)).doubleValue();
								 if(ppb>=0.01){
									 updateParentBrokerageAccount(user, user1, bonusProcess, bbk, 2, Integer.parseInt(memberType1), ppb, cftType);
								 }else{
									 log.info("合伙人推荐人ID:" + user1.getId() + "佣金小于0.01,不参与分佣"); 
								 }
							 }else{
								 log.info("合伙人推荐人ID:" + user1.getId() + "没有佣金账户,不参与分佣");
							 }
							 JianTuiFan(OrgAmt, user, user1, bonusProcess, cftType);
						 }else{
							 log.info("-----合伙人代理商所属分佣账户为空不参与分佣-----");
						 }
					}else if(org1.getOrgType().equals("3")){
						Tuser user2 = userService.findUserByLoginNameT(org1.getUserPhone(), org1.getCode());
						if(user2!=null){
							 Tbrokerage bbbk = super.bkDao.get("from Tbrokerage t where t.user = " + user2.getId() + " and t.status=0");
							 if(bbbk !=null){
								 Double pppb = OrgAmt.multiply(new BigDecimal(Type3RateType1)).doubleValue();
								 if(pppb>=0.01){
									 updateParentBrokerageAccount(user, user2, bonusProcess, bbbk, 2, Integer.parseInt(memberType1), pppb, cftType);
								 }else{
									 log.info("运营商推荐人ID:" + user2.getId() + "佣金小于0.01,不参与分佣"); 
								 }
							 }else{
								 log.info("运营商推荐人ID:" + user2.getId() + "没有佣金账户,不参与分佣");
							 }
							 JianTuiFan(OrgAmt, user, user2, bonusProcess, cftType);
						 }else{
							 log.info("-----运营商代理商所属分佣账户为空不参与分佣-----");
						 }
					}
				}
			}else{
				log.info("------代理无推荐人不参与代理分佣------");
			}
		}else if(OrgAmt.subtract(new BigDecimal(memberType2)).compareTo(BigDecimal.ZERO)==0){
			Tuser p2user = user.getParentUser();
			if(p2user!=null){
				Torganization org1 = organizationService.getTorganizationInCacheByMobile(p2user.getLoginName());
				if(org1!=null){
					if(p2user.getUserType()==21){
						Tbrokerage bk = super.bkDao.get("from Tbrokerage t where t.user = " + p2user.getId() + " and t.status=0");
						if (bk != null) {
						 	Double pb = OrgAmt.multiply(new BigDecimal(Type1RateType2)).doubleValue();
						 	if(pb>=0.01){
						 	 	updateParentBrokerageAccount(user, user.getParentUser(), bonusProcess, bk, 1, Integer.parseInt(memberType2), pb, cftType);
						 	}else{
						 		log.info("上级推荐人ID:" + p2user.getId() + "佣金小于0.01,不参与分佣");
						 	}
						}else{
							log.info("上级推荐人ID:" + p2user.getId() + "没有佣金账户,不参与分佣");
						}
					}
				}else{
					if(org1.getOrgType().equals("5")){
						Tuser p2user1 = userService.findUserByLoginNameT(org1.getUserPhone(), org1.getCode());
						 if(p2user1!=null){
							 Tbrokerage bbk = super.bkDao.get("from Tbrokerage t where t.user = " + p2user1.getId() + " and t.status=0");
							 if(bbk !=null){
								 Double ppb = OrgAmt.multiply(new BigDecimal(Type2RateType2)).doubleValue();
								 if(ppb>=0.01){
									 updateParentBrokerageAccount(user, p2user1, bonusProcess, bbk, 2, Integer.parseInt(memberType2), ppb, cftType);
								 }else{
									 log.info("合伙人推荐人ID:" + p2user1.getId() + "佣金小于0.01,不参与分佣"); 
								 }
								PuserAdd(user, p2user1, 2, bonusProcess, cftType, Double.valueOf(Type2RateType1), bbk);
							 }else{
								 log.info("合伙人推荐人ID:" + p2user1.getId() + "没有佣金账户,不参与分佣");
							 }
							 JianTuiFan2(OrgAmt, user, p2user1, bonusProcess, cftType);
						 }else{
							 log.info("-----合伙人代理商所属分佣账户为空不参与分佣-----");
						 }
					}else if(org1.getOrgType().equals("3")){
						Tuser p2user2 = userService.findUserByLoginNameT(org1.getUserPhone(), org1.getCode());
						if(p2user2!=null){
							 Tbrokerage bbbk = super.bkDao.get("from Tbrokerage t where t.user = " + p2user2.getId() + " and t.status=0");
							 if(bbbk !=null){
								 Double pppb = OrgAmt.multiply(new BigDecimal(Type3RateType2)).doubleValue();
								 if(pppb>=0.01){
									 updateParentBrokerageAccount(user, p2user2, bonusProcess, bbbk, 2, Integer.parseInt(memberType2), pppb, cftType);
								 }else{
									 log.info("运营商推荐人ID:" + p2user2.getId() + "佣金小于0.01,不参与分佣"); 
								 }
							 }else{
								 log.info("运营商推荐人ID:" + p2user2.getId() + "没有佣金账户,不参与分佣");
							 }
							 JianTuiFan2(OrgAmt, user, p2user2, bonusProcess, cftType);
						 }else{
							 log.info("-----运营商代理商所属分佣账户为空不参与分佣-----");
						 }
					}
				}
			}else{
				log.info("------代理无推荐人不参与代理分佣------");
			}
		}if(OrgAmt.subtract(new BigDecimal(memberType3)).compareTo(BigDecimal.ZERO)==0){
			Tuser p3user = user.getParentUser();
			if(p3user!=null){
				Torganization org1 = organizationService.getTorganizationInCacheByMobile(p3user.getLoginName());
				if(org1!=null){
					if(p3user.getUserType()==21){
						Tbrokerage bk = super.bkDao.get("from Tbrokerage t where t.user = " + p3user.getId() + " and t.status=0");
						if (bk != null) {
						 	Double pb = OrgAmt.multiply(new BigDecimal(Type1RateType3)).doubleValue();
						 	if(pb>=0.01){
						 	 	updateParentBrokerageAccount(user, user.getParentUser(), bonusProcess, bk, 1, Integer.parseInt(memberType3), pb, cftType);
						 	}else{
						 		log.info("上级推荐人ID:" + p3user.getId() + "佣金小于0.01,不参与分佣");
						 	}
						}else{
							log.info("上级推荐人ID:" + p3user.getId() + "没有佣金账户,不参与分佣");
						}
					}
				}else{
					if(org1.getOrgType().equals("5")){
						Tuser p3user1 = userService.findUserByLoginNameT(org1.getUserPhone(), org1.getCode());
						 if(p3user1!=null){
							 Tbrokerage bbk = super.bkDao.get("from Tbrokerage t where t.user = " + p3user1.getId() + " and t.status=0");
							 if(bbk !=null){
								 Double ppb = OrgAmt.multiply(new BigDecimal(Type2RateType3)).doubleValue();
								 if(ppb>=0.01){
									 updateParentBrokerageAccount(user, p3user1, bonusProcess, bbk, 2, Integer.parseInt(memberType3), ppb, cftType);
								 }else{
									 log.info("合伙人推荐人ID:" + p3user1.getId() + "佣金小于0.01,不参与分佣"); 
								 }
							 }else{
								 log.info("合伙人推荐人ID:" + p3user1.getId() + "没有佣金账户,不参与分佣");
							 }
						 }else{
							 log.info("-----合伙人代理商所属分佣账户为空不参与分佣-----");
						 }
					}else if(org1.getOrgType().equals("3")){
						Tuser p3user2 = userService.findUserByLoginNameT(org1.getUserPhone(), org1.getCode());
						if(p3user2!=null){
							 Tbrokerage bbbk = super.bkDao.get("from Tbrokerage t where t.user = " + p3user2.getId() + " and t.status=0");
							 if(bbbk !=null){
								 Double pppb = OrgAmt.multiply(new BigDecimal(Type3RateType3)).doubleValue();
								 if(pppb>=0.01){
									 updateParentBrokerageAccount(user, p3user2, bonusProcess, bbbk, 2, Integer.parseInt(memberType3), pppb, cftType);
								 }else{
									 log.info("运营商推荐人ID:" + p3user2.getId() + "佣金小于0.01,不参与分佣"); 
								 }
								 PuserAdd(user, p3user2, 3, bonusProcess, cftType, Double.valueOf(Type2RateType1), bbbk);
							 }else{
								 log.info("运营商推荐人ID:" + p3user2.getId() + "没有佣金账户,不参与分佣");
							 }
							 JianTuiFan3(OrgAmt, user, p3user2, bonusProcess, cftType);
						 }else{
							 log.info("-----运营商代理商所属分佣账户为空不参与分佣-----");
						 }
					}
				}
			}else{
				log.info("------代理无推荐人不参与代理分佣------");
			}
		}
	}
	
	
	
	
	private void updateParentBrokerageAccount(Tuser user, Tuser puser, TorderBonusProcess bonusProcess, Tbrokerage bk, int level, int levelRate, Double b, Integer cftType) {
		try {
			if (bk != null) {
				Integer brokerageType =1;
				TuserOrder order = bonusProcess.getOrder();
				updateUserBrokerage(bk, b, cftType);// 更新客户的分润账户
				String desc = level + "级交易金额提成" + levelRate*100 + "%";
				if (order.getTransPayType() - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0) {
					desc = level + "级推荐升级提成" + levelRate*100 + "%";
					brokerageType =2;
				}
				TbrokerageDetail bd = new TbrokerageDetail(order.getType(), order.getOrgAmt(), order.getCreateTime(), BigDecimal.valueOf(b), level, desc,brokerageType);
				bd.setBrokerageUserRate(Double.valueOf(levelRate));
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
	
	
	
	private void PuserAdd(Tuser user,Tuser p,int sum,TorderBonusProcess bonusProcess,Integer cftType,Double b,Tbrokerage bk){
		Tuser u = userService.getTuser(p.getId());
		if(sum==1){
			u.setRefreSum1(u.getRefreSum1()+1);
			userDao.update(u);
			if(u.getRefreSum1()==5){
				updateParentBrokerage(user, p, bonusProcess, bk, b, cftType);
			}
		}else if(sum==2){
			u.setRefreSum2(u.getRefreSum1()+2);
			userDao.update(u);
			if(u.getRefreSum2()==5){
				updateParentBrokerage(user, p, bonusProcess, bk, b, cftType);
			}
		}else if(sum==3){
			u.setRefreSum3(u.getRefreSum1()+3);
			userDao.update(u);
			if(u.getRefreSum3()==5){
				updateParentBrokerage(user, p, bonusProcess, bk, b, cftType);
			}
		}
	}
	
	
	
	
	
	private void updateParentBrokerage(Tuser user, Tuser puser, TorderBonusProcess bonusProcess, Tbrokerage bk,Double b, Integer cftType) {
		try {
			if (bk != null) {
				Integer brokerageType =5;
				TuserOrder order = bonusProcess.getOrder();
				updateUserBrokerage(bk, b, cftType);// 更新客户的分润账户
				String desc = "推荐五人返分润";
				TbrokerageDetail bd = new TbrokerageDetail(order.getType(), order.getOrgAmt(), order.getCreateTime(), BigDecimal.valueOf(b), 5, desc,brokerageType);
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
	
	
	
	private void JianTuiFan(BigDecimal OrgAmt,Tuser user,Tuser puser,TorderBonusProcess bonusProcess,Integer cftType){
		Tuser ppuser = puser.getParentUser();
		if(ppuser!=null){
			Torganization org2 = organizationService.getTorganizationInCacheByMobile(ppuser.getLoginName());
			if(org2!=null){
				if(org2.getOrgType().equals("5")||org2.getOrgType().equals("3")){
					Tuser user2 = userService.findUserByLoginNameT(org2.getUserPhone(), org2.getCode());
					if(user2!=null){
						 Tbrokerage bbk2 = super.bkDao.get("from Tbrokerage t where t.user = " + user2.getId() + " and t.status=0");
						 if(bbk2!=null){
							 Double ppb2 = null;
							 if(org2.getOrgType().equals("5")){
								 ppb2 = OrgAmt.multiply(new BigDecimal(Type2RateTypeZ1)).doubleValue();
							 }else if(org2.getOrgType().equals("3")){
								 ppb2 = OrgAmt.multiply(new BigDecimal(Type3RateTypeZ1)).doubleValue();
							 }
							 if(ppb2>=0.01){
								 updateParentBrokerageAccount(user, user2, bonusProcess, bbk2, 2, Integer.parseInt(memberType1), ppb2, cftType);
							 }else{
								 log.info("间推荐人ID:" + user2.getId() + "佣金小于0.01,不参与分佣"); 
							 }
						 }else{
							 log.info("间推推荐人ID:" + user2.getId() + "没有佣金账户,不参与分佣");
						 }
					}else{
						log.info("-----间推代理商所属分佣账户为空不参与分佣-----");
					}
				}
			}else{
				log.info("-----间推代理商所属分佣账户为空不参与分佣-----");
			}
		}else{
			log.info("无二级推荐人不参与分佣");
		}
	}
	
	
	
	
	private void JianTuiFan2(BigDecimal OrgAmt,Tuser user,Tuser puser,TorderBonusProcess bonusProcess,Integer cftType){
		Tuser ppuser = puser.getParentUser();
		if(ppuser!=null){
			Torganization org2 = organizationService.getTorganizationInCacheByMobile(ppuser.getLoginName());
			if(org2!=null){
				if(org2.getOrgType().equals("5")||org2.getOrgType().equals("3")){
					Tuser user2 = userService.findUserByLoginNameT(org2.getUserPhone(), org2.getCode());
					if(user2!=null){
						 Tbrokerage bbk2 = super.bkDao.get("from Tbrokerage t where t.user = " + user2.getId() + " and t.status=0");
						 if(bbk2!=null){
							 Double ppb2 = null;
							 if(org2.getOrgType().equals("5")){
								 ppb2 = OrgAmt.multiply(new BigDecimal(Type2RateTypeZ2)).doubleValue();
							 }else if(org2.getOrgType().equals("3")){
								 ppb2 = OrgAmt.multiply(new BigDecimal(Type3RateTypeZ2)).doubleValue();
							 }
							 if(ppb2>=0.01){
								 updateParentBrokerageAccount(user, user2, bonusProcess, bbk2, 2, Integer.parseInt(memberType1), ppb2, cftType);
							 }else{
								 log.info("间推荐人ID:" + user2.getId() + "佣金小于0.01,不参与分佣"); 
							 }
						 }else{
							 log.info("间推推荐人ID:" + user2.getId() + "没有佣金账户,不参与分佣");
						 }
					}else{
						log.info("-----间推代理商所属分佣账户为空不参与分佣-----");
					}
				}
			}else{
				log.info("-----间推代理商所属分佣账户为空不参与分佣-----");
			}
		}else{
			log.info("无二级推荐人不参与分佣");
		}
	}
	
	
	
	private void JianTuiFan3(BigDecimal OrgAmt,Tuser user,Tuser puser,TorderBonusProcess bonusProcess,Integer cftType){
		Tuser ppuser = puser.getParentUser();
		if(ppuser!=null){
			Torganization org2 = organizationService.getTorganizationInCacheByMobile(ppuser.getLoginName());
			if(org2!=null){
				if(org2.getOrgType().equals("5")||org2.getOrgType().equals("3")){
					Tuser user2 = userService.findUserByLoginNameT(org2.getUserPhone(), org2.getCode());
					if(user2!=null){
						 Tbrokerage bbk2 = super.bkDao.get("from Tbrokerage t where t.user = " + user2.getId() + " and t.status=0");
						 if(bbk2!=null){
							 Double ppb2 = null;
							 if(org2.getOrgType().equals("3")){
								 ppb2 = OrgAmt.multiply(new BigDecimal(Type3RateTypeZ3)).doubleValue();
							 }
							 if(ppb2>=0.01){
								 updateParentBrokerageAccount(user, user2, bonusProcess, bbk2, 2, Integer.parseInt(memberType1), ppb2, cftType);
							 }else{
								 log.info("间推荐人ID:" + user2.getId() + "佣金小于0.01,不参与分佣"); 
							 }
						 }else{
							 log.info("间推推荐人ID:" + user2.getId() + "没有佣金账户,不参与分佣");
						 }
					}else{
						log.info("-----间推代理商所属分佣账户为空不参与分佣-----");
					}
				}
			}else{
				log.info("-----间推代理商所属分佣账户为空不参与分佣-----");
			}
		}else{
			log.info("无二级推荐人不参与分佣");
		}
	}
	
	
	
	
	
}
