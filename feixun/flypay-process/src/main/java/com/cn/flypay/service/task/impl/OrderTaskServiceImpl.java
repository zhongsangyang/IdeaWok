package com.cn.flypay.service.task.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.task.OrderTaskService;
import com.cn.flypay.service.trans.OfflineDrawOrderService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.DateUtil;

@Service
public class OrderTaskServiceImpl implements OrderTaskService {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserOrderService userOrderService;
	@Autowired
	OfflineDrawOrderService offlineDrawOrderService;
	@Autowired
	private BaseDao<TuserOrder> orderDao;

	/**
	 * 实时异步查询未出代付结果的订单
	 */
	@Override
	public void dealProcessOrderAfterOneHours() {
		log.info("----每分钟处理未给结果的支付订单订单---start---");
		try {
			// 查询一小时之前待支付结果查询，
			String hql = "select t from TuserOrder t where t.status=300 and t.type in(:collectOrderTypes) and t.createTime between :startDate and :endDate ";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("startDate", DateUtil.getMinutebyEndInterval(new Date(), 60));
			params.put("endDate", DateUtil.getMinutebyEndInterval(new Date(), 59));
			params.put("collectOrderTypes", UserOrder.getCollectOrderTypes());
			List<TuserOrder> tols = orderDao.find(hql, params);
			for (TuserOrder t : tols) {
				userOrderService.dealReSentSearchOrder(t.getOrderNum());
			}
		} catch (Exception e) {
			log.error("每分钟处理未给结果的支付订单订单异常", e);
		}
		log.info("----每分钟处理未给结果的支付订单订单---end---");
	}

	/**
	 * 打批代付订单
	 */
	public void dealBunchOfflineOrder() {
		log.info("----打批代付订单---start---");
		int countNum = 0;
		try {
			countNum = offlineDrawOrderService.updateBunch();
		} catch (Exception e) {
			log.error("打批代付订单异常", e);
		}
		log.info("----打批代付订单数量={}---end---", countNum);
	}
}
