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
import com.cn.flypay.model.trans.TsysRoleHistory;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Role;
import com.cn.flypay.pageModel.trans.SysRoleHistory;
import com.cn.flypay.service.sys.RoleService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.SysRoleHistoryService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
/**
 * 用户角色权限分配记录
 * @author liangchao
 *
 */
@Service
public class SysRoleHistoryServiceImpl implements SysRoleHistoryService{
	
	@Autowired
	private BaseDao<TsysRoleHistory> sysRoleHistoryDao;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	
	
	/**
	 * 创建记录
	 */
	@Override
	public void createHistory(SysRoleHistory req) throws Exception {
		TsysRoleHistory trh = new TsysRoleHistory();
		Tuser creator = userService.getTuser(req.getCreatorId());
		
		BeanUtils.copyProperties(req, trh);
		trh.setCreator(creator);
		if(StringUtil.isNotBlank(creator.getRealName())){
			trh.setCreatorName(creator.getRealName());
		}else{
			trh.setCreatorName(creator.getName());
		}
		//调用id查询角色名称
		Role role = new Role();
		role.setId(req.getTargetId());
		PageFilter ph = new PageFilter();
		ph.setPage(1);
		ph.setRows(50);
		ph.setSort("id");
		ph.setOrder("asc");
		
		List<Role> targetInfo = roleService.dataGrid(role, ph);
		
		trh.setTargetName(targetInfo.get(0).getName());
		
		String operation = "";
		if(req.getBehaviorType() == 0){
			operation= "授权";
		}else if(req.getBehaviorType() == 1){
			operation= "编辑";
		}
		trh.setDetails("ID为" + req.getCreatorId() + "的用户" + operation
					+"sys_role 表中ID=" + req.getTargetId() + "的角色，角色运营商为: " + targetInfo.get(0).getOrganizationName()
					+",记录来源：管理平台--系统基础配置--角色管理");
		
		
		sysRoleHistoryDao.save(trh);
		
	}

	/**
	 * 读取数据
	 */
	@Override
	public List<SysRoleHistory> dataGrid(SysRoleHistory req, PageFilter ph) throws Exception {
		List<SysRoleHistory> us = new ArrayList<SysRoleHistory>();
		Map<String,Object> params = new HashMap<String,Object>();
		String hql = " select t from  TsysRoleHistory t left join t.creator cu ";
		List<TsysRoleHistory> ls = sysRoleHistoryDao.find(hql + whereHql(req,params) + orderHql(ph), params, ph.getPage(), ph.getRows() );
		for(TsysRoleHistory l : ls){
			SysRoleHistory u = new SysRoleHistory();
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
	private String whereHql(SysRoleHistory req,Map<String,Object> params) throws ParseException{
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
