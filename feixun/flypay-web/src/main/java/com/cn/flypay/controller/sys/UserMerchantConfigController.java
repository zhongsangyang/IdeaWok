package com.cn.flypay.controller.sys;

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
import com.cn.flypay.pageModel.sys.UserMerchantConfig;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.UserMerchantConfigService;
import com.cn.flypay.service.sys.UserService;

@Controller
@RequestMapping("/userMerchantConfig")
public class UserMerchantConfigController extends BaseController {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private UserMerchantConfigService userMerchantConfigService;

	@Autowired
	private UserService userService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/admin/userMerchantConfig";
	}

	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(UserMerchantConfig userMerchantConfig, PageFilter ph, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		userMerchantConfig.setOperateUser(userService.get(sessionInfo.getId()));
		Grid grid = new Grid();
		grid.setRows(userMerchantConfigService.dataGrid(userMerchantConfig, ph));
		grid.setTotal(userMerchantConfigService.count(userMerchantConfig, ph));
		return grid;
	}

	@RequestMapping("/editMerchantStatus")
	@ResponseBody
	public Json editMerchantStatus(Long id, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			// userMerchantConfigService.editMerchantStatus(id);
			j.setMsg("停用成功！");
			j.setSuccess(true);
			log.info("用户ID=" + sessionInfo.getId() + "停用系统通道：ID=" + id);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping(value = "/reRegistMerchant", method = RequestMethod.POST)
	@ResponseBody
	public Json reRegistMerchant(UserMerchantConfig userMerchantConfig, HttpSession session) {
		Json j = new Json();
		try {
			// userMerchantConfigService.sendMsgInfo(infoList,
			// StringUtil.getAgentId(tr.getCode()));
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
