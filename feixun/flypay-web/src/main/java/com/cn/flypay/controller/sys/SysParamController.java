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
import com.cn.flypay.pageModel.sys.SysParameter;
import com.cn.flypay.service.sys.SysParamService;

@Controller
@RequestMapping("/sysparam")
public class SysParamController extends BaseController {

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private SysParamService sysparamService;

	@RequestMapping("/manager")
	public String manager(HttpServletRequest request) {
		return "/admin/sysParam";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public Grid dataGrid(SysParameter param, PageFilter ph) {
		Grid grid = new Grid();
		grid.setRows(sysparamService.dataGrid(param, ph));
		grid.setTotal(sysparamService.count(param, ph));
		return grid;
	}

	@RequestMapping("/addPage")
	public String addPage(HttpServletRequest request, String type) {
		return "/admin/sysParamAdd";
	}

	@RequestMapping("/add")
	@ResponseBody
	public Json add(SysParameter param, HttpSession session) {
		Json j = new Json();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		try {
			sysparamService.add(param);
			j.setSuccess(true);
			j.setMsg("添加成功！");
			log.info("用户ID=" + sessionInfo.getId() + "添加系统参数：" + param.getParaName() + "value = " + param.getParaValue());
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
			sysparamService.delete(id);
			j.setMsg("停用成功！");
			j.setSuccess(true);
			log.info("用户ID=" + sessionInfo.getId() + "停用系统参数：ID=" + id);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("/editPage")
	public String editPage(HttpServletRequest request, Long id, Integer prmType, Integer prmVersion) {
		SysParameter u = sysparamService.get(id);
		request.setAttribute("systemParam", u);
		return "/admin/sysParamEdit";
	}

	@RequestMapping(value = "/edit")
	@ResponseBody
	public Json edit(SysParameter param, HttpSession session) {
		Json j = new Json();
		try {
			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
			sysparamService.edit(param);
			j.setSuccess(true);
			j.setMsg("编辑成功");
			log.info("用户ID=" + sessionInfo.getId() + "更改系统参数：" + param.getParaName() + "value = " + param.getParaValue());
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
