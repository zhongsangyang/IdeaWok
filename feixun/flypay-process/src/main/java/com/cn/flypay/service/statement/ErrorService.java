package com.cn.flypay.service.statement;

import java.util.List;

import com.cn.flypay.pageModel.account.AccountOrderError;
import com.cn.flypay.pageModel.base.PageFilter;


public interface ErrorService {
	
	public List<AccountOrderError> dataGrid(AccountOrderError accountOrderError, PageFilter ph) throws Exception;

	public Long count(AccountOrderError accountOrderError, PageFilter ph) throws Exception;



}
