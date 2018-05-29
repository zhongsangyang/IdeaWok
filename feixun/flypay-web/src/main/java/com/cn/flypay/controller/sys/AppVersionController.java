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

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.AppVersion;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.service.sys.AppVersionService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.UserService;

@Controller
@RequestMapping("/appversion")
public class AppVersionController extends BaseController {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private AppVersionService appVersionService;

	@Autowired
	private UserService userService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		System.out.println(request.getAttributeNames());
		return "/admin/appVersion";
	}

	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(AppVersion infoList, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(appVersionService.dataGrid(infoList, ph));
		grid.setTotal(appVersionService.count(infoList, ph));
		return grid;
	}

	@RequestMapping("/addPage")
	public String addPage(HttpServletRequest request) {
		return "/admin/appVersionAdd";
	}

	@RequestMapping("/add")
	@ResponseBody
	public Json add(AppVersion appVersion, HttpSession session) {
		Json j = new Json();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		try {
			appVersion.setCreator(userService.get(sessionInfo.getId()).getLoginName());
			appVersionService.add(appVersion);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/delete")
	@ResponseBody
	public Json delete(Long id, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			appVersionService.delete(id);
			j.setMsg("停用成功！");
			j.setSuccess(true);
			log.info("用户ID=" + sessionInfo.getId() + "停用系统通道：ID=" + id);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
		List<Dictionary> transTypeList = dictionaryService.combox("transType");
		request.setAttribute("transTypeObj", transTypeList);
		AppVersion u = appVersionService.get(id);
		request.setAttribute("appversion", u);
		return "/admin/appVersionEdit";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Json edit(AppVersion infoList, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			appVersionService.edit(infoList);
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
