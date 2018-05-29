package com.cn.flypay.service.trans.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.TsysFreezeHistory;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.trans.SysFreezeHistory;
import com.cn.flypay.service.trans.SysFreezeHistoryService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
/**
 * 用户冻结历史操作
 * @author liangchao
 *
 */
@Service
public class SysFreezeHistoryServiceImpl implements SysFreezeHistoryService {
	@Autowired
	private BaseDao<TsysFreezeHistory>  sysFreezeHistoryDao;
	@Autowired
	private BaseDao<Tuser> sysUserDao;
	
	
	/**
	 * 创建记录
	 */
	@Override
	public void createSysFreezeHistory(SysFreezeHistory req) throws Exception {
			TsysFreezeHistory tfh = new TsysFreezeHistory();
			Tuser creator = this.queryUser(req.getCreatorId());
			Tuser target = this.queryUser(req.getTargetId());
			tfh.setCreator(creator);
			tfh.setCreatorName(creator.getRealName());
			tfh.setBehaviorType(req.getBehaviorType());
			tfh.setTarget(target);
			tfh.setTargetName(target.getRealName());
			tfh.setRemark(req.getRemark());
			
			sysFreezeHistoryDao.save(tfh);
		
	}

	//根据id查询用户
	private Tuser queryUser(Long id){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		String hql = "select t from Tuser t where t.id = :id";
		List<Tuser> users = sysUserDao.find(hql,params);
		return users.get(0);
	}
	


	/**
	 * 读取数据
	 * @param req
	 * @param ph
	 * @return
	 */
	@Override
	public List<SysFreezeHistory> dataGrid(SysFreezeHistory req,PageFilter ph) throws Exception {
		List<SysFreezeHistory> us = new ArrayList<SysFreezeHistory>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from  TsysFreezeHistory t left join t.creator cu left join t.target tu  ";
		List<TsysFreezeHistory> ls = sysFreezeHistoryDao.find(hql + whereHql(req,params) + orderHql(ph), params, ph.getPage(), ph.getRows() );
		for(TsysFreezeHistory l :ls){
			SysFreezeHistory u = new SysFreezeHistory();
			BeanUtils.copyProperties(l,u);
			u.setCreatorId(l.getCreator().getId());
			u.setCreatorName(l.getCreator().getRealName());
			u.setCreatorPhone(l.getCreator().getPhone());
			u.setBehaviorType(l.getBehaviorType());
			u.setTargetId(l.getTarget().getId());
			u.setTargetName(l.getTarget().getRealName());
			u.setTargetPhone(l.getTarget().getPhone());
			u.setRemark(l.getRemark());
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
	private String whereHql(SysFreezeHistory req,Map<String,Object> params) throws ParseException{
		String hql = "";
		if(req != null){
			hql += " where 1=1 ";
			if(StringUtil.isNotBlank(req.getCreatorPhone())){
				hql += " and cu.phone = :creatorPhone ";
				params.put("creatorPhone", req.getCreatorPhone());
			}
			if(req.getBehaviorType() != null){
				hql += " and t.behaviorType = :behaviorType ";
				params.put("behaviorType", req.getBehaviorType());
			}
			if(StringUtil.isNotBlank(req.getTargetPhone())){
				hql += " and tu.phone = :targetPhone ";
				params.put("targetPhone", req.getTargetPhone());
			}
			if(StringUtil.isNotBlank(req.getRemark())){
				hql += " and t.remark = :remark ";
				params.put("remark", req.getRemark());
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
