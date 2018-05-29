package com.cn.flypay.service.payment.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.payment.ZanshanfuExpenseService;
import com.cn.flypay.service.sys.HolidayService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.ExcelUtil;

/**
 * Created by sunyue on 16/8/1.
 */

@Service(value = "zanshanfuExpenseService")
public class ZanshanfuExpenseServiceImpl implements ZanshanfuExpenseService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BaseDao<TuserOrder> orderDao;
	@Autowired
	private HolidayService holidayService;

	@Override
	public Workbook exportT1Zanshanfu(String dateyyyyMMdd) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		Date t1Date = DateUtil.convertStringToDate("yyyyMMdd", dateyyyyMMdd);
		Date[] dates = holidayService.getT1StartAndEndStatementWorkDate(DateUtil.getDatebyInterval(t1Date, 1));
		params.put("dateTimeStart", dates[0]);
		params.put("dateTimeEnd", dates[1]);
		Set<Integer> codes = new HashSet<Integer>();
		codes.add(UserOrder.trans_type.XJTX.getCode());
		codes.add(UserOrder.trans_type.YJTX.getCode());
		params.put("type", codes);
		params.put("status", UserOrder.order_status.PROCESSING.getCode());
		params.put("payType", 1);
		List<TuserOrder> orderList = orderDao.find("select t from TuserOrder t left join t.user u left join t.tranPayOrder p left join t.card c where  "
				+ "  t.status=:status and t.type in(:type)  and t.payType=:payType and  t.createTime BETWEEN :dateTimeStart and :dateTimeEnd", params);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String[] keys = new String[] { "cardNo", "idNo", "realName", "phone", "cvv", "validateDate", "amt" };
		String[] columnNames = new String[] { "银行卡号", "身份证号", "真实姓名", "银行卡绑定的手机号", "银行卡背面的cvn2三位数字", "银行卡有效期（格式：2501，年在前月在后）", "交易金额（元）" };
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("sheetName", DateUtil.convertDateStrYYYYMMDD(new Date()));
		list.add(m);
		if (orderList != null && orderList.size() > 0) {
			for (TuserOrder order : orderList) {
				Map<String, Object> contents = new HashMap<String, Object>();
				contents.put(keys[0], order.getCard().getCardNo());
				contents.put(keys[1], order.getUser().getIdNo());
				contents.put(keys[2], order.getUser().getRealName());
				contents.put(keys[3], order.getCard().getPhone());
				contents.put(keys[4], "");
				contents.put(keys[5], "");
				contents.put(keys[6], order.getAmt());
				list.add(contents);
			}
		} else {
			logger.info("在" + dateyyyyMMdd + "内，无T1提现订单");
		}
		return ExcelUtil.createWorkBook(list, keys, columnNames);
	}

}
