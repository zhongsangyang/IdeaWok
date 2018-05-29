package com.cn.flypay.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;

import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.service.common.ConsumerService;

public class ResponseQueueListener implements SessionAwareMessageListener<Message> {
	private Log logger = LogFactory.getLog(getClass());
	@Autowired
	private ConsumerService consumerService;

	@Override
	public void onMessage(Message message, Session session) throws JMSException {
		ObjectMessage objMessage = (ObjectMessage) message;
		try {
			InfoList infoList = (InfoList) objMessage.getObject();
			consumerService.dealReceiveInfoList(infoList);
		} catch (JMSException e) {
			logger.error("接收到发送到responseQueue的一个文本消息，内容是：" + message.toString());
			logger.error("推送消息失败", e);
			throw e;
		}
	}

}
