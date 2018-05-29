package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.TsysMsgHistory;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.SysMsgHistory;
import com.cn.flypay.service.account.OrgAccountService;
import com.cn.flypay.service.sys.MsgHistoryService;
import com.cn.flypay.service.sys.OrgSysConfigService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.SmsValidateService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;

@Service
public class MsgHistoryServiceImpl implements MsgHistoryService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private BaseDao<TsysMsgHistory> smsDao;
	@Autowired
	private SmsValidateService smsValidateService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private SysParamService sysParamService;
	@Autowired
	private OrgAccountService orgAccountService;
	@Autowired
	private OrgSysConfigService orgSysConfigService;
	/**
	 * 短信发送超时时间
	 */
	@Value("${msg_time_out}")
	private String msg_time_out;

	@Value("${max_validate_msg_num}")
	private String max_validate_msg_num;

	@Override
	public Boolean validateSmsCode(String smsCode, String msgCode) {
		boolean flag = false;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("msgCode", msgCode.trim());
			params.put("max_validatenum", Integer.parseInt(max_validate_msg_num));
			TsysMsgHistory sms = smsDao.get("select t from TsysMsgHistory t where t.status=0 and t.msgCode=:msgCode  and t.validateNum<=:max_validatenum", params);
			if (sms != null) {
				if (sms.getValidateCode().equalsIgnoreCase(smsCode.trim())) {
					sms.setStatus(1);
					flag = true;
				} else {
					sms.setValidateNum(sms.getValidateNum() + 1);
					flag = false;
				}
				smsDao.update(sms);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public SysMsgHistory sendSmsToUserPhone(String phone, String agentId, Integer type) throws Exception {
		String flag = GlobalConstant.SUCCESS;
		SysMsgHistory sh = new SysMsgHistory();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("phone", phone);
			params.put("agentId", agentId);
			params.put("createTimeStart", DateUtil.getStartOfDay(new Date()));
			params.put("createTimeEnd", DateUtil.getEndOfDay(new Date()));
			Long num = smsDao.count("select count(t.id) from  TsysMsgHistory t where t.phone=:phone and t.agentCode=:agentId and t.createTime>=:createTimeStart and t.createTime<=:createTimeEnd",
					params);
			String max_send_msg_num = sysParamService.searchSysParameter().get("max_send_msg_num");
			if (num > Integer.parseInt(max_send_msg_num)) {
				flag = "超出了同一个号码每天发送的最多发送" + max_send_msg_num + "次限制，请明天再试";
				sh.setContent(flag);
			} else {
				/* 判断运营商短信账户 是否可以扣费 */
				// flag =
				// orgAccountService.isAllowConsumeOrgAccount(OrgAccount.account_type.message.getCode(),
				// BigDecimal.ZERO, agentId);
				if (GlobalConstant.SUCCESS.equals(flag)) {
					Long code = Double.valueOf(Math.random() * 1000000).longValue();
					String codeStr = org.apache.commons.lang.StringUtils.leftPad(code.toString(), 6, "0");
					String templeId = SysMsgHistory.SMS_TYPE_TEMPLES.get(String.valueOf(type));
					JSONObject config = orgSysConfigService.getMsgConfigJSONObject(StringUtil.getAgentId(agentId));
					if (config != null) {
						templeId = config.getString(String.valueOf(type));
					}
					flag = smsValidateService.sendMsgValidate(phone, templeId, codeStr, StringUtil.getAgentId(agentId));
					if (StringUtil.isNotBlank(flag) && GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
						logger.info("send sms to " + phone + " ,code = " + codeStr);
						TsysMsgHistory t = new TsysMsgHistory();
						t.setContent(String.format(SysMsgHistory.SMS_TYPE_CONTENT.get(String.valueOf(type)), codeStr));
						t.setCreateTime(new Date());
						t.setExpiresIn(Long.parseLong(msg_time_out));
						String uuid = UUID.randomUUID().toString().replaceAll("\\-", "");// 返回一个随机UUID。
						t.setMsgCode(uuid);
						t.setMsgType(type);
						t.setPhone(phone);
						t.setValidateNum(0);
						t.setStatus(GlobalConstant.ZERO);
						t.setValidateCode(codeStr);
						t.setAgentCode(StringUtil.getAgentId(agentId));
						smsDao.save(t);
						flag = flag + codeStr;
						BeanUtils.copyProperties(t, sh);

						try {
							// orgAccountService.updateOrgAccountAfterConsumeSuccess(OrgAccount.account_type.message.getCode(),
							// BigDecimal.ZERO, agentId, t.getId());
						} catch (Exception e) {

						}

					} else {
						sh.setContent(flag);
					}
				} else {
					sh.setContent(flag);
				}
			}
		} catch (Exception e) {
			logger.error("send sms error", e);
			flag = "发送短信异常";
			sh.setContent(flag);
			throw e;
		}
		return sh;
	}

	@Override
	public List<SysMsgHistory> dataGrid(SysMsgHistory msgHistory, PageFilter ph) {
		List<SysMsgHistory> ul = new ArrayList<SysMsgHistory>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TsysMsgHistory t ";
		List<TsysMsgHistory> l = smsDao.find(hql + whereHql(msgHistory, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TsysMsgHistory t : l) {
			SysMsgHistory u = new SysMsgHistory();
			BeanUtils.copyProperties(t, u);
			Torganization org = organizationService.getTorganizationInCacheByCode(t.getAgentCode());
			if (org != null) {
				u.setOrganizationName(org.getName());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(SysMsgHistory msgHistory, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TsysMsgHistory t  ";
		return smsDao.count("select count(t.id) " + hql + whereHql(msgHistory, params), params);
	}

	private String whereHql(SysMsgHistory msgHistory, Map<String, Object> params) {
		String hql = "";
		if (msgHistory != null) {
			hql += " where 1=1 ";
			if (StringUtil.isNotBlank(msgHistory.getPhone())) {
				hql += " and t.phone like :phone";
				params.put("phone", "%%" + msgHistory.getPhone() + "%%");
			}
			if (StringUtil.isNotBlank(msgHistory.getAgentId()) && !"F00000001".equals(msgHistory.getAgentId())) {
				hql += " and  t.agentCode like :agentCode";
				params.put("agentCode", "%%" + msgHistory.getAgentId() + "%%");
			}
			if (msgHistory.getOrganizationId() != null) {
				Torganization org = organizationService.getTorganizationInCacheById(msgHistory.getOrganizationId());
				if (org != null) {
					hql += " and  t.agentCode = :agentCode";
					params.put("agentCode", StringUtil.getAgentId(org.getCode()));
				}
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
}
