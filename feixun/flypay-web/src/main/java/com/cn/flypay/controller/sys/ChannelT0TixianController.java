package com.cn.flypay.controller.sys;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.ChannelT0Tixian;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.service.payment.RouteService;
import com.cn.flypay.service.sys.ChannelT0TixianService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.DateUtil;

@Controller
@RequestMapping("/channelT0Tixian")
public class ChannelT0TixianController extends BaseController {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private ChannelT0TixianService channelT0TixianService;
	@Autowired
	private RouteService routeService;
	@Autowired
	private UserService userService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> statementStatusList = dictionaryService.combox("statementType");
		request.setAttribute("statementType", JSON.toJSONString(statementStatusList));
		return "/admin/channelT0Tixian";
	}

	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(ChannelT0Tixian channelT0Tixian, PageFilter ph, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		channelT0Tixian.setOperateUser(userService.get(sessionInfo.getId()));
		Grid grid = new Grid();
		grid.setRows(channelT0TixianService.dataGrid(channelT0Tixian, ph));
		grid.setTotal(channelT0TixianService.count(channelT0Tixian, ph));
		return grid;
	}

	@RequestMapping(value = "/reSearch", method = RequestMethod.POST)
	@ResponseBody
	public Json reSearch(Long id, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			ChannelT0Tixian ct = channelT0TixianService.get(id);
			routeService.getChannelPayRouteByChannelName(ct.getChannelName()).sendT0TixianSearch(ct.getOrderNum());
			j.setSuccess(true);
			j.setMsg("已向" + ct.getChannelDetailName() + "发送查询指令！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/exportExcel")
	@ResponseBody
	public ModelAndView exportExcel(HttpServletRequest request, HttpServletResponse response, ChannelT0Tixian t0tixian, HttpSession session) throws Exception {
		try {

			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition", "attachment;fileName=" + DateUtil.convertCurrentDateTimeToString() + ".xls");
			response.setHeader("Content-type", "charset=UTF-8");
			OutputStream ouputStream = response.getOutputStream();

			Workbook wb = channelT0TixianService.export(t0tixian);
			wb.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
