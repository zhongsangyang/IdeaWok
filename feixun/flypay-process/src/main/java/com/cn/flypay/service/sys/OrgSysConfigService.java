package com.cn.flypay.service.sys;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.OrgSysConfig;

public interface OrgSysConfigService {

	public List<OrgSysConfig> dataGrid(OrgSysConfig param, PageFilter ph);

	public Long count(OrgSysConfig param, PageFilter ph);

	public OrgSysConfig get(Long id);

	public void edit(OrgSysConfig orgSysConfig);

	public JSONObject getMsgConfigJSONObject(String agentId);

	public JSONObject getJiGuangConfigJSONObject(String agentId);

	public OrgSysConfig getByOrgId(Long orgId);

	public void initOrgSysConfig(Torganization t);
}
