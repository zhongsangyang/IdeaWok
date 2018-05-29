package com.cn.flypay.controller.sys;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.SysChannelOperationHistory;
import com.cn.flypay.service.payment.RouteService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.SysChannelOperationHistoryService;

@Controller
@RequestMapping("/channel")
public class ChannelController extends BaseController {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private UserService userService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private RouteService routeService;
	@Autowired
	private SysChannelOperationHistoryService sysChannelOperationHistoryService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> statementStatusList = dictionaryService.combox("transType");
		request.setAttribute("transType", JSON.toJSONString(statementStatusList));
		request.setAttribute("transTypeObj", statementStatusList);

		List<Dictionary> userTypeList = dictionaryService.combox("userType");
		request.setAttribute("userTypeObj", userTypeList);

		List<Dictionary> statementTypeList = dictionaryService.combox("statementType");
		request.setAttribute("statementTypeObj", statementTypeList);
		request.setAttribute("statementType", JSON.toJSONString(statementTypeList));

		List<Dictionary> channelUserTypeList = dictionaryService.combox("channelUserType");
		request.setAttribute("channelUserType", JSON.toJSONString(channelUserTypeList));
		return "/admin/channel";
	}

	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(HttpServletRequest request, Channel channel, PageFilter ph) {
		Grid grid = new Grid();
		ph.setSort("todayAmt");
		ph.setOrder("desc");
		channel.setChannelType(1);
		grid.setRows(channelService.dataGrid(channel, ph));
		grid.setTotal(channelService.count(channel, ph));
		return grid;
	}

	@RequestMapping("/addPage")
	public String addPage(HttpServletRequest request) {
		List<Dictionary> transTypeList = dictionaryService.combox("transType");
		request.setAttribute("transTypeObj", transTypeList);
		List<Dictionary> statementTypeList = dictionaryService.combox("statementType");
		request.setAttribute("statementTypeObj", statementTypeList);

		List<Dictionary> channelUserTypeList = dictionaryService.combox("channelUserType");
		request.setAttribute("channelUserTypeObj", channelUserTypeList);

		return "/admin/channelAdd";
	}

	@RequestMapping("/add")
	@ResponseBody
	public Json add(Channel channel, HttpSession session) {
		Json j = new Json();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		try {
			channelService.add(channel);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/delete")
	@ResponseBody
	public Json delete(Long id, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			channelService.delete(id);
			j.setMsg("停用成功！");
			j.setSuccess(true);
			log.info("用户ID=" + sessionInfo.getId() + "停用系统通道：ID=" + id);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id, Integer prmType, Integer prmVersion) {
		List<Dictionary> transTypeList = dictionaryService.combox("transType");
		request.setAttribute("transTypeObj", transTypeList);
		List<Dictionary> statementTypeList = dictionaryService.combox("statementType");
		request.setAttribute("statementTypeObj", statementTypeList);

		List<Dictionary> channelUserTypeList = dictionaryService.combox("channelUserType");
		request.setAttribute("channelUserTypeObj", channelUserTypeList);
		Channel u = channelService.get(id);
		request.setAttribute("channel", u);
		return "/admin/channelEdit";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Json edit(Channel param, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			channelService.edit(param);
			j.setSuccess(true);
			j.setMsg("编辑成功！");
			
			//开始保存修改记录
			SysChannelOperationHistory record = new SysChannelOperationHistory();
			record.setCreatorId(sessionInfo.getId());
			record.setTargetId(param.getId());
			record.setRecordType(1);   		//记录类型为1 支付通道
			record.setBehaviorType(1); 		//行为类型为1编辑
			record.setDetails("用户ID=" + sessionInfo.getId() + "编辑" + "sys_channel 表中ID=" + param.getId() + " 的通道，记录来源：管理平台--通道管理--支付通道");
			sysChannelOperationHistoryService.createHistory(record);
			
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping(value = "/channelT0Tixian", method = RequestMethod.POST)
	@ResponseBody
	public Json channelT0Tixian(Long id, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			User user = userService.get(sessionInfo.getId());
			Map<String, String> params = new HashMap<String, String>();
			params.put("operator_id", String.valueOf(user.getId()));
			params.put("operator_name", user.getRealName());
			Tchannel tl = channelService.getTchannelInCache(id);
			routeService.getChannelPayRouteByChannelName(tl.getName()).dealChannelT0Tixian(id, params);
			j.setSuccess(true);
			j.setMsg("发送成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping(value = "/deleteChannel", method = RequestMethod.POST)
	@ResponseBody
	public Json deleteChannel(@RequestParam(value = "params[]") Long[] params) {
		Json j = new Json();
		try {
			Channel channel = null;
			List<Long> list = Arrays.asList(params);
			for (Long id : list) {
				channel = channelService.get(id);
				channel.setId(id);
				channel.setStatus(10);
				channelService.edit(channel);
			}
			j.setSuccess(true);
			j.setMsg("删除成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
