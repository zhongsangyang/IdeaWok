package com.cn.flypay.service.payment.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.trans.TpinganFileDeal;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.PinganFileDeal;
import com.cn.flypay.service.payment.PinganFileDealService;

@Service
public class PinganFileDealServiceImpl implements PinganFileDealService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BaseDao<TpinganFileDeal> pingFileDealDao;

	@Override
	public List<PinganFileDeal> dataGrid(PinganFileDeal param, PageFilter ph) {
		List<PinganFileDeal> ul = new ArrayList<PinganFileDeal>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TpinganFileDeal t ";
		List<TpinganFileDeal> l = pingFileDealDao.find(hql + whereHql(param, params) + orderHql(ph), params,
				ph.getPage(), ph.getRows());
		for (TpinganFileDeal t : l) {
			PinganFileDeal u = new PinganFileDeal();
			BeanUtils.copyProperties(t, u);
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(PinganFileDeal param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TpinganFileDeal t ";
		return pingFileDealDao.count("select count(*) " + hql + whereHql(param, params), params);
	}

	private String whereHql(PinganFileDeal param, Map<String, Object> params) {
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
	public TpinganFileDeal getTpinganFileDeal(Long id) {

		return pingFileDealDao.get(TpinganFileDeal.class, id);
	}

	@Override
	public PinganFileDeal get(Long id) {
		TpinganFileDeal t = pingFileDealDao.get(TpinganFileDeal.class, id);
		if (t != null) {
			PinganFileDeal pfd = new PinganFileDeal();
			BeanUtils.copyProperties(t, pfd);
			return pfd;
		}
		return null;
	}

	@Override
	public TpinganFileDeal getWaitPayCommandFileDealByFileName(String fileName, String fileStatus) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fileName", fileName);
		params.put("status", fileStatus);
		return pingFileDealDao.get("select t from TpinganFileDeal t where t.fileName=:fileName and t.status=:status",
				params);
	}

	@Override
	public TpinganFileDeal getWaitDownFileDealByFileName(String fileName, String fileStatus) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fileName", fileName);
		params.put("status", fileStatus);

		return pingFileDealDao
				.get("select t from TpinganFileDeal t left join t.fileDeal f where f.fileName=:fileName and t.status=:status",
						params);
	}

	@Override
	public TpinganFileDeal getFileDealByFileName(String fileName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fileName", fileName);
		return pingFileDealDao.get("select t from TpinganFileDeal t where t.fileName=:fileName ", params);
	}

	@Override
	public TpinganFileDeal getPayFileDealByFileName(String fileName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fileName", fileName);

		return pingFileDealDao.get("select t from TpinganFileDeal t left join t.fileDeal f where f.fileName=:fileName",
				params);
	}

}
