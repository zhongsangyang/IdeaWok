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
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.SysCreateTestData;
import com.cn.flypay.service.trans.SysCreateTestDataService;
/**
 * 为王美凤造数据
 * @author liangchao
 *
 */
@Controller
@RequestMapping("/sysCreateTestData")
public class SysCreateTestDataController  extends BaseController{
	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private SysCreateTestDataService sysCreateTestDataService;
	
	
	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/admin/sysCreateTestData";
	}
	
	@RequestMapping("/addPage")
	public String addPage(HttpServletRequest request) {
		return "/admin/sysCreateTestDataAdd";
	}
	
	@RequestMapping("/add")
	@ResponseBody
	public Json add(SysCreateTestData req) {
		Json j = new Json();
		try {
			sysCreateTestDataService.create(req);
			
			j.setSuccess(true);
			j.setMsg("添加成功！");
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
		}
		return j;
	}

	
	
	
	
	/**
	 * 根据页面输入的条件，查询
	 * @param request
	 * @param channel
	 * @param ph
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(SysCreateTestData req,PageFilter ph) {
		Grid grid = new Grid();
		try {
			List<SysCreateTestData> rows = sysCreateTestDataService.dataGrid(req,ph);
			grid.setRows(rows);
			grid.setTotal(Long.valueOf(rows.size()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return grid;
	}
	
	
	
}
