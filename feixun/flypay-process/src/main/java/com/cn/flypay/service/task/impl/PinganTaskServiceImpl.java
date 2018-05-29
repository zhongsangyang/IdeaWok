package com.cn.flypay.service.task.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.trans.TpinganFileDeal;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.trans.PinganFileDeal;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.payment.PingAnExpenseService;
import com.cn.flypay.service.payment.PinganFileDealService;
import com.cn.flypay.service.payment.impl.ShenFuPaymentServiceImpl;
import com.cn.flypay.service.statement.StatementService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.service.task.PinganTaskService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.DateUtil;

/**
 * 业务消息处理 开发者根据自己的业务自行处理消息的接收与回复；
 */

@Service
public class PinganTaskServiceImpl implements PinganTaskService {

	private static final Logger LOG = LoggerFactory.getLogger(PinganTaskServiceImpl.class);

	@Autowired
	private PingAnExpenseService pingAnExpenseService;
	@Autowired
	private PinganFileDealService fileDealService;
	@Autowired
	private StatementService tradeStatementService;
	@Autowired
	private BaseDao<TuserOrder> orderDao;
	@Autowired
	private SysParamService sysParamService;
	@Autowired
	private ChannelPaymentService shenFuPaymentService;
	@Autowired
	UserOrderService userOrderService;

	@Value("${pingan_T1_batch_root_path}")
	private String pingan_T1_batch_root_path;

	/**
	 * 实时异步查询未出代付结果的申孚订单
	 */
	@Override
	public void searchOrderToShenfu() {
		LOG.info("----每5分钟处理未给处理结果的申孚代付订单---start---");
		try {
			// 查询10-40分钟前的单笔代付结果查询 最多4次。
			String hql = "select t from TuserOrder t left join t.tranPayOrder p left join p.payChannel c where t.status=300 and t.payType=0 and t.type in(700,710) and c.name='SHENFUDAIFU' ";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("startDate", DateUtil.getMinutebyEndInterval(new Date(), 40));
			params.put("endDate", DateUtil.getMinutebyEndInterval(new Date(), 10));
			List<TuserOrder> tols = orderDao.find(hql);
			for (TuserOrder order : tols) {
				userOrderService.inquireDaiFuOrderToShenfu(order.getOrderNum());
			}
		} catch (Exception e) {
			LOG.error("处理昨天的申孚代付订单异常", e);
		}
		LOG.info("----每5分钟处理未给处理结果的申孚代付订单---end---");
	}

	/**
	 * 实时异步查询未出代付结果的订单
	 */
	@Override
	public void dealSearchOrderToPingan() {
		LOG.info("----每分钟处理未给处理结果的平安代付订单---start---");
		try {
			// 查询2-12分钟前的单笔代付结果查询
			String hql = "select t from TuserOrder t where t.status=300 and t.payType=0 and t.type in(700,710)";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("startDate", DateUtil.getMinutebyEndInterval(new Date(), 12));
			params.put("endDate", DateUtil.getMinutebyEndInterval(new Date(), 2));
			List<TuserOrder> tols = orderDao.find(hql);
			for (TuserOrder t : tols) {
				pingAnExpenseService.sendSearchOrderToPingAN(t.getId());
			}
		} catch (Exception e) {
			LOG.error("处理昨天的T1订单异常", e);
		}
		LOG.info("----每分钟处理未给处理结果的平安代付订单---end---");
	}

	/**
	 * 实时异步查询未出代付结果的订单
	 */
	@Override
	public void dealSearchOrderToPinganLong() {
		LOG.info("----处理待人工处理状态的平安代付订单---start---");
		try {
			// 查询10天前的单笔代付结果查询，
			String hql = "select t from TuserOrder t where t.status=500 and t.scanNum=6 and t.payType=0 and t.type in(700,710)";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("startDate", DateUtil.getDatebyInterval(new Date(), -10));
			params.put("endDate", DateUtil.getDatebyInterval(new Date(), 0));
			List<TuserOrder> orders = orderDao.find(hql);
			for (TuserOrder order : orders) {
				LOG.info("process Long tixian order={}", order.getOrderNum());
				pingAnExpenseService.sendSearchOrderToPingANLong(order.getId());
			}
		} catch (Exception e) {
			LOG.error("处理昨天的T1订单异常", e);
		}
		LOG.info("----处理待人工处理状态的平安代付订单---end---");
	}

