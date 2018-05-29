package com.cn.flypay.service.payment;

import java.math.BigDecimal;
import java.util.Set;

import com.cn.flypay.pageModel.payment.ChannelPayRef;

/**
 * 路由服务器
 * 
 * @author sunyue
 * 
 */
public interface RouteService {

	/**
	 * 根据订单的交易类型和交易金额，获得处理的通道
	 * 
	 * @param transType
	 * @param tradeAmt
	 * @param userType
	 * @param userId
	 * @param limitType
	 *            通道限额类型，0通用，1 小额；2 大额
	 * @return
	 */
	public ChannelPayRef checkChannelPayRoute(Integer transType, BigDecimal tradeAmt, Integer userType, Long userId, Integer limitType);

	/**
	 * 根据订单的交易类型和交易金额，获得处理的通道
	 * 
	 * @param transType
	 * @param tradeAmt
	 * @param userType
	 * @param userId
	 * @param limitType
	 *            通道限额类型，0通用，1 小额；2 大额
	 * @return
	 */
	public ChannelPayRef checkChannelPayRoute(Integer transType, BigDecimal tradeAmt, Integer userType, Long userId, Integer limitType, String channelCode);

	/**
	 * 获取默认的支付通道，例如线上支付
	 * 
	 * @param transType
	 * @return
	 */
	public ChannelPayRef getDefaultChannelPayRoute(Integer transType);

	public ChannelPaymentService getChannelPayRouteByChannelName(String channelName);

	public Set<Integer> getChannelUserTypes(Integer userType);

	public ChannelPayRef getYLZXET1ChannelPayRoute(Integer transType, BigDecimal tradeAmt, Integer userType, Long userId, Integer limitType);

}
