package com.cn.flypay.controller.sys;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.BrokerageConfig;
import com.cn.flypay.service.base.ServiceException;
import com.cn.flypay.service.sys.BrokerageConfigService;
import com.cn.flypay.service.sys.OrganizationService;

@Controller
@RequestMapping("/brokerageConfig")
public class BrokerageConfigController extends BaseController {

	@Autowired
	private BrokerageConfigService brokerageConfigService;

	@Autowired
	private OrganizationService organizationService;

	@RequestMapping("/manager")
	public String manager() {
		return "/admin/brokerageConfig";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(BrokerageConfig bc, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(brokerageConfigService.dataGrid(bc, ph));
		grid.setTotal(brokerageConfigService.count(bc, ph));
		return grid;
	}

	@RequestMapping("/get")
	@ResponseBody
	public BrokerageConfig get(String agentType, Long orgId, String cfgType) {
		return brokerageConfigService.get(agentType, orgId, cfgType);
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long orgId, String agentType, String secAt, String cfgType) {
		BrokerageConfig u = brokerageConfigService.get(agentType, orgId, cfgType);
		request.setAttribute("brokerageConfig", u);
		return "/admin/brokerageConfigEdit";
	}

	@RequestMapping("/edit")
	@ResponseBody
	public Json edit(BrokerageConfig bc) {
		Json j = new Json();
		try {
			brokerageConfigService.edit(bc);
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		} catch (ServiceException e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
