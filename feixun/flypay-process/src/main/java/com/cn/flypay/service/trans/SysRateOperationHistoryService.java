package com.cn.flypay.service.trans;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.SysRateOperationHistory;

/**
 * 平台费率相关操作记录表
 * @author liangchao
 *
 */
public interface SysRateOperationHistoryService {

	/**
	 * 创建记录
	 * @param req
	 * @throws Exception
	 */
	public void createHistory(SysRateOperationHistory req) throws Exception;
	
	/**
	 * 读取数据
	 * @param req
	 * @param ph
	 * @return
	 * @throws Exception
	 */
	public List<SysRateOperationHistory> dataGrid(SysRateOperationHistory req,PageFilter ph) throws Exception;
	
	
	
}
