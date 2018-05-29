package com.cn.flypay.service.statement.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.TaccountOrderError;
import com.cn.flypay.pageModel.account.AccountOrderError;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.service.statement.ErrorService;
import com.cn.flypay.utils.DateUtil;



@Service
public class ErrorServiceImpl implements ErrorService {
	
	@Autowired
	private BaseDao<TaccountOrderError> errorDao;
	

	@Override
	public List<AccountOrderError> dataGrid(AccountOrderError accountOrderError, PageFilter ph) throws Exception {
		List<AccountOrderError> er = new ArrayList<AccountOrderError>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TaccountOrderError t where t.createTime";
		if(accountOrderError.getCreateTime()!=null){
			hql = "select t from TaccountOrderError t where t.createTime BETWEEN :startDate and :endDate";
			Date startDate = DateUtil.getBeforeDate(DateUtil.getDateFromString(accountOrderError.getCreateTime()), 0);
			Date endDate = DateUtil.getDatebyInterval(startDate, 1);
			params.put("startDate", startDate);
			params.put("endDate", endDate);
		}
		List<TaccountOrderError> errorlist = errorDao.find(hql, params);
		for (TaccountOrderError taccountOrderError : errorlist) {
			AccountOrderError a = new AccountOrderError();
			BeanUtils.copyProperties(taccountOrderError, a);
			er.add(a);
		}
		return er;
	}

	
	@Override
	public Long count(AccountOrderError accountOrderError, PageFilter ph) throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select count(t.id) from TaccountOrderError t where t.createTime";
		if(accountOrderError.getCreateTime()!=null){
			Date startDate = DateUtil.getBeforeDate(DateUtil.getDateFromString(accountOrderError.getCreateTime()), 0);
			Date endDate = DateUtil.getDatebyInterval(startDate, 1);
			hql = "select count(t.id) from TaccountOrderError t where t.createTime BETWEEN :startDate and :endDate";
			params.put("startDate", startDate);
			params.put("endDate", endDate);
		}
		return errorDao.count(hql, params);
	}

}
