package com.cn.flypay.controller.sys;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.sys.PayTypeLimitConfig;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.PayTypeLimitConfigService;
import com.cn.flypay.service.sys.UserService;

@Controller
@RequestMapping("/payTypeLimitConfig")
public class PayTypeLimitConfigController extends BaseController {

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private PayTypeLimitConfigService payTypeLimitConfigService;
	@Autowired
	private UserService userService;
	@Autowired
	private DictionaryService dictionaryService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> payTypeList = dictionaryService.combox("payType");
		request.setAttribute("payType", JSON.toJSONString(payTypeList));
		return "/admin/payTypeLimitConfig";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(PayTypeLimitConfig payTypeLimit, PageFilter ph, HttpSession session) {

		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User operator = userService.get(sessionInfo.getId());
		Grid grid = new Grid();
		grid.setRows(payTypeLimitConfigService.dataGrid(payTypeLimit, ph));
		grid.setTotal(payTypeLimitConfigService.count(payTypeLimit, ph));
		return grid;
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
		List<Dictionary> payTypeList = dictionaryService.combox("payType");
		request.setAttribute("payType", payTypeList);
		
		PayTypeLimitConfig u = payTypeLimitConfigService.get(id);
		request.setAttribute("payTypeLimitConfig", u);
		return "/admin/payTypeLimitConfigEdit";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Json edit(PayTypeLimitConfig payTypeLimitConfig, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			payTypeLimitConfigService.edit(payTypeLimitConfig);
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
