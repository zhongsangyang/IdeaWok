package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tholiday;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Holiday;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;
import com.cn.flypay.service.sys.HolidayService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

@Service
public class HolidayServiceImpl implements HolidayService {

	@Autowired
	private BaseDao<Tholiday> holidayDao;
	@Autowired
	private SysParamService sysParamService;

	@Override
	public void edit(Holiday u) {
		Tholiday t = holidayDao.get(Tholiday.class, u.getId());
		t.setContent(u.getContent());
		t.setIsHoliday(u.getIsHoliday());
		t.setUpdater(u.getUpdater());
		t.setUpdateTime(new Date());
		holidayDao.update(t);
	}

	@Override
	public Holiday get(Long id) {
		Tholiday t = holidayDao.get(Tholiday.class, id);
		Holiday u = new Holiday();
		BeanUtils.copyProperties(t, u);
		return u;
	}

	@Override
	public List<Holiday> dataGrid(Holiday param, PageFilter ph) {
		List<Holiday> ul = new ArrayList<Holiday>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from Tholiday t ";
		List<Tholiday> l = holidayDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(),
				ph.getRows());
		for (Tholiday t : l) {
			Holiday u = new Holiday();
			BeanUtils.copyProperties(t, u);
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(Holiday param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from Tholiday t ";
		return holidayDao.count("select count(*) " + hql + whereHql(param, params), params);
	}

	private String whereHql(Holiday param, Map<String, Object> params) {
		String hql = "";
		if (param != null) {
			hql += " where 1=1 ";
			try {
				if (StringUtil.isNotBlank(param.getDateStart())) {
					hql += " and t.holidayDate >= :dateStart";
					params.put("dateStart", DateUtil.getDateFromString(param.getDateStart()));
				}
				if (StringUtil.isNotBlank(param.getDateEnd())) {
					hql += " and t.holidayDate <= :dateEnd";
					params.put("dateEnd", DateUtil.getDateFromString(param.getDateEnd()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return hql;
	}

	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}

	@Override
	public void editHoliday(Long id) {

		Tholiday t = holidayDao.get(Tholiday.class, id);
		if (t.getIsHoliday() == 0) {
			t.setIsHoliday(1);
		} else {
			t.setIsHoliday(0);
		}
		t.setUpdateTime(new Date());
		holidayDao.update(t);
	}

	@Override
	public Date getWorkDateBeforeDate(Date date, int interval) {
		String hql = "select t from Tholiday t where t.isHoliday=0 and t.holidayDate<:date  order by t.id desc";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		List<Tholiday> workDates = holidayDao.find(hql, params);
		Tholiday sqlDate = workDates.get(interval - 1);
		return sqlDate.getHolidayDate();
	}

	@Override
	public Date getWorkDateAfterDate(Date date, int interval) {
		String hql = "select t from Tholiday t where t.isHoliday=0 and t.holidayDate>:date  order by t.id ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		List<Tholiday> workDates = holidayDao.find(hql, params);
		Tholiday sqlDate = workDates.get(interval - 1);
		return sqlDate.getHolidayDate();
	}

	@Override
	public Boolean isWorkDate(Date date) {
		String hql = "select t from Tholiday t where t.isHoliday=0 and t.holidayDate>=:beforeDate and t.holidayDate<=:afterDate";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("beforeDate", DateUtil.getBeforeDate(date, 0));
		params.put("afterDate", DateUtil.getEndOfDay(date));
		List<Tholiday> workDates = holidayDao.find(hql, params);
		return workDates.size() > 0 ? true : false;
	}

	@Override
	public Date getWorkDate(Date date) {
		if (isWorkDate(date)) {
			return DateUtil.getBeforeDate(date, 0);
		}
		return getWorkDateBeforeDate(date, 1);
	}

	@Override
	public Date[] getT0StartAndEndStatementWorkDate(Date stsDate) {
		Date[] dts = new Date[2];
		Date startDate = getWorkDateBeforeDate(stsDate, 1);
		Date endDate = DateUtil.getBeforeDate(stsDate, 0);
		Boolean isworkDate1 = isWorkDate(startDate);

		Boolean isworkDate = isWorkDate(endDate);
		Date t1startDate = stsDate;
		if (isworkDate1) {
			t1startDate = DateUtil.getHoursbyInterval(startDate, -1);
		} else {
			if (isworkDate) {
				startDate = getWorkDateBeforeDate(startDate, 1);
			} else {
				startDate = getWorkDateBeforeDate(startDate, 2);
			}
			t1startDate = DateUtil.getHoursbyInterval(startDate, 23);
		}

		Date t1EndDate = stsDate;
		if (isworkDate) {
			t1EndDate = DateUtil.getHoursbyInterval(endDate, -1);
		} else {
			t1EndDate = DateUtil.getHoursbyInterval(getWorkDateBeforeDate(endDate, 1), 23);
		}

		dts[0] = t1startDate;
		dts[1] = t1EndDate;
		return dts;
	}

	@Override
	public Date[] getT0StartAndEndStatementWorkDate2(Date stsDate) {
		Date[] dts = new Date[2];
		Date startDate = getWorkDateBeforeDate(stsDate, 1);
		Date endDate = DateUtil.getBeforeDate(stsDate, 0);
		Boolean isworkDate1 = isWorkDate(startDate);

		Boolean isworkDate = isWorkDate(endDate);
		Date t1startDate = stsDate;
		if (isworkDate1) {
			t1startDate = DateUtil.getHoursbyInterval(startDate, -1);
		} else {
			if (isworkDate) {
				startDate = getWorkDateBeforeDate(startDate, 1);
			} else {
				startDate = getWorkDateBeforeDate(startDate, 2);
			}
			t1startDate = DateUtil.getHoursbyInterval(startDate, 23);
		}

		Date t1EndDate = stsDate;
		if (isworkDate) {
			t1EndDate = DateUtil.getHoursbyInterval(endDate, -1);
		} else {
			t1EndDate = DateUtil.getHoursbyInterval(getWorkDateBeforeDate(endDate, 1), 23);
		}

		dts[0] = t1startDate;
		dts[1] = t1EndDate;
		return dts;
	}

	@Override
	public Date[] getT1StartAndEndStatementWorkDate(Date stsDate) {
		Date startDate = getWorkDateBeforeDate(stsDate, 1);
		Date[] dates = getT0StartAndEndStatementWorkDate(startDate);
		if (!isWorkDate(DateUtil.getHoursbyInterval(dates[0], 24))) {
			dates[0] = DateUtil.getHoursbyInterval(dates[0], -24);
		}
		return dates;
	}

	@Override
	public String isLimitTimeInterval(Date date, Boolean isInput, String transType) {
		String flag = GlobalConstant.RESP_CODE_SUCCESS;
		Map<String, String> params = sysParamService.searchSysParameter();
		String settle_out_holiday_flag = params.get("settle_out_holiday_flag");
		if ("0".equals(settle_out_holiday_flag) && !isInput && !isWorkDate(date)
				&& UserSettlementConfig.settlement_type.T0.name().equals(transType)) {
			flag = "非工作日期间，您无法快速提现";
		}

		String limitDateInterval = isInput ? params.get("settle_input_time") : params.get("settle_out_time");
		String startStr = DateUtil.getDateTime("yyyy-MM-dd", date);
		String limitDate = limitDateInterval.split("-")[0];
		Date limitStart = DateUtil.getDateFromString(startStr + " " + limitDate, "yyyy-MM-dd HH:mm:ss");
		Date todayEnd = DateUtil.getEndOfDay(date);
		boolean min = DateUtil.isBetweenDate(date, limitStart, todayEnd);

		Integer intervalMiniter = Integer.parseInt(limitDateInterval.split("-")[1]);
		String endDateStr = DateUtil.getDateTime("HH:mm:ss", DateUtil.getMinutebyInterval(limitStart, intervalMiniter));
		Date endLimit = DateUtil.getDateFromString(startStr + " " + endDateStr, "yyyy-MM-dd HH:mm:ss");
		boolean min2 = DateUtil.isBetweenDate(date, DateUtil.getStartOfDay(date), endLimit);

		if (min || min2) {
			flag = "系统正在进行账务核对，请您在" + endDateStr + "至" +  limitDate+ "再操作";
		}
		return flag;
	}

	@Override
	public boolean isWork() {
		if(!isWorkDate(new Date())){
			return true;
		}
		return false;
	}

	@Override
	public boolean isD0Work() {
		Map<String, String> params = sysParamService.searchSysParameter();
		String settle_out_holiday_flag = params.get("createPay_D0_seift_on");
		if ("0".equals(settle_out_holiday_flag)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean getisYILIAND0Work() {
		if(sysParamService.getD0YINLIAN()){
			return false;
		}
		return true;
	}
	
	
	@Override
	public boolean getisThroughD0Work() {
		if(sysParamService.getThrough("createPay_throughD0_seift_on")){
			return false;
		}
		return true;
	}
	
	@Override
	public boolean getisZTCD0Work() {
		if(sysParamService.getThrough("createPay_YILIAND0_seift_on")){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean getisThroughYLD0Work() {
		if(sysParamService.getThrough("createPay_throughYLD0_seift_on")){
			return false;
		}
		return true;
	}
	
	
	@Override
	public boolean getisThroughT1Work() {
		if(sysParamService.getThrough("createPay_throughT1_seift_on")){
			return false;
		}
		return true;
	}
	
	
	
}
