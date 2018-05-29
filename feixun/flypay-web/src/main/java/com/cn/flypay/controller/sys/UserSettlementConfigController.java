package com.cn.flypay.controller.sys;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;
import com.cn.flypay.pageModel.trans.SysRateOperationHistory;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.service.trans.SysRateOperationHistoryService;

@Controller
@RequestMapping("/userSettlementConfig")
public class UserSettlementConfigController extends BaseController {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private UserSettlementConfigService userSettlementConfigService;
	@Autowired
	private UserService userService;
	@Autowired
	private SysRateOperationHistoryService sysRateOperationHistoryService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/admin/userSettlementConfig";
	}

	@RequestMapping("/agentManager")
	public String agentManager(HttpServletRequest request) {
		return "/admin/agentUserSettlementConfig";
	}

	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(UserSettlementConfig usc, PageFilter ph, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		usc.setOperateUser(userService.get(sessionInfo.getId()));
		Grid grid = new Grid();
		grid.setRows(userSettlementConfigService.dataGrid(usc, ph));
		grid.setTotal(userSettlementConfigService.count(usc, ph));
		return grid;
	}

	@RequestMapping("/agentEditPage")
	public String agentEditPage(HttpServletRequest request, Long id) {
		UserSettlementConfig u = userSettlementConfigService.get(id);
		request.setAttribute("userSettlementConfig", u);
		return "/admin/agentUserSettlementConfigEdit";
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
		UserSettlementConfig u = userSettlementConfigService.get(id);
		request.setAttribute("userSettlementConfig", u);
		return "/admin/userSettlementConfigEdit";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Json edit(UserSettlementConfig userSettlementConfig, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			User user = userService.get(sessionInfo.getId());
			log.info("费率操作" + user.getLoginName() + " 调整了费率" + userSettlementConfig.getId());
			userSettlementConfigService.edit(userSettlementConfig);
			j.setSuccess(true);
			j.setMsg("编辑成功！");
			
			//开始保存记录
			SysRateOperationHistory req = new SysRateOperationHistory();
			req.setCreatorId(sessionInfo.getId());
			req.setRecordType(1);	//1 用户费率编辑
			req.setBehaviorType(1);	//1编辑
			req.setTargetId(userSettlementConfig.getId());
		    
			UserSettlementConfig usc = new UserSettlementConfig();
			usc.setId(userSettlementConfig.getId());
			PageFilter ph = new PageFilter();
			ph.setOrder("desc");
			ph.setPage(1);
			ph.setRows(50);
			ph.setSort("id");
			List<UserSettlementConfig>  us = userSettlementConfigService.dataGrid(usc, ph);
			UserSettlementConfig u = us.get(0);
			req.setTargetInfo("APP名称："+ u.getOrganizationAppName()
								+",App登录名:" + u.getLoginName()
								+",真实名称:" + u.getRealName());
			req.setDetails("ID为" + sessionInfo.getId() + "的用户，编辑"
								+ "sys_user_settlement_config 用户费率表中ID="+userSettlementConfig.getId() +"的费率,"
								+"记录来源：管理平台--系统管理--用户费率");
			
			sysRateOperationHistoryService.createHistory(req);
			
		} catch (Exception e) {
			log.error(e);
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping(value = "/setAllUserMaxT0Amt", method = RequestMethod.POST)
	@ResponseBody
	public Json setAllUserMaxT0Amt(Double maxT0Amt, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			User user = userService.get(sessionInfo.getId());
			if (maxT0Amt != null) {
				log.info("费率操作" + user.getLoginName() + " 调整了最大T0提现金额" + maxT0Amt);
				userSettlementConfigService.updateAllUserMaxT0Amt(BigDecimal.valueOf(maxT0Amt));
			}
			j.setSuccess(true);
			j.setMsg("编辑成功！");
			//开始保存记录
			SysRateOperationHistory req = new SysRateOperationHistory();
			req.setCreatorId(sessionInfo.getId());
			req.setRecordType(2);	//1 用户费率编辑 2 T0最高提现限额配置
			req.setBehaviorType(2);	//1编辑 2设置
			req.setTargetInfo("T0最高提现限额配置");
			req.setDetails("ID为" + sessionInfo.getId() + "的用户，设置了T0最高提现限额配置,"
						+"记录来源：管理平台--系统管理--用户费率");
			sysRateOperationHistoryService.createHistory(req);
			
			
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
