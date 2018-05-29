package com.cn.flypay.service.sys;

import com.cn.flypay.model.sys.TcreditCardReport;

public interface CreditCardReportService {

	public TcreditCardReport get(String orderNo);

	public void saveOrUpdate(TcreditCardReport cardReport);

}
