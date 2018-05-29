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
import com.cn.flypay.model.sys.TinfoList;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.util.CollectionUtil;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.service.sys.InfoListService;
import com.cn.flypay.service.sys.JiguangPushService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.StringUtil;

@Service
public class InfoListServiceImpl implements InfoListService {

	@Autowired
	private BaseDao<TinfoList> infoListDao;
	@Autowired
	private JiguangPushService jiguangPushService;

	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private SysParamService sysParamService;
	@Autowired
	private UserService userService;

	@Override
	public List<InfoList> dataGrid(InfoList param, PageFilter ph) {
		List<InfoList> ul = new ArrayList<InfoList>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TinfoList t left join t.user u left join t.organization g ";
		List<TinfoList> l = infoListDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TinfoList t : l) {
			InfoList u = new InfoList();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrganizationName(t.getOrganization().getName());
			}
			if (t.getUser() != null) {
				u.setPhone(t.getUser().getLoginName());
				u.setUserName(t.getUser().getRealName());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(InfoList param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TinfoList t left join t.user u left join t.organization g ";
		return infoListDao.count("select count(t.id) " + hql + whereHql(param, params), params);
	}

	// 查询未读消息，返回id方便后面更新未读消息状态为已读
	@Override
	public List<TinfoList> findUnRead(InfoList param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TinfoList t left join t.user u left join t.organization g ";
		List<TinfoList> list = infoListDao.find("select t.id " + hql + whereHql(param, params), params);
		return list;
	}

	private String whereHql(InfoList infoList, Map<String, Object> params) {
		String hql = "";
		if (infoList != null) {
			hql += " where 1=1 ";
			if (infoList.getStatus() != null) {
				hql += " and t.status=:status ";
				params.put("status", infoList.getStatus());
			}
			if (infoList.getIsRead() != null) {
				hql += " and t.isRead=:isRead ";
				params.put("isRead", infoList.getIsRead());
			}
			if (infoList.getIsShow() != null) {
				hql += " and t.isShow=:isShow ";
				params.put("isShow", infoList.getIsShow());
			}
			if (StringUtil.isNotBlank(infoList.getAgentId())) {
				hql += " and g.code  like :agentId";
				params.put("agentId", StringUtil.getAgentId(infoList.getAgentId()) + "%");
			}
			if (infoList.getInfoType() != null) {
				hql += " and t.infoType=:infoType ";
				params.put("infoType", infoList.getInfoType());
				if (infoList.getInfoType() == InfoList.info_Type.person.getCode() && infoList.getUserId() != null) {
					hql += " and u.id=:uid ";
					params.put("uid", infoList.getUserId());
				}
			}
			if (StringUtil.isNotBlank(infoList.getPhone())) {
				hql += " and u.loginName=:loginName ";
				params.put("loginName", infoList.getPhone());
			}
			if (infoList.getCreatedatetimeStart() != null) {
				hql += " and t.createTime >= :createdatetimeStart";
				params.put("createdatetimeStart", infoList.getCreatedatetimeStart());
			}
			if (infoList.getCreatedatetimeEnd() != null) {
				hql += " and t.createTime <= :createdatetimeEnd";
				params.put("createdatetimeEnd", infoList.getCreatedatetimeEnd());
			}
			if (infoList.getOperateUser() != null) {
				hql += " and  g.id in(:operaterOrgIds)";
				params.put("operaterOrgIds", organizationService.getOwerOrgIds(infoList.getOperateUser().getOrganizationId()));
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
	public void updateAllInfoListToReadedStatus(InfoList infoList) {
		String hql = "update TinfoList set isRead =:isRead where  1=1 ";
		Map<String, Object> params = new HashMap<String, Object>();
		// params.put("status", InfoList.info_status.release_success.getCode());
		params.put("isRead", InfoList.is_Read.readed.getCode());
		params.put("orgReadStatus", InfoList.is_Read.unread.getCode());
		params.put("infoType", infoList.getInfoType());
		if (infoList.getInfoType() == InfoList.info_Type.person.getCode() && infoList.getUserId() != null) {
			params.put("uid", infoList.getUserId());
			hql = hql + " and user.id=:uid";
		}
		hql = hql + " and isRead=:orgReadStatus and infoType=:infoType ";
		infoListDao.executeHql(hql, params);
	}

	// update 2017.11.22 重写更新未读消息为已读，根据之前的未读消息id，提高查询效率
	@Override
	public void updateAllInfoListToReadedStatus(InfoList infoList, List<TinfoList> list) {
		String hql = "update TinfoList set isRead =:isRead where  1=1 ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isRead", InfoList.is_Read.readed.getCode());
		params.put("id", list);
		if (CollectionUtil.isNotEmpty(list)) {
			hql = hql + " and id in(:id)";
			infoListDao.executeHql(hql, params);
		}
	}

	@Override
	public void add(InfoList param) {
		TinfoList t = new TinfoList();
		BeanUtils.copyProperties(param, t);
		t.setCreateTime(new Date());
		if (param.getUserId() != null) {
			Tuser tuser = userService.getTuser(param.getUserId());
			t.setUser(tuser);
			Torganization tt = organizationService.getTorganizationInCacheByCode(tuser.getAgentId());
			t.setOrganization(tt);
		}
		if (param.getOrganizationId() != null) {
			t.setOrganization(organizationService.getTorganizationInCacheById(param.getOrganizationId()));
		}
		infoListDao.save(t);
	}

	@Override
	public void delete(Long id) {
		TinfoList t = infoListDao.get(TinfoList.class, id);
		t.setStatus(2);
		infoListDao.update(t);
	}

	@Override
	public void edit(InfoList infoList) {

		// TinfoList t = infoListDao.get(TinfoList.class, infoList.getId());
		String hql = "select t from TinfoList t where t.id=" + infoList.getId();
		TinfoList t = infoListDao.get(hql);
		/* 对于公司公告，原来公告不是发布状态，而编辑后的公告为发布状态，将公告发送出去 */
		if (infoList.getInfoType() == InfoList.info_Type.company.getCode() && (t.getStatus() != InfoList.info_status.release_success.getCode() && infoList.getStatus() == InfoList.info_status.release_success.getCode())) {
			Torganization tr = organizationService.getTorganizationInCacheById(infoList.getOrganizationId());
			jiguangPushService.sendMsgInfo(infoList, StringUtil.getAgentId(tr.getCode()));
		}
		t.setContent(infoList.getContent());
		t.setTitle(infoList.getTitle());
		t.setStatus(infoList.getStatus());
		t.setIsShow(infoList.getIsShow());
		t.setIsForce(infoList.getIsForce());
		t.setForceHours(infoList.getForceHours());
		infoListDao.update(t);
	}

	@Override
	public InfoList get(Long id) {
		TinfoList t = infoListDao.get(TinfoList.class, id);
		InfoList f = new InfoList();
		BeanUtils.copyProperties(t, f);
		return f;
	}

	@Override
	public Long countUnreadedMsgInfoByUserId(Long userId) {
		return infoListDao.count("select count(id) from TinfoList t where t.isRead=0 and  t.user.id=" + userId);
	}

	@Override
	public List<String> findSystemNews(String agentId) {
		List<String> contents = new ArrayList<String>();
		String sql = "select f.id,f.content " + "from sys_info_list f left join sys_organization g on g.id=f.organization_id " + "where f.info_type=2  " + "and f.status=1  " + "and f.is_force=1 "
				+ "and TIMESTAMPDIFF(HOUR,f.create_time,DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s'))< f.force_hours " + "and g.`CODE` like '" + StringUtil.getAgentId(agentId) + "%' " + "order by f.id desc ";
		List<Object[]> ls = infoListDao.findBySql(sql);
		int items = Integer.parseInt(sysParamService.searchSysParameter().get("sys_info_num"));
		items = items >= ls.size() ? ls.size() : items;
		for (int i = 0; i < items; i++) {
			contents.add((String) ls.get(i)[1]);
		}
		return contents;
	}

	@Override
	public List<InfoList> findSystemNotice(String agentId) {
		List<InfoList> contents = new ArrayList<InfoList>();
		String sql = "select f.id,f.content,f.title " + "from sys_info_list f left join sys_organization g on g.id=f.organization_id " + "where f.info_type=2  " + "and f.status=1  " + "and f.is_force=1 "
				+ "and TIMESTAMPDIFF(HOUR,f.create_time,DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s'))< f.force_hours " + "and g.`CODE` like '" + StringUtil.getAgentId(agentId) + "%' " + "order by f.id desc ";
		List<Object[]> ls = infoListDao.findBySql(sql); // id,content,title
		int items = Integer.parseInt(sysParamService.searchSysParameter().get("sys_info_num"));
		items = items >= ls.size() ? ls.size() : items;
		InfoList info = null;
		for (int i = 0; i < items; i++) {
			info = new InfoList();
			info.setTitle((String) ls.get(i)[2]);
			info.setContent((String) ls.get(i)[1]);
			contents.add(info);
		}
		return contents;
	}
}
