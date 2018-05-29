package com.cn.flypay.controller.trans;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.trans.AppTransInfo;
import com.cn.flypay.service.sys.DictionaryService;

@Controller
@RequestMapping("/appTransinfo")
public class AppTransinfoController extends BaseController {


	@Autowired
	private DictionaryService dictionaryService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> statementStatusList = dictionaryService.combox("statementStatus");
		request.setAttribute("statementStatus", JSON.toJSONString(statementStatusList));
		request.setAttribute("statementStatusObj", statementStatusList);
		List<Dictionary> transTypeList = dictionaryService.combox("transType");
		request.setAttribute("transType", JSON.toJSONString(transTypeList));
		request.setAttribute("transTypeObj", transTypeList);
		return "/trans/apptransinfo";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(AppTransInfo app, PageFilter ph) {
		Grid grid = new Grid();
//		grid.setRows(appTransInfoService.dataGrid(app, ph));
//		grid.setTotal(appTransInfoService.count(app, ph));
		return grid;
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
//		AppTransInfo u = appTransInfoService.get(id);
//		request.setAttribute("appTransInfo", u);
		return "/trans/apptransinfoEdit";
	}
}
