package com.cn.flypay.service.trans.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.account.TaccountLog;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.TpayTypeLimitConfig;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.model.sys.TuserInfo;
import com.cn.flypay.model.sys.TuserMerchantReport;
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.model.trans.TbrokerageLog;
import com.cn.flypay.model.trans.TorderBonusProcess;
import com.cn.flypay.model.trans.TranPayOrder;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.model.trans.TuserOrderOperationRecord;
import com.cn.flypay.model.util.CollectionUtil;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.statement.OrderStatement;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserCard;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;
import com.cn.flypay.pageModel.trans.FinanceStatement;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.payment.PaymentService;
import com.cn.flypay.service.payment.PingAnExpenseService;
import com.cn.flypay.service.payment.RouteService;
import com.cn.flypay.service.payment.TroughTrainServeice;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.HolidayService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.PayTypeLimitConfigService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.ExcelUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.shenfu.ApplicationBase;
import com.cn.flypay.utils.shenfu.RSAUtil;
import com.cn.flypay.utils.shenfu.ShenFuPayUtil;

@Service
public class UserOrderServiceImpl implements UserOrderService {
	private Logger LOG = LoggerFactory.getLogger(getClass());
	@Autowired
	private BaseDao<TuserOrder> userOrderDao;
	@Autowired
	private BaseDao<TuserOrderOperationRecord> userOrderOperationRecordDao;
	@Autowired
	private BaseDao<BigDecimal> sumDao;
	@Autowired
	private BaseDao<TranPayOrder> payOrderDao;
	@Autowired
	private BaseDao<Tuser> userDao;
	@Autowired
	private BaseDao<TuserInfo> userInfoDao;
	@Autowired
	private BaseDao<Tbrokerage> brokerageDao;
	@Autowired
	private BaseDao<Taccount> accDao;
	@Autowired
	private BaseDao<TorderBonusProcess> bonusProcessDao;
	@Autowired
	private BaseDao<TaccountLog> accountLogDao;
	@Autowired
	private BaseDao<Tchannel> channelDao;
	@Autowired
	private BaseDao<TbrokerageLog> brokerageLogDao;
	@Autowired
	private PayTypeLimitConfigService payTypeLimitConfigService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private UserService userService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private SysParamService paramService;
	@Autowired
	private HolidayService holidayService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private RouteService routeService;
	@Autowired
	private TroughTrainServeice troughTrainServeice;
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private UserSettlementConfigService userSettlementConfigService;

	@Autowired
	private PingAnExpenseService pingAnExpenseService;

	@Autowired
	private ChannelPaymentService minshengPaymentService;

	@Autowired
	private ChannelPaymentService xinkePaymentService;

	@Autowired
	private UserSettlementConfigService configService;

	@Autowired
	private SysParamService sysParamService;

	// @Autowired
	// private UserOrderService userOrderService;

	@Value("${TroughTrainT0drawFee}")
	private String TroughTrainT0drawFee;

	@Value("${TroughTrainT1drawFee}")
	private String TroughTrainT1drawFee;

	@Value("${TrougWXD0tradeRate}")
	private String TrougWXD0tradeRate;

	@Value("${TrougWXT1tradeRate}")
	private String TrougWXT1tradeRate;

	@Value("${TrougZFBD0tradeRate}")
	private String TrougZFBD0tradeRate;

	@Value("${TrougZFBT1tradeRate}")
	private String TrougZFBT1tradeRate;

	@Value("${TrougQQZFD0tradeRate}")
	private String TrougQQZFD0tradeRate;

	@Value("${TrougQQZFT1tradeRate}")
	private String TrougQQZFT1tradeRate;

	@Value("${MINGSHENGD0}")
	private String MINGSHENGD0;

	@Value("${MINGSHENGT1}")
	private String MINGSHENGT1;

	@Override
	public UserOrder get(Long id) {
		TuserOrder t = userOrderDao.get(TuserOrder.class, id);
		UserOrder r = new UserOrder();
		BeanUtils.copyProperties(t, r);
		return r;
	}

