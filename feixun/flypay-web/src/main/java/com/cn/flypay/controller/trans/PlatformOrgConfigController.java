package com.cn.flypay.controller.trans;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.account.PlatformOrgConfig;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.PlatformOrgConfigService;

@Controller
@RequestMapping("/platformOrgConfig")
public class PlatformOrgConfigController extends BaseController {

	@Autowired
	private PlatformOrgConfigService platformOrgConfigService;
	@Autowired
	private DictionaryService dictionaryService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> transTypeList = dictionaryService.combox("orderStatus");
		request.setAttribute("orderStatus", JSON.toJSONString(transTypeList));
		return "/admin/platformOrgConfig";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(PlatformOrgConfig poc, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(platformOrgConfigService.dataGrid(poc, ph));
		grid.setTotal(platformOrgConfigService.count(poc, ph));
		return grid;
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
		PlatformOrgConfig pof = platformOrgConfigService.get(id);
		request.setAttribute("platformOrgConfig", pof);
		return "/admin/platformOrgConfigEdit";
	}

	@RequestMapping("/edit")
	@ResponseBody
	public Json add(PlatformOrgConfig param, HttpServletRequest request) {
		SessionInfo o = (SessionInfo) request.getSession().getAttribute(GlobalConstant.SESSION_INFO);
		Json j = new Json();
		try {
			platformOrgConfigService.edit(param);
			j.setSuccess(true);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
