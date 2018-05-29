package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.ChannelDayAmt;
/**
 * 通道每日录入金额统计
 * @author liangchao
 *
 */
public interface ChannelDayAmtMonitorService {
	/**
	 * 根据页面输入的条件，查询各通道每日录入金额数据
	 * @param channel
	 * @param pf
	 * @return
	 */
	public List<ChannelDayAmt> dataGrid(ChannelDayAmt channelDayAmt,PageFilter pf);
	
	/**
	 * 定时统计每日通道流入金额
	 */
	public void addCollectTodayAmtEveryDay();

}
