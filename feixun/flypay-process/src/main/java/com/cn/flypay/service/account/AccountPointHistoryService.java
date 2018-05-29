package com.cn.flypay.service.account;

import java.util.List;

import com.cn.flypay.pageModel.account.AccountPointHistory;
import com.cn.flypay.pageModel.base.PageFilter;

public interface AccountPointHistoryService {

	public List<AccountPointHistory> dataGrid(AccountPointHistory param, PageFilter ph);

	public Long count(AccountPointHistory param, PageFilter ph);

	public void addPointHistory(Long userId, String cdType, Long addPoint, String desc);

}
