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
import com.cn.flypay.pageModel.sys.SysMsgHistory;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.sys.HolidayService;
import com.cn.flypay.service.sys.MsgHistoryService;
import com.cn.flypay.service.sys.UserService;

@Controller
@RequestMapping("/message")
public class MsgHistoryController extends BaseController {

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private MsgHistoryService msgHistoryService;
	@Autowired
	private UserService userService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/admin/msgHistory";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(SysMsgHistory msgHistory, PageFilter ph, HttpSession session) {

		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User operator = userService.get(sessionInfo.getId());
		msgHistory.setAgentId(operator.getAgentId());
		Grid grid = new Grid();
		grid.setRows(msgHistoryService.dataGrid(msgHistory, ph));
		grid.setTotal(msgHistoryService.count(msgHistory, ph));
		return grid;
	}
}
