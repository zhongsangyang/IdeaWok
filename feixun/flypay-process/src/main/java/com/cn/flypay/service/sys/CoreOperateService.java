package com.cn.flypay.service.sys;

import java.util.Date;
import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.CoreOperate;

public interface CoreOperateService {

	public List<CoreOperate> dataGrid(CoreOperate param, PageFilter ph);

	public Long count(CoreOperate param, PageFilter ph);

	public void add(CoreOperate param);

	public void delete(Long id);

	public void edit(CoreOperate param);

	public CoreOperate get(Long id);

	public Boolean isT1T0AvlByDate(String date);

	/**
	 * 
	 * @param date
	 *            yyyyMMdd
	 */
	public String updateCoreOperateByDate(Date date, String operator);
	
	/**
	 * 
	 * @param date
	 *            yyyyMMdd
	 */
	public String updateCoreOperateByDateTwo(Date date, String operator);

}
