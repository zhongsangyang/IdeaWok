package com.cn.flypay.service.account.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.TaccountPoint;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.account.AccountPoint;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Dictionary;
import com.cn.flypay.pageModel.sys.OrgPointConfig;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;
import com.cn.flypay.service.account.AccountPointHistoryService;
import com.cn.flypay.service.account.AccountPointService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.OrgPointConfigService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.utils.StringUtil;

@Service
public class AccountPointServiceImpl implements AccountPointService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BaseDao<TaccountPoint> accountPointDao;

	@Autowired
	private AccountPointHistoryService accountPointHistoryService;

	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private SysParamService sysParamService;
	@Autowired
	private OrgPointConfigService orgPointConfigService;
	@Autowired
	private UserSettlementConfigService userSettlementConfigService;

	@Autowired
	private DictionaryService dictionaryService;
	

	@Override
	public List<AccountPoint> dataGrid(AccountPoint accountPoint, PageFilter ph) {
		List<AccountPoint> ul = new ArrayList<AccountPoint>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TaccountPoint t left join t.user u left join u.organization tog ";
		List<TaccountPoint> l = accountPointDao.find(hql + whereHql(accountPoint, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TaccountPoint t : l) {
			ul.add(new AccountPoint(t));
		}
		return ul;
	}

	private String whereHql(AccountPoint accountPoint, Map<String, Object> params) {
		String hql = "";
		if (accountPoint != null) {
			hql += " where 1=1 ";
			if (StringUtil.isNotBlank(accountPoint.getLoginName())) {
				hql += " and u.loginName=:loginName ";
				params.put("loginName", accountPoint.getLoginName());
			}
			if (accountPoint.getStatus() != null) {
				hql += " and t.status=:status ";
				params.put("status", accountPoint.getStatus());
			}
			if (accountPoint.getOrganizationId() != null) {
				hql += " and  tog.id in(:orgIds)";
				params.put("orgIds", organizationService.getOwerOrgIds(accountPoint.getOrganizationId()));
			}
			if (accountPoint.getOperateUser() != null) {

				hql += " and  tog.id in(:operaterOrgIds)";
				params.put("operaterOrgIds", organizationService.getOwerOrgIds(accountPoint.getOperateUser().getOrganizationId()));
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
	public Long count(AccountPoint point, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TaccountPoint t left join t.user u left join u.organization tog ";
		return accountPointDao.count("select count(t.id) " + hql + whereHql(point, params), params);
	}

	@Override
	public void update(AccountPoint accountPoint) {
		TaccountPoint t = accountPointDao.get(TaccountPoint.class, accountPoint.getId());
		t.setStatus(accountPoint.getStatus());
		t.setSubPersonNum(accountPoint.getSubPersonNum());
		t.setPoint(accountPoint.getPoint());
		accountPointDao.update(t);
	}

	@Override
	public AccountPoint getAccountPointByUserId(Long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		String hql = "select t from TaccountPoint t left join t.user u left join u.organization tog where u.id=:userId";
		TaccountPoint tap = accountPointDao.get(hql, params);
		if (tap != null) {
			return new AccountPoint(tap);
		}
		return null;
	}
	
	@Override
	public void updatePointByUserId(Long userId, String cdType, Long addPoint, String desc, String pointType) throws Exception {
		try {
			String hql = "update TaccountPoint set point=point+:addPoint, subPersonNum=subPersonNum+:num where user.id=:userId";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", userId);
			params.put("addPoint", addPoint);
			Integer num = 0;
			if (AccountPoint.pointTypes_popularity.equals(pointType)) {
				/* 推广认证的1级下线人数更新 */
				num = 1;
			}
			params.put("num", num);
			accountPointDao.executeHql(hql, params);
			accountPointHistoryService.addPointHistory(userId, cdType, addPoint, desc);
		} catch (Exception e) {
			logger.error("更新用户积分账户异常", e);
			throw e;
		}
	}

	@Override
	public void updatePoint(Long userId, String pointType, String desc) throws Exception {
		if (AccountPoint.pointTypes_popularity.equals(pointType)) {
			/* 每次推荐一人 需要添加的的积分 */
			String score = sysParamService.searchSysParameter().get(AccountPoint.pointTypes_popularity);
			updatePointByUserId(userId, "D", Long.parseLong(score), desc, pointType);
		} else {
			logger.info("无更新类型");
		}
	}

	@Override
	public String updateUserInputRateByConsumePoint(String agentId, Long userId, Integer channelType, Integer accountType, Integer type, Integer realPoint) {
		String flag = GlobalConstant.RESP_CODE_SUCCESS;
		try {
			UserSettlementConfig usc = userSettlementConfigService.getByUserId(userId);
			Integer inputRate = 0;
//			if (accountType == 0) {
//				inputRate = setUserSettlementByLowPoint(agentId, channelType, 0, type, usc);
//				inputRate = setUserSettlementByLowPoint(agentId, channelType, 1, type, usc);
//			} else {
//				inputRate = setUserSettlementByLowPoint(agentId, channelType, 10, type, usc);
//				inputRate = setUserSettlementByLowPoint(agentId, channelType, 11, type, usc);
//			}
			
			inputRate = setUserSettlementByLowPoint(agentId, 200, 0, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 200, 1, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 200, 10, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 200, 11, type, usc);
			
			inputRate = setUserSettlementByLowPoint(agentId, 300, 0, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 300, 1, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 300, 10, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 300, 11, type, usc);
			
			inputRate = setUserSettlementByLowPoint(agentId, 500, 0, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 500, 1, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 500, 10, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 500, 11, type, usc);
			
			inputRate = setUserSettlementByLowPoint(agentId, 900, 0, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 900, 1, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 900, 10, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 900, 11, type, usc);
			
			inputRate = setUserSettlementByLowPoint(agentId, 1000, 0, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 1000, 1, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 1000, 10, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 1000, 11, type, usc);
			
			inputRate = setUserSettlementByLowPoint(agentId, 1300, 10, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 1300, 11, type, usc);
			

			Map<String, String> payTypes = dictionaryService.comboxMap("payType");
			Map<String, String> bigTranTypes = dictionaryService.comboxMap("bigTranType");
			
			updatePointByUserId(userId, "C", realPoint.longValue() * (-1), "降低通道费率消耗10积分", AccountPoint.pointTypes_consume);
			userSettlementConfigService.edit(usc);

//			if (inputRate > 0) {
//				updatePointByUserId(userId, "C", realPoint.longValue() * (-1), "降低通道费率消耗10积分", AccountPoint.pointTypes_consume);
//				userSettlementConfigService.edit(usc);
//			} else {
//				flag = "用户" + payTypes.get(channelType.toString()) + bigTranTypes.get(accountType.toString()) + "的费率已经等于低于通道费率，请刷新页面或重新登录";
//			}
		} catch (Exception e) {
			logger.error("积分兑换降费率", e);
			flag = "积分兑换降费率异常，请重试";
		}
		return flag;

	}

	private Integer setUserSettlementByLowPoint(String agentId, Integer channelType, Integer accountType, Integer type, UserSettlementConfig usc) {
		OrgPointConfig opc = orgPointConfigService.getByAgentIdAndPayType(agentId, channelType, accountType);
		Integer inputRate = 0;
		if (type == 1 && iSPoint(usc, channelType, type, accountType, opc)) {
			inputRate = usc.setInputFeeByPayType(opc.getMidRate(), channelType, accountType);
		} else if (type == 2 && iSPoint(usc, channelType, type, accountType, opc)) {
			inputRate = usc.setInputFeeByPayType(opc.getLowRate(), channelType, accountType);
		}else if (type == 3 && iSPoint(usc, channelType, type, accountType, opc)) {
			inputRate = usc.setInputFeeByPayType(opc.getLowRate(), channelType, accountType);
		}
		return inputRate;
	}
	
	
	
	private boolean iSPoint(UserSettlementConfig usc,Integer channelType,Integer type,Integer accountType,OrgPointConfig opc){
		switch (channelType) {
		case 200:
			switch (accountType) {
			 case 0:
				 return isType(type,opc,usc.getInputFeeD0Alipay());
			 case 1:
				 return isType(type,opc,usc.getInputFeeAlipay());
			 case 10:
				 return isType(type,opc,usc.getInputFeeD0BigAlipay());
			 case 11:
				 return isType(type,opc,usc.getInputFeeBigAlipay());
			 case 20:
				 return isType(type,opc,usc.getInputFeeD0ZtAlipay());
			 case 21:
				 return isType(type,opc,usc.getInputFeeZtAlipay());
			}
			break;
		case 300:
			switch (accountType) {
			 case 0:
				 return isType(type,opc,usc.getInputFeeD0Weixin());
			 case 1:
				 return isType(type,opc,usc.getInputFeeWeixin());
			 case 10:
				 return isType(type,opc,usc.getInputFeeD0BigWeixin());
			 case 11:
				 return isType(type,opc,usc.getInputFeeBigWeixin());
			 case 20:
				 return isType(type,opc,usc.getInputFeeD0ZtWeixin());
			 case 21:
				 return isType(type,opc,usc.getInputFeeZtWeixin());
			}
			break;
		case 500:
			switch (accountType) {
			 case 0:
				 return isType(type,opc,usc.getInputFeeD0Yinlian());
			 case 1:
				 return isType(type,opc,usc.getInputFeeYinlian());
			 case 10:
				 return isType(type,opc,usc.getInputFeeD0BigYinlian());
			 case 11:
				 return isType(type,opc,usc.getInputFeeBigYinlian());
			 case 20:
				 return isType(type,opc,usc.getInputFeeD0ZtYinlian());
			 case 21:
				 return isType(type,opc,usc.getInputFeeZtYinlian());
			}
			break;
		case 550:
			switch (accountType) {
			 case 20:
				 return isType(type,opc,usc.getInputFeeD0ZtYinlianJf());
			 case 21:
				 return isType(type,opc,usc.getInputFeeZtYinlianJf());
			}
			break;
		case 900:
			switch (accountType) {
			 case 0:
				 return isType(type,opc,usc.getInputFeeD0JingDong());
			 case 1:
				 return isType(type,opc,usc.getInputFeeJingDong());
			 case 10:
				 return isType(type,opc,usc.getInputFeeD0BigJingDong());
			 case 11:
				 return isType(type,opc,usc.getInputFeeBigJingDong());
			}
			break;
		case 1000:
			switch (accountType) {
			 case 0:
				 return isType(type,opc,usc.getInputFeeD0Baidu());
			 case 1:
				 return isType(type,opc,usc.getInputFeeBaidu());
			 case 10:
				 return isType(type,opc,usc.getInputFeeD0BigBaidu());
			 case 11:
				 return isType(type,opc,usc.getInputFeeBigBaidu());
			}
			break;
		case 1300:
			switch (accountType) {
			 case 10:
				 return isType(type,opc,usc.getInputFeeD0BigQQzhifu());
			 case 11:
				 return isType(type,opc,usc.getInputFeeBigQQzhifu());
			 case 20:
				 return isType(type,opc,usc.getInputFeeD0ZtQQzhifu());
			 case 21:
				 return isType(type,opc,usc.getInputFeeZtQQzhifu());
			}
			break;
		}
	   return false;
	}
	
	
	
	private boolean isType(Integer type,OrgPointConfig opc,BigDecimal usc){
		if(type == 1){
			 if(usc.compareTo(opc.getMidRate())==1||usc.compareTo(opc.getMidRate())==0||usc.compareTo(opc.getMidRate())==-1&&usc.compareTo(opc.getLowRate())==1){
				 return true;
			 }
		 }else if (type == 2 || type == 3) {
			 if(usc.compareTo(opc.getLowRate())==1){
				 return true;
			 }
		 }
		return false;
	}
	
	
	
	

	@Override
	public TaccountPoint freeze(Long id) {
		TaccountPoint ta = accountPointDao.get(TaccountPoint.class, id);
		if (ta != null) {
			int status = ta.getStatus() == 0 ? 1 : 0;
			accountPointDao.executeHql("update TaccountPoint set status=" + status + " where id=" + id);
			ta.setStatus(status);
		}
		return ta;
	}

	@Override
	public void initAccountPoint(Tuser user) {
		TaccountPoint tp = new TaccountPoint();
		tp.setPoint(0l);
		tp.setLockPoint(0l);
		tp.setStatus(0);
		tp.setUser(user);
		tp.setSubPersonNum(0);
		tp.setFumiType(0);
		accountPointDao.save(tp);
	}

	@Override
	public String consumePointChlRateZTH(String agentId, Long userId,
			Integer channelType, Integer accountType, Integer type,
			Integer realPoint) {
		String flag = GlobalConstant.RESP_CODE_SUCCESS;
		try {
			UserSettlementConfig usc = userSettlementConfigService.getByUserId(userId);
			Integer inputRate = 0;
			
			inputRate = setUserSettlementByLowPoint(agentId, 200, 20, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 200, 21, type, usc);
			
			inputRate = setUserSettlementByLowPoint(agentId, 300, 20, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 300, 21, type, usc);
			
			inputRate = setUserSettlementByLowPoint(agentId, 500, 20, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 500, 21, type, usc);
			
			if(!agentId.equals("F20160015")){
				//金钱龟OEM银联积分应卢总要求，固定不变，at 2018-1-11
				inputRate = setUserSettlementByLowPoint(agentId, 550, 20, type, usc);
				inputRate = setUserSettlementByLowPoint(agentId, 550, 21, type, usc);
			}
			inputRate = setUserSettlementByLowPoint(agentId, 1300, 20, type, usc);
			inputRate = setUserSettlementByLowPoint(agentId, 1300, 21, type, usc);
			

			Map<String, String> payTypes = dictionaryService.comboxMap("payType");
			Map<String, String> bigTranTypes = dictionaryService.comboxMap("bigTranType");
			
			updatePointByUserId(userId, "C", realPoint.longValue() * (-1), "降低通道费率消耗10积分", AccountPoint.pointTypes_consume);
			userSettlementConfigService.edit(usc);

		} catch (Exception e) {
			logger.error("积分兑换降费率", e);
			flag = "积分兑换降费率异常，请重试";
		}
		return flag;
	}

}
