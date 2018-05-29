package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.base.Tree;
import com.cn.flypay.pageModel.sys.Resource;

public interface ResourceServiceI {

	public List<Resource> treeGrid();

	public void add(Resource resource);

	public void delete(Long id);

	public void edit(Resource resource);

	public Resource get(Long id);

	public List<Tree> tree(SessionInfo sessionInfo);

	public List<Tree> listAllTree(boolean flag);
	public List<Tree> listSelfTree(SessionInfo sessionInfo, boolean flag, String roleId);

	public List<String> listAllResource();

}
