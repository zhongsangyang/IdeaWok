package com.cn.flypay.controller.sys;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.ServiceMerchantService;

@Controller
@RequestMapping("/serviceMerchant")
public class ServiceMerchantController extends BaseController {

	@Autowired
	private ServiceMerchantService serviceMerchantService;
	@Autowired
	private DictionaryService dictionaryService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> statementTypeList = dictionaryService.combox("statementType");
		request.setAttribute("statementType", JSON.toJSONString(statementTypeList));

		return "/admin/serviceMerchant";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(ServiceMerchant poc, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(serviceMerchantService.dataGrid(poc, ph));
		grid.setTotal(serviceMerchantService.count(poc, ph));
		return grid;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Json edit(Long id, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			serviceMerchantService.editServiceMerchantStatus(id);
			j.setSuccess(true);
			j.setMsg("设置成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
