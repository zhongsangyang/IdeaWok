package com.cn.flypay.service.sys.impl;

import java.util.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tresource;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.base.Tree;
import com.cn.flypay.pageModel.sys.Resource;
import com.cn.flypay.service.sys.ResourceServiceI;

@Service
public class ResourceServiceImpl implements ResourceServiceI {

	@Autowired
	private BaseDao<Tresource> resourceDao;

	@Override
	public List<Resource> treeGrid() {
		List<Resource> lr = new ArrayList<Resource>();
		List<Tresource> l = resourceDao
				.find("select distinct t from Tresource t left join fetch t.resource  order by t.seq");
		if ((l != null) && (l.size() > 0)) {
			for (Tresource t : l) {
				Resource r = new Resource();
				BeanUtils.copyProperties(t, r);
				r.setCstate(t.getState());
				if (t.getResource() != null) {
					r.setPid(t.getResource().getId());
				}
				r.setIconCls(t.getIcon());
				lr.add(r);
			}
		}
		return lr;
	}

	@Override
	public void add(Resource r) {
		Tresource t = new Tresource();
		t.setCreateDatetime(new Date());
		t.setDescription(r.getDescription());
		t.setIcon(r.getIcon());
		t.setName(r.getName());
		if ((r.getPid() != null) && !"".equals(r.getPid())) {
			t.setResource(resourceDao.get(Tresource.class, r.getPid()));
		}
		t.setResourceType(r.getResourceType());
		t.setSeq(r.getSeq());
		t.setState(r.getCstate());
		t.setUrl(r.getUrl());
		resourceDao.save(t);
	}

	@Override
	public void delete(Long id) {
		Tresource t = resourceDao.get(Tresource.class, id);
		del(t);
	}

	private void del(Tresource t) {
		if ((t.getResources() != null) && (t.getResources().size() > 0)) {
			for (Tresource r : t.getResources()) {
				del(r);
			}
		}
		resourceDao.delete(t);
	}

	@Override
	public void edit(Resource r) {
		Tresource t = resourceDao.get(Tresource.class, r.getId());
		t.setDescription(r.getDescription());
		t.setIcon(r.getIcon());
		t.setName(r.getName());
		if ((r.getPid() != null) && !"".equals(r.getPid())) {
			t.setResource(resourceDao.get(Tresource.class, r.getPid()));
		}
		t.setResourceType(r.getResourceType());
		t.setSeq(r.getSeq());
		t.setState(r.getCstate());
		t.setUrl(r.getUrl());
		t.setDescription(r.getDescription());
		resourceDao.update(t);
	}

	@Override
	public Resource get(Long id) {
		Tresource t = resourceDao.get(Tresource.class, id);
		Resource r = new Resource();
		BeanUtils.copyProperties(t, r);
		r.setCstate(t.getState());
		if (t.getResource() != null) {
			r.setPid(t.getResource().getId());
			r.setPname(t.getResource().getName());
		}
		return r;
	}

	@Override
	public List<Tree> tree(SessionInfo sessionInfo) {
		List<Tresource> l = null;
		List<Tree> lt = new ArrayList<Tree>();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("resourcetype", 0);// 菜单类型的资源

		if (sessionInfo != null) {
			if ("admin".equals(sessionInfo.getLoginname())) {
				l = resourceDao.find(
						"select distinct t from Tresource t  where t.resourceType = :resourcetype  order by t.seq",
						params);
			} else {
				System.out.println("sessionInfo.getLoginname():"+sessionInfo.getLoginname());
				params.put("userId", Long.valueOf(sessionInfo.getId()));// 自查自己有权限的资源
				l = resourceDao.find(
						"select distinct t from Tresource t join fetch t.roles role join role.users user where t.resourceType = :resourcetype and user.id = :userId order by t.seq",
						params);
				
			}
		} else {
			return null;
		}

		if ((l != null) && (l.size() > 0)) {
			for (Tresource r : l) {
				Tree tree = new Tree();
				tree.setId(r.getId().toString());
				if (r.getResource() != null) {
					tree.setPid(r.getResource().getId().toString());
				} else {
					tree.setState("closed");
				}
				tree.setText(r.getName());
				tree.setIconCls(r.getIcon());
				Map<String, Object> attr = new HashMap<String, Object>();
				attr.put("url", r.getUrl());
				tree.setAttributes(attr);
				lt.add(tree);
			}
		}
		return lt;
	}

	@Override
	public List<Tree> listAllTree(boolean flag) {
		List<Tresource> l = null;
		List<Tree> lt = new ArrayList<Tree>();
		if (flag) {
			l = resourceDao.find("select distinct t from Tresource t left join fetch t.resource  order by t.seq");
		} else {
			l = resourceDao.find(
					"select distinct t from Tresource t left join fetch t.resource where t.resourceType =0 order by t.seq");
		}
		if ((l != null) && (l.size() > 0)) {
			for (Tresource r : l) {
				Tree tree = new Tree();
				tree.setId(r.getId().toString());
				if (r.getResource() != null) {
					tree.setPid(r.getResource().getId().toString());
				}
				tree.setText(r.getName());
				tree.setIconCls(r.getIcon());
				Map<String, Object> attr = new HashMap<String, Object>();
				attr.put("url", r.getUrl());
				tree.setAttributes(attr);
				lt.add(tree);
			}
		}
		return lt;
	}

	@Override
	public List<String> listAllResource() {
		List<String> resourceList = new ArrayList<String>();
		List<Tresource> l = resourceDao
				.find("select distinct t from Tresource t left join fetch t.resource  order by t.seq");
		for (int i = 0; i < l.size(); i++) {
			resourceList.add(l.get(i).getUrl());
		}
		return resourceList;
	}

	public List<Tree> listSelfTree(SessionInfo sessionInfo, boolean flag, String roleId) {
		List<Tresource> allTResList = null;
		List<Tree> resTree = new ArrayList<Tree>();
		if (flag) {
			allTResList = resourceDao
					.find("select distinct t from Tresource t left join fetch t.resource  order by t.seq");
		} else {
			allTResList = resourceDao.find(
					"select distinct t from Tresource t left join fetch t.resource where t.resourceType =0 order by t.seq");
		}
		List<String> resourceUrlNames = sessionInfo.getResourceList();
		Set<Long> setResIds = sessionInfo.getResourceIds();

		if ((allTResList != null) && (allTResList.size() > 0)) {
			for (Tresource tRes : allTResList) {
				if (setResIds.contains(tRes.getId()) == false) {
					continue;
				}
				Tree tree = new Tree();
				tree.setId(tRes.getId().toString());
				if (tRes.getResource() != null) {
					tree.setPid(tRes.getResource().getId().toString());
				}
				tree.setText(tRes.getName());
				tree.setIconCls(tRes.getIcon());
				Map<String, Object> attr = new HashMap<String, Object>();
				attr.put("url", tRes.getUrl());
				tree.setAttributes(attr);
				resTree.add(tree);
			}
		}
		return resTree;
	}
}
