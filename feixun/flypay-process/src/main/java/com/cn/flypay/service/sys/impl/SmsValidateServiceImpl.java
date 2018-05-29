package com.cn.flypay.service.sys.impl;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.service.sys.OrgSysConfigService;
import com.cn.flypay.service.sys.SmsValidateService;
import com.cn.flypay.utils.StringUtil;

@Service
public class SmsValidateServiceImpl implements SmsValidateService {
	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private OrgSysConfigService orgSysConfigService;
	@Value("${msg_restAPI_serverIP}")
	private String msg_restAPI_serverIP;
	@Value("${msg_restAPI_serverPort}")
	private String msg_restAPI_serverPort;
	@Value("${msg_accountSid}")
	private String msg_accountSid;
	@Value("${msg_accountToken}")
	private String msg_accountToken;

	@Value("${msg_appId_F20160001}")
	private String msg_appId_F20160001;
	@Value("${msg_time_out}")
	private String msg_time_out;

	/**
	 * 
	 */
	public String sendMsgValidate(String phone, String templateId, String code, String agentId) {
		String flag = GlobalConstant.RESP_CODE_SUCCESS;
		HashMap<String, Object> result = null;
		CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
		restAPI.init(msg_restAPI_serverIP, msg_restAPI_serverPort);
		restAPI.setAccount(msg_accountSid, msg_accountToken);
		String msg_appId = msg_appId_F20160001;
		JSONObject config = orgSysConfigService.getMsgConfigJSONObject(agentId);
		if (config != null) {
			msg_appId = config.getString("appId");
		}
		restAPI.setAppId(msg_appId);
		String[] params = null;
		if (StringUtil.isNotEmpty(code)) {
			params = new String[] { code, msg_time_out };
		}
		result = restAPI.sendTemplateSMS(phone, templateId, params);
		if ("000000".equals(result.get("statusCode"))) {
			// 正常返回输出data包体信息（map）
			@SuppressWarnings("unchecked")
			HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for (String key : keySet) {
				Object object = data.get(key);
				log.info(key + " = " + object);
			}
		} else {
			log.error("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
			flag = (String) result.get("statusMsg");
		}
		return flag;
	}
}
