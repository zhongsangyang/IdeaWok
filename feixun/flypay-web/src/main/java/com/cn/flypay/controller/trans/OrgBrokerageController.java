package com.cn.flypay.controller.trans;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.OrgBrokerage;
import com.cn.flypay.service.trans.OrgBrokerageService;

@Controller
@RequestMapping("/orgBrokerage")
public class OrgBrokerageController extends BaseController {

	@Autowired
	private OrgBrokerageService orgBrokerageService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/trans/orgbrokerage";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(OrgBrokerage app, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(orgBrokerageService.dataGrid(app, ph));
		grid.setTotal(orgBrokerageService.count(app, ph));
		return grid;
	}

}
