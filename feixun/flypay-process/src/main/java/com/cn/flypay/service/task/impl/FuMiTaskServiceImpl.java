package com.cn.flypay.service.task.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.TaccountPoint;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserSettlementConfig;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.service.task.FuMiTaskService;



/**
 * 蚨米定时跑批结算推荐人数升级
 * 1人升级银牌
 * 2人升级金牌
 * 3人升级钻石
 * @author LW
 *
 */
@Service
public class FuMiTaskServiceImpl implements FuMiTaskService {
   
	private Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private BaseDao<TaccountPoint> taccountPointDao;
	
	
	@Autowired
	private BaseDao<Tuser> userDao;
	
	@Autowired
	private BaseDao<TuserSettlementConfig> userSettlementConfigDao;
	
	@Autowired
	private UserSettlementConfigService userSettlementConfigService;
	
	@Autowired
	private OrganizationService organizationService;
	
	
	
	@Override
	public void updateFuMiTaskUpgrade() {
		log.info("---------------------每五分钟处理蚨米推广人升级标准start---------------------");
	    try {
	    	Map<String, Object> params = new HashMap<String, Object>();
			params.put("agentId", "F00060009");
    		String hql ="select t from TaccountPoint t left join t.user u where u.agentId=:agentId and t.fumiType<3 ";
			List<TaccountPoint> point = taccountPointDao.find(hql, params);
			Torganization org = organizationService.getTorganizationInCacheByCode("F00060009");
			for (TaccountPoint t : point) {
				
				
				if(t.getFumiType()==0){
					if(t.getSubPersonNum()==1){
						log.info("遍历---------"+t.getSubPersonNum()+t.getUser().getId());
						TuserSettlementConfig tuscs = userSettlementConfigService.getTuserSettlementConfigByUserId(t.getUser().getId());
						userSettlementConfigService.setSettlementConfigWhenUserUpdate(tuscs, 23, org.getId());
						userSettlementConfigDao.update(tuscs);
						t.setId(t.getId());
						t.setFumiType(1);
						t.getUser().setUserType(23);
						userDao.update(t.getUser());
						taccountPointDao.update(t);
					}else if(t.getSubPersonNum()==2){
						log.info("遍历---------"+t.getSubPersonNum()+t.getUser().getId());
						TuserSettlementConfig tuscs = userSettlementConfigService.getTuserSettlementConfigByUserId(t.getUser().getId());
						userSettlementConfigService.setSettlementConfigWhenUserUpdate(tuscs, 22, org.getId());
						userSettlementConfigDao.update(tuscs);
						t.setId(t.getId());
						t.setFumiType(2);
						t.getUser().setUserType(22);
						userDao.update(t.getUser());
						taccountPointDao.update(t);
					}else if(t.getSubPersonNum()>=3){
						log.info("遍历---------"+t.getSubPersonNum()+t.getUser().getId());
						TuserSettlementConfig tuscs = userSettlementConfigService.getTuserSettlementConfigByUserId(t.getUser().getId());
						userSettlementConfigService.setSettlementConfigWhenUserUpdate(tuscs, 21, org.getId());
						userSettlementConfigDao.update(tuscs);
						t.setId(t.getId());
						t.setFumiType(3);
						t.getUser().setUserType(21);
						userDao.update(t.getUser());
						taccountPointDao.update(t);
					}
				}	
					
					if(t.getFumiType()==1){
						if(t.getSubPersonNum()==2){
							log.info("遍历---------"+t.getSubPersonNum()+t.getUser().getId());
							TuserSettlementConfig tuscs = userSettlementConfigService.getTuserSettlementConfigByUserId(t.getUser().getId());
							userSettlementConfigService.setSettlementConfigWhenUserUpdate(tuscs, 22, org.getId());
							userSettlementConfigDao.update(tuscs);
							t.setId(t.getId());
							t.setFumiType(2);
							t.getUser().setUserType(22);
							userDao.update(t.getUser());
							taccountPointDao.update(t);
						}else if(t.getSubPersonNum()>=3){
							log.info("遍历---------"+t.getSubPersonNum()+t.getUser().getId());
							TuserSettlementConfig tuscs = userSettlementConfigService.getTuserSettlementConfigByUserId(t.getUser().getId());
							userSettlementConfigService.setSettlementConfigWhenUserUpdate(tuscs, 21, org.getId());
							userSettlementConfigDao.update(tuscs);
							t.setId(t.getId());
							t.setFumiType(3);
							t.getUser().setUserType(21);
							userDao.update(t.getUser());
							taccountPointDao.update(t);
						}
					}
					
					
					if(t.getFumiType()==2 && t.getSubPersonNum()>=3){
						log.info("遍历---------"+t.getSubPersonNum()+t.getUser().getId());
						TuserSettlementConfig tuscs = userSettlementConfigService.getTuserSettlementConfigByUserId(t.getUser().getId());
						userSettlementConfigService.setSettlementConfigWhenUserUpdate(tuscs, 21, org.getId());
						userSettlementConfigDao.update(tuscs);
						t.setId(t.getId());
						t.setFumiType(3);
						t.getUser().setUserType(21);
						userDao.update(t.getUser());
						taccountPointDao.update(t);
					}
			}
			log.info("---------------------每五分钟处理蚨米推广人升级标准end---------------------");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("每五分钟处理蚨米推广人升级标异常");
		}
	}
	
	
}
