package com.cn.flypay.service.trans.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.account.TaccountLog;
import com.cn.flypay.model.sys.TOrganizationProtectRate;
import com.cn.flypay.model.sys.TcoreOperate;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.TsysOrgRate;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.model.trans.TorderBonusProcess;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.account.Account;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.sys.OrgChannelUserRateConfigService;
import com.cn.flypay.service.sys.OrganizationProtectRateService;
import com.cn.flypay.service.sys.SysOrgRateService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.SysConvert;

@Service(value = "xiaShangYunLianService")
public class XiaShangYunLianServiceImpl extends AbstractShareBonusService {
	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private OrgChannelUserRateConfigService orgChannelUserRateConfigService;

	@Autowired
	private UserService userservice;

	@Autowired
	private AccountService accountService;

	@Autowired
	private BaseDao<TcoreOperate> coreOperateDao;

	@Autowired
	private SysOrgRateService sysOrgRateService;

	@Autowired
	private OrganizationProtectRateService organizationProtectRateService;

	@Autowired
	private BaseDao<Taccount> accountDao;

	@Autowired
	private BaseDao<TaccountLog> accountLogDao;

	private static Long UserIdZ = 96390L;

	@Override
	public void updateABUserWhenShare(Tuser user, BigDecimal shareBKbd, TorderBonusProcess bonusProcess,
			Integer cftType) {
		if (cftType - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0) {
			log.error("订单为代理订单此方法不参与分佣");
		} else {
			Torganization org = organizationService.getTorganizationInCacheByCode(user.getAgentId());
			TuserOrder order = bonusProcess.getOrder();
			BigDecimal cbbc = getOrgRateTwo(org.getId(), org.getOrgType(), order.getType());
			BigDecimal fy = order.getOrgAmt().multiply(order.getPersonRate().subtract(cbbc));
			BigDecimal lrate = order.getPersonRate();
			if (user.getUserType() <= 21) {
				log.info("用户只能分拥给代理等级用户");
			} else {
				if (user.getParentUser() != null) {
					Tuser puser = user.getParentUser();
					Torganization orgin1 = organizationService.getTorganizationInCodeTwo(puser.getLoginName());
					if (orgin1 == null) {
						int level = 1;
						Long pid = user.getParentUser().getId();
						if (puser.getUserType() >= user.getUserType()) {
							log.info("一级推荐人级别低于刷卡人，无利润分配");
						} else {
							if (fy.compareTo(BigDecimal.ZERO) == 1) {
								log.info("一级推荐人ID:" + pid);
								Double b = SysConvert.convertDoubleDigit(getPtradeBonus(order.getOrgAmt(), order,
										user.getUserType(), puser.getUserType(), org));
								if (b >= 0.01) {
									log.info(" 一级推荐人ID:" + pid + ",分润金额: " + b + "元,分入金额佣金账户");
									Tbrokerage bk = super.bkDao
											.get("select t from Tbrokerage t  left join t.user u where u.id = " + pid
													+ " and t.status=0");
									if (bk != null) {
										updateParentBrokerageAccount(user, user.getParentUser(), bonusProcess, bk,
												level, b, cftType);
										fy = fy.subtract(BigDecimal.valueOf(b));
										addCoreOper(fy);
										lrate = getPtradeRateC(shareBKbd, order, puser.getUserType(),
												puser.getUserType(), org);
									} else {
										log.info("一级推荐人ID:" + pid + "没有佣金账户,不参与分佣");
									}
									getLrate(puser, b, cftType, bonusProcess);
								} else {
									log.info(" 一级推荐人ID:" + pid + ",分润金额: " + b + "小于0.01,不参与分佣!");
								}
							} else {
								log.info("-----分佣总金额已经被分完,一级推荐人无佣金-----");
							}
						}

						Tuser ppuser = puser.getParentUser();
						if (ppuser != null) {
							Torganization orgin2 = organizationService.getTorganizationInCodeTwo(ppuser.getLoginName());
							if (orgin2 == null) {
								Long ppid = ppuser.getId();
								level = 2;
								if (ppuser.getUserType() >= user.getUserType()
										|| ppuser.getUserType() >= puser.getUserType()) {
									log.info("二级推荐人级别低于刷卡人或一级推荐人，无利润分配");
								} else {
									if (fy.compareTo(BigDecimal.ZERO) == 1) {
										int minType = puser.getUserType() < user.getUserType() ? puser.getUserType()
												: user.getUserType();
										Double pb = SysConvert.convertDoubleDigit(getPtradeBonus(order.getOrgAmt(),
												order, minType, ppuser.getUserType(), org));
										if (pb >= 0.01) {
											log.info(" 二级级推荐人ID:" + ppid + ",分润金额: " + pb + "元,分入金额佣金账户");
											Tbrokerage pbk = super.bkDao.get(
													"from Tbrokerage t where t.user = " + ppid + " and t.status=0");
											if (pbk != null) {
												updateParentBrokerageAccount(user, ppuser, bonusProcess, pbk, level, pb,
														cftType);
												fy = fy.subtract(BigDecimal.valueOf(pb));
												addCoreOper(fy);
												lrate = getPtradeRateC(shareBKbd, order, ppuser.getUserType(),
														ppuser.getUserType(), org);
											} else {
												log.info("二级推荐人ID:" + ppid + "没有佣金账户,不参与分佣");
											}
											getLrate(ppuser, pb, cftType, bonusProcess);
										} else {
											log.info("二级推荐人ID:" + ppid + ",分润金额: " + pb + "小于0.01,不参与分佣!");
										}

									} else {
										log.info("-----分佣总金额已经被分完,二级推荐人无佣金-----");
									}
								}

								if (org.getShareBonusLevelType() == 0) {
									Tuser pppuser = ppuser.getParentUser();
									if (pppuser != null) {
										Torganization orgin3 = organizationService
												.getTorganizationInCodeTwo(pppuser.getLoginName());
										if (orgin3 == null) {
											Long pppid = pppuser.getId();
											level = 3;
											if (pppuser.getUserType() >= user.getUserType()
													|| pppuser.getUserType() >= puser.getUserType()
													|| pppuser.getUserType() >= ppuser.getUserType()) {
												log.info("三级推荐人级别低于刷卡人或一二级推荐人，无利润分配");
											} else {
												if (fy.compareTo(BigDecimal.ZERO) == 1) {
													int minType = puser.getUserType() < user.getUserType()
															? puser.getUserType() : user.getUserType();
													minType = ppuser.getUserType() < minType ? ppuser.getUserType()
															: minType;
													Double ppb = SysConvert
															.convertDoubleDigit(getPtradeBonus(order.getOrgAmt(), order,
																	minType, pppuser.getUserType(), org));
													if (ppb >= 0.01) {
														log.info(" 三级级推荐人ID:" + pppid + ",分润金额: " + ppb + "元,分入金额佣金账户");
														Tbrokerage ppbk = super.bkDao
																.get("from Tbrokerage t where t.user = " + pppid
																		+ " and t.status=0");
														if (ppbk != null) {
															updateParentBrokerageAccount(user, pppuser, bonusProcess,
																	ppbk, level, ppb, cftType);
															fy = fy.subtract(BigDecimal.valueOf(ppb));
															addCoreOper(fy);
															lrate = getPtradeRateC(shareBKbd, order,
																	pppuser.getUserType(), pppuser.getUserType(), org);
														} else {
															log.info("三级推荐人ID:" + pppid + "没有佣金账户,不参与分佣");
														}
														getLrate(pppuser, ppb, cftType, bonusProcess);
													} else {
														log.info("三级级推荐人ID:" + pppid + ",分润金额: " + ppb
																+ "小于0.01,不参与分佣!");
													}
												} else {
													log.info("-----分佣总金额已经被分完,三级推荐人无佣金-----");
												}
											}
										} else {
											log.info("------三级返佣金人为代理商不参与返佣------");
										}
									}
								}
							} else {
								log.info("------二级返佣金人为代理商不参与返佣------");
							}
						}
					} else {
						log.info("------一级返佣金人为代理商不参与返佣------");
					}
				}
			}

			if (fy.compareTo(BigDecimal.ZERO) == 1) {
				Torganization org1 = organizationService.getTorganizationInCacheById(user.getOrganization().getId());
				if (org1 != null) {
					Tuser user1 = userservice.findUserByLoginNameT(org1.getUserPhone(), org.getCode());
					if (user1 != null) {
						BigDecimal cb = getOrgRateTwo(org.getId(), org1.getOrgType(), order.getType());
						Double org1b = SysConvert
								.convertDoubleDigit(order.getOrgAmt().multiply(lrate.subtract(cb)).doubleValue());
						if (!StringUtil.equals(user.getLoginName(), org1.getUserPhone())) {
							if (org1b >= 0.01) {
								log.info("一级代理商ID:" + org1.getId() + ",分润金额: " + org1b + "元,分入金额佣金账户");
								Tbrokerage org1bk = super.bkDao
										.get("from Tbrokerage t where t.user = " + user1.getId() + " and t.status=0");
								if (org1bk != null) {
									updateParentBrokerageAccountLT(user, user1, bonusProcess, org1bk, 1, org1b,
											cftType);
									fy = fy.subtract(BigDecimal.valueOf(org1b));
									addCoreOper(fy);
									lrate = cb;
								} else {
									log.info("一级代理商ID:" + org1.getId() + "没有佣金账户,不参与分佣");
								}
								getOrgRateLD(org1, org, org1b, cftType, bonusProcess);
							} else {
								log.info("一级代理商ID:" + org1.getId() + ",分润金额: " + org1b + "小于0.01,不参与分佣!");
							}
						} else {
							log.info("一级代理商是本人不参与分拥！");
						}

						Torganization org2 = org1.getOrganization();
						if (org2 != null) {
							Tuser user2 = userservice.findUserByLoginNameT(org2.getUserPhone(), org.getCode());
							if (user2 != null) {
								if (fy.compareTo(BigDecimal.ZERO) == 1) {
									BigDecimal cb2 = getOrgRateTwo(org.getId(), org2.getOrgType(), order.getType());
									Double org2b = SysConvert.convertDoubleDigit(
											order.getOrgAmt().multiply(lrate.subtract(cb2)).doubleValue());
									if (org2b >= 0.01) {
										log.info("二级代理商ID:" + org2.getId() + ",分润金额: " + org2b + "元,分入金额佣金账户");
										Tbrokerage org2bk = super.bkDao.get("from Tbrokerage t where t.user = "
												+ user2.getId() + " and t.status=0");
										if (org2bk != null) {
											updateParentBrokerageAccountLT(user, user2, bonusProcess, org2bk, 2, org2b,
													cftType);
											fy = fy.subtract(BigDecimal.valueOf(org2b));
											addCoreOper(fy);
											lrate = cb2;
										} else {
											log.info("二级代理商ID:" + org2.getId() + "没有佣金账户,不参与分佣");
										}
									} else {
										log.info("二级代理商ID:" + org2.getId() + ",分润金额: " + org2b + "小于0.01,不参与分佣!");
									}
								} else {
									log.info("-----分佣总金额和费率差已经被分完,二级代理老板和运营商无分润-----");
								}

								Torganization org3 = org2.getOrganization();
								if (org3 != null) {
									Tuser user3 = userservice.findUserByLoginNameT(org3.getUserPhone(), org.getCode());
									if (user3 != null) {
										if (fy.compareTo(BigDecimal.ZERO) == 1) {
											BigDecimal cb3 = getOrgRateTwo(org.getId(), org3.getOrgType(),
													order.getType());
											Double org3b = SysConvert.convertDoubleDigit(
													order.getOrgAmt().multiply(lrate.subtract(cb3)).doubleValue());
											if (org3b >= 0.01) {
												log.info("三级代理商ID:" + org3.getId() + ",分润金额: " + org3b + "元,分入金额佣金账户");
												Tbrokerage org3bk = super.bkDao.get("from Tbrokerage t where t.user = "
														+ user3.getId() + " and t.status=0");
												if (org3bk != null) {
													updateParentBrokerageAccountLT(user, user3, bonusProcess, org3bk, 3,
															org3b, cftType);
													fy = fy.subtract(BigDecimal.valueOf(org3b));
													addCoreOper(fy);
													lrate = cb3;
												} else {
													log.info("三级代理商ID:" + org3.getId() + "没有佣金账户,不参与分佣");
												}
											} else {
												log.info("三级代理商ID:" + org3.getId() + ",分润金额: " + org3b
														+ "小于0.01,不参与分佣!");
											}
										} else {
											log.info("-----分佣总金额和费率差已经被分完,三级代理老板和运营商无分润-----");
										}

										Torganization org4 = org3.getOrganization();
										if (org4 != null) {
											Tuser user4 = userservice.findUserByLoginNameT(org4.getUserPhone(),
													org.getCode());
											if (user4 != null) {
												if (fy.compareTo(BigDecimal.ZERO) == 1) {
													BigDecimal cb4 = getOrgRateTwo(org.getId(), org4.getOrgType(),
															order.getType());
													Double org4b = SysConvert.convertDoubleDigit(order.getOrgAmt()
															.multiply(lrate.subtract(cb4)).doubleValue());
													if (org4b >= 0.01) {
														log.info("四级代理商ID:" + org4.getId() + ",分润金额: " + org4b
																+ "元,分入金额佣金账户");
														Tbrokerage org4bk = super.bkDao
																.get("from Tbrokerage t where t.user = " + user4.getId()
																		+ " and t.status=0");
														if (org4bk != null) {
															updateParentBrokerageAccountLT(user, user4, bonusProcess,
																	org4bk, 3, org4b, cftType);
															fy = fy.subtract(BigDecimal.valueOf(org4b));
															addCoreOper(fy);
															lrate = cb4;
														} else {
															log.info("四级代理商ID:" + org4.getId() + "没有佣金账户,不参与分佣");
														}
													} else {
														log.info("四级代理商ID:" + org4.getId() + ",分润金额: " + org4b
																+ "小于0.01,不参与分佣!");
													}
												} else {
													log.info("-----分佣总金额和费率差已经被分完,四级代理老板和运营商无分润-----");
												}
											} else {
												log.info("-----四级代理商所属分佣账户为空不参与分佣-----");
											}
										} else {
											log.info("-----四级代理商为空不参与分佣-----");
										}
									} else {
										log.info("-----三级代理商所属分佣账户为空不参与分佣-----");
									}
								} else {
									log.info("-----三级代理商为空不参与分佣-----");
								}
							} else {
								log.info("-----二级代理商所属分佣账户为空不参与分佣-----");
							}
						} else {
							log.info("-----二级代理商为空不参与分佣-----");
						}
					} else {
						log.info("-----一级代理商所属分佣账户为空不参与分佣-----");
					}
				} else {
					log.info("-----一级代理商为空不参与分佣-----");
				}
			} else {
				log.info("-----分佣总金额和费率差已经被分完,代理老板和运营商无分润-----");
			}
		}
	}

