package com.cn.flypay.service.statement.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.TorderStatement;
import com.cn.flypay.model.trans.TranPayOrder;
import com.cn.flypay.model.trans.TransStatement;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.statement.AlipayStatement;
import com.cn.flypay.pageModel.statement.AlipayStatementDetail;
import com.cn.flypay.pageModel.statement.MinshengStatement;
import com.cn.flypay.pageModel.statement.MinshengStatementDetail;
import com.cn.flypay.pageModel.statement.OrderStatement;
import com.cn.flypay.pageModel.statement.PinganPayStatement;
import com.cn.flypay.pageModel.statement.PinganPayStatementDetail;
import com.cn.flypay.pageModel.statement.PinganStatement;
import com.cn.flypay.pageModel.statement.PinganStatementDetail;
import com.cn.flypay.pageModel.statement.WeixinStatement;
import com.cn.flypay.pageModel.statement.WeixinStatementDetail;
import com.cn.flypay.pageModel.statement.XinkeStatement;
import com.cn.flypay.pageModel.statement.XinkeStatementDetail;
import com.cn.flypay.pageModel.statement.ZanshanfuStatement;
import com.cn.flypay.pageModel.statement.ZanshanfuStatementDetail;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.Statement;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.statement.OrderStatementService;
import com.cn.flypay.service.statement.StatementService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.channel.AlipayUtil;
import com.cn.flypay.utils.channel.MinshengUtil;
import com.cn.flypay.utils.channel.PinganUtil;
import com.cn.flypay.utils.channel.WeixinUtil;
import com.cn.flypay.utils.channel.XinkeUtil;
import com.cn.flypay.utils.channel.ZanshanfuUtil;

