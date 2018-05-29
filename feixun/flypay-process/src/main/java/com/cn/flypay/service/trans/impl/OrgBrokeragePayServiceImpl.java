package com.cn.flypay.service.trans.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.TorgBrokerage;
import com.cn.flypay.model.trans.TorgBrokeragePay;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.OrgBrokeragePay;
import com.cn.flypay.service.trans.OrgBrokeragePayService;
import com.cn.flypay.utils.StringUtil;

@Service
public class OrgBrokeragePayServiceImpl implements OrgBrokeragePayService {

	@Autowired
	private BaseDao<TorgBrokeragePay> orgPayDao;

	@Autowired
	private BaseDao<TorgBrokerage> orgDao;

	@Autowired
	private BaseDao<Tuser> userDao;

	@Override
	public List<OrgBrokeragePay> dataGrid(OrgBrokeragePay app, PageFilter ph) {
		List<OrgBrokeragePay> ul = new ArrayList<OrgBrokeragePay>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TOrgBrokeragePay t left join t.orgBrokerage b left join b.organization bor left join t.user";
		List<TorgBrokeragePay> l = orgPayDao.find(hql + whereHql(app, params) + orderHql(ph), params, ph.getPage(),
				ph.getRows());
		for (TorgBrokeragePay t : l) {
			OrgBrokeragePay u = new OrgBrokeragePay();
			BeanUtils.copyProperties(t, u);
			if (t.getOrgBrokerage() != null && t.getOrgBrokerage().getOrganization() != null) {
				u.setOrganizationName(t.getOrgBrokerage().getOrganization().getName());
			}
			if (t.getUser() != null) {
				u.setUserName(t.getUser().getName());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(OrgBrokeragePay app, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from  TOrgBrokeragePay t left join t.orgBrokerage b left join b.organization bor";
		return orgPayDao.count("select count(t) " + hql + whereHql(app, params), params);
	}

	private String whereHql(OrgBrokeragePay app, Map<String, Object> params) {
		String hql = "";
		if (app != null) {
			hql += " where 1=1 ";
			if (StringUtil.isNotBlank(app.getOrganizationName())) {
				hql += " and bor.name like :organizationName";
				params.put("organizationName", "%%" + app.getOrganizationName() + "%%");
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
	public String add(OrgBrokeragePay orgBrokeragePay, Long userId) {
		String excetionStr = null;
		try {
			TorgBrokeragePay t = new TorgBrokeragePay();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orgId", orgBrokeragePay.getOrgId());
			TorgBrokerage tob = orgDao.get(
					"select  t from TOrgBrokerage t left join t.organization tor where tor.id=:orgId", params);
			t.setOldAmt(tob.getBrokerage());
			/*t.setAmt(orgBrokeragePay.getAmt());TODO 08/02
			t.setOrgBrokerage(tob);
			t.setPayDatetime(new Date());
			Tuser tu = userDao.get(Tuser.class, userId);
			t.setUser(tu);
			if (tob.getBrokerage() - orgBrokeragePay.getAmt() > 0) {
				t.setStatus(1);
				tob.setBrokerage(tob.getBrokerage() - orgBrokeragePay.getAmt());
				orgDao.update(tob);
			} else {
				excetionStr = "提现金额大于可用金额，提现失败！";
				t.setStatus(0);
			}*/
			orgPayDao.save(t);
		} catch (Exception e) {
			excetionStr = "提现失败！";
		}
		return excetionStr;
	}

}
