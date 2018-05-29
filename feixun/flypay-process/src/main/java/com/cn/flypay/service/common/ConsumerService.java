package com.cn.flypay.service.common;

import javax.jms.JMSException;

import com.cn.flypay.pageModel.sys.InfoList;

public interface ConsumerService {

	public void dealReceiveInfoList(InfoList infoList) throws JMSException;

}
