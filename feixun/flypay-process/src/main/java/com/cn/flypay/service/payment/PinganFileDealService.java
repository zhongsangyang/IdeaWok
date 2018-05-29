package com.cn.flypay.service.payment;

import java.util.List;

import com.cn.flypay.model.trans.TpinganFileDeal;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.PinganFileDeal;

public interface PinganFileDealService {

	public List<PinganFileDeal> dataGrid(PinganFileDeal statement, PageFilter ph);

	public Long count(PinganFileDeal statement, PageFilter ph);
	
	

	public TpinganFileDeal getTpinganFileDeal(Long id);

	public TpinganFileDeal getWaitPayCommandFileDealByFileName(String fileName, String status);

	public TpinganFileDeal getFileDealByFileName(String fileName);

	public PinganFileDeal get(Long id);

	/**
	 * 获取待下载的文件
	 * 
	 * @param convertDateStrYYYYMMDD
	 * @param name
	 * @return
	 */
	public TpinganFileDeal getWaitDownFileDealByFileName(String convertDateStrYYYYMMDD, String name);

	public TpinganFileDeal getPayFileDealByFileName(String fileName);
}