	private Double getPtradeBonus(BigDecimal shareBKbd, TuserOrder order, int sourceUserType, int targetUserType,
			Torganization org) {
		BigDecimal rate = BigDecimal.ZERO;
		MathContext mc = new MathContext(4, RoundingMode.FLOOR);
		if(order.getType() == 550){//银联积分直接查询550即可
			rate = orgChannelUserRateConfigService.getUserShareRateInCache(order.getType(),sourceUserType, targetUserType, order.getInputAccType(), org.getId());
		}else{
			rate = orgChannelUserRateConfigService.getUserShareRateInCache(UserOrder.getUserPayChannelType(order.getType()),sourceUserType, targetUserType, order.getInputAccType(), org.getId());
		}
		return shareBKbd.multiply(rate).doubleValue();
	}

	private BigDecimal getPtradeRateC(BigDecimal shareBKbd, TuserOrder order, int sourceUserType, int targetUserType,
			Torganization org) {
		BigDecimal rate = BigDecimal.ZERO;
		MathContext mc = new MathContext(4, RoundingMode.FLOOR);
		if(order.getType() == 550){//银联积分直接查询550即可
			rate = orgChannelUserRateConfigService.getUserShareRateInCacheC(order.getType(), sourceUserType, targetUserType, order.getInputAccType(), org.getId());
		}else{
			rate = orgChannelUserRateConfigService.getUserShareRateInCacheC(UserOrder.getUserPayChannelType(order.getType()), sourceUserType, targetUserType, order.getInputAccType(), org.getId());
		}
		return rate;
	}

