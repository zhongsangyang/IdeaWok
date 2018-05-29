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
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.TsysChannelOperationHistory;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.SysChannelOperationHistory;
import com.cn.flypay.service.trans.SysChannelOperationHistoryService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
/**
 * 通道编辑操作记录表
 * @author liangchao
 *
 */
@Service
public class SysChannelOperationHistoryServiceImpl implements SysChannelOperationHistoryService{
	@Autowired
	private BaseDao<Tuser> sysUserDao;
	@Autowired
	private BaseDao<Tchannel> sysChannelDao;
	
	@Autowired
	private BaseDao<TsysChannelOperationHistory> sysChannelOperationHistoryDao;
	
	/**
	 * 创建记录
	 */
	@Override
	public void createHistory(SysChannelOperationHistory req) throws Exception {
		TsysChannelOperationHistory tcoh = new TsysChannelOperationHistory();
		Tuser creator = sysUserDao.get(Tuser.class,req.getCreatorId());
		Tchannel target = sysChannelDao.get(Tchannel.class,req.getTargetId());
		BeanUtils.copyProperties(target, tcoh);
		tcoh.setCreator(creator);
		if(StringUtil.isNotBlank(creator.getRealName())){
			tcoh.setCreatorName(creator.getRealName());
		}else{
			tcoh.setCreatorName(creator.getName());
		}
		tcoh.setRecordType(req.getRecordType());
		tcoh.setBehaviorType(req.getBehaviorType());
		tcoh.setTarget(target);
		
		tcoh.setDetails(req.getDetails());
		
		sysChannelOperationHistoryDao.save(tcoh);
	}

	

	/**
	 * 读取数据
	 * @param req
	 * @param ph
	 * @return
	 */
	@Override
	public List<SysChannelOperationHistory> dataGrid(SysChannelOperationHistory req, PageFilter ph) throws Exception {
		List<SysChannelOperationHistory> us = new ArrayList<SysChannelOperationHistory>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from  TsysChannelOperationHistory t left join t.creator cu left join t.target tu  ";
		List<TsysChannelOperationHistory> ls = sysChannelOperationHistoryDao.find(hql + whereHql(req,params) + orderHql(ph), params, ph.getPage(), ph.getRows() );
		
		for(TsysChannelOperationHistory l : ls){
			SysChannelOperationHistory u =new SysChannelOperationHistory();
			BeanUtils.copyProperties(l,u);
			u.setCreatorId(l.getCreator().getId());
			if(StringUtil.isNotBlank(l.getCreator().getRealName())){
				u.setCreatorName(l.getCreator().getRealName());
			}else{
				u.setCreatorName(l.getCreator().getName());
			}
			u.setCreatorPhone(l.getCreator().getPhone());
			u.setTargetId(l.getTarget().getId());	//通道的D
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
	private String whereHql(SysChannelOperationHistory req,Map<String,Object> params) throws ParseException{
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
			if(req.getTargetId() != null){		//sys_channel 通道的id
				hql += " and tu.id = :targetId ";
				params.put("targetId", req.getTargetId());
			}
			if(StringUtil.isNotBlank(req.getName())){	//通道名称
				hql += " and t.name = :name ";
				params.put("name", req.getName());
			}
			if(req.getType() != null){	//通道类型   如：银联积分 、银联大额等
				hql += " and t.type = :type ";
				params.put("type", req.getType());
			}
			if(req.getUserType() !=null){	//用户类型   钻石会员，钻石和金牌会员等
				hql += " and t.userType = :userType ";
				params.put("userType", req.getUserType());
			}
			if(req.getStatus() !=null){	// 通道状态
				hql += " and t.status = :status ";
				params.put("status", req.getStatus());
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
