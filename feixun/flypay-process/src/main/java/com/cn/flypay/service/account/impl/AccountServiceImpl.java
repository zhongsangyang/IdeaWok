package com.cn.flypay.service.account.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.account.TaccountLog;
import com.cn.flypay.model.account.TaccountOrderError;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.model.trans.TbrokerageLog;
import com.cn.flypay.model.trans.TranPayOrder;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.account.Account;
import com.cn.flypay.pageModel.account.AccountAdjust;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.Brokerage;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.SmsValidateService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

@Service
public class AccountServiceImpl implements AccountService {
	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	// private static String[] keys = new String[] { "realName", "name",
	// "loginName", "userId", "status", "avlAmt", "lockOutAmt", "perMonthInAmt",
	// "perMonthOutAmt", "throughAmt", "t1Amt", "t2Amt", "t3Amt", "t4Amt",
	// "t5Amt", "t6Amt", "t7Amt",
	// "t8Amt", "t9Amt", "t10Amt", "t11Amt" };

	private static String[] columnNames = new String[] { "名称", "name", "loginName", "用户ID", "状态", "账户余额", "锁定余额", "月入", "月出", "直通车额度", "t1Amt", "t2Amt", "t3Amt", "t4Amt", "t5Amt", "t6Amt", "t7Amt", "t8Amt", "t9Amt", "t10Amt", "t11Amt" };

	// private static String[] keys2 = new String[] { "userName", "userCode",
	// "loginName", "userId", "status", "brokerage", "lockBrokerage",
	// "totalBrokerage", "totalTransBrokerage", "totalAgentBrokerage",
	// "totalLeadBrokerage",
	// "historyBrokerage", "yesterdayBrokerage" };

	private static String[] columnNames2 = new String[] { "名称", "userCode", "loginName", "用户ID", "状态", "佣金余额", "锁定佣金", "历史佣金总额", "共交易佣金", "共代理佣金", "共领导佣金", "历史佣金", "昨日佣金" };

	@Value("${account_root_path}")
	private String account_root_path;

	@Autowired
	private BaseDao<Taccount> accountDao;

	@Autowired
	private BaseDao<Tbrokerage> brokerageDao;
	@Autowired
	private BaseDao<TaccountLog> accountLogDao;
	@Autowired
	private BaseDao<TaccountOrderError> errorDao;
	@Autowired
	private BaseDao<TranPayOrder> payOrderDao;
	@Autowired
	private BaseDao<TuserOrder> userOrderDao;
	@Autowired
	private BaseDao<TbrokerageLog> brokerageLogDao;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private SmsValidateService smsValidateService;

	@Override
	public List<Account> dataGrid(Account param, PageFilter ph) {
		List<Account> ul = new ArrayList<Account>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from Taccount t left join t.user u left join u.organization tog ";
		List<Taccount> l = accountDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (Taccount t : l) {
			ul.add(new Account(t));
		}

		return ul;
	}

	private String whereHql(Account account, Map<String, Object> params) {
		String hql = "";
		if (account != null) {
			hql += " where 1=1 ";
			if (StringUtil.isNotBlank(account.getLoginName())) {
				hql += " and u.loginName=:loginName ";
				params.put("loginName", account.getLoginName());
			}
			if (account.getStatus() != null) {
				hql += " and t.status=:status ";
				params.put("status", account.getStatus());
			}
			if (StringUtil.isNotEmpty(account.getDateForYM())) {
				hql += " and u.createDatetime between :date1 and :date2 ";
				Date d1 = null;
				Date d2 = null;
				try {
					Date[] ds = new Date[2];
					ds = DateUtil.getFirstAndLastDayOfMonth(DateUtil.getDateFromString(account.getDateForYM() + "-01"));
					d1 = ds[0];
					d2 = ds[1];
				} catch (Exception e) {
				}
				params.put("date1", d1);
				params.put("date2", d2);
			}
			if (account.getOrganizationId() != null) {
				hql += " and  tog.id in(:orgIds)";
				params.put("orgIds", organizationService.getOwerOrgIds(account.getOrganizationId()));
			}
			if (account.getOperateUser() != null) {
				hql += " and  tog.id in(:operaterOrgIds)";
				params.put("operaterOrgIds", organizationService.getOwerOrgIds(account.getOperateUser().getOrganizationId()));
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
	public List<Account> searchAccount() {

		return null;
	}

	@Override
	public Long count(Account param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from Taccount t left join t.user u left join u.organization tog ";
		return accountDao.count("select count(t.id) " + hql + whereHql(param, params), params);
	}

	@Override
	public void editRemark(Account param) {
		Taccount ta = accountDao.get(Taccount.class, param.getId());
		ta.setRemark(param.getRemark());
		accountDao.update(ta);
	}

	@Override
	public void freeze(Long id) {
		Taccount ta = accountDao.get(Taccount.class, id);
		if (ta != null) {
			int status = ta.getStatus() == 0 ? 1 : 0;
			accountDao.executeHql("update Taccount set status=" + status + " where id=" + id);
		}
	}

	/**
	 * status:=100 跨平台冻结 全oem冻结  :=1 本oem冻结
	 */
	@Override
	public Taccount freeze(Account account) {
		Taccount t = accountDao.get("select t from Taccount t left join t.user u where t.id=" + account.getId());
		if (t != null) {
			if (t.getUser() != null && StringUtil.isNotEmpty(t.getUser().getIdNo())) {
				String updateSql = "update account a left join sys_user u on u.id=a.USER_ID set a.status=:status where 1=1 ";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("status", account.getStatus());
				if (t.getStatus() == 1 || account.getStatus() == 1) {
					updateSql = updateSql + " and u.id_no =:idNo and u.agent_id=:agentId";
					params.put("idNo", t.getUser().getIdNo());
					params.put("agentId", t.getUser().getAgentId());
					accountDao.executeSql(updateSql, params);
				} else if (t.getStatus() == 100 || account.getStatus() == 100) {
					updateSql = updateSql + " and u.id_no =:idNo ";
					params.put("idNo", t.getUser().getIdNo());
					accountDao.executeSql(updateSql, params);
				}
			}
			t.setRemark(account.getRemark());
			t.setStatus(account.getStatus());
			accountDao.update(t);
			return t;
		}
		return null;
	}

	@Override
	public Account getAccountByUserId(long userId) {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userId);
		String hql = " select t from Taccount t left join t.user u where u.id=:uid";
		Taccount t = accountDao.get(hql, params);
		return new Account(t);
	}

	@Override
	public Taccount getAccountByUserIdTwo(long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userId);
		String hql = " select t from Taccount t left join t.user u where u.id=:uid";
		Taccount t = accountDao.get(hql, params);
		return t;
	}

	/**
	 * 用户提现（T0/T1）
	 */
	@Override
	public String updateAccountBeforeLiq(Long userId, String liqType, BigDecimal orgAmt) {
		LOG.info("-------提现前账户的信息变更 begin - ------");
		String flag = GlobalConstant.RESP_CODE_999;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userId);
		String hql = " select t from Taccount t left join t.user u where t.status=0 and u.id=:uid";
		Taccount t = accountDao.get(hql, params);
		String description = liqType + "提现之前,将锁定金额" + orgAmt.doubleValue() + "待交易成功后，从锁定金额中扣除";
		if (t != null) {
			if (t.getAvlAmt().compareTo(orgAmt) >= 0) {
				t.setAvlAmt(t.getAvlAmt().subtract(orgAmt));
				t.setLockOutAmt(t.getLockOutAmt().add(orgAmt));
				accountDao.update(t);
				flag = GlobalConstant.RESP_CODE_SUCCESS;
			} else {
				description = liqType + "提现失败,账户可用金额（" + t.getAvlAmt().doubleValue() + "）小于提现订单金额(" + orgAmt.doubleValue() + ")，订单出现异常，锁定账户";
				flag = GlobalConstant.RESP_CODE_014;
			}
		} else {
			flag = GlobalConstant.RESP_CODE_035;
			description = liqType + "提现失败,用户账户处于锁定状态，无效请求，先将账户解冻";
		}

		TaccountLog log = new TaccountLog(t, UserOrder.cd_type.R.name(), orgAmt, description);
		log.setAvlAmt(t.getAvlAmt());
		log.setLockOutAmt(t.getLockOutAmt());
		accountLogDao.save(log);
		LOG.info("-------提现前账户的信息变更 end-------");
		return flag;
	}

