package com.cn.flypay.service.payment;

import org.apache.poi.ss.usermodel.Workbook;

public interface ZanshanfuExpenseService {

	/**
	 * 1，系统组装T1代付文件，发送给平安
	 * 
	 * @param dateyyyyMMdd
	 * @throws Exception
	 */
	public Workbook exportT1Zanshanfu(String dateyyyyMMdd) throws Exception;

}
