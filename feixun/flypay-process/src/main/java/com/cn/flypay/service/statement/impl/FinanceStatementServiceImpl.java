package com.cn.flypay.service.statement.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.sys.TfinanceProfit;
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.model.trans.TorderBonusProcess;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.account.FinanceProfit;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.FinanceStatement;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.statement.FinanceService;
import com.cn.flypay.service.statement.FinanceStatementService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.HolidayService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

@Service
public class FinanceStatementServiceImpl implements FinanceStatementService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BaseDao<Taccount> accountDao;
	@Autowired
	private BaseDao<Tbrokerage> brokerageDao;
	@Autowired
	private BaseDao<TfinanceProfit> finaceProfitDao;
	@Autowired
	private BaseDao<TorderBonusProcess> bonusProcessDao;
	@Autowired
	private BaseDao<TuserOrder> userOrderDao;
	@Autowired
	private HolidayService holidayService;
	@Autowired
	private UserOrderService userOrderService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private FinanceService financeService;
	@Value("${account_root_path}")
	private String account_root_path;

	@Override
	public List<FinanceProfit> dataGrid(FinanceProfit financeProfit, PageFilter ph) {

		List<FinanceProfit> ul = new ArrayList<FinanceProfit>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TfinanceProfit t  ";
		List<TfinanceProfit> l = finaceProfitDao.find(hql + whereHql(financeProfit, params) + orderHql(ph), params,
				ph.getPage(), ph.getRows());
		for (TfinanceProfit t : l) {
			FinanceProfit u = new FinanceProfit();
			BeanUtils.copyProperties(t, u);
			u.setErrorAmt(t.getHistoryUserAmt().subtract(t.getYesterdayUserAmt()).add(t.getTotalInputAmt())
					.subtract(t.getTotalOutAmt()));
			ul.add(u);
		}
		return ul;

	}

	@Override
	public Long count(FinanceProfit financeStatement, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "  from TfinanceProfit t  ";
		return finaceProfitDao.count("select count(t.id) " + hql + whereHql(financeStatement, params), params);
	}

	private String whereHql(FinanceProfit orderStatement, Map<String, Object> params) {
		String hql = "";
		if (orderStatement != null) {
			hql += " where 1=1 ";
			if (StringUtil.isNotBlank(orderStatement.getStatemtentDateStart())) {
				hql += " and t.statemtentDateStart >= :statementDateStart";
				params.put("statementDateStart", orderStatement.getStatemtentDateStart());
			}
			if (StringUtil.isNotBlank(orderStatement.getStatemtentDateEnd())) {
				hql += " and t.statemtentDateEnd <= :statementDateEnd";
				params.put("statementDateEnd", orderStatement.getStatemtentDateEnd());
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
	public TfinanceProfit saveFinanceProfit() {

		return saveFinanceProfit(new Date());
	}

	/**
	 * 2017.11.14启动，计算全平台，全日期的待提现额度
	 * 
	 * @param date
	 * @return
	 */
	@Override
	public TfinanceProfit saveFinanceProfitV2(Date date) {
		logger.info("-------开始记录系统总账户信息-----");
		TfinanceProfit financeProfit = new TfinanceProfit();

		return financeProfit;
	}

	@Override
	public TfinanceProfit saveFinanceProfit(Date date) {
		logger.info("-------开始记录系统总账户信息-----");
		TfinanceProfit fp = new TfinanceProfit();
		fp.setCreateTime(date);
		String sumAccount = "select SUM(history_amt) ,SUM(yesterday_amt),sum(lock_out_amt)  from account ";
		String sumBrokerage = "select SUM(history_Brokerage),SUM(yesterday_Brokerage) ,sum(LOCK_BROKERAGE) from TRANS_BROKERAGE";

		List<Object[]> accs = accountDao.findBySql(sumAccount);
		List<Object[]> bks = brokerageDao.findBySql(sumBrokerage);
		/* 记录用户账户信息 */
		writeLockAMtLog(date);
		BigDecimal historyBd = ((BigDecimal) accs.get(0)[0]).add((BigDecimal) bks.get(0)[0]);
		BigDecimal yesterdayBd = ((BigDecimal) accs.get(0)[1]).add((BigDecimal) bks.get(0)[1]);
		BigDecimal yesterdaylockBd = ((BigDecimal) accs.get(0)[2]).add((BigDecimal) bks.get(0)[2]);
		/* 获取系统系统中用户昨日账户余额 */
		fp.setYesterdayUserAmt(yesterdayBd.setScale(2, BigDecimal.ROUND_DOWN));
		/* 获取系统系统中用户前日账户余额 */
		fp.setHistoryUserAmt(historyBd.setScale(2, BigDecimal.ROUND_DOWN));
		/* 获取系统系统中用户已经锁定金额 */
		fp.setYesterdayLockAmt(yesterdaylockBd.setScale(2, BigDecimal.ROUND_DOWN));

		Date startDate = DateUtil.getHoursbyInterval(DateUtil.getBeforeDate(date, 0), -1);
		Date endDate = DateUtil.getHoursbyInterval(DateUtil.getBeforeDate(date, 0), 23);
		/* 获取T1 代付金额 */
		FinanceStatement fst1 = userOrderService.getOutFinanceAccount(startDate, endDate, 1,
				UserOrder.order_status.PROCESSING.getCode());

		fp.setStatemtentDateStart(fst1.getStatemtentDateStart());
		fp.setStatemtentDateEnd(fst1.getStatemtentDateEnd());
		/* T1代提金额 */
		fp.setYesterdayT1Amt(fst1.getTradeAmt());

		FinanceProfit fpf = financeService.findFinanceProfitByDateInterval(startDate, endDate);
		fp.setBrokerageProfit(fpf.getBrokerageProfit());
		fp.setTradeFeeAmt(fpf.getTradeFeeAmt());
		fp.setAgentFeeAmt(fpf.getAgentFeeAmt());
		fp.setDiamondNum(fpf.getDiamondNum());
		fp.setGlobNum(fpf.getGlobNum());
		fp.setTixianProfit(fpf.getTixianProfit());
		fp.setTixianT0Num(fpf.getTixianT0Num());
		fp.setTixianT1Num(fpf.getTixianT1Num());
		fp.setSysProfit(fpf.getSysProfit());

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("type", UserOrder.getCollectOrderTypes());
		params.put("status", UserOrder.order_status.SUCCESS.getCode());
		/* 总输入 */
		BigDecimal objs1 = getUserInputOrderParams(params);
		logger.info("上一日总输入： "+objs1);
		fp.setTotalInputAmt(objs1);

		/* 总输出 */
		Set<Integer> txTypes2 = new HashSet<Integer>();
		txTypes2.add(UserOrder.trans_type.XJTX.getCode());
		txTypes2.add(UserOrder.trans_type.YJTX.getCode());
		params.put("type", txTypes2);
		fp.setTotalOutAmt(getUserOutOrderParams(params));

		finaceProfitDao.save(fp);
		
		String hql = "select t from TfinanceProfit t order by id desc ";
		TfinanceProfit tfinanceProfit = finaceProfitDao.get(hql);
		if (tfinanceProfit != null) {
			 String jsonS = JSON.toJSONString(tfinanceProfit);
			 logger.info("系统代付对账数据： "+jsonS);
		}
		
		logger.info("-------结束记录系统总账户信息-----");
		return fp;
	}

	private void writeLockAMtLog(Date date) {
		String sumAccount = "select id,AVL_AMT,history_amt,yesterday_amt,lock_out_amt, T1_AMT, T2_AMT, T3_AMT, T4_AMT, T5_AMT, T6_AMT  from account ";
		String sumBrokerage = "select id,history_Brokerage,yesterday_Brokerage,LOCK_BROKERAGE from TRANS_BROKERAGE ";

		List<Object[]> accs = accountDao.findBySql(sumAccount);
		List<Object[]> bks = brokerageDao.findBySql(sumBrokerage);
		List<String> accLines = new ArrayList<>();
		accLines.add("id\t可用余额\t历史金额\t昨日金额\t锁定金额\tT1金额\tT2金额\tT3金额\tT4金额\tT5金额\tT6金额");
		for (Object[] objs : accs) {
			StringBuffer sb = new StringBuffer();
			sb.append((BigInteger) objs[0]);
			sb.append("\t");
			sb.append((BigDecimal) objs[1]);
			sb.append("\t");
			sb.append((BigDecimal) objs[2]);
			sb.append("\t");
			sb.append((BigDecimal) objs[3]);
			sb.append("\t");
			sb.append((BigDecimal) objs[4]);
			sb.append("\t");
			sb.append((BigDecimal) objs[5]);
			sb.append("\t");
			sb.append((BigDecimal) objs[5]);
			sb.append("\t");
			sb.append((BigDecimal) objs[7]);
			sb.append("\t");
			sb.append((BigDecimal) objs[8]);
			sb.append("\t");
			sb.append((BigDecimal) objs[9]);
			sb.append("\t");
			sb.append((BigDecimal) objs[10]);
			sb.append("\t");
			accLines.add(sb.toString());
		}

		List<String> brokerageLines = new ArrayList<>();
		brokerageLines.add("id\t历史金额\t昨日金额\t锁定金额");
		for (Object[] objs : bks) {
			StringBuffer sb = new StringBuffer();
			sb.append((BigInteger) objs[0]);
			sb.append("\t");
			sb.append((BigDecimal) objs[1]);
			sb.append("\t");
			sb.append((BigDecimal) objs[2]);
			sb.append("\t");
			sb.append((BigDecimal) objs[3]);
			sb.append("\t");
			brokerageLines.add(sb.toString());
		}
		try {
			FileUtils.writeLines(
					new File(account_root_path + "" + DateUtil.getDateTime("yyyyMMdd", date) + "_acc_.txt"), accLines);
			FileUtils.writeLines(
					new File(account_root_path + "" + DateUtil.getDateTime("yyyyMMdd", date) + "_brokerage_.txt"),
					brokerageLines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 商户的收入包括 支付宝微信银联在线 的收入以及分配的利润
	 * 
	 * @param params
	 * @return
	 */
	public BigDecimal getUserInputOrderParams(Map<String, Object> params) {

		BigDecimal bd = BigDecimal.ZERO;
		/* 用户账户流量总输入 */
		String sql = " select sum(r.ORG_AMT), sum(r.amt) from trans_order r  where r.STATUS=:status and r.trans_pay_type=10  and r.type in(:type) and r.create_time   BETWEEN :startDate and :endDate ";

		List<Object[]> fs = userOrderDao.findBySql(sql, params);
		/* 用户佣金总输入 1、查詢出所有訂單 2、計算分潤 */
		String sub_sql = " select r.id from trans_order r where r.STATUS=:status  and r.type in(:type) and r.create_time   BETWEEN :startDate and :endDate ";

		String sql2 = "select sum(bk.brokerage) ,count(bk.id) from trans_brokerage_detail bk left join trans_order_bonus_process p on bk.bonus_process_id=p.id where p.order_id in(%s)";

		List<Object[]> fs2 = userOrderDao.findBySql(String.format(sql2, sub_sql), params);
		if (fs.get(0)[1] != null) {
			bd = bd.add((BigDecimal) fs.get(0)[1]);
			logger.info("订单的输入量" + bd.doubleValue());
		}
		if (fs2.get(0)[0] != null) {
			bd = bd.add((BigDecimal) fs2.get(0)[0]);
			logger.info("佣金输入量" + ((BigDecimal) fs2.get(0)[0]));
		}

		Map<String, Object> param2 = new HashMap<String, Object>();
		param2.put("startDate", params.get("startDate"));
		param2.put("endDate", params.get("endDate"));
		param2.put("ztc", GlobalConstant.ZTC_LIST);
		String sql3 = " select  sum(t.ORG_AMT),sum(t.amt) from trans_order t left join tran_pay_order p on t.ID=p.ID left join sys_channel c on p.pay_channel_id=c.ID  "
				/*+ " where  c.name in ('XINKKEZHITONGCHE','MINGSHENGZHITONGCHE','YILIANZHIFU','XINKKEYINLIAN','XINKEYINLIAN','YILIANZHIFUZTC') "*/
				+ " where  c.name in ( :ztc) "//定义直通车名称全局变量，方便后续增加直通车
				+ " and t.`STATUS`='100' and t.create_time between :startDate and :endDate  ";
		List<Object[]> fs3 = userOrderDao.findBySql(sql3, param2);
		if (fs3.get(0)[1] != null) {
			logger.info("----------------直通车日切余额-------------" + fs3.get(0)[1] + "bd:" + bd);
			bd = bd.subtract((BigDecimal) fs3.get(0)[1]);
			logger.info("----------------bd:-------------" + bd);
		}
		return bd;
	}

	/**
	 * 商户账户的输出 包括 商户提现，以及商户使用钱包购买代理
	 * 
	 * @param params
	 * @return
	 */
	private BigDecimal getUserOutOrderParams(Map<String, Object> params) {

		BigDecimal bd = BigDecimal.ZERO;
		/* 账户提现 */
		Set<Integer> txTypes2 = new HashSet<Integer>();
		txTypes2.add(UserOrder.trans_type.XJTX.getCode());
		txTypes2.add(UserOrder.trans_type.YJTX.getCode());
		params.put("type", txTypes2);
		String sql = " select sum(r.ORG_AMT), sum(r.amt) from trans_order r  left join tran_pay_order p ON p.id=r.id where r.STATUS=:status  and r.type in(:type) and   p.finish_date   BETWEEN :startDate and :endDate ";
		List<Object[]> objs2 = userOrderDao.findBySql(sql, params);
		if (objs2.get(0)[0] != null) {
			bd = bd.add(((BigDecimal) objs2.get(0)[0]).setScale(2, BigDecimal.ROUND_DOWN));
			logger.info("提现输出量" + (bd));
		}

		params.put("type", UserOrder.trans_type.QBZZ.getCode());

		/* 用户佣金总输入 1、查詢出所有訂單 2、計算分潤 */

		String sql2 = "select sum(p.total_amt-bkp.sumbk), count(p.id) from trans_order_bonus_process p left join (select sum(bk.brokerage) sumbk, bk.bonus_process_id  bpid from trans_brokerage_detail bk  GROUP BY bk.bonus_process_id ) bkp on bkp.bpid=p.id left join trans_order r  ON p.order_id=r.id where r.STATUS=:status  and r.type =:type and   r.create_time   BETWEEN :startDate and :endDate ";

		List<Object[]> fs2 = bonusProcessDao.findBySql(sql2, params);

		if (fs2.get(0)[0] != null) {
			bd = bd.add(((BigDecimal) fs2.get(0)[0]).setScale(2, BigDecimal.ROUND_DOWN));
			logger.info("佣金输入万10剩余量" + (bd));
		}
		return bd;

	}

	@Override
	public BigDecimal getOrgAmtSum(Map<String, Object> params) {
		params.put("startOfDay", DateUtil.getStartOfDay(new Date()));
		params.put("endOfDay", DateUtil.getEndOfDay(new Date()));
		String sql = "SELECT SUM(t.ORG_AMT) FROM trans_order t LEFT JOIN tran_pay_order p ON t.ID=p.ID LEFT JOIN sys_channel c ON p.pay_channel_id = c.ID WHERE t.create_time>:startOfDay AND t.create_time<:endOfDay AND c.name='MINGSHENGZHITONGCHE' AND t.STATUS=100 ";
		List<Object[]> TuserOrderList = userOrderDao.findBySql(sql, params);
		BigDecimal orgAmt = new BigDecimal("0.00");
		if (TuserOrderList.get(0) != null) {
			orgAmt = (BigDecimal) TuserOrderList.get(0)[0];
		}
		return orgAmt;
	}
}
