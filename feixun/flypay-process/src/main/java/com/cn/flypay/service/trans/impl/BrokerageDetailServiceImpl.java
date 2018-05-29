package com.cn.flypay.service.trans.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.trans.TbrokerageDetail;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.BrokerageDetail;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.trans.BrokerageDetailService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

@Service
public class BrokerageDetailServiceImpl implements BrokerageDetailService {

	@Autowired
	private BaseDao<TbrokerageDetail> brokerageDetailDao;
	@Autowired
	private OrganizationService organizationService;

	@Override
	public List<BrokerageDetail> dataGrid(BrokerageDetail brokerageDetail, PageFilter ph) {
		List<BrokerageDetail> ul = new ArrayList<BrokerageDetail>();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			String hql = " select t from TbrokerageDetail t left join t.tbrokerage ttb  left join ttb.user tu left join t.brokerageUser tbu left join tu.organization tuo";
			List<TbrokerageDetail> l = brokerageDetailDao.find(hql + whereHql(brokerageDetail, params) + orderHql(ph),
					params, ph.getPage(), ph.getRows());
			for (TbrokerageDetail t : l) {
				BrokerageDetail u = new BrokerageDetail();
				BeanUtils.copyProperties(t, u);
				if (t.getBrokerage() != null && t.getTbrokerage().getUser() != null) {
					u.setUserCode(t.getUserCode());
					u.setUserName(t.getPhone());
					u.setUserType(t.getTbrokerage().getUser().getUserType());
					u.setPhone(t.getPhone());
					if (t.getTbrokerage().getUser().getOrganization() != null) {
						u.setOrganizationName(t.getTbrokerage().getUser().getOrganization().getName());
					}
				}
				if (t.getBrokerageUser() != null) {
					u.setBrokerageUserCode(t.getBrokerageUser().getCode());
					u.setBrokerageUserName(t.getBrokerageUser().getName());
					u.setBrokerageUserId(t.getBrokerageUser().getId());
				}
				ul.add(u);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ul;
	}

	@Override
	public List<BrokerageDetail> dataGridTwo(BrokerageDetail brokerageDetail, PageFilter ph) {
		List<BrokerageDetail> ul = new ArrayList<BrokerageDetail>();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", brokerageDetail.getBrokerageUserId());
			params.put("BROKERAGETYPE", brokerageDetail.getBrokerageType());
			String hql = " select t from TbrokerageDetail t left join t.brokerageUser u where u.id=:userId and t.brokerageType=:BROKERAGETYPE ";
			List<TbrokerageDetail> l = brokerageDetailDao.find(hql + orderHql(ph), params, ph.getPage(), ph.getRows());
			for (TbrokerageDetail t : l) {
				BrokerageDetail u = new BrokerageDetail();
				BeanUtils.copyProperties(t, u);
				if (t.getBrokerage() != null && t.getTbrokerage().getUser() != null) {
					u.setUserCode(t.getUserCode());
					u.setUserName(t.getPhone());
					u.setUserType(t.getTbrokerage().getUser().getUserType());
					if (t.getTbrokerage().getUser().getOrganization() != null) {
						u.setOrganizationName(t.getTbrokerage().getUser().getOrganization().getName());
					}
				}
				if (t.getBrokerageUser() != null) {
					u.setBrokerageUserCode(t.getBrokerageUser().getCode());
					u.setBrokerageUserName(t.getBrokerageUser().getName());
				}
				ul.add(u);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ul;
	}

	@Override
	public String dataGridSum(BrokerageDetail brokerageDetail) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("BROKERAGEUSERID", brokerageDetail.getBrokerageUserId());
			// 之前的sql，设置了查询条件BROKERAGE_TYPE
			// String sql ="select sum(d.BROKERAGE) as BROKERAGE from trans_brokerage_detail
			// d where d.BROKERAGE_TYPE=:BROKERAGETYPE and
			// d.BROKERAGE_USER_ID=:BROKERAGEUSERID";
			// update：2017.11.17 修改为当BROKERAGE_TYPE=null时查询所有类型佣金
			String sql = "select sum(d.BROKERAGE) as BROKERAGE  from trans_brokerage_detail d where d.BROKERAGE_USER_ID=:BROKERAGEUSERID";
			if (brokerageDetail.getBrokerageType() != null) {
				sql += " and d.BROKERAGE_TYPE=:BROKERAGETYPE";
				params.put("BROKERAGETYPE", brokerageDetail.getBrokerageType());
			}
			if (brokerageDetail.getCreateDatetimeStart() != null) {
				sql += " and d.TRANS_DATETIME>=:starDate ";
				params.put("starDate", brokerageDetail.getCreateDatetimeStart());
			}
			if (brokerageDetail.getCreateDatetimeEnd() != null) {
				sql += " and d.TRANS_DATETIME<=:endDate ";
				params.put("endDate", brokerageDetail.getCreateDatetimeEnd());
			}
			List<Object[]> obj = brokerageDetailDao.findBySql(sql, params);
			if (obj.get(0) != null) {
				return String.valueOf(obj.get(0));
			}
			return "0";

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Long count(BrokerageDetail app, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "  from TbrokerageDetail t left join t.tbrokerage ttb  left join ttb.user tu left join t.brokerageUser tbu left join tu.organization tuo";
		return brokerageDetailDao.count("select count(t) " + hql + whereHql(app, params), params);
	}

	private String whereHql(BrokerageDetail bd, Map<String, Object> params) {
		String hql = "";
		if (bd != null) {
			hql += " where 1=1 ";
			if (StringUtil.isNotBlank(bd.getPhone())) {
				hql += " and t.phone =:phone";
				params.put("phone", bd.getPhone());
			}
			if (StringUtil.isNotBlank(bd.getPhone())) {
				hql += " and t.phone =:phone";
				params.put("phone", bd.getPhone());
			}
			if (StringUtil.isNotBlank(bd.getUserCode())) {
				hql += " and t.userCode = :userCode";
				params.put("userCode", bd.getUserCode());
			}
			if (StringUtil.isNotBlank(bd.getBrokerageUserCode())) {
				hql += " and tbu.loginName = :brokerageUserCode";
				params.put("brokerageUserCode", bd.getBrokerageUserCode());
			}
			if (bd.getBrokerageUserId() != null) {
				hql += " and tbu.id =:tbuId";
				params.put("tbuId", bd.getBrokerageUserId());
			}
			if (StringUtil.isNotBlank(bd.getSearchType())) {
				if ("T".equals(bd.getSearchType())) {
					hql += " and t.transDatetime >= :todayStart and t.transDatetime <= :todayEnd";
					params.put("todayStart", DateUtil.getStartOfDay(new Date()));
					params.put("todayEnd", DateUtil.getEndOfDay(new Date()));
				}
			}
			if (bd.getOperateUser() != null) {

				hql += " and  tuo.id in(:operaterOrgIds)";
				params.put("operaterOrgIds",
						organizationService.getOwerOrgIds(bd.getOperateUser().getOrganizationId()));
			}
			if (bd.getBrokerageType() != null) {
				hql += " and  t.brokerageType=:brokerageType";
				params.put("brokerageType", bd.getBrokerageType());
			}
			if (bd.getCreateDatetimeStart() != null) {
				hql += " and t.transDatetime >= :createDatetimeStart";
				params.put("createDatetimeStart", bd.getCreateDatetimeStart());
			}
			if (bd.getCreateDatetimeEnd() != null) {
				hql += " and t.transDatetime <= :createDatetimeEnd";
				params.put("createDatetimeEnd", bd.getCreateDatetimeEnd());
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
	public String getSum(Long suerId, Integer brokerage) {
		String sql = "select sum(d.BROKERAGE) as brokerage from trans_brokerage_detail d "
				+ "where d.BROKERAGE_USER_ID=' " + suerId + " ' and d.BROKERAGE_TYPE=' " + brokerage + " ' ";
		List<Object[]> obj = brokerageDetailDao.findBySql(sql);
		if (obj.get(0) != null) {
			return String.valueOf(obj.get(0));
		}
		return "0";
	}

}
