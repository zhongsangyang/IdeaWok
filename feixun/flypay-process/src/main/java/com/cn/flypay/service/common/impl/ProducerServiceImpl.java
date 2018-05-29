package com.cn.flypay.service.common.impl;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.stereotype.Service;

import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.service.common.ProducerService;

@Service
public class ProducerServiceImpl implements ProducerService {

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination infoQueueDestination;

	@Override
	public void sendInfoList(final InfoList infoList) {
		jmsTemplate.execute(new ProducerCallback<Object>() {
			public Object doInJms(Session session, MessageProducer producer) throws JMSException {
				Message message = session.createObjectMessage(infoList);
				producer.send(infoQueueDestination, message);
				return null;
			}
		});
	}
}
