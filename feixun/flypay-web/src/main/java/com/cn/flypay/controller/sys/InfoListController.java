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
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.base.Grid;
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.InfoListService;
import com.cn.flypay.service.sys.JiguangPushService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.StringUtil;

@Controller
@RequestMapping("/infoList")
public class InfoListController extends BaseController {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private InfoListService infoService;

	@Autowired
	private UserService userService;
	@Autowired
	private JiguangPushService jiguangPushService;

	@Autowired
	private OrganizationService organizationService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/admin/infoList";
	}

	@ResponseBody
	@RequestMapping("/dataGrid")
	public Grid dataGrid(InfoList infoList, PageFilter ph, HttpSession session) {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		infoList.setOperateUser(userService.get(sessionInfo.getId()));
		Grid grid = new Grid();
		grid.setRows(infoService.dataGrid(infoList, ph));
		grid.setTotal(infoService.count(infoList, ph));
		return grid;
	}

	@RequestMapping("/addPage")
	public String addPage(HttpServletRequest request) {
		return "/admin/infoListAdd";
	}

	@RequestMapping("/add")
	@ResponseBody
	public Json add(InfoList infoList, HttpSession session) {
		Json j = new Json();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		try {
			infoList.setCreator(userService.get(sessionInfo.getId()).getLoginName());
			if (infoList.getInfoType() == InfoList.info_Type.company.getCode()
					&& infoList.getStatus() == InfoList.info_status.release_success.getCode()) {
				Torganization tr = organizationService.getTorganizationInCacheById(infoList.getOrganizationId());
				jiguangPushService.sendMsgInfo(infoList, StringUtil.getAgentId(tr.getCode()));
			}
			infoService.add(infoList);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/addPersonPage")
	public String addPersonPage(HttpServletRequest request) {
		return "/admin/personInfoListAdd";
	}

	@RequestMapping("/addPersonInfoList")
	@ResponseBody
	public Json addPersonInfoList(InfoList infoList, HttpSession session) {
		Json j = new Json();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		try {
			infoList.setCreator(userService.get(sessionInfo.getId()).getLoginName());
			if (infoList.getInfoType() == InfoList.info_Type.person.getCode()
					&& infoList.getStatus() == InfoList.info_status.release_success.getCode()) {
				User u = userService.findUserByLoginName(infoList.getPhone(), StringUtil.getAgentId(organizationService
						.getTorganizationInCacheById(infoList.getOrganizationId()).getCode()));
				if (u != null) {
					infoList.setUserId(u.getId());
					jiguangPushService.sendMsgInfoToPerson(infoList);
					infoService.add(infoList);
				}
			}
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
			infoService.delete(id);
			j.setMsg("停用成功！");
			j.setSuccess(true);
			log.info("用户ID=" + sessionInfo.getId() + "停用系统通道：ID=" + id);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id) {
		List<Dictionary> transTypeList = dictionaryService.combox("transType");
		request.setAttribute("transTypeObj", transTypeList);
		InfoList u = infoService.get(id);
		request.setAttribute("infoList", u);
		return "/admin/infoListEdit";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Json edit(InfoList infoList, HttpSession session) {
		Json j = new Json();
		try {
			// SessionInfo sessionInfo = (SessionInfo)
			// session.getAttribute(GlobalConstant.SESSION_INFO);
			
			infoService.edit(infoList);
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
