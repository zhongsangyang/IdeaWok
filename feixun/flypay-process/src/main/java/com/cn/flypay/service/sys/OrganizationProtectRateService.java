package com.cn.flypay.service.sys;

import com.cn.flypay.model.sys.TOrganizationProtectRate;
import com.cn.flypay.pageModel.sys.OrganizationProtectRate;
/**
 * 运营商保本费率相关
 * @author liangchao
 *
 */
public interface OrganizationProtectRateService {

	public void add(OrganizationProtectRate r) throws Exception;
	
	public TOrganizationProtectRate getTOrganizationProtectRate(Long  id,String type);
}
