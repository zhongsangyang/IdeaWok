package com.cn.flypay.service.statement;

import java.util.List;

import com.cn.flypay.model.trans.TorderStatement;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.statement.OrderStatement;

public interface OrderStatementService {

	String saveTorderStatements(List<TorderStatement> sts);

	public List<OrderStatement> dataGrid(OrderStatement statement, PageFilter ph);

	public Long count(OrderStatement statement, PageFilter ph);
}
