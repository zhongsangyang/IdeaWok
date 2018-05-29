package com.cn.flypay.service.sys;

import com.cn.flypay.model.sys.TinfoList;
import com.cn.flypay.pageModel.sys.InfoList;

public interface JiguangPushService {

	/**
	 * 向所有人发送系统公告
	 * 
	 * @return
	 */
	public String sendMsgInfo(InfoList infoList, String agentId);

	/**
	 * 发送个人公告给指定的人
	 * 
	 * @param infoList
	 * @return
	 */
	public String sendMsgInfoToPerson(TinfoList infoList);

	/**
	 * 发送个人公告给指定的人
	 * 
	 * @param infoList
	 * @return
	 */
	public String sendMsgInfoToPerson(InfoList infoList);

	String sendMsgSoundInfoToPerson(Long userId);

}
