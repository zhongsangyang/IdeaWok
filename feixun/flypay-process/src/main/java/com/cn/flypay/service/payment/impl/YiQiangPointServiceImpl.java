package com.cn.flypay.service.payment.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TYiQiang2MerchantReport;
import com.cn.flypay.model.util.CollectionUtil;
import com.cn.flypay.service.payment.YiQiangPointService;

@Service
public class YiQiangPointServiceImpl implements YiQiangPointService {
	
	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Autowired
	BaseDao<TYiQiang2MerchantReport> yiQiang2MerchantReportDao;
	
	@Override
	public TYiQiang2MerchantReport findByUserId(Long userId) {
		
		String hql = "select m from TYiQiang2MerchantReport m where m.userId=" + userId;
		List<TYiQiang2MerchantReport>  merchantReports = yiQiang2MerchantReportDao.find(hql);
		
		if(CollectionUtil.isNotEmpty(merchantReports)) {
			return merchantReports.get(0);
		}
		
		return null;
	}
	
	
	
	

}
