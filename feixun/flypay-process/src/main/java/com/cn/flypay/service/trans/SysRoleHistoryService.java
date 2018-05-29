package com.cn.flypay.service.trans;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.SysRoleHistory;
/**
 * 用户角色权限分配记录
 * @author liangchao
 *
 */
public interface SysRoleHistoryService {
	
	/**
	 * 创建记录
	 * @param req
	 * @throws Exception
	 */
	public void createHistory(SysRoleHistory req) throws Exception;
	
	/**
	 * 读取数据
	 * @param req
	 * @param ph
	 * @return
	 * @throws Exception
	 */
	public List<SysRoleHistory> dataGrid(SysRoleHistory req,PageFilter ph) throws Exception;
	
	
}
