package com.cn.flypay.service.trans.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.trans.TuserOrderOperationRecord;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Organization;
import com.cn.flypay.pageModel.trans.UserOrderOperationRecord;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.trans.UserOrderOperationRecordService;



@Service
public class UserOrderOperationRecordServiceImpl implements UserOrderOperationRecordService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private BaseDao<TuserOrderOperationRecord> userOrderOperationRecordDao;
	
	@Autowired
	private OrganizationService organizationService;

	@Override
	public List<UserOrderOperationRecord> dataGrid(UserOrderOperationRecord uoord, PageFilter ph) {
		List<UserOrderOperationRecord> ul = new ArrayList<UserOrderOperationRecord>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "from TuserOrderOperationRecord t";
		List<TuserOrderOperationRecord> l = new ArrayList<TuserOrderOperationRecord>();
		l = userOrderOperationRecordDao.find(hql + whereHql(uoord, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TuserOrderOperationRecord tuoor : l) {
			UserOrderOperationRecord uoor = new UserOrderOperationRecord();
			BeanUtils.copyProperties(tuoor, uoor);
			ul.add(uoor);
		}
		return ul;
	}

	@Override
	public Long count(UserOrderOperationRecord userOrderOperationRecord, PageFilter pf) {
		String hql = "select count(*) from TuserOrderOperationRecord t";
		return userOrderOperationRecordDao.count(hql);
	}

	@Override
	public UserOrderOperationRecord get(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	private String whereHql(UserOrderOperationRecord uoor, Map<String, Object> params) {
		String hql = "";
		if (uoor != null) {
			hql += " where 1=1 ";
			try {
				if (StringUtils.isNotBlank(uoor.getLoginName())) {
					hql += " and t.loginName = :loginName";
					params.put("loginName", uoor.getLoginName());
				}
				if (uoor.getOrganizationId() != null) {
					Organization org = organizationService.get(uoor.getOrganizationId());
					hql += " and t.organizationName = :organizationName";
					params.put("organizationName", org.getName());
				}
				if (StringUtils.isNotBlank(uoor.getOrderNum())) {
					hql += " and t.orderNum = :orderNum";
					params.put("orderNum", uoor.getOrderNum());
				}
				if (uoor.getTransPayType() != null) {
					hql += " and t.transPayType = :transPayType";
					params.put("transPayType", uoor.getTransPayType());
				}
				if (uoor.getOrderStatus() != null) {
					hql += " and t.orderStatus = :orderStatus";
					params.put("orderStatus", uoor.getOrderStatus());
				}
				if (uoor.getOrderType() != null) {
					hql += " and t.orderType = :orderType";
					params.put("orderType", uoor.getOrderType());
				}
			} catch (Exception e) {
				logger.equals(e);
			}
		}
		return hql;
	}

	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null) && (ph.getSort().equals("operationDatetime"))) {
			orderString = " order by t.operationDatetime " + ph.getOrder();
		} else if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}

}
