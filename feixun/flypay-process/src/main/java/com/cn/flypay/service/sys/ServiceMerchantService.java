package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.model.sys.TserviceMerchant;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.ServiceMerchant;

public interface ServiceMerchantService {

	public List<ServiceMerchant> dataGrid(ServiceMerchant param, PageFilter ph);

	public Long count(ServiceMerchant param, PageFilter ph);

	/**
	 * 获取所有的服务商
	 * 
	 * @return
	 */
	public List<ServiceMerchant> findAllServiceMerchant();

	/**
	 * 根据类型查询
	 * 
	 * @param serviceName
	 * @return
	 */
	public List<ServiceMerchant> findAllServiceMerchant(String serviceName);

	public TserviceMerchant getTserviceMerchant(Long serviceMerchantId);

	public void editServiceMerchantStatus(Long id);

	public ServiceMerchant findServiceMerchant(String serviceName);

}
