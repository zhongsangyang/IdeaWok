package com.cn.flypay.service.trans.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.Tbrokerage;
import com.cn.flypay.model.trans.TbrokerageDetail;
import com.cn.flypay.model.trans.TbrokerageLog;
import com.cn.flypay.model.trans.TorderBonusProcess;
import com.cn.flypay.model.trans.TorgBrokerage;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.common.ProducerService;
import com.cn.flypay.service.sys.BrokerageConfigService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.trans.ShareBonusService;
import com.cn.flypay.utils.jpush.api.JiguangUtil;

@Service(value = "shareBonusService")
public abstract class AbstractShareBonusService implements ShareBonusService {

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private BrokerageConfigService brokerageConfigService;
	@Autowired
	protected OrganizationService organizationService;

	@Autowired
	private BaseDao<TorgBrokerage> orgBkDao;

	@Autowired
	BaseDao<Tbrokerage> bkDao;

	@Autowired
	private BaseDao<TbrokerageDetail> bkdDao;

	@Autowired
	private BaseDao<TorderBonusProcess> bonusProcessDao;

	@Autowired
	private ProducerService producerService;
	
	@Autowired
	private BaseDao<TbrokerageLog> brokerageLogDao;
	
	void updateParentBrokerageAccount(Tuser user, Tuser puser, TorderBonusProcess bonusProcess, Tbrokerage bk, int level, Double b, Integer cftType) {
		try {
			if (bk != null) {
                Integer brokerageType =1;
				TuserOrder order = bonusProcess.getOrder();
				updateUserBrokerage(bk, b, cftType);// 更新客户的分润账户
				String desc = level + "级" + convert(puser.getUserType()) + "交易金额提成" + b + "元";
				if (order.getTransPayType() - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0) {
					desc = level + "级" + convert(puser.getUserType()) + "提成" + b + "元";
					 brokerageType =2;
				}
				TbrokerageDetail bd = new TbrokerageDetail(order.getType(), order.getOrgAmt(), order.getCreateTime(), BigDecimal.valueOf(b), level, desc,brokerageType);
				bd.setBrokerageUserRate(0d);// 不按照比例分成
				bd.setPhone(user.getLoginName());
				bd.setUserCode(user.getCode());
				bd.setBrokerage(BigDecimal.valueOf(b));
				bd.setTbrokerage(bk);
				bd.setBrokerageUser(puser);
				bd.setBonusProcess(bonusProcess);
				bkdDao.save(bd);
				//分拥日志
				TbrokerageLog brokerageLog = new TbrokerageLog(bk,UserOrder.cd_type.D.name(), BigDecimal.valueOf(b), desc);
				brokerageLog.setAvlAmt(bk.getBrokerage());
				brokerageLog.setLockOutAmt(bk.getLockBrokerage());
				brokerageLog.setOrdernum(bonusProcess.getOrderNum());
				brokerageLogDao.save(brokerageLog);
				try {
					DecimalFormat df = new DecimalFormat("#.00");
					String title = "您收获了佣金" + Double.valueOf(df.format(b)) + "元";
					if(user.getAgentId().equals("F20160017")){
						desc = String.format(JiguangUtil.ALTER_USER_BROKERAGERTWO, ReaNameSub(user.getRealName()), b);
					}else{
						desc = String.format(JiguangUtil.ALTER_USER_BROKERAGER, ReaNameSub(user.getRealName()), b, user.getOrganization().getAppName());
					}
					InfoList t = new InfoList(puser.getId(), title, InfoList.info_Type.person.getCode(), desc, 0, 0);
					producerService.sendInfoList(t);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				log.info(puser.getLoginName() + "佣金账号已经被冻结，跳过本人的分润");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	
	void updateParentBrokerageAccountLT(Tuser user, Tuser puser, TorderBonusProcess bonusProcess, Tbrokerage bk, int level, Double b, Integer cftType) {
		try {
			if (bk != null) {

				TuserOrder order = bonusProcess.getOrder();
				updateUserBrokerage(bk, b, cftType);// 更新客户的分润账户
				String desc = level + "级代理商" + "交易金额提成" + b + "元";
				TbrokerageDetail bd = new TbrokerageDetail(order.getType(), order.getOrgAmt(), order.getCreateTime(), BigDecimal.valueOf(b), level, desc,1);
				bd.setBrokerageUserRate(0d);// 不按照比例分成
				bd.setPhone(user.getLoginName());
				bd.setUserCode(user.getCode());
				bd.setBrokerage(BigDecimal.valueOf(b));
				bd.setTbrokerage(bk);
				bd.setBrokerageUser(puser);
				bd.setBonusProcess(bonusProcess);
				bkdDao.save(bd);
				//分拥日志
				TbrokerageLog brokerageLog = new TbrokerageLog(bk,UserOrder.cd_type.D.name(), BigDecimal.valueOf(b), desc);
				brokerageLog.setAvlAmt(bk.getBrokerage());
				brokerageLog.setLockOutAmt(bk.getLockBrokerage());
				brokerageLog.setOrdernum(bonusProcess.getOrderNum());
				brokerageLogDao.save(brokerageLog);
				try {
					DecimalFormat df = new DecimalFormat("#.00");
					String title = "您收获了佣金" + Double.valueOf(df.format(b)) + "元";
					desc = String.format(JiguangUtil.ALTER_USER_BROKERAGER, ReaNameSub(user.getRealName()), b, user.getOrganization().getAppName());
					InfoList t = new InfoList(puser.getId(), title, InfoList.info_Type.person.getCode(), desc, 0, 0);
					producerService.sendInfoList(t);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				log.info(puser.getLoginName() + "佣金账号已经被冻结，跳过本人的分润");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	
	
	void updateParentBrokerageAccountLD(Tuser user, Tuser puser, TorderBonusProcess bonusProcess, Tbrokerage bk, int level, Double b, Integer cftType) {
		try {
			if (bk != null) {

				TuserOrder order = bonusProcess.getOrder();
				updateUserBrokerageTwo(bk, b, cftType);// 更新客户的分润账户
				
				DecimalFormat df = new DecimalFormat("#.00");
				String desc = "领导奖" + "交易金额提成" + Double.valueOf(df.format(b)) + "元";
				TbrokerageDetail bd = new TbrokerageDetail(order.getType(), order.getOrgAmt(), order.getCreateTime(), BigDecimal.valueOf(b), level, desc,3);
				bd.setBrokerageUserRate(0d);// 不按照比例分成
				bd.setPhone(user.getLoginName());//部分用户手机号字段缺失，登录名替代
				bd.setUserCode(user.getCode());
				bd.setBrokerage(BigDecimal.valueOf(b));
				bd.setTbrokerage(bk);
				bd.setBrokerageUser(puser);
				bd.setBonusProcess(bonusProcess);
				bkdDao.save(bd);
				//分拥日志
				TbrokerageLog brokerageLog = new TbrokerageLog(bk,UserOrder.cd_type.D.name(), BigDecimal.valueOf(b), desc);
				brokerageLog.setAvlAmt(bk.getBrokerage());
				brokerageLog.setLockOutAmt(bk.getLockBrokerage());
				brokerageLog.setOrdernum(bonusProcess.getOrderNum());
				brokerageLogDao.save(brokerageLog);
				try {
					String title = "您收获了佣金" + Double.valueOf(df.format(b)) + "元";
					desc = String.format(JiguangUtil.ALTER_USER_BROKERAGER, ReaNameSub(user.getRealName()), Double.valueOf(df.format(b)), user.getOrganization().getAppName());
					InfoList t = new InfoList(puser.getId(), title, InfoList.info_Type.person.getCode(), desc, 0, 0);
					producerService.sendInfoList(t);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				log.info(puser.getLoginName() + "佣金账号已经被冻结，跳过本人的分润");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	
	void sedInfoTT(Long userId) {
		try {
			String desc = "您领导奖账户资金不足,请充值!";
			InfoList t = new InfoList(userId, "领导奖通知", InfoList.info_Type.person.getCode(), desc, 0, 0);
			producerService.sendInfoList(t);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	

	private String convert(int usertype) {
		String type = "普通会员";
		switch (usertype) {
		case 21:
			type = "高级会员";
			break;
		case 22:
			type = "中级会员";
			break;
		default:
			break;
		}
		return type;
	}

	/**
	 * 更新用户分润表
	 * 
	 * @param bk
	 * @param b
	 */
	private void updateUserBrokerage(Tbrokerage bk, Double b, Integer cftType) {
		if (UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == cftType) {
			bk.setTotalAgentBrokerage(bk.getTotalAgentBrokerage().add(BigDecimal.valueOf(b)));
		} else if (UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode() == cftType) {
			bk.setTotalTransBrokerage(bk.getTotalTransBrokerage().add(BigDecimal.valueOf(b)));
		} else {
			return;
		}
		bk.setTotalBrokerage(bk.getTotalBrokerage().add(BigDecimal.valueOf(b)));
		bk.setBrokerage(bk.getBrokerage().add(BigDecimal.valueOf(b)));
		bkDao.update(bk);
	}
	
	
	/**
	 * 更新用户分润表
	 * 
	 * @param bk
	 * @param b
	 */
	private void updateUserBrokerageTwo(Tbrokerage bk, Double b, Integer cftType) {
		bk.setTotalLeadBrokerage(bk.getTotalLeadBrokerage().add(BigDecimal.valueOf(b)));
		bk.setTotalBrokerage(bk.getTotalBrokerage().add(BigDecimal.valueOf(b)));
		bk.setBrokerage(bk.getBrokerage().add(BigDecimal.valueOf(b)));
		bkDao.update(bk);
	}
	
	private String ReaNameSub(String name){
		if (name.length() == 2) {
		    name = name.substring(0, 1) + "*";
		} else if (name.length() == 3) {
		    name = name.substring(0, 1) + "*" + name.substring(name.length() - 1, name.length());
		} else if (name.length() > 3) {
		    name = name.substring(0, 1) + "**" + name.substring(name.length() - 1, name.length());
		}
		return name;
	}

}
