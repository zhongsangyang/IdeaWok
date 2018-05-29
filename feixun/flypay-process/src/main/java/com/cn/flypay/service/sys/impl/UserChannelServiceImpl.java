package com.cn.flypay.service.sys.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.TuserChannel;
import com.cn.flypay.model.sys.TuserChannelId;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.UserChannelService;

@Service
public class UserChannelServiceImpl implements UserChannelService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private BaseDao<TuserChannel> userChannelDao;
	@Autowired
	private ChannelService channelService;

	@Override
	public Boolean isAvlUserChannel(Long userId, Long channelId) {
		TuserChannelId tid = new TuserChannelId(userId, channelId);
		TuserChannel tuc = userChannelDao.get(TuserChannel.class, tid);
		Tchannel tc = channelService.getTchannelInCache(channelId);
		/* 通道最大使用次数小于等于用户使用过的次数，不允许用户再次使用该通道 */
		if (tuc != null && tc.getMaxNumPerPersonPerDay() <= tuc.getNumPerDay()) {
			return false;
		}
		return true;
	}

	@Override
	public Boolean updateUserChannel(Long userId, Long channelId) {
		TuserChannelId tid = new TuserChannelId(userId, channelId);
		TuserChannel tuc = userChannelDao.get(TuserChannel.class, tid);
		Tchannel tc = channelService.getTchannelInCache(channelId);
		if (tuc == null) {
			tuc = new TuserChannel();
			tuc.setId(tid);
		} else {
			/* 通道最大使用次数小于等于用户使用过的次数，不允许用户再次使用该通道 */
			if (tc.getMaxNumPerPersonPerDay() <= tuc.getNumPerDay()) {
				return false;
			}
		}
		tuc.setNumPerDay(tuc.getNumPerDay() + 1);
		userChannelDao.saveOrUpdate(tuc);
		return true;
	}

	@Override
	public void dealClearUserChannel() {
		int num = userChannelDao.executeHql("update TuserChannel  set numPerDay=0 where numPerDay!=0");
		logger.info("用户通道数据总计更新了" + num + "条");
	}
}
