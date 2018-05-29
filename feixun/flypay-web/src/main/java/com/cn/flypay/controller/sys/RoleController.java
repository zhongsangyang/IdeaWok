package com.cn.flypay.controller.sys;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.base.*;
import com.cn.flypay.pageModel.sys.Role;
import com.cn.flypay.service.sys.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @RequestMapping("/manager")
    public String manager() {
        return "/admin/role";
    }

    @RequestMapping("/dataGrid")
    @ResponseBody
    public Grid dataGrid(Role role, PageFilter ph) {
        Grid grid = new Grid();
        grid.setRows(roleService.dataGrid(role, ph));
        grid.setTotal(roleService.count(role, ph));
        return grid;
    }

    @RequestMapping("/tree")
    @ResponseBody
    public List<Tree> tree(HttpSession session) {
        return roleService.tree(session);
    }

    @RequestMapping("/subTree")
    @ResponseBody
    public List<Tree> subTree(HttpSession session, String roleIdStr) {
        return roleService.subTree(session, roleIdStr);
    }

    @RequestMapping("/addPage")
    public String addPage() {
        return "/admin/roleAdd";
    }

    @RequestMapping("/add")
    @ResponseBody
    public Json add(HttpSession session, Role role) {
        Json j = new Json();
        try {
            SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
            role.setRoleIdStr(sessionInfo.getRoleIdStr());
            roleService.add(role);
            j.setSuccess(true);
            j.setMsg("添加成功！");
        } catch (Exception e) {
            j.setMsg(e.getMessage());
        }
        return j;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Json delete(Long id) {
        Json j = new Json();
        try {
            roleService.delete(id);
            j.setMsg("删除成功！");
            j.setSuccess(true);
        } catch (Exception e) {
            j.setMsg(e.getMessage());
        }
        return j;
    }

    @RequestMapping("/get")
    @ResponseBody
    public Role get(Long id) {
        return roleService.get(id);
    }

    @RequestMapping("/editPage")
    public String editPage(HttpServletRequest request, Long id) {
        Role r = roleService.get(id);
        request.setAttribute("role", r);
        return "/admin/roleEdit";
    }

    @RequestMapping("/edit")
    @ResponseBody
    public Json edit(Role role) {
        Json j = new Json();
        try {
            roleService.edit(role);
            j.setSuccess(true);
            j.setMsg("编辑成功！");
        } catch (Exception e) {
            j.setMsg(e.getMessage());
        }
        return j;
    }

    @RequestMapping("/grantPage")
    public String grantPage(HttpServletRequest request, Long id) {
        Role r = roleService.get(id);
        request.setAttribute("role", r);
        return "/admin/roleGrant";
    }

    @RequestMapping("/grant")
    @ResponseBody
    public Json grant(Role role) {
        Json j = new Json();
        try {
            roleService.grant(role);
            j.setMsg("授权成功！");
            j.setSuccess(true);
        } catch (Exception e) {
            j.setMsg(e.getMessage());
        }
        return j;
    }

}
