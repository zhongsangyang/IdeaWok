package com.cn.flypay.service.statement.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.trans.TorderStatement;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.statement.OrderStatement;
import com.cn.flypay.service.statement.OrderStatementService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

@Service
public class OrderStatementServiceImpl implements OrderStatementService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BaseDao<TorderStatement> stsDao;

	@Override
	public String saveTorderStatements(List<TorderStatement> sts) {
		Session session = stsDao.getCurrentSession();
		session.setCacheMode(CacheMode.IGNORE);
		int saveCount = 0;
		for (TorderStatement o : sts) {
			session.save(o);
			if (++saveCount % sts.size() == 0) {
				// session.clear();
				saveCount = 0;
			}
		}
		return GlobalConstant.RESP_CODE_SUCCESS;
	}

	@Override
	public List<OrderStatement> dataGrid(OrderStatement statement, PageFilter ph) {

		List<OrderStatement> ul = new ArrayList<OrderStatement>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TorderStatement t left join t.user u ";
		List<TorderStatement> l = stsDao.find(hql + whereHql(statement, params) + orderHql(ph), params, ph.getPage(),
				ph.getRows());
		for (TorderStatement t : l) {
			OrderStatement u = new OrderStatement();
			BeanUtils.copyProperties(t, u);
			if (t.getUser() != null) {
				u.setLoginName(t.getUser().getLoginName());
				u.setRealName(t.getUser().getRealName());
			}
			ul.add(u);
		}
		return ul;

	}

	@Override
	public Long count(OrderStatement statement, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "  from TorderStatement t  left join t.user u ";
		return stsDao.count("select count(t.id) " + hql + whereHql(statement, params), params);
	}

	private String whereHql(OrderStatement orderStatement, Map<String, Object> params) {
		String hql = "";
		if (orderStatement != null) {
			hql += " where 1=1 ";
			if (orderStatement.getStatementType() != null) {
				hql += " and t.statementType = :statementType";
				params.put("statementType", orderStatement.getStatementType());
			}
			if (StringUtil.isNotBlank(orderStatement.getLoginName())) {
				hql += " and u.loginName = :loginName";
				params.put("loginName", orderStatement.getLoginName());
			}
			try {
				if (StringUtil.isNotBlank(orderStatement.getStatementDateStart())) {
					hql += " and t.createTime >= :statementDateStart";
					params.put("statementDateStart", DateUtil.getDateFromString(orderStatement.getStatementDateStart()));
				}
				if (StringUtil.isNotBlank(orderStatement.getStatementDateEnd())) {
					hql += " and t.createTime <= :statementDateEnd";
					params.put("statementDateEnd",
							DateUtil.getEndOfDay(DateUtil.getDateFromString(orderStatement.getStatementDateEnd())));
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
}
