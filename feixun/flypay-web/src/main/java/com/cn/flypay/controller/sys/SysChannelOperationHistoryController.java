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
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.trans.SysChannelOperationHistory;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.trans.SysChannelOperationHistoryService;
/**
 * 通道编辑操作记录表
 * @author liangchao
 *
 */
@Controller
@RequestMapping("/sysChannelOperationHistory")
public class SysChannelOperationHistoryController extends BaseController{
	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired 
	private SysChannelOperationHistoryService sysChannelOperationHistoryService;
	
	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		//通道类型  如银联积分、银联扫码等
		List<Dictionary> statementStatusList = dictionaryService.combox("transType");
		request.setAttribute("transType", JSON.toJSONString(statementStatusList));
		request.setAttribute("transTypeObj", statementStatusList);
		//用户类型   所有用户、普通用户、钻石代理、金牌代理、银牌代理
		//支付通道中用于查询选择条件，亲测无用、匹配不上，所以改用channelUserType
		List<Dictionary> userTypeList = dictionaryService.combox("userType");
		request.setAttribute("userTypeObj", userTypeList);
		request.setAttribute("userType", JSON.toJSONString(userTypeList));

		//渠道名称
		List<Dictionary> statementTypeList = dictionaryService.combox("statementType");
		request.setAttribute("statementTypeObj", statementTypeList);
		request.setAttribute("statementType", JSON.toJSONString(statementTypeList));
		//用户类型   所有用户、普通用户、钻石代理、金牌代理、银牌代理   额外有    钻石和金牌会员\钻石和普通会员\金牌和普通会员
		//用于查询结果
		List<Dictionary> channelUserTypeList = dictionaryService.combox("channelUserType");
		request.setAttribute("channelUserType", JSON.toJSONString(channelUserTypeList));
		request.setAttribute("channelUserTypeObj", channelUserTypeList);
		
		
		
		return "/admin/sysChannelOperationHistory";
	}
	
	/**
	 * 根据页面输入的条件，查询通道操作的记录
	 * @param request
	 * @param channel
	 * @param ph
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(SysChannelOperationHistory req,PageFilter ph) {
		Grid grid = new Grid();
		try {
			List<SysChannelOperationHistory> rows = sysChannelOperationHistoryService.dataGrid(req,ph);
			grid.setRows(rows);
			grid.setTotal(Long.valueOf(rows.size()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return grid;
	}
	
	
	
}
