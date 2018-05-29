package com.cn.flypay.service.account.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.account.TaccountLog;
import com.cn.flypay.model.account.TorgAccount;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.TranPayOrder;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.account.Account;
import com.cn.flypay.pageModel.account.OrgAccount;
import com.cn.flypay.pageModel.account.PlatformOrgConfig;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.account.OrgAccountService;
import com.cn.flypay.service.payment.PaymentService;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.PlatformOrgConfigService;

@Service
public class OrgAccountServiceImpl implements OrgAccountService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BaseDao<TorgAccount> orgAccountDao;

	@Autowired
	private BaseDao<TaccountLog> accountLogDao;
	@Autowired
	private BaseDao<TranPayOrder> payOrderDao;

	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private AccountService accountService;

	@Autowired
	private PaymentService paymentService;
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private PlatformOrgConfigService platformOrgConfigService;

	@Override
	public List<OrgAccount> dataGrid(OrgAccount param, PageFilter ph) {
		List<OrgAccount> ul = new ArrayList<OrgAccount>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TorgAccount t left join t.organization tog ";
		List<TorgAccount> l = orgAccountDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TorgAccount t : l) {
			OrgAccount orgAccount = new OrgAccount();
			BeanUtils.copyProperties(t, orgAccount);
			if (t.getOrganization() != null) {
				orgAccount.setOrgName(t.getOrganization().getName());
			}
			ul.add(orgAccount);
		}
		return ul;
	}

	private String whereHql(OrgAccount account, Map<String, Object> params) {
		String hql = "";
		if (account != null) {
			hql += " where 1=1 ";

			if (account.getStatus() != null) {
				hql += " and t.status=:status ";
				params.put("status", account.getStatus());
			}
			if (account.getOrganizationId() != null) {
				hql += " and  tog.id in(:orgIds)";
				params.put("orgIds", organizationService.getOwerOrgIds(account.getOrganizationId()));
			}
			if (account.getOperateUser() != null) {

				hql += " and  tog.id in(:operaterOrgIds)";
				params.put("operaterOrgIds", organizationService.getOwerOrgIds(account.getOperateUser().getOrganizationId()));
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
	public Long count(OrgAccount param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TorgAccount t left join t.organization tog ";
		return orgAccountDao.count("select count(t.id) " + hql + whereHql(param, params), params);
	}

	@Override
	public void edit(OrgAccount param) {

	}

	@Override
	public OrgAccount get(Long id) {
		TorgAccount toa = orgAccountDao.get(" select t from TorgAccount t left join t.organization tog where t.id=" + id);
		OrgAccount orgAccount = new OrgAccount();
		BeanUtils.copyProperties(toa, orgAccount);
		if (toa.getOrganization() != null) {
			orgAccount.setOrgName(toa.getOrganization().getName());
		}
		return orgAccount;
	}

	@Override
	public String adjustOrgAccount(Long id, Double amt, User user, String desc) {
		String flag = GlobalConstant.SUCCESS;
		TorgAccount toa = orgAccountDao.get(TorgAccount.class, id);
		if (toa.getAmt().add(BigDecimal.valueOf(amt)).compareTo(toa.getLimitAmt()) < 0) {
			flag = "您充值后的金额小于境界金额，充值不成功";
		} else {
			Account account = accountService.getAccountByUserId(user.getId());
			if (account.getAvlAmt().compareTo(BigDecimal.valueOf(amt)) < 0) {
				flag = "您的可用金额不足，充值不成功";
			} else {
				String tranFlag = paymentService.updateAccountWhenTransferAccount(user.getId(), 1l, amt, desc);
				if (GlobalConstant.RESP_CODE_SUCCESS.equals(tranFlag)) {
					toa.setAmt(toa.getAmt().add(BigDecimal.valueOf(amt)));
					logger.info(desc + "成功");
				} else {
					flag = GlobalConstant.map.get(tranFlag);
					logger.info(desc + "失败");
				}
			}
		}
		return flag;
	}

	@Override
	public String isAllowConsumeOrgAccount(Integer accountType, BigDecimal amt, String agentId) {
		String sql = "select * from (" + " select a.id, CASE a.type WHEN 200 THEN a.amt-:amt" + " WHEN 300 THEN a.amt*poc.platform_T0_Tixian_rate"
				+ " WHEN 400 THEN a.amt-poc.platform_Authentication_Fee " + " WHEN 500 THEN a.amt-poc.platform_Message_Fee" + " ELSE 0 END as fee" + " from org_account a"
				+ " left join sys_organization g on g.id=a.org_id" + " left join platform_org_config poc on poc.org_id=g.ID" + " where a.status=0" + " and a.type=:accountType"
				+ " and  g.code=:agentId " + " ) t where t.fee>=0";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accountType", accountType);
		params.put("amt", amt);
		params.put("agentId", agentId);
		List<Object[]> objs = orgAccountDao.findBySql(sql, params);
		if (objs != null && objs.size() > 0) {
			return GlobalConstant.SUCCESS;
		}
		return GlobalConstant.RESP_CODE_070;
	}

	@Override
	public String updateOrgAccountAfterConsumeSuccess(Integer accountType, BigDecimal amt, String agentId, Long consumerId) {
		logger.info("-------提现成功后账户的信息变更 begin");
		String flag = GlobalConstant.RESP_CODE_999;
		PlatformOrgConfig poc = platformOrgConfigService.getPlatformOrgConfig("F20160001");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accountType", accountType);
		params.put("amt", amt);
		params.put("agentId", agentId);
		String updateStr = "update org_account a INNER JOIN sys_organization g on  g.id=a.org_id  INNER JOIN platform_org_config poc on poc.org_id=g.ID  set a.amt=a.amt- CASE a.type "
				+ " WHEN 100 THEN a.amt*poc.platform_Input_Rate"
				+ " WHEN 200 THEN :amt"
				+ " WHEN 300 THEN a.amt*poc.platform_T0_Tixian_rate"
				+ " WHEN 400 THEN poc.platform_Authentication_Fee "
				+ " WHEN 500 THEN poc.platform_Message_Fee "
				+ " ELSE 0 END"
				+ " where a.status=1 and a.type=:accountType and  g.code=:agentId";
		logger.info("-------提现成功后账户的信息变更 end");
		return flag;
	}

}
