package com.cn.flypay.controller.sys;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.AgentUpgradeFeeCfg;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.service.base.ServiceException;
import com.cn.flypay.service.sys.AgentUpgradeFeeCfgService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.OrganizationService;

@Controller
@RequestMapping("/agentUpgradeFeeCfg")
public class AgentUpgradeFeeCfgController extends BaseController {

	@Autowired
	private AgentUpgradeFeeCfgService agentUpgradeFeeCfgService;

	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private DictionaryService dictionaryService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {

		List<Dictionary> orgAgentLevel = dictionaryService.combox("orgAgentLevel");
		request.setAttribute("orgAgentLevel", JSON.toJSONString(orgAgentLevel));

		return "/admin/agentUpgradeFeeCfg";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(AgentUpgradeFeeCfg bc, PageFilter ph) {
		System.out.println("1111");
		Grid grid = new Grid();
		grid.setRows(agentUpgradeFeeCfgService.dataGrid(bc, ph));
		grid.setTotal(agentUpgradeFeeCfgService.count(bc, ph));
		return grid;
	}

	@RequestMapping("/get")
	@ResponseBody
	public AgentUpgradeFeeCfg get(Integer agentType, Long orgId) {
		return agentUpgradeFeeCfgService.get(agentType, orgId);
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Integer agentType, Long orgId) {
		List<Dictionary> orgAgentLevel = dictionaryService.combox("orgAgentLevel");
		request.setAttribute("orgAgentLevel", orgAgentLevel);

		List<Dictionary> payTypeList = dictionaryService.combox("payType");
		request.setAttribute("payType", payTypeList);

		AgentUpgradeFeeCfg u = agentUpgradeFeeCfgService.get(agentType, orgId);
		request.setAttribute("agentUpgradeFeeCfg", u);
		return "/admin/agentUpgradeFeeCfgEdit";
	}

	@RequestMapping("/edit")
	@ResponseBody
	public Json edit(AgentUpgradeFeeCfg bc) {
		Json j = new Json();
		try {
			agentUpgradeFeeCfgService.edit(bc);
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		} catch (ServiceException e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
