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
import com.cn.flypay.pageModel.sys.AgentSettlementRateCfg;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.service.base.ServiceException;
import com.cn.flypay.service.sys.AgentSettlementRateConfigService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.OrganizationService;

@Controller
@RequestMapping("/agentSettlementRate")
public class AgentSettlementRateConfigController extends BaseController {

	@Autowired
	private AgentSettlementRateConfigService agentSettlementRateConfigService;

	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private DictionaryService dictionaryService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {

		List<Dictionary> orgAgentLevel = dictionaryService.combox("orgAgentLevel");
		request.setAttribute("orgAgentLevel", JSON.toJSONString(orgAgentLevel));

		List<Dictionary> payTypeList = dictionaryService.combox("payType");
		request.setAttribute("payType", JSON.toJSONString(payTypeList));
		return "/admin/agentSettlementRateConfig";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(AgentSettlementRateCfg bc, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(agentSettlementRateConfigService.dataGrid(bc, ph));
		grid.setTotal(agentSettlementRateConfigService.count(bc, ph));
		return grid;
	}

	@RequestMapping("/get")
	@ResponseBody
	public AgentSettlementRateCfg get(Integer agentType, Long orgId, Integer payType) {
		return agentSettlementRateConfigService.get(agentType, orgId, payType);
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Integer agentType, Long orgId, Integer payType) {
		List<Dictionary> orgAgentLevel = dictionaryService.combox("orgAgentLevel");
		request.setAttribute("orgAgentLevel", orgAgentLevel);

		List<Dictionary> payTypeList = dictionaryService.combox("payType");
		request.setAttribute("payType", payTypeList);
		
		AgentSettlementRateCfg u = agentSettlementRateConfigService.get(agentType, orgId, payType);
		request.setAttribute("agentSettlementRateConfig", u);
		return "/admin/agentSettlementRateConfigEdit";
	}

	@RequestMapping("/edit")
	@ResponseBody
	public Json edit(AgentSettlementRateCfg bc) {
		Json j = new Json();
		try {
			agentSettlementRateConfigService.edit(bc);
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		} catch (ServiceException e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
