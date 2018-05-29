package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.model.sys.TinfoList;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.InfoList;

public interface InfoListService {

	public List<InfoList> dataGrid(InfoList infoList, PageFilter ph);

	public Long count(InfoList infoList, PageFilter ph);

	public void add(InfoList infoList);

	public void delete(Long id);

	public InfoList get(Long id);

	public void edit(InfoList infoList);

	public void updateAllInfoListToReadedStatus(InfoList infoList);

	public Long countUnreadedMsgInfoByUserId(Long userId);

	public List<String> findSystemNews(String agentId);

	public List<InfoList> findSystemNotice(String agentId);
	
	public List<TinfoList> findUnRead(InfoList infoList, PageFilter pf);

	public void updateAllInfoListToReadedStatus(InfoList infoList, List<TinfoList> list);
}
