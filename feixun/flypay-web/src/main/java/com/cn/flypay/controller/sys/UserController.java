package com.cn.flypay.controller.sys;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.AuthenticationLog;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.sys.FeedBack;
import com.cn.flypay.pageModel.sys.Organization;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserBlackList;
import com.cn.flypay.pageModel.sys.UserImage;
import com.cn.flypay.pageModel.trans.FinanceStatement;
import com.cn.flypay.service.base.ServiceException;
import com.cn.flypay.service.payment.AuthenticationService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.MD5Util;
import com.cn.flypay.utils.StringUtil;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

	@Autowired
	private UserService userService;
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private OrganizationService organizationService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		Tuser tuser = userService.getTuser(sessionInfo.getId());
		List<Dictionary> transTypeList = dictionaryService.combox("userType");
		if (tuser.getAgentId().equals("F20160001")) {
			for (Dictionary d : transTypeList) {
				if (d.getCode().equals("23")) {
					d.setText("钻石用户");
				} else if (d.getCode().equals("22")) {
					d.setText("代理商");
				} else if (d.getCode().equals("21")) {
					d.setText("运营中心");
				}
			}
		}
		request.setAttribute("userType", JSON.toJSONString(transTypeList));
		request.setAttribute("userTypeObj", transTypeList);
		List<Dictionary> merchantTypeList = dictionaryService.combox("merchantType");
		request.setAttribute("merchantType", JSON.toJSONString(merchantTypeList));
		request.setAttribute("merchantTypeObj", merchantTypeList);
		return "/admin/user";
	}

	@RequestMapping("/agentManager")
	public String agentManager(HttpServletRequest request, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		System.out.println("sessionInfo.getId():"+sessionInfo.getId());
		Tuser tuser = userService.getTuser(sessionInfo.getId());
		List<Dictionary> transTypeList = dictionaryService.combox("userType");
		//现在版本用户类型的区分
		if (tuser.getAgentId().equals("F20160001")) {
				/*||tuser.getAgentId().equals("F20160010") //天一猫 升级2.0需要
				||tuser.getAgentId().equals("F20160003") //云付
				||tuser.getAgentId().equals("F20160004") //宝库
				||tuser.getAgentId().equals("F20160011") //云汇宝
				 */			
			System.out.println("tuser.getAgentId():"+tuser.getAgentId());
			for (Dictionary d : transTypeList) {
				if (d.getCode().equals("23")) {
					d.setText("钻石用户");
				} else if (d.getCode().equals("22")) {
					d.setText("代理商");
				} else if (d.getCode().equals("21")) {
					System.out.println("运营中心");
					d.setText("运营中心");
				}
			}
		}
		request.setAttribute("userType", JSON.toJSONString(transTypeList));
		request.setAttribute("userTypeObj", transTypeList);
		List<Dictionary> merchantTypeList = dictionaryService.combox("merchantType");
		request.setAttribute("merchantType", JSON.toJSONString(merchantTypeList));
		request.setAttribute("merchantTypeObj", merchantTypeList);
		if (userService.getUserRole()) {
			request.setAttribute("isEdit", "1");
			System.out.println("userService.getUserRole():isEdit 1"+userService.getUserRole());
		} else {
			request.setAttribute("isEdit", "2");
			System.out.println("userService.getUserRole():"+userService.getUserRole());
			System.out.println("isEdit, 22222");
		}
		return "/admin/agentUser";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(User user, PageFilter ph, HttpSession session) {

		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User operator = userService.get(sessionInfo.getId());
		user.setOperateUser(operator);
		user.getOperateUser().setOperateUser(user.getOperateUser());
		user.getOperateUser().getOperateUser().setOperateUser(user.getOperateUser().getOperateUser());
		Grid grid = new Grid();
		grid.setRows(userService.dataGrid(user, ph));
		grid.setTotal(userService.count(user, ph));
		return grid;
	}

	@RequestMapping("agentDataGrid")
	@ResponseBody
	public Grid agentDataGrid(User user, PageFilter ph, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		User operator = userService.get(sessionInfo.getId());
		user.setOperateUser(operator);
		user.getOperateUser().setOperateUser(user.getOperateUser());
		user.getOperateUser().getOperateUser().setOperateUser(user.getOperateUser().getOperateUser());
		Grid grid = new Grid();
		grid.setRows(userService.dataGrid(user, ph));
		grid.setTotal(userService.count(user, ph));
		return grid;
	}

	@RequestMapping("/editPwdPage")
	public String editPwdPage() {
		return "/admin/userEditPwd";
	}

	@RequestMapping("/editUserPwd")
	@ResponseBody
	public Json editUserPwd(HttpServletRequest request, String oldPwd, String pwd) {
		SessionInfo sessionInfo = (SessionInfo) request.getSession().getAttribute(GlobalConstant.SESSION_INFO);
		Json j = new Json();
		try {
			userService.editUserPwd(sessionInfo, oldPwd, pwd);
			j.setSuccess(true);
			j.setMsg("密码修改成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/addPage")
	public String addPage(HttpServletRequest request) {
		List<Dictionary> transTypeList = dictionaryService.combox("userType");
		request.setAttribute("userTypeObj", transTypeList);
		return "/admin/userAdd";
	}

	@RequestMapping("/add")
	@ResponseBody
	public Json addRootUser(User user) {
		Json j = validateUser(user, false);
		if (StringUtil.isNullOrEmpty(j.getMsg())) {
			try {
				userService.addRootUserForOrganization(user.getLoginName(), MD5Util.md5(user.getPassword()),
						user.getPcode(), user.getOrganizationId());
				j.setSuccess(true);
				j.setMsg("根用户添加成功！");
			} catch (Exception e) {
				e.printStackTrace();
				j.setMsg(e.getMessage());
			}
		}
		return j;
	}

	private Json validateUser(User user, boolean isUpdate) {
		Json j = new Json();
		List<User> u = userService.getByLoginName(user);
		if (u != null && u.size() > 0 && !isUpdate) {
			j.setMsg("用户名已存在!");
		} else if (u != null && u.size() > 1 && isUpdate) {
			j.setMsg("用户名已存在!");
		} else if (u != null && u.size() == 1 && isUpdate && !u.get(0).getId().equals(user.getId())) {
			j.setMsg("用户名已存在!");
		}
		if (StringUtil.isNotBlank(user.getPcode())) {
			User pcode = userService.getByCode(user.getPcode());
			if (pcode == null) {
				j.setMsg("推荐人Code不存在!");
			} else {
				user.setPid(pcode.getId());
				user.setPath(user.getPath() == null ? "" : user.getPath() + "/" + pcode.getId());
			}
		}
		return j;
	}

	@RequestMapping("/get")
	@ResponseBody
	public User get(Long id) {
		return userService.get(id);
	}

	@RequestMapping("/delete")
	@ResponseBody
	public Json delete(Long id) {
		Json j = new Json();
		try {
			userService.delete(id);
			j.setMsg("删除成功！");
			j.setSuccess(true);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/agentUpgrade")
	@ResponseBody
	public Json agentUpgrade(Long id) {
		Json j = new Json();
		try {
			String ret = userService.editToAgent(id);
			String msg = StringUtil.isEmpty(ret) ? "添加结束" : ret;
			j.setMsg(msg);
			j.setSuccess(true);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
			j.setSuccess(false);
		}
		return j;
	}

	@RequestMapping("/agentUpgrade2")
	@ResponseBody
	public Json agentUpgrade2(Long id) {
		Json j = new Json();
		try {
			String ret = userService.editToAgent2(id);
			String msg = StringUtil.isEmpty(ret) ? "添加结束" : ret;
			j.setMsg(msg);
			j.setSuccess(true);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
			j.setSuccess(false);
		}
		return j;
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
		User u = userService.get(id);
		request.setAttribute("user", u);
		List<Dictionary> transTypeList = dictionaryService.combox("userType");
		request.setAttribute("userTypeObj", transTypeList);
		if (userService.getUserRole()) {
			request.setAttribute("isEdit", "1");
		} else {
			request.setAttribute("isEdit", "2");
		}
		return "/admin/userEdit";
	}

	@RequestMapping("/agentEditPage")
	public String agentEditPage(HttpServletRequest request, Long id) {
		User u = userService.get(id);
		request.setAttribute("user", u);
		List<Dictionary> transTypeList = dictionaryService.combox("userType");
		request.setAttribute("userTypeObj", transTypeList);
		return "/admin/agentUserEdit";
	}

	@RequestMapping("/agentEdit")
	@ResponseBody
	public Json agentEdit(User user) {
		Json j = validateUser(user, true);
		if (StringUtil.isNullOrEmpty(j.getMsg())) {
			try {
				userService.editAgent(user);
				j.setSuccess(true);
				j.setMsg("编辑成功！");
			} catch (ServiceException e) {
				j.setMsg(e.getMessage());
			}
		}
		return j;
	}

	@RequestMapping("/authenticationPage")
	public String authenticationPage(HttpServletRequest request, Long id) {
		User u = userService.get(id);
		List<UserImage> imageList = userService.findToCheckImagesByUserId(id);

		request.setAttribute("user", u);
		request.setAttribute("images", imageList);
		Set<Integer> authTyps = new HashSet<Integer>();
		authTyps.add(AuthenticationLog.auth_type.auto.getCode());
		authTyps.add(AuthenticationLog.auth_type.manual.getCode());
		request.setAttribute("errorInfo", authenticationService.findAuthErroInfo(id, authTyps));
		return "/admin/userAuthentication";
	}

	@RequestMapping("/authenticationMerchantPage")
	public String authenticationMerchantPage(HttpServletRequest request, Long id) {
		User u = userService.get(id);
		List<UserImage> imageList = userService.findToCheckMerchantImagesByUserId(id);
		List<Dictionary> imageTypeList = dictionaryService.combox("imageType");
		request.setAttribute("imageTypeObj", imageTypeList);

		Collections.sort(imageList, new Comparator<UserImage>() {
			public int compare(UserImage b1, UserImage b2) {
				return b1.getId().compareTo(b2.getId());
			};
		});
		List<Dictionary> merchantTypeList = dictionaryService.combox("merchantType");
		request.setAttribute("merchantTypeObj", merchantTypeList);

		request.setAttribute("user", u);
		request.setAttribute("images", imageList);
		Set<Integer> authTyps = new HashSet<Integer>();
		authTyps.add(AuthenticationLog.auth_type.manual_merchant.getCode());
		request.setAttribute("errorInfo", authenticationService.findAuthErroInfo(id, authTyps));
		return "/admin/merchantAuthentication";
	}

	@RequestMapping("/auth")
	@ResponseBody
	public Json auth(Long id, Boolean status, String errorInfo) {
		Json j = new Json();
		try {
			userService.updateUserAuthStatus(id, status, errorInfo);
			j.setSuccess(true);
			j.setMsg("认证" + (status ? "通过" : "不通过") + "！");

		} catch (ServiceException e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/agentAuth")
	@ResponseBody
	public Json agentAuth(Long id, Boolean status, String errorInfo) {
		Json j = new Json();
		try {
			userService.updateUserAuthStatus(id, status, errorInfo);
			j.setSuccess(true);
			j.setMsg("认证" + (status ? "通过" : "不通过") + "！");

		} catch (ServiceException e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/authMerchant")
	@ResponseBody
	public Json authMerchant(Long id, Integer merchantType, String errorInfo) {
		Json j = new Json();
		try {
			userService.updateUserAuthMerchantStatus(id, merchantType, errorInfo);
			j.setSuccess(true);
			j.setMsg("认证" + ((merchantType == User.merchant_type.REAL_MERCHANT.getCode()
					|| merchantType == User.merchant_type.NONE_MERCHANT.getCode()) ? "通过" : "不通过") + "！");

		} catch (ServiceException e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/edit")
	@ResponseBody
	public Json edit(User user) {
		Json j = validateUser(user, true);
		if (StringUtil.isNullOrEmpty(j.getMsg())) {
			try {
				userService.edit(user);
				j.setSuccess(true);
				j.setMsg("编辑成功！");
			} catch (Exception e) {
				e.printStackTrace();
				j.setMsg(e.getMessage());
			}
		}
		return j;
	}

	@RequestMapping("/black")
	@ResponseBody
	public Json black(String idNo, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			userService.editBlackStatus(idNo, sessionInfo.getId());
			j.setMsg("操作成功！");
			j.setSuccess(true);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/blackList")
	public String blackList() {
		return "/admin/userBlackList";
	}

	@ResponseBody
	@RequestMapping("/blackListDataGrid")
	public Grid blackListDataGrid(UserBlackList userBlackList, PageFilter ph, HttpSession session) {
		Grid grid = new Grid();
		grid.setRows(userService.dataGrid(userBlackList, ph));
		grid.setTotal(userService.count(userBlackList, ph));
		return grid;
	}

	@RequestMapping("/exportExcel")
	@ResponseBody
	public ModelAndView exportExcel(HttpServletRequest request, HttpServletResponse response, User user,
			HttpSession session) throws Exception {
		try {

			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition",
					"attachment;fileName=" + DateUtil.convertCurrentDateTimeToString() + ".xls");
			response.setHeader("Content-type", "charset=UTF-8");
			OutputStream ouputStream = response.getOutputStream();

			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			User operator = userService.get(sessionInfo.getId());
			user.setOperateUser(operator);

			Workbook wb = userService.exportUserList(user);
			wb.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
