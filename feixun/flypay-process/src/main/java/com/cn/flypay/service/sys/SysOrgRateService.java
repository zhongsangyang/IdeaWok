package com.cn.flypay.service.sys;

import com.cn.flypay.model.sys.TsysOrgRate;

public interface SysOrgRateService {
	
	public  TsysOrgRate getSysOrgRate(String agentId,String orgType);

}
