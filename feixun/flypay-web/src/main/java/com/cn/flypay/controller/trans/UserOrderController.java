package com.cn.flypay.controller.trans;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import com.cn.flypay.model.util.CollectionUtil;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

@Controller
@RequestMapping("/userOrder")
public class UserOrderController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private UserOrderService userOrderService;

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

		List<Dictionary> bigTranTypeList = dictionaryService.combox("bigTranType");
		request.setAttribute("bigTranType", JSON.toJSONString(bigTranTypeList));
		return "/trans/userorder";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(UserOrder userOrder, PageFilter ph, HttpSession session) {

		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User user = userService.get(sessionInfo.getId());
		userOrder.setOperateUser(user);

		Grid grid = new Grid();
		grid.setRows(userOrderService.dataGrid(userOrder, ph));
		grid.setTotal(userOrderService.count(userOrder, ph));
		return grid;
	}

	@RequestMapping("/agentManager")
	public String agentManager(HttpServletRequest request) {
		List<Dictionary> statementStatusList = dictionaryService.combox("transType");
		request.setAttribute("orderType", JSON.toJSONString(statementStatusList));
		request.setAttribute("orderTypeObj", statementStatusList);

		List<Dictionary> transTypeList = dictionaryService.combox("orderStatus");
		request.setAttribute("orderStatusObj", transTypeList);
		request.setAttribute("orderStatus", JSON.toJSONString(transTypeList));

		List<Dictionary> bigTranTypeList = dictionaryService.combox("bigTranType");
		request.setAttribute("bigTranType", JSON.toJSONString(bigTranTypeList));
		if (userService.getUserRole()) {
			request.setAttribute("isEdit", "1");
		} else {
			request.setAttribute("isEdit", "2");
		}
		return "/trans/agentUserOrder";
	}

	@RequestMapping("/manualOrderSearchManager")
	public String manualOrderSearchManager(HttpServletRequest request) {
		List<Dictionary> statementStatusList = dictionaryService.combox("orderType");
		request.setAttribute("orderType", JSON.toJSONString(statementStatusList));
		List<Dictionary> transTypeList = dictionaryService.combox("orderStatus");
		request.setAttribute("orderStatus", JSON.toJSONString(transTypeList));
		return "/trans/manualOrderManager";
	}

	@RequestMapping("/affirmOrderStatus")
	@ResponseBody
	public Json affirmOrderStatus(Long id, Integer status, HttpSession session) {
		System.out.println("订单确认");
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			User user = userService.get(sessionInfo.getId());
			String returnFlag = userOrderService.affirmOrderStatus(id, status == 0 ? false : true, user);
			if (GlobalConstant.RESP_CODE_SUCCESS.equals(returnFlag)) {
				j.setMsg("确认成功！");
				j.setSuccess(true);
				String operationName = status == 0 ? "确认失败" : "确认成功";
				UserOrder userOrder = new UserOrder();
				userOrder.setId(id);
				userOrderService.createTransOrderOperationRecordByUserOrder(userOrder, user, operationName);
			} else {
				j.setMsg(returnFlag);
				j.setSuccess(false);
			}
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/reSearchOrder")
	@ResponseBody
	public Json reSearchOrder(String orderNum, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			User user = userService.get(sessionInfo.getId());
			logger.info(user.getLoginName() + "--" + user.getRealName() + "重新查询了订单" + orderNum);
			Map<String, String> searchMap = userOrderService.dealReSentSearchOrder(orderNum);
			if (searchMap == null) {
				j.setMsg("查询结束");
			} else {
				if (CollectionUtil.isEmpty(searchMap.keySet())) {
					j.setMsg("查询完成");
				} else {
					j.setMsg(searchMap.get("retMsg"));
				}
			}
			j.setSuccess(true);
			UserOrder userOrder = new UserOrder();
			userOrder.setOrderNum(orderNum);
			userOrderService.createTransOrderOperationRecordByUserOrder(userOrder, user, "重新查询");
		} catch (Exception e) {
			logger.error("重新查询订单状态出错", e);
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/exportExcel")
	@ResponseBody
	public ModelAndView exportExcel(HttpServletRequest request, UserOrder userOrder, HttpServletResponse response,
			HttpSession session) throws Exception {
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			User user = userService.get(sessionInfo.getId());
			System.out.println("user: " + user);

			userOrder.setOperateUser(user);
			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition",
					"attachment;fileName=" + DateUtil.convertCurrentDateTimeToString() + ".xls");
			response.setHeader("Content-type", "charset=UTF-8");
			OutputStream ouputStream = response.getOutputStream();

			if (StringUtil.isNotBlank(userOrder.getPayDatetimeStart())) {
				// userOrder.setCreateDatetimeStart(DateUtil.getDateFromString(userOrder.getPayDatetimeStart()));
				// userOrder.setPayDatetimeStart(null);
			} else {
				userOrder.setCreateDatetimeStart(DateUtil.getStartOfDay(new Date()));
			}
			if (StringUtil.isNotBlank(userOrder.getPayDatetimeEnd())) {
				// userOrder.setCreateDatetimeEnd(DateUtil.getDateFromString(userOrder.getPayDatetimeEnd()));
				// userOrder.setPayDatetimeEnd(null);
			} else {
				userOrder.setCreateDatetimeEnd(DateUtil.getEndOfDay(new Date()));
			}
			Workbook wb = userOrderService.exportExcel(userOrder);
			wb.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("下载失败", e);
		}
		return null;
	}

}
