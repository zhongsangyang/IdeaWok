package com.cn.flypay.service.trans;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.SysFreezeHistory;

/**
 * 用户冻结历史操作
 * @author liangchao
 *
 */
public interface SysFreezeHistoryService {
	
	
	/**
	 * 创建记录
	 */
	public void createSysFreezeHistory(SysFreezeHistory req) throws Exception;
	
	
	
	/**
	 * 读取数据
	 * @param req
	 * @param ph
	 * @return
	 */
	public List<SysFreezeHistory> dataGrid(SysFreezeHistory req,PageFilter ph) throws Exception;
	
}
