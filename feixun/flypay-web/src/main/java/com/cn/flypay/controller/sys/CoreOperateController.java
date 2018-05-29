package com.cn.flypay.controller.sys;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.CoreOperate;
import com.cn.flypay.pageModel.trans.FinanceAccount;
import com.cn.flypay.service.statement.FinanceService;
import com.cn.flypay.service.sys.CoreOperateService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.StringUtil;

@Controller
@RequestMapping("/coreOperate")
public class CoreOperateController extends BaseController {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private CoreOperateService coreOperateService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private FinanceService financeService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		List<FinanceAccount> bl = financeService.findRealTimeAccount();
		request.setAttribute("T1Amt", bl.get(0).getT1Amt());
		request.setAttribute("D1Amt", bl.get(0).getD1Amt());
		return "/admin/coreOperate";
	}

	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(CoreOperate coreOperate, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(coreOperateService.dataGrid(coreOperate, ph));
		grid.setTotal(coreOperateService.count(coreOperate, ph));
		return grid;
	}

	@RequestMapping("/sendT5step")
	@ResponseBody
	public Json sendT5step(Long id, HttpSession session) {
		Json j = new Json();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		try {
			String loginName = userService.get(sessionInfo.getId()).getLoginName();
			log.info(loginName + "确认了的平台账务已经OK，进行T5步进");
			String result = coreOperateService.updateCoreOperateByDate(new Date(), loginName);
			if (StringUtil.isNotBlank(result) && result.equals(GlobalConstant.SUCCESS)) {
				j.setSuccess(true);
				j.setMsg("步进成功！");
			} else {
				j.setSuccess(false);
				j.setMsg(result);
			}
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	
	@RequestMapping("/sendD1step")
	@ResponseBody
	public Json sendD1step(Long id, HttpSession session) {
		Json j = new Json();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		try {
			String loginName = userService.get(sessionInfo.getId()).getLoginName();
			log.info(loginName + "确认了的平台账务已经OK，进行D1步进");
			String result = coreOperateService.updateCoreOperateByDateTwo(new Date(), loginName);
			if (StringUtil.isNotBlank(result) && result.equals(GlobalConstant.SUCCESS)) {
				j.setSuccess(true);
				j.setMsg("步进成功！");
			} else {
				j.setSuccess(false);
				j.setMsg(result);
			}
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
