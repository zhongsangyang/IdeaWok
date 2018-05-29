package com.cn.flypay.pageModel.base;

import java.util.List;
import java.util.Set;

public class SessionInfo implements java.io.Serializable {

    private static final long serialVersionUID = 7918191410900270565L;
    private Long id;// 用户ID
    private String loginname;// 登录名
    private String name;// 姓名
    private String ip;// 用户IP
    private String roleIdStr;
    private Set<Long> resourceIds;// 用户可以访问的资源ID

    private List<String> resourceList;// 用户可以访问的资源地址列表

    private List<String> resourceAllList;

    public List<String> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<String> resourceList) {
        this.resourceList = resourceList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleIdStr() {
        return roleIdStr;
    }

    public void setRoleIdStr(String roleIdStr) {
        this.roleIdStr = roleIdStr;
    }

    public Set<Long> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(Set<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public List<String> getResourceAllList() {
        return resourceAllList;
    }

    public void setResourceAllList(List<String> resourceAllList) {
        this.resourceAllList = resourceAllList;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
