package com.cn.flypay.service.trans.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.TsysUserRelateFreezeHistory;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.SysUserRelateFreezeHistory;
import com.cn.flypay.service.trans.SysUserRelateFreezeHistoryService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
/**
 * 用户相关冻结记录
 * @author liangchao
 *
 */
@Service
public class SysUserRelateFreezeHistoryServiceImpl implements SysUserRelateFreezeHistoryService {
	@Autowired
	private BaseDao<TsysUserRelateFreezeHistory>  sysUserRelateFreezeHistoryDao;
	@Autowired
	private BaseDao<Tuser> sysUserDao;
	
	
	/**
	 * 创建记录
	 */
	@Override
	public void createHistory(SysUserRelateFreezeHistory req) throws Exception {
			TsysUserRelateFreezeHistory tfh = new TsysUserRelateFreezeHistory();
			Tuser creator = sysUserDao.get(Tuser.class,req.getCreatorId());
			Tuser target = sysUserDao.get(Tuser.class,req.getTargetId());
			tfh.setCreator(creator);
			if(StringUtil.isNotBlank(creator.getRealName())){
				tfh.setCreatorName(creator.getRealName());
			}else{
				tfh.setCreatorName(creator.getName());
			}
			tfh.setRecordType(req.getRecordType());
			tfh.setBehaviorType(req.getBehaviorType());
			tfh.setTarget(target);
			if(StringUtil.isNotBlank(target.getRealName())){
				tfh.setTargetName(target.getRealName());
			}else{
				tfh.setTargetName(target.getName());
			}
			tfh.setDetails(req.getDetails());
			
			sysUserRelateFreezeHistoryDao.save(tfh);
		
	}



	/**
	 * 读取数据
	 * @param req
	 * @param ph
	 * @return
	 */
	@Override
	public List<SysUserRelateFreezeHistory> dataGrid(SysUserRelateFreezeHistory req,PageFilter ph) throws Exception {
		List<SysUserRelateFreezeHistory> us = new ArrayList<SysUserRelateFreezeHistory>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from  TsysUserRelateFreezeHistory t left join t.creator cu left join t.target tu  ";
		List<TsysUserRelateFreezeHistory> ls = sysUserRelateFreezeHistoryDao.find(hql + whereHql(req,params) + orderHql(ph), params, ph.getPage(), ph.getRows() );
		for(TsysUserRelateFreezeHistory l :ls){
			SysUserRelateFreezeHistory u = new SysUserRelateFreezeHistory();
			BeanUtils.copyProperties(l,u);
			u.setCreatorId(l.getCreator().getId());
			if(StringUtil.isNotBlank(l.getCreator().getRealName())){
				u.setCreatorName(l.getCreator().getRealName());
			}else{
				u.setCreatorName(l.getCreator().getName());
			}
			u.setCreatorPhone(l.getCreator().getPhone());
			u.setBehaviorType(l.getBehaviorType());
			u.setTargetId(l.getTarget().getId());
			if(StringUtil.isNotBlank(l.getTarget().getRealName())){
				u.setTargetName(l.getTarget().getRealName());
			}else{
				u.setTargetName(l.getTarget().getName());
			}
			u.setTargetPhone(l.getTarget().getPhone());
			u.setDetails(l.getDetails());
			us.add(u);
		}
		return us;
	}

	/**
	 * hql条件查询拼接
	 * @param req
	 * @param params
	 * @return
	 * @throws ParseException 
	 */
	private String whereHql(SysUserRelateFreezeHistory req,Map<String,Object> params) throws ParseException{
		String hql = "";
		if(req != null){
			hql += " where 1=1 ";
			if(StringUtil.isNotBlank(req.getCreatorPhone())){
				hql += " and cu.phone = :creatorPhone ";
				params.put("creatorPhone", req.getCreatorPhone());
			}
			if(req.getRecordType() != null){
				hql += " and t.recordType = :recordType ";
				params.put("recordType", req.getRecordType());
			}
			if(req.getBehaviorType() != null){
				hql += " and t.behaviorType = :behaviorType ";
				params.put("behaviorType", req.getBehaviorType());
			}
			if(StringUtil.isNotBlank(req.getTargetPhone())){
				hql += " and tu.phone = :targetPhone ";
				params.put("targetPhone", req.getTargetPhone());
			}
			if(StringUtil.isNotBlank(req.getDetails())){
				hql += " and t.details = :details ";
				params.put("details", req.getDetails());
			}
			if(StringUtil.isNotBlank(req.getSearchDateStart())){
				hql += " and t.createTime >= :searchDateStart ";
				params.put("searchDateStart",DateUtil.getStartOfDay(DateUtil.convertStringToDate("yyyy-MM-dd", req.getSearchDateStart())));
			}
			if(StringUtil.isNotBlank(req.getSearchDateEnd())){
				hql += " and t.createTime <= :searchDateEnd ";
				params.put("searchDateEnd",DateUtil.getEndOfDay(DateUtil.convertStringToDate("yyyy-MM-dd", req.getSearchDateEnd())));
			}
			
			
		}
		
		return hql;
	}
	
	/**
	 * 判断条件查询语句
	 * @param ph
	 * @return
	 */
	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null) && (ph.getSort().equals("createTime"))) {
			orderString = " order by t.createTime " + ph.getOrder();
		} else if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		
		return orderString;
	}

	
	
	
	
}
