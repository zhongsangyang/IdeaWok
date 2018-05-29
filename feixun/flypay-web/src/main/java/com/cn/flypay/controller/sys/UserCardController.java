package com.cn.flypay.controller.sys;

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
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserCard;
import com.cn.flypay.pageModel.trans.BrokerageDetail;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.StringUtil;

@Controller
@RequestMapping("/userCard")
public class UserCardController extends BaseController {

	private Log logger = LogFactory.getLog(getClass());
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private UserService userService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/admin/userCard";
	}

	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(UserCard uc, PageFilter ph, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User operator = userService.get(sessionInfo.getId());
		Grid grid = new Grid();
		uc.setOperateUser(operator);
		grid.setTotal(userCardService.count(uc, ph));
		grid.setRows(userCardService.dataGrid(sessionInfo,uc, ph));
		return grid;

	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
		UserCard u = userCardService.get(id);
		request.setAttribute("userCard", u);
		return "/admin/userCardEdit";
	}



	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Json edit(Channel param, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);

			// channelService.edit(param);
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping(value = "/stopUseCard", method = RequestMethod.POST)
	@ResponseBody
	public Json stopUseCard(Long userId, Long cardId, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);

			userCardService.deleteCard(userId, cardId);
			j.setSuccess(true);
			j.setMsg("停用成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
