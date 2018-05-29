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
import com.cn.flypay.pageModel.sys.ChannelDayAmt;
import com.cn.flypay.pageModel.trans.SysFreezeHistory;
import com.cn.flypay.service.trans.SysFreezeHistoryService;
/**
 * 平台中冻结/解冻的历史记录相关操作
 * @author liangchao
 *
 */
@Controller
@RequestMapping("/sysFreezeHistor")
public class SysFreezeHistoryController extends BaseController {
	private Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private SysFreezeHistoryService sysFreezeHistoryService;
	
	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/admin/sysFreezeHistory";
	}
	
	
	
	/**
	 * 根据页面输入的条件，查询冻结/解冻的记录参数
	 * @param request
	 * @param channel
	 * @param ph
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(SysFreezeHistory req,PageFilter ph) {
		Grid grid = new Grid();
		try {
			List<SysFreezeHistory> rows = sysFreezeHistoryService.dataGrid(req,ph);
			grid.setRows(rows);
			grid.setTotal(Long.valueOf(rows.size()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return grid;
	}
	
	
	
	
	
}
