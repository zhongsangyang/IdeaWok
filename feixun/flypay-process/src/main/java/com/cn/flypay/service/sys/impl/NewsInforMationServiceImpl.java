package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.TsysInformation;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.SysInformation;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.sys.NewsInforMationService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.utils.DateUtil;





@Service
public class NewsInforMationServiceImpl implements NewsInforMationService {
	
	@Value("${get_wen_root_path}")
	private String get_wen_root_path;
	
	@Autowired
	private BaseDao<TsysInformation> sysInformationDao;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private BaseDao<Torganization> organizationDao;
	
	
	
	@Override
	public List<SysInformation> dataGrid(SysInformation sys, PageFilter ph) {
		List<SysInformation> sl = new ArrayList<SysInformation>();
		ph.setSort("creatime");
		ph.setOrder("desc");
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			String hql = "select t from TsysInformation t left join t.organization g ";
			List<TsysInformation> s = sysInformationDao.find(hql + whereHql(sys, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
			for (TsysInformation t : s) {
				SysInformation tion = new SysInformation();
				BeanUtils.copyProperties(t, tion);
				sl.add(tion);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sl;
	}

	@Override
	public Long count(SysInformation sys, PageFilter ph) {
		Long h = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			String hql = "from TsysInformation t ";
			h = sysInformationDao.count("select count(t.id) " + hql + whereHql(sys, params), params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return h;
	}
	
	
	
	private String whereHql(SysInformation sys, Map<String, Object> params) throws Exception {
		String hql = "";
	    if(sys!=null){
	    	hql += " where t.stauts=0";
	    	if (sys.getStateDateStart() != null && !sys.getStateDateStart().equals("")) {
				hql += " and t.creatime >= :stateDateStart";
				params.put("stateDateStart", DateUtil.getStartOfDay(DateUtil.convertStringToDate("yyyyMMdd", sys.getStateDateStart())));
	    	}
	    	if (sys.getStateDateEnd() != null && !sys.getStateDateEnd().equals("")) {
				hql += " and t.creatime <= :stateDateEnd";
				params.put("stateDateEnd", DateUtil.getEndOfDay(DateUtil.convertStringToDate("yyyyMMdd", sys.getStateDateEnd())));
			}
	    	if(sys.getOrganizationId() !=null){
	    		hql += " and  t.organization.id in(:orgIds)";
				params.put("orgIds", organizationService.getOwerOrgIds(sys.getOrganizationId()));
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
	public void edit(SysInformation sys) {
		TsysInformation t = sysInformationDao.get(TsysInformation.class, sys.getId());
		t.setStauts(1);
		sysInformationDao.update(t);
	}

	@Override
	public void add(SysInformation sys,User user) {
		TsysInformation t = new TsysInformation();
		t.setOrganization(organizationDao.get(Torganization.class,user.getOrganizationId()));
		t.setTitle(sys.getTitle());
		t.setContext(sys.getContext());
		t.setSketch(sys.getSketch());
		t.setDownSum(0);
		t.setReadingSum(0);
		t.setStauts(0);
		t.setCreatime(new Date());
		t.setImgePath(sys.getImgePath());
		sysInformationDao.save(t);
	}

	
	@Override
	public JSONArray findList(String agentId, PageFilter pf) {
		String hql = "select t from TsysInformation t left join t.organization g where t.organization.code = '" + agentId + "'" +" and t.stauts=0 " ;
		List<TsysInformation> s = sysInformationDao.find(hql + orderHql(pf), pf.getPage(), pf.getRows());
		JSONArray ja = new JSONArray();
		for (TsysInformation t : s) {
			SysInformation tion = new SysInformation();
			tion.setId(t.getId());
			tion.setTitle(t.getTitle());
			tion.setSketch(t.getSketch());
			tion.setCreatime(t.getCreatime());
			tion.setDownSum(t.getDownSum());
			tion.setReadingSum(t.getReadingSum());
			String turl = get_wen_root_path + "getImgTW?imgName=" + t.getImgePath();
			tion.setTurl(turl);
			String qurl = get_wen_root_path + "detailsText?inforId=" + t.getId();
			tion.setQurl(qurl);
			ja.add(tion);
		}
		return ja;
	}

	@Override
	public SysInformation getSysInfor(String id) {
		TsysInformation t = sysInformationDao.get(TsysInformation.class, Long.parseLong(id));
		SysInformation tion = new SysInformation();
		BeanUtils.copyProperties(t, tion);
		return tion;
	}

	@Override
	public boolean editSum(SysInformation sys) {
		TsysInformation t = sysInformationDao.get(TsysInformation.class, sys.getId());
		if(sys.getType().equals("1")){
			t.setReadingSum(t.getReadingSum()+1);
		}else if(sys.getType().equals("2")){
			t.setDownSum(t.getDownSum()+1);
		}
		sysInformationDao.update(t);
		return true;
	}

}
