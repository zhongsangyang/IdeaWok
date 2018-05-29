package com.cn.flypay.controller.account;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.account.Account;
import com.cn.flypay.pageModel.account.OrgAccount;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.account.OrgAccountService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.UserService;

@Controller
@RequestMapping("/orgAccount")
public class OrgAccountController extends BaseController {

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private OrgAccountService orgAccountService;
	@Autowired
	private UserService userService;

	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private AccountService accountService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request, HttpSession session) {
		List<Dictionary> transTypeList = dictionaryService.combox("orgAccountType");
		request.setAttribute("orgAccountType", JSON.toJSONString(transTypeList));
		return "/account/orgAccount";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(HttpServletRequest request, OrgAccount account, PageFilter ph, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User user = userService.get(sessionInfo.getId());
		account.setOperateUser(user);

		Grid grid = new Grid();
		grid.setRows(orgAccountService.dataGrid(account, ph));
		grid.setTotal(orgAccountService.count(account, ph));

		return grid;
	}

	@RequestMapping("/rechargeOrgAccount")
	@ResponseBody
	public Json rechargeOrgAccount(Long id, Double amt, Integer type, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			User user = userService.get(sessionInfo.getId());
			Map<String, String> orgAccType = dictionaryService.comboxMap("orgAccountType");
			String desc = user.getRealName() + "向" + orgAccType.get(type.toString()) + "账户充值" + amt + "元";
			String flag = orgAccountService.adjustOrgAccount(id, amt, user, desc);
			if (GlobalConstant.SUCCESS.equals(flag)) {
				j.setMsg("操作成功！");
				j.setSuccess(true);
			} else {
				j.setMsg(flag);
				j.setSuccess(false);
			}
			log.info("用户ID=" + sessionInfo.getId() + "充值" + amt);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/rechargeOrgAccountPage")
	public String rechargeOrgAccountPage(HttpServletRequest request, Long id, HttpSession session) {
		OrgAccount account = orgAccountService.get(id);
		request.setAttribute("account", account);
		
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User user = userService.get(sessionInfo.getId());
		Account acc = accountService.getAccountByUserId(user.getId());
		request.setAttribute("accountAmt", acc.getAvlAmt().doubleValue());
		
		return "/account/adjustOrgAccount";
	}

}
