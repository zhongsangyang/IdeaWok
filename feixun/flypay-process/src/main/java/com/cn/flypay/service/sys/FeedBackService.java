package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.FeedBack;

public interface FeedBackService {

	public List<FeedBack> dataGrid(FeedBack param, PageFilter ph);

	public Long count(FeedBack param, PageFilter ph);

	public void edit(FeedBack param);

	public FeedBack get(Long id);

	void addFeedBack(Long userId, String msgCon, String agentId);
}
