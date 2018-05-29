package com.cn.flypay.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TuserMerchantReport;
import com.cn.flypay.service.sys.UserMerchantReportService;

@Service
public class UserMerchantReportServiceImpl implements UserMerchantReportService {

	@Autowired
	private BaseDao<TuserMerchantReport> userMerchantReportDao;

	@Override
	public TuserMerchantReport get(Long userId) {
		String reportHql = "select r from TuserMerchantReport r where r.userId=" + userId;
		TuserMerchantReport report = userMerchantReportDao.get(reportHql);
		return report;
	}
}
