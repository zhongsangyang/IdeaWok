package com.cn.flypay.service.sys;

import java.util.Date;
import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Holiday;

public interface HolidayService {

	public List<Holiday> dataGrid(Holiday param, PageFilter ph);

	public Long count(Holiday param, PageFilter ph);

	public void edit(Holiday param);

	public Holiday get(Long id);

	public void editHoliday(Long id);

	public Date getWorkDate(Date date);

	public Date getWorkDateBeforeDate(Date date, int interval);

	public Date getWorkDateAfterDate(Date date, int interval);

	public Boolean isWorkDate(Date date);

	public Date[] getT0StartAndEndStatementWorkDate(Date stsDate);

	public Date[] getT1StartAndEndStatementWorkDate(Date stsDate);

	/**
	 * 判断用户操作时间是否满足条件
	 * 
	 * @param date
	 *            判断日期
	 * @param isInput
	 *            true 收款， false 付款
	 * @param transType
	 *            T0 及时到账， T1隔日到账
	 * @return
	 */
	public String isLimitTimeInterval(Date date, Boolean isInput, String transType);
	
	/**
	 * 是否工作日
	 * @return
	 */
	public boolean isWork();

	Date[] getT0StartAndEndStatementWorkDate2(Date stsDate);
	
	/**
	 * DO收款开关
	 * @return
	 */
	public boolean isD0Work();
	
	
	/**
	 * 银联DO收款开关
	 * @return
	 */
	public boolean getisYILIAND0Work();
	
	/**
	 * 银联DO收款开关
	 * @return
	 */
	public boolean getisThroughD0Work();
	
	/**
	 * 银联DO收款开关
	 * @return
	 */
	public boolean getisThroughYLD0Work();
	
	
	/**
	 * 银联DO收款开关
	 * @return
	 */
	public boolean getisThroughT1Work();
	
	
	/**
	 * 银联DO收款开关
	 * @return
	 */
	public boolean getisZTCD0Work();
	
	

}
