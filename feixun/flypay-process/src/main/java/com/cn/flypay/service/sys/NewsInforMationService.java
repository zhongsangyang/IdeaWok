package com.cn.flypay.service.sys;

import com.alibaba.fastjson.JSONArray;
import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.SysInformation;
import com.cn.flypay.pageModel.sys.User;

public interface NewsInforMationService {
	
	public List<SysInformation> dataGrid(SysInformation sys, PageFilter ph);

	public Long count(SysInformation sys, PageFilter ph);
	
	public void edit(SysInformation sys);
	
	public void add(SysInformation sys,User user);
	
	public JSONArray findList(String agentId,PageFilter pf);
	
	public SysInformation getSysInfor(String id);
	
	public boolean editSum(SysInformation sys);

}
