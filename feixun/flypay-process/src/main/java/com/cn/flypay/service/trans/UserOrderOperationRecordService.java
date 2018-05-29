package com.cn.flypay.service.trans;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.UserOrderOperationRecord;

public interface UserOrderOperationRecordService {

	public List<UserOrderOperationRecord> dataGrid(UserOrderOperationRecord userOrderOperationRecord, PageFilter pf);

	public Long count(UserOrderOperationRecord userOrderOperationRecord, PageFilter pf);

	public UserOrderOperationRecord get(Long id);
	
}
