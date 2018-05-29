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
import com.cn.flypay.model.trans.TsysCreateTestData;
import com.cn.flypay.model.trans.TsysUserRelateFreezeHistory;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.SysCreateTestData;
import com.cn.flypay.pageModel.trans.SysUserRelateFreezeHistory;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.SysCreateTestDataService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
/**
 * 为王美凤造数据
 * @author liangchao
 *
 */
@Service
public class SysCreateTestDataServiceImpl implements  SysCreateTestDataService{
	
	
	@Autowired
	private BaseDao<TsysCreateTestData>  sysCreateTestDataDao;
	@Autowired
	private BaseDao<Tuser> sysUserDao;
	@Autowired
	private UserService userService;
	@Override
	/**
	 * 创建记录
	 */
	public void create(SysCreateTestData req) throws Exception {
		TsysCreateTestData tctd = new TsysCreateTestData();
		BeanUtils.copyProperties(req, tctd);
		Tuser creator = userService.getTuser(Long.valueOf(req.getUser_id()));
		
		if(StringUtil.isNotBlank(creator.getRealName())){
			tctd.setUser_name(creator.getRealName());
		}else{
			tctd.setUser_name(creator.getName());
		}
		sysCreateTestDataDao.save(tctd);
	}
	
	
	/**
	 * 读取数据
	 */
	@Override
	public List<SysCreateTestData> dataGrid(SysCreateTestData req, PageFilter ph) throws Exception {
		List<SysCreateTestData> us = new ArrayList<SysCreateTestData>();
		Map<String, Object> params = new HashMap<String, Object>();
		
		String hql = " select t from  TsysCreateTestData t ";
		List<TsysCreateTestData> ls = sysCreateTestDataDao.find(hql + whereHql(req,params) + orderHql(ph), params, ph.getPage(), ph.getRows() );
		for(TsysCreateTestData l :ls){
			SysCreateTestData u = new SysCreateTestData();
			BeanUtils.copyProperties(l,u);
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
	private String whereHql(SysCreateTestData req,Map<String,Object> params) throws ParseException{
		String hql = "where 1=1 ";
		if(req != null){
			if(StringUtil.isNotBlank(req.getUser_id())){
				hql += " and t.user_id = :user_id ";
				params.put("user_id", req.getUser_id());
			}
			if(StringUtil.isNotBlank(req.getSearchDateStart())){
				hql += " and t.create_time >= :searchDateStart ";
				params.put("searchDateStart",DateUtil.getStartOfDay(DateUtil.convertStringToDate("yyyy-MM-dd", req.getSearchDateStart())));
			}
			if(StringUtil.isNotBlank(req.getSearchDateEnd())){
				hql += " and t.create_time <= :searchDateEnd ";
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
			orderString = " order by t.create_time " + ph.getOrder();
		} else if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		
		return orderString;
	}
	
}
