package com.cn.flypay.controller.account;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.BrokerageDetail;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.BrokerageDetailService;


@Controller
@RequestMapping("/brokerageDetail")
public class BrokerageDetailController extends BaseController {

	@Autowired
	private BrokerageDetailService brokerageDetailService;
	@Autowired
	private DictionaryService dictionaryService;

	@Autowired
	private UserService userService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> transTypeList = dictionaryService.combox("transType");
		request.setAttribute("transType", JSON.toJSONString(transTypeList));
		return "/trans/brokeragedetail";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(BrokerageDetail app, PageFilter ph, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User user = userService.get(sessionInfo.getId());
		Grid grid = new Grid();
		app.setOperateUser(user);				
		grid.setTotal(brokerageDetailService.count(app, ph));
		grid.setRows(brokerageDetailService.dataGrid(app, ph));		
		return grid;
	}

}
