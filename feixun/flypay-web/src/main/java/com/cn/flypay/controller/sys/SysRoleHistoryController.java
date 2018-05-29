package com.cn.flypay.controller.sys;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.SysRoleHistory;
import com.cn.flypay.service.trans.SysRoleHistoryService;
/**
 * 用户角色权限分配记录
 * @author liangchao
 *
 */
@Controller
@RequestMapping("/sysRoleHistory")
public class SysRoleHistoryController extends BaseController{
	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private SysRoleHistoryService sysRoleHistoryService; 
	
	@RequestMapping("/manager")
	public String manager(HttpServletRequest request){
		return "/admin/sysRoleHistory";
	}
	
	/**
	 * 根据页面输入的条件，查询通道操作的记录
	 * @param request
	 * @param channel
	 * @param ph
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(SysRoleHistory req,PageFilter ph){
		Grid grid = new Grid();
		try {
			List<SysRoleHistory> rows = sysRoleHistoryService.dataGrid(req,ph);
			grid.setRows(rows);
			grid.setTotal(Long.valueOf(rows.size()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return grid;
		
	}
	
	
}