	private void addCoreOper(BigDecimal fy) {
		if (fy.compareTo(BigDecimal.ZERO) == -1) {
			TcoreOperate t = new TcoreOperate();
			t.setDateTime(DateUtil.getyyyyMMddToString());
			t.setT1PayFlag(0);
			t.setT1ToAvlFlag(1);
			t.setStatus(1);
			t.setRemark("返佣完成超过预期佣金请注意!");
			t.setCreator("SYSTEM");
			coreOperateDao.save(t);
		}
	}

	private BigDecimal getOrgRate(String agentId, String OrgType, BigDecimal lrate) {
		TsysOrgRate t = sysOrgRateService.getSysOrgRate(agentId, OrgType);
		if (t == null) {
			return lrate;
		} else {
			return t.getOrgRate();
		}
	}

	private BigDecimal getOrgRateTwo(Long orgId, String type, Integer orderType) {
		TOrganizationProtectRate org = organizationProtectRateService.getTOrganizationProtectRate(orgId, type);
		if (orderType == 300 || orderType == 320) {
			return org.getWeixinProtectRate();
		} else if (orderType == 200 || orderType == 220) {
			return org.getZhifubaoProtectRate();
		} else if (orderType == 1300) {
			return org.getQqProtectRate();
		} else if (orderType == 1000) {
			return org.getBaiduProtectRate();
		} else if (orderType == 900) {
			return org.getJingdongProtectRate();
		} else if (orderType == 500) {
			return org.getYinlianzaixianProtectRate();
		} else if (orderType == 530) {
			return org.getBigYinlianProtectRate();
		} else if (orderType == 550) {
			return org.getYinlianjifenProtectRate();
		}
		return new BigDecimal(1);
	}

