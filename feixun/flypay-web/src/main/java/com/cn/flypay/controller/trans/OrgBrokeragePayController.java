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
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.trans.OrgBrokeragePay;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.trans.OrgBrokeragePayService;

@Controller
@RequestMapping("/orgBrokeragePay")
public class OrgBrokeragePayController extends BaseController {

	@Autowired
	private OrgBrokeragePayService orgBrokeragePayService;
	@Autowired
	private DictionaryService dictionaryService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> transTypeList = dictionaryService.combox("orderStatus");
		request.setAttribute("orderStatus", JSON.toJSONString(transTypeList));
		return "/trans/orgbrokeragepay";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(OrgBrokeragePay app, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(orgBrokeragePayService.dataGrid(app, ph));
		grid.setTotal(orgBrokeragePayService.count(app, ph));
		return grid;
	}
	@RequestMapping("/addPage")
	public String addPage(HttpServletRequest request, String type) {
		return "/trans/orgbrokeragepayAdd";
	}

	@RequestMapping("/add")
	@ResponseBody
	public Json add(OrgBrokeragePay param, HttpServletRequest request) {
		SessionInfo o = (SessionInfo) request.getSession().getAttribute(GlobalConstant.SESSION_INFO);
		Json j = new Json();
		try {
			String excetionStr = orgBrokeragePayService.add(param,o.getId());
			if (excetionStr!=null) {
				j.setMsg(excetionStr);
			}else{
				j.setSuccess(true);
				j.setMsg("订单提现成功！");
			}
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
