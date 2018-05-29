package com.cn.flypay.controller.sys;

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
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.FeedBack;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.FeedBackService;
import com.cn.flypay.service.sys.UserService;

@Controller
@RequestMapping("/feedback")
public class FeedBackController extends BaseController {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private FeedBackService feedbackService;
	@Autowired
	private UserService userService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/admin/feedback";
	}

	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(FeedBack feedback, PageFilter ph, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		feedback.setOperateUser(userService.get(sessionInfo.getId()));
		Grid grid = new Grid();
		grid.setRows(feedbackService.dataGrid(feedback, ph));
		grid.setTotal(feedbackService.count(feedback, ph));
		return grid;
	}
}