	private void getLrate(Tuser puser, Double b, Integer cftType, TorderBonusProcess bonusProcess) {
		if (puser.getUserType() == 21) {
			Tuser Lpuser = puser.getParentUser();
			if (Lpuser != null) {
				if (Lpuser.getUserType() == 21) {
					Double lb = b * 0.05;
					if (lb > 0.01) {
						Taccount account = accountService.getAccountByUserIdTwo(UserIdZ);
						if (account.getAvlAmt().subtract(new BigDecimal(lb)).compareTo(BigDecimal.ZERO) == -1) {
							log.info("领导奖获取人ID:" + Lpuser.getId() + "佣金账户余额不足请充值!");
							sedInfoTT(UserIdZ);
						} else {
							Tbrokerage bl = super.bkDao
									.get("select t from Tbrokerage t  left join t.user u where u.id = " + Lpuser.getId()
											+ " and t.status=0");
							if (bl != null) {
								updateParentBrokerageAccountLD(puser, Lpuser, bonusProcess, bl, 1, lb, cftType);
								account.setAvlAmt(account.getAvlAmt().subtract(new BigDecimal(lb)));
								accountDao.update(account);
							} else {
								log.info("领导奖获取人ID:" + Lpuser.getId() + "没有佣金账户,不参与分佣");
							}
						}
					} else {
						log.info("领导奖获取人ID:" + Lpuser.getId() + ",分润金额: " + lb + "小于0.01,不参与领导奖!");
					}
				}
			} else {
				log.info("领导奖获推荐人ID为空不参与分领导奖");
			}
		}
	}