	@Override
	public String updateAccountAfterLiqFailure(Long userId, TuserOrder order) {
		LOG.info("-------提现失败后账户的信息变更 start");
		String flag = GlobalConstant.RESP_CODE_999;
		Map<String, Object> params = new HashMap<String, Object>();
		Tuser user = order.getUser();
		params.put("uid", user.getId());
		String hql = " select t from Taccount t left join t.user u where t.status=0 and u.id=:uid";
		Taccount t = accountDao.get(hql, params);
		BigDecimal orgAmt = order.getOrgAmt();
		String description = "提现失败,将锁定金额" + orgAmt.doubleValue() + ",成功会退给商户,订单号：" + order.getOrderNum();
		boolean isLock = true;
		if (t != null) {
			if (orgAmt.compareTo(t.getLockOutAmt()) <= 0) {
				t.setAvlAmt(t.getAvlAmt().add(orgAmt));
				t.setLockOutAmt(t.getLockOutAmt().subtract(orgAmt));
				isLock = false;
				flag = GlobalConstant.RESP_CODE_SUCCESS;
				LOG.info("商户" + user.getId() + " 名字" + user.getRealName() + " 账户退还提现失败后的金额" + orgAmt);
			} else {
				description = "提现失败,用户锁定金额（" + t.getLockOutAmt().doubleValue() + "）小于提现订单金额(" + orgAmt.doubleValue() + ")，订单出现异常，锁定账户,订单号：" + order.getOrderNum();
				flag = GlobalConstant.RESP_CODE_014;
			}
		} else {
			flag = GlobalConstant.RESP_CODE_035;
			description = "提现失败,用户账户处于锁定状态，但订单已经完成，请人工调整账户冻结，订单号：" + order.getOrderNum();
		}
		if (t != null) {
			if (isLock) {
				t.setStatus(1);
			}
			accountDao.update(t);
			TaccountLog log = new TaccountLog(t, UserOrder.cd_type.F.name(), order.getOrgAmt(), description);
			log.setAvlAmt(t.getAvlAmt());
			log.setLockOutAmt(t.getLockOutAmt());
			accountLogDao.save(log);
		}
		LOG.info("-------提现失败后账户的信息变更 end");
		return flag;
	}

	@Override
	public String updateAccountAfterLiqSuccess(Long userId, TuserOrder order) {
		LOG.info("-------提现成功后账户的信息变更 begin");
		String flag = GlobalConstant.RESP_CODE_999;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userId);
		String hql = " select t from Taccount t left join t.user u where t.status=0 and u.id=:uid";
		Taccount account = accountDao.get(hql, params);

