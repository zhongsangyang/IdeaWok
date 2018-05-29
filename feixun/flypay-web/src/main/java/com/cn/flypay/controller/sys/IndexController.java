package com.cn.flypay.controller.sys;

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
import com.cn.flypay.pageModel.base.Json;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.sys.ResourceServiceI;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.IpUtils;
import com.cn.flypay.utils.StringUtil;

@Controller
@RequestMapping("/admin")
public class IndexController extends BaseController {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private UserService userService;

	@Autowired
	private ResourceServiceI resourceService;

	@RequestMapping("/index")
	public String index(HttpServletRequest request) {
		SessionInfo sessionInfo = (SessionInfo) request.getSession().getAttribute(GlobalConstant.SESSION_INFO);
		if ((sessionInfo != null) && (sessionInfo.getId() != null)) {
			if (!sessionInfo.getRoleIdStr().equals("23")) {//不允许代理商登录
				return "/index";
			}
		}
		return "/login";
	}
		

    @ResponseBody
    @RequestMapping("/login")
    public Json login(User user, HttpSession session, HttpServletRequest request) {
        Json j = new Json();
        User sysuser = userService.loginManagerSystem(user);
        if (sysuser != null) {
            j.setSuccess(true);
            j.setMsg("登陆成功！");

            SessionInfo sessionInfo = new SessionInfo();
            sessionInfo.setId(sysuser.getId());
            sessionInfo.setLoginname(sysuser.getLoginName());
            sessionInfo.setName(sysuser.getName());
            sessionInfo.setRoleIdStr(sysuser.getRoleIds());
            String ip = null;
            try {
                ip = IpUtils.getIpAddress(request);
                sessionInfo.setIp(ip);
            } catch (Exception e) {
                log.info

                        (sysuser.getCode() + "登陆时，获取IP 失败");
            }
            sessionInfo.setResourceList(userService.listResource(sysuser.getId()));
            sessionInfo.setResourceIds(userService.listUserResIds(sysuser.getId()));
            sessionInfo.setResourceAllList(resourceService.listAllResource());
            session.setAttribute(GlobalConstant.SESSION_INFO, sessionInfo);
            userService.editUserIp(sessionInfo);
            log.info

                    (sysuser.getId() + "登录成功, 登录IP：" + ip);
        } else {
            j.setMsg("用户名或密码错误！");
        }
        return j;
    }

    @ResponseBody
	@RequestMapping("/cacheUser")
	public Json cacheUser(User user, HttpSession session) {
		Json j = new Json();
		User sysuser = userService.getMemntUser(user);
		SessionInfo sessionInfo = new SessionInfo();
		sessionInfo.setId(sysuser.getId());
		sessionInfo.setLoginname(sysuser.getLoginName());
		sessionInfo.setName(sysuser.getName());
		sessionInfo.setResourceList(userService.listResource(sysuser.getId()));
		sessionInfo.setResourceAllList(resourceService.listAllResource());
		session.setAttribute(GlobalConstant.SESSION_INFO, sessionInfo);
		return j;
	}

	@ResponseBody
	@RequestMapping("/logout")
	public Json logout(HttpSession session) {
		Json j = new Json();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		if (session != null) {
			session.invalidate();
		}
		j.setSuccess(true);
		j.setMsg("注销成功！");
		log.info(sessionInfo.getId() + "注销成功");
		return j;
	}
	
	public String subString(String sub,String con){
		return sub.substring(con.length());
	}
	
	
	/**
	 * 代理商登录入口
	 * @param request
	 * @return
	 */
	@RequestMapping("/agent")
	public String agentIndex(HttpServletRequest request) {
		SessionInfo sessionInfo = (SessionInfo) request.getSession().getAttribute(GlobalConstant.SESSION_INFO);
		//未登录或者登录不是代理商，返回登录界面
		if ((sessionInfo != null) && (sessionInfo.getId() != null) && (sessionInfo.getRoleIdStr().equals("23"))) {
				return "/agent";
		}
		
		return "/agentLg";
	}
	
	
    /**
     * TODO
     * 代理商登录
     * @param user
     * @param session
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/agentLg")
    public Json agentLogin(User user, HttpSession session, HttpServletRequest request) {
        Json j = new Json();
        User sysuser = userService.loginAgentManagerSystem(user);
        if (sysuser != null) {
            j.setSuccess(true);
            j.setMsg("登陆成功！");

            SessionInfo sessionInfo = new SessionInfo();
            sessionInfo.setId(sysuser.getId());
            sessionInfo.setLoginname(sysuser.getLoginName());
            sessionInfo.setName(sysuser.getName());
            sessionInfo.setRoleIdStr(sysuser.getRoleIds());
            String ip = null;
            try {
                ip = IpUtils.getIpAddress(request);
                sessionInfo.setIp(ip);
            } catch (Exception e) {
                log.info

                        (sysuser.getCode() + "登陆时，获取IP 失败");
            }
            sessionInfo.setResourceList(userService.listResource(sysuser.getId()));
            sessionInfo.setResourceIds(userService.listUserResIds(sysuser.getId()));
            sessionInfo.setResourceAllList(resourceService.listAllResource());
            session.setAttribute(GlobalConstant.SESSION_INFO, sessionInfo);
            userService.editUserIp(sessionInfo);
            log.info

                    (sysuser.getId() + "登录成功, 登录IP：" + ip);
        } else {
            j.setMsg("用户名或密码错误！");
        }
        return j;
    }	
	
}