	@Override
	public List<UserOrder> dataGrid(UserOrder app, PageFilter ph) {
		List<UserOrder> ul = new ArrayList<UserOrder>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TuserOrder t left join t.user tu left join tu.organization tog  left join t.tranPayOrder p left join t.card c left join c.bank k left join p.payChannel  pc ";
		// ph.setSort("createTime");
		// ph.setOrder("desc"); 
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = request.getSession();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		List<TuserOrder> l = new ArrayList<TuserOrder>();
		if (sessionInfo == null) {
			l = userOrderDao.find(hql + whereHql(app, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		} else {
			List<Object[]> accs = userOrderDao.findBySql("select s.ROLE_ID from sys_user_role s where s.USER_ID=" + sessionInfo.getId());
			if (String.valueOf(accs.get(0)).equals("2") || String.valueOf(accs.get(0)).equals("4") || String.valueOf(accs.get(0)).equals("16") || String.valueOf(accs.get(0)).equals("17")) {
				l = userOrderDao.find(hql + whereHql(app, params) + whereHqlTwo(app.getOperateUser(), params) + orderHql(ph), params, ph.getPage(), ph.getRows());
			} else {
				l = userOrderDao.find(hql + whereHql(app, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
			}
		}

		for (TuserOrder t : l) {
			UserOrder u = new UserOrder();
			BeanUtils.copyProperties(t, u);
			if (t.getTranPayOrder() != null) {
				PayOrder po = new PayOrder();
				BeanUtils.copyProperties(t.getTranPayOrder(), po);
				u.setPayOrder(po);
				u.setPayOrderFinishDate(t.getTranPayOrder().getFinishDate());
				u.setPayOrderPayNo(t.getTranPayOrder().getPayNo());
				u.setErrorInfo(t.getTranPayOrder().getErrorInfo());
				u.setTransPayType(t.getTransPayType());// update：2017.11.24
														// 交易明细显示升级订单，订单类型，10普通订单，20升级订单
				// update:2017.11.30 区分订单类型是否是直通车
				u.setChannelType("手动");
				if (t.getTranPayOrder().getPayChannel() != null) {
					String channelName = t.getTranPayOrder().getPayChannel().getName();
					if (GlobalConstant.ZTC_LIST.contains(channelName)) {
						// 直通车订单
						u.setChannelType("自动");
					}
				}
				/* 转账查询 贷记 */
				if (t.getType() == UserOrder.trans_type.QBZZ.getCode()) {
					TuserOrder cOrder = userOrderDao.get("select t from TuserOrder t left join t.tranPayOrder p left join t.user tu  where p.payNo='" + t.getOrderNum() + "'");
					if (cOrder.getUser() != null) {
						u.setTransPhone(cOrder.getUser().getLoginName());
					}
				}
				// if (t.getTranPayOrder().getPayChannel() != null) {
				// u.setChannelName(t.getTranPayOrder().getPayChannel().getDetailName());
				// }
			}
			if (t.getCard() != null) {
				UserCard uc = new UserCard();
				BeanUtils.copyProperties(t.getCard(), uc);
				u.setUserCard(uc);
				if (t.getCard().getBank() != null) {
					uc.setBankName(t.getCard().getBank().getBankName());
				}
			}
			if (t.getUser() != null) {
				u.setUserName(t.getUser().getRealName());
				u.setUserPhone(t.getUser().getLoginName());
				if (t.getUser().getOrganization() != null) {
					u.setOrganizationName(t.getUser().getOrganization().getName());
				}
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public List<UserOrder> dataGridXS(UserOrder order, PageFilter ph) {
		List<UserOrder> ul = new ArrayList<UserOrder>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TuserOrder t left join t.user tu where t.status=100 and t.type not in(700,710) ";
		if (order.getUserId() != null) {
			hql += " and tu.id = :uid";
			params.put("uid", order.getUserId());
		}
		if (order.getCreateDatetimeStart() != null) {
			hql += " and t.createTime >= :createDatetimeStart";
			params.put("createDatetimeStart", order.getCreateDatetimeStart());
		}
		if (order.getCreateDatetimeEnd() != null) {
			hql += " and t.createTime <= :createDatetimeEnd";
			params.put("createDatetimeEnd", order.getCreateDatetimeEnd());
		}
		List<TuserOrder> l = userOrderDao.find(hql + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TuserOrder t : l) {
			UserOrder u = new UserOrder();
			BeanUtils.copyProperties(t, u);
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(UserOrder order, PageFilter pf) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TuserOrder t left join t.user tu left join t.tranPayOrder p left join tu.organization tog left join p.payChannel  pc ";
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = request.getSession();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		Long l = null;
		// 默认查询时，时间为NULL，为提高速率，改为统计一天，一旦进行条件查询，时间不为null，正常查询
		if (order.getFinishDatetimeStart() == null && order.getFinishDatetimeEnd() == null && order.getPayDatetimeStart() == null && order.getPayDatetimeEnd() == null) {
			order.setPayDatetimeStart(DateUtil.getStringFromDate(new Date(), DateUtil.FORMAT_YYYY_MM_DD));
			order.setPayDatetimeEnd(DateUtil.getStringFromDate(new Date(), DateUtil.FORMAT_YYYY_MM_DD));
		}
		if (sessionInfo == null) {
			l = userOrderDao.count("select count(t.transPayType) " + hql + whereHql(order, params), params);
		} else {
			List<Object[]> accs = userOrderDao.findBySql("select s.ROLE_ID from sys_user_role s where s.USER_ID=" + sessionInfo.getId());
			if (String.valueOf(accs.get(0)).equals("2") || String.valueOf(accs.get(0)).equals("4") || String.valueOf(accs.get(0)).equals("16") || String.valueOf(accs.get(0)).equals("17")) {
				l = userOrderDao.count("select count(t.transPayType) " + hql + whereHql(order, params) + whereHqlTwo(order.getOperateUser(), params), params);
			} else {
				l = userOrderDao.count("select count(t.transPayType) " + hql + whereHql(order, params), params);
			}
		}
		return l;
	}

	@Override
	public BigDecimal getOrderOrgAmt(UserOrder order, PageFilter pf) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select sum(t.orgAmt) from TuserOrder t left join t.user tu left join t.tranPayOrder p left join tu.organization tog ";
		List<BigDecimal> orders = sumDao.find(hql + whereHql(order, params), params);
		return orders.get(0);
	}

	@Override
	public BigDecimal getOrderOrgAmtTwo(UserOrder order, PageFilter pf) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select sum(t.orgAmt) from TuserOrder t left join t.user tu left join t.tranPayOrder p left join tu.organization tog ";
		List<BigDecimal> orders = sumDao.find(hql + whereHqlTwo(order, params), params);
		return orders.get(0);
	}

	@Override
	public BigDecimal getOrderOrg(Long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		String hql = "select sum(t.orgAmt) from TuserOrder t left join t.user tu" + " where t.status=100 and t.type not in(700,710) and tu.id=:userId  ";
		List<BigDecimal> orders = sumDao.find(hql, params);
		if (orders.get(0) == null) {
			return new BigDecimal(0.00);
		}
		return orders.get(0);
	}

	private String whereHql(UserOrder order, Map<String, Object> params) {
		String hql = "";
		if (order != null) {
			hql += " where 1=1 ";
			try {
				if (order.getUserId() != null) {
					hql += " and tu.id = :uid";
					params.put("uid", order.getUserId());
				}
				if (StringUtil.isNotBlank(order.getUserPhone())) {
					hql += " and tu.loginName = :loginName";
					params.put("loginName", order.getUserPhone().trim());
				}
				if (StringUtil.isNotBlank(order.getOrderNum())) {
					hql += " and t.orderNum = :orderNum";
					params.put("orderNum", order.getOrderNum());
				}
				if (StringUtil.isNotBlank(order.getReturnOrderNum())) {
					hql += " and p.payNo = :returnOrderNum";
					params.put("returnOrderNum", order.getReturnOrderNum());
				}
				if (order.getTransPayType() != null) {
					hql += " and t.transPayType = :transPayType";
					params.put("transPayType", order.getTransPayType());
				}
				if (StringUtil.isNotBlank(order.getFinishDatetimeStart())) {
					hql += " and p.finishDate >= :finishDatetimeStart";
					params.put("finishDatetimeStart", DateUtil.getStartOfDay(DateUtil.convertStringToDate("yyyy-MM-dd", order.getFinishDatetimeStart())));
				}
				if (StringUtil.isNotBlank(order.getFinishDatetimeEnd())) {
					hql += " and p.finishDate <= :finishDatetimeEnd";
					params.put("finishDatetimeEnd", DateUtil.getEndOfDay(DateUtil.convertStringToDate("yyyy-MM-dd", order.getFinishDatetimeEnd())));
				}
				if (StringUtil.isNotBlank(order.getPayDatetimeStart())) {
					hql += " and t.createTime >= :payDatetimeStart";
					params.put("payDatetimeStart", DateUtil.getStartOfDay(DateUtil.convertStringToDate("yyyy-MM-dd", order.getPayDatetimeStart())));
				}
				if (StringUtil.isNotBlank(order.getPayDatetimeEnd())) {
					hql += " and t.createTime <= :payDatetimeEnd";
					params.put("payDatetimeEnd", DateUtil.getEndOfDay(DateUtil.convertStringToDate("yyyy-MM-dd", order.getPayDatetimeEnd())));
				}
				if (order.getCreateDatetimeStart() != null) {
					hql += " and t.createTime >= :createDatetimeStart";
					params.put("createDatetimeStart", order.getCreateDatetimeStart());
				}
				if (order.getCreateDatetimeEnd() != null) {
					hql += " and t.createTime <= :createDatetimeEnd";
					params.put("createDatetimeEnd", order.getCreateDatetimeEnd());
				}

				// 直通车类型判断 (如果添加新通道，需要和此保持一致)
				if (StringUtil.isNotBlank(order.getZtcType())) {
					hql += " and pc.name = :ztcType ";
					params.put("ztcType", order.getZtcType());
				}

				if (order.getType() != null) {
					hql += " and t.type = :tranType";
					params.put("tranType", order.getType());
				}
				if (order.getStatus() != null) {
					hql += " and t.status = :status";
					params.put("status", order.getStatus());
				}
				if (StringUtil.isNotBlank(order.getCdType())) {
					hql += " and t.cdType = :cdType";
					params.put("cdType", order.getCdType());
				}

				if (order.getMulType() != null && order.getMulType().size() > 0) {
					hql += " and t.type in ( :mulType)";
					params.put("mulType", order.getMulType());
				}

				if (StringUtil.isNotBlank(order.getFrontStatus())) {
					if ("S".equals(order.getFrontStatus())) {
						hql += " and t.status < :status";
						params.put("status", 200);
					} else if ("F".equals(order.getFrontStatus())) {
						hql += " and t.status < :status and t.status>=:mixstatus";
						params.put("mixstatus", 200);
						params.put("status", 300);
					} else if ("I".equals(order.getFrontStatus())) {
						hql += " and t.status >= :status";
						params.put("status", 300);
					}
				}
				if (order.getOrganizationId() != null) {
					hql += " and  tog.id in(:orgIds)";
					params.put("orgIds", organizationService.getOwerOrgIds(order.getOrganizationId()));
				}
				if (order.getOperateUser() != null) {

					hql += " and  tog.id in(:operaterOrgIds)";
					params.put("operaterOrgIds", organizationService.getOwerOrgIds(order.getOperateUser().getOrganizationId()));
				}
			} catch (ParseException e) {
				LOG.equals(e);
			}
		}
		return hql;
	}

	private String whereHqlTwo(UserOrder order, Map<String, Object> params) {
		String hql = "";
		if (order != null) {
			hql += " where t.type not in(700,710) ";
			try {
				if (order.getUserId() != null) {
					hql += " and tu.id = :uid";
					params.put("uid", order.getUserId());
				}
				if (StringUtil.isNotBlank(order.getUserPhone())) {
					hql += " and tu.loginName = :loginName";
					params.put("loginName", order.getUserPhone().trim());
				}
				if (StringUtil.isNotBlank(order.getOrderNum())) {
					hql += " and t.orderNum = :orderNum";
					params.put("orderNum", order.getOrderNum());
				}
				if (StringUtil.isNotBlank(order.getReturnOrderNum())) {
					hql += " and p.payNo = :returnOrderNum";
					params.put("returnOrderNum", order.getReturnOrderNum());
				}
				if (order.getTransPayType() != null) {
					hql += " and t.transPayType = :transPayType";
					params.put("transPayType", order.getTransPayType());
				}
				if (StringUtil.isNotBlank(order.getFinishDatetimeStart())) {
					hql += " and p.finishDate >= :finishDatetimeStart";
					params.put("finishDatetimeStart", DateUtil.getStartOfDay(DateUtil.convertStringToDate("yyyy-MM-dd", order.getFinishDatetimeStart())));
				}
				if (StringUtil.isNotBlank(order.getFinishDatetimeEnd())) {
					hql += " and p.finishDate <= :finishDatetimeEnd";
					params.put("finishDatetimeEnd", DateUtil.getEndOfDay(DateUtil.convertStringToDate("yyyy-MM-dd", order.getFinishDatetimeEnd())));
				}
				if (StringUtil.isNotBlank(order.getPayDatetimeStart())) {
					hql += " and t.createTime >= :payDatetimeStart";
					params.put("payDatetimeStart", DateUtil.getStartOfDay(DateUtil.convertStringToDate("yyyy-MM-dd", order.getPayDatetimeStart())));
				}
				if (StringUtil.isNotBlank(order.getPayDatetimeEnd())) {
					hql += " and t.createTime <= :payDatetimeEnd";
					params.put("payDatetimeEnd", DateUtil.getEndOfDay(DateUtil.convertStringToDate("yyyy-MM-dd", order.getPayDatetimeEnd())));
				}
				if (order.getCreateDatetimeStart() != null) {
					hql += " and t.createTime >= :createDatetimeStart";
					params.put("createDatetimeStart", order.getCreateDatetimeStart());
				}
				if (order.getCreateDatetimeEnd() != null) {
					hql += " and t.createTime <= :createDatetimeEnd";
					params.put("createDatetimeEnd", order.getCreateDatetimeEnd());
				}

				if (order.getType() != null) {
					hql += " and t.type = :tranType";
					params.put("tranType", order.getType());
				}
				if (order.getStatus() != null) {
					hql += " and t.status = :status";
					params.put("status", order.getStatus());
				}
				if (StringUtil.isNotBlank(order.getCdType())) {
					hql += " and t.cdType = :cdType";
					params.put("cdType", order.getCdType());
				}

				if (order.getMulType() != null && order.getMulType().size() > 0) {
					hql += " and t.type in ( :mulType)";
					params.put("mulType", order.getMulType());
				}

				if (StringUtil.isNotBlank(order.getFrontStatus())) {
					if ("S".equals(order.getFrontStatus())) {
						hql += " and t.status < :status";
						params.put("status", 200);
					} else if ("F".equals(order.getFrontStatus())) {
						hql += " and t.status < :status and t.status>=:mixstatus";
						params.put("mixstatus", 200);
						params.put("status", 300);
					} else if ("I".equals(order.getFrontStatus())) {
						hql += " and t.status >= :status";
						params.put("status", 300);
					}
				}
				if (order.getOrganizationId() != null) {
					hql += " and  tog.id in(:orgIds)";
					params.put("orgIds", organizationService.getOwerOrgIds(order.getOrganizationId()));
				}
				if (order.getOperateUser() != null) {
					hql += " and  tog.id in(:operaterOrgIds)";
					params.put("operaterOrgIds", organizationService.getOwerOrgIds(order.getOperateUser().getOrganizationId()));
				}
			} catch (ParseException e) {
				LOG.equals(e);
			}
		}
		return hql;
	}

	private String whereHqlTwo(User user, Map<String, Object> params) {
		String hql = "";
		if (user != null) {
			if (user.getId() != null) {
				hql += " and tu.pid = :id";
				params.put("id", user.getId());
			}
		}
		return hql;
	}

	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null) && (ph.getSort().equals("payOrderFinishDate"))) {
			orderString = " order by p.finishDate " + ph.getOrder();
		} else if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}

	/**
	 * 创建用户的提现订单
	 * 
	 * @param userId
	 * @param orderNum
	 *            提现订单号
	 * @param amt
	 *            提现金额
	 * @param t
	 *            0:T0 1:T1
	 * @param desc
	 *            提现描述
	 */
	@Override
	public TuserOrder createWithdrawOrder(Taccount account, String orderNum, Double amt, Boolean isT0, String desc, TuserCard card, Integer transPayType) throws Exception {
		LOG.info("-----------创建用户的提现订单 begin ------");
		try {
			Tuser user = account.getUser();
			BigDecimal amtbd = BigDecimal.valueOf(amt);
			/* 创建用户订单 */
			TuserOrder uor = new TuserOrder();
			uor.setTransPayType(transPayType);
			uor.setType(UserOrder.trans_type.XJTX.getCode());
			uor.setOrgAmt(amtbd);
			/* 根据用户提现的类型区分用户的手续费 */
			BigDecimal fee = isT0 ? user.getSettlementConfig().getT0Fee() : user.getSettlementConfig().getT1Fee();
			uor.setFee(fee);
			/* 因创建订单前已经更新了账户，故直接获取账户余额 */
			uor.setAvlAmt(account.getAvlAmt());
			uor.setAmt(amtbd.subtract(fee));// 实际提现金额=账户下单金额-费用
			uor.setDescription(desc);
			uor.setOrderNum(orderNum);
			uor.setCdType(UserOrder.cd_type.C.name());// d借记 加 ；c贷记 减
			uor.setUser(user);
			uor.setPayType(isT0 ? 0 : 1);
			uor.setCard(card);
			uor.setInputAccType(0);
			uor.setPersonRate(BigDecimal.ZERO);
			uor.setShareRate(BigDecimal.ZERO);
			/* 创建支付订单 */
			TranPayOrder tpo = new TranPayOrder();
			if (isT0) {
				tpo.setPayDate(new Date());
			}
			tpo.setPayAmt(amtbd);
			Tchannel chl = channelService.getTchannelByTransType(UserOrder.trans_type.XJTX.getCode());
			tpo.setPayChannel(chl);
			tpo.setStatus(PayOrder.pay_status.PROCESSING.getCode());
			uor.setTranPayOrder(tpo);
			tpo.setUserOrder(uor);
			userOrderDao.save(uor);
			LOG.info("-----------创建用户的提现订单 end ------");
			return uor;
		} catch (Exception e) {
			LOG.error("create user order error", e);
			throw e;
		}
		// logger.info("-----------创建用户的提现订单 end ------");
		// return uor;
	}

	/**
	 * 创建用户的佣金提现订单
	 * 
	 * @param userId
	 * @param orderNum
	 *            提现订单号
	 * @param amt
	 *            提现金额
	 * @param desc
	 *            提现描述
	 */
	@Override
	public TuserOrder createBrokerageOrder(Long userId, String orderNum, Double amt, String desc, TuserCard card) throws Exception {
		LOG.info("-----------创建用户的佣金提现订单 begin ------");
		try {
			Tuser t = userDao.get("select t from Tuser t left join t.settlementConfig s where t.id=" + userId);
			Tbrokerage acc = brokerageDao.get("select t from Tbrokerage t left join t.user u where u.id=" + t.getId());

			BigDecimal amtbd = BigDecimal.valueOf(amt);
			/* 创建用户订单 */
			TuserOrder uor = new TuserOrder();
			uor.setTransPayType(UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode());
			uor.setType(UserOrder.trans_type.YJTX.getCode());
			uor.setOrgAmt(amtbd);
			/* 根据用户提现的类型区分用户的手续费 */
			BigDecimal fee = t.getSettlementConfig().getRabaleFee();
			uor.setFee(fee);
			uor.setAvlAmt(acc.getBrokerage());
			uor.setAmt(amtbd.subtract(fee));
			uor.setDescription(desc);
			uor.setStatus(UserOrder.order_status.PROCESSING.getCode());
			uor.setOrderNum(orderNum);
			uor.setCdType(UserOrder.cd_type.C.name());// d借记 加 ；c贷记 减
			uor.setUser(t);
			uor.setPersonRate(BigDecimal.ZERO);
			uor.setShareRate(BigDecimal.ZERO);
			uor.setPayType(0);
			uor.setCreateTime(new Date());
			uor.setScanNum(0);
			uor.setCard(card);
			uor.setInputAccType(0);// T5入账 默认为0
			/* 创建支付订单 */
			TranPayOrder tpo = new TranPayOrder();
			tpo.setPayAmt(amtbd);
			tpo.setStatus(PayOrder.pay_status.PROCESSING.getCode());
			Tchannel chl = channelService.getTchannelByTransType(UserOrder.trans_type.YJTX.getCode());
			tpo.setPayChannel(chl);
			uor.setTranPayOrder(tpo);
			tpo.setUserOrder(uor);
			userOrderDao.save(uor);
			LOG.info("-----------创建用户的佣金提现订单 end ------");
			return uor;
		} catch (Exception e) {
			LOG.error("create user order error", e);
			throw e;
		}

	}

	@Override
	public String createTransOrder(Long userId, String orderNum, String payNum, String busNo, Integer transType, Double amt, String accType, String payNo, TuserCard card, String orderDesc, Integer transPayType, Channel channel,
			Integer inputAccType, Integer angentType) throws Exception {
		try {
			System.out.println("11111");
			// 为了检测订单创建bug，特地设置下列报文输出
			StringBuffer strbuf = new StringBuffer();
			strbuf.append("----创建交易订单的请求参数集合：订单号orderNum = " + orderNum);
			if (userId != null) {
				strbuf.append(",strbuf = " + userId);
			}
			if (StringUtil.isNotBlank(payNum)) {
				strbuf.append(",payNum = " + payNum);
			}
			if (StringUtil.isNotBlank(busNo)) {
				strbuf.append(",busNo = " + busNo);
			}
			if (transType != null) {
				strbuf.append(",transType = " + transType);
			}
			if (amt != null) {
				strbuf.append(",amt = " + amt);
			}
			if (StringUtil.isNotBlank(accType)) {
				strbuf.append(",accType = " + accType);
			}
			if (StringUtil.isNotBlank(payNo)) {
				strbuf.append(",payNo = " + payNo);
			}
			if (card != null) {
				strbuf.append(",card_id = " + card.getId());
			}
			if (StringUtil.isNotBlank(orderDesc)) {
				strbuf.append(",orderDesc = " + orderDesc);
			}
			if (transPayType != null) {
				strbuf.append(",transPayType = " + transPayType);
			}
			if (channel != null) {
				strbuf.append(",channel_id = " + channel.getId());
			}
			if (inputAccType != null) {
				strbuf.append(",inputAccType = " + inputAccType);
			}
			if (angentType != null) {
				strbuf.append(",angentType = " + angentType);
			}

			LOG.info(strbuf.toString());

			Tuser t = userDao.get("select t from Tuser t left join t.settlementConfig s where t.id=" + userId);
			Taccount acc = accDao.get("select t from Taccount t left join t.user u where u.id=" + t.getId());

			BigDecimal amtbd = BigDecimal.valueOf(amt);
			/* 创建用户订单 */
			TuserOrder uor = new TuserOrder();
			uor.setTransPayType(transPayType);
			uor.setType(transType);
			uor.setOrgAmt(amtbd);

			BigDecimal[] rate = userSettlementConfigService.getUserInputRateAndShareRate(userId, transType, inputAccType);

			// 添加2元提现费--start
			BigDecimal b1 = BigDecimal.valueOf(amt);
			BigDecimal b2 = BigDecimal.valueOf(2);
			BigDecimal b3 = b2.divide(b1, 4, BigDecimal.ROUND_DOWN); // 提现费手续费=2/交易金额（最小保留万分数，后面有余数不做四舍五入）
			BigDecimal b4 = rate[0].add(b3);
			// 添加2元提现费--end

			BigDecimal fee = null;
			if (transPayType == 10) {
				// 普通订单 平安和易联 有手续费中包含的2元提现手续费费率
				if (channel.getName().equals("PINGANPAYZHITONGCHE") || channel.getName().equals("PINGANPAY") || channel.getName().equals("PINGANPAYZHITONGCHE_ZHIQING") || channel.getName().equals("YILIANYINLIANJIFENZTC")) {
					uor.setExtractFee(b3);
					// 计算手续费
					fee = getInputOrderFee(inputAccType, transPayType, amtbd, b4);
				} else {
					uor.setExtractFee(BigDecimal.ZERO);
					fee = getInputOrderFee(inputAccType, transPayType, amtbd, rate[0]);
				}
			} else {
				// 升级订单
				uor.setExtractFee(BigDecimal.ZERO);
				fee = getInputOrderFee(inputAccType, transPayType, amtbd, rate[0]);
			}

			// 根据手续费率计算手续费
			if (channel.getName().equals("MINGSHENGZHITONGCHE") || channel.getName().equals("XINKEZHITONGCHE") || channel.getName().equals("PINGANPAYZHITONGCHE") || channel.getName().equals("PINGANPAYZHITONGCHE_ZHIQING")
					|| channel.getName().equals("ZHEYANGZTC") || channel.getName().equals("YIBAOZHITONGCHE") || channel.getName().equals("GAZHIYINLIANJIFENZHITONGCHE")) {

				// 如果是直通车通道下单，对于微信和支付宝订单，需要取各自用户费率表中的D0费率配置，
				// 升级订单和普通订单需要区分开,普通订单需要加上2元的费用费率
				UserSettlementConfig scg = configService.getByUserId(userId);
				if (transType == UserOrder.trans_type.ALQR.getCode()) {
					if (inputAccType == 0 || inputAccType == 10) {
						if (transPayType == 10) { // 平安普通订单 添加上2元手续费
							fee = amtbd.multiply(scg.getInputFeeD0ZtAlipay().add(b3));
						} else { // 升级订单
							fee = amtbd.multiply(scg.getInputFeeD0ZtAlipay());
						}
						// fee = fee.add(new BigDecimal(2));
					} else if (inputAccType == 1 || inputAccType == 11) {
						if (transPayType == 10) {
							fee = amtbd.multiply(scg.getInputFeeZtAlipay().add(b3));
						} else {
							fee = amtbd.multiply(scg.getInputFeeZtAlipay());
						}
						// fee = fee.add(new BigDecimal(1));
					}
				} else if (transType == UserOrder.trans_type.WXQR.getCode()) {
					if (inputAccType == 0 || inputAccType == 10) {
						if (transPayType == 10) {
							fee = amtbd.multiply(scg.getInputFeeD0ZtWeixin().add(b3));
						} else {
							fee = amtbd.multiply(scg.getInputFeeD0ZtWeixin());
						}
						// fee = fee.add(new BigDecimal(2));
					} else if (inputAccType == 1 || inputAccType == 11) {
						if (transPayType == 10) {
							fee = amtbd.multiply(scg.getInputFeeZtWeixin().add(b3));
						} else {
							fee = amtbd.multiply(scg.getInputFeeZtWeixin());
						}
						// fee = fee.add(new BigDecimal(1));
					}
				}
			}

			
			uor.setFee(fee);
			uor.setAvlAmt(acc.getAvlAmt());
			uor.setAmt(amtbd.subtract(fee));
			uor.setDescription(orderDesc);
			uor.setOrderNum(orderNum);
			uor.setCdType(accType);// d借记 加 ；c贷记 减
			uor.setInputAccType(inputAccType);// T5免除手续费，并且不参与分佣
			uor.setAgentType(angentType);
			if (inputAccType == 5) {
				uor.setPersonRate(BigDecimal.ZERO);// 个人刷卡费率
				uor.setShareRate(BigDecimal.ZERO);// 个人分润费率
			} else {
				uor.setPersonRate(rate[0]);// 个人刷卡费率
				uor.setShareRate(rate[1]);// 个人分润费率
			}
			uor.setUser(t);
			uor.setPayType(0);// 默认提现为实时提现
			if (card != null) {
				uor.setCard(card);
			}
			/* 创建支付订单 */
			TranPayOrder tpo = new TranPayOrder();
			tpo.setPayDate(new Date());
			tpo.setPayAmt(amtbd);
			if (StringUtil.isNotBlank(payNo)) {
				TranPayOrder tpc = payOrderDao.get("select t from TranPayOrder t where t.payNo='" + payNo.trim() + "'");
				if (tpc != null) {
					throw new Exception(payNo + "订单号已经存在，请重新创建");
				} else {
					tpo.setPayNo(payNo);
				}
			}
			if (StringUtil.isNotBlank(payNum)) {
				tpo.setPayNo(payNum);
			}
			if (StringUtil.isNotBlank(busNo)) {
				tpo.setBusNo(busNo);
			}
			Tchannel tchannel = channelService.getTchannelInCache(channel.getId());
			TpayTypeLimitConfig c=payTypeLimitConfigService.findByCode(tchannel.getName(), 2L);
			BigDecimal srvFee = c==null?new BigDecimal(0):c.getSrvFee();
			System.out.println("c.getSrvFee():"+c.getSrvFee());
			uor.setSrvFee(srvFee);
			tpo.setPayChannel(tchannel);
			tpo.setStatus(PayOrder.pay_status.PROCESSING.getCode());
			uor.setTranPayOrder(tpo);
			tpo.setUserOrder(uor);
			userOrderDao.save(uor);

			return orderNum;
		} catch (Exception e) {
			LOG.error("create user order error", e);
			throw e;
		}

	}

	/**
	 * 根据收单类型计算手续费， 购买代理没有手续费， 普通收款最低0.01，并且保留两位小数，四舍五入 D0 T1收款 有手续费 T5和代理费无手续费
	 * 
	 * @param inputAccType入账类型
	 *            例如 0 DO 1 T1
	 * @param transPayType
	 *            10 普通订单 20 代理订单
	 * @param amtbd
	 *            订单金额
	 * @param stcg
	 *            用户个人表里的费率
	 * @return
	 */
	private BigDecimal getInputOrderFee(Integer inputAccType, Integer transPayType, BigDecimal amtbd, BigDecimal feeRate) {
		BigDecimal fee = BigDecimal.ZERO;
		if (inputAccType == 0 || inputAccType == 1 || inputAccType == 10 || inputAccType == 11) {
			if (transPayType - UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode() == 0) {
				// T0 输入的手续费，最少0.01元
				fee = BigDecimal.valueOf(0.01d);
				BigDecimal realAmt = amtbd.multiply(feeRate); // 订单金额*手续费
				if (realAmt.compareTo(fee) >= 0) {
					fee = realAmt.setScale(2, RoundingMode.UP);
				}
			}
		} else if (inputAccType == 5) {
			// T5 或者 购买代理类型
			fee = BigDecimal.ZERO;
		}
		return fee;
	}

	private BigDecimal getInputOrderFeeTwo(Integer inputAccType, Integer transPayType, BigDecimal amtbd, BigDecimal feeRate, Integer transType) {
		BigDecimal fee = BigDecimal.ZERO;
		if (inputAccType == 0 || inputAccType == 1 || inputAccType == 10 || inputAccType == 11) {
			if (transPayType - UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode() == 0) {
				// T0 输入的手续费，最少0.01元
				fee = BigDecimal.valueOf(0.01d);
				BigDecimal realAmt = new BigDecimal(0);
				if (transType == 200) {
					if (inputAccType == 0 || inputAccType == 10) {
						realAmt = amtbd.multiply(new BigDecimal(TrougZFBD0tradeRate));
					} else if (inputAccType == 1 || inputAccType == 11) {
						realAmt = amtbd.multiply(new BigDecimal(TrougZFBT1tradeRate));
					}
				} else if (transType == 300) {
					if (inputAccType == 0 || inputAccType == 10) {
						realAmt = amtbd.multiply(new BigDecimal(TrougWXD0tradeRate));
					} else if (inputAccType == 1 || inputAccType == 11) {
						realAmt = amtbd.multiply(new BigDecimal(TrougWXT1tradeRate));
					}
				} else if (transType == 1300) {
					if (inputAccType == 0 || inputAccType == 10) {
						realAmt = amtbd.multiply(new BigDecimal(TrougQQZFD0tradeRate));
					} else if (inputAccType == 1 || inputAccType == 11) {
						realAmt = amtbd.multiply(new BigDecimal(TrougQQZFT1tradeRate));
					}
				}
				if (realAmt.compareTo(fee) >= 0) {
					fee = realAmt.setScale(2, RoundingMode.UP);
				}
			}
		} else if (inputAccType == 5) {
			// T5 或者 购买代理类型
			fee = BigDecimal.ZERO;
		}
		return fee;
	}

	@Override
	public UserOrder findTodoUserOrderByPayNum(String payNum) {
		return findTodoUserOrder(null, payNum, null, UserOrder.order_status.PROCESSING.getCode());
	}

	@Override
	public UserOrder findTodoUserOrderByBusNo(String busNo) {
		return findTodoUserOrder(null, null, busNo, UserOrder.order_status.PROCESSING.getCode());
	}

	@Override
	public UserOrder findTodoUserOrderByOrderNum(String orderNum) {
		return findTodoUserOrder(orderNum, null, null, UserOrder.order_status.PROCESSING.getCode());
	}

	@Override
	public UserOrder findOrderByOrderNum(String orderNum) {
		return findTodoUserOrder(orderNum, null, null, null);
	}

	private UserOrder findTodoUserOrder(String orderNum, String payNo, String busNo, Integer status) {
		String hql = "select t from TuserOrder t  left join t.tranPayOrder u left join t.user tu where 1=1 ";
		Map<String, Object> params = new HashMap<String, Object>();
		if (status != null) {
			params.put("status", status);
			hql = hql + " and t.status=:status ";
		}
		if (StringUtil.isNotBlank(orderNum)) {
			params.put("orderNum", orderNum);
			hql = hql + " and  t.orderNum=:orderNum";
		}
		if (StringUtil.isNotBlank(payNo)) {
			params.put("payNo", payNo);
			hql = hql + " and  u.payNo=:payNo";
		}
		if (StringUtil.isNotBlank(busNo)) {
			params.put("busNo", busNo);
			hql = hql + " and  u.busNo=:busNo";
		}
		List<TuserOrder> uOrders = userOrderDao.find(hql, params);
		if (uOrders.size() > 0) {
			UserOrder userOrder = new UserOrder();
			BeanUtils.copyProperties(uOrders.get(0), userOrder);
			userOrder.setUserId(uOrders.get(0).getUser().getId());
			TranPayOrder tpo = uOrders.get(0).getTranPayOrder();
			if (tpo != null) {
				userOrder.setPayOrderPayNo(tpo.getPayNo());
				userOrder.setPayOrderBusNo(tpo.getBusNo());
				if (tpo.getPayChannel() != null) {
					userOrder.setChannelId(tpo.getPayChannel().getId());
				}
				userOrder.setPayOrderFinishDate(tpo.getFinishDate());
			}
			return userOrder;
		}
		return null;
	}

	@Override
	public String finishInputOrderStatus(TuserOrder t, PayOrder payOrder) throws Exception {
		LOG.info("---------------订单：" + t.getOrderNum() + ",更新完成状态  start--------");
		String flag = GlobalConstant.RESP_CODE_SUCCESS;
		try {
			/* 收到通道消息后，更新订单情况 */
			updatePayOrderAfterChannelReturnInfo(payOrder, t);

			if (payOrder.getStatus() == PayOrder.pay_status.SUCCESS.getCode()) {
				/* 支付成功后，对于购买代理的订单，直接升级用户的等级； 对于普通的收款订单，需要完成账户信息的更新 */
				if (t.getTransPayType() == UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode()) {
					userService.updateUserType(t.getUser().getId(), t.getOrgAmt(), null);
				} else {
					try {
						// 由于2017-12月份平台全部通道切换至直清模式，所以下面机制杀掉 ---开始
						// Map<String, String> bigTranTypes =
						// dictionaryService.comboxMap("bigTranType");
						// String desc = t.getUser().getName() + "的" +
						// bigTranTypes.get(t.getInputAccType()) + "账户增加了" +
						// t.getAmt() + "元";
						// accountService.updateIncreaseAccountAfterSuccessInfo(t.getOrderNum(),
						// t.getUser().getId(), t.getInputAccType(), t.getAmt(),
						// desc);
						// 由于2017-12月份平台全部通道切换至直清模式，所以下面机制杀掉 ---结束

						LOG.info("由于当前项目只存在直通车通道，所以，对账户余额不做处理");
					} catch (Exception e) {
						LOG.error("账户更新失败", e);
						flag = GlobalConstant.RESP_CODE_047;
						throw e;
					}
				}
				/* 开始分润 */
				dealStartShareBonus(t);
				/* 更新通道金额 */
				try {
					if (t.getTranPayOrder() != null) {
						channelService.appendTodayAmt(t.getTranPayOrder().getPayChannel().getId(), t.getAmt());
					}
				} catch (Exception e) {
					/* 可能存在锁表问题，但通道方金额抛出异常时，不允许回滚 */
					LOG.error("更新通道累计金额失败", e);
				}
			}

		} catch (Exception e) {
			LOG.error("订单更新失败", e);
			flag = GlobalConstant.RESP_CODE_047;
			throw e;
		}
		LOG.info("---------------订单：" + t.getOrderNum() + ",更新完成状态  end--------");
		return flag;
	}

	@Override
	public String finishInputOrderStatus(String orderNum, PayOrder payOrder) throws Exception {
		LOG.info("---------------订单：" + orderNum + ",更新完成状态  start--------");
		String flag = GlobalConstant.RESP_CODE_SUCCESS;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderNum", orderNum);
		Set<Integer> status = new HashSet<Integer>();
		status.add(UserOrder.order_status.PROCESSING.getCode());
		status.add(UserOrder.order_status.MANUAL_PROCESSING.getCode());
		params.put("status", status);

		String hql = " select t from TuserOrder t left join t.tranPayOrder p  left join t.user u left join p.payChannel c where t.orderNum=:orderNum and t.status in(:status) ";
		TuserOrder t = userOrderDao.get(hql, params);
		if (t != null) {
			try {
				/* 收到通道消息后，更新订单情况 */
				updatePayOrderAfterChannelReturnInfo(payOrder, t);
				if (payOrder.getStatus() == PayOrder.pay_status.SUCCESS.getCode()) {
					/* 支付成功后，对于购买代理的订单，直接升级用户的等级； 对于普通的收款订单，需要完成账户信息的更新 */
					if (t.getTransPayType() - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0) {
						userService.updateUserType(t.getUser().getId(), t.getOrgAmt(), null);
					} else {
						try {
							Map<String, String> bigTranTypes = dictionaryService.comboxMap("bigTranType");
							String desc = t.getUser().getName() + "的" + bigTranTypes.get(t.getInputAccType()) + "账户增加了" + t.getAmt() + "元";
							Taccount acc = null;
							if (t.getTranPayOrder().getPayChannel().getName().equals("YIQIANGZTC")) {
								LOG.info("-----------亿强银联直通车处理start------------------");
								troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
								LOG.info("-------------亿强银联直通车处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("SHENFUZTC")) {
								LOG.info("-----------申孚银联直通车处理start------------------");
								troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
								LOG.info("-------------申孚银联直通车处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("WEILIANBAOJFZTC")) {
								LOG.info("-----------微联宝银联直通车处理start------------------");
								troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
								LOG.info("-------------微联宝银联直通车处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("MINGSHENGZHITONGCHE")) {
								LOG.info("-------------直通车处理start------------------");
								// String descZHI = t.getUser().getName() + "的"
								// + bigTranTypes.get(t.getInputAccType()) +
								// "账户解冻直通车金额" + t.getAmt() + "元";
								// acc =
								// accountService.updateAccountTroughInfo(t.getUser().getId(),
								// t.getOrgAmt(), descZHI);
								if (t.getInputAccType() == 0) {
									t.setFee(t.getFee().add(new BigDecimal(TroughTrainT0drawFee)));
									t.setAmt(t.getAmt().subtract(new BigDecimal(TroughTrainT0drawFee)));
									userOrderDao.update(t);
									Tchannel tcl = channelDao.get("select t from Tchannel t  where t.status=3 and t.payType=0 and t.userId=" + t.getUser().getId() + "and t.type=" + t.getType() + " and t.name='MINGSHENGZHITONGCHE' ");
									if (tcl != null) {
										LOG.info("-------------直通车D0start------------------");
										LOG.info("-------------直通车提现start------------------");
										Map<String, String> TiXian = new HashMap<String, String>();
										TiXian.put("operator_id", String.valueOf(t.getUser().getId()));
										TiXian.put("operator_name", t.getUser().getRealName());
										minshengPaymentService.dealChannelT0Tixian(tcl.getId(), TiXian);
										LOG.info("-------------直通车提现end------------------");
									}
									LOG.info("-------------直通车D0end------------------");
								} else if (t.getInputAccType() == 1) {
									LOG.info("-------------直通车处理T1start------------------");
									UserOrder userorder = new UserOrder();
									userorder.setCreateDatetimeStart(DateUtil.DateStart());
									userorder.setCreateDatetimeEnd(DateUtil.DateEnd());
									Map<String, Object> paraorder = new HashMap<String, Object>();
									paraorder.put("statementDateStart", userorder.getCreateDatetimeStart());
									paraorder.put("statementDateEnd", userorder.getCreateDatetimeEnd());
									paraorder.put("userId", t.getUser().getId());
									StringBuffer sb = new StringBuffer();
									sb.append(" select count(t.id) from trans_order t left join sys_user u on t.USER_ID = u.ID  " + " left join tran_pay_order p on t.ID = p.ID left join sys_channel c on p.pay_channel_id = c.ID "
											+ " where  t.input_Acc_Type='1' and t.`STATUS`='100' and c.name='MINGSHENGZHITONGCHE' " + " and u.ID=:userId and t.create_time >= :statementDateStart and t.create_time<= :statementDateEnd  ");
									List<Object[]> accs = userOrderDao.findBySql(sb.toString(), paraorder);
									if (Integer.parseInt(String.valueOf(accs.get(0))) == 0) {
										t.setFee(t.getFee().add(new BigDecimal(TroughTrainT1drawFee)));
										t.setAmt(t.getAmt().subtract(new BigDecimal(TroughTrainT1drawFee)));
										userOrderDao.update(t);
									}
									troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
									LOG.info("-------------直通车处理T1end------------------");
								}
								LOG.info("-------------直通车处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("XINKKEZHITONGCHE")) {
								LOG.info("-------------欣客直通车处理start------------------");
								if (t.getInputAccType() == 0) {
									LOG.info("-------------欣客直通车D0start------------------");
									t.setFee(t.getFee().add(new BigDecimal(TroughTrainT0drawFee)));
									t.setAmt(t.getAmt().subtract(new BigDecimal(TroughTrainT0drawFee)));
									userOrderDao.update(t);

									Tchannel tcl = channelDao.get("select t from Tchannel t  where t.status=3 and t.payType=0 and t.userId=" + t.getUser().getId() + "and t.name='XINKKEZHITONGCHE'");
									if (tcl != null) {
										LOG.info("-------------欣客直通车提现start------------------");
										Map<String, String> cinkemap = new HashMap<String, String>();
										cinkemap.put("operator_id", String.valueOf(t.getUser().getId()));
										cinkemap.put("operator_name", t.getUser().getRealName());
										xinkePaymentService.dealChannelT0Tixian(tcl.getId(), cinkemap);
										LOG.info("-------------欣客直通车提现end------------------");
									}
									LOG.info("-------------欣客直通车D0end------------------");
								} else if (t.getInputAccType() == 1) {
									LOG.info("-------------欣客直通车处理T1start------------------");
									UserOrder userorder = new UserOrder();
									userorder.setCreateDatetimeStart(DateUtil.DateStart());
									userorder.setCreateDatetimeEnd(DateUtil.DateEnd());
									Map<String, Object> paraorder = new HashMap<String, Object>();
									paraorder.put("statementDateStart", userorder.getCreateDatetimeStart());
									paraorder.put("statementDateEnd", userorder.getCreateDatetimeEnd());
									paraorder.put("userId", t.getUser().getId());
									StringBuffer sb = new StringBuffer();
									sb.append(" select count(t.id) from trans_order t left join sys_user u on t.USER_ID = u.ID  " + " left join tran_pay_order p on t.ID = p.ID left join sys_channel c on p.pay_channel_id = c.ID "
											+ " where  t.input_Acc_Type='1' and t.`STATUS`='100' and c.name='XINKKEZHITONGCHE' " + " and u.ID=:userId and t.create_time >= :statementDateStart and t.create_time<= :statementDateEnd  ");
									List<Object[]> accs = userOrderDao.findBySql(sb.toString(), paraorder);
									if (Integer.parseInt(String.valueOf(accs.get(0))) == 0) {
										t.setFee(t.getFee().add(new BigDecimal(TroughTrainT1drawFee)));
										t.setAmt(t.getAmt().subtract(new BigDecimal(TroughTrainT1drawFee)));
										userOrderDao.update(t);
									}
									troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
									LOG.info("-------------欣客直通车处理T1end------------------");
								}
								LOG.info("-------------欣客直通车处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("YILIANYINLIANJIFENZTC")) {
								LOG.info("-------------易联银联积分D0直通车处理start------------------");
								troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
								LOG.info("-------------易联银联积分D0直通车处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("YILIANZHIFU")) {
								LOG.info("-------------易联银联直通车处理start------------------");
								troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
								LOG.info("-------------易联银联直通车处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("YILIANZHIFUZTC")) {
								LOG.info("-------------易联银联小额直通车处理start------------------");
								troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
								LOG.info("-------------易联银联小额直通车处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("YIBAOZHITONGCHE")) {
								LOG.info("-------------易宝银联积分T0直通车处理start------------------");
								troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
								LOG.info("-------------易宝银联积分T0直通车处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("GAZHIYINLIANJIFENZHITONGCHE")) {
								LOG.info("-------------嘎吱银联积分T0直通车处理start------------------");
								troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
								LOG.info("-------------嘎吱银联积分T0直通车处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("XINKKEYINLIAN")) {
								LOG.info("-----------欣客银联直通车处理start------------------");
								troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
								LOG.info("-------------欣客银联直通车处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("XINKEYINLIAN")) {
								LOG.info("-----------欣客银联直通车处理start------------------");
								troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
								LOG.info("-------------欣客银联直通车处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("ZHEYANGZTC")) {
								LOG.info("-----------哲扬银联直通车处理start------------------");
								troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
								LOG.info("-------------哲扬银联直通车处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("PINGANPAYZHITONGCHE")) {
								LOG.info("-----------平安直通车处理start------------------");
								troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
								LOG.info("-------------平安银联直通车处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("PINGANPAYZHITONGCHE_ZHIQING")) {
								LOG.info("-----------平安直通车直清模式处理start------------------");
								troughTrainServeice.updateAccount(String.valueOf(t.getUser().getId()), String.valueOf(t.getOrgAmt()));
								LOG.info("-------------平安银联直通车直清模式处理end------------------");
							} else if (t.getTranPayOrder().getPayChannel().getName().equals("ZANSHANFU") || t.getTranPayOrder().getPayChannel().getName().equals("YILIANZHIFU") || t.getTranPayOrder().getPayChannel().getName().equals("XINKE")
									|| t.getTranPayOrder().getPayChannel().getName().equals("WEIXIN") || t.getTranPayOrder().getPayChannel().getName().equals("SHENFU") || t.getTranPayOrder().getPayChannel().getName().equals("PINGANPAY")
									|| t.getTranPayOrder().getPayChannel().getName().equals("MINSHENG") || t.getTranPayOrder().getPayChannel().getName().equals("QUANTONG") || t.getTranPayOrder().getPayChannel().getName().equals("GAZHI")
									|| t.getTranPayOrder().getPayChannel().getName().equals("ALIPAY")) {
								// 由于2017-12月份平台全部通道切换至直清模式，所以下面机制杀掉 ---开始
								// acc =
								// accountService.updateIncreaseAccountAfterSuccessInfo(t.getOrderNum(),
								// t.getUser().getId(), t.getInputAccType(),
								// t.getAmt(), desc);
								// 由于2017-12月份平台全部通道切换至直清模式，所以下面机制杀掉 ---开始
							}
							if (acc != null) {
								t.getTranPayOrder().setAvlAccAmt(acc.getAvlAmt());
								payOrderDao.update(t.getTranPayOrder());
							}
						} catch (Exception e) {
							LOG.error("账户更新失败", e);
							flag = GlobalConstant.RESP_CODE_047;
							throw e;
						}
					}

					/* 开始分润 */
					try {
						dealStartShareBonus(t);
					} catch (Exception e) {
						/* 可能存在锁表问题，但分润抛出异常时，不允许回滚 */
						LOG.error("分润失败", e);
					}
					/* 更新通道金额 */
					try {
						if (t.getTranPayOrder() != null) {
							channelService.appendTodayAmt(t.getTranPayOrder().getPayChannel().getId(), t.getAmt());
						}
					} catch (Exception e) {
						/* 可能存在锁表问题，但通道方金额抛出异常时，不允许回滚 */
						LOG.error("更新通道累计金额失败", e);
					}
				}

			} catch (Exception e) {
				LOG.error("订单更新失败", e);
				flag = GlobalConstant.RESP_CODE_047;
				throw e;
			}
		} else {
			flag = GlobalConstant.RESP_CODE_015;
			LOG.info("待完成的订单" + orderNum + "，不存在或已经完成支付。");
		}
		LOG.info("---------------订单：" + orderNum + ",更新完成状态  end--------");
		return flag;
	}

	/**
	 * 收到通道给出的回执消息，根据回执消息更新支付订单
	 * 
	 * @param payOrder
	 * @param t
	 */
	private void updatePayOrderAfterChannelReturnInfo(PayOrder payOrder, TuserOrder t) {

		TranPayOrder transPayOrder = t.getTranPayOrder();
		transPayOrder.setErrorCode(payOrder.getErrorCode());
		transPayOrder.setErrorInfo(payOrder.getErrorInfo());
		transPayOrder.setFinishDate(new Date());
		if (payOrder.getRealAmt() != null) {
			transPayOrder.setRealAmt(payOrder.getRealAmt());// 实际通道结算的金额
		}
		if (StringUtil.isNotBlank(payOrder.getPayFinishDate())) {
			transPayOrder.setPayFinishDate(payOrder.getPayFinishDate());
		}
		if (StringUtil.isNotBlank(payOrder.getPayNo())) {
			transPayOrder.setPayNo(payOrder.getPayNo());
		}
		if (payOrder.getFinishDate() != null) {
			transPayOrder.setFinishDate(payOrder.getFinishDate());
		}

		transPayOrder.setStatus(payOrder.getStatus());
		if (payOrder.getStatus() == PayOrder.pay_status.SUCCESS.getCode()) {
			t.setStatus(UserOrder.order_status.SUCCESS.getCode());
		} else {
			t.setStatus(UserOrder.order_status.FAILURE.getCode());
		}
		payOrderDao.update(transPayOrder);
		userOrderDao.update(t);
	}

	@Override
	public TuserOrder getOrderByPayNo(String payNo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("payNo", payNo.trim());
		String hql = " select t from TuserOrder t left join t.user u left join t.tranPayOrder p where p.payNo=:payNo";
		return userOrderDao.get(hql, params);
	}

	@Override
	public TuserOrder getTorderByOrderNo(String orderNum) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderNum", orderNum.trim());
		String hql = " select t from TuserOrder t left join t.user u left join t.tranPayOrder p left join p.payChannel where t.orderNum=:orderNum";
		return userOrderDao.get(hql, params);
	}

	@Override
	public List<UserOrder> findUserOrderByOrderNums(String[] orderNums) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderNum", orderNums);
		String hql = " select t from TuserOrder t where t.orderNum in(:orderNum)";
		List<TuserOrder> ts = userOrderDao.find(hql, params);
		List<UserOrder> us = new ArrayList<UserOrder>();
		for (TuserOrder t : ts) {
			UserOrder u = new UserOrder();
			BeanUtils.copyProperties(t, u);
			us.add(u);
		}
		return us;
	}

	@Override
	public String affirmOrderStatus(Long orderId, Boolean isSuccess, User operator) throws Exception {
		String msg = GlobalConstant.RESP_CODE_SUCCESS;
		String hql = " select t from TuserOrder t left join t.tranPayOrder p left join t.user u where  t.id=:orderId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderId", orderId);
		TuserOrder order = userOrderDao.get(hql, params);
		if (order != null) {
			PayOrder payOrder = new PayOrder();
			payOrder.setErrorInfo("人工确认订单");
			payOrder.setRealAmt(order.getAmt());
			payOrder.setPayNo("MANUAL_" + order.getOrderNum());
			payOrder.setStatus(isSuccess ? PayOrder.pay_status.SUCCESS.getCode() : PayOrder.pay_status.FAILURE.getCode());
			/* 更新订单 */
			updatePayOrderAfterChannelReturnInfo(payOrder, order);

			Taccount acc = accDao.get("select t from Taccount t left join t.user u where u.id=" + order.getUser().getId());
			Tbrokerage bk = brokerageDao.get("select t from Tbrokerage t left join t.user u where u.id=" + order.getUser().getId());
			Boolean isChanage = false;
			if (isSuccess) {
				/*
				 * 若用户判定订单正确，系统需要判断是否是购买代理，若购买代理的订单，帮助用户设置等级，并进行分润；
				 * 若为普通收款订单，系统需要在可用金额中添加订单实际金额；若为取现订单，系统需要减少锁定金额
				 */
				/* 若是app购买代理订单 */
				if (order.getTransPayType() == UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode()) {

					try {
						String desc = null;
						if (order.getCdType().equals(UserOrder.cd_type.C.name())) {
							if (bk.getLockBrokerage().subtract(order.getOrgAmt()).compareTo(BigDecimal.ZERO) >= 0) {
								bk.setLockBrokerage(bk.getLockBrokerage().subtract(order.getOrgAmt()));
								desc = operator.getLoginName() + "人工确认订单成功，账户金额减少了" + order.getOrgAmt().doubleValue() + "元";
								brokerageDao.update(bk);

								// TODO
								// 佣金提现成功日志
								TbrokerageLog brokerageLog = new TbrokerageLog(bk, UserOrder.cd_type.T.name(), order.getOrgAmt(), desc);
								brokerageLog.setAvlAmt(bk.getBrokerage());
								brokerageLog.setLockOutAmt(bk.getLockBrokerage());
								brokerageLog.setOrdernum(order.getOrderNum());
								brokerageLogDao.save(brokerageLog);

								isChanage = true;
							} else {
								throw new Exception("订单冻结金额小于提现金额，订单异常，请再次确认");
							}
						} else {
							try {
								/* 若用户购买代理订单成功，需要送出用户购买的代理 */
								userService.updateUserType(order.getUser().getId(), order.getOrgAmt(), null);
								desc = operator.getLoginName() + "人工确认订单成功，用户的身份已经改变";
								isChanage = true;
							} catch (Exception e) {
								throw e;
							}
						}
						LOG.info(desc);
					} catch (Exception e) {
						LOG.error("账户更新失败", e);
						throw e;
					}
				} else {
					try {
						// 普通用户交易,非购买代理
						String desc = null;
						BigDecimal amt = BigDecimal.ZERO;
						/* 账户 提现 */
						if (order.getCdType().equals(UserOrder.cd_type.C.name())) {
							if (acc.getLockOutAmt().subtract(order.getOrgAmt()).compareTo(BigDecimal.ZERO) >= 0) {
								acc.setLockOutAmt(acc.getLockOutAmt().subtract(order.getOrgAmt()));
								acc.setPerMonthOutAmt(acc.getPerMonthOutAmt().add(order.getOrgAmt()));
								amt = order.getOrgAmt();
								desc = operator.getLoginName() + "人工确认订单成功，账户金额减少了" + order.getOrgAmt().doubleValue() + "元";
								isChanage = true;

								/* 添加账户日志 */
								TaccountLog accountLog = new TaccountLog(acc, order.getCdType(), amt, desc);
								accountLog.setAvlAmt(acc.getAvlAmt());
								accountLogDao.save(accountLog);
								accDao.update(acc);
							} else {
								throw new Exception("订单冻结金额小于提现金额，订单异常，请再次确认");
							}
						} else {
							/* TODO 账户 充值 */
							String orderType = String.valueOf(order.getType());
							// 由于2017-12月份平台全部通道切换至直清模式，所以下面机制杀掉 ---开始
							// if ("520".equals(orderType) ||
							// "530".equals(orderType) ||
							// "550".equals(orderType)) {
							// LOG.info("银联直通车交易OderNo={} type={},不用增加余额.",
							// order.getOrderNum(), order.getType());
							// } else {
							// desc = operator.getLoginName() +
							// "人工确认订单成功，账户金额增加了" + order.getAmt().doubleValue()
							// + "元";
							// accountService.updateIncreaseAccountAfterSuccessInfo(order.getOrderNum(),
							// order.getUser().getId(), order.getInputAccType(),
							// order.getAmt(), desc);
							// }
							// 由于2017-12月份平台全部通道切换至直清模式，所以下面机制杀掉 ---结束

							LOG.info("目前平台都是直通车交易OderNo={} type={},不用增加余额.", order.getOrderNum(), order.getType());
							isChanage = true;
						}
					} catch (Exception e) {
						LOG.error("账户更新失败", e);
						throw e;
					}
				}
				if (isChanage) {
					dealStartShareBonus(order);
				}

			} else {
				if (order.getTransPayType() == UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode()) {

					/* 若人工判断订单失败，就是将账户中锁定的金额，变为可用金额 */
					try {
						/* 账户 加款 */
						if (order.getCdType().equals(UserOrder.cd_type.C.name())) {
							if (bk.getLockBrokerage().subtract(order.getOrgAmt()).compareTo(BigDecimal.ZERO) >= 0) {
								bk.setLockBrokerage(bk.getLockBrokerage().subtract(order.getOrgAmt()));
								bk.setBrokerage(bk.getBrokerage().add(order.getOrgAmt()));
								brokerageDao.update(bk);

								// TODO
								// 佣金提现失败日志
								String desc = operator.getLoginName() + "人工确认订单失败，账户金额增加了" + order.getOrgAmt().doubleValue() + "元";
								TbrokerageLog brokerageLog = new TbrokerageLog(bk, UserOrder.cd_type.F.name(), order.getOrgAmt(), desc);
								brokerageLog.setAvlAmt(bk.getBrokerage());
								brokerageLog.setLockOutAmt(bk.getLockBrokerage());
								brokerageLog.setOrdernum(order.getOrderNum());
								brokerageLogDao.save(brokerageLog);

								isChanage = true;
							} else {
								throw new Exception("订单冻结金额小于提现金额，订单异常，请再次确认");
							}
						}
					} catch (Exception e) {
						LOG.error("账户更新失败", e);
						throw e;
					}

				} else {
					/* 若人工判断订单失败，就是将账户中锁定的金额，变为可用金额 */
					try {
						/* 账户 加款 */
						if (order.getCdType().equals(UserOrder.cd_type.C.name())) {
							if (acc.getLockOutAmt().subtract(order.getOrgAmt()).compareTo(BigDecimal.ZERO) >= 0) {
								acc.setLockOutAmt(acc.getLockOutAmt().subtract(order.getOrgAmt()));
								acc.setAvlAmt(acc.getAvlAmt().add(order.getOrgAmt()));
								String des = operator.getLoginName() + "人工确认订单失败，账户冻结金额减少了" + order.getOrgAmt().doubleValue() + "元，可用金额增加了" + order.getOrgAmt().doubleValue() + "元";
								/* 添加账户日志 */
								TaccountLog accountLog = new TaccountLog(acc, order.getCdType(), order.getOrgAmt(), des);
								accountLog.setAvlAmt(acc.getAvlAmt());
								accountLogDao.save(accountLog);
								accDao.update(acc);
								isChanage = true;
							} else {
								throw new Exception("订单冻结金额小于提现金额，订单异常，请再次确认");
							}
						}
					} catch (Exception e) {
						LOG.error("账户更新失败", e);
						throw e;
					}
				}
			}
		} else {
			msg = "系统异常，订单不存在，请确认";
		}
		return msg;
	}

	/**
	 * 根据交易订单进行分润
	 * 
	 * @param orderAndPayOrder
	 *            订单 包含支付订单对象
	 * @category 订单分为 购买代理订单 和 交易流水订单
	 */
	private void dealStartShareBonus(TuserOrder orderAndPayOrder) throws Exception {
		if (orderAndPayOrder.getUser().getAgentId().equals("F20160017") && orderAndPayOrder.getTransPayType() - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() != 0) {
			BigDecimal bc = BigDecimal.ZERO;
			Torganization org = organizationService.getTorganizationInCacheByCode(orderAndPayOrder.getUser().getAgentId());
			// TODO
			bc = orderAndPayOrder.getOrgAmt().multiply(orderAndPayOrder.getShareRate());
			TorderBonusProcess bonusProcess = new TorderBonusProcess(orderAndPayOrder, bc);
			bonusProcessDao.save(bonusProcess);
		} else {
			if (UserOrder.cd_type.D.name().equals(orderAndPayOrder.getCdType())) {
				/* 处理完成后，进行分润 */
				BigDecimal bd = BigDecimal.ZERO;

				if (orderAndPayOrder.getTransPayType() - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0) {
					/* 芦总建议： 购买代理的金额不扣除手续费，直接分润 2016-09-09 */
					bd = orderAndPayOrder.getOrgAmt();
				} else if (orderAndPayOrder.getInputAccType() == 0 || orderAndPayOrder.getInputAccType() == 1 || orderAndPayOrder.getInputAccType() == 10 || orderAndPayOrder.getInputAccType() == 11) {
					/* 插入分润通知表 , 该交易总共获得的利润= 商户实际金额 * 渠道分润比例 */
					// 芦总提出分润不扣除手续费，按总交易额计算 2017-12-12
					bd = orderAndPayOrder.getOrgAmt().multiply(orderAndPayOrder.getShareRate());
				} else {
					LOG.info("T5 订单不参与分佣");
				}
				/* 总的分润金额大于0.01时才会分润 */
				if (bd.compareTo(BigDecimal.ZERO) > 0) {
					TorderBonusProcess bonusProcess = new TorderBonusProcess(orderAndPayOrder, bd);
					bonusProcessDao.save(bonusProcess);
				} else {
					LOG.info("total amt " + bd.doubleValue() + " less than 0.01, none share bonus");
				}
			} else {
				LOG.info("提现订单不参与分佣");
			}
		}
	}

	@Override
	public String isAllowPayAgent(User u, Integer agentType) {
		String flag = GlobalConstant.RESP_CODE_SUCCESS;
		if (u.getUserType() - agentType <= 0) {
			flag = GlobalConstant.RESP_CODE_060;
		} else {
			String maxMini = paramService.searchSysParameter().get("max_pay_agent_time");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", u.getId());
			params.put("transPayType", UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode());
			params.put("status", UserOrder.order_status.PROCESSING.getCode());
			params.put("createTime", DateUtil.getMinutebyInterval(new Date(), -1 * Integer.parseInt(maxMini)));
			List<TuserOrder> tod = userOrderDao.find("select t from TuserOrder t where t.user.id=:userId and  t.transPayType=:transPayType and t.status=:status and t.createTime>=:createTime", params);
			/* 若不为空 ，表示用户刚刚下过购买代理的订单 */
			if (tod != null && tod.size() > 0) {
				flag = GlobalConstant.RESP_CODE_059;
			}
		}
		return flag;
	}

	@Override
	public String dealProcessingOrderBeforeTwoDays(String dateStr, Set<Integer> tranTypes) {
		try {
			String hql = " select t from TuserOrder t left join t.user tu left join t.tranPayOrder p " + "where t.status = :status and t.type in(:transType) and  t.createTime >= :payDatetimeStart and t.createTime <= :payDatetimeEnd";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("status", UserOrder.order_status.PROCESSING.getCode());
			params.put("transType", tranTypes);
			params.put("payDatetimeStart", DateUtil.getStartOfDay(DateUtil.convertStringToDate("yyyyMMdd", dateStr)));
			params.put("payDatetimeEnd", DateUtil.getEndOfDay(DateUtil.convertStringToDate("yyyyMMdd", dateStr)));
			List<TuserOrder> l = userOrderDao.find(hql, params);
			if (l != null) {
				LOG.info(dateStr + "的两天之前有 " + l.size() + " 笔订单未处理,系统将自动判定订单失效");
				for (TuserOrder t : l) {
					LOG.info("系统自动判定订单号OrderNum =" + t.getOrderNum() + " 失效");
					PayOrder payOrder = new PayOrder();
					payOrder.setErrorCode("OVER_TWO_DAY");
					payOrder.setErrorInfo("订单超过2天没有给出对账单，系统自动判定订单失效");
					payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
					updatePayOrderAfterChannelReturnInfo(payOrder, t);
				}
			}
		} catch (Exception e) {
			LOG.error("处理未处理的两天前订单异常", e);
		}
		return null;
	}

	@Override
	public TuserOrder getTuserOrderByOrderNum(String orderNum) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderNum", orderNum);
		String hql = " select t from TuserOrder t left join t.user tu  where t.orderNum=:orderNum";
		return userOrderDao.get(hql, params);
	}

	@Override
	public Map<String, String> dealReSentSearchOrder(String orderNum) {

		TuserOrder order = getTorderByOrderNo(orderNum);
		Map<String, String> orderMsg = new HashMap<String, String>();
		if (order != null) {
			if (UserOrder.getCollectOrderTypes().contains(order.getType())) {
				ChannelPaymentService paymentService = routeService.getChannelPayRouteByChannelName(order.getTranPayOrder().getPayChannel().getName());
				if (paymentService != null) {
					return paymentService.sendOrderNumToChannelForSearchStatus(orderNum);
				}
				return orderMsg;
			} else if (UserOrder.trans_type.XJTX.getCode() == order.getType() || UserOrder.trans_type.YJTX.getCode() == order.getType()) {
				try {
					Map<String, String> ret = pingAnExpenseService.sendSearchOrderToPingAN(order.getId());
					String pinganMsg = GlobalConstant.map.get(ret.get("flag"));
					orderMsg.put("retMsg", pinganMsg);
					return orderMsg;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return orderMsg;
			}
		}
		return orderMsg;
	}

	/**
	 * 按照工作日统计
	 * 
	 * @category 暂时废弃，财务统计每天结算
	 */
	@Override
	public List<FinanceStatement> findFinanceStatement(FinanceStatement statement) {
		List<FinanceStatement> finananceStsList = new ArrayList<FinanceStatement>();
		try {

			Date endStatement = holidayService.getWorkDate(DateUtil.getDateFromString(statement.getStatemtentDate()));
			Date startStatement = holidayService.getWorkDateBeforeDate(endStatement, 1);

			FinanceStatement zanshanfu = getInputFinanceAccount(DateUtil.getDateFromString(statement.getStatemtentDate()), DateUtil.getDateFromString(statement.getStatemtentDate()), OrderStatement.statement_type.ZANSHANFU.name());
			finananceStsList.add(zanshanfu);

			FinanceStatement weixin = getInputFinanceAccount(startStatement, endStatement, OrderStatement.statement_type.WEIXIN.name());
			finananceStsList.add(weixin);

			FinanceStatement alipay = getInputFinanceAccount(startStatement, endStatement, OrderStatement.statement_type.ALIPAY.name());
			finananceStsList.add(alipay);

			FinanceStatement mingsheng = getInputFinanceAccount(startStatement, endStatement, OrderStatement.statement_type.MINSHENG.name());

			finananceStsList.add(mingsheng);
			FinanceStatement pinganPay = getInputFinanceAccount(startStatement, endStatement, OrderStatement.statement_type.PINGANPAY.name());
			finananceStsList.add(pinganPay);
			FinanceStatement xinkePay = getInputFinanceAccount(startStatement, endStatement, OrderStatement.statement_type.XINKE.name());
			finananceStsList.add(xinkePay);

			FinanceStatement t0Fs = getT0OutFinanceAccount(DateUtil.getDateFromString(statement.getStatemtentDate()), UserOrder.order_status.SUCCESS.getCode());
			finananceStsList.add(t0Fs);
			if (holidayService.isWorkDate(DateUtil.getDateFromString(statement.getStatemtentDate()))) {
				FinanceStatement t1Fs = getT1OutFinanceAccount(DateUtil.getDateFromString(statement.getStatemtentDate()), UserOrder.order_status.SUCCESS.getCode());
				finananceStsList.add(t1Fs);
			}
			FinanceStatement ysFs = getYesterOutFinanceAccount(DateUtil.getDateFromString(statement.getStatemtentDate()), UserOrder.order_status.SUCCESS.getCode());

			finananceStsList.add(ysFs);
		} catch (Exception e) {
			LOG.error("财务查询失败", e);
		}
		return finananceStsList;
	}

	/**
	 * 按照自然日统计
	 * 
	 */
	@Override
	public List<FinanceStatement> findFinanceStatementPerDate(FinanceStatement statement) {
		List<FinanceStatement> finananceStsList = new ArrayList<FinanceStatement>();
		try {

			Date startStatement = DateUtil.getBeforeDate(DateUtil.getDateFromString(statement.getStatemtentDate()), 0);
			Date endStatement = DateUtil.getDatebyInterval(startStatement, 1);

			FinanceStatement zanshanfu = getInputFinanceAccountPerDate(startStatement, endStatement, OrderStatement.statement_type.ZANSHANFU.name());
			finananceStsList.add(zanshanfu);

			FinanceStatement weixin = getInputFinanceAccountPerDate(startStatement, endStatement, OrderStatement.statement_type.WEIXIN.name());
			finananceStsList.add(weixin);

			FinanceStatement alipay = getInputFinanceAccountPerDate(startStatement, endStatement, OrderStatement.statement_type.ALIPAY.name());
			finananceStsList.add(alipay);

			FinanceStatement mingsheng_t0_tixian = getOutFinanceAccountPerDate(startStatement, endStatement, OrderStatement.statement_type.MINSHENG.name());
			finananceStsList.add(mingsheng_t0_tixian);

			FinanceStatement mingsheng = getInputFinanceAccountPerDate(startStatement, endStatement, OrderStatement.statement_type.MINSHENG.name());
			dealFinanceStatementD0Tixian(mingsheng, mingsheng_t0_tixian, OrderStatement.statement_type.MINSHENG.name());
			finananceStsList.add(mingsheng);

			FinanceStatement pinganPay = getInputFinanceAccountPerDate(startStatement, endStatement, OrderStatement.statement_type.PINGANPAY.name());
			finananceStsList.add(pinganPay);
			FinanceStatement pingan = getInputFinanceAccountPerDate(startStatement, endStatement, OrderStatement.statement_type.PINGAN.name());
			finananceStsList.add(pingan);
			FinanceStatement xinkePay = getInputFinanceAccountPerDate(startStatement, endStatement, OrderStatement.statement_type.XINKE.name());
			finananceStsList.add(xinkePay);

			FinanceStatement t0Fs = getOutFinanceAccount(DateUtil.getHoursbyInterval(startStatement, -1), DateUtil.getHoursbyInterval(endStatement, -1), 0, UserOrder.order_status.SUCCESS.getCode());
			finananceStsList.add(t0Fs);
			FinanceStatement t1Fs = getOutFinanceAccount(DateUtil.getHoursbyInterval(startStatement, -25), DateUtil.getHoursbyInterval(endStatement, -25), 1, UserOrder.order_status.SUCCESS.getCode());
			finananceStsList.add(t1Fs);
		} catch (Exception e) {
			LOG.error("财务查询失败", e);
		}
		return finananceStsList;
	}

	private void dealFinanceStatementD0Tixian(FinanceStatement inputFinanceStatement, FinanceStatement outFinanceStatement, String channelName) {
		BigDecimal feeRate = new BigDecimal(MINGSHENGD0);
		BigDecimal inputfeeRate = new BigDecimal(MINGSHENGT1);
		BigDecimal ze = BigDecimal.valueOf(1d);
		BigDecimal min = inputFinanceStatement.getTradeAmt().subtract(outFinanceStatement.getTradeAmt()).multiply(inputfeeRate).setScale(2, BigDecimal.ROUND_DOWN);
		BigDecimal minD0 = outFinanceStatement.getTradeAmt().multiply(feeRate).setScale(2, BigDecimal.ROUND_DOWN).add(outFinanceStatement.getOperateFee());
		inputFinanceStatement.setFeeAmt(min.add(minD0));
		inputFinanceStatement.setRealInputAmt(inputFinanceStatement.getTradeAmt().subtract(outFinanceStatement.getTradeAmt()).multiply(ze.subtract(inputfeeRate)).setScale(2, BigDecimal.ROUND_DOWN));
	}

	/**
	 * 工作日统计
	 * 
	 * @param startStatement
	 * @param endStatement
	 * @param channelName
	 * @return
	 */
	private FinanceStatement getInputFinanceAccount(Date startStatement, Date endStatement, String channelName) {
		String input_order_sql = "select SUM(r.org_amt), sum(ROUND(r.org_amt*c.real_rate, 2)), sum(if(ROUND(r.org_amt*c.real_rate, 2) > 0.01, ROUND(r.org_amt*c.real_rate, 2), 0.01)) ,COUNT(r.id) ,count( if( r.trans_pay_type =20, true,null)),count( if( r.trans_pay_type =10, true,null)) from trans_order r left join tran_pay_order p on p.id=r.id left join sys_channel c on c.id=p.pay_channel_id where c.name=:channelName and r.`STATUS`=100   and r.create_time  BETWEEN :createTimeStart and :createTimeEnd ";

		Map<String, Object> params = new HashMap<String, Object>();
		if (OrderStatement.statement_type.ZANSHANFU.name().equals(channelName)) {
			Date[] dts = holidayService.getT0StartAndEndStatementWorkDate(endStatement);
			startStatement = dts[0];
			endStatement = dts[1];
			params.put("createTimeStart", startStatement);
			params.put("createTimeEnd", endStatement);
			params.put("channelName", channelName);
		} else if (OrderStatement.statement_type.WEIXIN.name().equals(channelName)) {
			params.put("createTimeStart", startStatement);
			params.put("createTimeEnd", endStatement);
			params.put("channelName", channelName);

		} else if (OrderStatement.statement_type.ALIPAY.name().equals(channelName)) {
			params.put("createTimeStart", startStatement);
			params.put("createTimeEnd", endStatement);
			params.put("channelName", channelName);
		} else if (OrderStatement.statement_type.MINSHENG.name().equals(channelName)) {
			params.put("createTimeStart", startStatement);
			params.put("createTimeEnd", endStatement);
			params.put("channelName", channelName);
		} else if (OrderStatement.statement_type.XINKE.name().equals(channelName)) {
			params.put("createTimeStart", startStatement);
			params.put("createTimeEnd", endStatement);
			params.put("channelName", channelName);
		} else if (OrderStatement.statement_type.PINGANPAY.name().equals(channelName)) {
			input_order_sql = "select SUM(r.org_amt), sum(ROUND(r.org_amt*c.real_rate, 2)), sum(if(ROUND(r.org_amt*c.real_rate, 2) > 0.01, ROUND(r.org_amt*c.real_rate, 2), 0.01)) ,COUNT(r.id) ,count( if( r.trans_pay_type =20, true,null)),count( if( r.trans_pay_type =10, true,null)),"
					+ " sum(if(ROUND(r.org_amt*:rate_plat, 2) >= 0.01, ROUND(r.org_amt*:rate_plat, 2), 0)) ," + " sum(if(ROUND(r.org_amt*:rate_clear, 2) >= 0.01, ROUND(r.org_amt*:rate_clear, 2), 0)) ,"
					+ " sum(if(ROUND(r.org_amt*:rate_user, 2) >= 0.01, ROUND(r.org_amt*:rate_user, 2), 0)) "
					+ " from trans_order r left join tran_pay_order p on p.id=r.id left join sys_channel c on c.id=p.pay_channel_id where c.name=:channelName and r.`STATUS`=100   and r.create_time  BETWEEN :createTimeStart and :createTimeEnd ";

			String[] rates = paramService.searchSysParameter().get("pingan_pay_rates").split("\\|");
			params.put("createTimeStart", startStatement);
			params.put("createTimeEnd", endStatement);
			params.put("channelName", channelName);

			params.put("rate_plat", BigDecimal.valueOf(Double.parseDouble(rates[0])));
			params.put("rate_clear", BigDecimal.valueOf(Double.parseDouble(rates[1])));
			params.put("rate_user", BigDecimal.valueOf(Double.parseDouble(rates[2])));

		}

		List<Object[]> financeInfos = userOrderDao.findBySql(input_order_sql, params);
		FinanceStatement fs = new FinanceStatement(1, DateUtil.getStringFromDate(startStatement, DateUtil.FORMAT_YYYY_MM_DD_HH_mm_ss), DateUtil.getStringFromDate(endStatement, DateUtil.FORMAT_YYYY_MM_DD_HH_mm_ss), channelName);
		/* 交易总金额 */
		if (financeInfos.get(0)[0] != null) {
			fs.setTradeAmt(((BigDecimal) financeInfos.get(0)[0]).setScale(2, BigDecimal.ROUND_DOWN));
		} else {
			fs.setTradeAmt(BigDecimal.ZERO);
		}
		/* 实际手续费，四舍五入 */
		if (financeInfos.get(0)[1] != null && (OrderStatement.statement_type.ALIPAY.name().equals(channelName) || OrderStatement.statement_type.WEIXIN.name().equals(channelName)
				|| OrderStatement.statement_type.XINKE.name().equals(channelName) || OrderStatement.statement_type.MINSHENG.name().equals(channelName))) {
			fs.setFeeAmt(((BigDecimal) financeInfos.get(0)[1]).setScale(2, BigDecimal.ROUND_DOWN));
			/* 实际入账金额=总交易金额-实际手续费 */
			fs.setRealInputAmt(fs.getTradeAmt().subtract(fs.getFeeAmt()));
		} else if (financeInfos.get(0)[2] != null && OrderStatement.statement_type.ZANSHANFU.name().equals(channelName)) {
			/* 实际手续费，四舍五入 ，至少1分钱 */
			fs.setFeeAmt(((BigDecimal) financeInfos.get(0)[2]).setScale(2, BigDecimal.ROUND_DOWN));
			/* 实际入账金额=总交易金额-实际手续费 */
			fs.setRealInputAmt(fs.getTradeAmt().subtract(fs.getFeeAmt()));
		} else if (OrderStatement.statement_type.PINGANPAY.name().equals(channelName)) {
			/* 实际手续费，四舍五入 ，至少1分钱 */
			BigDecimal bdFee = BigDecimal.ZERO;
			if (financeInfos.get(0)[6] != null) {
				bdFee = bdFee.add(((BigDecimal) financeInfos.get(0)[6]));
			}
			if (financeInfos.get(0)[7] != null) {
				bdFee = bdFee.add(((BigDecimal) financeInfos.get(0)[7]));
			}
			fs.setFeeAmt(bdFee.setScale(2, BigDecimal.ROUND_DOWN));// 手续费=0.002+0.0005
			if (financeInfos.get(0)[8] != null) {
				fs.setRealInputAmt(((BigDecimal) financeInfos.get(0)[8]).setScale(2, BigDecimal.ROUND_DOWN));// 用户获得0.9962
			}
			fs.setBrokerageAmt(fs.getTradeAmt().subtract(fs.getFeeAmt()).subtract(fs.getRealInputAmt()));
		} else {
			/* 实际入账金额=总交易金额-实际手续费 */
			fs.setRealInputAmt(fs.getTradeAmt().subtract(fs.getFeeAmt()));
			fs.setFeeAmt(BigDecimal.ZERO);
		}
		fs.setTradeNum(((BigInteger) financeInfos.get(0)[3]).longValue());
		fs.setAgentTradeNum(((BigInteger) financeInfos.get(0)[4]).longValue());
		fs.setAmtTradeNum(((BigInteger) financeInfos.get(0)[5]).longValue());
		return fs;
	}

	/**
	 * 自然日统计
	 * 
	 * @param startStatement
	 * @param endStatement
	 * @param channelName
	 * @return
	 */
	private FinanceStatement getInputFinanceAccountPerDate(Date startStatement, Date endStatement, String channelName) {
		  //String input_order_sql = "select SUM(r.org_amt), sum(ROUND(r.org_amt*c.real_rate, 2)), sum(if(ROUND(r.org_amt*c.real_rate, 2) > 0.01, ROUND(r.org_amt*c.real_rate, 2), 0.01)) ,COUNT(r.id) ,count( if( r.trans_pay_type =20, true,null)),count( if( r.trans_pay_type =10, true,null)) from trans_order r left join tran_pay_order p on p.id=r.id left join sys_channel c on c.id=p.pay_channel_id where c.name=:channelName and r.`STATUS`=100   and r.create_time  BETWEEN :createTimeStart and :createTimeEnd ";
	      String input_order_sql = "select SUM(r.org_amt), sum(ROUND(r.org_amt*c.real_rate, 2)), sum(if(ROUND(r.org_amt*c.real_rate, 2) > 0.01, ROUND(r.org_amt*c.real_rate, 2), 0.01)) ,COUNT(r.id) ,count( if( r.trans_pay_type =20, true,null)),count( if( r.trans_pay_type =10, true,null)) from trans_order r left join tran_pay_order p on p.id=r.id left join sys_channel c on c.id=p.pay_channel_id where c.name=:channelName and r.`STATUS`=100   and p.finish_date  BETWEEN :createTimeStart and :createTimeEnd ";


		Map<String, Object> params = new HashMap<String, Object>();
		if (OrderStatement.statement_type.ZANSHANFU.name().equals(channelName)) {
			startStatement = DateUtil.getHoursbyInterval(startStatement, -1);
			endStatement = DateUtil.getHoursbyInterval(endStatement, -1);
			params.put("createTimeStart", startStatement);
			params.put("createTimeEnd", endStatement);
			params.put("channelName", channelName);
		} else if (OrderStatement.statement_type.WEIXIN.name().equals(channelName)) {
			params.put("createTimeStart", startStatement);
			params.put("createTimeEnd", endStatement);
			params.put("channelName", channelName);

		} else if (OrderStatement.statement_type.ALIPAY.name().equals(channelName)) {
			params.put("createTimeStart", startStatement);
			params.put("createTimeEnd", endStatement);
			params.put("channelName", channelName);
		} else if (OrderStatement.statement_type.MINSHENG.name().equals(channelName)) {
			params.put("createTimeStart", startStatement);
			params.put("createTimeEnd", endStatement);
			params.put("channelName", channelName);
		} else if (OrderStatement.statement_type.XINKE.name().equals(channelName)) {
			params.put("createTimeStart", startStatement);
			params.put("createTimeEnd", endStatement);
			params.put("channelName", channelName);
		} else if (OrderStatement.statement_type.PINGANPAY.name().equals(channelName)) {
			input_order_sql = "select SUM(r.org_amt), su(ROUND(r.org_amt*c.real_rate, 2)), sum(if(ROUND(r.org_amt*c.real_rate, 2) > 0.01, ROUND(r.org_amt*c.real_rate, 2), 0.01)) ,COUNT(r.id) ,count( if( r.trans_pay_type =20, true,null)),count( if( r.trans_pay_type =10, true,null)),"
					+ " sum(if(ROUND(r.org_amt*:rate_plat, 2) >= 0.01, ROUND(r.org_amt*:rate_plat, 2), 0)) ," + " sum(if(ROUND(r.org_amt*:rate_clear, 2) >= 0.01, ROUND(r.org_amt*:rate_clear, 2), 0)) ,"
					+ " sum(if(ROUND(r.org_amt*:rate_user, 2) >= 0.01, ROUND(r.org_amt*:rate_user, 2), 0)) "
					+ " from trans_order r left join tran_pay_order p on p.id=r.id left join sys_channel c on c.id=p.pay_channel_id where c.name='PINGANPAY' and r.`STATUS`=100   and r.create_time  BETWEEN :createTimeStart and :createTimeEnd ";

			String[] rates = paramService.searchSysParameter().get("pingan_pay_rates").split("\\|");
			params.put("createTimeStart", startStatement);
			params.put("createTimeEnd", endStatement);
			// params.put("channelName", channelName);
			params.put("rate_plat", BigDecimal.valueOf(Double.parseDouble(rates[0])));
			params.put("rate_clear", BigDecimal.valueOf(Double.parseDouble(rates[1])));
			params.put("rate_user", BigDecimal.valueOf(Double.parseDouble(rates[2])));
		} else if (OrderStatement.statement_type.PINGAN.name().equals(channelName)) {
			input_order_sql = "select SUM(r.org_amt), sum(ROUND(r.org_amt*c.real_rate, 2)), sum(if(ROUND(r.org_amt*c.real_rate, 2) > 0.01, ROUND(r.org_amt*c.real_rate, 2), 0.01)) ,COUNT(r.id) ,count( if( r.trans_pay_type =20, true,null)),count( if( r.trans_pay_type =10, true,null)),"
					+ " sum(if(ROUND(r.org_amt*:rate_plat, 2) >= 0.01, ROUND(r.org_amt*:rate_plat, 2), 0)) ," + " sum(if(ROUND(r.org_amt*:rate_clear, 2) >= 0.01, ROUND(r.org_amt*:rate_clear, 2), 0)) ,"
					+ " sum(if(ROUND(r.org_amt*:rate_user, 2) >= 0.01, ROUND(r.org_amt*:rate_user, 2), 0)) "
					+ " from trans_order r left join tran_pay_order p on p.id=r.id left join sys_channel c on c.id=p.pay_channel_id where c.name in('PINGANPAYZHITONGCHE','PINGANPAYZHITONGCHE_ZHIQING') and r.`STATUS`=100   and r.create_time  BETWEEN :createTimeStart and :createTimeEnd ";

			String[] rates = paramService.searchSysParameter().get("pingan_pay_rates").split("\\|");
			params.put("createTimeStart", startStatement);
			params.put("createTimeEnd", endStatement);
			// params.put("channelName", channelName);

			params.put("rate_plat", BigDecimal.valueOf(Double.parseDouble(rates[0])));
			params.put("rate_clear", BigDecimal.valueOf(Double.parseDouble(rates[1])));
			params.put("rate_user", BigDecimal.valueOf(Double.parseDouble(rates[2])));

		}

		List<Object[]> financeInfos = userOrderDao.findBySql(input_order_sql, params);
		FinanceStatement fs = new FinanceStatement(1, DateUtil.getStringFromDate(startStatement, DateUtil.FORMAT_YYYY_MM_DD_HH_mm_ss), DateUtil.getStringFromDate(endStatement, DateUtil.FORMAT_YYYY_MM_DD_HH_mm_ss), channelName);
		/* 交易总金额 */
		if (financeInfos.get(0)[0] != null) {
			fs.setTradeAmt(((BigDecimal) financeInfos.get(0)[0]).setScale(2, BigDecimal.ROUND_DOWN));
		} else {
			fs.setTradeAmt(BigDecimal.ZERO);
		}
		/* 实际手续费，四舍五入 */
		if (financeInfos.get(0)[1] != null && (OrderStatement.statement_type.ALIPAY.name().equals(channelName) || OrderStatement.statement_type.WEIXIN.name().equals(channelName)
				|| OrderStatement.statement_type.XINKE.name().equals(channelName) || OrderStatement.statement_type.MINSHENG.name().equals(channelName))) {
			fs.setFeeAmt(((BigDecimal) financeInfos.get(0)[1]).setScale(2, BigDecimal.ROUND_DOWN));
			/* 实际入账金额=总交易金额-实际手续费 */
			fs.setRealInputAmt(fs.getTradeAmt().subtract(fs.getFeeAmt()));
		} else if (financeInfos.get(0)[2] != null && OrderStatement.statement_type.ZANSHANFU.name().equals(channelName)) {
			/* 实际手续费，四舍五入 ，至少1分钱 */
			fs.setFeeAmt(((BigDecimal) financeInfos.get(0)[2]).setScale(2, BigDecimal.ROUND_DOWN));
			/* 实际入账金额=总交易金额-实际手续费 */
			fs.setRealInputAmt(fs.getTradeAmt().subtract(fs.getFeeAmt()));
		} else if (OrderStatement.statement_type.PINGANPAY.name().equals(channelName)) {
			/* 实际手续费，四舍五入 ，至少1分钱 */
			BigDecimal bdFee = BigDecimal.ZERO;
			if (financeInfos.get(0)[6] != null) {
				bdFee = bdFee.add(((BigDecimal) financeInfos.get(0)[6]));
			}
			if (financeInfos.get(0)[7] != null) {
				bdFee = bdFee.add(((BigDecimal) financeInfos.get(0)[7]));
			}
			fs.setFeeAmt(bdFee.setScale(2, BigDecimal.ROUND_DOWN));// 手续费=0.002+0.0005
			if (financeInfos.get(0)[8] != null) {
				fs.setRealInputAmt(((BigDecimal) financeInfos.get(0)[8]).setScale(2, BigDecimal.ROUND_DOWN));// 用户获得0.9962
			}
			fs.setBrokerageAmt(fs.getTradeAmt().subtract(fs.getFeeAmt()).subtract(fs.getRealInputAmt()));
		} else {
			/* 实际入账金额=总交易金额-实际手续费 */
			fs.setRealInputAmt(fs.getTradeAmt().subtract(fs.getFeeAmt()));
			fs.setFeeAmt(BigDecimal.ZERO);
		}
		fs.setTradeNum(((BigInteger) financeInfos.get(0)[3]).longValue());
		fs.setAgentTradeNum(((BigInteger) financeInfos.get(0)[4]).longValue());
		fs.setAmtTradeNum(((BigInteger) financeInfos.get(0)[5]).longValue());
		return fs;
	}

	/**
	 * 自然日统计D0提现
	 * 
	 * @param startStatement
	 * @param endStatement
	 * @param channelName
	 * @return
	 */
	private FinanceStatement getOutFinanceAccountPerDate(Date startStatement, Date endStatement, String channelName) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT sum(amt+draw_fee+trade_Fee) as trade_amt,");
		sb.append(" sum(draw_fee+trade_Fee) as total_fee, ");
		sb.append(" sum(amt) as real_aml,   ");
		sb.append(" count(t.id) ,");
		sb.append(" sum(trade_Fee) , ");
		sb.append(" sum(draw_fee) as operate_fee ");
		sb.append(" from sys_channel_t0_tixian t  ");
		sb.append(" left join sys_channel cl on cl.id=t.chl_id ");
		sb.append(" where t.`status`=100 and cl.`name`=:channelName and create_date BETWEEN  :createTimeStart and :createTimeEnd ");
		String input_order_sql = sb.toString();

		Map<String, Object> params = new HashMap<String, Object>();
		FinanceStatement fs = new FinanceStatement(1, DateUtil.getStringFromDate(startStatement, DateUtil.FORMAT_YYYY_MM_DD_HH_mm_ss), DateUtil.getStringFromDate(endStatement, DateUtil.FORMAT_YYYY_MM_DD_HH_mm_ss), channelName);
		if (OrderStatement.statement_type.MINSHENG.name().equals(channelName)) {
			params.put("createTimeStart", startStatement);
			params.put("createTimeEnd", endStatement);
			params.put("channelName", channelName);
			fs.setStsType(OrderStatement.statement_type.MINSHENG_D0_TIXIAN.name());
		}
		List<Object[]> financeInfos = userOrderDao.findBySql(input_order_sql, params);
		/* 交易总金额 */
		if (financeInfos.get(0)[0] != null) {
			fs.setTradeAmt(((BigDecimal) financeInfos.get(0)[0]).setScale(2, BigDecimal.ROUND_DOWN));
		} else {
			fs.setTradeAmt(BigDecimal.ZERO);
		}
		/* 实际手续费，四舍五入 */
		if (financeInfos.get(0)[1] != null) {
			fs.setFeeAmt(((BigDecimal) financeInfos.get(0)[1]).setScale(2, BigDecimal.ROUND_DOWN));
		}
		if (financeInfos.get(0)[2] != null) {
			fs.setRealInputAmt(((BigDecimal) financeInfos.get(0)[2]).setScale(2, BigDecimal.ROUND_DOWN));
		}
		fs.setTradeNum(((BigInteger) financeInfos.get(0)[3]).longValue());
		if (financeInfos.get(0)[4] != null) {
			fs.setTradeFee(((BigDecimal) financeInfos.get(0)[4]).setScale(2, BigDecimal.ROUND_DOWN));
		}
		if (financeInfos.get(0)[5] != null) {
			fs.setOperateFee(((BigDecimal) financeInfos.get(0)[5]).setScale(2, BigDecimal.ROUND_DOWN));
		}
		return fs;
	}

	@Override
	public FinanceStatement getT1OutFinanceAccount(Date endStatement, Integer status) {

		Date[] sts = holidayService.getT1StartAndEndStatementWorkDate(endStatement);
		return getOutFinanceAccount(sts[0], sts[1], 1, status);
	}

	@Override
	public FinanceStatement getYesterOutFinanceAccount(Date endStatement, Integer status) {
		FinanceStatement t1 = new FinanceStatement(0, null, null, OrderStatement.statement_type.PINGAN.name());
		if (holidayService.isWorkDate(endStatement)) {
			Date[] sts = holidayService.getT1StartAndEndStatementWorkDate(endStatement);
			t1 = getOutFinanceAccount(sts[0], sts[1], 1, status);
		}
		Date t0Date = DateUtil.getBeforeDate(endStatement, 2);
		FinanceStatement t0 = getOutFinanceAccount(DateUtil.getHoursbyInterval(t0Date, -1), DateUtil.getHoursbyInterval(t0Date, 23), 0, status);

		t1.setFeeAmt(t0.getFeeAmt().add(t1.getFeeAmt() == null ? BigDecimal.ZERO : t1.getFeeAmt()));
		t1.setPayType(null);
		t1.setStatemtentDateStart("");
		t1.setStatemtentDateEnd("");
		t1.setTradeAmt(null);
		t1.setTradeNum(t0.getTradeNum().longValue() + (t1.getTradeNum() == null ? 0l : t1.getTradeNum().longValue()));
		t1.setAmtTradeNum(t0.getAmtTradeNum().longValue() + (t1.getAmtTradeNum() == null ? 0l : t1.getAmtTradeNum().longValue()));
		t1.setAgentTradeNum(t0.getAgentTradeNum().longValue() + (t1.getAgentTradeNum() == null ? 0l : t1.getAgentTradeNum().longValue()));
		return t1;
	}

	@Override
	public FinanceStatement getT0OutFinanceAccount(Date endStatement, Integer status) {
		Date[] dts = holidayService.getT0StartAndEndStatementWorkDate(endStatement);
		return getOutFinanceAccount(dts[0], dts[1], 0, status);
	}

	@Override
	public FinanceStatement getOutFinanceAccount(Date startStatement, Date endStatement, Integer isT0, Integer status) {
		String pingan_sql = "select sum(amt), sum(fee), COUNT(id) ,count( if( trans_pay_type =20, true,null)),count( if( trans_pay_type =10, true,null))  from trans_order where type in(:type) and `STATUS`=:status  and create_time  BETWEEN :createTimeStart and :createTimeEnd ";

		Map<String, Object> pinganParams = new HashMap<String, Object>();
		Set<Integer> txTypes = new HashSet<Integer>();
		txTypes.add(UserOrder.trans_type.XJTX.getCode());
		txTypes.add(UserOrder.trans_type.YJTX.getCode());
		pinganParams.put("type", txTypes);

		pinganParams.put("createTimeStart", startStatement);
		pinganParams.put("createTimeEnd", endStatement);
		pinganParams.put("status", status);
		if (isT0 == null) {

		} else if (isT0 == 0) {
			pingan_sql += " and pay_type=:payType";
			pinganParams.put("payType", 0);
		} else if (isT0 == 1) {
			pingan_sql += " and pay_type=:payType";
			pinganParams.put("payType", 1);
		}
		List<Object[]> pinganOrder = userOrderDao.findBySql(pingan_sql, pinganParams);
		FinanceStatement fs = new FinanceStatement(0, DateUtil.getStringFromDate(startStatement, DateUtil.FORMAT_YYYY_MM_DD_HH_mm_ss), DateUtil.getStringFromDate(endStatement, DateUtil.FORMAT_YYYY_MM_DD_HH_mm_ss),
				OrderStatement.statement_type.PINGAN.name());
		if (pinganOrder.get(0)[0] != null) {
			fs.setTradeAmt(((BigDecimal) pinganOrder.get(0)[0]).setScale(2, BigDecimal.ROUND_DOWN));
		} else {
			fs.setTradeAmt(BigDecimal.ZERO);
		}
		fs.setFeeAmt(BigDecimal.valueOf(0.3d).multiply(BigDecimal.valueOf(((BigInteger) pinganOrder.get(0)[2]).doubleValue())).setScale(2, BigDecimal.ROUND_DOWN));
		fs.setTradeNum(((BigInteger) pinganOrder.get(0)[2]).longValue());
		fs.setAgentTradeNum(((BigInteger) pinganOrder.get(0)[3]).longValue());
		fs.setAmtTradeNum(((BigInteger) pinganOrder.get(0)[4]).longValue());
		fs.setPayType(isT0);// T1 交易
		return fs;
	}

	@Override
	public Workbook exportExcel(UserOrder userOrder) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TuserOrder t left join t.user tu left join tu.organization tog  left join t.tranPayOrder p  left join p.payChannel ";

		List<TuserOrder> udl = userOrderDao.find(hql + whereHql(userOrder, params), params);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String[] keys = new String[] { "userName", "orderNum", "type", "transPayType", "inputAccType", "payType", "orgAmt", "fee", "status", "organizationName", "personRate", "shareRate", "createTime", "payOrderFinishDate", "channelName" };
		String[] columnNames = new String[] { "用户名", "订单号", "订单类型", "购买用途", "入账类型", "提现类型", "下单金额", "费用", "状态", "代理商", "手续费率", "分润费率", "创建时间", "完成时间", " " };
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("sheetName", DateUtil.convertDateStrYYYYMMDD(new Date()));
		list.add(m);
		for (TuserOrder t : udl) {
			Map<String, Object> contents = new HashMap<String, Object>();
			contents.put(keys[0], t.getUser().getRealName());
			contents.put(keys[1], t.getOrderNum());
			contents.put(keys[2], dictionaryService.comboxMap("transType").get(t.getType().toString()));
			contents.put(keys[3], t.getTransPayType() == 10 ? "普通订单" : "用户升级");
			contents.put(keys[4], dictionaryService.comboxMap("bigTranType").get(t.getInputAccType().toString()));
			contents.put(keys[5], t.getPayType() == 0 ? "即时到账" : "T1到账");
			contents.put(keys[6], t.getOrgAmt().doubleValue());
			contents.put(keys[7], t.getFee());
			contents.put(keys[8], UserOrder.getOrderStatusChineseName(t.getStatus()));

			contents.put(keys[9], t.getUser().getOrganization().getName());
			contents.put(keys[10], t.getPersonRate());
			contents.put(keys[11], t.getShareRate());
			contents.put(keys[12], t.getCreateTime());

			if (t.getTranPayOrder() != null) {
				contents.put(keys[13], t.getTranPayOrder().getFinishDate());
				if (t.getTranPayOrder().getPayChannel() != null && (userOrder.getOperateUser() != null && userService.isSuperAdmin(userOrder.getOperateUser().getId()))) {
					contents.put(keys[14], t.getTranPayOrder().getPayChannel().getDetailName());
				}
			}
			list.add(contents);
		}
		return ExcelUtil.createWorkBook(list, keys, columnNames);
	}

	@Override
	public void createTransOrderOperationRecordByUserOrder(UserOrder userOrder, User user, String operationName) {
		LOG.info("----------- 创建用户订单操作记录 begin ------");
		try {
			TuserOrderOperationRecord u = new TuserOrderOperationRecord();
			String hql = "select t from TuserOrder t left join t.user tu left join tu.organization where 1=1 and ";
			if (userOrder.getId() != null) {
				hql += " t.id =" + userOrder.getId();
			}
			if (userOrder.getOrderNum() != null) {
				hql += " t.orderNum = '" + userOrder.getOrderNum() + "'";
			}
			TuserOrder t = userOrderDao.get(hql);
			if (t != null) {
				u.setLoginName(t.getUser().getLoginName());
				u.setRealName(t.getUser().getRealName());
				u.setOrganizationName(t.getUser().getOrganization().getName());
				u.setOrderNum(t.getOrderNum());
				u.setOrderType(t.getType());
				u.setTransPayType(t.getTransPayType());
				u.setOrderStatus(t.getStatus());
				u.setOperator(user.getName());
				u.setOperationDatetime(new Date());
				u.setOperationName(operationName);
				userOrderOperationRecordDao.save(u);
			}
		} catch (Exception e) {
			LOG.error("create user order error", e);
			throw e;
		}
		LOG.info("----------- 创建用户订单操作记录 end ------");
	}

	public String adjustFirstUnipayInfo(Long userId) {
		String hql = " select i from TuserInfo i left join i.user u where 1=1 and u.id=:userId";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", (Object) userId);
		List<TuserInfo> userInfos = userInfoDao.find(hql, param);
		String result = "9";
		if (CollectionUtil.isEmpty(userInfos)) {
			result = "2";// 找不到
		} else if (userInfos.size() != 1) {
			result = "3"; // 多了
		} else {
			TuserInfo userInfo = (TuserInfo) userInfos.get(0);
			if (userInfo.getFirstSuccUnipayTxn() != null && "1".equals(userInfo.getFirstSuccUnipayTxn())) {
				result = "1";
			} else {
				result = "0";// 数据不准
			}
		}

		String orderhql = " select sum(o) from TuserOrder o left join o.user u where u.id=:userId and o.status=100 and o.type in(500,520,530,550)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", (Object) userId);
		Long succNum = userOrderDao.count(orderhql, params);
		String firstSuccUnipayTxn = succNum == null ? "0" : succNum > 0L ? "1" : "0";
		if ("2".equals(result)) {
			Tuser tuser = userDao.get(Tuser.class, userId);
			TuserInfo userInfo = new TuserInfo();
			userInfo.setFirstSuccUnipayTxn(firstSuccUnipayTxn);
			userInfo.setUser(tuser);
			userInfo.setCreatetime(new Date());
			userInfoDao.save(userInfo);
		} else if ("0".equals(result) && "1".equals(firstSuccUnipayTxn)) {
			TuserInfo uInfo = userInfos.get(0);
			uInfo.setFirstSuccUnipayTxn(firstSuccUnipayTxn);
			userInfoDao.saveOrUpdate(uInfo);
		}
		LOG.info("createScanCode result={},firstSuccUnipayTxn={}", result, firstSuccUnipayTxn);
		return firstSuccUnipayTxn;
	}

	public void updatePayOrderAfterNotify(PayOrder payOrder, TuserOrder tOrder) {
		TranPayOrder transPayOrder = tOrder.getTranPayOrder();
		transPayOrder.setErrorCode(payOrder.getErrorCode());
		transPayOrder.setErrorInfo(payOrder.getErrorInfo());
		transPayOrder.setFinishDate(new Date());
		if (payOrder.getRealAmt() != null) {
			transPayOrder.setRealAmt(payOrder.getRealAmt());// 实际通道结算的金额
		}
		if (StringUtil.isNotBlank(payOrder.getPayFinishDate())) {
			transPayOrder.setPayFinishDate(payOrder.getPayFinishDate());
		}
		if (StringUtil.isNotBlank(payOrder.getPayNo())) {
			transPayOrder.setPayNo(payOrder.getPayNo());
		}
		if (payOrder.getFinishDate() != null) {
			transPayOrder.setFinishDate(payOrder.getFinishDate());
		}
		transPayOrder.setStatus(payOrder.getStatus());
		if (payOrder.getStatus() == PayOrder.pay_status.SUCCESS.getCode()) {
			tOrder.setStatus(UserOrder.order_status.SUCCESS.getCode());
			try {
				dealStartShareBonus(tOrder);
				if (transPayOrder != null && transPayOrder.getPayChannel() != null) {
					channelService.appendTodayAmt(transPayOrder.getPayChannel().getId(), tOrder.getAmt());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			tOrder.setStatus(UserOrder.order_status.FAILURE.getCode());
		}
		payOrderDao.update(transPayOrder);
		userOrderDao.update(tOrder);
	}

	public void inquireDaiFuOrderToShenfu(String orderNum) {
		TuserOrder tOrder = getTorderByOrderNo(orderNum);
		if (tOrder != null && tOrder.getUser() != null && tOrder.getUser().getId() != null) {
			try {
				Long userId = tOrder.getUser().getId();
				Map<String, String> orderMap = new TreeMap<String, String>();
				orderMap.put("mercId", ApplicationBase.SHENFU_DAIFU_MERCHID);
				orderMap.put("orderNo", orderNum);
				String mercPrivateKey = ApplicationBase.SHENFU_DAIFU_PRIVATEKEY;
				JSONObject jsonResult = ShenFuPayUtil.searchOrderToShenfu(orderMap, mercPrivateKey);
				LOG.info("inquireDaiFuOrder Order={},result={}", orderNum, jsonResult.toJSONString());
				/**
				 * retCode 查询结果 String 是 00表示查询操作成功，其他值表示查询操作失败。 retMsg 查询结果描述
				 * String 是 查询结果描述 以下值仅当仅当retCode=00是才会有出现 merSeqId 交易流水号 String
				 * 否 平台交易流水号 orderId 商户订单号 String 否 订单号 txnStatus 交易状态 String 否
				 * S成功，F失败，0交易处理中，N交易不存在 txnStatusDesc 交易状态描述 String 否 交易状态描述
				 * ignature 签名 String 否 响应签名字符串
				 **/
				String retCode = (String) jsonResult.get("retCode");
				String retMsg = (String) jsonResult.get("retMsg");
				String merSeqId = (String) jsonResult.get("merSeqId");
				String orderId = (String) jsonResult.get("orderId");
				String txnStatus = (String) jsonResult.get("txnStatus");
				String txnStatusDesc = (String) jsonResult.get("txnStatusDesc");
				String signature = (String) jsonResult.get("signature");

				Map<String, String> map = new TreeMap<String, String>();
				map.put("retCode", retCode);
				map.put("retMsg", retMsg);
				if (StringUtil.isNotEmpty(merSeqId)) {
					map.put("merSeqId", merSeqId);
				}
				if (StringUtil.isNotEmpty(orderId)) {
					map.put("orderId", orderId);
				}
				if (StringUtil.isNotEmpty(txnStatus)) {
					map.put("txnStatus", txnStatus);
				}
				if (StringUtil.isNotEmpty(txnStatusDesc)) {
					map.put("txnStatusDesc", txnStatusDesc);
				}
				boolean bKeySign = RSAUtil.verify(ApplicationBase.coverMap2String(map).getBytes("UTF-8"), ApplicationBase.SHENFU_DAIFU_PUBLICKEY, signature);
				LOG.info("inquireDaiFuOrderToShenfu PublicKey orderId={},Sign={}", orderId, bKeySign);
				// 设置后续信息
				String hql = " select t from TuserOrder t  left join t.user left join t.tranPayOrder u where  t.id=:orderId ";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("orderId", orderId);
				TuserOrder tuserOrder = userOrderDao.get(hql, params);
				PayOrder payOrder = new PayOrder();
				if (tuserOrder.getAmt() != null) {
					payOrder.setRealAmt(tuserOrder.getAmt());
				}
				if (StringUtil.isNotBlank(merSeqId)) {
					payOrder.setPayNo(merSeqId);
				}
				if (StringUtil.isNotBlank(retCode)) {
					payOrder.setErrorCode(retCode);
				}
				if (StringUtil.isNotBlank(retMsg)) {
					payOrder.setErrorInfo(retMsg);
				}
				payOrder.setPayFinishDate(DateUtil.convertCurrentDateTimeToString());
				payOrder.setFinishDate(new Date());

				// S成功，F失败，0交易处理中，N交易不存在,ret!=00
				// s f n f f
				Tuser user = tuserOrder.getUser();
				String res = "1";// 默认OK
				if ("00".equals(retCode) && bKeySign) {
					if ("F".equalsIgnoreCase(txnStatus) || "N".equalsIgnoreCase(txnStatus)) {
						res = "0";// fail
					} else if ("0".equalsIgnoreCase(txnStatus)) {
						res = "2";// 处理中
					}
				} else {
					res = "0";// fail
				}

				if ("1".equals(res)) {
					/* 处理成功 */
					tuserOrder.setStatus(UserOrder.order_status.SUCCESS.getCode());
					payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
					if (UserOrder.trans_type.YJTX.getCode() == tuserOrder.getType()) {
						accountService.updateBrokerageAccountAfterLiqSuccess(user.getId(), tuserOrder);
					} else {
						accountService.updateAccountAfterLiqSuccess(user.getId(), tuserOrder);
					}
				} else if ("0".equals(res)) {
					// 订单失败状态
					tuserOrder.setStatus(UserOrder.order_status.FAILURE.getCode());
					payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
					/* 账户退款 */
					if (UserOrder.trans_type.YJTX.getCode() == tuserOrder.getType()) {
						accountService.updateBrokerageAccountAfterLiqFailure(user.getId(), tuserOrder);
					} else {
						accountService.updateAccountAfterLiqFailure(user.getId(), tuserOrder);
					}
				}
				updatePayOrderAfterNotify(payOrder, tuserOrder);
			} catch (Exception e1) {
				LOG.error("----银联订单回调异常", e1);
			}

		} else {
			LOG.info("申孚银联订单：" + orderNum + "信息不完整。");
		}
		// return null;
	}

	public void affirmDaiFuNotify(boolean success, String orderNum, PayOrder payOrder) {
		TuserOrder tuserOrder = getTorderByOrderNo(orderNum);
		Tuser user = tuserOrder.getUser();
		try {
			LOG.info("----准备结束代付订单号={}----", orderNum);
			if (success) {
				/* 处理成功 */
				LOG.info("--DaiFuNotify 处理成功--");
				tuserOrder.setStatus(UserOrder.order_status.SUCCESS.getCode());
				payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
				if (UserOrder.trans_type.YJTX.getCode() == tuserOrder.getType()) {
					accountService.updateBrokerageAccountAfterLiqSuccess(user.getId(), tuserOrder);
				} else {
					accountService.updateAccountAfterLiqSuccess(user.getId(), tuserOrder);
				}
			} else {
				// 订单失败状态
				LOG.info("--DaiFuNotify 订单失败--");
				tuserOrder.setStatus(UserOrder.order_status.FAILURE.getCode());
				payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
				/* 账户退款 */
				if (UserOrder.trans_type.YJTX.getCode() == tuserOrder.getType()) {
					accountService.updateBrokerageAccountAfterLiqFailure(user.getId(), tuserOrder);
				} else {
					accountService.updateAccountAfterLiqFailure(user.getId(), tuserOrder);
				}
			}
			updatePayOrderAfterNotify(payOrder, tuserOrder);
			LOG.info("----END 代付订单号={}----", orderNum);
		} catch (Exception e) {
			LOG.error("----订单回调异常", e);
		}

	}

}
