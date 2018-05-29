package com.cn.flypay.service.common;

import com.cn.flypay.pageModel.sys.InfoList;

public interface ProducerService {

	/**
	 * 发送推送消息进MQ
	 * 
	 * @param infoList
	 */
	public void sendInfoList(InfoList infoList);

}