@Service(value = "tradeStatementService")
public class TradeStatementServiceImpl implements StatementService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BaseDao<TuserOrder> orderDao;
	@Autowired
	private BaseDao<TranPayOrder> payOrderDao;
	@Autowired
	private OrderStatementService orderStatementService;
	@Autowired
	private BaseDao<TransStatement> statementDao;
	@Autowired
	private AccountService accountService;
	@Autowired
	private UserOrderService orderService;
	@Autowired
	private ChannelService channelService;

	@Autowired
	private SysParamService paramService;

	@Autowired
	private DictionaryService dictionaryService;

	@Value("${zanshanfu_root_path}")
	private String zanshanfu_root_path;
	@Value("${xinke_root_path}")
	private String xinke_root_path;
	@Value("${pingan_T1_batch_root_path}")
	private String pingan_T1_batch_root_path;

	/**
	 * 与攒善付银联在线对账
	 */
	@Override
	public ZanshanfuStatement checkYLZXstatement(String filePath, String dateyyyyMMdd) {
		logger.info("--------开始银联在线" + dateyyyyMMdd + "对账-----");
		Date startDate = new Date();
		try {
			ZanshanfuStatement zsf = ZanshanfuUtil.getZanshanfuStatementInfo(zanshanfu_root_path);
			if (zsf != null && zsf.getDetails().size() > 0) {
				/* 出现异常的用户ID */
				Set<Long> brokerageUserIds = new HashSet<Long>();
				Set<Long> userIds = new HashSet<Long>();
				List<TorderStatement> ordStatements = new ArrayList<>();

				Integer stsSuccussNum = 0;
				Integer stsNeedDealNum = 0;
				Integer stsFailNum = 0;

				for (ZanshanfuStatementDetail dt : zsf.getDetails()) {
					TuserOrder order = orderService.getOrderByPayNo(dt.getPayNo());
					if (order != null) {
						Tuser user = order.getUser();
						int statementFlag = "已支付".equals(dt.getStatus()) ? UserOrder.order_status.SUCCESS.getCode() : UserOrder.order_status.FAILURE.getCode();

						if (order.getStatus() == statementFlag) {
							if (order.getOrgAmt().doubleValue() - Double.parseDouble(dt.getTranAmt()) == 0) {
								logger.info("对账成功：支付订单号=" + dt.getPayNo());
								stsSuccussNum++;
							} else {
								String desc = " 支付的金额不符合：对账单支付金额=" + dt.getTranAmt() + " 系统订单支付金额=" + order.getOrgAmt().doubleValue() + " ，请人工核对";
								logger.error("对账失败：支付订单号=" + dt.getPayNo() + desc);
								TorderStatement os = new TorderStatement(user, OrderStatement.statement_type.ZANSHANFU.getCode(), dateyyyyMMdd, order.getId(), order.getOrderNum(), null, 1, desc);
								ordStatements.add(os);
								/* 锁定用户的订单 */
								isAccountOrBrokerage(order, userIds, brokerageUserIds);
								stsFailNum++;
							}
						} else {
							if (statementFlag - UserOrder.order_status.SUCCESS.getCode() == 0) {
								/*
								 * 根据对账单自动处理系统的订单，若订单处于待处理状态或者是人工处理状态，系统自动调整，不需要锁定用户
								 */
								if (order.getStatus() == UserOrder.order_status.PROCESSING.getCode() || order.getStatus() == UserOrder.order_status.MANUAL_PROCESSING.getCode()) {
									try {
										String desc = "订单状态与对账单不符，订单为" + UserOrder.getOrderStatusChineseName(order.getStatus()) + ",对账单为" + dt.getStatus() + " 系统自动完成订单和账户处理";
										logger.info(desc);
										PayOrder payOrder = initSuccessPayOrder(Double.parseDouble(dt.getTranAmt()), Double.parseDouble(dt.getRealAmt()), dt.getPayNo(), dt.getTranDateStr());
										orderService.finishInputOrderStatus(order, payOrder);
										TorderStatement os = new TorderStatement(user, OrderStatement.statement_type.ZANSHANFU.getCode(), dateyyyyMMdd, order.getId(), order.getOrderNum(), null, 0,
												desc);
										ordStatements.add(os);
										stsNeedDealNum++;
									} catch (Exception e) {
										logger.error("----攒善付对账单异常:订单号=" + order.getOrderNum(), e);
										stsFailNum++;
									}
								} else {
									String desc = " 订单状态与对账单不符，订单为" + UserOrder.getOrderStatusChineseName(order.getStatus()) + ",对账单为  " + dt.getStatus() + ",需人工查询状态不一致的原因，为用户解除冻结状态";
									logger.error("订单状态不一致，对账失败：支付订单号=" + order.getOrderNum() + desc);
									TorderStatement os = new TorderStatement(user, OrderStatement.statement_type.ZANSHANFU.getCode(), dateyyyyMMdd, order.getId(), order.getOrderNum(), dt.getPayNo(),
											1, desc);
									ordStatements.add(os);
									/* 锁定用户的订单 */
									isAccountOrBrokerage(order, userIds, brokerageUserIds);
									stsFailNum++;
								}
							} else {
								String desc = "订单状态不一致，对账对账单状态为：" + dt.getStatus() + "：支付订单号=" + dt.getPayNo() + UserOrder.getOrderStatusChineseName(order.getStatus()) + ",对账单为" + dt.getStatus()
										+ " 等待明天的对账单，两天内无处理的订单系统自动关闭订单";
								// 攒善付未支付的订单，两天内有效，系统第三天0：10自动定时关闭这未处理的订单
								logger.info(desc);
								TorderStatement os = new TorderStatement(order.getUser(), OrderStatement.statement_type.ZANSHANFU.getCode(), dateyyyyMMdd, order.getId(), order.getOrderNum(), null, 1,
										desc);
								ordStatements.add(os);
								stsFailNum++;
							}
						}
					} else {
						logger.error("对账单中存在，订单中不存在，无法对账：支付订单号=" + dt.getPayNo());
						// 订单中不存在，对账单中存在
						TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.ZANSHANFU.getCode(), dateyyyyMMdd, null, null, dt.getPayNo(), 1,
								"对账单中存在，订单中不存在,需人工确认对账单中订单的来源,订单号=" + dt.getPayNo());
						ordStatements.add(os);
					}
				}
				orderStatementService.saveTorderStatements(ordStatements);
				/* 冻结账户 */
				accountService.updateAccountWhenAccountException(userIds);
				/* 冻结佣金账户 */
				accountService.updateBrokerageAccountWhenAccountException(brokerageUserIds);

				/* 保存对账单汇总 */
				TransStatement transStatement = new TransStatement(OrderStatement.statement_type.ZANSHANFU.getCode(), dateyyyyMMdd, zsf.getTotalSuccessNum().longValue(), zsf.getTotalSuccessNum()
						.longValue(), BigDecimal.valueOf(zsf.getTotalSuccessAmt()), BigDecimal.valueOf(zsf.getTotalSuccessAmt()), BigDecimal.valueOf(zsf.getFee()),
						BigDecimal.valueOf(zsf.getFeeRate()), stsSuccussNum, stsNeedDealNum, stsFailNum, userIds.size(), brokerageUserIds.size());

				add(transStatement);
			}
		} catch (Exception e) {
			logger.error("银联在线对账出现异常", e);
		}
		logger.info("-------银联在线" + dateyyyyMMdd + "对账结束-----  耗时：" + DateUtil.getBetweenSecond(startDate, new Date()));
		return null;
	}

	private PayOrder initSuccessPayOrder(Double transAmt, Double realAmt, String payNo, String finishDate) {
		PayOrder payOrder = new PayOrder();
		payOrder.setPayAmt(BigDecimal.valueOf(transAmt));
		payOrder.setRealAmt(BigDecimal.valueOf(realAmt));
		payOrder.setPayNo(payNo);
		payOrder.setPayFinishDate(finishDate);
		payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
		return payOrder;
	}

	/**
	 * 批量平安对账文件
	 */
	@Override
	public String dealBatchPinganStatement(String filePath, String dateyyyyMMdd) {
		logger.info("--------开始平安" + dateyyyyMMdd + "对账-----");
		String desc = "平安对账成功";
		Date startDate = new Date();
		try {
			String fileName = "CHK_KHKF01_" + ApplicatonStaticUtil.getAppStaticData("pinganAccount.signNo") + "_" + dateyyyyMMdd + ".txt";
			filePath = pingan_T1_batch_root_path + File.separator + fileName;
			PinganStatement ps = PinganUtil.getPinganStatement(filePath);
			if (ps != null && ps.getDetails().size() > 0) {
				/* 出现异常的用户ID */
				Set<Long> userIds = new HashSet<Long>();
				Set<Long> brokerageUserIds = new HashSet<Long>();
				List<TorderStatement> ordStatements = new ArrayList<>();

				Integer stsSuccussNum = 0;
				Integer stsFailNum = 0;
				Integer stsAutoDealNum = 0;
				BigDecimal feeBD = BigDecimal.ZERO;
				for (PinganStatementDetail dt : ps.getDetails()) {
					feeBD = feeBD.add(BigDecimal.valueOf(dt.getFee()));
					TuserOrder order = orderService.getTorderByOrderNo(dt.getOrderNum());
					if (order != null) {
						TranPayOrder payOrder = order.getTranPayOrder();
						Tuser user = order.getUser();
						int statementFlag = "0000".equals(dt.getErrorCode()) ? UserOrder.order_status.SUCCESS.getCode() : UserOrder.order_status.FAILURE.getCode();
						Double pinganRealAmt = dt.getAmt() - dt.getFee();
						payOrder.setRealAmt(BigDecimal.valueOf(pinganRealAmt));
						if (order.getStatus() - statementFlag == 0) {
							Double payAmt = order.getAmt().doubleValue();// 订单实际金额=下单金额-手续费
							if (payAmt - dt.getAmt() == 0) {
								logger.info("对账成功：支付订单号=" + dt.getOrderNum());
								stsSuccussNum++;
							} else {
								/* 对账单中状态一致，需要人工确认金额不一致的原因， 调整完账务后，才能解冻账户 */
								String errorDesc = " 支付的金额不符合：对账单支付金额=" + dt.getAmt() + " 系统订单支付金额=" + payAmt + " ，系统冻结用户账户，请人工核对金额不同的原因，做完调账处理后，再解除冻结";
								logger.error("对账失败：支付订单号=" + dt.getOrderNum() + errorDesc);
								TorderStatement os = new TorderStatement(user, OrderStatement.statement_type.PINGAN.getCode(), dateyyyyMMdd, order.getId(), order.getOrderNum(), dt.getOrderNum(), 1,
										errorDesc);
								ordStatements.add(os);
								isAccountOrBrokerage(order, userIds, brokerageUserIds);
								stsFailNum++;
							}
						} else {

							/* 根据对账单自动处理系统的订单，若订单处于待处理状态或者是人工处理状态，系统自动调整，不需要锁定用户 */
							if (order.getStatus() == UserOrder.order_status.PROCESSING.getCode() || order.getStatus() == UserOrder.order_status.MANUAL_PROCESSING.getCode()) {
								try {

									logger.info("订单状态不一致，对账失败：支付订单号=" + dt.getOrderNum() + " 订单状态=" + UserOrder.getOrderStatusChineseName(order.getStatus()) + " 对账单状态" + dt.getErrorCode() + " | "
											+ dt.getErrorInfo());
									if (statementFlag == UserOrder.order_status.SUCCESS.getCode()) {
										logger.info("对账单中该订单已成功处理，系统自动调账用户账户");
										/* 处理成功 */
										order.setStatus(UserOrder.order_status.SUCCESS.getCode());
										payOrder.setFinishDate(new Date());
										payOrder.setPayFinishDate(dt.getStmDate());
										payOrder.setRealAmt(BigDecimal.valueOf(pinganRealAmt));
										payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
										if (UserOrder.trans_type.YJTX.getCode() == order.getType()) {
											accountService.updateBrokerageAccountAfterLiqSuccess(user.getId(), order);
										} else {
											accountService.updateAccountAfterLiqSuccess(user.getId(), order);
										}
									} else {
										logger.info("对账单中该订单处理失败，系统自动调账用户账户");
										/* 处理失败 */
										/* 若发送的订单失败，回退给用户锁定的money */
										/* 关闭订单 */
										// 订单失败状态
										order.setStatus(UserOrder.order_status.FAILURE.getCode());
										// 错误代码
										payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());

										payOrder.setErrorCode(dt.getErrorCode());
										payOrder.setErrorInfo(dt.getErrorInfo());
										/* 账户退款 */
										if (UserOrder.trans_type.YJTX.getCode() == order.getType()) {
											accountService.updateBrokerageAccountAfterLiqFailure(user.getId(), order);
										} else {
											accountService.updateAccountAfterLiqFailure(user.getId(), order);
										}
									}
									TorderStatement os = new TorderStatement(user, OrderStatement.statement_type.PINGAN.getCode(), dateyyyyMMdd, order.getId(), order.getOrderNum(), dt.getOrderNum(),
											0, "订单状态与对账单不符，订单为" + UserOrder.getOrderStatusChineseName(order.getStatus()) + ",系统已经自动完成对账调整");
									ordStatements.add(os);
									stsAutoDealNum++;
								} catch (Exception e) {
									logger.error("平安对账异常", e);
									stsFailNum++;
								}
							} else {
								/*
								 * 根据对账单自动处理系统的订单，若订单处于成功或者失败的状态，系统无法判断状态来源，需要锁定用户账户
								 * ，进行人工调账
								 */
								logger.error("订单状态不一致，对账失败：支付订单号=" + dt.getOrderNum());
								TorderStatement os = new TorderStatement(user, OrderStatement.statement_type.PINGAN.getCode(), dateyyyyMMdd, order.getId(), order.getOrderNum(), dt.getOrderNum(), 1,
										"订单状态与对账单不符，订单为" + UserOrder.getOrderStatusChineseName(order.getStatus()) + ",对账单为" + dt.getErrorCode() + " | " + dt.getErrorInfo());
								ordStatements.add(os);
								isAccountOrBrokerage(order, userIds, brokerageUserIds);
								stsFailNum++;
							}
						}

					} else {
						logger.error("未知来源的对账单数据，对账单中存在，订单中不存在，无法对账：支付订单号=" + dt.getOrderNum());
						// 订单中不存在，对账单中存在
						TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.PINGAN.getCode(), dateyyyyMMdd, null, null, dt.getOrderNum(), 1, "对账单中存在，订单中不存在，人工确认对账单中订单来源,订单号="
								+ dt.getOrderNum() + " 卡号=" + dt.getCardNo());
						ordStatements.add(os);
					}
				}
				orderStatementService.saveTorderStatements(ordStatements);
				/* 冻结钱包账户 */
				accountService.updateAccountWhenAccountException(userIds);
				/* 冻结佣金账户 */
				accountService.updateBrokerageAccountWhenAccountException(brokerageUserIds);

				/* 保存对账单汇总 */
				TransStatement transStatement = new TransStatement(OrderStatement.statement_type.PINGAN.getCode(), dateyyyyMMdd, ps.getTotalNum(), ps.getTotalSuccessNum(), BigDecimal.valueOf(ps
						.getTotalAmt()), BigDecimal.valueOf(ps.getTotalSuccessAmt()), feeBD, BigDecimal.valueOf(0.3d), stsSuccussNum, stsAutoDealNum, stsFailNum, userIds.size(),
						brokerageUserIds.size());

				add(transStatement);
			} else {
				desc = "对账文件不存在，请确定是否下载成功";
			}
		} catch (Exception e) {
			e.printStackTrace();
			desc = "对账文件不存在，请确定是否下载成功";

		}
		logger.info("-------平安对账" + dateyyyyMMdd + "结束-----  耗时：" + DateUtil.getBetweenSecond(startDate, new Date()));
		return desc;
	}

	/**
	 * 根据订单类型判断待锁定用户的账号类型
	 * 
	 * @param order
	 * @param userIds
	 *            钱包账户
	 * @param brokerageUserIds
	 *            佣金账户
	 */
	private void isAccountOrBrokerage(TuserOrder order, Set<Long> userIds, Set<Long> brokerageUserIds) {
		if (order != null) {
			if (order.getTransPayType() == UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode()) {
				userIds.add(order.getUser().getId());
			} else if (order.getTransPayType() == UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode()) {
				brokerageUserIds.add(order.getUser().getId());
			}
		}
	}

	@Override
	public void dealWeixinStatement(String filePath, String dateyyyyMMdd) {
		logger.info("--------开始微信" + dateyyyyMMdd + "对账-----");
		Date startDate = new Date();
		try {
			WeixinStatement ps = WeixinUtil.getWeixinStatement(filePath);
			if (ps != null && ps.getDetails().size() > 0) {
				/* 出现异常的用户ID */
				Set<Long> userIds = new HashSet<Long>();
				Set<Long> brokerageUserIds = new HashSet<Long>();
				List<TorderStatement> ordStatements = new ArrayList<>();

				Integer stsSuccussNum = 0;
				Integer stsAutoDealNum = 0;
				Integer stsFailNum = 0;

				for (WeixinStatementDetail dt : ps.getDetails()) {

					TuserOrder order = orderService.getTorderByOrderNo(dt.getOrderNum());
					if (order != null) {
						int statementFlag = "SUCCESS".equals(dt.getTranStatus()) ? UserOrder.order_status.SUCCESS.getCode() : UserOrder.order_status.FAILURE.getCode();
						Tuser user = order.getUser();
						Double weixinRealAmt = dt.getTotalAmt() - dt.getFee();

						TranPayOrder tpayOrder = order.getTranPayOrder();
						if (order.getStatus() == statementFlag) {
							Double payAmt = order.getOrgAmt().doubleValue();
							if (dt.getTotalAmt() - payAmt == 0) {
								logger.info("对账成功：支付订单号=" + dt.getOrderNum());
								stsSuccussNum++;
								tpayOrder.setFinishDate(DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", dt.getTranDateStr()));
							} else {
								String desc = "支付的金额不符合：对账单支付金额=" + dt.getTotalAmt() + " 系统订单支付金额=" + payAmt + " ，请人工核对,然后解冻账户";
								logger.error("对账失败：支付订单号=" + dt.getOrderNum() + desc);
								TorderStatement os = new TorderStatement(user, OrderStatement.statement_type.WEIXIN.getCode(), dateyyyyMMdd, order.getId(), order.getOrderNum(), dt.getOrderNum(), 1,
										desc);
								ordStatements.add(os);
								/* 金额不一致 锁定用户 */
								isAccountOrBrokerage(order, userIds, brokerageUserIds);
								stsFailNum++;
							}
						} else {
							logger.error("订单状态不一致，对账失败：支付订单号=" + dt.getOrderNum());
							if (statementFlag == UserOrder.order_status.SUCCESS.getCode()) {
								/*
								 * 根据对账单自动处理系统的订单，若订单处于待处理状态或者是人工处理状态，系统自动调整，不需要锁定用户
								 */
								if (order.getStatus() == UserOrder.order_status.PROCESSING.getCode() || order.getStatus() == UserOrder.order_status.MANUAL_PROCESSING.getCode()) {
									logger.info("订单状态不一致，对账失败：支付订单号=" + dt.getOrderNum() + " 订单状态=" + UserOrder.getOrderStatusChineseName(order.getStatus()) + " 对账单状态：" + dt.getTranStatus());
									PayOrder payOrder = new PayOrder();
									payOrder.setPayAmt(BigDecimal.valueOf(dt.getTotalAmt()));
									payOrder.setRealAmt(BigDecimal.valueOf(weixinRealAmt));
									payOrder.setPayNo(dt.getOrderNum());
									payOrder.setPayFinishDate(dt.getTranDateStr());

									payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
									try {
										orderService.finishInputOrderStatus(order, payOrder);
									} catch (Exception e) {
										logger.error("----微信对账单异常", e);
									}
									TorderStatement os = new TorderStatement(user, OrderStatement.statement_type.WEIXIN.getCode(), dateyyyyMMdd, order.getId(), order.getOrderNum(), null, 0,
											"订单状态与对账单不符，订单为" + UserOrder.getOrderStatusChineseName(order.getStatus()) + ",对账单为" + dt.getTranStatus() + " 系统自动完成订单和账户处理");
									ordStatements.add(os);
									stsAutoDealNum++;
								} else {
									String desc = "订单状态与对账单不符，订单为" + UserOrder.getOrderStatusChineseName(order.getStatus()) + ",对账单为" + dt.getTranStatus() + ",需人工查询状态不一致的原因，为用户解除冻结状态";
									logger.error("订单状态不一致，对账失败：支付订单号=" + order.getOrderNum());
									TorderStatement os = new TorderStatement(user, OrderStatement.statement_type.WEIXIN.getCode(), dateyyyyMMdd, order.getId(), order.getOrderNum(), dt.getOrderNum(),
											1, desc);
									ordStatements.add(os);
									/* 锁定用户的订单 */
									isAccountOrBrokerage(order, userIds, brokerageUserIds);
									stsFailNum++;
								}
							} else {
								// 微信 未支付的订单
								String desc = "订单状态不一致，对账对账单状态为：" + dt.getTranStatus() + "：支付订单号=" + dt.getOrderNum() + " 系统订单状态为" + UserOrder.getOrderStatusChineseName(order.getStatus());
								logger.info(desc);
								PayOrder payOrder = new PayOrder();
								payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
								payOrder.setErrorInfo("对账单中订单失败");
								try {
									orderService.finishInputOrderStatus(order, payOrder);
								} catch (Exception e) {
									logger.error("----微信对账单异常", e);
								}
								TorderStatement os = new TorderStatement(order.getUser(), OrderStatement.statement_type.WEIXIN.getCode(), dateyyyyMMdd, order.getId(), order.getOrderNum(), null, 1,
										desc);
								ordStatements.add(os);
								stsFailNum++;
							}
						}

					} else {
						logger.error("对账单中存在，订单中不存在，无法对账：支付订单号=" + dt.getOrderNum());
						// 订单中不存在，对账单中存在
						TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.WEIXIN.getCode(), dateyyyyMMdd, null, null, dt.getOrderNum(), 1, "对账单中存在，订单中不存在,订单号="
								+ dt.getOrderNum());
						ordStatements.add(os);
					}

				}
				orderStatementService.saveTorderStatements(ordStatements);
				// 冻结账户
				accountService.updateAccountWhenAccountException(userIds);
				/* 冻结佣金账户 */
				accountService.updateBrokerageAccountWhenAccountException(brokerageUserIds);

				/* 保存对账单汇总 */
				TransStatement transStatement = new TransStatement(OrderStatement.statement_type.WEIXIN.getCode(), dateyyyyMMdd, ps.getTotalNum(), ps.getTotalNum(), BigDecimal.valueOf(ps
						.getTotalAmt()), BigDecimal.valueOf(ps.getTotalAmt()), BigDecimal.valueOf(ps.getTotalFeeAmt()), ps.getFeeRate(), stsSuccussNum, stsAutoDealNum, stsFailNum, userIds.size(),
						brokerageUserIds.size());
				add(transStatement);
			} else {
				logger.error("微信对账文件下载出错或无对账订单，请检查！！");
			}
		} catch (Exception e) {
			logger.error("微信对账失败", e);
		}
		logger.info("-------微信" + dateyyyyMMdd + "对账结束-----  耗时：" + DateUtil.getBetweenSecond(startDate, new Date()));
	}

	/**
	 * 支付宝对账单处理 对账单中每一条记录都是成功的记录
	 */
	@Override
	public void dealAlipayStatement(String filePath, String dateyyyyMMdd) {
		logger.info("--------开始支付宝" + dateyyyyMMdd + "对账-----");
		Date startDate = new Date();
		try {
			AlipayStatement ps = AlipayUtil.getAlipayStatementInfo(filePath);
			if (ps != null && ps.getDetails().size() > 0) {
				/* 出现异常的用户ID */
				Set<Long> userIds = new HashSet<Long>();
				Set<Long> brokerageUserIds = new HashSet<Long>();
				List<TorderStatement> ordStatements = new ArrayList<>();

				Integer stsSuccussNum = 0;
				Integer stsAutoDealNum = 0;
				Integer stsFailNum = 0;
				for (AlipayStatementDetail dt : ps.getDetails()) {

					TuserOrder order = orderService.getTorderByOrderNo(dt.getOrderNum());
					if (order != null) {
						TranPayOrder tpayOrder = order.getTranPayOrder();
						Tuser user = order.getUser();
						if (order.getStatus() == UserOrder.order_status.SUCCESS.getCode()) {
							Double payAmt = order.getOrgAmt().doubleValue();
							if (dt.getOrderAmt() - payAmt == 0) {
								tpayOrder.setFinishDate(dt.getTranDate());
								logger.info("对账成功：支付订单号=" + dt.getOrderNum());
								stsSuccussNum++;
								payOrderDao.update(tpayOrder);
							} else {
								String desc = "支付的金额不符合：对账单支付金额=" + dt.getOrderAmt() + " 系统订单支付金额=" + payAmt + " ，请人工核对";
								logger.error("对账失败：支付订单号=" + desc);
								TorderStatement os = new TorderStatement(user, OrderStatement.statement_type.ALIPAY.getCode(), dateyyyyMMdd, order.getId(), order.getOrderNum(), dt.getOrderNum(), 1,
										desc);
								ordStatements.add(os);
								userIds.add(order.getUser().getId());
								/* 锁定用户的订单 */
								isAccountOrBrokerage(order, userIds, brokerageUserIds);
								stsFailNum++;
							}
						} else if (order.getStatus() == UserOrder.order_status.PROCESSING.getCode() || order.getStatus() == UserOrder.order_status.MANUAL_PROCESSING.getCode()) {

							PayOrder payOrder = new PayOrder();
							payOrder.setPayAmt(BigDecimal.valueOf(dt.getOrderAmt()));
							payOrder.setRealAmt(BigDecimal.valueOf(dt.getRealAmt()));
							payOrder.setPayNo(dt.getOrderNum());
							payOrder.setPayFinishDate(DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss", dt.getTranDate()));
							payOrder.setFinishDate(dt.getTranDate());
							payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
							try {
								orderService.finishInputOrderStatus(order, payOrder);

								TorderStatement os = new TorderStatement(user, OrderStatement.statement_type.ALIPAY.getCode(), dateyyyyMMdd, order.getId(), order.getOrderNum(), null, 0,
										"订单状态与对账单不符，订单为" + UserOrder.getOrderStatusChineseName(order.getStatus()) + ",对账单为SUCCESS, 系统自动完成订单和账户处理");
								ordStatements.add(os);
								stsAutoDealNum++;
							} catch (Exception e) {
								logger.error("----微信对账单异常", e);
								stsFailNum++;
							}

						} else {
							logger.error("订单状态不一致，对账失败：支付订单号=" + dt.getOrderNum());
							TorderStatement os = new TorderStatement(order.getUser(), OrderStatement.statement_type.ALIPAY.getCode(), dateyyyyMMdd, order.getId(), order.getOrderNum(),
									dt.getOrderNum(), 1, "订单状态不一致，对账失败：支付订单号=" + dt.getOrderNum());
							ordStatements.add(os);
							isAccountOrBrokerage(order, userIds, brokerageUserIds);
							stsFailNum++;
						}

					} else {
						logger.error("对账单中存在，订单中不存在，无法对账：支付订单号=" + dt.getOrderNum());
						// 订单中不存在，对账单中存在
						TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.ALIPAY.getCode(), dateyyyyMMdd, null, null, dt.getOrderNum(), 1, "对账单中存在，订单中不存在,订单号="
								+ dt.getOrderNum());
						ordStatements.add(os);
					}
				}
				orderStatementService.saveTorderStatements(ordStatements);
				// 冻结账户
				accountService.updateAccountWhenAccountException(userIds);
				/* 冻结佣金账户 */
				accountService.updateBrokerageAccountWhenAccountException(brokerageUserIds);

				/* 保存对账单汇总 */
				TransStatement transStatement = new TransStatement(OrderStatement.statement_type.ALIPAY.getCode(), dateyyyyMMdd, ps.getTotalNum(), ps.getTotalNum(), BigDecimal.valueOf(ps
						.getTotalTradeAmt()), BigDecimal.valueOf(ps.getTotalRealAmt()), BigDecimal.valueOf(ps.getTotalFeeAmt()), BigDecimal.ZERO, stsSuccussNum, stsAutoDealNum, stsFailNum,
						userIds.size(), brokerageUserIds.size());

				add(transStatement);
			} else {
				logger.error("支付宝对账文件下载出错，请检查！！");
			}
		} catch (Exception e) {
			logger.error("支付宝对账失败", e);
		}
		logger.info("-------支付宝" + dateyyyyMMdd + "对账结束-----  耗时：" + DateUtil.getBetweenSecond(startDate, new Date()));
	}

	@Override
	public void dealMinshengStatement(String filePath, String dateyyyyMMdd) {

		logger.info("--------开始民生" + dateyyyyMMdd + "对账-----");
		Date startDate = new Date();
		try {
			MinshengStatement ps = MinshengUtil.getMinshengStatement(filePath);
			if (ps != null && ps.getDetails().size() > 0) {
				/* 出现异常的用户ID */
				Set<Long> userIds = new HashSet<Long>();
				Set<Long> brokerageUserIds = new HashSet<Long>();
				List<TorderStatement> ordStatements = new ArrayList<>();

				Integer stsSuccussNum = 0;
				Integer stsAutoDealNum = 0;
				Integer stsFailNum = 0;
				int tradeType = OrderStatement.statement_type.MINSHENG.getCode();

				for (MinshengStatementDetail dt : ps.getDetails()) {
					TuserOrder order = orderService.getTorderByOrderNo(dt.getReqMsgId());
					if (order != null) {
						Tuser user = order.getUser();
						/* 民生 ：对账流水只包含状态respType为S成功且清算撤销标识isClearOrCancel为0的数据 */
						if (order.getStatus() == UserOrder.order_status.SUCCESS.getCode()) {
							Double payAmt = order.getOrgAmt().doubleValue();

							if (dt.getAmount() - payAmt == 0) {
								logger.info("对账成功：支付订单号=" + dt.getReqMsgId());
								stsSuccussNum++;
							} else {
								String desc = "支付的金额不符合：对账单支付金额=" + dt.getAmount() + " 系统订单支付金额=" + payAmt + " ，请人工核对";
								logger.error("对账失败：支付订单号=" + desc);
								TorderStatement os = new TorderStatement(user, tradeType, dateyyyyMMdd, order.getId(), order.getOrderNum(), dt.getReqMsgId(), 1, desc);
								ordStatements.add(os);
								userIds.add(order.getUser().getId());
								/* 锁定用户的订单 */
								isAccountOrBrokerage(order, userIds, brokerageUserIds);
								stsFailNum++;
							}
						} else if (order.getStatus() == UserOrder.order_status.PROCESSING.getCode() || order.getStatus() == UserOrder.order_status.MANUAL_PROCESSING.getCode()) {

							PayOrder payOrder = new PayOrder();
							payOrder.setPayAmt(BigDecimal.valueOf(dt.getAmount()));
							payOrder.setRealAmt(BigDecimal.valueOf(dt.getAmount() - dt.getFee()));
							payOrder.setPayNo(dt.getReqMsgId());
							payOrder.setPayFinishDate(dt.getSettleDate());

							payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
							try {
								orderService.finishInputOrderStatus(order, payOrder);

								TorderStatement os = new TorderStatement(user, tradeType, dateyyyyMMdd, order.getId(), order.getOrderNum(), null, 0, "订单状态与对账单不符，订单为"
										+ UserOrder.getOrderStatusChineseName(order.getStatus()) + ",对账单为SUCCESS, 系统自动完成订单和账户处理");
								ordStatements.add(os);
								stsAutoDealNum++;
							} catch (Exception e) {
								logger.error("----民生对账单异常", e);
								stsFailNum++;
							}

						} else {
							logger.error("订单状态不一致，对账失败：支付订单号=" + dt.getReqMsgId());
							TorderStatement os = new TorderStatement(order.getUser(), tradeType, dateyyyyMMdd, order.getId(), order.getOrderNum(), dt.getReqMsgId(), 1, "订单状态不一致，对账失败：支付订单号="
									+ dt.getReqMsgId());
							ordStatements.add(os);
							isAccountOrBrokerage(order, userIds, brokerageUserIds);
							stsFailNum++;
						}

					} else {
						logger.error("对账单中存在，订单中不存在，无法对账：支付订单号=" + dt.getReqMsgId());
						// 订单中不存在，对账单中存在
						TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.MINSHENG.getCode(), dateyyyyMMdd, null, null, dt.getReqMsgId(), 1, "对账单中存在，订单中不存在,订单号="
								+ dt.getReqMsgId());
						ordStatements.add(os);
					}
				}
				orderStatementService.saveTorderStatements(ordStatements);
				// 冻结账户
				accountService.updateAccountWhenAccountException(userIds);
				/* 冻结佣金账户 */
				accountService.updateBrokerageAccountWhenAccountException(brokerageUserIds);

				/* 保存对账单汇总 */
				TransStatement transStatement = new TransStatement(tradeType, dateyyyyMMdd, ps.getTotalNum(), ps.getTotalNum(), BigDecimal.valueOf(ps.getTotalAmt()), BigDecimal.valueOf(ps
						.getTotalAmt() - ps.getTotalFeeAmt()), BigDecimal.valueOf(ps.getTotalFeeAmt()), BigDecimal.ZERO, stsSuccussNum, stsAutoDealNum, stsFailNum, userIds.size(),
						brokerageUserIds.size());

				add(transStatement);
			} else {
				logger.error("民生对账文件下载出错，请检查！！");
			}
		} catch (Exception e) {
			logger.error("民生对账失败", e);
		}
		logger.info("-------民生" + dateyyyyMMdd + "对账结束-----  耗时：" + DateUtil.getBetweenSecond(startDate, new Date()));

	}

	@Override
	public void dealPinganStatement(String filePath, String dateyyyyMMdd) {

		logger.info("--------开始平安支付" + dateyyyyMMdd + "对账-----");
		Date startDate = new Date();
		try {
			PinganPayStatement ps = PinganUtil.getPinganPayStatement(filePath);
			if (ps != null && ps.getDetails().size() > 0) {
				/* 出现异常的用户ID */
				Set<Long> userIds = new HashSet<Long>();
				Set<Long> brokerageUserIds = new HashSet<Long>();
				List<TorderStatement> ordStatements = new ArrayList<>();

				Integer stsSuccussNum = 0;
				Integer stsAutoDealNum = 0;
				Integer stsFailNum = 0;
				int tradeType = OrderStatement.statement_type.PINGANPAY.getCode();

				for (PinganPayStatementDetail dt : ps.getDetails()) {
					TuserOrder order = orderService.getTorderByOrderNo(dt.getOrderNum());
					if (order != null) {
						Tuser user = order.getUser();
						/* 民生 ：对账流水只包含状态respType为S成功且清算撤销标识isClearOrCancel为0的数据 */
						if (order.getStatus() == UserOrder.order_status.SUCCESS.getCode()) {
							Double payAmt = order.getOrgAmt().doubleValue();

							if (dt.getOriAmt() - payAmt == 0) {
								logger.info("对账成功：支付订单号=" + dt.getOrderNum());
								stsSuccussNum++;
							} else {
								String desc = "支付的金额不符合：对账单支付金额=" + dt.getOriAmt() + " 系统订单支付金额=" + payAmt + " ，请人工核对";
								logger.error("对账失败：支付订单号=" + desc);
								TorderStatement os = new TorderStatement(user, tradeType, dateyyyyMMdd, order.getId(), order.getOrderNum(), dt.getOrderNum(), 1, desc);
								ordStatements.add(os);
								userIds.add(order.getUser().getId());
								/* 锁定用户的订单 */
								isAccountOrBrokerage(order, userIds, brokerageUserIds);
								stsFailNum++;
							}
						} else if (order.getStatus() == UserOrder.order_status.PROCESSING.getCode() || order.getStatus() == UserOrder.order_status.MANUAL_PROCESSING.getCode()) {

							PayOrder payOrder = new PayOrder();
							payOrder.setPayAmt(BigDecimal.valueOf(dt.getOriAmt()));
							payOrder.setRealAmt(BigDecimal.valueOf(dt.getOriAmt() - dt.getRealAmt()));
							payOrder.setPayNo(dt.getOrderNum());
							payOrder.setPayFinishDate(dt.getPaymentDate());

							payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
							try {
								orderService.finishInputOrderStatus(order, payOrder);

								TorderStatement os = new TorderStatement(user, tradeType, dateyyyyMMdd, order.getId(), order.getOrderNum(), null, 0, "订单状态与对账单不符，订单为"
										+ UserOrder.getOrderStatusChineseName(order.getStatus()) + ",对账单为SUCCESS, 系统自动完成订单和账户处理");
								ordStatements.add(os);
								stsAutoDealNum++;
							} catch (Exception e) {
								logger.error("----平安支付对账单异常", e);
								stsFailNum++;
							}

						} else {
							logger.error("订单状态不一致，对账失败：支付订单号=" + dt.getOrderNum());
							TorderStatement os = new TorderStatement(order.getUser(), tradeType, dateyyyyMMdd, order.getId(), order.getOrderNum(), dt.getOrderNum(), 1, "订单状态不一致，对账失败：支付订单号="
									+ dt.getOrderNum());
							ordStatements.add(os);
							isAccountOrBrokerage(order, userIds, brokerageUserIds);
							stsFailNum++;
						}

					} else {
						logger.error("对账单中存在，订单中不存在，无法对账：支付订单号=" + dt.getOrderNum());
						// 订单中不存在，对账单中存在
						TorderStatement os = new TorderStatement(null, OrderStatement.statement_type.PINGANPAY.getCode(), dateyyyyMMdd, null, null, dt.getOrderNum(), 1, "对账单中存在，订单中不存在,订单号="
								+ dt.getOrderNum());
						ordStatements.add(os);
					}
				}
				orderStatementService.saveTorderStatements(ordStatements);
				// 冻结账户
				accountService.updateAccountWhenAccountException(userIds);
				/* 冻结佣金账户 */
				accountService.updateBrokerageAccountWhenAccountException(brokerageUserIds);

				/* 保存对账单汇总 */
				TransStatement transStatement = new TransStatement(tradeType, dateyyyyMMdd, ps.getTotalNum(), ps.getTotalNum(), BigDecimal.valueOf(ps.getTotalAmt()), BigDecimal.valueOf(ps
						.getTotalAmt() - ps.getTotalFeeAmt()), BigDecimal.valueOf(ps.getTotalFeeAmt()), BigDecimal.ZERO, stsSuccussNum, stsAutoDealNum, stsFailNum, userIds.size(),
						brokerageUserIds.size());

				add(transStatement);
			} else {
				logger.error("平安支付对账文件下载出错，请检查！！");
			}
		} catch (Exception e) {
			logger.error("平安支付对账失败", e);
		}
		logger.info("-------平安支付" + dateyyyyMMdd + "对账结束-----  耗时：" + DateUtil.getBetweenSecond(startDate, new Date()));

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
	public void dealXinkeStatement(String filePath, String dateyyyyMMdd) {

		logger.info("--------开始欣客" + dateyyyyMMdd + "对账-----");
		Date startDate = new Date();
		try {
			XinkeStatement ps = XinkeUtil.getXinkeStatement(filePath);
			if (ps != null && ps.getDetails().size() > 0) {
				/* 出现异常的用户ID */
				Set<Long> userIds = new HashSet<Long>();
				Set<Long> brokerageUserIds = new HashSet<Long>();
				List<TorderStatement> ordStatements = new ArrayList<>();

				Integer stsSuccussNum = 0;
				Integer stsAutoDealNum = 0;
				Integer stsFailNum = 0;
				int tradeType = OrderStatement.statement_type.XINKE.getCode();

				for (XinkeStatementDetail dt : ps.getDetails()) {
					TuserOrder order = orderService.getOrderByPayNo(dt.getOrderNum());
					if (order != null) {
						Tuser user = order.getUser();
						/* 民生 ：对账流水只包含状态respType为S成功且清算撤销标识isClearOrCancel为0的数据 */
						if (order.getStatus() == UserOrder.order_status.SUCCESS.getCode()) {
							Double payAmt = order.getOrgAmt().doubleValue();

							if (Double.valueOf(dt.getAmt()) - payAmt == 0) {
								logger.info("对账成功：支付订单号=" + dt.getOrderNum());
								stsSuccussNum++;
							} else {
								String desc = "支付的金额不符合：对账单支付金额=" + dt.getAmt() + " 系统订单支付金额=" + payAmt + " ，请人工核对";
								logger.error("对账失败：支付订单号=" + desc);
								TorderStatement os = new TorderStatement(user, tradeType, dateyyyyMMdd, order.getId(), order.getOrderNum(), dt.getOrderNum(), 1, desc);
								ordStatements.add(os);
								userIds.add(order.getUser().getId());
								/* 锁定用户的订单 */
								isAccountOrBrokerage(order, userIds, brokerageUserIds);
								stsFailNum++;
							}
						} else if (order.getStatus() == UserOrder.order_status.PROCESSING.getCode() || order.getStatus() == UserOrder.order_status.MANUAL_PROCESSING.getCode()) {

							PayOrder payOrder = new PayOrder();
							payOrder.setPayAmt(BigDecimal.valueOf(Double.valueOf(dt.getAmt())));
							// payOrder.setRealAmt(BigDecimal.valueOf(dt.getAmount()
							// - dt.getFee()));
							// payOrder.setPayNo(dt.getReqMsgId());
							payOrder.setPayFinishDate(dt.getSettlementDate());

							payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
							try {
								orderService.finishInputOrderStatus(order, payOrder);

								TorderStatement os = new TorderStatement(user, tradeType, dateyyyyMMdd, order.getId(), order.getOrderNum(), null, 0, "订单状态与对账单不符，订单为"
										+ UserOrder.getOrderStatusChineseName(order.getStatus()) + ",对账单为SUCCESS, 系统自动完成订单和账户处理");
								ordStatements.add(os);
								stsAutoDealNum++;
							} catch (Exception e) {
								logger.error("----欣客对账单异常", e);
								stsFailNum++;
							}

						} else {
							logger.error("订单状态不一致，对账失败：支付订单号=" + dt.getOrderNum());
							TorderStatement os = new TorderStatement(order.getUser(), tradeType, dateyyyyMMdd, order.getId(), order.getOrderNum(), dt.getOrderNum(), 1, "订单状态不一致，对账失败：支付订单号="
									+ dt.getOrderNum());
							ordStatements.add(os);
							isAccountOrBrokerage(order, userIds, brokerageUserIds);
							stsFailNum++;
						}

					} else {
						logger.error("对账单中存在，订单中不存在，无法对账：支付订单号=" + dt.getOrderNum());
						// 订单中不存在，对账单中存在
						TorderStatement os = new TorderStatement(null, tradeType, dateyyyyMMdd, null, null, dt.getOrderNum(), 1, "对账单中存在，订单中不存在,订单号=" + dt.getOrderNum());
						ordStatements.add(os);
					}
				}
				orderStatementService.saveTorderStatements(ordStatements);
				// 冻结账户
				accountService.updateAccountWhenAccountException(userIds);
				/* 冻结佣金账户 */
				accountService.updateBrokerageAccountWhenAccountException(brokerageUserIds);

				/* 保存对账单汇总 */
				TransStatement transStatement = new TransStatement(tradeType, dateyyyyMMdd, ps.getTotalNum(), ps.getTotalNum(), BigDecimal.valueOf(ps.getTotalAmt()), BigDecimal.valueOf(ps
						.getTotalAmt() - ps.getTotalFeeAmt()), BigDecimal.valueOf(ps.getTotalFeeAmt()), BigDecimal.ZERO, stsSuccussNum, stsAutoDealNum, stsFailNum, userIds.size(),
						brokerageUserIds.size());

				add(transStatement);
			} else {
				logger.error("欣客对账文件下载出错，请检查！！");
			}
		} catch (Exception e) {
			logger.error("欣客对账失败", e);
		}
		logger.info("-------欣客" + dateyyyyMMdd + "对账结束-----  耗时：" + DateUtil.getBetweenSecond(startDate, new Date()));

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
			try {
				if (StringUtil.isNotBlank(statement.getCreateDateStart())) {
					hql += " and t.createDate >= :createTimeStart";
					params.put("createTimeStart", DateUtil.getDateFromString(statement.getCreateDateStart()));
				}
				if (StringUtil.isNotBlank(statement.getCreateDateEnd())) {
					hql += " and t.createDate <= :createTimeEnd";
					params.put("createTimeEnd", DateUtil.getEndOfDay(DateUtil.getDateFromString(statement.getCreateDateEnd())));
				}
			} catch (Exception e) {
				logger.error("search statement log error", e);
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

		statementDao.save(statement);
	}
}
