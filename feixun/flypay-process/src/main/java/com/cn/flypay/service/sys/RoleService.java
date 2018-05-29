package com.cn.flypay.service.sys;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.cn.flypay.model.sys.Trole;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.Tree;
import com.cn.flypay.pageModel.sys.Role;

public interface RoleService {

	public List<Role> dataGrid(Role role, PageFilter ph);

	public Map<Long, Trole> findAllRole();

	public Long count(Role role, PageFilter ph);

	public void add(Role role);

	public void delete(Long id);

	public void edit(Role role);

	public Role get(Long id);

	public void grant(Role role);

	public List<Tree> tree(HttpSession session);
	public List<Tree> subTree(HttpSession session, String roleIdStr);

}
