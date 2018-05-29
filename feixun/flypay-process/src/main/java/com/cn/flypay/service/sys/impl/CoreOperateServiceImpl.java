package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TcoreOperate;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.CoreOperate;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.sys.CoreOperateService;
import com.cn.flypay.service.sys.HolidayService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.task.UserTaskService;
import com.cn.flypay.utils.DateUtil;

@Service
public class CoreOperateServiceImpl implements CoreOperateService {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private BaseDao<TcoreOperate> coreOperateDao;
	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private UserTaskService userTaskService;
	@Autowired
	private HolidayService holidayService;

	@Autowired
	private AccountService accountService;

	@Override
	public List<CoreOperate> dataGrid(CoreOperate param, PageFilter ph) {
		List<CoreOperate> ul = new ArrayList<CoreOperate>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TcoreOperate t ";
		List<TcoreOperate> l = coreOperateDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TcoreOperate t : l) {
			CoreOperate u = new CoreOperate();
			BeanUtils.copyProperties(t, u);
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(CoreOperate param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TcoreOperate t  ";
		return coreOperateDao.count("select count(t.id) " + hql + whereHql(param, params), params);
	}

	private String whereHql(CoreOperate param, Map<String, Object> params) {
		String hql = "";
		if (param != null) {
			hql += " where 1=1 ";
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
	public void add(CoreOperate coreOperate) {
		TcoreOperate t = new TcoreOperate();
		BeanUtils.copyProperties(coreOperate, t);
		coreOperateDao.save(t);
	}

	@Override
	public void delete(Long id) {

	}

	@Override
	public void edit(CoreOperate coreOperate) {
		TcoreOperate t = coreOperateDao.get(TcoreOperate.class, coreOperate.getId());
		t.setT1PayFlag(coreOperate.getT1PayFlag());
		t.setT1ToAvlFlag(coreOperate.getT1ToAvlFlag());
		coreOperateDao.update(t);
	}

	@Override
	public CoreOperate get(Long id) {
		TcoreOperate t = coreOperateDao.get(TcoreOperate.class, id);
		if (t != null) {
			CoreOperate av = new CoreOperate();
			BeanUtils.copyProperties(t, av);
			return av;
		}
		return null;
	}

	@Override
	public Boolean isT1T0AvlByDate(String date) {
		Boolean flag = false;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dateTime", date);
		map.put("t1ToAvlFlag", 1);
		TcoreOperate t = coreOperateDao.get("select t from TcoreOperate t where t.dateTime=:dateTime and t.t1ToAvlFlag=:t1ToAvlFlag", map);
		if (t == null) {
			flag = true;
		}
		return flag;
	}

	@Override
	public String updateCoreOperateByDate(Date date, String operator) {
		String result = GlobalConstant.SUCCESS;
		Boolean flag = holidayService.isWorkDate(date);
		if (flag) {
			accountService.updateUserT1AccountToAvl();
			TcoreOperate t = new TcoreOperate(DateUtil.convertDateStrYYYYMMDD(date), 1, 0, 0, null, operator);
			coreOperateDao.save(t);
		} else {
			result = "今天是节假日，无需更新用户T1账户可用金额中";
		}
		log.info(result);
		return result;
	}
	
	
	@Override
	public String updateCoreOperateByDateTwo(Date date, String operator) {
		String result = GlobalConstant.SUCCESS;
		Boolean flag = holidayService.isWorkDate(date);
		if (flag) {
			//isT1T0AvlByDate(DateUtil.convertDateStrYYYYMMDD(date))
			accountService.updateUserT1AccountToAvlTwo();
			TcoreOperate t = new TcoreOperate(DateUtil.convertDateStrYYYYMMDD(date), 1, 0, 0, null, operator);
			coreOperateDao.save(t);
		} else {
			result = "今天是节假日，无需更新用户T1账户可用金额中";
		}
		log.info(result);
		return result;
	}
}
