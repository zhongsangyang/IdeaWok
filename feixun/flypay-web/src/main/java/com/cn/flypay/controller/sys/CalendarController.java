package com.cn.flypay.controller.sys;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.account.Account;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.UserOrderService;

@Controller
@RequestMapping("/calendar")
public class CalendarController extends BaseController {

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private AccountService accountService;
	@Autowired
	private UserOrderService orderService;
	@Autowired
	private UserService userService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/calendar/calendar";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(Account param, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(accountService.dataGrid(param, ph));
		grid.setTotal(accountService.count(param, ph));
		return grid;
	}

}
