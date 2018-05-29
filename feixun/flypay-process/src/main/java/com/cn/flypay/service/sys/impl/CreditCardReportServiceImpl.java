package com.cn.flypay.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TcreditCardReport;
import com.cn.flypay.service.sys.CreditCardReportService;

@Service
public class CreditCardReportServiceImpl implements CreditCardReportService {

	@Autowired
	private BaseDao<TcreditCardReport> creditCardReportDao;

	@Override
	public void saveOrUpdate(TcreditCardReport cardReport) {
		creditCardReportDao.saveOrUpdate(cardReport);
	}

	@Override
	public TcreditCardReport get(String orderNo) {
		String hql = "select r from TcreditCardReport r where r.orderNo='" + orderNo + "'";
		TcreditCardReport report = creditCardReportDao.get(hql);
		return report;
	}

}
