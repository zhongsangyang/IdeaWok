package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TorgChannel;
import com.cn.flypay.pageModel.sys.OrgChannel;
import com.cn.flypay.service.sys.OrgChannelService;
import com.cn.flypay.utils.StringUtil;

@Service
public class OrgChannelServiceImpl implements OrgChannelService {

	@Autowired
	private BaseDao<TorgChannel> orgChannelDao;

	@Override
	public List<OrgChannel> findOrgChannelByOrgId(Long orgId) {
		List<OrgChannel> cls = new ArrayList<OrgChannel>();

		String hql = "select t from TorgChannel t left join t.organization g where g.id=" + orgId;
		List<TorgChannel> tcls = orgChannelDao.find(hql);
		for (TorgChannel tc : tcls) {
			OrgChannel gl = new OrgChannel();
			BeanUtils.copyProperties(tc, gl);
			if (tc.getChannel() != null) {
				gl.setChannelName(tc.getChannel().getName());
				gl.setChannelId(tc.getChannel().getId());
				gl.setDetailName(tc.getChannel().getDetailName());
			}
			gl.setOrgId(orgId);
			cls.add(gl);
		}
		return cls;
	}

	@Override
	public List<OrgChannel> findOrgChannelByAgentId(String agentId) {
		List<OrgChannel> cls = new ArrayList<OrgChannel>();

		String hql = "select t from TorgChannel t left join t.organization g where g.code like '" + StringUtil.getAgentId(agentId) + "%'";
		List<TorgChannel> tcls = orgChannelDao.find(hql);
		for (TorgChannel tc : tcls) {
			OrgChannel gl = new OrgChannel();
			BeanUtils.copyProperties(tc, gl);
			if (tc.getChannel() != null) {
				gl.setChannelName(tc.getChannel().getName());
				gl.setChannelId(tc.getChannel().getId());
				gl.setDetailName(tc.getChannel().getDetailName());
			}
			gl.setOrgId(tc.getOrganization().getId());
			cls.add(gl);
		}
		return cls;
	}

	@Override
	public void add(OrgChannel orgChannel) {

	}

	@Override
	public void edit(OrgChannel orgChannel) {

	}

	@Override
	public void addTorgChannelList(List<TorgChannel> tcs) {
		Session session = orgChannelDao.getCurrentSession();
		session.setCacheMode(CacheMode.IGNORE);
		int saveCount = 0;
		for (TorgChannel o : tcs) {
			session.save(o);
			if (++saveCount % tcs.size() == 0) {
				saveCount = 0;
			}
		}
	}

	@Override
	public void editOrgChannel(List<OrgChannel> orgs) {
		for (OrgChannel o : orgs) {
			TorgChannel tcl = orgChannelDao.get(TorgChannel.class, o.getId());
			tcl.setRealRate(o.getRealRate());
			if (o.getStartDate() != null) {
				tcl.setStartDate(o.getStartDate());
			}
			if (o.getStartDate() != null) {
				tcl.setEndDate(o.getEndDate());
			}
			if (o.getStartDate() != null) {
				tcl.setStatus(o.getStatus());
			}
			tcl.setUpdator(o.getUpdator());
			tcl.setUpdateDate(o.getUpdateDate());
			orgChannelDao.update(tcl);
		}

	}

}
