package com.cn.flypay.service.sys;

public interface UserChannelService {

	/**
	 * 更新用户使用通道记录次数
	 */
	public Boolean updateUserChannel(Long userId, Long channelId);

	public void dealClearUserChannel();

	Boolean isAvlUserChannel(Long userId, Long channelId);
}
