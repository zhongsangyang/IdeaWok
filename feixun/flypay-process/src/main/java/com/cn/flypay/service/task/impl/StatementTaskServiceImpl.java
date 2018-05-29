package com.cn.flypay.service.task.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.task.StatementTaskService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.DateUtil;

/**
 * 处理微信对账单 的定时器 ，每天10点开始
 * 
 * @author sunyue
 * 
 */
@Service
public class StatementTaskServiceImpl implements StatementTaskService {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private ChannelPaymentService weixinPaymentService;
	@Autowired
	private ChannelPaymentService minshengPaymentService;
	@Autowired
	private ChannelPaymentService alipayPaymentService;
	@Autowired
	private ChannelPaymentService pinganPaymentService;
	@Autowired
	private UserOrderService orderService;

	@Override
	public void dealWeixinStatement() {
		log.info("----dealWeixinStatement---start---");
		String dateStr = DateUtil.convertDateStrYYYYMMDD(DateUtil.getBeforeDate(new Date(), 1));
		weixinPaymentService.dealDownloadStatement(dateStr);
		log.info("----dealWeixinStatement---end---");
	}

	@Override
	public void dealAlipayStatement() {
		log.info("----dealAlipayStatement---start---");
		String dateStr = DateUtil.convertDateStrYYYYMMDD(DateUtil.getBeforeDate(new Date(), 1));
		alipayPaymentService.dealDownloadStatement(dateStr);
		log.info("----dealAlipayStatement---end---");
	}

	@Override
	public void dealMinshengStatement() {
		log.info("----dealMinshengStatement---start---");
		String dateStr = DateUtil.convertDateStrYYYYMMDD(DateUtil.getBeforeDate(new Date(), 1));
		minshengPaymentService.dealDownloadStatement(dateStr);
		log.info("----dealMinshengStatement---end---");
	}

	@Override
	public void dealPinganPayStatement() {
		log.info("----dealPinganPayStatement---start---");
		String dateStr = DateUtil.convertDateStrYYYYMMDD(DateUtil.getBeforeDate(new Date(), 1));
		pinganPaymentService.dealDownloadStatement(dateStr);
		log.info("----dealPinganPayStatement---end---");
	}

	@Override
	public void dealProcessingZanShanFuOrderBeforeTwoDays() {
		log.info("----关闭2天以前的在线银联订单---start---");
		String dateStr = DateUtil.convertDateStrYYYYMMDD(DateUtil.getBeforeDate(new Date(), 3));
		Set<Integer> types = new HashSet<Integer>();
		types.add(UserOrder.trans_type.YLZX.getCode());
		orderService.dealProcessingOrderBeforeTwoDays(dateStr, types);
		log.info("----关闭2天以前的在线银联订单---end---");

	}

	@Override
	public void dealProcessingWeixinOrderBeforeTwoDays() {
		log.info("----关闭2天以前的微信订单---start---");
		String dateStr = DateUtil.convertDateStrYYYYMMDD(DateUtil.getBeforeDate(new Date(), 3));
		Set<Integer> types = new HashSet<Integer>();
		types.add(UserOrder.trans_type.WXQR.getCode());
		types.add(UserOrder.trans_type.WXSM.getCode());
		types.add(UserOrder.trans_type.WXOL.getCode());
		orderService.dealProcessingOrderBeforeTwoDays(dateStr, types);
		log.info("----关闭2天以前的微信订单---end---");

	}

	@Override
	public void dealProcessingZhifubaoOrderBeforeTwoDays() {
		log.info("----关闭2天以前的支付宝订单---start---");
		String dateStr = DateUtil.convertDateStrYYYYMMDD(DateUtil.getBeforeDate(new Date(), 3));
		Set<Integer> types = new HashSet<Integer>();
		types.add(UserOrder.trans_type.ALQR.getCode());
		types.add(UserOrder.trans_type.ALSM.getCode());
		types.add(UserOrder.trans_type.ALOL.getCode());
		orderService.dealProcessingOrderBeforeTwoDays(dateStr, types);
		log.info("----关闭2天以前的支付宝订单---end---");
	}

	@Override
	public void dealProcessingJingdongOrderBeforeTwoDays() {
		log.info("----关闭2天以前的京东订单---start---");
		String dateStr = DateUtil.convertDateStrYYYYMMDD(DateUtil.getBeforeDate(new Date(), 3));
		Set<Integer> types = new HashSet<Integer>();
		types.add(UserOrder.trans_type.JDQR.getCode());
		types.add(UserOrder.trans_type.JDSM.getCode());
		types.add(UserOrder.trans_type.JDOL.getCode());
		orderService.dealProcessingOrderBeforeTwoDays(dateStr, types);
		log.info("----关闭2天以前的京东订单---end---");
	}

	@Override
	public void dealProcessingBaiduOrderBeforeTwoDays() {
		log.info("----关闭2天以前的百度订单---start---");
		String dateStr = DateUtil.convertDateStrYYYYMMDD(DateUtil.getBeforeDate(new Date(), 3));
		Set<Integer> types = new HashSet<Integer>();
		types.add(UserOrder.trans_type.BDQR.getCode());
		types.add(UserOrder.trans_type.BDSM.getCode());
		types.add(UserOrder.trans_type.BDOL.getCode());
		orderService.dealProcessingOrderBeforeTwoDays(dateStr, types);
		log.info("----关闭2天以前的百度订单---end---");
	}

	@Override
	public void dealProcessingYiZhifuOrderBeforeTwoDays() {
		log.info("----关闭2天以前的翼支付订单---start---");
		String dateStr = DateUtil.convertDateStrYYYYMMDD(DateUtil.getBeforeDate(new Date(), 3));
		Set<Integer> types = new HashSet<Integer>();
		types.add(UserOrder.trans_type.YZFQR.getCode());
		types.add(UserOrder.trans_type.YISM.getCode());
		types.add(UserOrder.trans_type.YIOL.getCode());
		orderService.dealProcessingOrderBeforeTwoDays(dateStr, types);
		log.info("----关闭2天以前的翼支付订单---end---");
	}

	@Override
	public void dealProcessingYinLianOrderBeforeTwoDays() {
		log.info("----关闭2天以前的银联支付订单---start---");
		String dateStr = DateUtil.convertDateStrYYYYMMDD(DateUtil.getBeforeDate(new Date(), 3));
		Set<Integer> types = new HashSet<Integer>();
		types.add(UserOrder.trans_type.YLQR.getCode());
		types.add(UserOrder.trans_type.YLSM.getCode());
		types.add(UserOrder.trans_type.YLOL.getCode());
		orderService.dealProcessingOrderBeforeTwoDays(dateStr, types);
		log.info("----关闭2天以前的银联订单---end---");
	}
}
