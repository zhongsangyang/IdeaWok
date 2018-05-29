package com.cn.flypay.controller.account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.account.AccountPointHistory;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.account.AccountPointHistoryService;
import com.cn.flypay.service.sys.UserService;

@Controller
@RequestMapping("/pointlog")
public class PointLogController extends BaseController {

	private Log logger = LogFactory.getLog(getClass());

	@Autowired
	private AccountPointHistoryService pointHistoryService;
	@Autowired
	private UserService userService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/account/pointlog";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(AccountPointHistory accPointLog, PageFilter ph, HttpSession session) {

		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User user = userService.get(sessionInfo.getId());
		accPointLog.setOperateUser(user);

		Grid grid = new Grid();
		grid.setRows(pointHistoryService.dataGrid(accPointLog, ph));
		grid.setTotal(pointHistoryService.count(accPointLog, ph));
		return grid;
	}

}
