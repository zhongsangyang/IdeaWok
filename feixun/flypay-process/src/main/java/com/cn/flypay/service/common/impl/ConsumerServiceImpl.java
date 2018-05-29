package com.cn.flypay.service.common.impl;

import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TinfoList;
import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.service.common.ConsumerService;
import com.cn.flypay.service.sys.InfoListService;
import com.cn.flypay.service.sys.JiguangPushService;

@Service
public class ConsumerServiceImpl implements ConsumerService {
	@Autowired
	private JiguangPushService jiguangPushService;
	@Autowired
	private BaseDao<TinfoList> infoListDao;
	@Autowired
	private InfoListService infoListService;

	@Override
	public void dealReceiveInfoList(InfoList infoList) throws JMSException {
		try {
			jiguangPushService.sendMsgInfoToPerson(infoList);
			infoListService.add(infoList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
