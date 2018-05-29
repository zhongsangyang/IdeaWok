package com.cn.flypay.service.statement.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.trans.TorderStatement;
import com.cn.flypay.model.trans.TransStatement;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.statement.AlipayStatement;
import com.cn.flypay.pageModel.statement.AlipayStatementDetail;
import com.cn.flypay.pageModel.statement.OrderStatement;
import com.cn.flypay.pageModel.statement.PinganStatement;
import com.cn.flypay.pageModel.statement.PinganStatementDetail;
import com.cn.flypay.pageModel.statement.WeixinStatement;
import com.cn.flypay.pageModel.statement.WeixinStatementDetail;
import com.cn.flypay.pageModel.statement.ZanshanfuStatement;
import com.cn.flypay.pageModel.statement.ZanshanfuStatementDetail;
import com.cn.flypay.pageModel.trans.Statement;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.statement.OrderStatementService;
import com.cn.flypay.service.statement.StatementService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.channel.AlipayUtil;
import com.cn.flypay.utils.channel.PinganUtil;
import com.cn.flypay.utils.channel.WeixinUtil;
import com.cn.flypay.utils.channel.ZanshanfuUtil;

@Service(value = "statementService")
public class StatementServiceImpl implements StatementService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BaseDao<TuserOrder> orderDao;

	@Autowired
	private BaseDao<TransStatement> statementDao;
	@Autowired
	private OrderStatementService orderStatementService;
	@Autowired
	private AccountService accountService;

	@Value("${zanshanfu_root_path}")
	private String zanshanfu_root_path;

	/**
	 * 与攒善付银联在线对账
	 */
	@Override
	public ZanshanfuStatement checkYLZXstatement(String filePath, String dateyyyyMMdd) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("type", UserOrder.trans_type.YLZX.getCode());
			params.put("startDate", DateUtil.getStartOfDay(DateUtil.getDateFromString(dateyyyyMMdd)));
			params.put("endDate", DateUtil.getEndOfDay(DateUtil.getDateFromString(dateyyyyMMdd)));
			String sql = "select d.id,d.ORDER_NUM,p.pay_no,d.STATUS ,p.pay_amt, p.real_amt ,d.USER_ID from trans_order d left join tran_pay_order p on d.id=p.id "
					+ "where  d.type=:type and d.create_time>=:startDate and d.create_time<=:endDate";
			/* 0:id 1:orderNum, 2:payNum ,3 status 4 订单金额， 5 实际金额 , 6 用户ID */
			List<Object[]> orders = orderDao.findBySql(sql, params);
			ZanshanfuStatement zsf = ZanshanfuUtil.getZanshanfuStatementInfo(zanshanfu_root_path);
			if (zsf != null && zsf.getDetails().size() > 0) {
				/* 出现异常的用户ID */
				Set<Long> userIds = new HashSet<Long>();
				List<TorderStatement> ordStatements = new ArrayList<>();
				for (ZanshanfuStatementDetail dt : zsf.getDetails()) {
					boolean isFind = false;// 订单中是否存在
					for (Object[] objs : orders) {
						Long orderId = ((BigInteger) objs[0]).longValue();
						Long userId = ((BigInteger) objs[6]).longValue();
						String orderNo = (String) objs[1];
						/* 当天内商户的支付号存在，就去做对比，否则就被打入人工队列 */
						if (objs[2] != null) {
							/**/
							if (objs[2].equals(dt.getPayNo())) {
								isFind = true;
								int statementFlag = "已支付".equals(dt.getStatus()) ? UserOrder.order_status.SUCCESS.getCode() : UserOrder.order_status.FAILURE.getCode();
								if ((int) objs[3] == statementFlag) {
									Double payAmt = ((BigDecimal) objs[4]).doubleValue();
									Double realAmt = ((BigDecimal) objs[5]).doubleValue();
									if (payAmt == Double.parseDouble(dt.getTranAmt()) && realAmt == Double.parseDouble(dt.getRealAmt())) {
										logger.info("对账成功：支付订单号=" + dt.getPayNo());
									} else {
										logger.error("对账失败：支付订单号=" + dt.getPayNo());
										TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.ZANSHANFU.getCode(), dateyyyyMMdd, orderId, orderNo, dt.getPayNo(), 1,
												"支付的金额不符合：对账单支付金额=" + dt.getTranAmt() + " 系统订单支付金额=" + payAmt + " 对账单实际金额=" + dt.getRealAmt() + " 对账单支付金额=" + realAmt + " ，请人工核对");
										ordStatements.add(os);
										userIds.add(userId);
									}
									// 对账成功
								} else {
									// 对账失败
									logger.error("订单状态不一致，对账失败：支付订单号=" + dt.getPayNo());
									TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.ZANSHANFU.getCode(), dateyyyyMMdd, orderId, orderNo, dt.getPayNo(), 1,
											"订单状态与对账单不符，订单为" + UserOrder.getOrderStatusChineseName((Integer) objs[3]) + ",对账单为" + dt.getStatus());

									ordStatements.add(os);
									userIds.add(userId);
								}
								orders.remove(objs);
								break;
							}
						} else {
							logger.error("该笔订单没有银联支付号，无法对账：订单号=" + objs[2]);
							TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.ZANSHANFU.getCode(), dateyyyyMMdd, orderId, orderNo, null, 1, "该笔订单没有银联支付号，无法对账");
							ordStatements.add(os);
							userIds.add(userId);
						}
					}
					if (!isFind) {
						logger.error("对账单中存在，订单中不存在，无法对账：支付订单号=" + dt.getPayNo());
						// 订单中不存在，对账单中存在
						TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.ZANSHANFU.getCode(), dateyyyyMMdd, null, null, dt.getPayNo(), 1, "对账单中存在，订单中不存在");
						ordStatements.add(os);
					}
				}
				orderStatementService.saveTorderStatements(ordStatements);
				/* 冻结账户 */
				accountService.updateAccountWhenAccountException(userIds);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 批量平安对账文件
	 */
	@Override
	public String dealBatchPinganStatement(String filePath, String dateyyyyMMdd) {

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			Set<Integer> types = new HashSet<Integer>();
			types.add(UserOrder.trans_type.XJTX.getCode());
			types.add(UserOrder.trans_type.YJTX.getCode());
			params.put("type", types);
			params.put("startDate", DateUtil.getStartOfDay(DateUtil.getDateFromString(dateyyyyMMdd)));
			params.put("endDate", DateUtil.getEndOfDay(DateUtil.getDateFromString(dateyyyyMMdd)));
			String sql = "select d.id,d.ORDER_NUM,p.pay_no,d.STATUS ,p.pay_amt, p.real_amt ,d.USER_ID from trans_order d left join tran_pay_order p on d.id=p.id "
					+ "where  d.type in(:type) and d.create_time>=:startDate and d.create_time<=:endDate";
			/* 0:id 1:orderNum, 2:payNum ,3 status 4 订单金额， 5 实际金额 , 6 用户ID */
			List<Object[]> orders = orderDao.findBySql(sql, params);
			PinganStatement ps = PinganUtil.getPinganStatement(filePath);
			if (ps != null && ps.getDetails().size() > 0) {
				/* 出现异常的用户ID */
				Set<Long> userIds = new HashSet<Long>();
				List<TorderStatement> ordStatements = new ArrayList<>();
				for (PinganStatementDetail dt : ps.getDetails()) {
					boolean isFind = false;// 订单中是否存在
					for (Object[] objs : orders) {
						Long orderId = ((BigInteger) objs[0]).longValue();
						Long userId = ((BigInteger) objs[6]).longValue();
						String orderNo = (String) objs[1];
						/* 当天内商户的支付号存在，就去做对比，否则就被打入人工队列 */
						if (objs[2] != null) {
							/**/
							if (objs[2].equals(dt.getOrderNum())) {
								isFind = true;
								int statementFlag = "0000".equals(dt.getErrorCode()) ? UserOrder.order_status.SUCCESS.getCode() : UserOrder.order_status.FAILURE.getCode();
								if ((int) objs[3] == statementFlag) {
									Double payAmt = ((BigDecimal) objs[4]).doubleValue();
									Double realAmt = ((BigDecimal) objs[5]).doubleValue();
									if (payAmt == dt.getAmt() && realAmt == (dt.getAmt() - dt.getFee())) {
										logger.info("对账成功：支付订单号=" + dt.getOrderNum());
									} else {
										logger.error("对账失败：支付订单号=" + dt.getOrderNum());
										TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.PINGAN.getCode(), dateyyyyMMdd, orderId, orderNo, dt.getOrderNum(), 1,
												"支付的金额不符合：对账单支付金额=" + dt.getAmt() + " 系统订单支付金额=" + payAmt + " 对账单实际金额=" + (dt.getAmt() - dt.getFee()) + " 对账单支付金额=" + realAmt + " ，请人工核对");
										ordStatements.add(os);
										userIds.add(userId);
									}
									// 对账成功
								} else {
									// 对账失败
									logger.error("订单状态不一致，对账失败：支付订单号=" + dt.getOrderNum());
									TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.PINGAN.getCode(), dateyyyyMMdd, orderId, orderNo, dt.getOrderNum(), 1,
											"订单状态与对账单不符，订单为" + UserOrder.getOrderStatusChineseName((Integer) objs[3]) + ",对账单为" + dt.getErrorCode());

									ordStatements.add(os);
									userIds.add(userId);
								}
								orders.remove(objs);
								break;
							}
						} else {
							logger.error("该笔订单没有银联支付号，无法对账：订单号=" + objs[2]);
							TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.PINGAN.getCode(), dateyyyyMMdd, orderId, orderNo, null, 1, "该笔订单没有银联支付号，无法对账");
							ordStatements.add(os);
							userIds.add(userId);
						}
					}
					if (!isFind) {
						logger.error("对账单中存在，订单中不存在，无法对账：支付订单号=" + dt.getOrderNum());
						// 订单中不存在，对账单中存在
						TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.PINGAN.getCode(), dateyyyyMMdd, null, null, dt.getOrderNum(), 1, "对账单中存在，订单中不存在");
						ordStatements.add(os);
					}
				}
				orderStatementService.saveTorderStatements(ordStatements);
				/* 冻结账户 */
				accountService.updateAccountWhenAccountException(userIds);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void dealWeixinStatement(String filePath, String dateyyyyMMdd) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			Set<Integer> types = new HashSet<Integer>();
			types.add(UserOrder.trans_type.WXQR.getCode());
			types.add(UserOrder.trans_type.WXSM.getCode());
			params.put("type", types);
			params.put("startDate", DateUtil.getStartOfDay(DateUtil.getDateFromString(dateyyyyMMdd)));
			params.put("endDate", DateUtil.getEndOfDay(DateUtil.getDateFromString(dateyyyyMMdd)));
			String sql = "select d.id,d.ORDER_NUM,p.pay_no,d.STATUS ,p.pay_amt, p.real_amt ,d.USER_ID from trans_order d left join tran_pay_order p on d.id=p.id "
					+ "where  d.type in(:type) and d.create_time>=:startDate and d.create_time<=:endDate";
			/* 0:id 1:orderNum, 2:payNum ,3 status 4 订单金额， 5 实际金额 , 6 用户ID */
			List<Object[]> orders = orderDao.findBySql(sql, params);
			WeixinStatement ps = WeixinUtil.getWeixinStatement(filePath);
			if (ps != null && ps.getDetails().size() > 0) {
				/* 出现异常的用户ID */
				Set<Long> userIds = new HashSet<Long>();
				List<TorderStatement> ordStatements = new ArrayList<>();
				for (WeixinStatementDetail dt : ps.getDetails()) {
					boolean isFind = false;// 订单中是否存在
					for (Object[] objs : orders) {
						Long orderId = ((BigInteger) objs[0]).longValue();
						Long userId = ((BigInteger) objs[6]).longValue();
						String orderNo = (String) objs[1];
						// 当天内商户的支付号存在，就去做对比，否则就被打入人工队列
						if (objs[2] != null && objs[2].equals(dt.getOrderNum())) {
							isFind = true;
							int statementFlag = "SUCCESS".equals(dt.getTranStatus()) ? UserOrder.order_status.SUCCESS.getCode() : UserOrder.order_status.FAILURE.getCode();
							if ((int) objs[3] == statementFlag) {
								Double payAmt = ((BigDecimal) objs[4]).doubleValue();
								Double realAmt = ((BigDecimal) objs[5]).doubleValue();
								if (payAmt == dt.getTotalAmt() && realAmt == (dt.getTotalAmt() - dt.getFee())) {
									logger.info("对账成功：支付订单号=" + dt.getOrderNum());
								} else {
									logger.error("对账失败：支付订单号=" + dt.getOrderNum());
									TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.WEIXIN.getCode(), dateyyyyMMdd, orderId, orderNo, dt.getOrderNum(), 1,
											"支付的金额不符合：对账单支付金额=" + dt.getTotalAmt() + " 系统订单支付金额=" + payAmt + " 对账单实际金额=" + (dt.getTotalAmt() - dt.getFee()) + " 对账单支付金额=" + realAmt + " ，请人工核对");
									ordStatements.add(os);
									userIds.add(userId);
								}
								// 对账成功
							} else {
								// 对账失败
								logger.error("订单状态不一致，对账失败：支付订单号=" + dt.getOrderNum());
								TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.WEIXIN.getCode(), dateyyyyMMdd, orderId, orderNo, dt.getOrderNum(), 1, "订单状态与对账单不符，订单为"
										+ UserOrder.getOrderStatusChineseName((Integer) objs[3]) + ",对账单为" + dt.getTranStatus());

								ordStatements.add(os);
								userIds.add(userId);
							}
							orders.remove(objs);
							break;
						}
					}
					if (!isFind) {
						logger.error("对账单中存在，订单中不存	在，无法对账：支付订单号=" + dt.getOrderNum());
						// 订单中不存在，对账单中存在
						TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.WEIXIN.getCode(), dateyyyyMMdd, null, null, dt.getOrderNum(), 1, "对账单中存在，订单中不存在");
						ordStatements.add(os);
					}
				}
				orderStatementService.saveTorderStatements(ordStatements);
				// 冻结账户
				accountService.updateAccountWhenAccountException(userIds);
			} else {
				logger.error("微信对账文件下载出错，请检查！！");
			}
		} catch (Exception e) {
			logger.error("微信对账失败", e);
		}

	}

	@Override
	public void dealAlipayStatement(String filePath, String dateyyyyMMdd) {

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			Set<Integer> types = new HashSet<Integer>();
			types.add(UserOrder.trans_type.ALQR.getCode());
			types.add(UserOrder.trans_type.ALSM.getCode());
			params.put("type", types);
			params.put("startDate", DateUtil.getStartOfDay(DateUtil.getDateFromString(dateyyyyMMdd)));
			params.put("endDate", DateUtil.getEndOfDay(DateUtil.getDateFromString(dateyyyyMMdd)));
			String sql = "select d.id,d.ORDER_NUM,p.pay_no,d.STATUS ,p.pay_amt, p.real_amt ,d.USER_ID from trans_order d left join tran_pay_order p on d.id=p.id "
					+ "where  d.type in(:type) and d.create_time>=:startDate and d.create_time<=:endDate";
			/* 0:id 1:orderNum, 2:payNum ,3 status 4 订单金额， 5 实际金额 , 6 用户ID */
			List<Object[]> orders = orderDao.findBySql(sql, params);
			AlipayStatement ps = AlipayUtil.getAlipayStatementInfo(filePath);
			if (ps != null && ps.getDetails().size() > 0) {
				/* 出现异常的用户ID */
				Set<Long> userIds = new HashSet<Long>();
				List<TorderStatement> ordStatements = new ArrayList<>();
				for (AlipayStatementDetail dt : ps.getDetails()) {
					boolean isFind = false;// 订单中是否存在
					for (Object[] objs : orders) {
						Long orderId = ((BigInteger) objs[0]).longValue();
						Long userId = ((BigInteger) objs[6]).longValue();
						String orderNo = (String) objs[1];
						// 当天内商户的支付号存在，就去做对比，否则就被打入人工队列
						if (objs[2] != null && objs[2].equals(dt.getOrderNum())) {
							isFind = true;
							int statementFlag = UserOrder.order_status.SUCCESS.getCode();
							if ((int) objs[3] == statementFlag) {
								Double payAmt = ((BigDecimal) objs[4]).doubleValue();
								Double realAmt = ((BigDecimal) objs[5]).doubleValue();
								if (payAmt == dt.getOrderAmt() && realAmt == dt.getRealAmt()) {
									logger.info("对账成功：支付订单号=" + dt.getOrderNum());
								} else {
									logger.error("对账失败：支付订单号=" + dt.getOrderNum());
									TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.ALIPAY.getCode(), dateyyyyMMdd, orderId, orderNo, dt.getOrderNum(), 1,
											"支付的金额不符合：对账单支付金额=" + dt.getOrderAmt() + " 系统订单支付金额=" + payAmt + " 对账单实际金额=" + dt.getRealAmt() + " 对账单支付金额=" + realAmt + " ，请人工核对");
									ordStatements.add(os);
									userIds.add(userId);
								}
								// 对账成功
							}
							orders.remove(objs);
							break;
						}
					}
					if (!isFind) {
						logger.error("支付宝对账单中存在，订单中不存	在，无法对账：支付订单号=" + dt.getOrderNum());
						// 订单中不存在，对账单中存在
						TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.ALIPAY.getCode(), dateyyyyMMdd, null, null, dt.getOrderNum(), 1, "支付宝对账单中存在，订单中不存在");
						ordStatements.add(os);
					}
				}
				orderStatementService.saveTorderStatements(ordStatements);
				// 冻结账户
				accountService.updateAccountWhenAccountException(userIds);
			} else {
				logger.error("支付宝对账文件下载出错，请检查！！");
			}
		} catch (Exception e) {
			logger.error("支付宝对账失败", e);
		}
	}

	@Override
	public List<Statement> dataGrid(Statement statement, PageFilter ph) {

		List<Statement> ul = new ArrayList<Statement>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TransStatement t ";
		List<TransStatement> l = statementDao.find(hql + whereHql(statement, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TransStatement t : l) {
			Statement u = new Statement();
			BeanUtils.copyProperties(t, u);
			ul.add(u);
		}
		return ul;

	}

	@Override
	public Long count(Statement statement, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "  from TransStatement t  ";
		return statementDao.count("select count(t.id) " + hql + whereHql(statement, params), params);
	}

	private String whereHql(Statement statement, Map<String, Object> params) {
		String hql = "";
		if (statement != null) {
			hql += " where 1=1 ";
			if (statement.getStsType() != null) {
				hql += " and t.stsType = :stsType";
				params.put("stsType", statement.getStsType());
			}
			if (StringUtil.isNotBlank(statement.getCreateDateStart())) {
				hql += " and t.createTime >= :createTimeStart";
				params.put("createTimeStart", statement.getCreateDateStart());
			}
			if (StringUtil.isNotBlank(statement.getCreateDateEnd())) {
				hql += " and t.createTime <= :createTimeEnd";
				params.put("createTimeEnd", statement.getCreateDateEnd());
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
	public void add(Statement statement) {

	}

	@Override
	public void add(TransStatement statement) {

	}

	@Override
	public void dealMinshengStatement(String filePath, String dateyyyyMMdd) {

	}

	@Override
	public void dealPinganStatement(String filePath, String dateyyyyMMdd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dealXinkeStatement(String filePath, String dateyyyyMMdd) {
		// TODO Auto-generated method stub

	}
}
