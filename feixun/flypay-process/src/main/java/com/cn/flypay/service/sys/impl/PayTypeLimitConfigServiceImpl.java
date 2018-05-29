package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TorgSysConfig;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.TpayTypeLimitConfig;
import com.cn.flypay.model.util.CollectionUtil;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.PayTypeLimitConfig;
import com.cn.flypay.service.sys.PayTypeLimitConfigService;
import com.cn.flypay.utils.StringUtil;

@Service
public class PayTypeLimitConfigServiceImpl implements PayTypeLimitConfigService {

	private static Logger LOG = LoggerFactory.getLogger(PayTypeLimitConfigServiceImpl.class);

	@Autowired
	private BaseDao<TpayTypeLimitConfig> payTypeLimitConfigDao;

	@Override
	public List<PayTypeLimitConfig> dataGrid(PayTypeLimitConfig param, PageFilter ph) {
		List<PayTypeLimitConfig> ul = new ArrayList<PayTypeLimitConfig>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TpayTypeLimitConfig t   left join t.organization g ";
		List<TpayTypeLimitConfig> l = payTypeLimitConfigDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TpayTypeLimitConfig t : l) {
			PayTypeLimitConfig u = new PayTypeLimitConfig();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrganizationName(t.getOrganization().getName());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(PayTypeLimitConfig param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TpayTypeLimitConfig t left join t.organization g  ";
		return payTypeLimitConfigDao.count("select count(t.id) " + hql + whereHql(param, params), params);
	}

	private String whereHql(PayTypeLimitConfig param, Map<String, Object> params) {
		String hql = "";
		if (param != null) {
			hql += " where 1=1 ";
			if (param.getOrganizationId() != null) {
				hql += " and g.id = :organizationId";
				params.put("organizationId", param.getOrganizationId());
			}
			if (param.getPayType() != null) {
				hql += " and t.payType = :payType";
				params.put("payType", param.getPayType());
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
	public PayTypeLimitConfig get(Long id) {
		TpayTypeLimitConfig t = payTypeLimitConfigDao.get(TpayTypeLimitConfig.class, id);
		PayTypeLimitConfig p = new PayTypeLimitConfig();
		BeanUtils.copyProperties(t, p);
		return p;
	}
	
	public TpayTypeLimitConfig findByCode(String code, Long orgId) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TpayTypeLimitConfig t  left join t.organization g  where t.code=:code t.status=1 and  g.id=:orgId ";
		params.put("code", code);
		params.put("orgId", orgId);
		List<TpayTypeLimitConfig> l = payTypeLimitConfigDao.find(hql, params);
		if(CollectionUtil.isEmpty(l) ) {
			return null;
		}
		return l.get(0);
	}
	
	@Override
	public void edit(PayTypeLimitConfig payTypeLimitConfig) {
		TpayTypeLimitConfig t = payTypeLimitConfigDao.get(TpayTypeLimitConfig.class, payTypeLimitConfig.getId());
		t.setMaxAmt(payTypeLimitConfig.getMaxAmt());
		t.setMinAmt(payTypeLimitConfig.getMinAmt());
		t.setSrvFee(payTypeLimitConfig.getSrvFee());
		t.setStatus(payTypeLimitConfig.getStatus());
		t.setName(payTypeLimitConfig.getName());
		t.setCode(payTypeLimitConfig.getCode());
		t.setStartTime(payTypeLimitConfig.getStartTime());
		t.setEndTime(payTypeLimitConfig.getEndTime());
		t.setPayTypeName(payTypeLimitConfig.getPayTypeName());
		t.setUnSupportCardName(payTypeLimitConfig.getUnSupportCardName());
		payTypeLimitConfigDao.update(t);
	}

	@Override
	public List<PayTypeLimitConfig> findPayTypeLimitConfigs(Set<Integer> transTypes, String agentId) {
		List<PayTypeLimitConfig> ul = new ArrayList<PayTypeLimitConfig>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TpayTypeLimitConfig t  left join t.organization g  where t.status=0 and  g.code=:agentId and t.payType in(:transTypes)";
		params.put("agentId", agentId);
		params.put("transTypes", transTypes);
		List<TpayTypeLimitConfig> l = payTypeLimitConfigDao.find(hql, params);
		for (TpayTypeLimitConfig t : l) {
			PayTypeLimitConfig u = new PayTypeLimitConfig();
			BeanUtils.copyProperties(t, u);
			ul.add(u);
		}
		return ul;
	}

	@Override
	public List<PayTypeLimitConfig> findPayTypeLimitConfigsFTC(Integer PayType, String agentId) {
		List<PayTypeLimitConfig> ul = new ArrayList<PayTypeLimitConfig>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = null;
		params.put("agentId", agentId);
		if (PayType == 580) {
			hql = " select t from TpayTypeLimitConfig t  left join t.organization g  where t.status=0 and  g.code=:agentId and t.payType in(500,550,551,552,520) and t.amtType !=100";
		} else {
			hql = " select t from TpayTypeLimitConfig t  left join t.organization g  where t.status=0 and  g.code=:agentId and t.payType=:transTypes and t.amtType !=100";
			params.put("transTypes", PayType);
		}

		List<TpayTypeLimitConfig> l = payTypeLimitConfigDao.find(hql, params);
		for (TpayTypeLimitConfig t : l) {
			PayTypeLimitConfig u = new PayTypeLimitConfig();
			BeanUtils.copyProperties(t, u);
			ul.add(u);
		}
		return ul;
	}

	@Override
	public List<PayTypeLimitConfig> findPayTypeLimitConfigsZTC(Set<Integer> transTypes, String agentId) {
		List<PayTypeLimitConfig> ul = new ArrayList<PayTypeLimitConfig>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TpayTypeLimitConfig t  left join t.organization g  where t.status=0 and  g.code=:agentId and t.payType in(:transTypes) and t.amtType=300";
		params.put("agentId", agentId);
		params.put("transTypes", transTypes);
		List<TpayTypeLimitConfig> l = payTypeLimitConfigDao.find(hql, params);
		for (TpayTypeLimitConfig t : l) {
			PayTypeLimitConfig u = new PayTypeLimitConfig();
			BeanUtils.copyProperties(t, u);
			ul.add(u);
		}
		return ul;
	}

	@Override
	public List<PayTypeLimitConfig> findPayTypeLimitConfigsXTC(Set<Integer> transTypes, String agentId) {
		List<PayTypeLimitConfig> ul = new ArrayList<PayTypeLimitConfig>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TpayTypeLimitConfig t  left join t.organization g  where t.status=0 and  g.code=:agentId and t.payType in(:transTypes) and t.amtType in(300,200) GROUP BY t.payType";
		params.put("agentId", agentId);
		params.put("transTypes", transTypes);
		List<TpayTypeLimitConfig> l = payTypeLimitConfigDao.find(hql, params);

		Set<Integer> payTypeSet = new HashSet<Integer>();
		for (TpayTypeLimitConfig t : l) {
			LOG.info("ChannelList payType={}", t.getPayType());
			PayTypeLimitConfig u = new PayTypeLimitConfig();
			if (t.getPayType() == 500) {
				payTypeSet.add(580);
			} else if (t.getPayType() == 550) {
				payTypeSet.add(580);
			} else if (t.getPayType() == 520) {
				payTypeSet.add(580);
			} else if (t.getPayType() == 551) {
				payTypeSet.add(580);
			} else if (t.getPayType() == 552) {
				payTypeSet.add(580);
			} else {
				payTypeSet.add(t.getPayType());
			}
		}

		for (Integer payTypeInteger : payTypeSet) {
			PayTypeLimitConfig u = new PayTypeLimitConfig();
			u.setPayType(payTypeInteger);
			if (580 == payTypeInteger) {
				ul.add(0, u);
			} else {
				ul.add(u);
			}
		}
		return ul;
	}

	@Override
	public void initPayTypeLimitConfig(Torganization t) {
		List<TpayTypeLimitConfig> tscs = payTypeLimitConfigDao.find(" select t from TpayTypeLimitConfig t  left join t.organization g  where g.id=" + t.getOrganization().getId());
		for (TpayTypeLimitConfig tsc : tscs) {
			TpayTypeLimitConfig ntcs = new TpayTypeLimitConfig();
			BeanUtils.copyProperties(tsc, ntcs);
			ntcs.setId(null);
			ntcs.setOrganization(t);
			payTypeLimitConfigDao.save(ntcs);
		}
	}

	@Override
	public PayTypeLimitConfig getPayType(int payAmtType, String agentId, int payType) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("payAmtType", payAmtType);
		params.put("agentId", agentId);
		params.put("payType", payType);
		String hql = " select t from TpayTypeLimitConfig t  left join t.organization g " + " where g.code=:agentId and t.amtType=:payAmtType and t.payType=:payType";
		TpayTypeLimitConfig t = payTypeLimitConfigDao.get(hql, params);
		PayTypeLimitConfig p = new PayTypeLimitConfig();
		BeanUtils.copyProperties(t, p);
		return p;
	}
}
