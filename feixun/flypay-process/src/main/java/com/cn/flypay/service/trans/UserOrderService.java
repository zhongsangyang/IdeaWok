package com.cn.flypay.service.trans;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;

import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.FinanceStatement;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;

public interface UserOrderService {

	public List<UserOrder> dataGrid(UserOrder order, PageFilter pf);

	public List<UserOrder> dataGridXS(UserOrder order, PageFilter pf);

	public Long count(UserOrder order, PageFilter pf);

	public UserOrder get(Long id);

	/**
	 * 根据订单号查询待处理的订单
	 * 
	 * @param orderNum
	 * @return
	 */
	public UserOrder findTodoUserOrderByOrderNum(String orderNum);

	/**
	 * 根据订单号查询订单
	 * 
	 * @param orderNum
	 * @return
	 */
	public UserOrder findOrderByOrderNum(String orderNum);

	/**
	 * 根据订单号查询待处理的订单
	 * 
	 * @param payNum
	 * @return
	 */
	public UserOrder findTodoUserOrderByPayNum(String payNum);

	/**
	 * 创建支付订单
	 * 
	 * @param userId
	 *            订单用户ID
	 * @param orderNum
	 *            系统订单号
	 * @param payNum
	 *            清算订单号
	 * @param busNo
	 *            业务参考号
	 * @param transType
	 *            支付类型
	 * @param amt
	 *            原始金额
	 * @param accType
	 * 
	 * @param payNo
	 *            支付通道的订单号
	 * @param card
	 *            银联在线用户所刷卡
	 * @param orderDesc
	 *            订单描述
	 * @param transPayType
	 *            10 普通支付类型 20 代理费支付类型
	 * @param channel
	 *            通道
	 * 
	 */
	
	/**
	 * 创建支付订单
	 * @param userId 订单用户ID
	 * @param orderNum  系统订单号
	 * @param payNum 清算订单号
	 * @param busNo 业务参考号
	 * @param transType  支付类型
	 * @param amt 原始金额
	 * @param accType
	 * @param payNo 支付通道的订单号
	 * @param card 银联在线用户所刷卡
	 * @param orderDesc 订单描述
	 * @param transPayType 10 普通支付类型 20 代理费支付类型
	 * @param channel  通道
	 * @param inputAccType 
	 * @param angentType  0 通用，21 钻石升级，22 金牌升级
	 * @throws Exception
	 */
	public String createTransOrder(Long userId, String orderNum, String payNum, String busNo, Integer transType,
			Double amt, String accType, String payNo, TuserCard card, String orderDesc, Integer transPayType,
			Channel channel, Integer inputAccType, Integer angentType) throws Exception;

	/**
	 * 根据支付异步回执更新用户的订单和账户信息
	 * 
	 * @param orderNum
	 * @param payOrder
	 * @return
	 * @throws Exception
	 */
	public String finishInputOrderStatus(String orderNum, PayOrder payOrder) throws Exception;

	public String finishInputOrderStatus(TuserOrder order, PayOrder payOrder) throws Exception;

	/**
	 * 创建用户的提现订单
	 * 
	 * @param account
	 * @param orderNum
	 *            提现订单号
	 * @param amt
	 *            提现金额
	 * @param isT0
	 *            true:T0 false:T1
	 * @param desc
	 *            提现描述
	 */
	public TuserOrder createWithdrawOrder(Taccount account, String orderNum, Double amt, Boolean isT0, String desc, TuserCard settlementCard, Integer transPayType) throws Exception;

	/**
	 * 
	 * @param userId
	 * @param orderNum
	 * @param amt
	 * @param desc
	 * @param card
	 * @throws Exception
	 */
	public TuserOrder createBrokerageOrder(Long userId, String orderNum, Double amt, String desc, TuserCard card) throws Exception;

	/**
	 * 根据订单查询出订单
	 * 
	 * @param orderNums
	 */
	public List<UserOrder> findUserOrderByOrderNums(String[] orderNums);

	/**
	 * 确认订单状态
	 * 
	 * @param orderId
	 * @param isSuccess
	 * @throws Exception
	 */
	public String affirmOrderStatus(Long orderId, Boolean isSuccess, User operator) throws Exception;

	public TuserOrder getOrderByPayNo(String payNo);

	public TuserOrder getTorderByOrderNo(String orderNum);

	/**
	 * 处理未完成的订单 的定时器 ，每天0点开始，关闭2天前的订单
	 * 
	 */
	public String dealProcessingOrderBeforeTwoDays(String dateStr, Set<Integer> tranTypes);

	public TuserOrder getTuserOrderByOrderNum(String orderNum);

	public Map<String, String> dealReSentSearchOrder(String orderNum);

	/**
	 * 按照工作日统计财务情况
	 * 
	 * @param statement
	 * @return
	 */
	public List<FinanceStatement> findFinanceStatement(FinanceStatement statement);

	/**
	 * 按照每天统计财务情况
	 * 
	 * @param statement
	 * @return
	 */
	List<FinanceStatement> findFinanceStatementPerDate(FinanceStatement statement);

	/**
	 * 判断用户是否在五分钟内购买过代理
	 * 
	 * @param parseLong
	 * @param agentType
	 * @return
	 */
	public String isAllowPayAgent(User u, Integer agentType);

	FinanceStatement getT1OutFinanceAccount(Date endStatement, Integer status);

	FinanceStatement getYesterOutFinanceAccount(Date endStatement, Integer status);

	FinanceStatement getT0OutFinanceAccount(Date endStatement, Integer status);

	FinanceStatement getOutFinanceAccount(Date startStatement, Date endStatement, Integer isT0, Integer status);

	public Workbook exportExcel(UserOrder userOrder);

	UserOrder findTodoUserOrderByBusNo(String busNo);

	BigDecimal getOrderOrgAmt(UserOrder order, PageFilter pf);

	BigDecimal getOrderOrgAmtTwo(UserOrder order, PageFilter pf);

	BigDecimal getOrderOrg(Long userId);

	public String adjustFirstUnipayInfo(Long userId);

	public void createTransOrderOperationRecordByUserOrder(UserOrder userOrder, User user, String operationName);

	public void updatePayOrderAfterNotify(PayOrder payOrder, TuserOrder tOrder);

	public void inquireDaiFuOrderToShenfu(String orderNum);

	public void affirmDaiFuNotify(boolean success, String orderNum, PayOrder payOrder);

}
