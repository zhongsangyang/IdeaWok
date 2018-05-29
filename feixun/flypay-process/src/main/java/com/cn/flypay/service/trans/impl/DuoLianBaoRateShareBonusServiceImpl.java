package com.cn.flypay.service.trans.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserSettlementConfig;
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
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.utils.jpush.api.JiguangUtil;

@Service(value = "duoLianBaoRateShareBonusService")
public class DuoLianBaoRateShareBonusServiceImpl extends AbstractShareBonusService {

	private static Logger LOG = LoggerFactory.getLogger(BaoBeRateShareBonusServiceImpl.class);
	/**
	 * 980
	 */
	@Value("${duoLianBaoPartnerSum}")
	private String partnerSum;
	/**
	 * 98
	 */
	@Value("${duoLianBaoPartner1}")
	private String partner1;
	/**
	 * 490
	 */
	@Value("${duoLianBaoPartner2}")
	private String partner2;
	/**
	 * 490
	 */
	@Value("${duoLianBaoPartner3}")
	private String partner3;
	
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

	@Autowired
	private BaseDao<TuserSettlementConfig> userSettlementConfigDao;

	@Autowired
	private UserSettlementConfigService userSettlementConfigService;
	
	@Autowired
	private BaseDao<TbrokerageLog> brokerageLogDao;
	
