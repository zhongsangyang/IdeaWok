package com.cn.flypay.service.trans;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.SysUserRelateFreezeHistory;

/**
 * 用户相关冻结记录
 * @author liangchao
 *
 */
public interface SysUserRelateFreezeHistoryService {
	
	
	/**
	 * 创建记录
	 */
	public void createHistory(SysUserRelateFreezeHistory req) throws Exception;
	
	
	
	/**
	 * 读取数据
	 * @param req
	 * @param ph
	 * @return
	 */
	public List<SysUserRelateFreezeHistory> dataGrid(SysUserRelateFreezeHistory req,PageFilter ph) throws Exception;
	
}
