package com.cn.flypay.service.account;

import java.util.List;

import com.cn.flypay.pageModel.account.AccountLog;
import com.cn.flypay.pageModel.base.PageFilter;

public interface AccountLogService {

	public List<AccountLog> dataGrid(AccountLog param, PageFilter ph);

	public Long count(AccountLog param, PageFilter ph);

}