	public void updateABUserWhenShare(Tuser user, BigDecimal shareBKbd, TorderBonusProcess bonusProcess, Integer cftType) {
		LOG.info("----------------多联宝代理分佣开始----------------------");
		TuserOrder userOrder = bonusProcess.getOrder();
		BigDecimal OrgAmt = userOrder.getOrgAmt();
		if (OrgAmt.subtract(new BigDecimal(partnerSum)).compareTo(BigDecimal.ZERO) == 0) {// 升级代理的分润
			Tuser p1user = user.getParentUser();
			if (p1user != null) {
				Torganization org1 = organizationService.getTorganizationInCacheByMobile(p1user.getLoginName());
				if (org1 == null) {// 直推用户非代理商
					if (p1user.getUserType() == 23) {
						Tbrokerage brokage = super.bkDao.get("from Tbrokerage t where t.user = " + p1user.getId() + " and t.status=0");
						if (brokage != null) {
							Double brokageAmt = new BigDecimal(partner1).doubleValue();
							if (brokageAmt >= 0.01) {
								updateParentBrokerageAccount(user, user.getParentUser(), bonusProcess, brokage, 1, Integer.parseInt(partnerSum), brokageAmt, cftType);
							} else {
								LOG.info("上级推荐人ID:" + p1user.getId() + "佣金小于0.01,不参与分佣");
							}
						} else {
							LOG.info("上级推荐人ID:" + p1user.getId() + "没有佣金账户,不参与分佣");
						}
					} else {
						LOG.info("------代理推荐人没有资格参与代理分佣------");
						// update：2017.11.22 发送极光推送,通知未升级用户无法获取佣金
						sendJiGuangUnAgentUserAlert(user.getRealName(), user.getParentUser());
					}

					Torganization pUOrg = organizationService.getTorganizationInCacheById(p1user.getOrganization().getId());// 直推用户的代理商
					if (pUOrg != null) {
						Tuser pOrgUuser = userService.findUserByLoginNameT(pUOrg.getUserPhone(), user.getAgentId());
						if (pOrgUuser != null) {
							Tbrokerage pOrgUserBrokage = super.bkDao.get("from Tbrokerage t where t.user = " + pOrgUuser.getId() + " and t.status=0");
							if (pOrgUserBrokage != null) {
								Double brokageAmt = new BigDecimal(partner1).doubleValue();
								if (brokageAmt >= 0.01) {
									updateParentBrokerageAccount(user, pOrgUuser, bonusProcess, pOrgUserBrokage, 2, Integer.parseInt(partnerSum), brokageAmt, cftType);
								} else {
									LOG.info("合伙人推荐人ID:" + pOrgUuser.getId() + "佣金小于0.01,不参与分佣");
								}
							} else {
								LOG.info("-----间推代理商所属分佣账户为空不参与分佣-----");
							}
						} else {
							LOG.info("-----代理商所属用户为空不参与分佣-----");
						}
					} else {
						LOG.info("-----代理商所属分佣账户为空不参与分佣-----");
					}
				} else {// 直推用户是代理级别
					if (org1.getOrgType().equals("5")) {// 直推用户代理商
						Tuser p1User = userService.findUserByLoginNameT(org1.getUserPhone(), user.getAgentId());
						if (p1User != null) {// 直推用户
							Tbrokerage pOrgBkg = super.bkDao.get("from Tbrokerage t where t.user = " + p1User.getId() + " and t.status=0");
							if (pOrgBkg != null) {
								Double bkgAmt = new BigDecimal(partner2).doubleValue();
								if (bkgAmt >= 0.01) {
									updateParentBrokerageAccount(user, p1User, bonusProcess, pOrgBkg, 2, Integer.parseInt(partnerSum), bkgAmt, cftType);
								} else {
									LOG.info("合伙人推荐人ID:" + p1User.getId() + "佣金小于0.01,不参与分佣");
								}
							} else {
								LOG.info("合伙人推荐人ID:" + p1User.getId() + "没有佣金账户,不参与分佣");
							}
							// PuserAdd(user, p1User, 2, bonusProcess, cftType,
							// Double.valueOf(partnerSum), pOrgBkg);
							// TODO 推荐25人升运营中心 每次推荐一个代理商，代理商数量参数+1
							updateUserToSuperAdmin(user, p1User, 2, bonusProcess, cftType, Double.valueOf(partnerSum), pOrgBkg);
							Tuser p2User = p1User.getParentUser();
							if (p2User != null) {// 二级直推用户
								Torganization p2Org = organizationService.getTorganizationInCacheByMobile(p2User.getLoginName());
								if (p2Org != null) {
									if (p2Org.getOrgType().equals("5") || p2Org.getOrgType().equals("4")) {
										Tbrokerage p2Bkg = super.bkDao.get("from Tbrokerage t where t.user = " + p2User.getId() + " and t.status=0");
										if (p2Bkg != null) {
											Double bkgAmt = new BigDecimal(partner1).doubleValue();
											if (bkgAmt >= 0.01) {
												updateParentBrokerageAccount(user, p2User, bonusProcess, p2Bkg, 2, Integer.parseInt(partnerSum), bkgAmt, cftType);
											} else {
												LOG.info("合伙人推荐人ID:" + p1User.getId() + "佣金小于0.01,不参与分佣");
											}
										} else {
											LOG.info("-----间推代理商所属分佣账户为空不参与分佣-----");
										}
									} else {
										LOG.info("-----间推代理商所属为空不参与分佣-----");
									}
								} else {
									LOG.info("-----代理商所属分佣账户为空不参与分佣-----");
								}
							} else {
								LOG.info("-----间推代理商所属分佣账户为空不参与分佣-----");
							}
						} else {
							LOG.info("-----合伙人代理商所属分佣账户为空不参与分佣-----");
						}
					} else if (org1.getOrgType().equals("4")) {// 直推用户运营中心
						Tuser p1User = userService.findUserByLoginNameT(org1.getUserPhone(), user.getAgentId());
						if (p1User != null) {
							Tbrokerage p1UserBkg = super.bkDao.get("from Tbrokerage t where t.user = " + p1User.getId() + " and t.status=0");
							if (p1UserBkg != null) {
								Double bkgAmt = new BigDecimal(partner3).doubleValue();
								if (bkgAmt >= 0.01) {
									updateParentBrokerageAccount(user, p1User, bonusProcess, p1UserBkg, 2, Integer.parseInt(partnerSum), bkgAmt, cftType);
								} else {
									LOG.info("运营商推荐人ID:" + p1User.getId() + "佣金小于0.01,不参与分佣");
								}
							} else {
								LOG.info("运营商推荐人ID:" + p1User.getId() + "没有佣金账户,不参与分佣");
							}

							Tuser p2User = p1User.getParentUser();
							if (p2User != null) {
								Torganization org13 = organizationService.getTorganizationInCacheByMobile(p2User.getLoginName());
								if (org13 != null) {
									if (org13.getOrgType().equals("5") || org13.getOrgType().equals("4")) {
										Tbrokerage bbbbk = super.bkDao.get("from Tbrokerage t where t.user = " + p2User.getId() + " and t.status=0");
										if (bbbbk != null) {
											Double ppb = new BigDecimal(partner1).doubleValue();
											if (ppb >= 0.01) {
												updateParentBrokerageAccount(user, p2User, bonusProcess, bbbbk, 2, Integer.parseInt(partnerSum), ppb, cftType);
											} else {
												LOG.info("合伙人推荐人ID:" + p2User.getId() + "佣金小于0.01,不参与分佣");
											}
										} else {
											LOG.info("-----间推代理商所属分佣账户为空不参与分佣-----");
										}
									} else {
										LOG.info("-----间推代理商所属为空不参与分佣-----");
									}
								} else {
									LOG.info("-----代理商所属分佣账户为空不参与分佣-----");
								}
							} else {
								LOG.info("-----间推代理商所属分佣账户为空不参与分佣-----");
							}
						} else {
							LOG.info("-----运营商代理商所属分佣账户为空不参与分佣-----");
						}
					} else {
						LOG.info("------无资格参与分佣------");
					}
				}
			} else {
				LOG.info("------代理无推荐人不参与代理分佣------");
			}
		}
		LOG.info("----------------多联宝代理分佣结束----------------------");

		LOG.info("----------------多联宝代理费率变更开始----------------------");
		TuserSettlementConfig sc = userSettlementConfigService.getTuserSettlementConfigByUserId(user.getId());
		Torganization oemOrg = organizationService.getTorganizationInCacheByCode(user.getAgentId());
		Torganization sOrg = organizationService.getTorganizationInCacheByMobile(user.getLoginName());
		int levle = 24;
		if (sOrg == null) {
			levle = user.getUserType();
		} else if ("5".equals(sOrg.getOrgType())) {
			levle = 22;
		} else if ("4".equals(sOrg.getOrgType())) {
			levle = 21;
		}
		if (oemOrg != null) {
			userSettlementConfigService.setSettlementConfigWhenUserUpdate(sc, levle, oemOrg.getId());
			userSettlementConfigDao.update(sc);
		}
		LOG.info("----------------多联宝代理费率变更结束----------------------");
	}

