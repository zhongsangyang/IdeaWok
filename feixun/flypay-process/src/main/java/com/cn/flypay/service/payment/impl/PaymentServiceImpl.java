package com.cn.flypay.service.payment.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.account.TaccountLog;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.model.trans.TOffLineDrawOrder;
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.model.trans.TbrokerageLog;
import com.cn.flypay.model.trans.TorderBonusProcess;
import com.cn.flypay.model.trans.TranPayOrder;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.trans.OffLineDrawOrder;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.payment.PaymentService;
import com.cn.flypay.service.payment.PingAnExpenseService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.StringUtil;

/**
 * Created by zhoujifeng1 on 16/8/1.
 */
@Service
public class PaymentServiceImpl implements PaymentService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private BaseDao<TuserOrder> orderDao;
	@Autowired
	private AccountService accountService;

	@Autowired
	private UserOrderService orderService;
	@Autowired
	private BaseDao<Tuser> userDao;
	@Autowired
	private BaseDao<Taccount> accountDao;
	@Autowired
	private BaseDao<Tbrokerage> brokerageDao;
	@Autowired
	private BaseDao<TaccountLog> accountLogDao;
	@Autowired
	private CommonService commonService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private PingAnExpenseService pingAnExpenseService;
	@Autowired
	com.cn.flypay.service.trans.OfflineDrawOrderService OfflineDrawOrderService;
	@Autowired
	private UserCardService cardService;
	@Autowired
	private BaseDao<TorderBonusProcess> bonusProcessDao;
	@Autowired
	private UserService userService;
	@Autowired
	private BaseDao<TbrokerageLog> brokerageLogDao;

	@Override
	public String updateAccountWhenTransferAccount(Long fromUserId, Long toUserId, Double amt, String desc) {
		String flag = GlobalConstant.RESP_CODE_999;
		Taccount fromAcc = accountDao.get("select t from Taccount t left join t.user u left join u.settlementConfig s where u.id=" + fromUserId);
		Tuser fromUser = fromAcc.getUser();
		Taccount toAccount = accountDao.get("select t from Taccount t left join t.user u left join u.settlementConfig s where u.id=" + toUserId);
		Tuser toUser = toAccount.getUser();
		BigDecimal amtBd = BigDecimal.valueOf(amt);
		if (fromAcc.getAvlAmt().subtract(amtBd).compareTo(BigDecimal.ZERO) > 0) {
			String fromOrderNum = commonService.getUniqueOrderByType(UserOrder.trans_type.QBZZ.name(), fromUserId);
			String toOrderNum = commonService.getUniqueOrderByType(UserOrder.trans_type.QBZZ.name(), toUserId);

			try {
				if (fromUser != null) {
					Tchannel chl = channelService.getTchannelByTransType(UserOrder.trans_type.QBZZ.getCode());
					/* 账户扣款 */
					fromAcc.setAvlAmt(fromAcc.getAvlAmt().subtract(amtBd));
					fromAcc.setPerMonthOutAmt(fromAcc.getPerMonthOutAmt().add(amtBd));
					accountDao.update(fromAcc);

					TaccountLog accountLog = new TaccountLog();
					accountLog.setAccount(fromAcc);
					accountLog.setAmt(amtBd);
					accountLog.setCreateTime(new Date());
					accountLog.setType(UserOrder.cd_type.H.name());
					accountLog.setDescription("向" + toUser.getLoginName() + "转账金额" + amtBd.doubleValue() + "元");
					accountLog.setAvlAmt(fromAcc.getAvlAmt());
					accountLogDao.save(accountLog);
					/* 生成订单 */
					TuserOrder fromOrder = new TuserOrder();
					fromOrder.setTransPayType(UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode());
					fromOrder.setType(UserOrder.trans_type.QBZZ.getCode());
					fromOrder.setCdType(UserOrder.cd_type.C.name());// 贷记
					fromOrder.setOrgAmt(amtBd);// 转账金额
					if (StringUtil.isNotBlank(desc)) {
						fromOrder.setDescription(desc);
					}
					fromOrder.setFee(BigDecimal.ZERO);// 费用为0
					fromOrder.setAvlAmt(fromAcc.getAvlAmt().subtract(amtBd));// 可用金额
					fromOrder.setStatus(UserOrder.order_status.SUCCESS.getCode());// 支付订单成功
					fromOrder.setOrderNum(fromOrderNum);
					fromOrder.setCreateTime(new Date());
					fromOrder.setUser(fromUser);
					fromOrder.setPayType(0);
					fromOrder.setScanNum(0);
					fromOrder.setInputAccType(0);
					fromOrder.setPersonRate(BigDecimal.ZERO);
					fromOrder.setShareRate(BigDecimal.ZERO);
					fromOrder.setDescription("向" + toUser.getLoginName() + "转账金额" + amtBd.doubleValue() + "元");
					/* 支付订单 */
					TranPayOrder tpo = new TranPayOrder();
					tpo.setPayDate(new Date());
					tpo.setPayAmt(amtBd);
					tpo.setRealAmt(amtBd);
					tpo.setFinishDate(new Date());
					tpo.setPayNo(toOrderNum);
					tpo.setStatus(PayOrder.pay_status.SUCCESS.getCode());// 已完成
					tpo.setPayChannel(chl);
					fromOrder.setTranPayOrder(tpo);
					tpo.setUserOrder(fromOrder);
					orderDao.save(fromOrder);

					/* 账户添加款项 */
					toAccount.setAvlAmt(toAccount.getAvlAmt().add(amtBd));
					toAccount.setPerMonthInAmt(toAccount.getAvlAmt().add(amtBd));
					accountDao.update(toAccount);

					TaccountLog toAccLog = new TaccountLog();
					toAccLog.setAccount(toAccount);
					toAccLog.setAmt(amtBd);
					toAccLog.setCreateTime(new Date());
					toAccLog.setType(UserOrder.cd_type.J.name());
					toAccLog.setDescription("收到" + fromUser.getLoginName() + "转账金额" + amtBd.doubleValue() + "元");
					toAccLog.setAvlAmt(toAccount.getAvlAmt());
					accountLogDao.save(toAccLog);

					/* 入账账户 */
					TuserOrder toOrder = new TuserOrder();
					toOrder.setTransPayType(UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode());
					toOrder.setType(UserOrder.trans_type.QBZZ.getCode());
					toOrder.setCdType(UserOrder.cd_type.D.name());// 贷记
					toOrder.setOrgAmt(amtBd);// 转账金额
					if (StringUtil.isNotBlank(desc)) {
						toOrder.setDescription(desc);
					}
					toOrder.setFee(BigDecimal.ZERO);// 费用为0
					toOrder.setAvlAmt(fromAcc.getAvlAmt().add(amtBd));// 可用金额
					toOrder.setStatus(UserOrder.order_status.SUCCESS.getCode());// 支付订单成功
					toOrder.setOrderNum(toOrderNum);
					toOrder.setCreateTime(new Date());
					toOrder.setPayType(0);
					toOrder.setScanNum(0);
					toOrder.setUser(toUser);
					toOrder.setInputAccType(0);
					toOrder.setPersonRate(BigDecimal.ZERO);
					toOrder.setShareRate(BigDecimal.ZERO);
					fromOrder.setDescription("收到" + fromUser.getLoginName() + "转账金额" + amtBd.doubleValue() + "元");

					TranPayOrder topo = new TranPayOrder();
					topo.setPayDate(new Date());
					topo.setPayAmt(amtBd);
					topo.setRealAmt(amtBd);
					topo.setFinishDate(new Date());
					topo.setPayNo(fromOrderNum);
					topo.setStatus(PayOrder.pay_status.SUCCESS.getCode());// 已完成
					topo.setPayChannel(chl);

					toOrder.setTranPayOrder(topo);
					topo.setUserOrder(toOrder);
					orderDao.save(toOrder);

					flag = GlobalConstant.RESP_CODE_SUCCESS;
				} else {
					flag = GlobalConstant.RESP_CODE_012;
				}

			} catch (Exception e) {
				logger.error("用户转账异常", e);
				flag = GlobalConstant.RESP_CODE_047;
			}
		} else {
			flag = GlobalConstant.RESP_CODE_028;
		}
		return flag;
	}

	@Override
	public String updateAccountWhenLiqAccount(Long userId, String liqType, Double orgAmt, String desc) throws Exception {

		String flag = GlobalConstant.RESP_CODE_999;
		Tuser user = userDao.get(Tuser.class, userId);
		Taccount account = accountDao.get("select t from Taccount t left join t.user u left join u.settlementConfig s where t.status=0 and  u.id=" + userId);
		BigDecimal amtBd = BigDecimal.valueOf(orgAmt);
		if (account != null) {
			if (account.getAvlAmt().compareTo(amtBd) >= 0) {
				String orderNum = commonService.getUniqueOrderByUserId(user.getId());
				try {
					flag = accountService.updateAccountBeforeLiq(userId, liqType, amtBd);
					if (GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
						TuserCard settlementCard = cardService.getTUserCarByUserId(userId);
						if (settlementCard != null) {
							try {
								orderService.createWithdrawOrder(account, orderNum, orgAmt, "T0".equals(liqType) ? true : false, desc, settlementCard, UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode());
								logger.info("用户" + user.getRealName() + " 发起" + liqType + "提现，金额" + orgAmt);
								flag = GlobalConstant.RESP_CODE_SUCCESS + orderNum;
								// T1交易必须在第二天对账结束后，有业务人员发起
							} catch (Exception e) {
								flag = GlobalConstant.RESP_CODE_051;
								throw e;
							}
						} else {
							logger.info("用户没有结算卡，拒绝发送给平安代付通道" + orderNum);
							flag = GlobalConstant.RESP_CODE_024;
						}
					}
				} catch (Exception e) {
					flag = GlobalConstant.RESP_CODE_051;
					throw e;
				}
			} else {
				flag = GlobalConstant.RESP_CODE_028;
			}
		} else {
			// 账户已经被冻结，无法提现
			flag = GlobalConstant.RESP_CODE_035;
		}
		return flag;

	}

	public Map<String, String> updateAccountWhenLiqAccountMap(Long userId, String liqType, Double orgAmt, String desc) throws Exception {
		String flag = GlobalConstant.RESP_CODE_999;
		Map<String, String> returnMap = new HashMap<String, String>();
		Tuser user = userDao.get(Tuser.class, userId);
		Taccount account = accountDao.get("select t from Taccount t left join t.user u left join u.settlementConfig s where t.status=0 and  u.id='" + userId + "'");
		BigDecimal amtBd = BigDecimal.valueOf(orgAmt);
		BigDecimal _beforeAvlAmt = account.getAvlAmt();
		BigDecimal _payAmt = amtBd;
		BigDecimal _afterAvlAmt = _beforeAvlAmt.subtract(_payAmt);
		BigDecimal _realPayAmt = _payAmt.subtract(new BigDecimal(2));

		if (account != null) {
			if (account.getAvlAmt().compareTo(amtBd) >= 0) {
				String orderNum = commonService.getUniqueOrderByUserId(user.getId());
				returnMap.put("orderNum", orderNum);
				try {
					flag = accountService.updateAccountBeforeLiq(userId, liqType, amtBd);
					if (GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
						TuserCard settlementCard = cardService.getTUserCarByUserId(userId);
						if (settlementCard != null) {
							try {
								TuserOrder uORder = orderService.createWithdrawOrder(account, orderNum, orgAmt, "T0".equals(liqType) ? true : false, desc, settlementCard, UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode());
								String channelName = uORder.getTranPayOrder().getPayChannel().getName();
								returnMap.put("channelName", channelName);
								logger.info("用户" + user.getRealName() + " 发起" + liqType + "提现，金额" + orgAmt);
								// flag = GlobalConstant.RESP_CODE_SUCCESS +
								// orderNum;
								flag = GlobalConstant.RESP_CODE_SUCCESS;
								// T1交易必须在第二天对账结束后，有业务人员发起
								// OFFLINE ORDER TODO
								OffLineDrawOrder offLineDrawOrder = new OffLineDrawOrder();
								offLineDrawOrder.setAfterAvlAmt(_afterAvlAmt);
								offLineDrawOrder.setBeforeAvlAmt(_beforeAvlAmt);
								offLineDrawOrder.setPayAmt(_realPayAmt);
								offLineDrawOrder.setCreateTime(new Date());
								offLineDrawOrder.setDrawSrc("Account");
								offLineDrawOrder.setLoginName(user.getLoginName());
								// 上海涩零企业营销策划有限公司:商户编号6688150131005437
								offLineDrawOrder.setMerFlowNo("5437-" + orderNum);// 以商户编号后4位开头
								String _openBankName = StringUtil.isEmpty(settlementCard.getBranchName()) ? "中国建设银行" : settlementCard.getBranchName();
								offLineDrawOrder.setOpenBankName(_openBankName);
								offLineDrawOrder.setOrderNo(orderNum);
								offLineDrawOrder.setAccountBankNo(settlementCard.getCardNo());
								offLineDrawOrder.setUserId(userId);
								offLineDrawOrder.setReceiverName(user.getRealName());
								offLineDrawOrder.setStatus(TOffLineDrawOrder.STATUS_INIT);
								OfflineDrawOrderService.add(offLineDrawOrder);
							} catch (Exception e) {
								flag = GlobalConstant.RESP_CODE_051;
								throw e;
							}
						} else {
							logger.info("用户没有结算卡，拒绝发送给平安代付通道" + orderNum);
							flag = GlobalConstant.RESP_CODE_024;
						}
					}
				} catch (Exception e) {
					flag = GlobalConstant.RESP_CODE_051;
					throw e;
				}
			} else {
				flag = GlobalConstant.RESP_CODE_028;
			}
		} else {
			// 账户已经被冻结，无法提现
			flag = GlobalConstant.RESP_CODE_035;
		}
		returnMap.put("flag", flag);
		return returnMap;
	}

	@Override
	public String updateBrokerageAccountWhenLiqBrokerage(Long userId, Double amt, String desc, String type) {

		String flag = GlobalConstant.RESP_CODE_999;
		Tuser user = userDao.get(Tuser.class, userId);
		Tbrokerage bk = brokerageDao.get("select t from Tbrokerage t left join t.user u where u.id=" + userId);
		BigDecimal amtBd = BigDecimal.valueOf(amt);
		if (bk != null && bk.getBrokerage().subtract(amtBd).compareTo(BigDecimal.ZERO) >= 0) {
			String orderNum = commonService.getUniqueOrderByUserId(user.getId());
			try {
				try {
					logger.info("-------提现前账户的信息变更 begin - ------");
					String description = "提现之前,将锁定金额" + amt.doubleValue() + "待交易成功后，从锁定金额中扣除";
					if (bk.getBrokerage().doubleValue() - amt >= 0) {
						bk.setBrokerage(bk.getBrokerage().subtract(BigDecimal.valueOf(amt)));
						if (type != null) {
							if (type.equals("1")) {
								bk.setTotalTransBrokerage(bk.getTotalTransBrokerage().subtract(BigDecimal.valueOf(amt)));
							} else if (type.equals("2")) {
								bk.setTotalAgentBrokerage(bk.getTotalAgentBrokerage().subtract(BigDecimal.valueOf(amt)));
							} else if (type.equals("3")) {
								bk.setTotalLeadBrokerage(bk.getTotalLeadBrokerage().subtract(BigDecimal.valueOf(amt)));
							}
						}

						bk.setLockBrokerage(bk.getLockBrokerage().add(BigDecimal.valueOf(amt)));
						brokerageDao.update(bk);

						// 佣金提现日志
						TbrokerageLog brokerageLog = new TbrokerageLog(bk, UserOrder.cd_type.R.name(), BigDecimal.valueOf(amt), description);
						brokerageLog.setAvlAmt(bk.getBrokerage());
						brokerageLog.setLockOutAmt(bk.getLockBrokerage());
						brokerageLog.setOrdernum(orderNum);
						brokerageLogDao.save(brokerageLog);

						flag = GlobalConstant.RESP_CODE_SUCCESS;
						logger.info("-------提现前账户的信息变更 end-------");

						TuserCard settlementCard = cardService.getTUserCarByUserId(userId);
						if (settlementCard != null) {
							try {
								orderService.createBrokerageOrder(userId, orderNum, amt, desc, settlementCard);
								logger.info("用户" + user.getRealName() + " 发起佣金提现，金额" + amt);
								flag = GlobalConstant.RESP_CODE_SUCCESS + orderNum;
							} catch (Exception e) {
								flag = GlobalConstant.RESP_CODE_051;
							}
						} else {
							logger.info("用户没有结算卡，拒绝发送给平安代付通道" + orderNum);
							flag = GlobalConstant.RESP_CODE_024;
						}

					} else {
						description = "提现失败,账户可用金额（" + bk.getBrokerage().doubleValue() + "）小于提现订单金额(" + amt.doubleValue() + ")，订单出现异常，锁定账户";
						flag = GlobalConstant.RESP_CODE_014;
					}

					logger.info(description);
				} catch (Exception e) {
					flag = GlobalConstant.RESP_CODE_051;
				}
			} catch (Exception e) {
				logger.error("用户提现", e);
				flag = GlobalConstant.RESP_CODE_047;
			}
		} else {
			flag = GlobalConstant.RESP_CODE_028;
		}
		return flag;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cn.flypay.service.payment.PaymentService#
	 * updateBrokerageAccountWhenLiqBrokerageMap(java.lang.Long,
	 * java.lang.Double, java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, String> updateBrokerageAccountWhenLiqBrokerageMap(Long userId, Double amt, String desc, String type) {

		String flag = GlobalConstant.RESP_CODE_999;
		Map<String, String> returnMap = new HashMap<String, String>();
		Tuser user = userDao.get(Tuser.class, userId);
		Tbrokerage bk = brokerageDao.get("select t from Tbrokerage t left join t.user u where u.id='" + userId + "'");
		BigDecimal amtBd = BigDecimal.valueOf(amt);
		BigDecimal _beforeAvlAmt = bk.getBrokerage();
		BigDecimal _payAmt = amtBd;
		BigDecimal _afterAvlAmt = _beforeAvlAmt.subtract(_payAmt);
		BigDecimal _realPayAmt = _payAmt.subtract(new BigDecimal(2));

		if (bk != null && bk.getBrokerage().subtract(amtBd).compareTo(BigDecimal.ZERO) >= 0) {
			String orderNum = commonService.getUniqueOrderByUserId(user.getId());
			returnMap.put("orderNum", orderNum);
			try {
				try {
					logger.info("-------提现前账户的信息变更 begin - ------");
					String description = "提现之前,将锁定金额" + amt.doubleValue() + "待交易成功后，从锁定金额中扣除";
					if (bk.getBrokerage().doubleValue() - amt >= 0) {
						bk.setBrokerage(bk.getBrokerage().subtract(BigDecimal.valueOf(amt)));
						if (type != null) {
							if (type.equals("1")) {
								bk.setTotalTransBrokerage(bk.getTotalTransBrokerage().subtract(BigDecimal.valueOf(amt)));
							} else if (type.equals("2")) {
								bk.setTotalAgentBrokerage(bk.getTotalAgentBrokerage().subtract(BigDecimal.valueOf(amt)));
							} else if (type.equals("3")) {
								bk.setTotalLeadBrokerage(bk.getTotalLeadBrokerage().subtract(BigDecimal.valueOf(amt)));
							}
						}

						bk.setLockBrokerage(bk.getLockBrokerage().add(BigDecimal.valueOf(amt)));
						brokerageDao.update(bk);

						// 佣金提现日志
						TbrokerageLog brokerageLog = new TbrokerageLog(bk, UserOrder.cd_type.R.name(), BigDecimal.valueOf(amt), description);
						brokerageLog.setAvlAmt(bk.getBrokerage());
						brokerageLog.setLockOutAmt(bk.getLockBrokerage());
						brokerageLog.setOrdernum(orderNum);
						brokerageLogDao.save(brokerageLog);

						flag = GlobalConstant.RESP_CODE_SUCCESS;
						logger.info("-------提现前账户的信息变更 end-------");

						TuserCard settlementCard = cardService.getTUserCarByUserId(userId);
						if (settlementCard != null) {
							try {
								TuserOrder uORder = orderService.createBrokerageOrder(userId, orderNum, amt, desc, settlementCard);
								String channelName = uORder.getTranPayOrder().getPayChannel().getName();
								returnMap.put("channelName", channelName);
								logger.info("用户" + user.getRealName() + " 发起佣金提现，金额" + amt);
								flag = GlobalConstant.RESP_CODE_SUCCESS;
								// OFFLINE ORDER TODO
								OffLineDrawOrder offLineDrawOrder = new OffLineDrawOrder();
								offLineDrawOrder.setAfterAvlAmt(_afterAvlAmt);
								offLineDrawOrder.setBeforeAvlAmt(_beforeAvlAmt);
								offLineDrawOrder.setPayAmt(_realPayAmt);
								offLineDrawOrder.setCreateTime(new Date());
								offLineDrawOrder.setDrawSrc("Brokerage");
								offLineDrawOrder.setLoginName(user.getLoginName());
								// 上海涩零企业营销策划有限公司:商户编号6688150131005437
								offLineDrawOrder.setMerFlowNo("5437-" + orderNum);// 以商户编号后4位开头
								String _openBankName = StringUtil.isEmpty(settlementCard.getBranchName()) ? "中国建设银行" : settlementCard.getBranchName();
								offLineDrawOrder.setOpenBankName(_openBankName);
								offLineDrawOrder.setOrderNo(orderNum);
								offLineDrawOrder.setAccountBankNo(settlementCard.getCardNo());
								offLineDrawOrder.setUserId(userId);
								offLineDrawOrder.setReceiverName(user.getRealName());
								offLineDrawOrder.setStatus(TOffLineDrawOrder.STATUS_INIT);
								OfflineDrawOrderService.add(offLineDrawOrder);
							} catch (Exception e) {
								e.printStackTrace();
								flag = GlobalConstant.RESP_CODE_051;
							}
						} else {
							logger.info("用户没有结算卡，拒绝发送给平安代付通道" + orderNum);
							flag = GlobalConstant.RESP_CODE_024;
						}

					} else {
						description = "提现失败,账户可用金额（" + bk.getBrokerage().doubleValue() + "）小于提现订单金额(" + amt.doubleValue() + ")，订单出现异常，锁定账户";
						flag = GlobalConstant.RESP_CODE_014;
					}

					logger.info(description);
				} catch (Exception e) {
					flag = GlobalConstant.RESP_CODE_051;
				}
			} catch (Exception e) {
				logger.error("用户提现", e);
				flag = GlobalConstant.RESP_CODE_047;
			}
		} else {
			flag = GlobalConstant.RESP_CODE_028;
		}
		returnMap.put("flag", flag);
		return returnMap;

	}

	@Override
	public String updateBrokerageAccountWhenLiqBrokerageTwo(Long userId, Double amt, String desc, String type) {

		String flag = GlobalConstant.RESP_CODE_999;
		Tuser user = userDao.get(Tuser.class, userId);
		Tbrokerage bk = brokerageDao.get("select t from Tbrokerage t left join t.user u where u.id=" + userId);
		BigDecimal amtBd = BigDecimal.valueOf(amt);
		if (bk != null && bk.getBrokerage().subtract(amtBd).compareTo(BigDecimal.ZERO) >= 0) {
			String orderNum = commonService.getUniqueOrderByUserId(user.getId());
			try {
				try {
					logger.info("-------提现前账户的信息变更 begin - ------");
					String description = "提现之前,将锁定金额" + amt.doubleValue() + "待交易成功后，从锁定金额中扣除";
					if (bk.getBrokerage().doubleValue() - amt >= 0) {
						bk.setBrokerage(bk.getBrokerage().subtract(BigDecimal.valueOf(amt)));
						if (type.equals("1")) {
							bk.setTotalTransBrokerage(bk.getTotalTransBrokerage().subtract(BigDecimal.valueOf(amt)));
						} else if (type.equals("2")) {
							bk.setTotalAgentBrokerage(bk.getTotalAgentBrokerage().subtract(BigDecimal.valueOf(amt)));
						} else if (type.equals("3")) {
							bk.setTotalLeadBrokerage(bk.getTotalLeadBrokerage().subtract(BigDecimal.valueOf(amt)));
						}
						bk.setLockBrokerage(bk.getLockBrokerage().add(BigDecimal.valueOf(amt)));
						brokerageDao.update(bk);

						// 佣金提现日志
						TbrokerageLog brokerageLog = new TbrokerageLog(bk, UserOrder.cd_type.R.name(), BigDecimal.valueOf(amt), description);
						brokerageLog.setAvlAmt(bk.getBrokerage());
						brokerageLog.setLockOutAmt(bk.getLockBrokerage());
						brokerageLog.setOrdernum(orderNum);
						brokerageLogDao.save(brokerageLog);

						flag = GlobalConstant.RESP_CODE_SUCCESS;
						logger.info("-------提现前账户的信息变更 end-------");

						TuserCard settlementCard = cardService.getTUserCarByUserId(userId);
						if (settlementCard != null) {
							try {
								orderService.createBrokerageOrder(userId, orderNum, amt, desc, settlementCard);
								logger.info("用户" + user.getRealName() + " 发起佣金提现，金额" + amt);
								flag = GlobalConstant.RESP_CODE_SUCCESS + orderNum;
							} catch (Exception e) {
								flag = GlobalConstant.RESP_CODE_051;
							}
						} else {
							logger.info("用户没有结算卡，拒绝发送给平安代付通道" + orderNum);
							flag = GlobalConstant.RESP_CODE_024;
						}

					} else {
						description = "提现失败,账户可用金额（" + bk.getBrokerage().doubleValue() + "）小于提现订单金额(" + amt.doubleValue() + ")，订单出现异常，锁定账户";
						flag = GlobalConstant.RESP_CODE_014;
					}

					logger.info(description);
				} catch (Exception e) {
					flag = GlobalConstant.RESP_CODE_051;
				}
			} catch (Exception e) {
				logger.error("用户提现", e);
				flag = GlobalConstant.RESP_CODE_047;
			}
		} else {
			flag = GlobalConstant.RESP_CODE_028;
		}
		return flag;

	}

	@Override
	public String updateAccountWhenPayAgent(Long userId, Double amt) {

		String flag = GlobalConstant.RESP_CODE_999;
		Tuser user = userDao.get("select t from Tuser t left join t.settlementConfig s where t.id=" + userId);

		Taccount account = accountDao.get("select t from Taccount t left join t.user u where u.id=" + userId);
		BigDecimal amtBd = BigDecimal.valueOf(amt);
		if (account.getAvlAmt().subtract(amtBd).compareTo(BigDecimal.ZERO) > 0) {
			String fromOrderNum = commonService.getUniqueOrderByType(UserOrder.trans_type.YJDL.name(), userId);
			try {
				if (user != null) {
					Tchannel chl = channelService.getTchannelByTransType(UserOrder.trans_type.QBZZ.getCode());
					/* 账户扣款 */
					account.setAvlAmt(account.getAvlAmt().subtract(amtBd));
					account.setPerMonthOutAmt(account.getPerMonthOutAmt().add(amtBd));
					accountDao.update(account);

					TaccountLog accountLog = new TaccountLog(account, UserOrder.cd_type.N.name(), amtBd, "您用钱包余额" + amtBd + "元进行用户升级");
					accountLog.setAvlAmt(account.getAvlAmt());
					accountLogDao.save(accountLog);

					/* 生成订单 */
					TuserOrder order = new TuserOrder();
					order.setTransPayType(UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode());
					order.setType(UserOrder.trans_type.QBZZ.getCode());
					order.setCdType(UserOrder.cd_type.C.name());// 贷记
					order.setOrgAmt(amtBd);// 转账金额
					order.setDescription("用户升级消费" + amtBd.longValue() + "元");
					order.setFee(BigDecimal.ZERO);// 费用为0
					order.setAvlAmt(account.getAvlAmt());// 可用金额
					order.setStatus(UserOrder.order_status.SUCCESS.getCode());// 支付订单成功
					order.setOrderNum(fromOrderNum);
					order.setCreateTime(new Date());
					order.setUser(user);
					order.setPayType(0);
					order.setScanNum(0);
					order.setInputAccType(0);
					order.setPersonRate(BigDecimal.ZERO);
					order.setShareRate(BigDecimal.ONE);

					/* 支付订单 */
					TranPayOrder tpo = new TranPayOrder();
					tpo.setPayDate(new Date());
					tpo.setPayAmt(amtBd);
					tpo.setRealAmt(amtBd);
					tpo.setFinishDate(new Date());
					tpo.setPayNo(fromOrderNum);
					tpo.setStatus(PayOrder.pay_status.SUCCESS.getCode());// 已完成
					tpo.setPayChannel(chl);

					order.setTranPayOrder(tpo);
					tpo.setUserOrder(order);
					orderDao.save(order);

					/* 开始分润 */
					TorderBonusProcess bonusProcess = new TorderBonusProcess(order, amtBd);
					bonusProcessDao.save(bonusProcess);
					// shareBonusService.dealBonusWhenOrder(fromOrderNum);

					/* 设置用户的类型 */
					userService.updateUserType(userId, amtBd, null);
					flag = GlobalConstant.RESP_CODE_SUCCESS;
				} else {
					flag = GlobalConstant.RESP_CODE_012;
				}

			} catch (Exception e) {
				logger.error("用户转账异常", e);
				flag = GlobalConstant.RESP_CODE_047;
			}
		} else {
			flag = GlobalConstant.RESP_CODE_028;
		}
		return flag;
	}
}
