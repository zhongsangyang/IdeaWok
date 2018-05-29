package com.cn.flypay.service.payment.impl;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.sys.TauthenticationLog;
import com.cn.flypay.model.sys.TsysImage;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.AuthenticationLog;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.payment.AuthenticationService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.Base64;
import com.cn.flypay.utils.HttpPostMap;
import com.cn.flypay.utils.SignatureUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.auth.HttpsClientUtil;
import com.thinkive.base.util.security.AES;
import com.google.gson.JsonObject;

/**
 * 实名认证
 * 
 * @author sunyue
 * 
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
	private Log log = LogFactory.getLog(getClass());
	
	private static String userName = "feifuxinxi";
	private static String password = "feifuxinxi0213";
	private static String nameIDCardPhoneAccountVerifyV3Url = "https://www.miniscores.cn:8313/CreditFunc/v2.1/NameIDCardPhoneAccountVerifyV3";
	

	private String auth_function = "2000207";
	@Autowired
	private UserService userService;
	/**
	 * 认证地址
	 */
	@Value("${auth_url}")
	private String auth_url;
	/**
	 * 机构账号
	 */
	@Value("${auth_ptyacct}")
	private String auth_ptyacct;
	/**
	 * 机构号
	 */
	@Value("${auth_ptycd}")
	private String auth_ptycd;
	/**
	 * 会话密钥 必须先执行006---》002
	 */
	@Value("${auth_ptyKey}")
	private String auth_ptyKey;

	@Autowired
	private BaseDao<TauthenticationLog> authLogDao;

	@Autowired
	private BaseDao<Tuser> userDao;

	@Autowired
	private BaseDao<TsysImage> imagesDao;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private BaseDao<Taccount> accountDao;

	@Override
	public AuthenticationLog sendInfoToAuthentication(AuthenticationLog authLog) throws Exception {
		try {

			Map<String, String> map = new HashMap<String, String>();
			String ptyKye = auth_ptyKey;

			String acctno = authLog.getCardNo();//卡号
			String certseq = authLog.getIdNo();//身份证号
			AES aes = new AES(ptyKye);
			String AESAcctno = aes.encrypt(acctno, "utf-8"); // 加密
			String AESCertseq = aes.encrypt(certseq, "utf-8"); // 加密

			String base64Acctno = new String(Base64.encode(URLEncoder.encode(AESAcctno, "utf-8").getBytes("utf-8")), "utf-8");
			String base64Certseq = new String(Base64.encode(URLEncoder.encode(AESCertseq, "utf-8").getBytes("utf-8")), "utf-8");

			map.put("acctno", AESAcctno);//卡号
			map.put("biztyp", "0541");// 对照接口文档查看
			map.put("biztypdesc", "银行卡认证");// 服务描述
			map.put("certseq", AESCertseq);// 身份证号
			map.put("code", "");// 短信验证码 .如不调用短信，这里可以传空字符
			map.put("phoneno", authLog.getPhone());// 手机号

			map.put("sysseqnb", "");// 调用生成短信接口返回的业务流水号 .如不调用短信，这里可以传空字符

			map.put("placeid", "330104");// 业务发生地
			map.put("ptyacct", auth_ptyacct);// 机构帐号
			map.put("ptycd", auth_ptycd);// 机构号
			map.put("sourcechnl", "0");// 来源渠道，pc端传0

			String sign = SignatureUtil.signature(map, ptyKye);
			map.put("sign", sign);// 防篡改密钥
			map.put("funcNo", auth_function);// 单笔请求业务BUS功能号
			map.put("acctno", base64Acctno);
			map.put("certseq", base64Certseq);// 身份证号
			map.put("usernm", authLog.getRealName());// 姓名
			// 请求接口
			String result = HttpPostMap.post(auth_url, map, 2);
			JSONObject jsonObject = JSONObject.parseObject(result);

			/*
			 * "[0]:成功 [-1000]:业务中间件内部错误 [-1001]:入口参数错误 [-1002]:认证平台内部错误
			 * [-1003]:SQL结果集空 [-1004]:没有查到对应结果 [-10010]：入参校验失败;
			 * [-20001]：网络不可用，请稍后再试； [-20304]：银行卡验证失败,请重新尝试； [-1009]：机构号与机构编号有误；
			 * [-1008]：机构号与机构编号有误； [-200020301]:参数为空错误;
			 */

			String error_no = (String) jsonObject.get("error_no");
			if (!error_no.equals("0")) {
				authLog.setStatus(0);
				authLog.setErrorCode(error_no);
				authLog.setErrorInfo(jsonObject.getString("error_info"));
				log.info("操作失败，失败原因：" + jsonObject.get("error_info"));
			} else {
				authLog.setStatus(0);
				if (jsonObject.get("results") != null) {
					JSONArray jsonArray = jsonObject.getJSONArray("results");
					JSONObject resJson = (JSONObject) jsonArray.get(0);
					// 获取返回的参数
					String status = resJson.getString("status");// 处理状态，00为成功，01为正在处理，03位处理失败
					String respcd = resJson.getString("respcd");// 返回码 具体参见接口文档

					/*
					 * respcd 应答码（查询接口） "[2000]:认证一致（通过） [2001]:认证不一致（不通过）
					 * [2002]:交易异常
					 */

					if ("00".equals(status) && "2000".equals(respcd)) {
						authLog.setStatus(1);
					}
					String respinfo = resJson.getString("respinfo");// 返回信息
																	// 具体参见接口文档
					authLog.setErrorCode(respcd);
					authLog.setErrorInfo(respinfo);
					String sysseqnb = resJson.getString("sysSeqNb");// 流水号
					authLog.setSysSeq(sysseqnb);

					log.info("返回的结果：[status=" + status + "],[respcd=" + respcd + "]," + "[respinfo=" + respinfo + "],[sysseqnb=" + sysseqnb + "]");
				} else {
					log.info("操作失败！");
				}

			}
			TauthenticationLog t = new TauthenticationLog();
			BeanUtils.copyProperties(authLog, t);
			if (authLog.getUserId() != null) {
				t.setUser(userDao.get(Tuser.class, authLog.getUserId()));
			}
			t.setCreateTime(new Date());
			authLogDao.save(t);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		return authLog;
	}

	@Override
	public AuthenticationLog saveAuthentication(AuthenticationLog authLog) {
		TauthenticationLog t = new TauthenticationLog();
		BeanUtils.copyProperties(authLog, t);
		t.setUser(userDao.get(Tuser.class, authLog.getUserId()));
		authLogDao.save(t);
		authLog.setId(t.getId());
		return authLog;
	}

	@Override
	public String findAuthErroInfo(Long userId, Set<Integer> authTyps) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("authTyps", authTyps);
		List<TauthenticationLog> logs = authLogDao.find("select t from TauthenticationLog t left join t.user u where u.id = :userId  and t.authType in (:authTyps) order by t.createTime desc", params);

		if (logs != null && logs.size() > 0) {
			TauthenticationLog tl = logs.get(0);

			if (tl.getAuthType() == AuthenticationLog.auth_type.manual.getCode() || tl.getAuthType() == AuthenticationLog.auth_type.manual_merchant.getCode()) {
				if (StringUtil.isNotBlank(tl.getErrorInfo())) {
					return tl.getErrorInfo();
				}
			}
			if (tl.getAuthType() == AuthenticationLog.auth_type.auto.getCode()) {
				if (!"2000".equals(tl.getErrorCode())) {
					return tl.getErrorInfo();
				}
			}
		}
		return null;
	}

	@Override
	public List<AuthenticationLog> dataGrid(AuthenticationLog authLog, PageFilter ph) {
		List<AuthenticationLog> ul = new ArrayList<AuthenticationLog>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TauthenticationLog t left join t.user u left join u.organization tog ";
		List<TauthenticationLog> l = authLogDao.find(hql + whereHql(authLog, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TauthenticationLog t : l) {
			AuthenticationLog u = new AuthenticationLog();
			BeanUtils.copyProperties(t, u);
			if (authLog.getOperateUser() == null || !userService.isSuperAdmin(authLog.getOperateUser().getId())) {
				u.setIdNo(StringUtil.getCiphertextInfo(u.getIdNo(), 3, 4));
				u.setCardNo(StringUtil.getCiphertextInfo(u.getCardNo(), 3, 4));
				u.setPhone(StringUtil.getCiphertextInfo(u.getPhone(), 3, 4));
			}
			Tuser user = t.getUser();
			if (user != null) {
				u.setLoginName(user.getLoginName());
				if (user.getOrganization() != null) {
					u.setOrganizationName(user.getOrganization().getName());
				}
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(AuthenticationLog authLog, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TauthenticationLog t left join t.user u left join u.organization tog ";
		return authLogDao.count("select count(t.id) " + hql + whereHql(authLog, params), params);
	}

	private String whereHql(AuthenticationLog authLog, Map<String, Object> params) {
		String hql = "";
		if (authLog != null) {
			hql += " where 1=1 ";
			if (StringUtil.isNotBlank(authLog.getLoginName())) {
				hql += " and u.loginName like :loginName";
				params.put("loginName", "%%" + authLog.getLoginName() + "%%");
			}
			if (authLog.getOrganizationId() != null) {
				hql += " and  tog.id in(:orgIds)";
				params.put("orgIds", organizationService.getOwerOrgIds(authLog.getOrganizationId()));
			}
			if (authLog.getOperateUser() != null) {

				hql += " and  tog.id in(:operaterOrgIds)";
				params.put("operaterOrgIds", organizationService.getOwerOrgIds(authLog.getOperateUser().getOrganizationId()));
			}
			if (authLog.getAuthType() != null) {
				hql += " and t.authType = :authType";
				params.put("authType", authLog.getAuthType());
			}
			if (authLog.getStatus() != null) {
				hql += " and t.status = :status";
				params.put("status", authLog.getStatus());
			}
			if (authLog.getIdNo() != null) {
				hql += " and t.idNo = :idNo";
				params.put("idNo", authLog.getIdNo());
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
	public Boolean isAllowAuthentication(Long userId, String idNo) {
		User u = userService.getSimpleUser(userId);
		List<Taccount> tls = accountDao.find(" select t  from Taccount t left join t.user u  where t.status in(1,100) and u.idNo='" + idNo + "'");
		for (Taccount ta : tls) {
			if (ta.getStatus() == 1 && ta.getUser().getAgentId().equals(u.getAgentId())) {
				return false;
			} else if (ta.getStatus() == 100) {
				return false;
			}
		}
		return true;
	}
	
	

	@Override
	public Boolean isIdon(String agentId, String idNo) {
		String sql =" select count(s.ID) from sys_user s where s.id_no='"+ idNo +"' and s.agent_id='"+ agentId +"'";
		BigInteger in = userDao.countBySql(sql);
		int count = in.intValue();
		if(count>2){
			return false;
		}
		return true;
	}

	@Override
	public AuthenticationLog sendInfoToAuthen(AuthenticationLog authLog)throws Exception {
		String postStr = getNameIDAccountPhoneParas(authLog.getRealName(), authLog.getIdNo(), authLog.getCardNo(), authLog.getPhone());
		String result = HttpsClientUtil.doPost(nameIDCardPhoneAccountVerifyV3Url, postStr, "utf-8");
		JSONObject json = JSONObject.parseObject(result);
		String code = json.getString("RESULT");
		String message = json.getString("MESSAGE");
		String sysseqnb = json.getString("guid");//流水号
		authLog.setSysSeq(sysseqnb);
		if (!code.equals("1")) {
			authLog.setStatus(0);
			authLog.setErrorCode(code);
			authLog.setErrorInfo(message);
			log.info("操作失败，失败原因：" + json.getString("MESSAGE"));
		}else{
			authLog.setStatus(0);
			if (code.equals("1")) {
				authLog.setStatus(1);
			}
			authLog.setErrorCode(code);
			authLog.setErrorInfo(message);
			log.info("返回的结果：[status=" + code + "],[respcd=" + code + "]," + "[respinfo=" + message + "],[sysseqnb=" + sysseqnb + "]");
		}
		TauthenticationLog t = new TauthenticationLog();
		BeanUtils.copyProperties(authLog, t);
		if (authLog.getUserId() != null) {
			t.setUser(userDao.get(Tuser.class, authLog.getUserId()));
		}
		t.setCreateTime(new Date());
		authLogDao.save(t);
		return authLog;
	}
	
	
	
	
	public static String getNameIDAccountPhoneParas(String name, String idCard, String account, String phone) {
		JsonObject totalJsonObj = new JsonObject();
		totalJsonObj.addProperty("loginName", userName);
		totalJsonObj.addProperty("pwd", password);
		totalJsonObj.addProperty("serviceName", "NameIDCardPhoneAccountVerifyV3");
		totalJsonObj.addProperty("reqType", "demo");
		JsonObject paramJsonObj = new JsonObject();
		paramJsonObj.addProperty("idCard", idCard);
		paramJsonObj.addProperty("name", name);
		paramJsonObj.addProperty("accountNo", account);
		paramJsonObj.addProperty("mobile", phone);
		totalJsonObj.addProperty("param", paramJsonObj.toString());
		return totalJsonObj.toString();
	}
	
	
	
}
