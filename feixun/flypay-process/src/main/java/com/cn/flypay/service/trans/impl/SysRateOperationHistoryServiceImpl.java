package com.cn.flypay.service.trans.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.TsysRateOperationHistory;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.SysRateOperationHistory;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.SysRateOperationHistoryService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;


/**
 * 平台费率相关操作记录表
 * @author liangchao
 *
 */
@Service
public class SysRateOperationHistoryServiceImpl implements SysRateOperationHistoryService{
	@Autowired
	private BaseDao<TsysRateOperationHistory> sysRateOperationHistoryDao;
	@Autowired
	private UserService userService;
	
	/**
	 * 创建记录
	 * @param req
	 * @throws Exception
	 */
	@Override
	public void createHistory(SysRateOperationHistory req) throws Exception {
		TsysRateOperationHistory tsoh = new TsysRateOperationHistory();
		Tuser creator = userService.getTuser(req.getCreatorId());
		
		tsoh.setCreator(creator);
		BeanUtils.copyProperties(req, tsoh);
		if(StringUtil.isNotBlank(creator.getRealName())){
			tsoh.setCreatorName(creator.getRealName());
		}else{
			tsoh.setCreatorName(creator.getName());
		}
		sysRateOperationHistoryDao.save(tsoh);
	}
	
	
	
	/**
	 * 读取数据
	 * @param req
	 * @param ph
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<SysRateOperationHistory> dataGrid(SysRateOperationHistory req, PageFilter ph) throws Exception {
		List<SysRateOperationHistory> us = new ArrayList<SysRateOperationHistory>();
		Map<String,Object> params = new HashMap<String,Object>();
		String hql = " select t from  TsysRateOperationHistory t left join t.creator cu ";
		List<TsysRateOperationHistory> ls = sysRateOperationHistoryDao.find(hql + whereHql(req,params) + orderHql(ph), params, ph.getPage(), ph.getRows() );
		
		for(TsysRateOperationHistory l : ls){
			SysRateOperationHistory u = new SysRateOperationHistory();
			BeanUtils.copyProperties(l,u);
			u.setCreatorId(l.getCreator().getId());
			u.setCreatorPhone(l.getCreator().getPhone());
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
	private String whereHql(SysRateOperationHistory req,Map<String,Object> params) throws ParseException{
		String hql = "";
		if(req != null){
			hql += " where 1=1 ";
			if(StringUtil.isNotBlank(req.getCreatorPhone())){	//操作者手机号
				hql += " and cu.phone = :creatorPhone ";
				params.put("creatorPhone", req.getCreatorPhone());
			}
			if(req.getRecordType() != null){	//记录来源的类型
				hql += " and t.recordType = :recordType ";
				params.put("recordType", req.getRecordType());
			}
			if(req.getBehaviorType() != null){		//行为类型
				hql += " and t.behaviorType = :behaviorType ";
				params.put("behaviorType", req.getBehaviorType());
			}
			if(req.getTargetId() != null){		//行为接受者在自己所属表中的ID
				hql += " and t.targetId = :targetId ";
				params.put("targetId", req.getTargetId());
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
