package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.model.sys.TorgChannel;
import com.cn.flypay.pageModel.sys.OrgChannel;

public interface OrgChannelService {

	public List<OrgChannel> findOrgChannelByOrgId(Long orgId);

	public void add(OrgChannel orgChannel);

	public void edit(OrgChannel orgChannel);

	public void addTorgChannelList(List<TorgChannel> tcs);

	public void editOrgChannel(List<OrgChannel> orgs);

	List<OrgChannel> findOrgChannelByAgentId(String agentId);
	
	
}
