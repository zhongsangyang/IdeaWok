package com.cn.flypay.service.task.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.trans.TorderBonusProcess;
import com.cn.flypay.service.task.BrokerageTaskService;
import com.cn.flypay.service.trans.RouteShareBonusService;

/**
 * 业务消息处理 开发者根据自己的业务自行处理消息的接收与回复；
 */

@Service
public class BrokerageTaskServiceImpl implements BrokerageTaskService {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private BaseDao<TorderBonusProcess> processDao;
	@Autowired
	private RouteShareBonusService routeShareBonusService;

	/**
	 * 实时异步查询未出代付结果的订单
	 */
	@Override
	public void dealShareBrokerage() {

		log.info("----每分钟处理未分润的订单--start---");
		try {
			String hql = " select t from TorderBonusProcess t left join t.order d  left join d.user u left join u.organization g where t.status=0 ";
			List<TorderBonusProcess> tols = processDao.find(hql);
			for (TorderBonusProcess t : tols) {
				routeShareBonusService.dealBonusWhenOrder(t);
			}
		} catch (Exception e) {
			log.error("未分润的订单异常", e);
		}
		log.info("----每分钟处理未分润的订单---end---");
	}
}
