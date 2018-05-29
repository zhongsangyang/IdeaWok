package com.cn.flypay.service.payment;

import com.cn.flypay.model.sys.TYiQiang2MerchantReport;

public interface YiQiangPointService {
	
	
	public TYiQiang2MerchantReport findByUserId(Long userId);

}
