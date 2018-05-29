package com.cn.flypay.service.task.impl;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.statement.FinanceStatementService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.CoreOperateService;
import com.cn.flypay.service.sys.HolidayService;
import com.cn.flypay.service.sys.UserChannelService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.task.UserTaskService;
import com.cn.flypay.service.trans.BrokerageService;
import com.cn.flypay.utils.DateUtil;

@Service
public class UserTaskServiceImpl implements UserTaskService {

	private Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private UserService userService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private UserChannelService userChannelService;
	@Autowired
	private BrokerageService brokerageService;
	@Autowired
	private HolidayService holidayService;
	@Autowired
	private FinanceStatementService financeStatementService;
	@Autowired
	private CoreOperateService coreOperateService;

	@Override
	public void dealUpdateUserAuthErrorNum() {
		log.info("-----清理用户登录错误和实名认证次数----begin----");
		userService.dealClearUserAuthErrorNum();
		log.info("-----清理用户登录错误和实名认证次数----end----");
	}

	@Override
	public void dealCleanPerMonthAccount() {
		log.info("-----清理用户每月账户信息----begin----");
		accountService.dealCleanPerMonthAccount();
		log.info("-----清理用户每月账户信息----end----");
	}

	@Override
	public void dealCleanChannelTodayAmt() {
		log.info("-----清理通道每天累计账户信息----begin----");
		channelService.removeTodayAmt(null);
		userChannelService.dealClearUserChannel();
		log.info("-----清理通道每天累计账户信息----end----");
	}

	@Override
	public void updateBrokerageAccount() {
		log.info("-----更新用户佣金历史信息----begin----");
		brokerageService.updateBrokerageAccount();
		log.info("-----更新用户佣金历史信息----end----");
	}

	@Override
	public void updateAllUserAccount() {
		log.info("-----更新用户账户信息----begin----");
		accountService.updateAllUserAccount();
		log.info("-----更新用户账户信息----end----");
	}

	@Override
	public void updateUserT5Account() {
		log.info("-----更新用户T5账户信息----begin----");

		Boolean flag = holidayService.isWorkDate(DateUtil.addDate(new Date(), 1));
		if (flag) {
			accountService.updateUserT5Account();
		} else {
			log.info("今天是节假日，无需更新用户T5账户");
		}
		log.info("-----更新用户T5账户信息----end----");
	}

	@Override
	public void updateUserT1AccountToAvl() {
		log.info("-----更新用户T1账户到可用金额中----begin----");
		coreOperateService.updateCoreOperateByDate(new Date(), "SYSTEM");
		coreOperateService.updateCoreOperateByDateTwo(new Date(), "SYSTEM");
		log.info("-----更新用户T1账户到可用金额中----end----");
	}

	@Override
	public void updateFinanceProfitAccount() {
		log.info("-----更新财务收益信息----begin----");
		financeStatementService.saveFinanceProfit();
		log.info("-----更新财务收益信息----end----");
	}

	@Override
	public void updateThroughChannel() {
		log.info("-----更新民生子商户信息----begin----");
		try {
			channelService.updateThroughChannel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("-----更新民生子商户信息----end----");
	}

	@Override
	public void addAccountLogProfit() {
		log.info("-----账户记录出入统计----begin----");
		accountService.addAccountLogProfit();
		log.info("-----账户记录出入统计----end----");
	}

	@Override
	public void updateThroughChannelPN() {
		log.info("-----更新平安子商户信息----begin----");
		try {
			channelService.updateThroughChannelPA();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("-----更新平安子商户信息----end----");
	}

	@Override
	public void downloadUserAccount() {
		log.info("-----下载商户余额信息----begin----");
		try {
			accountService.downloadAccount();
		} catch (Exception e) {
			// e.printStackTrace();
			log.error("-----下载商户余额信息error-{}--", e);
		}
		log.info("-----下载商户余额信息----end----");

	}

	@Override
	public void downloadUserBrokerage() {
		log.info("-----下载商户佣金信息----begin----");
		try {
			accountService.downloadBrokerage();
		} catch (Exception e) {
			// e.printStackTrace();
			log.error("-----下载商户佣金信息error-{}--", e.getMessage());
		}
		log.info("-----下载商户佣金信息----end----");

	}

}
