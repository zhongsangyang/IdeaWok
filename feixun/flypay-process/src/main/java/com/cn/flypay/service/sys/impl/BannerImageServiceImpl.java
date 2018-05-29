package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TbannerImage;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.BannerImage;
import com.cn.flypay.service.sys.BannerImageService;
import com.cn.flypay.utils.StringUtil;

@Service
public class BannerImageServiceImpl implements BannerImageService {

	private static final Logger LOG = LoggerFactory.getLogger(BannerImageServiceImpl.class);

	@Autowired
	private BaseDao<TbannerImage> bannerImageDao;

	@Override
	public List<BannerImage> dataGrid(BannerImage param, PageFilter ph) {
		List<BannerImage> banners = new ArrayList<BannerImage>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TbannerImage t ";
		List<TbannerImage> l = bannerImageDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TbannerImage t : l) {
			BannerImage banner = new BannerImage();
			BeanUtils.copyProperties(t, banner);
			banners.add(banner);
		}
		return banners;
	}

	private String whereHql(BannerImage banner, Map<String, Object> params) {
		String hql = "";
		if (banner != null) {
			hql += " where 1=1 ";
			if (StringUtil.isNotBlank(banner.getStatus())) {
				hql += " and t.status=:status ";
				params.put("status", banner.getStatus());
			}
			if (StringUtil.isNotBlank(banner.getName())) {
				hql += " and t.name=:name ";
				params.put("name", banner.getName());
			}
			if (StringUtil.isNotBlank(banner.getCode())) {
				hql += " and t.code=:code ";
				params.put("code", banner.getCode());
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
	public Long count(BannerImage param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TbannerImage t ";
		return bannerImageDao.count("select count(t.id) " + hql + whereHql(param, params), params);
	}

	@Override
	public void add(BannerImage param) {
		TbannerImage bannerImage = new TbannerImage();
		BeanUtils.copyProperties(param, bannerImage);
		bannerImage.setCreateTime(new Date());
		LOG.info("Add BannerImage name={},status={}", bannerImage.getName(), bannerImage.getStatus());
		bannerImageDao.save(bannerImage);
	}

	@Override
	public void edit(BannerImage param) {
		TbannerImage t = bannerImageDao.get(TbannerImage.class, param.getId());
		// t.setAppType(appversion.getAppType());
		// t.setContent(appversion.getContent());
		// t.setStatus(appversion.getStatus());
		// t.setUpdateUrl(appversion.getUpdateUrl());
		// t.setVersionName(appversion.getVersionName());
		// t.setDownloadNet(appversion.getDownloadNet());
		// t.setIsForce(appversion.getIsForce());
		t.setName(param.getName());
		t.setStatus(param.getStatus());
		t.setActionUrl(param.getActionUrl());
		bannerImageDao.update(t);
	}

	@Override
	public void delete(Long id) {
	}

	@Override
	public BannerImage get(Long id) {
		TbannerImage t = bannerImageDao.get(TbannerImage.class, id);
		if (t != null) {
			BannerImage bi = new BannerImage();
			BeanUtils.copyProperties(t, bi);
			return bi;
		}
		return null;
	}
}
