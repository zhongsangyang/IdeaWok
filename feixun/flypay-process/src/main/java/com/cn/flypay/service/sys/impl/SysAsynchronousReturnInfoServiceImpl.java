package com.cn.flypay.service.sys.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TsysAsynchronousReturnInfo;
import com.cn.flypay.pageModel.sys.SysAsynchronousReturnInfo;
import com.cn.flypay.service.sys.SysAsynchronousReturnInfoService;
@Service
public class SysAsynchronousReturnInfoServiceImpl  implements  SysAsynchronousReturnInfoService{
	@Autowired
	private BaseDao<TsysAsynchronousReturnInfo> tsysAsynchronousReturnInfoDao;
	
	
	@Override
	public void save(SysAsynchronousReturnInfo s) {
		TsysAsynchronousReturnInfo t =new TsysAsynchronousReturnInfo();
		BeanUtils.copyProperties(s, t);
		tsysAsynchronousReturnInfoDao.save(t);
	}

}