	@Override
	public void dealT1OrderToPingan() {
		LOG.info("----处理昨天的T1订单---start---");
		Date dealDate = DateUtil.getBeforeDate(new Date(), 1);
		try {
			TpinganFileDeal fileDeal = pingAnExpenseService
					.sendBatchT1OrderToPingAN(DateUtil.convertDateStrYYYYMMDD(dealDate));
			if (fileDeal != null && PinganFileDeal.file_status.file01_success.name().equals(fileDeal.getStatus())) {
				/* 发送代付指令给平安 */
				// pingAnExpenseService.sendKHKF01ToPingan(fileDeal);
			} else {
				LOG.info("代付文件发送失败");
			}
		} catch (Exception e) {
			LOG.error("处理昨天的T1订单异常", e);
		}
		LOG.info("----处理昨天的T1订单---end---");
	}

	@Override
	public void dealT1OrderSendBatchDealToPingan() {
		LOG.info("----发送批量处理指令---start---");
		Date dealDate = DateUtil.getBeforeDate(new Date(), 1);
		TpinganFileDeal pfd = fileDealService.getWaitPayCommandFileDealByFileName(
				DateUtil.convertDateStrYYYYMMDD(dealDate), PinganFileDeal.file_status.file04_success.name());
		if (pfd != null) {
			try {
				// pingAnExpenseService.sendKHKF01ToPingan(pfd);
			} catch (Exception e) {
				LOG.error("处理昨天的T1订单异常", e);
			}
		}
		LOG.info("----发送批量处理指令---end---");
	}

	@Override
	public void dealT1OrderSendBatchSearchToPingan() {
		LOG.info("----发送批量处理查询指令---start---");
		Date dealDate = DateUtil.getBeforeDate(new Date(), 1);
		TpinganFileDeal pfd = fileDealService.getWaitDownFileDealByFileName(DateUtil.convertDateStrYYYYMMDD(dealDate),
				PinganFileDeal.file_status.KHKF01_success.name());
		if (pfd != null) {
			try {
				pingAnExpenseService.sendKHKF02ToPingan(pfd);
			} catch (Exception e) {
				LOG.error("处理昨天的T1订单异常", e);
			}
		}
		LOG.info("----发送批量处理查询指令---end---");
	}

	@Override
	public void dealT1Result() {
		LOG.info("----处理t1的批量订单---start---");
		Date dealDate = DateUtil.getBeforeDate(new Date(), 1);
		/* 对账单已下载 */
		TpinganFileDeal pfd = fileDealService.getWaitPayCommandFileDealByFileName(
				DateUtil.convertDateStrYYYYMMDD(dealDate), PinganFileDeal.file_status.file03_success.name());
		if (pfd != null) {
			try {
				pingAnExpenseService.dealBatchT1Result(pfd);
			} catch (Exception e) {
				LOG.error("处理昨天的T1订单异常", e);
			}
		}
		LOG.info("----处理t1的批量订单---end---");
	}

	@Override
	public void dealDownLoadStatement() {
		LOG.info("----平安对账单下载处理---start---");
		Date dealDate = DateUtil.getBeforeDate(new Date(), 1);
		/* 对账单已下载 */
		try {
			pingAnExpenseService.dealDownLoadStatement(DateUtil.convertDateStrYYYYMMDD(dealDate));
		} catch (Exception e) {
			LOG.error("平安对账单下载异常", e);
		}
		LOG.info("----平安对账单下载处理---end---");
	}

	@Override
	public void dealStatement() {
		LOG.info("----平安对账单处理---start---");
		Date dealDate = DateUtil.getBeforeDate(new Date(), 1);
		/* 对账单已下载 */
		try {
			tradeStatementService.dealBatchPinganStatement(null, DateUtil.convertDateStrYYYYMMDD(dealDate));
		} catch (Exception e) {
			LOG.error("平安对账单处理异常", e);
		}
		LOG.info("----平安对账单处理---end---");
	}
}
