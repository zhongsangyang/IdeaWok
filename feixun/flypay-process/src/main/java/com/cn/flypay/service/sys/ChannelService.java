package com.cn.flypay.service.sys;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Channel;

public interface ChannelService {

	public List<Channel> dataGrid(Channel channel, PageFilter pf);

	public Long count(Channel channel, PageFilter pf);

	public List<Tchannel> searchTchannels();

	public void add(Channel param);

	public void delete(Long id);

	public void edit(Channel param);

	public Channel get(Long id);
	
	/**
	 * 查询指定条件的通道集合
	 * @param channel
	 * @return
	 */
	public List<Tchannel> searchTchannels(Channel channel);
	
	/**
	 * 更换通道状态
	 * @param id
	 * @param status
	 */
	public void updateStatus(Long id,Integer status);
	
	/**
	 * 更改通道的配置信息
	 * @param c
	 */
	public void updateConfig(Channel c);

	public Channel getChannelByTransType(Integer transType);

	public Tchannel getTchannelByTransType(Integer transType);

	public Tchannel getTchannelInCache(Long channelId);

	public List<Map<String, String>> findShowChannelLimit(Long userId, String agentId);
	
	
	public List<Map<String, String>> findShowChannelLimitZTC(Long userId, String agentId);

	/**
	 * 根据通道ID 获取通道配置
	 * 
	 * @param channelId
	 * @return
	 */
	public JSONObject getChannelConfig(Long channelId);
	

	public List<JSONObject> getAvailableChannelConfigByChannelName(String string);

	public void appendTodayAmt(Long channelId, BigDecimal amt);

	void removeTodayAmt(Long channelId);
	
	public void updateThroughChannel()throws Exception;
	
	public Map<String, String> getCountAmt();
	
	public void updateThroughChannelPA()throws Exception;

}
