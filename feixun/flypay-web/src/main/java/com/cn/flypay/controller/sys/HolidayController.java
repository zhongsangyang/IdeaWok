package com.cn.flypay.controller.sys;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Holiday;
import com.cn.flypay.service.sys.HolidayService;

@Controller
@RequestMapping("/holiday")
public class HolidayController extends BaseController {

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private HolidayService holidayService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/admin/holiday";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(Holiday holiday, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(holidayService.dataGrid(holiday, ph));
		grid.setTotal(holidayService.count(holiday, ph));
		return grid;
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
		Holiday u = holidayService.get(id);
		request.setAttribute("holidayService", u);
		return "/admin/holidayEdit";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Json edit(Long id, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			holidayService.editHoliday(id);
			j.setSuccess(true);
			j.setMsg("设置成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
