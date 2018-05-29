package com.cn.flypay.controller.sys;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.ChannelDayAmt;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.service.sys.ChannelDayAmtMonitorService;
import com.cn.flypay.service.sys.DictionaryService;

@Controller
@RequestMapping("/channelDayAmtMonitorController")
public class ChannelDayAmtMonitorController extends BaseController{
	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private ChannelDayAmtMonitorService channelDayAmtMonitorService;
	
	
	/**
	 * 访问通道每日流入金额统计页面指定
	 * @param request
	 * @return
	 */
	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<Dictionary> statementStatusList = dictionaryService.combox("transType");
		//支付类型	对应sys_channel type字段    用于js匹配
		request.setAttribute("transType", JSON.toJSONString(statementStatusList));
		//通道类型  	对应sys_channel type字段    用于选择框
		request.setAttribute("transTypeObj", statementStatusList);

		//用户类型   对应sys_channel user_Type字段  用于选择框
		List<Dictionary> userTypeList = dictionaryService.combox("userType");			//用户类型
		request.setAttribute("userTypeObj", userTypeList);
		
		//渠道名称  对应sys_channel name字段   用于Js判断
		List<Dictionary> statementTypeList = dictionaryService.combox("statementType");
		request.setAttribute("statementType", JSON.toJSONString(statementTypeList));
		
		//渠道名称  对应sys_channel name字段   用于选择框
		request.setAttribute("statementTypeObj", statementTypeList);
		
		//用户类型  对应sys_channel  user_Type  字段  用于JS判断
		List<Dictionary> channelUserTypeList = dictionaryService.combox("channelUserType");		//channelUserType  通道与用户类型
		request.setAttribute("channelUserType", JSON.toJSONString(channelUserTypeList));
		return "/admin/channelDayAmtMonitor";
	}
	
	/**
	 * 根据页面输入的条件，查询各通道每日录入金额数据
	 * @param request
	 * @param channel
	 * @param ph
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(ChannelDayAmt channelDayAmt, PageFilter pf) {
		Grid grid = new Grid();
		List<ChannelDayAmt> rows = channelDayAmtMonitorService.dataGrid(channelDayAmt,pf);
		grid.setRows(rows);
		grid.setTotal(Long.valueOf(rows.size()));
		return grid;
	}
}
