package com.cn.flypay.controller.trans;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.pageModel.trans.UserOrderOperationRecord;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.UserOrderOperationRecordService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

@Controller
@RequestMapping("/userOrderOperationRecord")
public class UserOrderOperationRecordController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private UserOrderOperationRecordService userOrderOperationRecordService;

	@Autowired
	private UserService userService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> statementStatusList = dictionaryService.combox("transType");
		request.setAttribute("orderType", JSON.toJSONString(statementStatusList));
		request.setAttribute("orderTypeObj", statementStatusList);

		List<Dictionary> transTypeList = dictionaryService.combox("orderStatus");
		request.setAttribute("orderStatusObj", transTypeList);
		request.setAttribute("orderStatus", JSON.toJSONString(transTypeList));

		return "/trans/userorderOperationRecord";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(UserOrderOperationRecord uoord, PageFilter ph, HttpSession session) {
		Grid grid = new Grid();
		grid.setRows(userOrderOperationRecordService.dataGrid(uoord, ph));
		grid.setTotal(userOrderOperationRecordService.count(uoord, ph));
		return grid;
	}

}