		BigDecimal orgAmt = order.getOrgAmt();
		String description = "提现订单已完成，金额" + orgAmt.doubleValue() + ",订单号：" + order.getOrderNum();
		LOG.info(description);
		boolean isLock = true;
		if (account != null) {
			if (orgAmt.compareTo(account.getLockOutAmt()) <= 0) {
				account.setLockOutAmt(account.getLockOutAmt().subtract(orgAmt));
				account.setPerMonthOutAmt(account.getPerMonthOutAmt().add(orgAmt));
				isLock = false;
				flag = GlobalConstant.RESP_CODE_SUCCESS;
				/* 每人每日T0限额 统计，当天23点清零 */
				if (order.getPayType() == 0) {
					account.setTodayOutAmt(account.getTodayOutAmt().add(orgAmt));
				}

				if (order.getTranPayOrder() != null) {
					order.getTranPayOrder().setAvlAccAmt(account.getAvlAmt());
					payOrderDao.update(order.getTranPayOrder());
				}
			} else {
				description = "用户锁定金额（" + account.getLockOutAmt().doubleValue() + "）小于提现订单金额(" + orgAmt.doubleValue() + ")，订单出现异常，锁定账户,订单号：" + order.getOrderNum();
				flag = GlobalConstant.RESP_CODE_014;
			}
		} else {
			flag = GlobalConstant.RESP_CODE_035;
			description = "用户账户处于锁定状态，但订单已经完成，请人工调整账户冻结，订单号：" + order.getOrderNum();
		}
		if (isLock) {
			account.setStatus(1);
		}
		accountDao.update(account);
		TaccountLog log = new TaccountLog(account, UserOrder.cd_type.T.name(), order.getOrgAmt(), description);
		log.setOrdernum(order.getOrderNum());
		log.setAvlAmt(account.getAvlAmt());
		log.setLockOutAmt(account.getLockOutAmt());
		accountLogDao.save(log);
		LOG.info("-------提现成功后账户的信息变更 end");
		return flag;
	}

	@Override
	public String updateAccountWhenAccountException(Set<Long> userIds) {
		if (userIds != null && userIds.size() > 0) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("uids", userIds);
			accountDao.executeHql("update Taccount set status = 1 where user.id in (:uids) ", params);
		}
		return GlobalConstant.RESP_CODE_SUCCESS;
	}

	@Override
	public String updateBrokerageAccountWhenAccountException(Set<Long> userIds) {
		if (userIds != null && userIds.size() > 0) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("uids", userIds);
			accountDao.executeHql("update Tbrokerage set status = 1 where user.id in (:uids) ", params);
		}
		return GlobalConstant.RESP_CODE_SUCCESS;
	}

	@Override
	public String updateBrokerageAccountBeforeLiq(Long userId, Double amt) {

		LOG.info("-------提现前账户的信息变更 begin - ------");
		String flag = GlobalConstant.RESP_CODE_999;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userId);
		String hql = " select t from Tbrokerage t left join t.user u where t.status=0 and u.id=:uid";
		Tbrokerage t = brokerageDao.get(hql, params);
		String description = "提现之前,将锁定金额" + amt.doubleValue() + "待交易成功后，从锁定金额中扣除";
		if (t != null) {
			if (t.getBrokerage().doubleValue() >= amt) {
				t.setBrokerage(t.getBrokerage().subtract(BigDecimal.valueOf(amt)));
				t.setLockBrokerage(t.getLockBrokerage().add(BigDecimal.valueOf(amt)));
				brokerageDao.update(t);
				flag = GlobalConstant.RESP_CODE_SUCCESS;
			} else {
				description = "提现失败,账户可用金额（" + t.getBrokerage().doubleValue() + "）小于提现订单金额(" + amt.doubleValue() + ")，订单出现异常，锁定账户";
				flag = GlobalConstant.RESP_CODE_014;
			}
		} else {
			flag = GlobalConstant.RESP_CODE_035;
			description = "提现失败,用户账户处于锁定状态，无效请求，请先将账户解冻";
		}
		// 增加佣金日志
		TbrokerageLog brokerageLog = new TbrokerageLog(t, UserOrder.cd_type.R.name(), BigDecimal.valueOf(amt), description);
		brokerageLog.setAvlAmt(t.getBrokerage());
		brokerageLog.setLockOutAmt(t.getLockBrokerage());
		brokerageLogDao.save(brokerageLog);
		LOG.info(description);
		LOG.info("-------提现前账户的信息变更 end-------");
		return flag;

	}

	@Override
	public String updateBrokerageAccountAfterLiqSuccess(Long userId, TuserOrder order) {

		LOG.info("-------佣金提现成功后账户的信息变更开始：orderId=" + order.getId() + ",订单号=" + order.getOrderNum());
		String flag = GlobalConstant.RESP_CODE_999;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userId);
		String hql = " select t from Tbrokerage t left join t.user u where t.status=0 and u.id=:uid";
		Tbrokerage t = brokerageDao.get(hql, params);

		BigDecimal orgAmt = order.getOrgAmt();
		String description = "提现订单已完成，金额" + orgAmt.doubleValue() + ",订单号：" + order.getOrderNum();
		boolean isLock = true;
		if (t != null) {
			if (orgAmt.compareTo(t.getLockBrokerage()) <= 0) {
				t.setLockBrokerage(t.getLockBrokerage().subtract(orgAmt));
				isLock = false;
				flag = GlobalConstant.RESP_CODE_SUCCESS;
				LOG.info("账户锁定金额减少了" + orgAmt + "元");
			} else {
				description = "用户锁定金额（" + t.getLockBrokerage().doubleValue() + "）小于提现订单金额(" + orgAmt.doubleValue() + ")，订单出现异常，锁定账户,订单号：" + order.getOrderNum();
				flag = GlobalConstant.RESP_CODE_014;
			}
		} else {
			flag = GlobalConstant.RESP_CODE_035;
			description = "用户账户处于锁定状态，但订单已经完成，请人工调整账户冻结，订单号：" + order.getOrderNum();
		}
		if (isLock) {
			t.setStatus(1);
		}
		LOG.info(description);
		brokerageDao.update(t);
		// 佣金提现日志
		TbrokerageLog brokerageLog = new TbrokerageLog(t, UserOrder.cd_type.T.name(), orgAmt, description);
		brokerageLog.setAvlAmt(t.getBrokerage());
		brokerageLog.setLockOutAmt(t.getLockBrokerage());
		brokerageLog.setOrdernum(order.getOrderNum());
		brokerageLogDao.save(brokerageLog);
		LOG.info("-------佣金提现成功后账户的信息变更 end");
		return flag;
	}

	@Override
	public String updateBrokerageAccountAfterLiqFailure(Long userId, TuserOrder order) {

		LOG.info("-------提现失败后账户的信息变更 begin");
		String flag = GlobalConstant.RESP_CODE_999;
		Map<String, Object> params = new HashMap<String, Object>();
		Tuser user = order.getUser();
		params.put("uid", user.getId());
		String hql = " select t from Tbrokerage t left join t.user u where t.status=0 and u.id=:uid";
		Tbrokerage t = brokerageDao.get(hql, params);
		BigDecimal amt = order.getOrgAmt();
		String description = "提现失败,将锁定金额" + amt.doubleValue() + ",成功会退给商户,订单号：" + order.getOrderNum();
		boolean isLock = true;
		if (t != null) {
			if (t.getLockBrokerage().doubleValue() - amt.doubleValue() >= 0) {
				t.setBrokerage(t.getBrokerage().add(amt));
				t.setLockBrokerage(t.getLockBrokerage().subtract(amt));
				isLock = false;
				flag = GlobalConstant.RESP_CODE_SUCCESS;
				LOG.info("商户" + user.getId() + " 名字" + user.getRealName() + " 账户退还提现失败后的金额" + amt);
			} else {
				description = "提现失败,用户锁定金额（" + t.getLockBrokerage().doubleValue() + "）小于提现订单金额(" + amt.doubleValue() + ")，订单出现异常，锁定账户,订单号：" + order.getOrderNum();
				flag = GlobalConstant.RESP_CODE_014;
			}
		} else {
			flag = GlobalConstant.RESP_CODE_035;
			description = "提现失败,用户账户处于锁定状态，但订单已经完成，请人工调整账户冻结，订单号：" + order.getOrderNum();
		}
		LOG.info(description);
		if (isLock) {
			t.setStatus(1);
		}
		brokerageDao.update(t);
		// 佣金提现日志
		TbrokerageLog brokerageLog = new TbrokerageLog(t, UserOrder.cd_type.F.name(), amt, description);
		brokerageLog.setAvlAmt(t.getBrokerage());
		brokerageLog.setLockOutAmt(t.getLockBrokerage());
		brokerageLog.setOrdernum(order.getOrderNum());
		brokerageLogDao.save(brokerageLog);
		LOG.info("-------提现失败后账户的信息变更 end");
		return flag;

	}

	@Override
	public Account get(Long accountId) {
		Taccount t = accountDao.get("select t from Taccount t left join t.user u where t.id=" + accountId);
		if (t != null) {
			Account acc = new Account(t);
			acc.setRemark(t.getRemark());
			return acc;
		}
		return null;
	}

	@Override
	public void dealAdjustAccount(User user, AccountAdjust adjust) {
		Taccount t = accountDao.get("select t from Taccount t left join t.user u where t.id=" + adjust.getId());
		if (t != null) {
			BigDecimal bd = BigDecimal.valueOf(adjust.getAmt());
			String desc = user.getLoginName() + "为商户" + t.getUser().getLoginName() + "调整，%s" + adjust.getAmt() + "元";
			if (UserOrder.cd_type.A.name().equals(adjust.getAdjustType())) {
				t.setAvlAmt(t.getAvlAmt().add(bd));
				t.setPerMonthInAmt(t.getPerMonthInAmt().add(bd));
				desc = String.format(desc, "添加了");
			} else {
				t.setAvlAmt(t.getAvlAmt().subtract(bd));
				t.setPerMonthOutAmt(t.getPerMonthOutAmt().add(bd));
				desc = String.format(desc, "减少了");
			}
			accountDao.update(t);
			TaccountLog log = new TaccountLog(t, adjust.getAdjustType(), bd, desc);
			log.setAvlAmt(t.getAvlAmt());
			accountLogDao.save(log);
		}

	}

	@Override
	public Taccount updateIncreaseAccountAfterSuccessInfo(String ordernum, Long userId, Integer inputAccType, BigDecimal amt, String desc) throws Exception {
		try {
			LOG.info(desc);
			String accCategory = "";
			Taccount acc = accountDao.get("select t from Taccount t left join t.user u where u.id=" + userId);
			if (inputAccType == 5) {
				accCategory = "T6";
				acc.setT6Amt(acc.getT6Amt().add(amt));
			} else if (inputAccType == 100) { // T10
				accCategory = "T11";
				acc.setT11Amt(acc.getT11Amt().add(amt));
			} else if (inputAccType == 8) {
				accCategory = "T9";
				acc.setT9Amt(acc.getT9Amt().add(amt));
			} else if (inputAccType == 1 || inputAccType == 11) {// T1 小额和大额
				// 监控日志
				LOG.info("ordernum={},inputAccType={},Amt={}", new Object[] { ordernum, inputAccType, amt });
				accCategory = "D2";
				acc.setD2Amt(acc.getD2Amt().add(amt));
			} else {
				accCategory = "AVL";
				acc.setAvlAmt(acc.getAvlAmt().add(amt));
			}
			acc.setPerMonthInAmt(acc.getPerMonthInAmt().add(amt));
			/* 添加账户日志 */
			desc = desc + ",类型=" + accCategory;
			TaccountLog accountLog = new TaccountLog(acc, UserOrder.cd_type.S.name(), amt, desc);
			accountLog.setOrdernum(ordernum);
			accountLog.setAvlAmt(acc.getAvlAmt());
			accountLogDao.save(accountLog);

			accountDao.update(acc);
			return acc;
		} catch (Exception e) {
			LOG.error("账户更新失败", e);
			throw e;
		}
	}

	@Override
	public Taccount updateAccountTroughInfo(Long userId, BigDecimal amt, String desc) throws Exception {
		try {
			LOG.info(desc);
			Taccount acc = accountDao.get("select t from Taccount t left join t.user u where u.id=" + userId);
			acc.setThroughAmt(acc.getThroughAmt().subtract(amt));
			/* 添加账户日志 */
			TaccountLog accountLog = new TaccountLog(acc, UserOrder.cd_type.D.name(), amt, desc);
			accountLogDao.save(accountLog);
			accountLog.setAvlAmt(acc.getAvlAmt());
			accountDao.update(acc);
			return acc;
		} catch (Exception e) {
			LOG.error("账户更新失败", e);
			throw e;
		}
	}

	@Override
	public void dealCleanPerMonthAccount() {
		accountDao.executeHql("update Taccount set perMonthInAmt=0 , perMonthOutAmt=0");
	}

	@Override
	public void updateUserT5Account() {
		accountDao.executeHql("update Taccount set t1Amt=t1Amt+t2Amt,t2Amt=t3Amt,t3Amt=t4Amt,t4Amt=t5Amt,t5Amt=t6Amt,t6Amt=t7Amt,t7Amt=t8Amt,t8Amt=t9Amt,t9Amt=t10Amt,t10Amt=t11Amt,t11Amt=0,d1Amt=d2Amt,d2Amt=0");
	}

	@Override
	public void updateUserT1AccountToAvl() {
		int num = accountDao.executeHql("update Taccount set avlAmt=avlAmt+t1Amt,t1Amt=0 where t1Amt!=0");
		LOG.info("总计" + num + "数据的T1账户金额进入可用金额");
	}

	@Override
	public void updateUserT1AccountToAvlTwo() {
		int numTwo = accountDao.executeHql("update Taccount set avlAmt=avlAmt+d1Amt,d1Amt=0 where d1Amt!=0");
		LOG.info("总计" + numTwo + "数据的D1账户金额进入可用金额");
	}

	@Override
	public void updateAllUserAccount() {
		accountDao.executeHql("update Taccount set historyAmt=yesterdayAmt ,yesterdayAmt=avlAmt+lockOutAmt+t1Amt+t2Amt+t3Amt+t4Amt+t5Amt+t6Amt+t7Amt+t8Amt+t9Amt+t10Amt+t11Amt+d1Amt+d2Amt,todayOutAmt=0,throughAmt=0");
	}

	@Override
	public void addAccountLogProfit() {
		Date date = new Date();
		Date startDate = DateUtil.getHoursbyInterval(DateUtil.getBeforeDate(date, 0), -1);
		Date endDate = DateUtil.getHoursbyInterval(DateUtil.getBeforeDate(date, 0), 23);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);

		String hql = "select t from TuserOrder t left join t.user u left join t.tranPayOrder p left join p.payChannel c where t.status=100 and t.type !=710 and c.name !='MINGSHENGZHITONGCHE' and t.createTime BETWEEN :startDate and :endDate";
		String hql2 = "select t from TaccountLog t left join t.account a left join a.user u where t.type in('S','T') and t.createTime BETWEEN :startDate and :endDate";
		List<TuserOrder> orderList = userOrderDao.find(hql, params);
		List<TaccountLog> logList = accountLogDao.find(hql2, params);

		if (orderList.size() != logList.size()) {
			TaccountOrderError error = new TaccountOrderError();
			error.setType("Z");
			error.setCreateTime(new Date());
			error.setRemark("时间:" + DateUtil.getStringFromDate(date, "yyyy-MM-dd") + "交易记录和入账记录不匹配,交易笔数:" + orderList.size() + "出入账笔数:" + logList.size());
			errorDao.save(error);
		} else {
			TaccountOrderError error = new TaccountOrderError();
			error.setType("Z");
			error.setCreateTime(new Date());
			error.setRemark("交易总笔数核对无问题");
			errorDao.save(error);
		}

		for (TaccountLog l : logList) {
			String hql3 = "select t from TuserOrder t where t.orderNum='" + l.getOrdernum() + "'";
			TuserOrder order = userOrderDao.get(hql3);
			if (order == null) {
				TaccountOrderError error = new TaccountOrderError();
				if (l.getType().equals("S")) {
					error.setType("S");
				} else {
					error.setType("T");
				}
				error.setUserID(l.getAccount().getUser().getId());
				error.setAccountAmt(l.getAmt());
				error.setOrderNum(l.getOrdernum());
				error.setCreateTime(new Date());
				error.setRemark("入账用户无交易订单记录");
				errorDao.save(error);
			} else {
				if (l.getAmt() != null && order.getAmt() != null) {
					if (order.getTranPayOrder().getPayChannel().getName().equals("XINKKEZHITONGCHE") || order.getTranPayOrder().getPayChannel().getName().equals("MINGSHENGZHITONGCHE")
							|| order.getTranPayOrder().getPayChannel().getName().equals("YILIANZHIFU") || order.getTranPayOrder().getPayChannel().getName().equals("XINKKEYINLIAN")
							|| order.getTranPayOrder().getPayChannel().getName().equals("XINKEYINLIAN")) {

						TaccountOrderError error = new TaccountOrderError();
						if (l.getType().equals("S")) {
							error.setType("S");
						} else {
							error.setType("T");
						}
						error.setUserID(l.getAccount().getUser().getId());
						error.setOrderAmt(order.getAmt());
						error.setAccountAmt(l.getAmt());
						error.setOrderNum(l.getOrdernum());
						error.setCreateTime(new Date());
						error.setRemark("直通车交易可能涉及重复清算请重视");
						errorDao.save(error);
						smsValidateService.sendMsgValidate("17321026899", "142355", "8888", "F20160001");
						smsValidateService.sendMsgValidate("18073969176", "142355", "8888", "F20160001");
						smsValidateService.sendMsgValidate("13641717364", "142355", "8888", "F20160001");
					} else {
						if (l.getType().equals("T")) {
							BigDecimal two = new BigDecimal(2.00);
							if (l.getAmt().subtract(two).compareTo(order.getAmt()) != 0) {
								TaccountOrderError error = new TaccountOrderError();
								if (l.getType().equals("S")) {
									error.setType("S");
								} else {
									error.setType("T");
								}
								error.setUserID(l.getAccount().getUser().getId());
								error.setOrderAmt(order.getAmt());
								error.setAccountAmt(l.getAmt());
								error.setOrderNum(l.getOrdernum());
								error.setCreateTime(new Date());
								error.setRemark("入账用户交易金额不一致");
								errorDao.save(error);
							}
						} else {
							if (l.getAmt().compareTo(order.getAmt()) != 0) {
								TaccountOrderError error = new TaccountOrderError();
								if (l.getType().equals("S")) {
									error.setType("S");
								} else {
									error.setType("T");
								}
								error.setUserID(l.getAccount().getUser().getId());
								error.setOrderAmt(order.getAmt());
								error.setAccountAmt(l.getAmt());
								error.setOrderNum(l.getOrdernum());
								error.setCreateTime(new Date());
								error.setRemark("入账用户交易金额不一致");
								errorDao.save(error);
							}
						}
					}
				} else {
					TaccountOrderError error = new TaccountOrderError();
					error.setUserID(l.getAccount().getUser().getId());
					error.setCreateTime(new Date());
					error.setRemark("金额为空");
					errorDao.save(error);
				}
			}
		}

		for (TuserOrder t : orderList) {
			String hql4 = "select t from TaccountLog t left join t.account a left join a.user u where t.type in('S','T') and t.ordernum='" + t.getOrderNum() + "'";
			TaccountLog log = accountLogDao.get(hql4);
			if (log == null) {
				if (!t.getTranPayOrder().getPayChannel().getName().equals("XINKKEZHITONGCHE") && !t.getTranPayOrder().getPayChannel().getName().equals("MINGSHENGZHITONGCHE")
						&& !t.getTranPayOrder().getPayChannel().getName().equals("YILIANZHIFU") && !t.getTranPayOrder().getPayChannel().getName().equals("XINKKEYINLIAN")
						&& !t.getTranPayOrder().getPayChannel().getName().equals("XINKEYINLIAN")) {
					TaccountOrderError error = new TaccountOrderError();
					if (t.getType() == 700) {
						error.setType("T");
					} else {
						error.setType("S");
					}
					error.setUserID(t.getUser().getId());
					error.setOrderNum(t.getOrderNum());
					error.setCreateTime(new Date());
					error.setRemark("交易订单无账户记录");
					errorDao.save(error);
				}
			} else {
				if (t.getAmt() != null && log.getAmt() != null) {
					if (t.getType() == 700) {

						if (t.getTranPayOrder().getPayChannel().getName().equals("XINKKEZHITONGCHE") || t.getTranPayOrder().getPayChannel().getName().equals("MINGSHENGZHITONGCHE")
								|| t.getTranPayOrder().getPayChannel().getName().equals("YILIANZHIFU") || t.getTranPayOrder().getPayChannel().getName().equals("XINKKEYINLIAN")
								|| t.getTranPayOrder().getPayChannel().getName().equals("XINKEYINLIAN")) {
							TaccountOrderError error = new TaccountOrderError();
							if (t.getType() == 700) {
								error.setType("T");
							} else {
								error.setType("S");
							}
							error.setUserID(t.getUser().getId());
							error.setOrderNum(t.getOrderNum());
							error.setCreateTime(new Date());
							error.setRemark("直通车交易可能涉及重复清算请重视");
							errorDao.save(error);
							smsValidateService.sendMsgValidate("17321026899", "142355", "8888", "F20160001");
							smsValidateService.sendMsgValidate("18073969176", "142355", "8888", "F20160001");
							smsValidateService.sendMsgValidate("13641717364", "142355", "8888", "F20160001");
						} else {
							BigDecimal two = new BigDecimal(2.00);
							if (t.getAmt().compareTo(log.getAmt().subtract(two)) != 0) {
								TaccountOrderError error = new TaccountOrderError();
								if (t.getType() == 700) {
									error.setType("T");
								} else {
									error.setType("S");
								}
								error.setUserID(t.getUser().getId());
								error.setAccountAmt(log.getAmt());
								error.setOrderAmt(t.getAmt());
								error.setOrderNum(t.getOrderNum());
								error.setCreateTime(new Date());
								error.setRemark("交易订单出入账金额不一致");
								errorDao.save(error);
							}
						}
					} else {
						if (t.getAmt().compareTo(log.getAmt()) != 0) {
							TaccountOrderError error = new TaccountOrderError();
							if (t.getType() == 700) {
								error.setType("T");
							} else {
								error.setType("S");
							}
							error.setUserID(t.getUser().getId());
							error.setAccountAmt(log.getAmt());
							error.setOrderAmt(t.getAmt());
							error.setOrderNum(t.getOrderNum());
							error.setCreateTime(new Date());
							error.setRemark("交易订单出入账金额不一致");
							errorDao.save(error);
						}
					}
				} else {
					TaccountOrderError error = new TaccountOrderError();
					error.setUserID(t.getUser().getId());
					error.setOrderNum(t.getOrderNum());
					error.setCreateTime(new Date());
					error.setRemark("金额为空");
					errorDao.save(error);
				}
			}
		}
	}

	@Override
	public void downloadAccount() throws Exception {
		try {
			LOG.info("-------下载账户信息 start--------");
			List<Account> ul = new ArrayList<Account>();
			List<Account> totalAccounts = new ArrayList<Account>();
			Map<String, Object> params = new HashMap<String, Object>();
			String hql = " select t from Taccount t left join t.user u where u.authenticationStatus=" + User.authentication_status.SUCCESS.getCode();
			System.out.println("下载账户信息");
			int page = 1;
			int total = 0;
			int size = 10000;
			List<Taccount> taccounts = null;
			// List<Map<String, Object>> list = new ArrayList<Map<String,
			// Object>>(1024*128);
			do {
				taccounts = accountDao.find(hql, params, page++, size);
				for (Taccount t : taccounts) {
					if (t.getUser() == null) {
						continue;
					}
					ul.add(new Account(t, "temp"));
				}
				if (ul.size() > 0) {
					// fillAccountContent(ul, list);
					totalAccounts.addAll(ul);
				}
				ul.clear();
				total = total + taccounts.size();
				LOG.info("下载账户信息 cur Page={},total={}", page, total);
			} while (taccounts != null && taccounts.size() > 0);
			LOG.info("-------下载账户信息 end--page={}---size={}---total={}", page, size, total);
			XSSFWorkbook wb = fillAccountContent(totalAccounts);
			// Workbook wb = ExcelUtil.createWorkBook(list, keys, columnNames);
			String dateStr = DateUtil.convertDateStrYYYYMMDD(new Date());
			String fileName = "account" + dateStr + ".xlsx";
			writeExcel(wb, fileName);
		} catch (Exception e) {
			LOG.error("-------下载账户信息 error:{}", e);
		}
	}

	private XSSFWorkbook fillAccountContent(List<Account> accounts) {
		if (CollectionUtils.isEmpty(accounts)) {
			return new XSSFWorkbook();
		}
		int lineNo = 0;
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("账户余额");
		XSSFRow row0 = sheet.createRow(lineNo++);// 0
		XSSFCell cell00 = row0.createCell(0);
		XSSFCell cell01 = row0.createCell(1);
		XSSFCell cell02 = row0.createCell(2);
		XSSFCell cell03 = row0.createCell(3);
		XSSFCell cell04 = row0.createCell(4);
		XSSFCell cell05 = row0.createCell(5);
		XSSFCell cell06 = row0.createCell(6);
		XSSFCell cell07 = row0.createCell(7);
		XSSFCell cell08 = row0.createCell(8);
		XSSFCell cell09 = row0.createCell(9);
		XSSFCell cell10 = row0.createCell(10);
		XSSFCell cell11 = row0.createCell(11);
		XSSFCell cell12 = row0.createCell(12);
		XSSFCell cell13 = row0.createCell(13);
		XSSFCell cell14 = row0.createCell(14);
		XSSFCell cell15 = row0.createCell(15);
		XSSFCell cell16 = row0.createCell(16);
		XSSFCell cell17 = row0.createCell(17);
		XSSFCell cell18 = row0.createCell(18);
		XSSFCell cell19 = row0.createCell(19);
		XSSFCell cell20 = row0.createCell(20);
		cell00.setCellValue(columnNames[0]);
		cell01.setCellValue(columnNames[1]);
		cell02.setCellValue(columnNames[2]);
		cell03.setCellValue(columnNames[3]);
		cell04.setCellValue(columnNames[4]);
		cell05.setCellValue(columnNames[5]);
		cell06.setCellValue(columnNames[6]);
		cell07.setCellValue(columnNames[7]);
		cell08.setCellValue(columnNames[8]);
		cell09.setCellValue(columnNames[9]);
		cell10.setCellValue(columnNames[10]);
		cell11.setCellValue(columnNames[11]);
		cell12.setCellValue(columnNames[12]);
		cell13.setCellValue(columnNames[13]);
		cell14.setCellValue(columnNames[14]);
		cell15.setCellValue(columnNames[15]);
		cell16.setCellValue(columnNames[16]);
		cell17.setCellValue(columnNames[17]);
		cell18.setCellValue(columnNames[18]);
		cell19.setCellValue(columnNames[19]);
		cell20.setCellValue(columnNames[20]);
		XSSFRow row = null;
		for (Account acc : accounts) {
			row = sheet.createRow(lineNo++);
			XSSFCell celli0 = row.createCell(0);
			XSSFCell celli1 = row.createCell(1);
			XSSFCell celli2 = row.createCell(2);
			XSSFCell celli3 = row.createCell(3);
			XSSFCell celli4 = row.createCell(4);
			XSSFCell celli5 = row.createCell(5);
			XSSFCell celli6 = row.createCell(6);
			XSSFCell celli7 = row.createCell(7);
			XSSFCell celli8 = row.createCell(8);
			XSSFCell celli9 = row.createCell(9);
			XSSFCell celli10 = row.createCell(10);
			XSSFCell celli11 = row.createCell(11);
			XSSFCell celli12 = row.createCell(12);
			XSSFCell celli13 = row.createCell(13);
			XSSFCell celli14 = row.createCell(14);
			XSSFCell celli15 = row.createCell(15);
			XSSFCell celli16 = row.createCell(16);
			XSSFCell celli17 = row.createCell(17);
			XSSFCell celli18 = row.createCell(18);
			XSSFCell celli19 = row.createCell(19);
			XSSFCell celli20 = row.createCell(20);
			celli0.setCellValue(acc.getRealName());
			celli1.setCellValue(acc.getName());
			celli2.setCellValue(acc.getLoginName());
			celli3.setCellValue(acc.getUserId());
			celli4.setCellValue(acc.getStatus());
			BigDecimal temp = acc.getAvlAmt();
			celli5.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getLockOutAmt();
			celli6.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getPerMonthInAmt();
			celli7.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getPerMonthOutAmt();
			celli8.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getThroughAmt();
			celli9.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getT1Amt();
			celli10.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getT2Amt();
			celli11.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getT3Amt();
			celli12.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getT4Amt();
			celli13.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getT5Amt();
			celli14.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getT6Amt();
			celli15.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getT7Amt();
			celli16.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getT8Amt();
			celli17.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getT9Amt();
			celli18.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getT10Amt();
			celli19.setCellValue(temp == null ? "" : temp.toString());
			temp = acc.getT11Amt();
			celli20.setCellValue(temp == null ? "" : temp.toString());
		}
		return wb;
	}

	private void writeExcel(Workbook wb, String fileName) throws Exception {
		String dateDir = DateUtil.getyyyyMMddToString();
		String fileDir = account_root_path + dateDir;
		File excelFileDir = new File(fileDir);
		if (excelFileDir.exists() == false) {
			excelFileDir.mkdirs();
		}
		String fullFileName = excelFileDir + "\\" + fileName;
		FileOutputStream fos = new FileOutputStream(fullFileName);
		wb.write(fos);
		fos.close();
		LOG.info("-------账户信息 写入成功-------");
	}

	/*
	 * private void fillAccountContent(List<Account> acounts, List<Map<String,
	 * Object>> list) {
	 * 
	 * Map<String, Object> contents = null; if (list.size() == 0) { contents =
	 * new HashMap<String, Object>(); contents.put("sheetName", "商户余额信息");
	 * list.add(contents); }
	 * 
	 * for (Account acc : acounts) { contents = new HashMap<String, Object>();
	 * contents.put(keys[0], acc.getRealName()); contents.put(keys[1],
	 * acc.getName()); contents.put(keys[2], acc.getLoginName());
	 * contents.put(keys[3], acc.getUserId()); contents.put(keys[4],
	 * acc.getStatus()); contents.put(keys[5], acc.getAvlAmt());
	 * contents.put(keys[6], acc.getLockOutAmt()); contents.put(keys[7],
	 * acc.getPerMonthInAmt()); contents.put(keys[8], acc.getPerMonthOutAmt());
	 * contents.put(keys[9], acc.getThroughAmt()); contents.put(keys[10],
	 * acc.getT1Amt()); contents.put(keys[11], acc.getT2Amt());
	 * contents.put(keys[12], acc.getT3Amt()); contents.put(keys[13],
	 * acc.getT4Amt()); contents.put(keys[14], acc.getT5Amt());
	 * contents.put(keys[15], acc.getT6Amt()); contents.put(keys[16],
	 * acc.getT7Amt()); contents.put(keys[17], acc.getT8Amt());
	 * contents.put(keys[18], acc.getT9Amt()); contents.put(keys[19],
	 * acc.getT10Amt()); contents.put(keys[20], acc.getT11Amt());
	 * list.add(contents); } }
	 */

	@Override
	public void downloadBrokerage() {
		try {
			LOG.info("-------下载佣金信息 start--------");
			List<Brokerage> ul = new ArrayList<Brokerage>();
			List<Brokerage> totalBrokerages = new ArrayList<Brokerage>();
			Map<String, Object> params = new HashMap<String, Object>();
			String hql = " select t from Tbrokerage t left join t.user u where u.authenticationStatus=" + User.authentication_status.SUCCESS.getCode();
			int page = 1;
			int total = 0;
			int size = 10000;
			List<Tbrokerage> tb = null;
			// List<Map<String, Object>> list = new ArrayList<Map<String,
			// Object>>();
			do {
				tb = brokerageDao.find(hql, params, page++, size);
				for (Tbrokerage t : tb) {
					if (t.getUser() == null) {
						continue;
					}
					ul.add(new Brokerage(t));
				}
				if (ul.size() > 0) {
					// fillBrokerageContent(ul, list);
					totalBrokerages.addAll(ul);
				}
				ul.clear();
				total = total + tb.size();
				LOG.info("下载佣金信息 cur Page={},total={}", page, total);
			} while (tb != null && tb.size() > 0);
			LOG.info("-------下载佣金信息 end--page={}---size={}---total={}", page, size, total);

			XSSFWorkbook wb = fillBrokerageContent(totalBrokerages);
			// Workbook wb = ExcelUtil.createWorkBook(list, keys2,
			// columnNames2);
			String dateStr = DateUtil.convertDateStrYYYYMMDD(new Date());
			String fileName = "brokerage" + dateStr + ".xlsx";
			writeExcel(wb, fileName);
		} catch (Exception e) {
			LOG.error("-------下载佣金信息 error:{}", e.getMessage());
		}
	}

	private XSSFWorkbook fillBrokerageContent(List<Brokerage> brokerages) {
		if (CollectionUtils.isEmpty(brokerages)) {
			return new XSSFWorkbook();
		}
		int lineNo = 0;
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("佣金余额");
		XSSFRow row0 = sheet.createRow(lineNo++);// 0
		XSSFCell cell00 = row0.createCell(0);
		XSSFCell cell01 = row0.createCell(1);
		XSSFCell cell02 = row0.createCell(2);
		XSSFCell cell03 = row0.createCell(3);
		XSSFCell cell04 = row0.createCell(4);
		XSSFCell cell05 = row0.createCell(5);
		XSSFCell cell06 = row0.createCell(6);
		XSSFCell cell07 = row0.createCell(7);
		XSSFCell cell08 = row0.createCell(8);
		XSSFCell cell09 = row0.createCell(9);
		XSSFCell cell10 = row0.createCell(10);
		XSSFCell cell11 = row0.createCell(11);
		XSSFCell cell12 = row0.createCell(12);
		cell00.setCellValue(columnNames2[0]);
		cell01.setCellValue(columnNames2[1]);
		cell02.setCellValue(columnNames2[2]);
		cell03.setCellValue(columnNames2[3]);
		cell04.setCellValue(columnNames2[4]);
		cell05.setCellValue(columnNames2[5]);
		cell06.setCellValue(columnNames2[6]);
		cell07.setCellValue(columnNames2[7]);
		cell08.setCellValue(columnNames2[8]);
		cell09.setCellValue(columnNames2[9]);
		cell10.setCellValue(columnNames2[10]);
		cell11.setCellValue(columnNames2[11]);
		cell12.setCellValue(columnNames2[12]);
		XSSFRow row = null;
		for (Brokerage bro : brokerages) {
			row = sheet.createRow(lineNo++);
			XSSFCell celli0 = row.createCell(0);
			XSSFCell celli1 = row.createCell(1);
			XSSFCell celli2 = row.createCell(2);
			XSSFCell celli3 = row.createCell(3);
			XSSFCell celli4 = row.createCell(4);
			XSSFCell celli5 = row.createCell(5);
			XSSFCell celli6 = row.createCell(6);
			XSSFCell celli7 = row.createCell(7);
			XSSFCell celli8 = row.createCell(8);
			XSSFCell celli9 = row.createCell(9);
			XSSFCell celli10 = row.createCell(10);
			XSSFCell celli11 = row.createCell(11);
			XSSFCell celli12 = row.createCell(12);

			celli0.setCellValue(bro.getUserName());
			celli1.setCellValue(bro.getUserCode());
			celli2.setCellValue(bro.getLoginName());
			celli3.setCellValue(bro.getUserId());
			celli4.setCellValue(bro.getStatus());
			celli5.setCellValue(bro.getBrokerage().toString());
			celli6.setCellValue(bro.getLockBrokerage().toString());
			celli7.setCellValue(bro.getTotalBrokerage().toString());
			celli8.setCellValue(bro.getTotalTransBrokerage().toString());
			celli9.setCellValue(bro.getTotalAgentBrokerage().toString());
			celli10.setCellValue(bro.getTotalLeadBrokerage().toString());
			celli11.setCellValue(bro.getHistoryBrokerage().toString());
			celli12.setCellValue(bro.getYesterdayBrokerage().toString());
		}
		return wb;

	}

	@Override
	public Workbook exportExcel(Account account) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * private void fillBrokerageContent(List<Brokerage> brokerages,
	 * List<Map<String, Object>> list) { Map<String, Object> contents = null; if
	 * (list.size() == 0) { contents = new HashMap<String, Object>();
	 * contents.put("sheetName", "商户分润信息"); list.add(contents); }
	 * 
	 * for (Brokerage bro : brokerages) { contents = new HashMap<String,
	 * Object>(); contents.put(keys2[0], bro.getUserName());
	 * contents.put(keys2[1], bro.getUserCode()); contents.put(keys2[2],
	 * bro.getLoginName()); contents.put(keys2[3], bro.getUserId());
	 * contents.put(keys2[4], bro.getStatus()); contents.put(keys2[5],
	 * bro.getBrokerage()); contents.put(keys2[6], bro.getLockBrokerage());
	 * contents.put(keys2[7], bro.getTotalBrokerage()); contents.put(keys2[8],
	 * bro.getTotalTransBrokerage()); contents.put(keys2[9],
	 * bro.getTotalAgentBrokerage()); contents.put(keys2[10],
	 * bro.getTotalLeadBrokerage()); contents.put(keys2[11],
	 * bro.getHistoryBrokerage()); contents.put(keys2[12],
	 * bro.getYesterdayBrokerage()); list.add(contents); } }
	 */
}
