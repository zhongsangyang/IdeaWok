package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.AppVersion;

public interface AppVersionService {

	public List<AppVersion> dataGrid(AppVersion param, PageFilter ph);

	public Long count(AppVersion param, PageFilter ph);

	public void add(AppVersion param);

	public void delete(Long id);

	public void edit(AppVersion param);

	public AppVersion getNewestAppVersion(String appType, String agentId);
	
	
	public AppVersion getNewestApp(String appType, String agentId,String versionId);

	public AppVersion get(Long id);

}
