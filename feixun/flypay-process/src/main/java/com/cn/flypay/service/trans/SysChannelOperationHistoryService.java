package com.cn.flypay.service.trans;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.SysChannelOperationHistory;
/**
 * 通道编辑操作记录表
 * @author liangchao
 *
 */
public interface SysChannelOperationHistoryService {
	/**
	 * 创建记录
	 */
	public void createHistory(SysChannelOperationHistory req) throws Exception;
	/**
	 * 读取数据
	 * @param req
	 * @param ph
	 * @return
	 */
	public List<SysChannelOperationHistory> dataGrid(SysChannelOperationHistory req,PageFilter ph) throws Exception;

}
