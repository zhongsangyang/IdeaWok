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
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.payment.PingAnExpenseService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.trans.UserOrderService;

@Controller
@RequestMapping("/userExceptionOrder")
public class UserExceptionOrderController extends BaseController {

	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private UserOrderService userOrderService;
	@Autowired
	private PingAnExpenseService pingAnExpenseService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> statementStatusList = dictionaryService.combox("transType");
		request.setAttribute("transType", JSON.toJSONString(statementStatusList));
		List<Dictionary> transTypeList = dictionaryService.combox("orderStatus");
		request.setAttribute("orderStatus", JSON.toJSONString(transTypeList));
		return "/trans/userExceptionOrder";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(UserOrder userOrder, PageFilter ph) {
		Grid grid = new Grid();
		userOrder.setStatus(UserOrder.order_status.MANUAL_PROCESSING.getCode());
		List<UserOrder> uos = userOrderService.dataGrid(userOrder, ph);
		grid.setRows(uos);
		grid.setTotal(userOrderService.count(userOrder, ph));
		return grid;
	}

	@RequestMapping("/resentOrderToPingan")
	@ResponseBody
	public Json resentOrderToPingan(Long id) {
		Json j = new Json();
		try {
			pingAnExpenseService.sendSearchOrderToPingAN(id);
			j.setMsg("已重新成功发送查询请求");
			j.setSuccess(true);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