	/**
	 * 通知未升级的用户无法获取下级用户升级代理的分拥 update：2017.11.22
	 * 
	 * @param loginName
	 * @param parentUser
	 */
	private void sendJiGuangUnAgentUserAlert(String loginName, Tuser parentUser) {
		try {
			String desc = String.format(JiguangUtil.ALTER_UNAGENT_USER_BROKERAGER, ReaNameSub(loginName));
			InfoList t = new InfoList(parentUser.getId(), JiguangUtil.ALTER_UNAGENT_USER_BROKERAGER_TITLE, InfoList.info_Type.person.getCode(), desc, 0, 0);
			producerService.sendInfoList(t);

		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	// 隐藏用户名
	private String ReaNameSub(String name) {
		if (name.length() == 2) {
			name = name.substring(0, 1) + "*";
		} else if (name.length() == 3) {
			name = name.substring(0, 1) + "*" + name.substring(name.length() - 1, name.length());
		} else if (name.length() > 3) {
			name = name.substring(0, 1) + "**" + name.substring(name.length() - 1, name.length());
		}
		return name;
	}

	private void updateParentBrokerageAccount(Tuser user, Tuser puser, TorderBonusProcess bonusProcess, Tbrokerage bk, int level, int levelRate, Double b, Integer cftType) {
		try {
			if (bk != null) {
				Integer brokerageType = 1;
				TuserOrder order = bonusProcess.getOrder();
				updateUserBrokerage(bk, b, cftType);// 更新客户的分润账户
				String desc = level + "级交易金额提成" + levelRate * 100 + "%";
				if (order.getTransPayType() - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0) {
					desc = level + "级推荐升级提成" + levelRate * 100 + "%";
					brokerageType = 2;
				}
				TbrokerageDetail bd = new TbrokerageDetail(order.getType(), order.getOrgAmt(), order.getCreateTime(), BigDecimal.valueOf(b), level, desc, brokerageType);
				bd.setBrokerageUserRate(Double.valueOf(levelRate));
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
				LOG.info(puser.getLoginName() + "佣金账号已经被冻结，跳过本人的分润");
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
	}

	/**
	 * 直推5个代理商返666
	 * 
	 * @param user
	 * @param p
	 * @param sum
	 * @param bonusProcess
	 * @param cftType
	 * @param b
	 * @param bk
	 */
	private void PuserAdd(Tuser user, Tuser p, int sum, TorderBonusProcess bonusProcess, Integer cftType, Double b, Tbrokerage bk) {
		Tuser u = userService.getTuser(p.getId());
		u.setRefreSum3(u.getRefreSum3() + 1);
		userDao.update(u);
		if (u.getRefreSum3() == 5) {
			updateParentBrokerage(user, p, bonusProcess, bk, b, cftType);
		}
	}

	/**
	 * TODO 升级代理商到运营中心
	 * 
	 * @param user
	 * @param puser
	 * @param sum
	 * @param bonusProcess
	 * @param cftType
	 * @param b
	 * @param bk
	 */
	private void updateUserToSuperAdmin(Tuser user, Tuser puser, int sum, TorderBonusProcess bonusProcess, Integer cftType, Double b, Tbrokerage bk) {
		Tuser u = userService.getTuser(puser.getId());
		u.setRefreSum3(u.getRefreSum3() + 1);
		userDao.update(u);
		Torganization organization = u.getOrganization();
		if (u.getRefreSum3() >= 25 && !"4".equals(organization.getOrgType())) {// 推荐25人升运营中心
			organization.setOrgType("4");
		}
	}

	private void updateParentBrokerage(Tuser user, Tuser puser, TorderBonusProcess bonusProcess, Tbrokerage bk, Double b, Integer cftType) {
		try {
			if (bk != null) {
				Integer brokerageType = 5;
				TuserOrder order = bonusProcess.getOrder();
				updateUserBrokerage(bk, b, cftType);// 更新客户的分润账户
				String desc = "推荐五人返分润";
				TbrokerageDetail bd = new TbrokerageDetail(order.getType(), order.getOrgAmt(), order.getCreateTime(), BigDecimal.valueOf(b), 5, desc, brokerageType);
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
				LOG.info(puser.getLoginName() + "佣金账号已经被冻结，跳过本人的分润");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
