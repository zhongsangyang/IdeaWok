package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TfeedBack;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.FeedBack;
import com.cn.flypay.service.sys.FeedBackService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.StringUtil;

@Service
public class FeedBackServiceImpl implements FeedBackService {

	@Autowired
	private BaseDao<TfeedBack> feedBackDao;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private UserService userService;

	@Override
	public List<FeedBack> dataGrid(FeedBack param, PageFilter ph) {
		List<FeedBack> ul = new ArrayList<FeedBack>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TfeedBack t left join t.user u  left join t.organization org ";
		List<TfeedBack> l = feedBackDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(),
				ph.getRows());
		for (TfeedBack t : l) {
			FeedBack u = new FeedBack();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrganizationName(t.getOrganization().getName());
				u.setOrganizationAppName(t.getOrganization().getAppName());
			}
			if (t.getUser() != null) {
				u.setUserName(t.getUser().getRealName());
				u.setUserPhone(t.getUser().getLoginName());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(FeedBack param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TfeedBack t  left join t.user u  left join t.organization org ";
		return feedBackDao.count("select count(*) " + hql + whereHql(param, params), params);
	}

	private String whereHql(FeedBack param, Map<String, Object> params) {
		String hql = "";
		if (param != null) {
			hql += " where 1=1 ";
			if (param.getOperateUser() != null) {
				hql += " and  org.id in(:operaterOrgIds)";
				params.put("operaterOrgIds",
						organizationService.getOwerOrgIds(param.getOperateUser().getOrganizationId()));
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
	public void edit(FeedBack feedBack) {
		TfeedBack t = feedBackDao.get(TfeedBack.class, feedBack.getId());
		feedBackDao.update(t);
	}

	@Override
	public FeedBack get(Long id) {
		return null;
	}

	@Override
	public void addFeedBack(Long userId, String msgCon, String agentId) {
		TfeedBack t = new TfeedBack();
		t.setContent(msgCon);
		t.setCreateTime(new Date());
		t.setUser(userService.getTuser(userId));
		if (StringUtil.isNotBlank(agentId)) {
			t.setOrganization(organizationService.getTorganizationInCacheByCode(agentId));
		}
		feedBackDao.save(t);
	}
}
