package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.util.ArrayUtil;
import com.cn.flypay.model.util.CollectionUtil;
import com.cn.flypay.model.util.JSON;
import com.cn.flypay.model.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tresource;
import com.cn.flypay.model.sys.Trole;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.base.Tree;
import com.cn.flypay.pageModel.sys.Role;
import com.cn.flypay.service.sys.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private BaseDao<Trole> roleDao;

	@Autowired
	private BaseDao<Tuser> userDao;

	@Autowired
	private BaseDao<Tresource> resourceDao;
	
	@Autowired
	private BaseDao<Torganization> organizationDao;

    @Override
    public void add(Role r) {
        Trole t = new Trole();
        t.setIsDefault(GlobalConstant.NOT_DEFAULT);
        t.setName(r.getName());
        t.setSeq(r.getSeq());
        t.setDescription(r.getDescription());
        Torganization org = organizationDao.get(Torganization.class, r.getOrganizationId());
        t.setOrganization(org);

        String fromIds = r.getRoleIdStr();
        String[] roleIds = StringUtil.split(fromIds);

        Set<Trole> fromSetRole = new HashSet<Trole>();
        for (String idStr : roleIds) {
            Trole fromR = roleDao.get(Trole.class, Long.parseLong(idStr));
            fromSetRole.add(fromR);
        }
        t.setFromRoles(fromSetRole);
        roleDao.save(t);
    }
	
	@Override
	public void delete(Long id) {
		Trole t = roleDao.get(Trole.class, id);
		roleDao.delete(t);
	}

	@Override
	public void edit(Role r) {
		Trole t = roleDao.get(Trole.class, r.getId());
		t.setDescription(r.getDescription());
		t.setName(r.getName());
		t.setSeq(r.getSeq());
		Torganization org = organizationDao.get(Torganization.class, r.getOrganizationId());
		t.setOrganization(org);
		roleDao.update(t);
	}

	@Override
	public Role get(Long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		Trole t = roleDao.get("select t from Trole t left join t.organization org where t.id = :id", params);
		Role r = new Role();
		BeanUtils.copyProperties(t, r);
		
		if (t.getOrganization() != null) {
			r.setOrganizationId(t.getOrganization().getId());
			r.setOrganizationName(t.getOrganization().getName());
		}
		
		Set<Tresource> s = t.getResources();
		if ((s != null) && !s.isEmpty()) {
			boolean b = false;
			String ids = "";
			String names = "";
			for (Tresource tr : s) {
				if (b) {
					ids += ",";
					names += ",";
				} else {
					b = true;
				}
				ids += tr.getId();
				names += tr.getName();
			}
			r.setResourceIds(ids);
			r.setResourceNames(names);
		}
		return r;
	}

	@Override
	public List<Role> dataGrid(Role role, PageFilter ph) {
		List<Role> ul = new ArrayList<Role>();
		Map<String, Object> params = new HashMap<String, Object>();
	/*	System.out.println("roleID=" + role.getId());
		System.out.println("roleIdStr=" + role.getRoleIdStr());
		System.out.println("role=" + JSON.getDefault().toJSONString(role));*/

		String roleIds = role.getRoleIdStr();
		String[] listRoleIds = StringUtil.split(roleIds);
		Set<String> setRoleIds = ArrayUtil.asSet(listRoleIds);
		List<Trole> tRoles = null;

		if (StringUtil.isEmpty(roleIds)) {
			return ul;
		} else {
			// 超级管理员角色
			if (setRoleIds.contains("1")) {
				String hql = "select t from Trole t left join t.organization org";
				tRoles = roleDao.find(hql + whereHql(role, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
			} else {
				// 其他角色
//                params.put("roleIds", roleIds);
				String hql = "select t from Trole t left join t.organization org where t.id in (" + roleIds + ")";
				tRoles = roleDao.find(hql + orderHql(ph), params, ph.getPage(), ph.getRows());
				tRoles = findSubRoleFromTrole(tRoles, false);
			}
		}

		if (CollectionUtil.isEmpty(tRoles)) {
			return ul;
		}

		for (Trole t : tRoles) {
			Role u = new Role();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrganizationId(t.getOrganization().getId());
				u.setOrganizationName(t.getOrganization().getName());
			}
			ul.add(u);
		}
		return ul;

	}

	@Override
	public Long count(Role role, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from Trole t ";
		return roleDao.count("select count(*) " + hql + whereHql(role, params), params);
	}

	private String whereHql(Role role, Map<String, Object> params) {
		String hql = "";
		if (role != null) {
			hql += " where 1=1 ";
			if (role.getId() != null) {
				hql += " and t.id = :id";
				params.put("id",role.getId());
			}
			if (role.getName() != null) {
				hql += " and t.name like :name";
				params.put("name", "%%" + role.getName() + "%%");
			}
		}
		return hql;
	}

	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}

	@Override
	public void grant(Role role) {
		Trole t = roleDao.get(Trole.class, role.getId());
		if ((role.getResourceIds() != null) && !role.getResourceIds().equalsIgnoreCase("")) {
			System.out.println("role.getResourceIds()"+role.getResourceIds());
			String ids = "";
			boolean b = false;
			for (String id : role.getResourceIds().split(",")) {
				if (b) {
					ids += ",";
				} else {
					b = true;
				}
				ids += id;
			}
			t.setResources(new HashSet<Tresource>(resourceDao.find("select distinct t from Tresource t where t.id in ("
					+ ids + ")")));
		} else {
			t.setResources(null);
		}
	}

	@Override
	public List<Tree> tree(HttpSession session) {
		List<Trole> l = null;
		List<Tree> lt = new ArrayList<Tree>();
		
		SessionInfo sessionInfo = (SessionInfo)session.getAttribute(GlobalConstant.SESSION_INFO);
		List<Object[]> accs = roleDao.findBySql("select s.ROLE_ID from sys_user_role s where s.USER_ID="+sessionInfo.getId());
		System.out.println("sessionInfo.getId()="+ sessionInfo.getId());
		for (Object[] objects : accs) {
			System.out.println(objects.length);
		}
		if(String.valueOf(accs.get(0)).equals("3")){
			l = roleDao.find("select distinct t from Trole t where t.id in(4,2,16,17,18,19)");
		}else if(String.valueOf(accs.get(0)).equals("4")){
			l = roleDao.find("select distinct t from Trole t where t.id in(16,2,17)");
		}else if(String.valueOf(accs.get(0)).equals("16")){
			l = roleDao.find("select distinct t from Trole t where t.id in(17,2)");
		}else if(String.valueOf(accs.get(0)).equals("18")){
			l = roleDao.find("select distinct t from Trole t where t.id in(2,4,16,17)");
		}else if(String.valueOf(accs.get(0)).equals("19")){
			l = roleDao.find("select distinct t from Trole t where t.id in(2,4,16,17)");
		}else{
			l = roleDao.find("select distinct t from Trole t order by t.seq");
		}

		if ((l != null) && (l.size() > 0)) {
			for (Trole r : l) {
				Tree tree = new Tree();
				tree.setId(r.getId().toString());
				tree.setText(r.getName());
				lt.add(tree);
			}
		}
		return lt;
	}

	@Cacheable(value = "roleCache", key = "'allRole'")
	@Override
	public Map<Long, Trole> findAllRole() {
		Map<Long, Trole> roles = new HashMap<Long, Trole>();
		List<Trole> ls = roleDao.find("select distinct t from Trole t order by t.seq");
		for (Trole t : ls) {
			roles.put(t.getId(), t);
		}
		return roles;
	}


	public List<Tree> subTree(HttpSession session, String roleIdStr) {
		System.out.println("roleService.subTree");
		List<Tree> rolesTree = new ArrayList<Tree>();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(GlobalConstant.SESSION_INFO);
		List<Trole> tRoles = new ArrayList<Trole>();
		List<Tuser> tUsers = new ArrayList<Tuser>();
		Long loginUserId = sessionInfo.getId();
		String hql = "select t from Tuser t left join t.roles r where t.id=" + loginUserId;
		tUsers = userDao.find(hql);
		if (CollectionUtil.isEmpty(tUsers)) {
			return rolesTree;
		}

		Tuser tuser = tUsers.get(0);
		Set<Trole> uRoles = tuser.getRoles();

		if (CollectionUtil.isEmpty(uRoles)) {
			return rolesTree;
		}

		Set<String> setRoleIds = new HashSet<String>();
		for (Trole r : uRoles) {
			setRoleIds.add(String.valueOf(r.getId()));
		}
		String roleIdS = StringUtil.join(setRoleIds);
		System.out.println("roleIdS=" + roleIdS);

		// 超级管理员角色
		if (setRoleIds.contains("1")) {
			hql = "select t from Trole t left join t.organization org where 1=1";
			tRoles = roleDao.find(hql);
		} else {
			// 其他角色
			hql = "select t from Trole t left join t.organization org where t.id in (" + roleIdS + ")";
			tRoles = roleDao.find(hql);
			tRoles = findSubRoleFromTrole(tRoles, false);
		}

		if (CollectionUtil.isEmpty(tRoles)) {
			return rolesTree;
		}

		for (Trole r : tRoles) {
			Tree tree = new Tree();
			tree.setId(r.getId().toString());
			tree.setText(r.getName());
			rolesTree.add(tree);
		}

		return rolesTree;
	}

	private List<Trole> findSubRoleFromTrole(List<Trole> tRoles, boolean isContainSelf) {
		List<Trole> returnTRoles = new ArrayList<Trole>();
		if (isContainSelf) {
			returnTRoles.addAll(tRoles); // 非超管不能看自己的角色
		}
		List<Trole> tRolesTmp = tRoles;
		StringBuilder ids = null;
		Boolean isFirst = true;

		Map<String, Object> params = new HashMap<String, Object>();
		Map<Long, Trole> subMapTRole = new HashMap<Long, Trole>();


		while (CollectionUtil.isNotEmpty(tRolesTmp)) {
			ids = new StringBuilder("");
			isFirst = true;
			params.clear();
			for (Trole r : tRolesTmp) {
				if (isFirst) {
					ids.append(String.valueOf(r.getId()));
					isFirst = false;
				} else {
					ids.append(",");
					ids.append(String.valueOf(r.getId()));
				}
			}

			params.put("fromRoleIds", ids);
			String hql = "select t from Trole t left join t.organization org left join t.fromRoles fr where fr.id in (" + ids.toString() + ")";
			tRolesTmp = roleDao.find(hql);

			for (Trole tRoleTmp : tRolesTmp) {
				if (subMapTRole.get(tRoleTmp.getId()) == null) {
					subMapTRole.put(tRoleTmp.getId(), tRoleTmp);
				}
			}

			tRolesTmp.clear();
			tRolesTmp.addAll(subMapTRole.values());
			returnTRoles.addAll(subMapTRole.values());
			subMapTRole.clear();
		}

		return returnTRoles;
	}

}
