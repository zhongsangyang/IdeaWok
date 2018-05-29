package com.cn.flypay.service.statement.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.sys.TauthenticationLog;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.TsysMsgHistory;
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.model.trans.TorderBonusProcess;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.account.AgentFinanceProfit;
import com.cn.flypay.pageModel.account.FinanceProfit;
import com.cn.flypay.pageModel.account.OrgFinanceProfit;
import com.cn.flypay.pageModel.account.PlatformChannelProfit;
import com.cn.flypay.pageModel.payment.minsheng.SMZF020;
import com.cn.flypay.pageModel.sys.Organization;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.FinanceAccount;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.statement.FinanceService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.HolidayService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.PlatformOrgConfigService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.ExcelUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.XmlMapper;
import com.cn.flypay.utils.ZipUtil;
import com.cn.flypay.utils.minsheng.MinShengMerchantInputMinShengUtil;



@Service
public class FinanceServiceImpl implements FinanceService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private BaseDao<Taccount> accountDao;
	@Autowired
	private BaseDao<Tbrokerage> brokerageDao;
	@Autowired
	private BaseDao<TsysMsgHistory> msgHistoryDao;
	@Autowired
	private BaseDao<TauthenticationLog> authLogDao;
	@Autowired
	private BaseDao<Torganization> organizationDao;
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
	private UserService userService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private SysParamService sysParamService;
	@Autowired
	private PlatformOrgConfigService platformOrgConfigService;

	@Autowired
	private DictionaryService dictionaryService;
	
	@Value("${minsheng_statement_root_path}")
	private String minsheng_statement_root_path;
	@Value("${minshengCost}")
	private String minshengCost;
	@Value("${pinganCost}")
	private String pinganCost;
	@Value("${zanshnfuCost}")
	private String zanshnfuCost;
	@Value("${shenfuCost}")
	private String shenfuCost;
	@Value("${aliPayCost}")
	private String aliPayCost;
	@Value("${weixinCost}")
	private String weixinCost;
	@Value("${minzhitongcheCost}")
	private String minzhitongcheCost;
	@Value("${pingan_statement_root_path}")
	private String pingan_statement_root_path;
	

	/**
	 * 实施总账户
	 */
	@Override
	public List<FinanceAccount> findRealTimeAccount() {
		logger.info("--  findRealTimeAccount   start ---");
		List<FinanceAccount> ul = new ArrayList<FinanceAccount>();
		String sql = "select sum(lock_out_amt),sum(AVL_AMT),sum(T1_AMT),sum(T2_AMT),sum(T3_AMT),sum(T4_AMT),sum(T5_AMT),sum(T6_AMT),sum(T7_AMT),sum(T8_AMT),sum(T9_AMT),sum(T10_AMT),sum(T11_AMT),sum(D1_AMT),sum(D2_AMT) from account ";
		String bk_sql = "select sum(BROKERAGE),sum(lock_BROKERAGE) from trans_brokerage ";

		List<Object[]> objsList = accountDao.findBySql(sql);
		List<Object[]> bksList = brokerageDao.findBySql(bk_sql);
		/* 用户账户 */
		FinanceAccount fa = new FinanceAccount();
		Object[] accounts = objsList.get(0);
		fa.setLockOutAmt((BigDecimal) accounts[0]);
		fa.setAvlAmt((BigDecimal) accounts[1]);
		fa.setT1Amt((BigDecimal) accounts[2]);
		fa.setT2Amt((BigDecimal) accounts[3]);
		fa.setT3Amt((BigDecimal) accounts[4]);
		fa.setT4Amt((BigDecimal) accounts[5]);
		fa.setT5Amt((BigDecimal) accounts[6]);
		fa.setT6Amt((BigDecimal) accounts[7]);
		fa.setT7Amt((BigDecimal) accounts[8]);
		fa.setT8Amt((BigDecimal) accounts[9]);
		fa.setT9Amt((BigDecimal) accounts[10]);
	    fa.setT10Amt((BigDecimal) accounts[11]);
	    fa.setT11Amt((BigDecimal) accounts[12]);
	    fa.setD1Amt((BigDecimal) accounts[13]);
	    fa.setD2Amt((BigDecimal) accounts[14]);
	    
		/* 用户佣金 */
		Object[] brokerages = bksList.get(0);

		fa.setAvlAmt(fa.getAvlAmt().add((BigDecimal) brokerages[0]));
		fa.setLockOutAmt(fa.getLockOutAmt().add((BigDecimal) brokerages[1]));

		ul.add(fa);
		logger.info("--  findRealTimeAccount   end ---");
		return ul;
	}

	@Override
	public FinanceProfit findFinanceProfitByDateInterval(Date startDate, Date endDate) {
		FinanceProfit fp = new FinanceProfit();
		fp.setSysDate(DateUtil.convertDateStrYYYYMMDD(new Date()));
		StringBuffer sb = new StringBuffer();
		sb.append(" select sum(NULLIF(pp.totalBrokerage,0)) agentAmt, "// 代理费
				+ "sum(NULLIF(pp.totalTradeBrokerage,0)) tradebrokerageAmt, "// 代理流量费
				+ "sum(NULLIF(pp.totalAgentBrokerage,0)) agentbrokerageAmt, "// 代理佣金费
				+ "sum(td.ORG_AMT) tradAmt, "// 交易总金额
		// T0流量费
				+ " sum(if( td.trans_pay_type =10 and (td.input_Acc_Type!=5) and td.type!=800, td.org_amt*( td.person_rate-ocl.real_rate-td.share_rate ),0)) amtT1Fee, "
				// T5流量费
				+ " sum(if( td.trans_pay_type =10 and td.input_Acc_Type=5 and td.type!=800, td.org_amt*(0-ocl.real_rate),0)) amtT5Fee, "
				// 代理流量费
				+ " sum(if( td.trans_pay_type =20 and td.type!=800, td.org_amt*(0-ocl.real_rate),0)) agentFee,"

		// 钻石代理个数
				+ " count( if( td.trans_pay_type =20 and td.ORG_AMT in(org.A_AGENT,org.A_AGENT-org.B_AGENT), true,null)) diamond , "
				// 黄金代理个数
				+ " count( if( td.trans_pay_type =20 and td.ORG_AMT=org.B_AGENT, true,null)) gold  ," + " org.NAME"

		+ "  from trans_order td  " + "  left join tran_pay_order pay on pay.id=td.id "
				+ "  left join sys_user u on td.USER_ID=u.id  "
				+ "  left join sys_user_settlement_config cfg on cfg.user_id=u.id  "
				+ "  left join sys_organization org on org.code = u.agent_id "
				+ "  left join sys_channel chl on chl.id=pay.pay_channel_id "
				+ "  left join sys_org_channel ocl on ocl.channel_id=chl.id and org.ID=ocl.org_id"
				+ "  left join( select if( trd.trans_pay_type =10 , p.total_amt-bp.totalBrokerage,0) totalTradeBrokerage, "
				+ " 									 if( trd.trans_pay_type =20 , p.total_amt-bp.totalBrokerage,0) totalAgentBrokerage, "
				+ " 									 p.total_amt-bp.totalBrokerage totalBrokerage,"
				+ " 									 p.order_id    "
				+ " 						from trans_order_bonus_process p   "
				+ " 						INNER JOIN(  "
				+ " 								select SUM(d.BROKERAGE) totalBrokerage,  "
				+ " 										 bonus_process_id  from trans_brokerage_detail d  "
				+ " 								where d.TRANS_DATETIME BETWEEN :startDate and :endDate   GROUP BY d.bonus_process_id ) bp on bp.bonus_process_id=p.id  "
				+ " 						left join trans_order trd on trd.id=p.order_id) pp on pp.order_id=td.ID "
				+ " where td.`STATUS`=100   and td.type not in(700,710)  and td.create_time  BETWEEN :startDate and :endDate  ");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		fp.setStatemtentDateStart(DateUtil.getStringFromDate(startDate, "yyyy-MM-dd HH:mm:ss"));
		fp.setStatemtentDateEnd(DateUtil.getStringFromDate(endDate, "yyyy-MM-dd HH:mm:ss"));

		List<Object[]> accs = userOrderDao.findBySql(sb.toString(), params);
		if (accs.get(0)[0] != null) {
			fp.setBrokerageProfit(((BigDecimal) accs.get(0)[0]).setScale(2, BigDecimal.ROUND_DOWN));
		} else {
			fp.setBrokerageProfit(BigDecimal.ZERO);
		}
		if (accs.get(0)[1] != null) {
			fp.setBrokerageAmt(((BigDecimal) accs.get(0)[1]).setScale(2, BigDecimal.ROUND_DOWN));
		} else {
			fp.setBrokerageAmt(BigDecimal.ZERO);
		}
		if (accs.get(0)[2] != null) {
			fp.setBrokerageAgent(((BigDecimal) accs.get(0)[2]).setScale(2, BigDecimal.ROUND_DOWN));
		} else {
			fp.setBrokerageAgent(BigDecimal.ZERO);
		}
		if (accs.get(0)[4] != null) {
			fp.setTradeT0FeeAmt(((BigDecimal) accs.get(0)[4]).setScale(2, BigDecimal.ROUND_DOWN));
		} else {
			fp.setTradeT0FeeAmt(BigDecimal.ZERO);
		}
		if (accs.get(0)[5] != null) {
			fp.setTradeT5FeeAmt(((BigDecimal) accs.get(0)[5]).setScale(2, BigDecimal.ROUND_DOWN));
		} else {
			fp.setTradeT5FeeAmt(BigDecimal.ZERO);
		}
		fp.setTradeFeeAmt(fp.getTradeT0FeeAmt().add(fp.getTradeT5FeeAmt()));

		if (accs.get(0)[6] != null) {
			fp.setAgentFeeAmt(((BigDecimal) accs.get(0)[6]).setScale(2, BigDecimal.ROUND_DOWN));
		} else {
			fp.setAgentFeeAmt(BigDecimal.ZERO);
		}

		fp.setDiamondNum(((BigInteger) accs.get(0)[7]).intValue());
		fp.setGlobNum(((BigInteger) accs.get(0)[8]).intValue());
		fp.setOrganizationName((String) accs.get(0)[9]);

		String tx_sql = " select sum(r.fee),"// 手续费
				+ " count( if( r.pay_type =0, true,null)), "// T0笔数
				+ " count( if( r.pay_type =1, true,null)) "// T1笔数
				+ " from trans_order r " + " left join tran_pay_order p ON p.id=r.id " + " where r.STATUS=100  "
				+ " and r.type in(700,710) " + " and p.finish_date   BETWEEN :startDate and :endDate ";
		List<Object[]> txs = userOrderDao.findBySql(tx_sql, params);
		if (txs.get(0)[0] != null) {
			fp.setTixianProfit(((BigDecimal) txs.get(0)[0]).setScale(2, BigDecimal.ROUND_DOWN)
					.subtract(BigDecimal.valueOf(((BigInteger) txs.get(0)[1]).intValue() * 0.3))
					.setScale(2, BigDecimal.ROUND_DOWN));
			fp.setTixianT0Num(((BigInteger) txs.get(0)[1]).longValue());
			fp.setTixianT1Num(((BigInteger) txs.get(0)[2]).longValue());
		} else {
			fp.setTixianProfit(BigDecimal.ZERO);
			fp.setTixianT0Num(0l);
			fp.setTixianT1Num(0l);
		}
		/* 系统收益= 佣金剩余+流量提成 （0.0049-0.001）+ 佣金手续费（-1）+提现手续费（2-0.3） + */
		BigDecimal sysBd = fp.getBrokerageProfit().add(fp.getTradeFeeAmt()).add(fp.getAgentFeeAmt())
				.add(fp.getTixianProfit());
		fp.setSysProfit(sysBd.setScale(2, BigDecimal.ROUND_DOWN));

		return fp;
	}

	/**
	 * 平台收益
	 * 
	 * @category 暂停使用
	 */
	@Override
	public List<PlatformChannelProfit> findPlatformChannelProfitListByDateInterval(Date startDate, Date endDate,
			User operator) {
		List<PlatformChannelProfit> ppl = new ArrayList<PlatformChannelProfit>();

		return ppl;
	}

	/**
	 * 运营商收益
	 */
	@Override
	public List<OrgFinanceProfit> findFinanceProfitListByDateInterval(Date startDate, Date endDate, User operator) {
		List<OrgFinanceProfit> fpfs = new ArrayList<OrgFinanceProfit>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);

		List<String> agentIds = new ArrayList<String>();
		if (userService.isSuperAdmin(operator.getId())) {
			List<Organization> orgs = organizationService.getOrganiztions();
			for (Organization organization : orgs) {
				agentIds.add(organization.getCode());
			}
		} else {
			agentIds.add(operator.getAgentId());
		}
		for (String agentId : agentIds) {
			List<Object[]> accs = userOrderDao.findBySql(getUserAgentProfit(agentId, false), params);
			for (Object[] acc : accs) {
				OrgFinanceProfit fp = new OrgFinanceProfit();
				fp.setStatemtentDateStart(DateUtil.getStringFromDate(startDate, "yyyy-MM-dd HH:mm:ss"));
				fp.setStatemtentDateEnd(DateUtil.getStringFromDate(endDate, "yyyy-MM-dd HH:mm:ss"));
				/* 流量佣金 */
				if (acc[1] != null) {
					fp.setBrokerageAmt(((BigDecimal) acc[1]).setScale(2, BigDecimal.ROUND_DOWN));
					fp.setBrokerageProfit(fp.getBrokerageProfit().add(fp.getBrokerageAmt()));
				}
				/* 金牌 钻石 金生钻 个数*运营商分配 */
				fp.setBrokerageAgent(BigDecimal.valueOf(
						((BigInteger) acc[13]).add(((BigInteger) acc[14])).add(((BigInteger) acc[15])).longValue()));
				fp.setBrokerageProfit(fp.getBrokerageProfit().add(fp.getBrokerageAgent()));

				if (acc[7] != null) {
					fp.setTradeT0FeeAmt(((BigDecimal) acc[7]).setScale(2, BigDecimal.ROUND_DOWN));
				}
				if (acc[9] != null) {// 升级流量通道费
					fp.setTradeAgentFeeAmt(((BigDecimal) acc[9]).setScale(2, BigDecimal.ROUND_DOWN));
				}
				/* 流量费利润中只包含T0利润，T5的成本有平台承担，运营商不负责 */
				fp.setTradeFeeProfit(fp.getTradeT0FeeAmt().add(fp.getTradeAgentFeeAmt()));

				fp.setDiamondNum(((BigInteger) acc[10]).intValue());// 钻石个数
				fp.setGlobToDiamondNum(((BigInteger) acc[11]).intValue());// 金生钻数
				fp.setGlobNum(((BigInteger) acc[12]).intValue());// 金牌个数
				fp.setOrganizationName((String) acc[16]);// 运营商名字

				List<Object[]> txs = userOrderDao.findBySql(getTixianSql(agentId, false), params);
				if (txs != null && txs.size() > 0 && txs.get(0).length > 0 && txs.get(0)[0] != null) {
					fp.setTixianT0Profit(((BigDecimal) txs.get(0)[5]).setScale(2, BigDecimal.ROUND_DOWN));
					fp.setTixianFee(((BigDecimal) txs.get(0)[9]).setScale(2, BigDecimal.ROUND_DOWN));
					// 提现总利润=用户手续费-运营商成本
					fp.setTixianProfit(fp.getTixianT0Profit().subtract(fp.getTixianFee()));
					// 垫资成本
					fp.setTixianAmtFee(((BigDecimal) txs.get(0)[11]).setScale(2, BigDecimal.ROUND_DOWN));
				}

				/* 实名认证数量 */
				List<Object[]> atAcc = authLogDao.findBySql(getAuthlogSql(agentId, false), params);
				if (atAcc != null && atAcc.size() > 0 && atAcc.get(0).length >= 6 && atAcc.get(0)[5] != null) {
					fp.setAuthFee(((BigDecimal) atAcc.get(0)[5]).setScale(2, BigDecimal.ROUND_DOWN));
				}
				// 实名认证 仅收取自动认证的手续费

				/* 短信数量 */
				List<Object[]> msgAcc = msgHistoryDao.findBySql(getMsgSql(agentId), params);
				if (msgAcc != null && msgAcc.size() > 0 && msgAcc.get(0)[3] != null) {
					fp.setMsgFee((BigDecimal) msgAcc.get(0)[3]);
				}
				/* 系统收益= 佣金收益+交易手续费收益+提现收益-垫资成本-实名认证成本-短信成本 */
				BigDecimal sysBd = fp.getBrokerageProfit().add(fp.getTradeFeeProfit()).add(fp.getTixianProfit())
						.subtract(fp.getTixianAmtFee()).subtract(fp.getAuthFee()).subtract(fp.getMsgFee());

				fp.setSysProfit(sysBd.setScale(2, BigDecimal.ROUND_DOWN));
				fpfs.add(fp);
			}
		}
		return fpfs;
	}

	/**
	 * 代理商收益
	 */
	@Override
	public List<AgentFinanceProfit> findAgentFinanceProfitListByDateInterval(Date startDate, Date endDate,
			User operator) {
		List<AgentFinanceProfit> fpfs = new ArrayList<AgentFinanceProfit>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);

		List<String> agentIds = new ArrayList<String>();
		Long org_id = null;
		if (userService.isSuperAdmin(operator.getId())) {
			List<Organization> orgs = organizationService.getOrganiztions();
			for (Organization organization : orgs) {
				agentIds.add(organization.getCode());
			}
		} else {
			agentIds.add(operator.getAgentId());
			org_id = operator.getOrganizationId();
		}
		for (String agentId : agentIds) {
			List<Object[]> accs = userOrderDao.findBySql(getUserAgentProfitForAgent(agentId, org_id), params);
			for (Object[] acc : accs) {
				AgentFinanceProfit fp = new AgentFinanceProfit();
				fp.setStatemtentDateStart(DateUtil.getStringFromDate(startDate, "yyyy-MM-dd HH:mm:ss"));
				fp.setStatemtentDateEnd(DateUtil.getStringFromDate(endDate, "yyyy-MM-dd HH:mm:ss"));
				/* 总计佣金 */
				if (acc[0] != null) {
					fp.setBrokerageProfit(((BigDecimal) acc[0]).setScale(2, BigDecimal.ROUND_DOWN));
				}
				/* 流量佣金 */
				if (acc[1] != null) {
					fp.setBrokerageAmt(((BigDecimal) acc[1]).setScale(2, BigDecimal.ROUND_DOWN));
				}
				/* 代理佣金 */
				if (acc[2] != null) {
					fp.setBrokerageAgent(((BigDecimal) acc[2]).setScale(2, BigDecimal.ROUND_DOWN));
				}
				/* 总计交易金额 */
				if (acc[3] != null) {
					fp.setTradeAmt(((BigDecimal) acc[3]).setScale(2, BigDecimal.ROUND_DOWN));
				}
				/* T0交易金额 */
				if (acc[4] != null) {
					fp.setTradeT0Amt(((BigDecimal) acc[4]).setScale(2, BigDecimal.ROUND_DOWN));
				}
				/* T5交易金额 */
				if (acc[5] != null) {
					fp.setTradeT5Amt(((BigDecimal) acc[5]).setScale(2, BigDecimal.ROUND_DOWN));
				}
				/* 用户刷卡手续费 */
				if (acc[6] != null) {
					fp.setTradeFeeProfit(((BigDecimal) acc[6]).setScale(2, BigDecimal.ROUND_DOWN));
				}
				fp.setDiamondNum(((BigInteger) acc[7]).intValue());// 钻石个数
				fp.setGlobToDiamondNum(((BigInteger) acc[8]).intValue());// 金生钻数
				fp.setGlobNum(((BigInteger) acc[9]).intValue());// 金牌个数
				fp.setOrganizationName((String) acc[10]);// 运营商名字
				fp.setAgentId((String) acc[12]);
				fp.setAlAmt(((BigDecimal) acc[13]).setScale(2, BigDecimal.ROUND_DOWN));
				fp.setWxAmt(((BigDecimal) acc[14]).setScale(2, BigDecimal.ROUND_DOWN));
				fp.setJdAmt(((BigDecimal) acc[15]).setScale(2, BigDecimal.ROUND_DOWN));
				fp.setYlzxAmt(((BigDecimal) acc[16]).setScale(2, BigDecimal.ROUND_DOWN));
				fp.setTradeT1Amt(((BigDecimal) acc[17]).setScale(2, BigDecimal.ROUND_DOWN));
				fp.setTradeD0FeeAmt(((BigDecimal) acc[18]).setScale(2, BigDecimal.ROUND_DOWN));
				fp.setTradeT1FeeAmt(((BigDecimal) acc[19]).setScale(2, BigDecimal.ROUND_DOWN));
				fp.setTradeT5FeeAmt(((BigDecimal) acc[20]).setScale(2, BigDecimal.ROUND_DOWN));
				fpfs.add(fp);
			}
			List<Object[]> txs = userOrderDao.findBySql(getTixianSql(agentId, true), params);
			for (Object[] tx : txs) {
				for (AgentFinanceProfit fp : fpfs) {
					if (fp.getAgentId().equals(tx[12])) {
						fp.setTixianT0Profit(((BigDecimal) tx[5]).setScale(2, BigDecimal.ROUND_DOWN));
						fp.setTixianT0Num(((BigInteger) tx[7]).longValue());
						fp.setTixianT1Num(((BigInteger) tx[8]).longValue());
						break;
					}
				}
			}
			/* 实名认证数量 */
			List<Object[]> atAccs = authLogDao.findBySql(getAuthlogSql(agentId, true), params);
			for (Object[] atAcc : atAccs) {
				for (AgentFinanceProfit fp : fpfs) {
					if (fp.getAgentId().equals(atAcc[6])) {
						if (atAcc.length >= 7) {
							fp.setAuthNum(((BigInteger) atAcc[2]).longValue() + ((BigInteger) atAcc[3]).longValue());
							fp.setAutomaticAuthNum(((BigInteger) atAcc[3]).longValue());
							fp.setManualAuthNum(((BigInteger) atAcc[2]).longValue());
						}
					}
				}
			}
		}
		return fpfs;
	}

	/**
	 * 导出代理商收益
	 */
	@Override
	public Workbook exportAgentProfitListByDateInterval(Date startDate, Date endDate, User operator) {
		List<AgentFinanceProfit> agps = findAgentFinanceProfitListByDateInterval(startDate, endDate, operator);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String[] keys = new String[] { "orgName0", "tradeD0Amt1", "tradeAmt", "tradeT1Amt1", "tradeT5Amt1", "tradeFee2",
				"al3", "wx4", "jd5", "ylzx6", "diamondNum7", "globToDiamondNum8", "globNum9", "tixianFee10",
				"tixianT0Num11", "tixianT1Num12", "authNum13", "automaticAuthNum14", "manualAuthNum15",
				"statemtentDateStart16", "statemtentDateEnd17", "D0Fee18", "T1Fee19", "T5Fee20" };
		String[] columnNames = new String[] { "代理商名称", "D0交易金额", "总交易金额", "T1交易金额", "T5交易金额", "用户刷卡手续费", "支付宝", "微信",
				"京东", "银联在线", "钻石个数", "金生钻", "金牌个数", "用户提现手续费", "T0提现笔数", "T1提现笔数", "认证笔数", "自动认证笔数", "人工认证笔数", "开始时间",
				"结束时间", "D0手续费", "T1手续费", "T5手续费" };
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("sheetName", "代理商利润");
		list.add(m);
		for (AgentFinanceProfit ap : agps) {
			Map<String, Object> contents = new HashMap<String, Object>();
			contents.put(keys[0], ap.getOrganizationName());
			contents.put(keys[1], ap.getTradeT0Amt());
			contents.put(keys[2], ap.getTradeAmt());
			contents.put(keys[3], ap.getTradeT1Amt());
			contents.put(keys[4], ap.getTradeT5Amt());
			contents.put(keys[5], ap.getTradeFeeProfit());
			contents.put(keys[6], ap.getAlAmt());
			contents.put(keys[7], ap.getWxAmt());
			contents.put(keys[8], ap.getJdAmt());
			contents.put(keys[9], ap.getYlzxAmt());
			contents.put(keys[10], ap.getDiamondNum());
			contents.put(keys[11], ap.getGlobToDiamondNum());
			contents.put(keys[12], ap.getGlobNum());
			contents.put(keys[13], ap.getTixianProfit());
			contents.put(keys[14], ap.getTixianT0Num());
			contents.put(keys[15], ap.getTixianT1Num());
			contents.put(keys[16], ap.getAuthNum());
			contents.put(keys[17], ap.getAutomaticAuthNum());
			contents.put(keys[18], ap.getManualAuthNum());
			contents.put(keys[19], DateUtil.getStringFromDate(startDate, "yyyy-MM-dd HH:mm:ss"));
			contents.put(keys[20], DateUtil.getStringFromDate(endDate, "yyyy-MM-dd HH:mm:ss"));
			contents.put(keys[21], ap.getTradeD0FeeAmt());
			contents.put(keys[22], ap.getTradeT1FeeAmt());
			contents.put(keys[23], ap.getTradeT5FeeAmt());
			list.add(contents);
		}
		Workbook wb = ExcelUtil.createWorkBook(list, keys, columnNames);
		return wb;
	}

	/**
	 * 平台收益
	 */
	@Override
	public Workbook exportPlatformChannelProfitListByDateInterval(Date startDate, Date endDate, User operator) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("orderTypes", UserOrder.getCollectOrderTypes());
		List<Object[]> accs = userOrderDao.findBySql(getChannelProfitSql(), params);
		params.remove("orderTypes");
		BigDecimal channelProfit = BigDecimal.ZERO;
		BigDecimal agentProfit = BigDecimal.ZERO;
		BigDecimal tixianProfit = BigDecimal.ZERO;
		BigDecimal authProfit = BigDecimal.ZERO;
		BigDecimal msgProfit = BigDecimal.ZERO;

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String[] keys = new String[] { "orgName0", "channelName1", "channelDetailName2", "tradeAmt3", "realFee4",
				"t0TradeFee5", "t5TradeFee6", "tradeProfit7", "commissionTradeFee8", "totalProfit",
				"statemtentDateStart", "statemtentDateEnd" };
		String[] columnNames = new String[] { "APP名称", "支付通道", "通道详细", "交易总金额", "真实手续费", "平台T0成本", "平台T5成本", "代理商手续费成本",
				"返佣", "通道利润(流量利润+返佣-T0-T5)", "开始时间", "结束时间" };
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("sheetName", "通道利润");
		list.add(m);
		Map<String, String> dicts = dictionaryService.comboxMap("statementType");
		for (Object[] acc : accs) {
			Map<String, Object> contents = new HashMap<String, Object>();
			contents.put(keys[0], (String) acc[0]);
			contents.put(keys[1], dicts.get((String) acc[1]));
			contents.put(keys[2], (String) acc[2]);
			contents.put(keys[3], ((BigDecimal) acc[3]).setScale(2, BigDecimal.ROUND_DOWN));
			contents.put(keys[4], ((BigDecimal) acc[4]).setScale(2, BigDecimal.ROUND_DOWN));
			contents.put(keys[5], ((BigDecimal) acc[5]).setScale(2, BigDecimal.ROUND_DOWN));
			contents.put(keys[6], ((BigDecimal) acc[6]).setScale(2, BigDecimal.ROUND_DOWN));
			// 流量
			contents.put(keys[7], ((BigDecimal) acc[7]).setScale(2, BigDecimal.ROUND_DOWN));
			// 返佣
			contents.put(keys[8], ((BigDecimal) acc[8]).setScale(2, BigDecimal.ROUND_DOWN));
			BigDecimal profit = ((BigDecimal) acc[7]).add((BigDecimal) acc[8]).subtract((BigDecimal) acc[5])
					.subtract((BigDecimal) acc[6]).setScale(2, BigDecimal.ROUND_DOWN);
			contents.put(keys[9], profit);
			contents.put(keys[10], DateUtil.getStringFromDate(startDate, "yyyy-MM-dd HH:mm:ss"));
			contents.put(keys[11], DateUtil.getStringFromDate(endDate, "yyyy-MM-dd HH:mm:ss"));
			list.add(contents);
			channelProfit = channelProfit.add(profit);
		}

		params.put("channelName", "MINSHENG");
		List<Object[]> channelT0Tixians = userOrderDao.findBySql(getT0ChannelTixianSql(), params);
		params.remove("channelName");
		BigDecimal feeRate = BigDecimal.valueOf(0.0003d);
		for (Object[] channelT0Tixian : channelT0Tixians) {
			Map<String, Object> contents = new HashMap<String, Object>();
			contents.put(keys[0], dicts.get("MINSHENG_D0_TIXIAN"));
			contents.put(keys[1], dicts.get("MINSHENG_D0_TIXIAN"));
			contents.put(keys[2], dicts.get("MINSHENG_D0_TIXIAN"));
			// 交易总金额
			contents.put(keys[3], ((BigDecimal) channelT0Tixian[0]).setScale(2, BigDecimal.ROUND_DOWN));
			// 真实手续费
			contents.put(keys[4],
					((BigDecimal) channelT0Tixian[5]).setScale(2, BigDecimal.ROUND_DOWN).toString() + "(0.2操作费)");
			// 平台T0成本
			BigDecimal feebd = ((BigDecimal) channelT0Tixian[0]).multiply(feeRate).setScale(2, BigDecimal.ROUND_DOWN);
			contents.put(keys[5], feebd.toString() + "(+0.0003提现手续费)");
			contents.put(keys[6], 0);
			// 流量
			contents.put(keys[7], 0);
			// 返佣
			contents.put(keys[8], 0);
			BigDecimal profit = BigDecimal.ZERO.subtract((BigDecimal) channelT0Tixian[5]).subtract(feebd).setScale(2,
					BigDecimal.ROUND_DOWN);
			contents.put(keys[9], profit);
			contents.put(keys[10], DateUtil.getStringFromDate(startDate, "yyyy-MM-dd HH:mm:ss"));
			contents.put(keys[11], DateUtil.getStringFromDate(endDate, "yyyy-MM-dd HH:mm:ss"));
			list.add(contents);
			channelProfit = channelProfit.add(profit);
		}
		Workbook wb = ExcelUtil.createWorkBook(list, keys, columnNames);

		m.clear();
		list.clear();
		List<Object[]> agentProfits = userOrderDao.findBySql(getUserAgentProfit(null, false), params);
		keys = new String[] { "orgName16", "aNum10", "aProfit3", "bNum11", "bProfit4", "cNum12", "cProfit5",
				"totalProfit", "statemtentDateStart", "statemtentDateEnd" };
		columnNames = new String[] { "运营商名称", "钻石个数", "钻石利润", "金生钻个数", "金生钻利润", "金牌个数", "金牌利润", "用户升级利润(钻石+金生钻+金)",
				"开始时间", "结束时间" };
		m.put("sheetName", "升级利润");
		list.add(m);
		for (Object[] ap : agentProfits) {
			Map<String, Object> contents = new HashMap<String, Object>();
			contents.put(keys[0], (String) ap[16]);
			contents.put(keys[1], ((BigInteger) ap[10]).longValue());
			contents.put(keys[2], ((BigDecimal) ap[3]).setScale(2, BigDecimal.ROUND_DOWN));
			contents.put(keys[3], ((BigInteger) ap[11]).longValue());
			contents.put(keys[4], ((BigDecimal) ap[4]).setScale(2, BigDecimal.ROUND_DOWN));
			contents.put(keys[5], ((BigInteger) ap[12]).longValue());
			contents.put(keys[6], ((BigDecimal) ap[5]).setScale(2, BigDecimal.ROUND_DOWN));
			BigDecimal profit = ((BigDecimal) ap[3]).add((BigDecimal) ap[4]).add((BigDecimal) ap[5]).setScale(2,
					BigDecimal.ROUND_DOWN);
			contents.put(keys[7], profit);

			contents.put(keys[8], DateUtil.getStringFromDate(startDate, "yyyy-MM-dd HH:mm:ss"));
			contents.put(keys[9], DateUtil.getStringFromDate(endDate, "yyyy-MM-dd HH:mm:ss"));
			list.add(contents);
			agentProfit = agentProfit.add(profit);
		}
		wb = ExcelUtil.appendSheetInWorkBook(list, keys, columnNames, wb);

		m.clear();
		list.clear();
		List<Object[]> tixians = userOrderDao.findBySql(getTixianSql(null, false), params);
		keys = new String[] { "orgName0", "tradeAmt2", "t1Amt3", "t1Amt4", "tixianNum6", "T0Num7", "t1Num8",
				"orgTixianFee9", "platTixianFee10", "platProift", "t0DianziProfit11", "tixianTotalProfit",
				"userTixianFee5", "statemtentDateStart", "statemtentDateEnd" };
		columnNames = new String[] { "运营商名称", "总交易金额", "T0交易金额", "T1交易金额", "提现总数量", "T0提现数量", "T1提现数量", "运营商提现成本",
				"平台提现成本", "平台提现利润", "运营商给平台的垫资成本", "总利润(平台提现利润+垫资利润)", "用户手续费", "开始时间", "结束时间" };
		m.put("sheetName", "提现利润");
		list.add(m);
		for (Object[] ap : tixians) {
			if (StringUtil.isNullOrEmpty(ap[0])) {
				continue;
			}
			Map<String, Object> contents = new HashMap<String, Object>();
			contents.put(keys[0], (String) ap[0]);
			contents.put(keys[1], ((BigDecimal) ap[2]).setScale(2, BigDecimal.ROUND_DOWN));
			contents.put(keys[2], ((BigDecimal) ap[3]).setScale(2, BigDecimal.ROUND_DOWN));
			contents.put(keys[3], ((BigDecimal) ap[4]).setScale(2, BigDecimal.ROUND_DOWN));
			contents.put(keys[4], ((BigInteger) ap[6]).longValue());
			contents.put(keys[5], ((BigInteger) ap[7]).longValue());
			contents.put(keys[6], ((BigInteger) ap[8]).longValue());

			contents.put(keys[7], ((BigDecimal) ap[9]).setScale(2, BigDecimal.ROUND_DOWN));
			BigDecimal platFee = (BigDecimal.valueOf((Double) ap[10])).setScale(2, BigDecimal.ROUND_DOWN);
			contents.put(keys[8], platFee);
			contents.put(keys[9], ((BigDecimal) ap[9]).subtract(platFee));
			contents.put(keys[10], ((BigDecimal) ap[11]).setScale(2, BigDecimal.ROUND_DOWN));
			BigDecimal profit = ((BigDecimal) contents.get(keys[9])).add((BigDecimal) contents.get(keys[10]))
					.setScale(2, BigDecimal.ROUND_DOWN);
			contents.put(keys[11], profit);
			contents.put(keys[12], (BigDecimal) ap[5]);
			contents.put(keys[13], DateUtil.getStringFromDate(startDate, "yyyy-MM-dd HH:mm:ss"));
			contents.put(keys[14], DateUtil.getStringFromDate(endDate, "yyyy-MM-dd HH:mm:ss"));
			list.add(contents);
			tixianProfit = tixianProfit.add(profit);
		}
		wb = ExcelUtil.appendSheetInWorkBook(list, keys, columnNames, wb);

		m.clear();
		list.clear();
		List<Object[]> ahths = userOrderDao.findBySql(getAuthlogSql(null, false), params);
		keys = new String[] { "orgName0", "tradeAmt2", "t1Amt3", "t1Amt4", "tixianNum6", "T0Num7", "t1Num8",
				"orgTixianFee9", "platTixianFee10", "platProift", "t0DianziProfit11", "statemtentDateStart",
				"statemtentDateEnd" };
		columnNames = new String[] { "运营商名称", "人工认证数量", "自动认证数量", "平台成本", "运营商成本", "认证利润(运营商-平台)", "开始时间", "结束时间" };
		m.put("sheetName", "认证利润");
		list.add(m);
		for (Object[] ap : ahths) {
			Map<String, Object> contents = new HashMap<String, Object>();
			contents.put(keys[0], (String) ap[0]);
			contents.put(keys[1], ((BigInteger) ap[2]).longValue());
			contents.put(keys[2], ((BigInteger) ap[3]).longValue());
			BigDecimal platFee = (BigDecimal.valueOf((Double) ap[4])).setScale(2, BigDecimal.ROUND_DOWN);
			contents.put(keys[3], platFee);
			contents.put(keys[4], ((BigDecimal) ap[5]).setScale(2, BigDecimal.ROUND_DOWN));
			BigDecimal profit = ((BigDecimal) ap[5]).subtract(platFee);
			contents.put(keys[5], profit);
			contents.put(keys[6], DateUtil.getStringFromDate(startDate, "yyyy-MM-dd HH:mm:ss"));
			contents.put(keys[7], DateUtil.getStringFromDate(endDate, "yyyy-MM-dd HH:mm:ss"));
			list.add(contents);
			authProfit = authProfit.add(profit);
		}
		wb = ExcelUtil.appendSheetInWorkBook(list, keys, columnNames, wb);

		m.clear();
		list.clear();
		List<Object[]> msgs = userOrderDao.findBySql(getMsgSql(null), params);
		keys = new String[] { "orgName0", "msgNum2", "orgFee3", "platFee4", "platProfit", "statemtentDateStart",
				"statemtentDateEnd" };
		columnNames = new String[] { "运营商名称", "短信数量", "运营商成本", "平台成本", "平台利润(运营商-平台)", "开始时间", "结束时间" };
		m.put("sheetName", "短信利润");
		list.add(m);
		for (Object[] ap : msgs) {
			Map<String, Object> contents = new HashMap<String, Object>();
			contents.put(keys[0], (String) ap[0]);
			contents.put(keys[1], ((BigInteger) ap[2]).longValue());
			BigDecimal orgFee = BigDecimal.ZERO;
			if (ap[3] != null) {
				orgFee = ((BigDecimal) ap[3]).setScale(2, BigDecimal.ROUND_DOWN);
			}
			BigDecimal platFee = BigDecimal.ZERO;
			if (ap[4] != null) {
				platFee = (BigDecimal.valueOf((Double) ap[4])).setScale(2, BigDecimal.ROUND_DOWN);
			}
			contents.put(keys[2], orgFee);
			contents.put(keys[3], platFee);
			BigDecimal profit = (orgFee).subtract(platFee);
			contents.put(keys[4], profit);
			contents.put(keys[5], DateUtil.getStringFromDate(startDate, "yyyy-MM-dd HH:mm:ss"));
			contents.put(keys[6], DateUtil.getStringFromDate(endDate, "yyyy-MM-dd HH:mm:ss"));
			list.add(contents);
			msgProfit = msgProfit.add(profit);
		}
		wb = ExcelUtil.appendSheetInWorkBook(list, keys, columnNames, wb);

		m.clear();
		list.clear();
		keys = new String[] { "tatal0", "channel", "tixian", "agent", "auth", "msg", "statemtentDateStart",
				"statemtentDateEnd" };
		columnNames = new String[] { "总计", "通道利润", "提现利润", "用户升级利润", "实名认证利润", "短信利润", "开始时间", "结束时间" };
		m.put("sheetName", "总计利润");
		list.add(m);
		Map<String, Object> contents = new HashMap<String, Object>();
		contents.put(keys[0], channelProfit.add(tixianProfit).add(agentProfit).add(authProfit).add(msgProfit));
		contents.put(keys[1], channelProfit);
		contents.put(keys[2], tixianProfit);
		contents.put(keys[3], agentProfit);
		contents.put(keys[4], authProfit);
		contents.put(keys[5], msgProfit);
		contents.put(keys[6], DateUtil.getStringFromDate(startDate, "yyyy-MM-dd HH:mm:ss"));
		contents.put(keys[7], DateUtil.getStringFromDate(endDate, "yyyy-MM-dd HH:mm:ss"));
		list.add(contents);
		wb = ExcelUtil.appendSheetInWorkBook(list, keys, columnNames, wb);

		m.clear();
		list.clear();
		Set<Integer> types = UserOrder.getCollectOrderTypes();
		types.add(800);
		params.put("orderTypes", types);
		List<Object[]> clAgents = userOrderDao.findBySql(getChannelAgentDetailSql(), params);
		params.remove("orderTypes");
		keys = new String[] { "orgName0", "channelName1", "channelDetailName2", "tradeAmt3", "aNum4", "aNum5", "aNum6",
				"statemtentDateStart", "statemtentDateEnd" };
		columnNames = new String[] { "APP名称", "支付通道", "通道详细", "交易总金额", "钻石个数", "金升钻个数", "金牌个数", "开始时间", "结束时间" };
		m.put("sheetName", "通道代理详细");
		list.add(m);
		for (Object[] ap : clAgents) {
			Map<String, Object> agentContents = new HashMap<String, Object>();
			agentContents.put(keys[0], (String) ap[0]);
			agentContents.put(keys[1], (String) ap[1]);
			agentContents.put(keys[2], (String) ap[2]);
			agentContents.put(keys[3], ((BigDecimal) ap[3]).setScale(2, BigDecimal.ROUND_DOWN));
			agentContents.put(keys[4], ((BigInteger) ap[4]).longValue());
			agentContents.put(keys[5], ((BigInteger) ap[5]).longValue());
			agentContents.put(keys[6], ((BigInteger) ap[6]).longValue());
			agentContents.put(keys[7], DateUtil.getStringFromDate(startDate, "yyyy-MM-dd HH:mm:ss"));
			agentContents.put(keys[8], DateUtil.getStringFromDate(endDate, "yyyy-MM-dd HH:mm:ss"));
			list.add(agentContents);
		}
		list.add(contents);
		wb = ExcelUtil.appendSheetInWorkBook(list, keys, columnNames, wb);

		return wb;
	}

	private String getChannelProfitSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("  select ");
		sb.append("  org.app_name as app名称0,");
		sb.append("  cl.name as 支付公司1,");
		sb.append("  cl.detail_name as 通道名称2,");
		sb.append("  sum(ORG_AMT) as 交易金额3,");
		sb.append("  sum(ORG_AMT*cl.real_rate) as 流量总成本4,");
		sb.append("   sum(if((r.input_Acc_Type!=5 ), r.ORG_AMT*(cl.real_rate) ,0)) as 平台T0成本5,");
		sb.append("  sum(if(r.input_Acc_Type=5, r.ORG_AMT*(cl.real_rate) ,0)) as 平台T5成本6,");
		sb.append("  sum(if((r.input_Acc_Type!=5 ),   r.ORG_AMT*(IFNULL(sol.real_rate,0)) ,0)) AS 流量利润7,");
		sb.append("  sum( r.ORG_AMT*cl.commission_rate) as 返佣8");
		sb.append("  from trans_order r ");
		sb.append("  left join tran_pay_order p on p.id=r.ID");
		sb.append("  left join sys_user u on u.id=r.user_id");
		sb.append("  left join sys_channel cl on cl.id=p.pay_channel_id");
		sb.append("  left join sys_organization org on u.agent_id=org.code");
		sb.append("  LEFT JOIN sys_org_channel sol on sol.channel_id=cl.id and org.id=sol.org_id");
		sb.append("  where r.status=100");
		sb.append("  and r.TYPE in(:orderTypes)");
		sb.append(" and r.create_time BETWEEN :startDate and :endDate");
		sb.append("  group by sol.id order by org.app_name,cl.detail_name");
		return sb.toString();
	}

	private String getT0ChannelTixianSql() {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ifnull(sum(amt+draw_fee+trade_Fee) ,0)as trade_amt,");
		sb.append(" ifnull(sum(draw_fee+trade_Fee),0) as total_fee, ");
		sb.append(" ifnull(sum(amt) ,0) as real_aml,   ");
		sb.append(" count(t.id) ,");
		sb.append(" ifnull(sum(trade_Fee),0) , ");
		sb.append(" ifnull(sum(draw_fee),0) as operate_fee ");
		sb.append(" from sys_channel_t0_tixian t  ");
		sb.append(" left join sys_channel cl on cl.id=t.chl_id ");
		sb.append(
				" where t.`status`=100 and cl.`name`=:channelName and create_date BETWEEN  :startDate and :endDate  ");
		return sb.toString();
	}

	private String getChannelAgentDetailSql() {
		StringBuffer sb = new StringBuffer();
		sb.append(" select ");
		sb.append(" org.app_name as app名称0, ");
		sb.append(" cl.name as 支付公司1, ");
		sb.append(" cl.detail_name as 通道名称2, ");
		sb.append(" sum(r.ORG_AMT) as 交易金额3, ");
		sb.append(" count( if( r.trans_pay_type =20 and r.ORG_AMT =org.A_AGENT, true,null))  as 运营商_钻石个数4 , ");
		sb.append(
				" count( if( r.trans_pay_type =20 and r.ORG_AMT=org.A_AGENT-org.B_AGENT, true,null))  as 运营商_金生钻个数5 , ");
		sb.append(" count( if(r.trans_pay_type =20 and r.ORG_AMT=org.B_AGENT, true,null))   as 运营商_金牌个数6 ");
		sb.append(" from trans_order r ");
		sb.append(" left join tran_pay_order p on p.id=r.ID ");
		sb.append(" left join sys_user u on u.id=r.user_id ");
		sb.append(" left join sys_channel cl on cl.id=p.pay_channel_id ");
		sb.append(" left join sys_organization org on u.agent_id=org.code ");
		sb.append(" LEFT JOIN sys_org_channel sol on sol.channel_id=cl.id and org.id=sol.org_id ");
		sb.append(" where r.status=100 ");
		sb.append(" and r.trans_pay_type=20 and  (r.cd_type='D' or( r.cd_type='C' and r.type=800)) ");
		sb.append("  and r.TYPE in(:orderTypes)");
		sb.append(" and r.create_time BETWEEN :startDate and :endDate");
		sb.append(" group by sol.id order by org.app_name,cl.detail_name ");
		return sb.toString();
	}

	private String getUserAgentProfitForAgent(String agentId, Long org_id) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select sum(NULLIF(pp.totalBrokerage,0))  as 佣金总剩余0, ");
		sb.append(" sum(NULLIF(pp.totalTradeBrokerage,0))  as 交易总剩余1,");
		sb.append(" sum(NULLIF(pp.totalAgentBrokerage,0))  as 升级总剩余2,");
		sb.append(" sum(td.ORG_AMT)  as 总交易金额3, ");
		sb.append(
				" sum(if( td.trans_pay_type =10 and  (td.input_Acc_Type!=5) and td.type!=800, td.org_amt ,0))  as T0交易总金额4, ");
		sb.append(" sum(if( td.trans_pay_type =10 and td.input_Acc_Type=5 , td.org_amt ,0))  as T5交易总金额5,");
		sb.append(" sum(td.fee)  as  用户刷卡手续费6,");
		sb.append(" count( if( td.trans_pay_type =20 and td.ORG_AMT =org.A_AGENT, true,null))  as 钻石个数7 ,");
		sb.append(" count( if( td.trans_pay_type =20 and td.ORG_AMT=org.A_AGENT-org.B_AGENT, true,null))  as 金生钻个数8 ,");
		sb.append(" count( if( td.trans_pay_type =20 and td.ORG_AMT=org.B_AGENT, true,null))   as 金牌个数9,");
		sb.append(" org.NAME  as 机构名称10,");
		sb.append(" org.id as 机构ID11,  ");
		sb.append(" org.code as 机构代码12, ");
		sb.append(
				" sum(if( td.trans_pay_type =10 and td.type in(200,210,220) and  (td.input_Acc_Type!=5) and td.type!=800, td.org_amt ,0))  as 支付宝13,");
		sb.append(
				" sum(if( td.trans_pay_type =10 and td.type in(300,310,320) and  (td.input_Acc_Type!=5) and td.type!=800, td.org_amt ,0))  as 微信14,");
		sb.append(
				"  sum(if( td.trans_pay_type =10 and td.type in(900,910,920) and  (td.input_Acc_Type!=5) and td.type!=800, td.org_amt ,0)) as 京东15,");
		sb.append(
				"  sum(if( td.trans_pay_type =10 and td.type =500 and  (td.input_Acc_Type!=5) and td.type!=800, td.org_amt ,0)) as 银联在线16,");
		sb.append("  sum(if( td.trans_pay_type =10 and td.input_Acc_Type=1 , td.org_amt ,0))   as T1交易总金额17,");
		sb.append("  sum(if( td.input_Acc_Type=0 , td.fee ,0))   as D0手续费18,");
		sb.append("  sum(if( td.input_Acc_Type=1 , td.fee ,0))   as T1手续费19,");
		sb.append("  sum(if( td.input_Acc_Type=5 , td.fee ,0))   as T5手续费20");
		sb.append("  from trans_order td  ");
		sb.append("  left join tran_pay_order pay on pay.id=td.id ");
		sb.append("  left join sys_user u on td.USER_ID=u.id  ");
		// 查询代理
		sb.append("  left join sys_organization org_agent on org_agent.code = u.agent_id ");
		sb.append("  left join sys_organization org on org.id = u.organization_id ");
		sb.append(
				"  left join( select if( trd.trans_pay_type =10 , p.total_amt-bp.totalBrokerage,0) totalTradeBrokerage,");
		sb.append(
				" 									 if( trd.trans_pay_type =20 , p.total_amt-bp.totalBrokerage,0) totalAgentBrokerage,");
		sb.append(" 										p.total_amt-bp.totalBrokerage totalBrokerage,");
		sb.append(" 										p.order_id    ");
		sb.append(" 						from trans_order_bonus_process p   ");
		sb.append(" 						INNER JOIN(  ");
		sb.append(" 								select SUM(d.BROKERAGE) totalBrokerage,  ");
		sb.append(" 										 bonus_process_id  from trans_brokerage_detail d  ");
		sb.append(
				" 								where d.TRANS_DATETIME BETWEEN   DATE_ADD(:startDate,INTERVAL -3 DAY)  and DATE_ADD(:endDate,INTERVAL 3 DAY)    GROUP BY d.bonus_process_id ) bp on bp.bonus_process_id=p.id  ");
		sb.append(" 						left join trans_order trd on trd.id=p.order_id) pp on pp.order_id=td.ID ");
		sb.append(" where td.`STATUS`=100    and td.type !=700   and td.type!=710  ");
		if (StringUtil.isNotBlank(agentId)) {
			sb.append("  and u.agent_id='" + agentId + "' ");
		}
		if (org_id != null) {
			sb.append(" and u.ORGANIZATION_ID in (select id from sys_organization where FIND_IN_SET(id, getChildOrg("
					+ org_id + ")))");
		}
		sb.append("	 and (td.cd_type='D' or( td.cd_type='C' and td.type=800))  ");
		sb.append("   and td.create_time  BETWEEN :startDate and :endDate  group by org.id ");
		return sb.toString();
	}

	private String getUserAgentProfit(String agentId, Boolean isAgent) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select sum(NULLIF(pp.totalBrokerage,0))  as 佣金总剩余0, ");
		sb.append(" sum(NULLIF(pp.totalTradeBrokerage,0))  as 运营商_交易总剩余1,");
		sb.append(" sum(NULLIF(pp.totalAgentBrokerage,0))  as 升级总剩余2,");
		sb.append(
				" sum(if(td.trans_pay_type =20 and org.diamond_fee!=-1 and org.A_AGENT=td.ORG_AMT ,pp.totalAgentBrokerage-org.diamond_fee,0)) as 平台_钻石剩余减去运营商3, ");
		sb.append(
				" sum(if(td.trans_pay_type =20 and org.diamond_fee!=-1 and org.A_AGENT-org.B_AGENT=td.ORG_AMT, NULLIF(pp.totalAgentBrokerage,0)-org.diamond_fee+org.gold_fee,0)) as 平台_金生钻剩余减去运营商4, ");
		sb.append(
				" sum(if(td.trans_pay_type =20 and org.diamond_fee!=-1 and org.B_AGENT=td.ORG_AMT, NULLIF(pp.totalAgentBrokerage,0)-org.gold_fee,0) ) as 平台_金剩余减去运营商5, ");
		sb.append(" sum(td.ORG_AMT)  as 总交易金额6, ");
		sb.append(
				" sum(if( td.trans_pay_type =10 and  (td.input_Acc_Type!=5) and td.type!=800, td.org_amt*( td.person_rate-ocl.real_rate)-td.amt*td.share_rate ,0))  as 运营商_T1流量利润7, ");
		sb.append(
				" sum(if( td.trans_pay_type =10 and td.input_Acc_Type=5 , td.org_amt*(0-ocl.real_rate),0))  as 运营商_T5流量成本8,");
		sb.append(" sum(if( td.trans_pay_type =20 and td.type!=800, td.org_amt*(0-ocl.real_rate),0))  as 运营商_升级流量成本9,");
		sb.append(" count( if( td.trans_pay_type =20 and td.ORG_AMT =org.A_AGENT, true,null))  as 运营商_钻石个数10 ,");
		sb.append(
				" count( if( td.trans_pay_type =20 and td.ORG_AMT=org.A_AGENT-org.B_AGENT, true,null))  as 运营商_金生钻个数11 ,");
		sb.append(" count( if( td.trans_pay_type =20 and td.ORG_AMT=org.B_AGENT, true,null))   as 运营商_金牌个数12,");

		sb.append(
				" if(org.diamond_fee=-1,count( if( td.trans_pay_type =20 and td.ORG_AMT =org.A_AGENT, true,null))*org.A_AGENT,count( if( td.trans_pay_type =20 and td.ORG_AMT =org.A_AGENT, true,null))*org.diamond_fee)  as 运营商_钻石利润13 , ");
		sb.append(
				" if(org.diamond_fee=-1,count( if( td.trans_pay_type =20 and td.ORG_AMT=org.A_AGENT-org.B_AGENT, true,null))*(org.A_AGENT-org.B_AGENT),count( if( td.trans_pay_type =20 and td.ORG_AMT=org.A_AGENT-org.B_AGENT, true,null))*(org.diamond_fee-org.gold_fee) ) as 运营商_金生钻利润14 ,");
		sb.append(
				" if(org.gold_fee=-1,count( if( td.trans_pay_type =20 and td.ORG_AMT=org.B_AGENT, true,null))*org.B_AGENT ,count( if( td.trans_pay_type =20 and td.ORG_AMT=org.B_AGENT, true,null))*org.gold_fee)  as 运营商_金牌利润15,");

		sb.append(" org.NAME  as 机构名称16,");
		sb.append(" org.id as 机构ID17,  ");
		sb.append(" org.code as 机构代码18");
		sb.append("  from trans_order td  ");
		sb.append("  left join tran_pay_order pay on pay.id=td.id ");
		sb.append("  left join sys_user u on td.USER_ID=u.id  ");
		sb.append("  left join sys_user_settlement_config cfg on cfg.user_id=u.id  ");
		if (!isAgent) {
			// 查询OEM
			sb.append("  left join sys_organization org on org.code = u.agent_id ");
		} else {
			// 查询代理
			sb.append("  left join sys_organization org on org.id = u.organization_id ");
		}
		sb.append("  left join sys_channel chl on chl.id=pay.pay_channel_id ");
		sb.append("  left join sys_org_channel ocl on ocl.channel_id=chl.id and org.ID=ocl.org_id");
		sb.append(
				"  left join( select if( trd.trans_pay_type =10 , p.total_amt-bp.totalBrokerage,0) totalTradeBrokerage,");
		sb.append(
				" 									 if( trd.trans_pay_type =20 , p.total_amt-bp.totalBrokerage,0) totalAgentBrokerage,");
		sb.append(" 										p.total_amt-bp.totalBrokerage totalBrokerage,");
		sb.append(" 										p.order_id    ");
		sb.append(" 						from trans_order_bonus_process p   ");
		sb.append(" 						INNER JOIN(  ");
		sb.append(" 								select SUM(d.BROKERAGE) totalBrokerage,  ");
		sb.append(" 										 bonus_process_id  from trans_brokerage_detail d  ");
		sb.append(
				" 								where d.TRANS_DATETIME BETWEEN   DATE_ADD(:startDate,INTERVAL -3 DAY)  and DATE_ADD(:endDate,INTERVAL 3 DAY)    GROUP BY d.bonus_process_id ) bp on bp.bonus_process_id=p.id  ");
		sb.append(" 						left join trans_order trd on trd.id=p.order_id) pp on pp.order_id=td.ID ");
		sb.append(" where td.`STATUS`=100    and td.type !=700   and td.type!=710  ");
		if (StringUtil.isNotBlank(agentId)) {
			sb.append("  and u.agent_id='" + agentId + "' ");
		}
		sb.append("	 and (td.cd_type='D' or( td.cd_type='C' and td.type=800))  ");
		sb.append("   and td.create_time  BETWEEN :startDate and :endDate  group by org.id ");

		return sb.toString();
	}

	private String getTixianSql(String agentId, Boolean isAgent) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select   ");
		sb.append(" org.NAME AS 运营商名称0, ");
		sb.append(" org.id as 运营商ID1,   ");
		sb.append(" sum(r.ORG_AMT) as 总交易金额2, ");
		sb.append(" sum(if( r.pay_type=0, r.ORG_AMT ,0)) as T0交易金额3, ");
		sb.append(" sum(if( r.pay_type=1, r.ORG_AMT ,0)) as T1交易金额4, ");
		sb.append(" sum(r.fee) as 用户手续费5, ");
		sb.append(" count(r.id) as 提现数量6, ");
		sb.append(" count( if( r.pay_type=0  , true,null))  as T0提现数量7, ");
		sb.append(" count( if( r.pay_type=1  , true,null))  as T1提现数量8, ");
		sb.append(" count(r.id)* pc.platform_Tixian_Fee as 运营商成本9 , ");
		sb.append(
				" count(r.id)*((select PARA_VALUE from sys_system_parameter where PARA_NAME='platform_tixian_fee')+0) as 平台成本10  , ");
		sb.append(" sum(if( r.pay_type=0, r.ORG_AMT*pc.platform_T0_Tixian_rate ,0)) as 运营商给平台的垫资成本11, ");
		sb.append(" org.code as 运营商编码12 ");
		sb.append(" from trans_order r  ");
		sb.append(" left join tran_pay_order p on p.id=r.ID ");
		sb.append(" left join sys_user u on u.id=r.user_id ");
		if (!isAgent) {
			sb.append(" left join sys_organization org on u.agent_id=org.code ");
		} else {
			sb.append(" left join sys_organization org on u.organization_id=org.id ");
		}
		sb.append(" left join platform_org_config pc on pc.org_id=org.ID ");
		sb.append(" where r.status=100 ");
		if (StringUtil.isNotBlank(agentId)) {
			sb.append("  and u.agent_id='" + agentId + "' ");
		}
		sb.append(" and r.TYPE in(700,710) ");
		sb.append(" and r.create_time   BETWEEN :startDate and :endDate  ");
		sb.append(" group BY org.id");

		return sb.toString();
	}

	private String getAuthlogSql(String agentId, Boolean isAgent) {
		StringBuffer sb = new StringBuffer();
		sb.append("  select  ");
		sb.append(" 	org.NAME AS 运营商名称0, ");
		sb.append(" 	org.id as 运营商ID1, ");
		sb.append(" 	count( if( g.auth_type =1 , true,null)) AS 人工认证数量2, ");
		sb.append("  count( if( g.auth_type =0 , true,null)) AS 自动认证数量3, ");
		sb.append(
				"  count( if( g.auth_type =0 , true,null))*((select PARA_VALUE from sys_system_parameter where PARA_NAME='platform_authentication_fee')+0) AS 平台成本4, ");
		sb.append("  count( if( g.auth_type =0 , true,null))*(poc.platform_Authentication_Fee) AS 运营商成本5 , ");
		sb.append("  org.code AS 运营商编码6 ");
		sb.append("  from sys_authentication_log g ");
		sb.append("  left join sys_user u on u.id=g.user_id ");
		if (!isAgent) {
			sb.append("  left join sys_organization org on org.code=u.agent_id ");
		} else {
			sb.append("  left join sys_organization org on org.id=u.organization_id ");
		}
		sb.append("  left join platform_org_config poc on poc.org_id=org.id ");
		sb.append("  where 1=1 ");
		if (StringUtil.isNotBlank(agentId)) {
			sb.append("  and u.agent_id='" + agentId + "' ");
		}
		sb.append("  and g.create_time   BETWEEN :startDate and :endDate  ");
		sb.append(" GROUP BY org.id ");
		return sb.toString();
	}

	@Override
	public Workbook getMinShendowoan(String statemtentDate) throws Exception{
			String date = DateUtil.DateString(statemtentDate);
			SMZF020 f020 = new SMZF020();
			f020.setSettleDate(date);
			f020.setFileType("1");
			f020.setCooperator("SMZF_SHFF_HD_T0");
			String responseStr = MinShengMerchantInputMinShengUtil.doPost(f020);
			Map<String, String> response = XmlMapper.xml2Map(responseStr);
			String statementContent = response.get("content");
			String filePath = "";
			if (StringUtil.isNotBlank(statementContent)) {
				File file = new File(minsheng_statement_root_path);
				if (!file.exists()) {
					file.mkdirs();
				 }
			filePath = minsheng_statement_root_path + File.separator + "SMZF_SHFF_HD_T0" + "_" + date + ".txt";
			FileUtils.writeStringToFile(new File(filePath), statementContent, "utf-8");
			}
			
			Double AMTA = 0d;
			Double AMTB = 0d;
			Double AMTC = 0d;
			
			List<String> rows = FileUtils.readLines(new File(filePath),"GBK");
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			String[] keys = new String[] { "merchantCode", "amount", "fee", "settleDate", "respType", "respCode", "respMsg","dateTime","status" };
			String[] columnNames = new String[] { "子商户", "交易金额", "交易手续费", "结算时间", "成功标识", "响应码 ", "响应描述","交易时间","系统状态"};
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("sheetName", date+"民生普通对账单");
			list.add(m);
			for (String row : rows) {
				if (row.startsWith("#")) {
					continue;
				}
				String[] cls = row.split("\\|");
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("orderNum", cls[3]);
				String hql = " select t from TuserOrder t left join t.tranPayOrder p  left join t.user u left join p.payChannel c where t.orderNum=:orderNum";
				TuserOrder t = userOrderDao.get(hql, params);
				if(t.getTranPayOrder().getPayChannel().getName().equals("MINSHENG")){
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("merchantCode", cls[1]);
					map.put("amount", cls[4]);
					map.put("fee", cls[11]);
					map.put("settleDate", cls[5]);
					map.put("respType", cls[6]);
					map.put("respCode", cls[7]);
					map.put("respMsg", cls[8]);
					map.put("dateTime", t.getCreateTime());
					if(t.getStatus()==100){
						map.put("status", "成功");
					}else{
						map.put("status", "待处理");
					}
					list.add(map);
					AMTB = AMTB + + Double.parseDouble(cls[4]);
				}
				AMTA = AMTA + + Double.parseDouble(cls[4]);
			}
			Workbook wb = ExcelUtil.createWorkBook(list, keys, columnNames);
			m.clear();
			list.clear();
			
			
			keys = new String[] { "merchantCode", "reqMsgId", "amount", "fee", "settleDate", "respType", "respCode", "respMsg","dateTime","status" };
			columnNames = new String[] { "子商户", "订单号", "交易金额", "交易手续费", "结算时间", "成功标识", "响应码 ", "响应描述","交易时间","系统状态"};
			m.put("sheetName", date+"直通车对账单");
			list.add(m);
			for (String row : rows) {
				if (row.startsWith("#")) {
					continue;
				}
				String[] cls = row.split("\\|");
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("orderNum", cls[3]);
				String hql = " select t from TuserOrder t left join t.tranPayOrder p  left join t.user u left join p.payChannel c where t.orderNum=:orderNum";
				TuserOrder t = userOrderDao.get(hql, params);
				if(t.getTranPayOrder().getPayChannel().getName().equals("MINGSHENGZHITONGCHE")){
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("merchantCode", cls[1]);
					map.put("reqMsgId", cls[3]);
					map.put("amount", cls[4]);
					map.put("fee", cls[11]);
					map.put("settleDate", cls[5]);
					map.put("respType", cls[6]);
					map.put("respCode", cls[7]);
					map.put("respMsg", cls[8]);
					map.put("dateTime", t.getCreateTime());
					if(t.getStatus()==100){
						map.put("status", "成功");
					}else{
						map.put("status", "待处理");
					}
					list.add(map);
					AMTC = AMTC + + Double.parseDouble(cls[4]);
				}
			}
		  wb = ExcelUtil.appendSheetInWorkBook(list, keys, columnNames, wb);
		  m.clear();
		  list.clear();
		
		
		  keys = new String[] { "zhangSum", "zhangPu", "zhangZhu" };
		  columnNames = new String[] { "对账单总金额", "普通收款总金额", "直通车收款总金额"};
		  m.put("sheetName", date+"对账单汇总");
		  list.add(m);
		  Map<String, Object> map = new HashMap<String, Object>();
		  map.put("zhangSum", String.valueOf(AMTA));
		  map.put("zhangPu", String.valueOf(AMTB));
		  map.put("zhangZhu", String.valueOf(AMTC));
		  list.add(map);
		  wb = ExcelUtil.appendSheetInWorkBook(list, keys, columnNames, wb);
		  return wb;
	}

	private String getMsgSql(String agentId) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select  ");
		sb.append(" g.NAME AS 运营商名称0, ");
		sb.append(" g.id as 运营商ID1, ");
		sb.append(" count(h.id) as 数量2,  ");
		sb.append(" count(h.id)*(poc.platform_Message_Fee) as 运营商成本3, ");
		sb.append(
				" count(h.id)*((select PARA_VALUE from sys_system_parameter where PARA_NAME='platform_message_fee')+0) as 平台成本4 ");
		sb.append("  from sys_msg_history h   ");
		sb.append(" left join sys_organization g on g.`CODE`=h.agent_code  	 ");
		sb.append(" left join platform_org_config poc on poc.org_id=g.id   ");
		sb.append(" where  1=1 ");
		if (StringUtil.isNotBlank(agentId)) {
			sb.append("  and h.agent_code='" + agentId + "' ");
		}
		sb.append(" and h.create_time    BETWEEN :startDate and :endDate  ");
		sb.append(" GROUP BY g.id ");
		return sb.toString();
	}

	@Override
	public Workbook getShenFudowoan(String statemtentDate) throws Exception {
		String date = DateUtil.DateString(statemtentDate);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", date);
		params.put("endDate", DateUtil.DateJ(date));
		String sb = "select u.ID,u.real_name,u.LOGIN_NAME,t.ORG_AMT,t.amt,t.fee,t.ORDER_NUM,t.create_time,p.finish_date,p.error_code,p.error_info from trans_order t  "
				  + "left join tran_pay_order p on t.ID=p.ID left join sys_channel c on p.pay_channel_id = c.ID left join sys_user u on t.USER_ID = u.ID  "
				  + "where t.`STATUS`='100'  and c.name='SHENFU' and t.create_time BETWEEN :startDate and :endDate  ";
		List<Object[]> accs = userOrderDao.findBySql(sb.toString(), params);
		
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String[] keys = new String[] { "userId", "real_name", "LOGIN_NAME", "ORG_AMT", "amt", "fee", "ORDER_NUM", "create_time",
				 "finish_date","error_code","error_info"};
		String[] columnNames = new String[] { "用户ID", "用户姓名", "用户登录名", "交易金额", "到账金额", "手续费", "订单号 ", "创建时间",
				  "结算时间","响应码","响应信息"};
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("sheetName", date+"申付对账单");
		list.add(m);
		
        for (Object[] objects : accs) {
        	Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", objects[0]);
			map.put("real_name", objects[1]);
			map.put("LOGIN_NAME", objects[2]);
			map.put("ORG_AMT", objects[3]);
			map.put("amt", objects[4]);
			map.put("fee", objects[5]);
			map.put("ORDER_NUM", objects[6]);
			map.put("create_time", objects[7]);
			map.put("finish_date", objects[8]);
			map.put("error_code", objects[9]);
			map.put("error_info", objects[10]);
			list.add(map);
		}
        Workbook wb = ExcelUtil.createWorkBook(list, keys, columnNames);
		return wb;
	}

	@Override
	public Workbook exportDailyProfitDowoan(String statemtentDate) throws Exception {
		String date = DateUtil.DateString(statemtentDate);
		
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String[] keys = new String[] { "type","totalAmount", "totalPayAmount", "D0amount", "T1amount", "T5amount","T8amount", "totalCharge", "totalBrokerageAmount", "totalCost","totalProfit" };
		String[] columnNames = new String[] { "类型","总交易金额", "总支付金额", "D0交易金额", "T1交易金额", "T5交易金额","T8交易金额", "总手续费", "总分佣金额 ", "总成本","总盈利"};
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("sheetName", date+"每日盈利");
		list.add(m);
		
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", date);
		params.put("endDate", DateUtil.DateJ(date));

		
		
		String hql3 = "select sum(t.ORG_AMT) as orgAmt,sum(t.amt) as amt,sum(t.fee) as fee from trans_order t "
				+ "left join tran_pay_order p on t.ID = p.ID left join sys_channel c on p.pay_channel_id = c.ID "
				+ "where t.`TYPE` not in (700,710) and t.`STATUS`='100' and t.trans_pay_type='10' "
				+ "and t.create_time BETWEEN :startDate and :endDate and c.name=:name ";
		String hql5 = "select sum(bk.brokerage) ,count(bk.id) from trans_brokerage_detail bk left join trans_order_bonus_process p "
				+ "on bk.bonus_process_id=p.id where p.order_id in( "
				+ "select t.ID from trans_order t left join tran_pay_order p on t.ID = p.ID left join sys_channel c "
				+ "on p.pay_channel_id = c.ID where t.`TYPE` not in (700,710) and t.`STATUS`='100' and t.trans_pay_type='10' "
				+ "and t.create_time BETWEEN :startDate and :endDate and c.name=:name ) ";
		 

		
		params.put("name", "MINSHENG");
		List<Object[]> accsMin = userOrderDao.findBySql(hql3,params);
		List<Object[]> accsMinD0 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(0,10) ",params);
		List<Object[]> accsMinT1 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(1,11) ",params);
		List<Object[]> accsMinT5 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(5) ",params);
		List<Object[]> accsMinT8 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(8) ",params);
		List<Object[]> accsMin6 = userOrderDao.findBySql(hql5,params);
		Map<String, Object> mapMIN = new HashMap<String, Object>();
		mapMIN.put("type","民生交易统计");
		mapMIN.put("totalAmount", accsMin.get(0)[0]);
		mapMIN.put("totalPayAmount", accsMin.get(0)[1]);
		mapMIN.put("D0amount", accsMinD0.get(0)[0]);
		mapMIN.put("T1amount", accsMinT1.get(0)[0]);
		mapMIN.put("T5amount", accsMinT5.get(0)[0]);
		mapMIN.put("T8amount", accsMinT8.get(0)[0]);
		mapMIN.put("totalCharge", accsMin.get(0)[2]);
		mapMIN.put("totalBrokerageAmount", accsMin6.get(0)[0]);
		BigDecimal minAcc = BigDecimal.ZERO;
		BigDecimal minfee = BigDecimal.ZERO;
		if(accsMin.get(0)[0]!=null){
			minAcc=new BigDecimal(String.valueOf(accsMin.get(0)[0])); 
		}
		if(accsMin.get(0)[2]!=null){
			minfee=new BigDecimal(String.valueOf(accsMin.get(0)[2])); 
		}
		BigDecimal minRate=new BigDecimal(minshengCost); 
		BigDecimal totalCost = minAcc.multiply(minRate);
		BigDecimal totalProfit = minfee.subtract(totalCost);
		mapMIN.put("totalCost", totalCost);
		mapMIN.put("totalProfit", totalProfit);
		list.add(mapMIN);

		
		params.put("name", "PINGANPAY");
		List<Object[]> accsPin = userOrderDao.findBySql(hql3,params);
		List<Object[]> accsPinD0 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(0,10) ",params);
		List<Object[]> accsPinT1 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(1,11) ",params);
		List<Object[]> accsPinT5 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(5) ",params);
		List<Object[]> accsPinT8 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(8) ",params);
		List<Object[]> aaccsPin6 = userOrderDao.findBySql(hql5,params);
		Map<String, Object> mapPIN = new HashMap<String, Object>();
		mapPIN.put("type","平安交易统计");
		mapPIN.put("totalAmount", accsPin.get(0)[0]);
		mapPIN.put("totalPayAmount", accsPin.get(0)[1]);
		mapPIN.put("D0amount", accsPinD0.get(0)[0]);
		mapPIN.put("T1amount", accsPinT1.get(0)[0]);
		mapPIN.put("T5amount", accsPinT5.get(0)[0]);
		mapPIN.put("T8amount", accsPinT8.get(0)[0]);
		mapPIN.put("totalCharge", accsPin.get(0)[2]);
		mapPIN.put("totalBrokerageAmount", aaccsPin6.get(0)[0]);
		BigDecimal minAccPIN = BigDecimal.ZERO;
		BigDecimal minfeePIN = BigDecimal.ZERO;
		if(accsPin.get(0)[0]!=null){
			minAccPIN=new BigDecimal(String.valueOf(accsPin.get(0)[0])); 
		}
		if(accsPin.get(0)[2]!=null){
			minfeePIN=new BigDecimal(String.valueOf(accsPin.get(0)[2])); 
		}
		BigDecimal minRatePIN=new BigDecimal(pinganCost); 
		BigDecimal totalCostPIN = minAccPIN.multiply(minRatePIN);
		BigDecimal totalProfitPIN = minfeePIN.subtract(totalCostPIN);
		mapPIN.put("totalCost", totalCostPIN);
		mapPIN.put("totalProfit", totalProfitPIN);
		list.add(mapPIN);

		
		params.put("name", "MINGSHENGZHITONGCHE");
		List<Object[]> accsMinZ = userOrderDao.findBySql(hql3,params);
		List<Object[]> accsMinZD0 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(0,10) ",params);
		List<Object[]> accsMinZT1 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(1,11) ",params);
		List<Object[]> accsMinZT5 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(5) ",params);
		List<Object[]> accsMinZT8 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(8) ",params);
		List<Object[]> accsMinZPin6 = userOrderDao.findBySql(hql5,params);
		Map<String, Object> mapMinZ = new HashMap<String, Object>();
		mapMinZ.put("type","直通车交易统计");
		mapMinZ.put("totalAmount", accsMinZ.get(0)[0]);
		mapMinZ.put("totalPayAmount", accsMinZ.get(0)[1]);
		mapMinZ.put("D0amount", accsMinZD0.get(0)[0]);
		mapMinZ.put("T1amount", accsMinZT1.get(0)[0]);
		mapMinZ.put("T5amount", accsMinZT5.get(0)[0]);
		mapMinZ.put("T8amount", accsMinZT8.get(0)[0]);
		mapMinZ.put("totalCharge", accsMinZ.get(0)[2]);
		mapMinZ.put("totalBrokerageAmount", accsMinZPin6.get(0)[0]);
		BigDecimal minAccMinZ = BigDecimal.ZERO;
		BigDecimal minfeeMinZ = BigDecimal.ZERO;
		if(accsMinZ.get(0)[0]!=null){
			minAccMinZ=new BigDecimal(String.valueOf(accsMinZ.get(0)[0])); 
		}
		if(accsMinZ.get(0)[2]!=null){
			minfeeMinZ=new BigDecimal(String.valueOf(accsMinZ.get(0)[2])); 
		}
		BigDecimal minRateMinZ=new BigDecimal(minzhitongcheCost); 
		BigDecimal totalCostMinZ = minAccMinZ.multiply(minRateMinZ);
		BigDecimal totalProfitMinZ = minfeeMinZ.subtract(totalCostMinZ);
		mapMinZ.put("totalCost", totalCostMinZ);
		mapMinZ.put("totalProfit", totalProfitMinZ);
		list.add(mapMinZ);
		
		
		params.put("name", "ZANSHANFU");
		List<Object[]> accsZANF = userOrderDao.findBySql(hql3,params);
		List<Object[]> accsZANFD0 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(0,10) ",params);
		List<Object[]> accsZANFT1 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(1,11) ",params);
		List<Object[]> accsZANFT5 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(5) ",params);
		List<Object[]> accsZANFT8 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(8) ",params);
		List<Object[]> accsZANFPin6 = userOrderDao.findBySql(hql5,params);
		Map<String, Object> mapZANF = new HashMap<String, Object>();
		mapZANF.put("type","攒善银联交易统计");
		mapZANF.put("totalAmount", accsZANF.get(0)[0]);
		mapZANF.put("totalPayAmount", accsZANF.get(0)[1]);
		mapZANF.put("D0amount", accsZANFD0.get(0)[0]);
		mapZANF.put("T1amount", accsZANFT1.get(0)[0]);
		mapZANF.put("T5amount", accsZANFT5.get(0)[0]);
		mapZANF.put("T8amount", accsZANFT8.get(0)[0]);
		mapZANF.put("totalCharge", accsZANF.get(0)[2]);
		mapZANF.put("totalBrokerageAmount", accsZANFPin6.get(0)[0]);
		BigDecimal minAccZANFZ = BigDecimal.ZERO;
		BigDecimal minfeeZANF = BigDecimal.ZERO;
		if(accsZANF.get(0)[0]!=null){
			minAccZANFZ=new BigDecimal(String.valueOf(accsZANF.get(0)[0])); 
		}
		if(accsZANF.get(0)[2]!=null){
			minfeeZANF=new BigDecimal(String.valueOf(accsZANF.get(0)[2])); 
		}
		BigDecimal minRateZANF=new BigDecimal(zanshnfuCost); 
		BigDecimal totalCostZANF = minAccZANFZ.multiply(minRateZANF);
		BigDecimal totalProfitZANF = minfeeZANF.subtract(totalCostZANF);
		mapZANF.put("totalCost", totalCostZANF);
		mapZANF.put("totalProfit", totalProfitZANF);
		list.add(mapZANF);
		
		
		params.put("name", "SHENFU");
		List<Object[]> accsSHENF = userOrderDao.findBySql(hql3,params);
		List<Object[]> accsSHENFD0 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(0,10) ",params);
		List<Object[]> accsSHENFT1 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(1,11) ",params);
		List<Object[]> accsSHENFT5 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(5) ",params);
		List<Object[]> accsSHENFT8 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(8) ",params);
		List<Object[]> accsSHENFPin6 = userOrderDao.findBySql(hql5,params);
		Map<String, Object> mapSHENF = new HashMap<String, Object>();
		mapSHENF.put("type","申付银联交易统计");
		mapSHENF.put("totalAmount", accsSHENF.get(0)[0]);
		mapSHENF.put("totalPayAmount", accsSHENF.get(0)[1]);
		mapSHENF.put("D0amount", accsSHENFD0.get(0)[0]);
		mapSHENF.put("T1amount", accsSHENFT1.get(0)[0]);
		mapSHENF.put("T5amount", accsSHENFT5.get(0)[0]);
		mapSHENF.put("T8amount", accsSHENFT8.get(0)[0]);
		mapSHENF.put("totalCharge", accsSHENF.get(0)[2]);
		mapSHENF.put("totalBrokerageAmount", accsSHENFPin6.get(0)[0]);
		BigDecimal minAccSHENFZ = BigDecimal.ZERO;
		BigDecimal minfeeSHENF = BigDecimal.ZERO;
		if(accsSHENF.get(0)[0]!=null){
			minAccSHENFZ=new BigDecimal(String.valueOf(accsSHENF.get(0)[0])); 
		}
		if(accsSHENF.get(0)[2]!=null){
			minfeeSHENF=new BigDecimal(String.valueOf(accsSHENF.get(0)[2])); 
		}
		BigDecimal minRateSHENF=new BigDecimal(shenfuCost); 
		BigDecimal totalCostSHENF = minAccSHENFZ.multiply(minRateSHENF);
		BigDecimal totalProfitSHENF = minfeeSHENF.subtract(totalCostSHENF);
		mapSHENF.put("totalCost", totalCostSHENF);
		mapSHENF.put("totalProfit", totalProfitSHENF);
		list.add(mapSHENF);
		
		
		params.put("name", "WEIXIN");
		List<Object[]> accsWEIXIN = userOrderDao.findBySql(hql3,params);
		List<Object[]> accsWEIXIND0 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(0,10) ",params);
		List<Object[]> accsWEIXINT1 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(1,11) ",params);
		List<Object[]> accsWEIXINT5 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(5) ",params);
		List<Object[]> accsWEIXINT8 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(8) ",params);
		List<Object[]> accsWEIXINPin6 = userOrderDao.findBySql(hql5,params);
		Map<String, Object> mapWEIXIN = new HashMap<String, Object>();
		mapWEIXIN.put("type","微信交易统计");
		mapWEIXIN.put("totalAmount", accsWEIXIN.get(0)[0]);
		mapWEIXIN.put("totalPayAmount", accsWEIXIN.get(0)[1]);
		mapWEIXIN.put("D0amount", accsWEIXIND0.get(0)[0]);
		mapWEIXIN.put("T1amount", accsWEIXINT1.get(0)[0]);
		mapWEIXIN.put("T5amount", accsWEIXINT5.get(0)[0]);
		mapWEIXIN.put("T8amount", accsWEIXINT8.get(0)[0]);
		mapWEIXIN.put("totalCharge", accsWEIXIN.get(0)[2]);
		mapWEIXIN.put("totalBrokerageAmount", accsWEIXINPin6.get(0)[0]);
		BigDecimal minAccWEIXIN = BigDecimal.ZERO;
		BigDecimal minfeeWEIXIN = BigDecimal.ZERO;
		if(accsWEIXIN.get(0)[0]!=null){
			minAccWEIXIN=new BigDecimal(String.valueOf(accsWEIXIN.get(0)[0])); 
		}
		if(accsWEIXIN.get(0)[2]!=null){
			minfeeWEIXIN=new BigDecimal(String.valueOf(accsWEIXIN.get(0)[2])); 
		}
		BigDecimal minRateWEIXIN=new BigDecimal(weixinCost); 
		BigDecimal totalCostWEIXIN = minAccWEIXIN.multiply(minRateWEIXIN);
		BigDecimal totalProfitWEIXIN = minfeeWEIXIN.subtract(totalCostWEIXIN);
		mapWEIXIN.put("totalCost", totalCostWEIXIN);
		mapWEIXIN.put("totalProfit", totalProfitWEIXIN);
		list.add(mapWEIXIN);
		
		
		params.put("name", "ALIPAY");
		List<Object[]> accsALIPAY = userOrderDao.findBySql(hql3,params);
		List<Object[]> accsALIPAYD0 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(0,10) ",params);
		List<Object[]> accsALIPAYT1 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(1,11) ",params);
		List<Object[]> accsALIPAYT5 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(5) ",params);
		List<Object[]> accsALIPAYT8 = userOrderDao.findBySql(hql3 + " and t.input_Acc_Type in(8) ",params);
		List<Object[]> accsALIPAYPin6 = userOrderDao.findBySql(hql5,params);
		Map<String, Object> mapALIPAY = new HashMap<String, Object>();
		mapALIPAY.put("type","支付宝交易统计");
		mapALIPAY.put("totalAmount", accsALIPAY.get(0)[0]);
		mapALIPAY.put("totalPayAmount", accsALIPAY.get(0)[1]);
		mapALIPAY.put("D0amount", accsALIPAYD0.get(0)[0]);
		mapALIPAY.put("T1amount", accsALIPAYT1.get(0)[0]);
		mapALIPAY.put("T5amount", accsALIPAYT5.get(0)[0]);
		mapALIPAY.put("T8amount", accsALIPAYT8.get(0)[0]);
		mapALIPAY.put("totalCharge", accsALIPAY.get(0)[2]);
		mapALIPAY.put("totalBrokerageAmount", accsALIPAYPin6.get(0)[0]);
		BigDecimal minAccALIPAYN = BigDecimal.ZERO;
		BigDecimal minfeeALIPAY = BigDecimal.ZERO;
		if(accsALIPAY.get(0)[0]!=null){
			minAccALIPAYN=new BigDecimal(String.valueOf(accsALIPAY.get(0)[0])); 
		}
		if(accsALIPAY.get(0)[2]!=null){
			minfeeALIPAY=new BigDecimal(String.valueOf(accsALIPAY.get(0)[2])); 
		}
		BigDecimal minRateALIPAY=new BigDecimal(aliPayCost); 
		BigDecimal totalCostALIPAY = minAccALIPAYN.multiply(minRateALIPAY);
		BigDecimal totalProfitALIPAY = minfeeALIPAY.subtract(totalCostALIPAY);
		mapALIPAY.put("totalCost", totalCostALIPAY);
		mapALIPAY.put("totalProfit", totalProfitALIPAY);
		list.add(mapALIPAY);
		
		
		String hql =" select sum(t.ORG_AMT) as orgAmt,sum(t.amt) as amt,sum(t.fee) as fee from trans_order t "
				+ " where t.`TYPE` not in (700,710) and t.`STATUS`='100' and t.trans_pay_type='10' "
				+ " and t.create_time BETWEEN :startDate and :endDate ";
		String hql2 = " select sum(bk.brokerage) ,count(bk.id) from trans_brokerage_detail bk left join trans_order_bonus_process p "
				+ " on bk.bonus_process_id=p.id where p.order_id in( select t.ID from trans_order t where t.`TYPE` not in (700,710) "
				+ " and t.`STATUS`='100' and t.trans_pay_type='10' and t.create_time BETWEEN :startDate and :endDate ) ";
		params.remove("name");
		List<Object[]> accs =   userOrderDao.findBySql(hql,params);
		List<Object[]> accsD0 = userOrderDao.findBySql(hql + " and t.input_Acc_Type in(0,10) ",params);
		List<Object[]> accsT1 = userOrderDao.findBySql(hql + " and t.input_Acc_Type in(1,11) ",params);
		List<Object[]> accsT5 = userOrderDao.findBySql(hql + " and t.input_Acc_Type in(5) ",params);
		List<Object[]> accsT8 = userOrderDao.findBySql(hql + " and t.input_Acc_Type in(8) ",params);
		List<Object[]> accsT6 = userOrderDao.findBySql(hql2,params);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type","系统总交易统计");
		map.put("totalAmount", accs.get(0)[0]);
		map.put("totalPayAmount", accs.get(0)[1]);
		map.put("D0amount", accsD0.get(0)[0]);
		map.put("T1amount", accsT1.get(0)[0]);
		map.put("T5amount", accsT5.get(0)[0]);
		map.put("T8amount", accsT8.get(0)[0]);
		map.put("totalCharge", accs.get(0)[2]);
		map.put("totalBrokerageAmount", accsT6.get(0)[0]);
		BigDecimal total = totalCostALIPAY.add(totalCostWEIXIN).add(totalCostSHENF).add(totalCostZANF).add(totalCostMinZ).add(totalCostPIN).add(totalCost);
		BigDecimal Profit = totalProfitALIPAY.add(totalProfitWEIXIN).add(totalProfitSHENF).add(totalProfitZANF).add(totalProfitMinZ).add(totalProfitPIN).add(totalProfit);
		map.put("totalCost", total);
		map.put("totalProfit", Profit);
		list.add(map);
		
		
		Workbook wb = ExcelUtil.createWorkBook(list, keys, columnNames);
		return wb;
	}

	@Override
	public Workbook exportProfitDetaildowoan(String statemtentDate) throws Exception {
		
		String hql =" select t from TuserOrder t left join t.tranPayOrder p left join p.payChannel c left join t.user u where t.status=100 "
				+ "and t.type not in(700,710) and t.createTime BETWEEN :startDate and :endDate";
		Map<String, Object> params = new HashMap<String, Object>();
		Date startDate = DateUtil.getBeforeDate(DateUtil.getDateFromString(statemtentDate), 0);
		Date endDate = DateUtil.getDatebyInterval(startDate, 1);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		
		
		String[] keys = new String[] { "name","mobile", "amount", "payAmount", "fee","chenfee","yinfee", "orderNum","Type", "inputType", "creaTime", "finsTime" };
		String[] columnNames = new String[] { "用户姓名","用户手机", "交易金额", "到账金额", "手续费","成本手续费","单笔盈利", "订单号","交易类型", "到账类型", "创建时间 ", "结算时间"};
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("sheetName", DateUtil.DateString(statemtentDate)+"民生交易明细");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add(m);
		
		List<Map<String, Object>> listPin = new ArrayList<Map<String, Object>>();
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("sheetName", DateUtil.DateString(statemtentDate)+"平安交易明细");
		listPin.add(p);
		
		List<Map<String, Object>> listZsf = new ArrayList<Map<String, Object>>();
		Map<String, Object> zf = new HashMap<String, Object>();
		zf.put("sheetName", DateUtil.DateString(statemtentDate)+"攒善银联交易明细");
		listZsf.add(zf);
		
		List<Map<String, Object>> listZT = new ArrayList<Map<String, Object>>();
		Map<String, Object> zt = new HashMap<String, Object>();
		zt.put("sheetName", DateUtil.DateString(statemtentDate)+"直通车交易明细");
		listZT.add(zt);
		
		List<Map<String, Object>> listSHFU = new ArrayList<Map<String, Object>>();
		Map<String, Object> sf = new HashMap<String, Object>();
		sf.put("sheetName", DateUtil.DateString(statemtentDate)+"申付银联交易明细");
		listSHFU.add(sf);
		
		List<Map<String, Object>> listALI = new ArrayList<Map<String, Object>>();
		Map<String, Object> a = new HashMap<String, Object>();
		a.put("sheetName", DateUtil.DateString(statemtentDate)+"支付宝交易明细");
		listALI.add(a);
		
		List<Map<String, Object>> listWEIXIN = new ArrayList<Map<String, Object>>();
		Map<String, Object> w = new HashMap<String, Object>();
		w.put("sheetName", DateUtil.DateString(statemtentDate)+"微信交易明细");
		listWEIXIN.add(w);
		
		List<TuserOrder>  t = userOrderDao.find(hql, params);
		for (TuserOrder r : t) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name",r.getUser().getRealName());
			map.put("mobile",r.getUser().getLoginName());
			map.put("amount",r.getOrgAmt());
			map.put("payAmount",r.getAmt());
			map.put("orderNum", r.getOrderNum());
			map.put("Type", UserOrder.getType(String.valueOf(r.getType())));
			map.put("inputType",UserOrder.getInputType(String.valueOf(r.getInputAccType())));
			map.put("creaTime", r.getCreateTime());
			map.put("finsTime", r.getTranPayOrder().getFinishDate());
			BigDecimal fee = r.getFee();
			map.put("fee", fee);
			if(r.getTranPayOrder().getPayChannel().getName().equals("MINSHENG")){
				BigDecimal rate=new BigDecimal(minshengCost); 
				BigDecimal chenfee = r.getOrgAmt().multiply(rate);
				map.put("chenfee", chenfee);
				map.put("yinfee", fee.subtract(chenfee));
				list.add(map);
			}else if(r.getTranPayOrder().getPayChannel().getName().equals("PINGANPAY")){
				BigDecimal rate=new BigDecimal(pinganCost); 
				BigDecimal chenfee = r.getOrgAmt().multiply(rate);
				map.put("chenfee", chenfee);
				map.put("yinfee", fee.subtract(chenfee));
				listPin.add(map);
			}else if(r.getTranPayOrder().getPayChannel().getName().equals("ZANSHANFU")){
				BigDecimal rate=new BigDecimal(zanshnfuCost); 
				BigDecimal chenfee = r.getOrgAmt().multiply(rate);
				map.put("chenfee", chenfee);
				map.put("yinfee", fee.subtract(chenfee));
				listZsf.add(map);
			}else if(r.getTranPayOrder().getPayChannel().getName().equals("MINGSHENGZHITONGCHE")){
				BigDecimal rate=new BigDecimal(minzhitongcheCost); 
				BigDecimal chenfee = r.getOrgAmt().multiply(rate);
				map.put("chenfee", chenfee);
				map.put("yinfee", fee.subtract(chenfee));
				listZT.add(map);
			}else if(r.getTranPayOrder().getPayChannel().getName().equals("SHENFU")){
				BigDecimal rate=new BigDecimal(shenfuCost); 
				BigDecimal chenfee = r.getOrgAmt().multiply(rate);
				map.put("chenfee", chenfee);
				map.put("yinfee", fee.subtract(chenfee));
				listSHFU.add(map);
			}else if(r.getTranPayOrder().getPayChannel().getName().equals("ALIPAY")){
				BigDecimal rate=new BigDecimal(aliPayCost); 
				BigDecimal chenfee = r.getOrgAmt().multiply(rate);
				map.put("chenfee", chenfee);
				map.put("yinfee", fee.subtract(chenfee));
				listALI.add(map);
			}else if(r.getTranPayOrder().getPayChannel().getName().equals("WEIXIN")){
				BigDecimal rate=new BigDecimal(weixinCost); 
				BigDecimal chenfee = r.getOrgAmt().multiply(rate);
				map.put("chenfee", chenfee);
				map.put("yinfee", fee.subtract(chenfee));
				listWEIXIN.add(map);
			}
		}
		Workbook wb = ExcelUtil.createWorkBook(list, keys, columnNames);
		wb = ExcelUtil.appendSheetInWorkBook(listPin, keys, columnNames, wb);
		wb = ExcelUtil.appendSheetInWorkBook(listZT, keys, columnNames, wb);
		wb = ExcelUtil.appendSheetInWorkBook(listZsf, keys, columnNames, wb);
		wb = ExcelUtil.appendSheetInWorkBook(listSHFU, keys, columnNames, wb);
		wb = ExcelUtil.appendSheetInWorkBook(listALI, keys, columnNames, wb);
		wb = ExcelUtil.appendSheetInWorkBook(listWEIXIN, keys, columnNames, wb);
		return wb;
	}

	@Override
	public List<String> findPinganStatement(String statemtentDate) {
		List<String> fileNames = new ArrayList<>();
		//对账单保存路径
		File file = new File(pingan_statement_root_path);
		//判断是否的文件夹
		if (file.isDirectory()) {
			//遍历所有文件
			String[] list = file.list();
			for (String fileName : list) {
				//查找需要下载的对账单文件
				if (fileName.startsWith(DateUtil.convertDateStrYYYYMMDD(statemtentDate))&&fileName.endsWith(".csv")) {
					File statemtentFile = new File(pingan_statement_root_path+File.separator+fileName);
					if (statemtentFile.exists()&&statemtentFile.isFile()) {
						fileNames.add(statemtentFile.getName());
					}
				}
			}
		}
		return fileNames;
	}
	
	
}