	private void getOrgRateLD(Torganization org1, Torganization org, Double b, Integer cftType,
			TorderBonusProcess bonusProcess) {
		if (org1.getOrgType().equals("5")) {
			Tuser luser1 = userservice.findUserByLoginNameT(org1.getUserPhone(), org.getCode());
			if (luser1 != null) {
				Tuser luser2 = luser1.getParentUser();
				if (luser2 != null) {
					Torganization orgLD = organizationService
							.getTorganizationInCacheById(luser2.getOrganization().getId());
					if (orgLD.getOrgType().equals("5")) {
						Double lb = b * 0.1;
						DecimalFormat df = new DecimalFormat("#.00");
						lb = Double.valueOf(df.format(lb));
						if (lb > 0.01) {
							Taccount account = accountService.getAccountByUserIdTwo(UserIdZ);
							if (account.getAvlAmt().subtract(new BigDecimal(lb)).compareTo(BigDecimal.ZERO) == -1) {
								log.info("领导奖获取人ID:" + luser2.getId() + "佣金账户余额不足请充值!");
								sedInfoTT(UserIdZ);
							} else {
								Tbrokerage bl = super.bkDao
										.get("select t from Tbrokerage t  left join t.user u where u.id = "
												+ luser2.getId() + " and t.status=0");
								if (bl != null) {
									updateParentBrokerageAccountLD(luser1, luser2, bonusProcess, bl, 1, lb, cftType);
									account.setAvlAmt(account.getAvlAmt().subtract(new BigDecimal(lb)));
									accountDao.update(account);
									TaccountLog log = new TaccountLog(account, UserOrder.cd_type.T.name(),
											new BigDecimal(lb), "领导奖金扣除");
									log.setAvlAmt(account.getAvlAmt());
									accountLogDao.save(log);
								} else {
									log.info("领导奖获取人ID:" + luser2.getId() + "没有佣金账户,不参与分佣");
								}
							}
						} else {
							log.info("领导奖获取人ID:" + luser2.getId() + ",分润金额: " + lb + "小于0.01,不参与领导奖!");
						}
					} else {
						log.info("-----推荐代理用户不为三级代理商不参与领导奖-----");
					}
				} else {
					log.info("-----代理商没有推荐用户不参与领导奖-----");
				}
			} else {
				log.info("-----代理商未绑定结算用户-----");
			}
		} else {
			log.info("-----不是三级代理不参与领导奖-----");
		}
	}

}
